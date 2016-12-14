package org.unhack.bip38decrypt.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import net.bither.bitherj.crypto.DumpedPrivateKey;
import net.bither.bitherj.crypto.ECKey;
import net.bither.bitherj.crypto.bip38.Bip38;
import net.bither.bitherj.exception.AddressFormatException;

import org.unhack.bip38decrypt.decodeactivity.DecodeActivity;
import org.unhack.bip38decrypt.MainActivity;
import org.unhack.bip38decrypt.R;
import org.unhack.bip38decrypt.Utils;

import java.util.HashMap;
import java.util.Map;

public class bip38service extends IntentService {
    private String res,wallet,address;
    public static  boolean IAM = false;
    public static Thread worker;
    public bip38service() {
        super("org.unhack.bip38decrypt.services.bip38service");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        IAM = true;
        wallet = intent.getStringExtra("wallet");
        final String password = intent.getStringExtra("password");
        final String password2 = intent.getStringExtra("password2");
        final boolean needReEncrypt = intent.getBooleanExtra("reencrypt", false);
        HashMap<String, String> mWalletsToEncrypt = null;
        mWalletsToEncrypt = (HashMap<String, String>) intent.getSerializableExtra("hashMapWallets");
        if (mWalletsToEncrypt != null) {
            Log.d("BIP SERVICE", "Stage 1");
            if (password != null) {
                if (!password.isEmpty()) {
                    try {
                        Log.d("BIP SERVICE", "Stage 2");
                        for (Map.Entry<String, String> mEntry : mWalletsToEncrypt.entrySet()) {
                            DumpedPrivateKey dumpedPrivateKey = new DumpedPrivateKey(mEntry.getValue());
                            ECKey ecKey = dumpedPrivateKey.getKey();
                            byte[] privKey = ecKey.getPrivKeyBytes();
                            try {
                                res = Utils.encodePrivateKeyToWIF(privKey);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            res = Bip38.encryptNoEcMultiply(password, res);
                            Log.d("BIP38 RES", res);
                            mEntry.setValue(res);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Intent resIntent = new Intent(MainActivity.INTENT_FILTER);
            resIntent.putExtra("hashmapWallets", mWalletsToEncrypt);
            sendBroadcast(resIntent);
            Log.d("Service ", "After miltiple encryption");
            IAM = false;
        } else {
            try {
                worker = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            res = Bip38.decrypt(wallet, password).toString();
                            DumpedPrivateKey dumpedPrivateKey = new DumpedPrivateKey(res);
                            ECKey ecKey = dumpedPrivateKey.getKey();
                            Log.d("ecKEY", ecKey.toStringWithPrivate());
                            address = ecKey.toAddress();
                            byte[] privKey = ecKey.getPrivKeyBytes();
                            //convert priv key to uncompressed format
                            try {
                                res = Utils.encodePrivateKeyToWIF(privKey);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.d("Service addr", address);
                            if (needReEncrypt) {
                                Intent setStateIntent = new Intent(DecodeActivity.DECODE_STATE_FILTER);
                                setStateIntent.putExtra("state", getString(R.string.state_encoding));
                                sendBroadcast(setStateIntent);
                                res = Bip38.encryptNoEcMultiply(password2, res);
                            }

                        } catch (NullPointerException e) {
                            //probably wrong password / input
                            //ok, we need to create wrong password error fragment
                            Log.d("Service", "NullPointer!");
                            Bundle mBundle = new Bundle();
                            mBundle.putString("error", getString(R.string.wrongPassword));
                            Intent decodeErrorIntent = new Intent(DecodeActivity.DECODE_INTENT_ERROR);
                            decodeErrorIntent.putExtra("args", mBundle);
                            sendBroadcast(decodeErrorIntent);
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (AddressFormatException e) {
                            e.printStackTrace();
                        } catch (RuntimeException e) {
                            //it's ok
                            //it will happen on thread.interrupt()
                            e.printStackTrace();
                        }
                        if (res != null) {
                            Log.d("Service ", "Before Handler");
                            Intent resIntent = new Intent(MainActivity.INTENT_FILTER);
                            HashMap<String, String> mWallet = new HashMap<>();
                            mWallet.put(address, res);
                            resIntent.putExtra("hashmapWallets", mWallet);
                            sendBroadcast(resIntent);
                            Log.d("Service ", "After Handler");
                        }
                        IAM = false;
                    }
                });
                worker.setPriority(Thread.MAX_PRIORITY);
                worker.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static Thread getWorker(){
        return worker;
    }
}
