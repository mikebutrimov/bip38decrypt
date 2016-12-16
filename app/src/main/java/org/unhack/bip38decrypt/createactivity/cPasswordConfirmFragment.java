package org.unhack.bip38decrypt.createactivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.unhack.bip38decrypt.R;
import org.unhack.bip38decrypt.mfragments.imFragment;
import org.unhack.bip38decrypt.mfragments.mFragment;
import org.unhack.bip38decrypt.services.createService;

/**
 * Created by unhack on 11/25/16.
 */

public class cPasswordConfirmFragment extends mFragment implements imFragment {
    EditText  edittext_passphrase;
    CheckBox checkbox_showcontent;
    Button button_next, button_back;
    String old_password;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.create_step_confirm_password, container, false);
        old_password = getArguments().get("password").toString();

        checkbox_showcontent = (CheckBox) view.findViewById(R.id.checkBox_create_confirm_show_content);
        edittext_passphrase = (EditText) view.findViewById(R.id.editText_confirm_password);
        button_back = (Button) view.findViewById(R.id.button_create_confirm_back);
        button_next = (Button) view.findViewById(R.id.button_create_confirm_next);
        button_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CreateActivity.createPagerAdapter.CoolNavigateToTab(0,CreateActivity.TABNUMBER,CreateActivity.createSwipeHandler,false);
            }
        });

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClick(v);
            }
        });

        checkbox_showcontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContent(v);
            }
        });
        return view;
    }

    public void onNextClick(View v){
        String new_password = edittext_passphrase.getText().toString();
        if (old_password!=null){
            if (new_password.equals(old_password)){
                Bundle mDataBundle = getArguments();
                cStateFragment mcStateFragment = new cStateFragment();
                mcStateFragment.setArguments(mDataBundle);
                Intent createIntent = new Intent(getActivity().getApplicationContext(), createService.class);
                createIntent.putExtra("vanity", mDataBundle.getString("vanity"));
                createIntent.putExtra("wallets", mDataBundle.getInt("wallets",1));
                createIntent.putExtra("password", mDataBundle.getString("password"));
                getContext().startService(createIntent);
                CreateActivity.createPagerAdapter.addFragment(mcStateFragment);
                CreateActivity.createPagerAdapter.CoolNavigateToTab(CreateActivity.createPagerAdapter.getCount(),CreateActivity.TABNUMBER,CreateActivity.createSwipeHandler,false);
            }
            else {
                //show error
                Bundle mErrorBundle = new Bundle();
                mErrorBundle.putString("error", "Sorry, passphrases don't match");
                cErrorFragment mcErrorFragment = new cErrorFragment();
                mcErrorFragment.setArguments(mErrorBundle);
                CreateActivity.createPagerAdapter.addFragment(mcErrorFragment);
                CreateActivity.createPagerAdapter.CoolNavigateToTab(CreateActivity.createPagerAdapter.getCount(),CreateActivity.TABNUMBER,CreateActivity.createSwipeHandler,false);
            }
        }
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
