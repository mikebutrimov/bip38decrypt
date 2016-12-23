package org.unhack.bip38decrypt.createactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import org.unhack.bip38decrypt.R;
import org.unhack.bip38decrypt.mfragments.imFragment;
import org.unhack.bip38decrypt.mfragments.mFragment;
import org.unhack.bip38decrypt.services.createService;

/**
 * Created by unhack on 11/25/16.
 */

public class cPasswordConfirmFragment extends mFragment implements imFragment {
    EditText  edittext_passphrase;
    Button button_next, button_back;
    String old_password;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.create_step_confirm_password, container, false);
        old_password = getArguments().get("password").toString();

        edittext_passphrase = (EditText) view.findViewById(R.id.editText_confirm_password);
        edittext_passphrase.setOnKeyListener(new View.OnKeyListener()
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



}
