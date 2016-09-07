package org.unhack.bip38decrypt.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import net.bither.bitherj.crypto.ECKey;

import org.unhack.bip38decrypt.Utils;
import org.unhack.bip38decrypt.createactivity.AddressGenerator;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import javax.annotation.ParametersAreNonnullByDefault;



public class createService extends IntentService {
    private String phrase = "1";
    private String vanity = null;
    private static ECKey createdKey;
    private static boolean isSet = false;
    public createService() {
        super("createService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                vanity = intent.getStringExtra("vanity");
            }
            catch (NullPointerException e){
                //Oooops string was empty
                vanity = null;
            }
            phrase = phrase + vanity;
            generateAddress(phrase);
        }
    }

    private static void generateAddress(final String targetPhrase) {
        final int cores = Runtime.getRuntime().availableProcessors();
        final ListeningExecutorService execService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(cores));
        final long timeStart = System.nanoTime();
        for (int i = 0; i < cores; i++) {
            Callable<ECKey> callable = new AddressGenerator(targetPhrase);
            ListenableFuture<ECKey> future = execService.submit(callable);
            Futures.addCallback(future, new FutureCallback<ECKey>() {
                @Override
                public void onSuccess(ECKey key) {
                    if (key.toAddress().toString().startsWith(targetPhrase)) {
                        setCreatedKey(key);
                    }
                    execService.shutdownNow();
                }
                @Override
                @ParametersAreNonnullByDefault
                public void onFailure(Throwable thrown) {
                    Log.d("generator",thrown.getMessage());
                }
            });
        }
    }
    private static synchronized boolean lockKey(){
        boolean isLocked = isSet;
        isSet = true;
        return isLocked;
    }

    private static  void setCreatedKey(ECKey key){
        if (!lockKey()){
            createdKey = key;
            String lesText = "Address: " + key.toAddress() + "Private Key: " + Utils.encodePrivateKeyToWIF(key.getPrivKeyBytes());
            Log.d("GENERATE", lesText);
        }
    }
}
