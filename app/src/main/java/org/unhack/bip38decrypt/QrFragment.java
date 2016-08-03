package org.unhack.bip38decrypt;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Created by unhack on 6/23/16.
 */
public class QrFragment extends mFragment implements imFragment {
    private String addr,wallet;
    private Bitmap qrbitmap;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qrfragment_layout, container, false);
        ImageView qrView = (ImageView) view.findViewById(R.id.imageView);
        Bundle mBundle =  this.getArguments();
        try {
            this.addr = mBundle.getString("addr");
            this.wallet = mBundle.getString("res");
            qrbitmap = getQr(this.wallet);
            Drawable qrD = new BitmapDrawable(this.qrbitmap);
            qrView.setImageDrawable(qrD);
        }
        catch (Exception e){
            //todo handle exception
            Log.d("QrFragment",e.toString());
        }


        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.fab_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.pagerAdapter.NavigateToTab(0);
            }
        });
        return view;
    }

    private Bitmap getQr(String lesText){
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(lesText, BarcodeFormat.QR_CODE, 256, 256);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;

        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

}


