package org.unhack.bip38decrypt.createactivity;

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

import org.unhack.bip38decrypt.R;
import org.unhack.bip38decrypt.mfragments.imFragment;
import org.unhack.bip38decrypt.mfragments.mFragment;
import org.unhack.bip38decrypt.services.bip38service;
import org.unhack.bip38decrypt.services.createService;

/**
 * Created by unhack on 11/29/16.
 */

public class cStateFragment extends mFragment implements imFragment {
    View view;
    private Button button_cancel;
    private  ProgressBar mProgressBar;
    private TextView mTextViewProgress;
    public static Handler onCreateKeyHandler, onEncryptKeyHandler;
    private int cores = Runtime.getRuntime().availableProcessors();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.create_step_progress, container, false);

        onCreateKeyHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                try{
                    Bundle mData =  msg.getData();
                    int generated_wallets = mData.getInt("generated_wallets");
                    int wallets = mData.getInt("wallets");
                    if (generated_wallets < wallets) {
                        mTextViewProgress.setText(getString(R.string.generating) + "\n" + generated_wallets + "/" + wallets);
                    }
                    else {
                        mTextViewProgress.setText(getString(R.string.generating) + "\n" + wallets + "/" + wallets);
                    }


                }
                catch (Exception e){
                    e.printStackTrace(); //need to remove all of this later

                }
            }
        };

        onEncryptKeyHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                try{
                    Bundle mData =  msg.getData();
                    int encrypted_wallets = mData.getInt("encrypted_wallets");
                    int wallets = mData.getInt("wallets");
                    if (encrypted_wallets < wallets) {
                        mTextViewProgress.setText(getString(R.string.encrypting) + "\n" + encrypted_wallets + "/" + wallets);
                    }
                    else {
                        mTextViewProgress.setText(getString(R.string.encrypting) + "\n" + wallets + "/" + wallets);
                    }
                }
                catch (Exception e){
                    e.printStackTrace(); //need to remove all of this later

                }
            }
        };

        mTextViewProgress = (TextView) view.findViewById(R.id.textView_progress);
        mProgressBar = (ProgressBar) view.findViewById(R.id.create_progressBar);
        button_cancel = (Button) view.findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopServiceIntent = new Intent(createService.STOP_SERVICE);
                createService.clearAllTasks();
                mProgressBar.setProgress(0);
                bip38service.getWorker().interrupt();
                if (bip38service.getWorker().isInterrupted()){
                    Log.d("CreateActivity","thread was interrupted");
                }
                CreateActivity.createPagerAdapter.CoolNavigateToTab(0,CreateActivity.TABNUMBER,CreateActivity.createSwipeHandler,true);
            }
        });

        mProgressBar.setProgress(0);
        return view;
    }

}
