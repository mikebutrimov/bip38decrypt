package org.unhack.bip38decrypt.services;

import android.app.IntentService;
import android.content.Intent;

import net.bither.bitherj.crypto.ECKey;

import org.unhack.bip38decrypt.createactivity.CreateActivity;

import java.security.SecureRandom;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class speedtest extends IntentService {
    public final int cores = Runtime.getRuntime().availableProcessors();
    public speedtest() {
        super("speedtest");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            ECKey key;
            SecureRandom rnd = new SecureRandom();
            //warm up to force android speed-up processors
            for (int i = 0; i < 100; i++){
                key = ECKey.generateECKey(rnd);
            }
            int keys = 0;
            long test_end = System.currentTimeMillis()+500;
            while (System.currentTimeMillis() < test_end){
                key = ECKey.generateECKey(rnd);
                keys++;
            }
            Intent resIntent = new Intent(CreateActivity.SPEEDTEST_FILTER);
            keys = keys*2*cores;
            resIntent.putExtra("speed",keys);
            sendBroadcast(resIntent);
        }

    }
}


