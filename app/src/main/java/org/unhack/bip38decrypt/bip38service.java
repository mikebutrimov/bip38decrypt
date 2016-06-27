package org.unhack.bip38decrypt;

import android.app.IntentService;
import android.content.Intent;


public class bip38service extends IntentService {


    public bip38service() {
        super("bip38service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

        }
    }

}
