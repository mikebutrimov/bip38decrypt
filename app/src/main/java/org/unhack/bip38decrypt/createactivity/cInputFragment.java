package org.unhack.bip38decrypt.createactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.unhack.bip38decrypt.R;
import org.unhack.bip38decrypt.Utils;
import org.unhack.bip38decrypt.mfragments.imFragment;
import org.unhack.bip38decrypt.mfragments.mFragment;

/**
 * Created by unhack on 11/23/16.
 */

public class cInputFragment extends mFragment implements imFragment {
    private Button button_inc_wallets, button_dec_wallets, button_back, button_next;
    EditText editText_wallets2generate,edittext_passphrase, editText_title ;
    CheckBox checkbox_showcontent;
    private Long totalCreationTarget;
    private String vanity;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("INPUT FRG", "CREATE");
        View view;
        view = inflater.inflate(R.layout.create_step_uinput, container, false);
        checkbox_showcontent = (CheckBox) view.findViewById(R.id.checkBox_create_show_content);
        edittext_passphrase = (EditText) view.findViewById(R.id.editText_create_password);
        editText_wallets2generate = (EditText) view.findViewById(R.id.editText_wallets2generate);
        editText_title = (EditText) view.findViewById(R.id.editText_walletname);
        button_inc_wallets = (Button) view.findViewById(R.id.button_inc_wallets);
        button_dec_wallets = (Button) view.findViewById(R.id.button_dec_wallets);
        button_back = (Button) view.findViewById(R.id.button_create_back);
        button_next = (Button) view.findViewById(R.id.button_create_next);

        button_inc_wallets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incDecEditText(editText_wallets2generate,true);
            }
        });

        button_dec_wallets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incDecEditText(editText_wallets2generate, false);
            }
        });


        checkbox_showcontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContent(v);
            }
        });

        final TextView textView_difficulty = (TextView) view.findViewById(R.id.textView_calculatedDifficulty);
        final TextView textView_addresswillbelike = (TextView) view.findViewById(R.id.textView_addresswillbelike);
        final EditText editText_vanity = (EditText) view.findViewById(R.id.editText_vanity);
        editText_vanity.addTextChangedListener(new TextWatcher() {
            void processtext(){
                String pattern = editText_vanity.getText().toString().trim();
                String diff = "0";
                pattern = "1" + pattern;
                try {
                    diff = Utils.getDif(pattern);
                }
                catch (NumberFormatException nfe){
                    nfe.printStackTrace();
                    diff = "0";
                }
                vanity = pattern;
                if (diff != null) {
                    totalCreationTarget = Long.valueOf(diff);
                    textView_difficulty.setText(diff);
                }
                else {
                    textView_difficulty.setText(getString(R.string.nonbase58));
                }

                textView_addresswillbelike.setText(getText(R.string.address_will_be) + pattern);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                processtext();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                processtext();
            }

            @Override
            public void afterTextChanged(Editable s) {
                processtext();
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
                if (totalCreationTarget == null && vanity!=null){
                    Toast.makeText(getContext(), getString(R.string.nonbase58), Toast.LENGTH_SHORT).show();
                }
                else {
                    totalCreationTarget = Long.valueOf(1);
                    Bundle mCreateDataBundle = new Bundle();
                    mCreateDataBundle.putString("password", edittext_passphrase.getText().toString());
                    mCreateDataBundle.putString("title", editText_title.getText().toString());
                    mCreateDataBundle.putString("vanity", vanity);
                    int wallets = 1;
                    try {
                        wallets = Integer.valueOf(editText_wallets2generate.getText().toString());
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                    mCreateDataBundle.putInt("wallets", wallets);
                    mCreateDataBundle.putLong("totalCreationTarget", totalCreationTarget * wallets);
                    cPasswordConfirmFragment mcPasswordConfirmFragment = new cPasswordConfirmFragment();
                    mcPasswordConfirmFragment.setArguments(mCreateDataBundle);
                    CreateActivity.createPagerAdapter.addFragment(mcPasswordConfirmFragment);
                    CreateActivity.createPagerAdapter.CoolNavigateToTab(1, CreateActivity.TABNUMBER, CreateActivity.createSwipeHandler, false);
                }
            }
        });
        return view;
    }

    private void incDecEditText(EditText editText, boolean action){
        int previous_value;
        try {
            previous_value = Integer.valueOf(editText.getText().toString());
        }
        catch (NumberFormatException nfe){
            nfe.printStackTrace();
            previous_value = 1;
        }

        if (previous_value == 1 && !action){
            previous_value = 1;
        }
        if (previous_value > 1 && !action){
            previous_value--;
        }
        if (previous_value >= 1 && action){
            previous_value++;
        }
        editText.setText(String.valueOf(previous_value));
    }

    private void showContent(View v){
        if (checkbox_showcontent.isChecked()){
            edittext_passphrase.setTransformationMethod(null);
        }
        else {
            edittext_passphrase.setTransformationMethod(new PasswordTransformationMethod());
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        //check for show content
        showContent(getView());
    }



}
