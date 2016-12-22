package org.unhack.bip38decrypt;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.unhack.bip38decrypt.createactivity.CreateActivity;
import org.unhack.bip38decrypt.decodeactivity.DecodeActivity;
import org.unhack.bip38decrypt.mfragments.imFragment;
import org.unhack.bip38decrypt.mfragments.mFragment;
import org.unhack.bip38decrypt.services.bip38service;

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
        Button tv_decode = (Button) view.findViewById(R.id.main_button_decode);
        Button tv_reencode = (Button) view.findViewById(R.id.main_button_reencode);
        Button tv_create =  (Button) view.findViewById(R.id.main_button_create);

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
        tv_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(),getString(R.string.notimplemented),Toast.LENGTH_LONG).show();
                Intent generate_activity_intent = new Intent(getContext(), CreateActivity.class);
                startActivity(generate_activity_intent);
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
            if (bip38service.bip38serviceIsRunning) {
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