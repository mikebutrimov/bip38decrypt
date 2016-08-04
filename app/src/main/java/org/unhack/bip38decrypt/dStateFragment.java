package org.unhack.bip38decrypt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by unhack on 8/3/16.
 */
public class dStateFragment extends mFragment implements imFragment {
    Button button_cancel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.decode_step_progress, container, false);
        button_cancel = (Button) view.findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DecodeActivity.pagerAdapter.CoolNavigateToTab(0,DecodeActivity.TABNUMBER,DecodeActivity.mSwipeHandler);
            }
        });
        return view;
    }
}
