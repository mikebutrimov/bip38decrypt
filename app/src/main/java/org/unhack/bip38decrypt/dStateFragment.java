package org.unhack.bip38decrypt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by unhack on 8/3/16.
 */
public class dStateFragment extends mFragment implements imFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.decode_step_progress, container, false);
        return view;
    }
}
