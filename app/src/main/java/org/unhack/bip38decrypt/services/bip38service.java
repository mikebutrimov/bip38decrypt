package org.unhack.bip38decrypt.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import net.bither.bitherj.crypto.DumpedPrivateKey;
import net.bither.bitherj.crypto.ECKey;
import net.bither.bitherj.crypto.bip38.Bip38;
import net.bither.bitherj.exception.AddressFormatException;
import net.bither.bitherj.net.StreamParser;

import org.unhack.bip38decrypt.decodeactivity.DecodeActivity;
import org.unhack.bip38decrypt.MainActivity;
import org.unhack.bip38decrypt.R;
import org.unhack.bip38decrypt.Utils;

import java.util.HashMap;
import java.util.Map;

import static org.unhack.bip38decrypt.createactivity.cStateFragment.onEncryptKeyHandler;

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
        final HashMap<String, String> mWalletsToEncrypt = (HashMap<String, String>) intent.getSerializableExtra("hashMapWallets");
        Log.d("BIP38 SERVICE", "VARS: " + mWalletsToEncrypt.toString() + " is empty" + mWalletsToEncrypt.isEmpty() + "size" + mWalletsToEncrypt.size());
        if (!mWalletsToEncrypt.isEmpty()) {
            worker = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("BIP SERVICE", "Stage 1");
                        HashMap<String, String> mResEncryptedWallets = new HashMap<>();
                        if (!password.isEmpty()) {
                            try {
                                Log.d("BIP SERVICE", "Stage 2");
                                int encrypted = 0;
                                for (Map.Entry<String, String> mEntry : mWalletsToEncrypt.entrySet()) {
                                    Message mWalletEncryptedMessage = new Message();
                                    Bundle mData = new Bundle();
                                    mData.putInt("encrypted_wallets", encrypted);
                                    mData.putInt("wallets", mWalletsToEncrypt.size());
                                    mWalletEncryptedMessage.setData(mData);
                                    onEncryptKeyHandler.sendMessage(mWalletEncryptedMessage);
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
                                    mResEncryptedWallets.put(mEntry.getKey(),res);
                                    encrypted++;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            mResEncryptedWallets = new HashMap<>(mWalletsToEncrypt);
                            Log.d("Bip38 stahge3", "new hashmap size: " + mResEncryptedWallets.size()  );
                        }
                        Intent resIntent = new Intent(MainActivity.INTENT_FILTER);
                        resIntent.putExtra("hashmapWallets", mResEncryptedWallets);
                        sendBroadcast(resIntent);
                        Log.d("Service ", "After muiltiple encryption");
                        IAM = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            worker.setPriority(Thread.MAX_PRIORITY);
            worker.start();


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
