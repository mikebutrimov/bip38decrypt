package org.unhack.bip38decrypt;


import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by unhack on 6/23/16.
 */
public class StartCreationFragment extends mFragment implements imFragment {
    public static Handler startCreationFragmentHandler;
    public View mView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        startCreationFragmentHandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                //TextView testView = (TextView)  mView.findViewById(R.id.textView2);
                //testView.setText("Test");
                LinearLayout buttonLayout = (LinearLayout) mView.findViewById(R.id.LLButtons);
                LinearLayout loadingLayout = (LinearLayout) mView.findViewById(R.id.LLLoading);
                buttonLayout.setVisibility(View.INVISIBLE);
                loadingLayout.setVisibility(View.VISIBLE);

            }
        };

        View view = inflater.inflate(R.layout.creationfragment_layout, container, false);
        Toast.makeText(getContext(),"Ololo",Toast.LENGTH_LONG).show();
        mView = view;
        return view;
    }

}