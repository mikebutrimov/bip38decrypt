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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.bither.bitherj.crypto.ECKey;

import org.unhack.bip38decrypt.R;
import org.unhack.bip38decrypt.Utils;
import org.unhack.bip38decrypt.services.createService;


import java.security.SecureRandom;

public class CreateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Button lesButton = (Button) findViewById(R.id.button);
        TextView textView_performance = (TextView) findViewById(R.id.textView_performance);
        final int cores = Runtime.getRuntime().availableProcessors();
        long test_result = speedTest();
        double speed = (100.0/test_result)*1000*cores;

        textView_performance.setText( String.format("Cores available: "+String.valueOf(cores)+ "\nAddresses per second: %.2f",speed));
        final TextView textView_difficulty = (TextView) findViewById(R.id.textView_calculatedDifficulty);
        final TextView textView_addresswillbelike = (TextView) findViewById(R.id.textView_addresswillbelike);
        final EditText editText_vanity = (EditText) findViewById(R.id.editText_vanity);

        editText_vanity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pattern = editText_vanity.getText().toString();
                pattern = "1" + pattern;
                String diff = Utils.getDif(pattern);
                textView_difficulty.setText(diff);
                textView_addresswillbelike.setText(getText(R.string.address_will_be) + pattern);
            }


            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        lesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startCreateIntent = new Intent(getApplicationContext(), createService.class);
                startCreateIntent.putExtra("vanity","13");
                startService(startCreateIntent);
            }
        });
    }


    long speedTest(){
        long test_begin = System.currentTimeMillis();
        ECKey key;
        SecureRandom rnd = new SecureRandom();
        for (int i = 0; i< 100; i++){
            key = ECKey.generateECKey(rnd);
        }
        long test_end = System.currentTimeMillis();
        Log.d("Delta T", String.valueOf(test_end - test_begin));
        return (test_end - test_begin);
    }

}
