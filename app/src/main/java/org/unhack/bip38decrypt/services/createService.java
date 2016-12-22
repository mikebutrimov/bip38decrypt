package org.unhack.bip38decrypt.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import net.bither.bitherj.crypto.ECKey;

import org.unhack.bip38decrypt.MainActivity;
import org.unhack.bip38decrypt.Utils;
import org.unhack.bip38decrypt.createactivity.AddressGenerator;
import org.unhack.bip38decrypt.createactivity.cStateFragment;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.ParametersAreNonnullByDefault;


public class createService extends IntentService {
    public final static String  STOP_SERVICE = "org.unhack.bip38decrypt.STOPSERVICE";
    private String mKeyPhrase = "1";
    private String password = null;
    private int wallets = 1;
    private volatile int  generated_wallets = 0;
    private static final int cores = Runtime.getRuntime().availableProcessors();
    private static ListeningExecutorService mExecutorService = null;
    private volatile HashMap<String,String> mWallets = new HashMap<>();
    private boolean isSet = false;
    private volatile boolean isFired = false;

    public createService() {
        super("createService");
        //Clear executor if exists
        if (mExecutorService != null) {
            while (!mExecutorService.isShutdown()) {
                mExecutorService.shutdownNow();
            }
        }
        mExecutorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(cores));
    }
    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        password = intent.getStringExtra("password");
        String vanity = null;
        mKeyPhrase = "1";
        try {
            vanity = intent.getStringExtra("vanity");
            if (vanity != null) {
                mKeyPhrase = vanity;
            }
            wallets = intent.getIntExtra("wallets",1);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        try {
            Message mKeyMsg = new Message();
            Bundle mData = new Bundle();
            mData.putInt("generated_wallets", generated_wallets);
            mData.putInt("wallets", wallets);
            mKeyMsg.setData(mData);
            cStateFragment.onCreateKeyHandler.sendMessage(mKeyMsg);
            generateAddress(mKeyPhrase);
        }
        catch (NullPointerException npe){
            npe.printStackTrace();
        }
    }

    private void submitTask(final String mPhrase){
        try {
            Callable<ECKey> callable = new AddressGenerator(mPhrase);
            ListenableFuture<ECKey> future = mExecutorService.submit(callable);
            Futures.addCallback(future, new FutureCallback<ECKey>() {
                @Override
                public void onSuccess(ECKey key) {
                    if (key.toAddress().startsWith(mPhrase)) {
                        setCreatedKey(key);
                    }
                }
                @Override
                @ParametersAreNonnullByDefault
                public void onFailure(Throwable thrown) {
                    Log.d("generator", thrown.getMessage());
                }
            });
        }
        catch (RejectedExecutionException ree){
            ree.printStackTrace();
        }
    }

    private   void generateAddress(final String targetPhrase) {
        for (int i = 0; i < cores; i++) {
            submitTask(targetPhrase);
        }
    }

    private synchronized void setCreatedKey(ECKey key) {
        while (!getLock());
        Message mKeyMsg = new Message();
        Bundle mData = new Bundle();
        mData.putString("address", key.toAddress());
        mData.putString("privatekey", Utils.encodePrivateKeyToWIF(key.getPrivKeyBytes()));
        mData.putInt("generated_wallets",generated_wallets);
        mData.putInt("wallets",wallets);
        mKeyMsg.setData(mData);
        cStateFragment.onCreateKeyHandler.sendMessage(mKeyMsg);

        String mWIFKey = Utils.encodePrivateKeyToWIF(key.getPrivKeyBytes());
        String lesText = "Address: " + key.toAddress() + "Private Key: " + mWIFKey;
        if (mWallets.size() < wallets) {
            mWallets.put(key.toAddress(), mWIFKey);
        }
        generated_wallets++;

        if (generated_wallets < wallets) {
            submitTask(mKeyPhrase);
        } else {
            clearAllTasks();
            //force bip38 service to service
            if (!isFired) {
                Intent encryptIntent = new Intent(getApplicationContext(), bip38service.class);
                encryptIntent.putExtra("password", password);
                encryptIntent.putExtra("hashMapWallets", mWallets);
                startService(encryptIntent);
                isFired = true;
            }
        }
        releaseLock();
    }



    public static void clearAllTasks(){
        while (!mExecutorService.isShutdown()) {
            mExecutorService.shutdownNow();
            }
        if (mExecutorService.isShutdown()){
            Log.d("EXEC", "IS SHUTTED DOWN");
        }
    }

    private synchronized boolean getLock(){
        boolean isLocked = isSet;
        isSet = true;
        return isLocked;
    }
    private synchronized void releaseLock(){
        isSet = false;
    }

}
