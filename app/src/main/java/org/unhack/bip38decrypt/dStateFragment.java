package org.unhack.bip38decrypt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by unhack on 8/3/16.
 */
public class dStateFragment extends mFragment implements imFragment {
    Button button_cancel;
    ProgressBar mProgressBar;
    View view;
    public static Handler onWorkHandler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        onWorkHandler = new Handler(){
            public  void handleMessage(android.os.Message msg) {
                if (msg.getData().getBoolean("isWorking")){
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }

                if (msg.getData().getString("state")!= null) {
                    try {
                        TextView stateView = (TextView) view.findViewById(R.id.textView_decoding);
                        stateView.setText(msg.getData().getString("state"));
                    }
                    catch (Exception e){
                        //lol ok :(
                    }
                }
            }
        };

        view = inflater.inflate(R.layout.decode_step_progress, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        button_cancel = (Button) view.findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bip38service.getWorker().interrupt();
                if (bip38service.getWorker().isInterrupted()){
                    Log.d("DestroyActivity","thread was interrupted");
                }
                DecodeActivity.decodePagerAdapter.CoolNavigateToTab(0,DecodeActivity.TABNUMBER,DecodeActivity.decodeSwipeHandler,true);
            }
        });
        return view;
    }
}
