package org.unhack.bip38decrypt;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

/**
 * Created by unhack on 8/3/16.
 */
public class dStateFragment extends mFragment implements imFragment {
    Button button_cancel;
    ProgressBar mProgressBar;
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
            }
        };



        View view = inflater.inflate(R.layout.decode_step_progress, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        button_cancel = (Button) view.findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DecodeActivity.decodePagerAdapter.CoolNavigateToTab(0,DecodeActivity.TABNUMBER,DecodeActivity.decodeSwipeHandler,true);
            }
        });
        return view;
    }
}
