package org.unhack.bip38decrypt.createactivity;/*
The MIT License (MIT)

Copyright (c) 2015 lifeofreilly

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


Some code was taken from https://github.com/lifeofreilly/vanitas
and slightly modified to get rid of bitcoinj library

 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import org.unhack.bip38decrypt.R;
import org.unhack.bip38decrypt.adaptors.MixedPagerAdapter;
import org.unhack.bip38decrypt.adaptors.NonSwipeableViewPager;
import org.unhack.bip38decrypt.mfragments.mFragment;
import org.unhack.bip38decrypt.services.speedtest;

import java.util.ArrayList;
import java.util.List;


public class CreateActivity extends AppCompatActivity {
    public static final String TABNUMBER = "tab_number";
    public static MixedPagerAdapter createPagerAdapter;
    public static Handler createSwipeHandler,createErrorHandler;
    private NonSwipeableViewPager viewPager;
    public final int cores = Runtime.getRuntime().availableProcessors();
    public double speed = 0;
    public  TextView textView_performance;
    public final static String SPEEDTEST_FILTER = "org.unhack.bip38decrypt.SPEEDTEST_FILTER";
    private BroadcastReceiver mSppedtestReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int speed = intent.getIntExtra("speed",0);
            textView_performance = (TextView) findViewById(R.id.textView_performance);
            textView_performance.setText( String.format("Cores available: "+String.valueOf(cores)+ "\nAddresses per second: %d",speed));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(mSppedtestReciever, new IntentFilter(SPEEDTEST_FILTER));
        createSwipeHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {

                viewPager.setCurrentItem(msg.getData().getInt(TABNUMBER));
                Log.d("SWIPE", "Swipe toggled " + msg.getData().toString());
            }
        };

        /*createErrorHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                //create error fragment, put data into it, set focus to it
                //we need to bypass error string and button callback
                //oh fuck, i was looking to the wrong lifecycle scheme
                Bundle args = msg.getData();
                cErrorFragment errorFragment = new cErrorFragment();
                errorFragment.setArguments(args);
                createPagerAdapter.addFragment(errorFragment);
                Log.d("errHandler",String.valueOf(createPagerAdapter.getCount()));
                createPagerAdapter.CoolNavigateToTab(1,TABNUMBER, CreateActivity.createSwipeHandler,false);
            }
        };*/


        setContentView(R.layout.activity_create);
        Intent speedTestIntent = new Intent(this, speedtest.class);
        startService(speedTestIntent);
        List<mFragment> fragments = new ArrayList<mFragment>();
        cInputFragment inputFragment = new cInputFragment();
        fragments.add(inputFragment);
        initPaging(fragments);

    }

    private void initPaging(List<mFragment> fragments) {
        createPagerAdapter = new MixedPagerAdapter(getSupportFragmentManager());
        for (mFragment frg: fragments){
            createPagerAdapter.addFragment(frg);
        }
        viewPager = (NonSwipeableViewPager) findViewById(R.id.create_container);
        if (viewPager != null) {
            viewPager.setAdapter(createPagerAdapter);
            viewPager.setPagingEnabled(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mSppedtestReciever);
    }
}
