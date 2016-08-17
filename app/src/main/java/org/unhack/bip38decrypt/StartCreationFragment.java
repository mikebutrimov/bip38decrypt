package org.unhack.bip38decrypt;


import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import org.w3c.dom.Text;

/**
 * Created by unhack on 6/23/16.
 */
public class StartCreationFragment extends mFragment implements imFragment {
    public static Handler startCreationFragmentHandler;
    public View mView;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        startCreationFragmentHandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                if (msg.getData().getBoolean("working")){
                    setMode(true);
                }
                else {
                    setMode(false);
                }
            }
        };

        View view = inflater.inflate(R.layout.creationfragment_layout, container, false);
        TextView tv_decode = (TextView) view.findViewById(R.id.textView_decode);
        TextView tv_reencode = (TextView) view.findViewById(R.id.textView2_reencode);
        tv_decode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent decode_activity_intent = new Intent(getContext(), DecodeActivity.class);
                decode_activity_intent.putExtra(MainActivity.WORKING_MODE,MainActivity.DECODE);
                startActivity(decode_activity_intent);
            }
        });
        tv_reencode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent decode_activity_intent = new Intent(getContext(), DecodeActivity.class);
                decode_activity_intent.putExtra(MainActivity.WORKING_MODE,MainActivity.REENDCODE);
                decode_activity_intent.putExtra("reencrypt",true);
                startActivity(decode_activity_intent);
            }
        });
        mView = view;
        return view;
    }


    public void setMode(boolean mode) {
        LinearLayout buttonLayout = (LinearLayout) mView.findViewById(R.id.LLButtons);
        LinearLayout loadingLayout = (LinearLayout) mView.findViewById(R.id.LLLoading);
        if (mode) {
            buttonLayout.setVisibility(View.INVISIBLE);
            loadingLayout.setVisibility(View.VISIBLE);
        } else {
            buttonLayout.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.INVISIBLE);
        }
    }



    @Override
    public void onResume(){
        super.onResume();
        try {
            if (bip38service.IAM) {
                setMode(true);
            } else {
                setMode(false);
            }
        }
        catch (Exception e){
            //in case of emergency - fubar
            setMode(false);
        }
    }

}