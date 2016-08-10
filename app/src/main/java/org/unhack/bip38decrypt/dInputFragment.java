package org.unhack.bip38decrypt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.zxing.integration.android.IntentIntegrator;

/**
 * Created by unhack on 7/27/16.
 */
public class dInputFragment extends mFragment implements imFragment {
    public static Handler decodeInputFragmentUpdateWallet;
    private boolean fired = false;
    EditText edittext_wallet, edittext_passphrase;
    CheckBox checkbox_showcontent;
    Button button_back, button_next;
    ImageButton button_scan;
    Bundle mBundle;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.decode_step_uinput, container, false);
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
        mBundle = this.getArguments();
        edittext_wallet = (EditText) view.findViewById(R.id.editText_wallet);
        edittext_passphrase = (EditText) view.findViewById(R.id.editText_passphrase);
        checkbox_showcontent = (CheckBox) view.findViewById(R.id.checkBox_showcontent);
        button_back = (Button) view.findViewById(R.id.button_back);
        button_next = (Button) view.findViewById(R.id.button_next);
        button_scan = (ImageButton) view.findViewById(R.id.button_scan);
        //check for show content
        showContent(view);
        //set onclick listeners
        checkbox_showcontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContent(v);
            }
        });
        button_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getActivity().finish();
            }
        });
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("decode","In add fragment");
                Intent bip38ServiceIntent = new Intent(getActivity().getApplicationContext(), bip38service.class);
                bip38ServiceIntent.putExtra("wallet", edittext_wallet.getText().toString());
                bip38ServiceIntent.putExtra("password", edittext_passphrase.getText().toString());
                getContext().startService(bip38ServiceIntent);
                dStateFragment stateFrg = new dStateFragment();
                DecodeActivity.decodePagerAdapter.addFragment(stateFrg);
                DecodeActivity.decodePagerAdapter.CoolNavigateToTab(1,DecodeActivity.TABNUMBER,DecodeActivity.decodeSwipeHandler,false);
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

    private void showContent(View v){
        if (checkbox_showcontent.isChecked()){
            edittext_passphrase.setTransformationMethod(null);
        }
        else {
            edittext_passphrase.setTransformationMethod(new PasswordTransformationMethod());
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
