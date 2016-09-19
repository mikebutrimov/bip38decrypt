package org.unhack.bip38decrypt.createactivity;/*
The MIT License (MIT)

Copyright (c) 2015 lifeofreilly

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


Some code was taken from https://github.com/lifeofreilly/vanitas
and slightly modified to get rid of bitcoinj library

 */


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;


import net.bither.bitherj.crypto.ECKey;
import net.bither.bitherj.exception.AddressFormatException;
import net.bither.bitherj.utils.Base58;

import org.unhack.bip38decrypt.R;
import org.unhack.bip38decrypt.Utils;
import org.unhack.bip38decrypt.services.createService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

import javax.annotation.ParametersAreNonnullByDefault;


public class CreateActivity extends AppCompatActivity {

    public static TextView textview4;
    BigDecimal begin_test;
    BigDecimal biggest_niumber;
    BigDecimal end_test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);
        textview4 = (TextView) findViewById(R.id.textView4);
        Button lesButton = (Button) findViewById(R.id.button);
        lesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startCreateIntent = new Intent(getApplicationContext(), createService.class);
                startCreateIntent.putExtra("vanity","13");
                startService(startCreateIntent);
            }
        });

        try {
            //New potential biggest number.
            byte[] buf = Utils.hexStringToByteArray("00ffffffffffffffffffffffffffffffffffffffffffffffff");
            biggest_niumber = new BigDecimal(new BigInteger(buf));
            String pattern,smallest,p_buf,s_buf;
            pattern = "111";
            String pattern_word = "";
            //setp1 count leading 1's
            int zero_count = 0;
            for (int i = 0; i< pattern.length(); i++){
                if (i == zero_count){
                    if (String.valueOf(pattern.charAt(i)).equals("1")) {
                        zero_count++;
                    }
                    else {
                        pattern_word = pattern_word + pattern.charAt(i);
                    }
                }
                else {
                    pattern_word = pattern_word + pattern.charAt(i);
                }
            }
            //pattern - initial pattern
            //pattern_word - everything expect leading 1's
            //zero_count - number of leading 1's
            int hex_length = 50 - (2*zero_count);
            int i = 0;
            //hex_length - length of hex representation of bigint of our range
            String hex_range_top,hex_range_bottom,hex_range_top_buf,hex_range_bottom_buf;
            hex_range_top = hex_range_bottom = hex_range_top_buf = hex_range_bottom_buf = "00"+Utils.bytesToHex(Base58.decode(pattern_word));
            while (hex_range_bottom_buf.length() <= 50 && hex_range_top_buf.length() <= 50 && i<= hex_length){
                hex_range_bottom = hex_range_bottom_buf;
                hex_range_top = hex_range_top_buf;
                hex_range_bottom_buf = hex_range_bottom_buf + "0";
                hex_range_top_buf = hex_range_top_buf + "f";
                i++;
            }

            Log.d("WORDS", String.valueOf(zero_count)+"   " +pattern.toString() + "  " + pattern_word.toString());
            Log.d("RANGES",String.valueOf(hex_length) + "   "+ hex_range_top + "  " + hex_range_bottom);
            //brilliant. calculate difficult
            byte[] range_bottom, range_top;
            range_bottom = Utils.hexStringToByteArray(hex_range_bottom);
            range_top = Utils.hexStringToByteArray(hex_range_top);
            BigDecimal bgn_top = new BigDecimal(new BigInteger(range_top));
            BigDecimal bgn_bottom = new BigDecimal(new BigInteger(range_bottom));
            BigDecimal bgn_range = bgn_top.subtract(bgn_bottom);
            Log.d("NUMBERS:", bgn_top.toString()+"   "+ bgn_bottom.toString());
            Log.d("SUBS", bgn_range.toString());
            BigDecimal bgn_dif = biggest_niumber.divide(bgn_range, 2, RoundingMode.HALF_UP);
            Log.d("BGN DIF", bgn_dif.toString());



            //test section
            String high = "1AAzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz";
            String low = "11111111111111111111111111111111AA";
            byte[] _low = Base58.decode(low);
            byte[] _high = Base58.decode(high);
            BigDecimal _bnlow = new BigDecimal(new BigInteger(_low));
            BigDecimal _bnhigh = new BigDecimal(new BigInteger(_high));

            Log.d("__TEST__ HLR",_bnlow.toString() + " " + _bnhigh.toString());
            bgn_dif = biggest_niumber.divide((_bnhigh.subtract(_bnlow)), 2, RoundingMode.HALF_UP);
            Log.d("__TEST__ DIF", bgn_dif.toString());

            //end of test section


       } catch (AddressFormatException e) {
            e.printStackTrace();
        }






    }


}
