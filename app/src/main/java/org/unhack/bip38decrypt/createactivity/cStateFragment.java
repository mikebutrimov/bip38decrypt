package org.unhack.bip38decrypt.createactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.print.PrintAttributes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import org.unhack.bip38decrypt.R;
import org.unhack.bip38decrypt.mfragments.imFragment;
import org.unhack.bip38decrypt.mfragments.mFragment;

/**
 * Created by unhack on 11/29/16.
 */

public class cStateFragment extends mFragment implements imFragment {
    View view;
    private Button button_cancel;
    private  ProgressBar mProgressBar;
    private Bundle mArgs;
    public static Handler onStateWorkerHandler;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mArgs = getArguments();
        onStateWorkerHandler = new Handler(){
            public  void handleMessage(android.os.Message msg) {

            }
        };

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        button_cancel = (Button) view.findViewById(R.id.button_cancel);

        view = inflater.inflate(R.layout.create_step_progress, container, false);
        return view;
    }
}
