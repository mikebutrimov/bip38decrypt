package org.unhack.bip38decrypt.createactivity;

import android.content.Intent;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
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
import org.unhack.bip38decrypt.services.createService;

/**
 * Created by unhack on 11/29/16.
 */

public class cStateFragment extends mFragment implements imFragment {
    View view;
    private Button button_cancel;
    private  ProgressBar mProgressBar;
    private Bundle mArgs;
    private TextView mTextViewStatus;
    private long creationProgress, totalCreationTarget;
    private int encryptionProgress, totalEncryptionTarget, wallets;
    private String password, title, vanity;
    public static Handler onCreateProgressCreateHandler, onStateWorkerHandler, onCreateKeyHandler;
    private int cores = Runtime.getRuntime().availableProcessors();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.create_step_progress, container, false);

        onCreateKeyHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                try{
                    Log.d("ON KEY HANDLER", "We have get a key, ok");
                }
                catch (Exception e){
                    e.printStackTrace(); //need to remove all of this later

                }
            }
        };

        onCreateProgressCreateHandler = new Handler(){
            public  void handleMessage(android.os.Message msg) {
                try {
                    Bundle mData = msg.getData();
                    long currProgress = mData.getLong("progress");
                    setCreationProgress(currProgress);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        onStateWorkerHandler = new Handler(){
            public  void handleMessage(android.os.Message msg) {
                try {
                    Bundle mData = msg.getData();
                    int currProgress = mData.getInt("progress");
                    setEncryptionProgress(currProgress);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        creationProgress = encryptionProgress = 0;
        try {
            mArgs = getArguments();
            totalCreationTarget = mArgs.getLong("totalCreationTarget");
            password = mArgs.getString("password");
            title = mArgs.getString("rirle");
            vanity = mArgs.getString("vanity");
            totalEncryptionTarget = mArgs.getInt("wallets");
            wallets = totalEncryptionTarget;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        mProgressBar = (ProgressBar) view.findViewById(R.id.create_progressBar);
        button_cancel = (Button) view.findViewById(R.id.button_cancel);


        //fire up intent services for wallets to create
        for (int i = 0; i< wallets; i++){
            Intent createIntent = new Intent(getActivity().getApplicationContext(), createService.class);
            createIntent.putExtra("vanity", vanity);
            getContext().startService(createIntent);
        }


        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createService.clearAllTasks();
                createService.getworker().interrupt();
                CreateActivity.createPagerAdapter.CoolNavigateToTab(0,CreateActivity.TABNUMBER,CreateActivity.createSwipeHandler,true);
            }
        });
        return view;
    }




    public void setCreationProgress(long currProgress){
        creationProgress = currProgress * cores;
        double percentage = creationProgress *100.0/ totalCreationTarget;
        mProgressBar.setProgress((int)percentage);
        //Log.d("CREATE STATUS VARS", String.valueOf(creationProgress) + " " + String.valueOf(totalCreationTarget) + " " + String.valueOf((int) percentage));
        //Log.d("CREATE STATUS FRAGMENT", "Progress is: " + String.valueOf(percentage));
    }

    public void setEncryptionProgress(int currProgress){

    }
}
