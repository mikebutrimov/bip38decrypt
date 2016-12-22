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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import org.unhack.bip38decrypt.adaptors.MixedPagerAdapter;
import org.unhack.bip38decrypt.createactivity.CreateActivity;
import org.unhack.bip38decrypt.decodeactivity.DecodeActivity;
import java.util.HashMap;
import java.util.Map;


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
    boolean doubleBackToExitPressedOnce = false;

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            HashMap<String,String> mWallets = null;
            try {
                mWallets = (HashMap<String, String>) intent.getSerializableExtra("hashmapWallets");
            }
            catch (Exception nse){
                nse.printStackTrace();
            }
            if (mWallets != null){
                for (Map.Entry<String,String> record : mWallets.entrySet()){
                    Message msgCreationFragment = new Message();
                    Bundle args = new Bundle();
                    args.putString("addr",record.getKey());
                    args.putString("res",record.getValue());
                    QrFragment mQrFragment = new QrFragment();
                    mQrFragment.setArguments(args);
                    pagerAdapter.addFragment(mQrFragment);
                }

            }
            Intent dFinishIntent = new Intent(DecodeActivity.DECODE_INTENT_FILTER);
            Intent cFinishIntent = new Intent(CreateActivity.CREATE_INTENT_FILTER);
            sendBroadcast(cFinishIntent);
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

    /*
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
    }*/

    private void initPaging() {
        StartCreationFragment fragmentOne = new StartCreationFragment();
        pagerAdapter = new MixedPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(fragmentOne);
        viewPager = (ViewPager) findViewById(R.id.container);
        if (viewPager != null) {
            viewPager.setAdapter(pagerAdapter);
        }
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        MainActivity.pagerAdapter.NavigateToTab(0);
        Toast.makeText(this, getString(R.string.clickBackToExit), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
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
