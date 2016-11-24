package org.unhack.bip38decrypt.createactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.unhack.bip38decrypt.R;
import org.unhack.bip38decrypt.Utils;
import org.unhack.bip38decrypt.mfragments.imFragment;
import org.unhack.bip38decrypt.mfragments.mFragment;

/**
 * Created by unhack on 11/23/16.
 */

public class cInputFragment extends mFragment implements imFragment {
    private Button button_inc_wallets, button_dec_wallets;
    EditText editText_wallets2generate,edittext_passphrase ;
    CheckBox checkbox_showcontent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.create_step_uinput, container, false);
        checkbox_showcontent = (CheckBox) view.findViewById(R.id.checkBox_create_show_content);
        edittext_passphrase = (EditText) view.findViewById(R.id.editText_create_password);
        editText_wallets2generate = (EditText) view.findViewById(R.id.editText_wallets2generate);
        button_inc_wallets = (Button) view.findViewById(R.id.button_inc_wallets);
        button_dec_wallets = (Button) view.findViewById(R.id.button_dec_wallets);
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
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pattern = editText_vanity.getText().toString();
                pattern = "1" + pattern;
                String diff = Utils.getDif(pattern);
                textView_difficulty.setText(diff);
                textView_addresswillbelike.setText(getText(R.string.address_will_be) + pattern);
            }

            @Override
            public void afterTextChanged(Editable s) {
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



}
