package org.unhack.bip38decrypt;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import net.bither.bitherj.crypto.DumpedPrivateKey;
import net.bither.bitherj.crypto.ECKey;
import net.bither.bitherj.crypto.SecureCharSequence;
import net.bither.bitherj.crypto.bip38.Bip38;
import net.bither.bitherj.exception.AddressFormatException;


public class bip38service extends IntentService {
    public static final String BIP38_SERVICE_INTENT_FILTER = "BIP38STOPSERVICE";
    private String res,wallet,address;
    public static  boolean IAM = false;
    public static Thread worker;

    private BroadcastReceiver mThreadHaltReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BIP38S","Reciever Stop service");
            getWorker().interrupt();
            Log.d("BIP38S","Reciever some service's .interrupt was called");
        }
    };

    public bip38service() {
        super("bip38service");
    }

    @Override
    public void onCreate(){
        super.onCreate();

        this.registerReceiver(mThreadHaltReciever, new IntentFilter(this.BIP38_SERVICE_INTENT_FILTER));
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



        try {

            worker = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        res = Bip38.decrypt(wallet,password).toString();
                        DumpedPrivateKey dumpedPrivateKey = new DumpedPrivateKey(res);
                        ECKey ecKey = dumpedPrivateKey.getKey();
                        address = ecKey.toAddress();
                        Log.d("Service addr",address);
                        if (needReEncrypt){
                            res = Bip38.encryptNoEcMultiply(password2,res);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (AddressFormatException e) {
                        e.printStackTrace();
                    } catch (RuntimeException e){
                        e.printStackTrace();
                    }

                    if (res != null) {
                        Log.d("Service ", "Before Handler");
                        Intent resIntent = new Intent(MainActivity.INTENT_FILTER);
                        resIntent.putExtra("result",res);
                        resIntent.putExtra("address",address);
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

    public static Thread getWorker(){
        return worker;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mThreadHaltReciever);
    }

}
