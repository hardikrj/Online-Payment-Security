package com.example.vipin.myfirstapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class second extends AppCompatActivity {
    TextView tv,tv2,tv3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        tv=(TextView)findViewById(R.id.textView5);
        tv2=(TextView)findViewById(R.id.textView2);
        //tv3=(TextView)findViewById(R.id.textView3);
        android.content.Intent i=getIntent();

        String plainText=i.getStringExtra("name")+" "+i.getStringExtra("card")+" "+i.getStringExtra("pin")+" "+i.getStringExtra("date")+" "+
                i.getStringExtra("CVV")+" "+i.getStringExtra("lat")+" "+i.getStringExtra("long");
        String seedValue="This is mySecure";

        tv.setText("PlainText: "+plainText+"\n");
        try {
            AESHandler a=new AESHandler(getApplicationContext());
            String encText = AESHandler.encrypt(seedValue,plainText);
            tv2.setText("EncryptedText: " + encText+"\n");
        //    String decText = AESHandler.decrypt(seedValue,encText);
        //    tv3.setText("DecryptedText: "+ decText);
        }
        catch(Exception e){

        }


    }

}

/*
* android.content.Intent i=getIntent();

        try {

        } catch (Exception e) {

        }

        tv=(TextView)findViewById(R.id.textView);
        tv2=(TextView)findViewById(R.id.textView2);
        tv3=(TextView)findViewById(R.id.textView3);

        tv.setText("Plain Text: "+plainText);
        tv2.setText("Encrypted Text: "+ encText);
        tv3.setText("Decrypted Text: "+decText);
* */