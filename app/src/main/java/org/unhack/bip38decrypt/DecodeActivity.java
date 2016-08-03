package org.unhack.bip38decrypt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import net.bither.bitherj.crypto.bip38.Bip38;
import net.bither.bitherj.crypto.bip38.Bip38.*;
import net.bither.bitherj.exception.AddressFormatException;

public class DecodeActivity extends AppCompatActivity {
    public static MixedPagerAdapter pagerAdapter;
    private NonSwipeableViewPager viewPager;
    private String wallet;
    public static Handler mSwipeHandler;
    public static final String TABNUMBER = "tab_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);

        mSwipeHandler  = new Handler() {
            public void handleMessage(android.os.Message msg) {
                viewPager.setCurrentItem(msg.getData().getInt(TABNUMBER));
            }
        };


        List<mFragment> fragments = new ArrayList<mFragment>();
        dInputFragment inputFragment = new dInputFragment();
        fragments.add(inputFragment);
        initPaging(fragments);

    }

    private void initPaging(List<mFragment> fragments) {
        pagerAdapter = new MixedPagerAdapter(getSupportFragmentManager());
        for (mFragment frg: fragments){
            pagerAdapter.addFragment(frg);
        }
        viewPager = (NonSwipeableViewPager) findViewById(R.id.decode_container);
        if (viewPager != null) {
            viewPager.setAdapter(pagerAdapter);
            viewPager.setPagingEnabled(false);
        }
    }

    public static void addFragment(mFragment frg){
        pagerAdapter.addFragment(frg);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            if (resultCode == RESULT_OK) {
                wallet = data.getStringExtra("SCAN_RESULT");
                if (!wallet.isEmpty()){
                    try {
                        if (Bip38.isBip38PrivateKey(wallet)){
                            Message msg = new Message();
                            Bundle mBundle = new Bundle();
                            mBundle.putString("wallet",wallet);
                            msg.setData(mBundle);
                            dInputFragment.updateWallet.sendMessage(msg);
                        }
                    } catch (AddressFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(resultCode == RESULT_CANCELED){
                //okay :(
            }
        }
    }


}
