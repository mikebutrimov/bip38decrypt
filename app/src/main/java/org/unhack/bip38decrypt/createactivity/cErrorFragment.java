package org.unhack.bip38decrypt.createactivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.unhack.bip38decrypt.R;
import org.unhack.bip38decrypt.mfragments.imFragment;
import org.unhack.bip38decrypt.mfragments.mFragment;

/**
 * Created by unhack on 11/25/16.
 */

public class cErrorFragment extends mFragment implements imFragment {
    TextView textView_error;
    Button button_oops;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_step_error, container, false);
        textView_error = (TextView) view.findViewById(R.id.textView_error);
        button_oops = (Button) view.findViewById(R.id.button_oops);
        button_oops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateActivity.createPagerAdapter.CoolNavigateToTab(0,CreateActivity.TABNUMBER,CreateActivity.createSwipeHandler,true);
            }
        });
        textView_error.setText(getArguments().getString("error"));
        return view;
    }
}
