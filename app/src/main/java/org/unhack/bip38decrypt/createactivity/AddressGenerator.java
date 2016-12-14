package org.unhack.bip38decrypt.createactivity;
/*
The MIT License (MIT)

Copyright (c) 2015 lifeofreilly

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


This part was taken from https://github.com/lifeofreilly/vanitas
and slightly modified to get rid of bitcoinj library

 */

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import net.bither.bitherj.crypto.ECKey;

import org.unhack.bip38decrypt.Utils;

import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.Callable;


public class AddressGenerator implements Callable<ECKey> {

    private static final int BTC_ADDRESS_MAX_LENGTH = 35;
    //private final NetworkParameters netParams;
    private long attempts;
    private String targetPhrase;
    private SecureRandom rnd;
    /**
     * Sole constructor for AddressGenerator
     *
     * @param targetPhrase the desired bitcoin address substring
     */
    public AddressGenerator(final String targetPhrase) {
        if (Utils.isValidBTCAddressSubstring(targetPhrase)) {
            this.targetPhrase = targetPhrase;
        } else {
            throw new IllegalArgumentException("The requested phrase is not a valid bitcoin address substring.");
        }
    }

    /**
     * Attempts to compute a bitcoin address that contains the target phrase.
     *
     * @return An ECKey which represents an elliptic curve public key (bitcoin address) and private key
     * @throws Exception
     */
    @Override
    public ECKey call() throws Exception {
        ECKey key;
        do {
            key = ECKey.generateECKey(rnd);
            attempts++;
            //logAttempts();
        } while (!(key.toAddress().startsWith(targetPhrase)) &&
                !Thread.currentThread().isInterrupted());
        return key;
    }

    /**
     * Logs progress every 1M attempts
     */
    private void logAttempts() {
        if (attempts % 1000 == 0) {
            //Log.d("Thread ", Thread.currentThread().getName() + " is still working, # of attempts: " +
            //        NumberFormat.getNumberInstance(Locale.US).format(attempts));
            //Message mMsg = new Message();
            //Bundle mBundle = new Bundle();
            //mBundle.putLong("progress", attempts);
            //mMsg.setData(mBundle);
            //cStateFragment.onCreateProgressCreateHandler.sendMessage(mMsg);
        }
    }
}