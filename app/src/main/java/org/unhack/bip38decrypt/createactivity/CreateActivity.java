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
    BigDecimal biggest_niumber;
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

    }

    public String getDif(String pattern) {
        if (pattern.isEmpty()){
            Log.d("getDif","Empty string");
            return null;
        }
        try {
            //Number of all possible addresses
            biggest_niumber = BigDecimal.valueOf(Math.pow(2,160));
            boolean zeroPrefix = false;
            BigDecimal pattern_prefix;
            String smallest,p_buf,s_buf;
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
                if (zero_count>=19){
                    //we can not have so much 1's in the pattern
                    throw new AddressFormatException();
                }
            }
            //pattern - initial pattern
            //pattern_word - everything expect leading 1's
            //zero_count - number of leading 1's
            int bin_len = 200 - (8*zero_count);
            int i = 0;
            byte[] zero = Utils.hexStringToByteArray("00");
            try {
                pattern_prefix = new BigDecimal(new BigInteger(Base58.decode(pattern_word)));
            }
            catch (Exception e){
                Log.d("Zero?","yup, Zero");
                pattern_prefix = new BigDecimal(BigInteger.valueOf(0));
                zeroPrefix = true;
            }
            BigDecimal top = new BigDecimal(new BigInteger(zero));
            BigDecimal bottom = new BigDecimal(new BigInteger(zero));
            BigDecimal buf_top = new BigDecimal(new BigInteger(zero));
            BigDecimal buf_bottom = new BigDecimal(new BigInteger(zero));
            BigDecimal buffer = new BigDecimal(new BigInteger(zero));
            if (zeroPrefix){
                buffer = BigDecimal.valueOf(Math.pow(2,bin_len));
            }
            else {
                //set pow to 1
                int n = 1;
                while (top.toBigInteger().bitLength() <= (bin_len + 8)) {
                    buf_bottom = bottom;
                    buf_top = top;
                    bottom = pattern_prefix.multiply(BigDecimal.valueOf(Math.pow(58, n)));
                    top = bottom.add((BigDecimal.valueOf(Math.pow(58, n)))).subtract(BigDecimal.valueOf(1));
                    Log.d("Numbers", bottom.toString() + " " + top.toString());
                    if (buf_bottom.toBigInteger().bitLength() > (bin_len - 8)
                            && buf_bottom.toBigInteger().bitLength() <= bin_len
                            && buf_top.toBigInteger().bitLength() <= bin_len) {
                        int compareBottom = buf_bottom.compareTo(BigDecimal.valueOf(Math.pow(2, 192)));
                        int compareTop = buf_top.compareTo(BigDecimal.valueOf(Math.pow(2, 192)));
                        if (compareTop == -1 && compareBottom == -1) {
                            buffer = buffer.add(buf_top).subtract(buf_bottom);
                        } else if (compareTop == 1 && compareBottom == -1) {
                            buffer = buffer.add(BigDecimal.valueOf(Math.pow(2, 192))).subtract(buf_bottom);
                        }
                    }
                    n = n + 1;
                }
            }
            Log.d("BUF TOP",buf_top.toString());
            buffer = buffer.divide(BigDecimal.valueOf(Math.pow(256,4)),1,BigDecimal.ROUND_UP);
            String res = biggest_niumber.divide(buffer,0,BigDecimal.ROUND_DOWN).toString();
            Log.d("DIFF", res);
            return res;
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
