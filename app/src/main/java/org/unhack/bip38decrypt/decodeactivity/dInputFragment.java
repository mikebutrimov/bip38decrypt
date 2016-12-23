package org.unhack.bip38decrypt.decodeactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.zxing.integration.android.IntentIntegrator;

import net.bither.bitherj.crypto.bip38.Bip38;
import net.bither.bitherj.exception.AddressFormatException;

import org.unhack.bip38decrypt.R;
import org.unhack.bip38decrypt.mfragments.imFragment;
import org.unhack.bip38decrypt.mfragments.mFragment;
import org.unhack.bip38decrypt.services.bip38service;

/**
 * Created by unhack on 7/27/16.
 */
public class dInputFragment extends mFragment implements imFragment {
    public static Handler decodeInputFragmentUpdateWallet;
    private boolean reencrypt = false;
    private boolean fired = false;
    EditText edittext_wallet, edittext_passphrase, editText_newpassphrase;
    Button button_back, button_next;
    ImageButton button_scan;
    Bundle mBundle;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        mBundle = this.getArguments();
        try{
            reencrypt = mBundle.getBoolean("reencrypt", false);
        }
        catch (Exception e){
            reencrypt = false;
        }

        if (reencrypt) {
            view = inflater.inflate(R.layout.decode_step_uinput_re, container, false);
            editText_newpassphrase = (EditText) view.findViewById(R.id.editText_newpassphrase);
            editText_newpassphrase.setOnKeyListener(new View.OnKeyListener()
            {
                public boolean onKey(View v, int keyCode, KeyEvent event)
                {
                    if (event.getAction() == KeyEvent.ACTION_DOWN)
                    {
                        switch (keyCode)
                        {
                            case KeyEvent.KEYCODE_ENTER:
                                onNextClick(v);
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                return true;
                            default:
                                break;
                        }
                    }
                    return false;
                }
            });
        } else {
            view = inflater.inflate(R.layout.decode_step_uinput_nore, container, false);
        }
        decodeInputFragmentUpdateWallet = new Handler(){
            public void handleMessage(android.os.Message msg) {
                try {
                    edittext_wallet.setText(msg.getData().getString("wallet"));
                }
                catch (Exception e){
                    //Ooops
                }
            }
        };
        //init local vars and view elements
        edittext_wallet = (EditText) view.findViewById(R.id.editText_wallet);
        edittext_passphrase = (EditText) view.findViewById(R.id.editText_passphrase);
        button_back = (Button) view.findViewById(R.id.button_back);
        button_next = (Button) view.findViewById(R.id.button_next);
        button_scan = (ImageButton) view.findViewById(R.id.button_scan);
        //check for show content
        //set onclick listeners
        if (!reencrypt) {
            edittext_passphrase.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_ENTER:
                                onNextClick(v);
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                return true;
                            default:
                                break;
                        }
                    }
                    return false;
                }
            });
        }

        button_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getActivity().finish();
            }
        });
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClick(v);
            }
        });
        button_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQr();
            }
        });

        if (!fired){
            fired = true;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scanQr();
                }
            });
        }

        return view;
    }


    public void onNextClick(View V){
        Intent bip38ServiceIntent = new Intent(getActivity().getApplicationContext(), bip38service.class);
        try {
            if (Bip38.isBip38PrivateKey(edittext_wallet.getText().toString())){
                Log.d("InputFRG","Wrong key");
                bip38ServiceIntent.putExtra("wallet", edittext_wallet.getText().toString());
                bip38ServiceIntent.putExtra("password", edittext_passphrase.getText().toString());
                bip38ServiceIntent.putExtra("reencrypt",reencrypt);
                try{
                    bip38ServiceIntent.putExtra("password2",editText_newpassphrase.getText().toString());
                }
                catch (Exception e){

                }
                getContext().startService(bip38ServiceIntent);
                dStateFragment stateFrg = new dStateFragment();
                DecodeActivity.decodePagerAdapter.addFragment(stateFrg);
                DecodeActivity.decodePagerAdapter.CoolNavigateToTab(1,DecodeActivity.TABNUMBER,DecodeActivity.decodeSwipeHandler,false);
            }
            else {
                Bundle mBundle = new Bundle();
                mBundle.putString("error",getString(R.string.notBipKey));
                Intent decodeErrorIntent = new Intent(DecodeActivity.DECODE_INTENT_ERROR);
                decodeErrorIntent.putExtra("args",mBundle);
                getActivity().sendBroadcast(decodeErrorIntent);
            }
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }
    }




    public void scanQr(){
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.setDesiredBarcodeFormats(integrator.QR_CODE_TYPES);
        integrator.setPrompt("Place qr-code into the scanner area");
        integrator.initiateScan();
    }

}
