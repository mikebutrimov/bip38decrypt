package org.unhack.bip38decrypt.services;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import net.bither.bitherj.crypto.ECKey;

import org.unhack.bip38decrypt.Utils;
import org.unhack.bip38decrypt.createactivity.AddressGenerator;
import org.unhack.bip38decrypt.createactivity.cStateFragment;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import javax.annotation.ParametersAreNonnullByDefault;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


public class createService extends IntentService {
    public final static String  STOP_SERVICE = "org.unhack.bip38decrypt.STOPSERVICE";
    private static String phrase = "1";
    private static String vanity = null;
    private static ECKey createdKey;
    private static boolean isSet = false;
    private static int wallets = 1;
    private static int generated_wallets = 0;
    public static Thread worker;
    private static final int cores = Runtime.getRuntime().availableProcessors();
    private static  ListeningExecutorService execService = null;


    public createService() {
        super("createService");
        if (execService != null) {
            while (!execService.isShutdown()) {
                execService.shutdownNow();
            }
            if (execService.isShutdown()) {
                Log.d("EXEC", "Was SHUTDOWNED IN ONCREATE");
            }
        }
        execService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(cores));
    }
    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("CREATE SERVICE", "onCreate reciever was set");
        phrase = "1";
        if (intent != null) {
            try {
                vanity = intent.getStringExtra("vanity");
                if (vanity != null) {
                    phrase = vanity;
                }
                wallets = intent.getIntExtra("wallets",1);
            }
            catch (NullPointerException e){
                //Oooops string was empty
                vanity = null;
            }
            startGeneration();
        }
    }

    private  void startGeneration(){
        Log.d("CREATE SERVICE PHRASE", phrase);
        generateAddress(phrase);
    }

    private  void generateAddress(final String targetPhrase) {
        final long timeStart = System.nanoTime();
        for (int i = 0; i < cores; i++) {
            try {
                Callable<ECKey> callable = new AddressGenerator(targetPhrase);
                ListenableFuture<ECKey> future = execService.submit(callable);
                Futures.addCallback(future, new FutureCallback<ECKey>() {
                    @Override
                    public void onSuccess(ECKey key) {
                        if (key.toAddress().startsWith(targetPhrase)) {
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
                return;
            }
        }

    }
    private static synchronized boolean lockKey(){
        boolean isLocked = isSet;
        isSet = true;
        return isLocked;
    }

    private  void setCreatedKey(ECKey key){
        if (!lockKey()){
            createdKey = key;
            String lesText = "Address: " + key.toAddress() + "Private Key: " + Utils.encodePrivateKeyToWIF(key.getPrivKeyBytes());
            Log.d("GENERATE", lesText);
            Message mKeyMsg = new Message();
            Bundle mData = new Bundle();
            mData.putString("address", key.toAddress());
            mData.putString("privatekey", Utils.encodePrivateKeyToWIF(key.getPrivKeyBytes()));
            cStateFragment.onCreateKeyHandler.sendMessage(mKeyMsg);
            Log.d("C Service w", " "+String.valueOf(generated_wallets)+ " " + String.valueOf(wallets));
            generated_wallets++;
        }
    }

    public static void clearAllTasks(){
        while (!execService.isShutdown()) {
            execService.shutdownNow();
        }
        if (execService.isShutdown()){
            Log.d("EXEC", "IS SHUTTED DOWN");
        }
    }

}
