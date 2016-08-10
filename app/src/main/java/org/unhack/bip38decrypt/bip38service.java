package org.unhack.bip38decrypt;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import net.bither.bitherj.crypto.DumpedPrivateKey;
import net.bither.bitherj.crypto.ECKey;
import net.bither.bitherj.crypto.SecureCharSequence;
import net.bither.bitherj.crypto.bip38.Bip38;
import net.bither.bitherj.exception.AddressFormatException;


public class bip38service extends IntentService {
    private String res,wallet,address;
    public static  boolean IAM = false;
    public bip38service() {
        super("bip38service");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        IAM = true;
           wallet = intent.getStringExtra("wallet");
        Log.d("Service W",wallet);
        final String password = intent.getStringExtra("password");
        Log.d("Service P",password);
        final String password2 = intent.getStringExtra("password2");
        final boolean needReEncrypt = intent.getBooleanExtra("reencrypt",false);
        SecureCharSequence result = null;
        try {
            result = Bip38.decrypt(wallet,password);
            res = result.toString();
            DumpedPrivateKey dumpedPrivateKey = new DumpedPrivateKey(res);
            ECKey ecKey = dumpedPrivateKey.getKey();
            address = ecKey.toAddress();
            Log.d("Service addr",address);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (AddressFormatException e) {
            e.printStackTrace();
        } catch (NullPointerException e){

        }
        if (result != null) {
            res = result.toString();
            Log.d("Service res",res);
        }
        if (needReEncrypt){
            try {
                res = Bip38.encryptNoEcMultiply(password2,res);
                Log.d("Service re res",res);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (AddressFormatException e) {
                e.printStackTrace();
            }
        }


        if (res != null) {
            Log.d("Service ", "Before Handler");

            Intent resIntent = new Intent(MainActivity.INTENT_FILTER);
            resIntent.putExtra("result",res);
            resIntent.putExtra("address",address);
            sendBroadcast(resIntent);
            Log.d("Service ", "After Handler");


        }
        else {
        }
        IAM = false;
    }
}
