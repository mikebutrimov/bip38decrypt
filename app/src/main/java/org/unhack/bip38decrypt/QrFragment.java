package org.unhack.bip38decrypt;

import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by unhack on 6/23/16.
 */
public class QrFragment extends mFragment implements imFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qrfragment_layout, container, false);
        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.fab_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixedPagerAdapter.NavigateToTab(0);
            }
        });
        return view;
    }

}


