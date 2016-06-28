package org.unhack.bip38decrypt;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 736;
    private ViewPager viewPager;
    private String wallet, password, second_password;
    public static boolean reencrypt = false;
    private boolean needReEncrypt = false;
    public static MixedPagerAdapter pagerAdapter;
    public static Handler mSwipeHandler,mQrCreatreHandler;
    public static final String TABNUMBER = "tab_number";
    public AlertDialog.Builder mDialogBuilder;
    public AlertDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                Message msgCreationFragment = new Message();
                msgCreationFragment.copyFrom(msg);
                Bundle args = msg.getData();
                StartCreationFragment.startCreationFragmentHandler.sendMessage(msgCreationFragment);
                QrFragment mQrFragment = new QrFragment();
                mQrFragment.setArguments(args);
                pagerAdapter.addFragment(mQrFragment);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initPaging() {
        StartCreationFragment fragmentOne = new StartCreationFragment();
        //QrFragment fragmentTwo= new QrFragment();
        pagerAdapter = new MixedPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(fragmentOne);
        //pagerAdapter.addFragment(fragmentTwo);
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(pagerAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            if (resultCode == RESULT_OK) {
                wallet = data.getStringExtra("SCAN_RESULT");
                //start dialog
                mDialogBuilder = new AlertDialog.Builder(this);
                mDialogBuilder.setTitle(R.string.mDialogBuilder);
                LayoutInflater inflater = this.getLayoutInflater();
                if (!reencrypt){
                    mDialogBuilder.setView(inflater.inflate(R.layout.password_form, null));
                }
                else {
                    mDialogBuilder.setView(inflater.inflate(R.layout.password_form_re, null));
                }
                mDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText ePassword1 = (EditText) mDialog.findViewById(R.id.editText_password1);
                        password = ePassword1.getText().toString();
                        if (reencrypt) {
                            EditText ePassword2 = (EditText) mDialog.findViewById(R.id.editText2_password2);
                            second_password = ePassword2.getText().toString();
                            if (second_password.isEmpty()){
                                Toast.makeText(getApplicationContext(),R.string.noPassword2,Toast.LENGTH_LONG).show();
                            }
                            else {
                                needReEncrypt = true;
                            }
                        }

                        if (password.isEmpty()){
                            Toast.makeText(getApplicationContext(),R.string.noPassword,Toast.LENGTH_LONG).show();
                        }
                        else {
                            Intent bip38ServiceIntent = new Intent(getApplicationContext(), bip38service.class);
                            bip38ServiceIntent.putExtra("wallet", wallet);
                            bip38ServiceIntent.putExtra("password", password);
                            bip38ServiceIntent.putExtra("password2", second_password);
                            bip38ServiceIntent.putExtra("reencrypt", needReEncrypt);
                            startService(bip38ServiceIntent);
                        }
                    }
                });
                mDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                mDialog = mDialogBuilder.create();
                mDialog.show();

            }
            if(resultCode == RESULT_CANCELED){
                //okay :(
            }
        }
    }

    public void setPasswdVisible(View v){
        CheckBox setPasswdVisible = (CheckBox) mDialog.findViewById(R.id.checkBox_showPassword);
        EditText ePassword1 = (EditText) mDialog.findViewById(R.id.editText_password1);
        EditText ePassword2 = (EditText) mDialog.findViewById(R.id.editText2_password2);
        if (!setPasswdVisible.isChecked()){
            ePassword1.setTransformationMethod(new PasswordTransformationMethod());
            ePassword2.setTransformationMethod(new PasswordTransformationMethod());
        }
        else {
            ePassword1.setTransformationMethod(null);
            ePassword2.setTransformationMethod(null);
        }
    }


}
