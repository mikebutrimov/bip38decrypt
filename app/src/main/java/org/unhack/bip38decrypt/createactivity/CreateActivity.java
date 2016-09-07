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

import org.unhack.bip38decrypt.R;
import org.unhack.bip38decrypt.Utils;
import org.unhack.bip38decrypt.services.createService;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

import javax.annotation.ParametersAreNonnullByDefault;


public class CreateActivity extends AppCompatActivity {

    public static TextView textview4;
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
                startCreateIntent.putExtra("vanity","");
                startService(startCreateIntent);
            }
        });

    }


}
