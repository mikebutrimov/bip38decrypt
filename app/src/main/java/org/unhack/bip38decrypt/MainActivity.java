package org.unhack.bip38decrypt;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 136;
    public final static String WALLET_MESSAGE = "org.unhack.bip38decrypt.WALLET";
    public static final String INTENT_FILTER = "org.unhack.bip38decrypt.BROADCAST_FILTER";
    public final static String WORKING_MODE = "org.unhack.bip38decrypt.WORKING_MODE";
    public final static String DECODE = "decode";
    public final static String REENDCODE = "reencode";
    private ViewPager viewPager;
    private String wallet, password, second_password;
    public static boolean reencrypt = false;
    private boolean needReEncrypt = false;
    public static MixedPagerAdapter pagerAdapter;
    public static Handler mSwipeHandler,mQrCreatreHandler, mErrorHandler;
    public static final String TABNUMBER = "tab_number";
    public AlertDialog.Builder mDialogBuilder;
    public AlertDialog mDialog;

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("In RECIEVE","Bonjour epta");
            Log.d("MAIN F Rec",intent.getStringExtra("result"));
            Message msgCreationFragment = new Message();
            Bundle args = new Bundle();
            args.putString("addr",intent.getStringExtra("address"));
            args.putString("res",intent.getStringExtra("result"));
            QrFragment mQrFragment = new QrFragment();
            mQrFragment.setArguments(args);
            pagerAdapter.addFragment(mQrFragment);
            Intent dFinishIntent = new Intent(DecodeActivity.DECODE_INTENT_FILTER);
            sendBroadcast(dFinishIntent);
            pagerAdapter.NavigateToTab(pagerAdapter.getCount());
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(myReceiver,new IntentFilter(INTENT_FILTER));
        //check for camera permission
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
        mSwipeHandler  = new Handler() {
            public void handleMessage(android.os.Message msg) {
                viewPager.setCurrentItem(msg.getData().getInt(TABNUMBER));
            }
        };
        mQrCreatreHandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                Log.d("MainA QRH","Bojour EPTA");
                Message msgCreationFragment = new Message();
                msgCreationFragment.copyFrom(msg);
                Bundle args = msg.getData();
                //StartCreationFragment.startCreationFragmentHandler.sendMessage(msgCreationFragment);
                QrFragment mQrFragment = new QrFragment();
                mQrFragment.setArguments(args);
                pagerAdapter.addFragment(mQrFragment);
            }
        };
        mErrorHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                String errString;
                try {
                    errString = msg.getData().getString("error");
                }
                catch (Exception e){
                    errString = "Unknown error";
                }
                Toast.makeText(getApplicationContext(), errString,Toast.LENGTH_LONG).show();
            }
        };


        setContentView(R.layout.activity_main);
        initPaging();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initPaging() {
        StartCreationFragment fragmentOne = new StartCreationFragment();
        pagerAdapter = new MixedPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(fragmentOne);
        viewPager = (ViewPager) findViewById(R.id.container);
        if (viewPager != null) {
            viewPager.setAdapter(pagerAdapter);
        }
    }


    public void setPasswdVisible(View v){
        CheckBox setPasswdVisible = (CheckBox) mDialog.findViewById(R.id.checkBox_showPassword);
        EditText ePassword1 = (EditText) mDialog.findViewById(R.id.editText_password1);
        EditText ePassword2 = (EditText) mDialog.findViewById(R.id.editText2_password2);
        if (setPasswdVisible != null) {
            if (!setPasswdVisible.isChecked()){
                if (ePassword1 != null) {
                    ePassword1.setTransformationMethod(new PasswordTransformationMethod());
                }
                if (ePassword2 != null) {
                    ePassword2.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
            else {
                if (ePassword1 != null) {
                    ePassword1.setTransformationMethod(null);
                }
                if (ePassword2 != null) {
                    ePassword2.setTransformationMethod(null);
                }
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();;
        registerReceiver(myReceiver,new IntentFilter(INTENT_FILTER));
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

}
