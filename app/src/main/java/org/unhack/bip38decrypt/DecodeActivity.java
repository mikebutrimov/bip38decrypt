package org.unhack.bip38decrypt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import net.bither.bitherj.crypto.bip38.Bip38;
import net.bither.bitherj.exception.AddressFormatException;

public class DecodeActivity extends AppCompatActivity {
    public static MixedPagerAdapter decodePagerAdapter;
    private NonSwipeableViewPager viewPager;
    private String wallet;
    public static Handler decodeSwipeHandler,decodeErrorHandler;
    public static final String TABNUMBER = "tab_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);
        //This handler is used to swipe tabs
        decodeSwipeHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                viewPager.setCurrentItem(msg.getData().getInt(TABNUMBER));
            }
        };
        //this handler is used to react on different errors
        decodeErrorHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                //create error fragment, put data into it, set focus to it
                //we need to bypass error string and button callback
                //oh fuck, i was looking to the wrong lifecycle scheme
                Bundle args = msg.getData();
                dErrorFragment errorFragment = new dErrorFragment();
                errorFragment.setArguments(args);
                decodePagerAdapter.addFragment(errorFragment);
                Log.d("errHandler",String.valueOf(decodePagerAdapter.getCount()));
                decodePagerAdapter.CoolNavigateToTab(1,TABNUMBER,DecodeActivity.decodeSwipeHandler,false);
            }
        };



        List<mFragment> fragments = new ArrayList<mFragment>();
        dInputFragment inputFragment = new dInputFragment();
        fragments.add(inputFragment);
        initPaging(fragments);

    }

    private void initPaging(List<mFragment> fragments) {
        decodePagerAdapter = new MixedPagerAdapter(getSupportFragmentManager());
        for (mFragment frg: fragments){
            decodePagerAdapter.addFragment(frg);
        }
        viewPager = (NonSwipeableViewPager) findViewById(R.id.decode_container);
        if (viewPager != null) {
            viewPager.setAdapter(decodePagerAdapter);
            viewPager.setPagingEnabled(false);
        }
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
                            dInputFragment.decodeInputFragmentUpdateWallet.sendMessage(msg);
                        }
                        else {
                            Message msg = new Message();
                            Bundle mBundle = new Bundle();
                            mBundle.putString("error",getString(R.string.notBipKey));
                            msg.setData(mBundle);
                            DecodeActivity.decodeErrorHandler.sendMessage(msg);
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
