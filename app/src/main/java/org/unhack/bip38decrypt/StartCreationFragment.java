package org.unhack.bip38decrypt;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

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
               setMode(true);
            }
        };

        View view = inflater.inflate(R.layout.creationfragment_layout, container, false);
        TextView tv_decode = (TextView) view.findViewById(R.id.textView_decode);
        tv_decode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQr();
            }
        });
        mView = view;
        return view;
    }


    public void setMode(boolean mode) {
        LinearLayout buttonLayout = (LinearLayout) mView.findViewById(R.id.LLButtons);
        LinearLayout loadingLayout = (LinearLayout) mView.findViewById(R.id.LLLoading);
        if (mode) {
            buttonLayout.setVisibility(View.INVISIBLE);
            loadingLayout.setVisibility(View.VISIBLE);
        } else {
            buttonLayout.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void scanQr(){
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(integrator.QR_CODE_TYPES);
        integrator.setPrompt("Place qr-code into the scanner area");
        integrator.initiateScan();
    }
}