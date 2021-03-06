package com.example.local192.tp1_nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLogTags;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {

    IntentFilter[] filters;
    String[][] techs;
    PendingIntent pendingIntent;
    NfcAdapter adapter;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pendingIntent = PendingIntent.getActivity(
                this,0,new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        );
        IntentFilter mifare = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        filters = new IntentFilter[] {mifare};
        techs = new String[][] {new String[] {NfcA.class.getName()}};
        adapter = NfcAdapter.getDefaultAdapter(this);

        imageView = (ImageView)findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotation);
                imageView.startAnimation(rotation);
            }
        });
    }

    public void onResume(){
        super.onResume();
        Animation Animation = AnimationUtils.loadAnimation(this, R.anim.shake);

        imageView.startAnimation(Animation);

        adapter.enableForegroundDispatch(this, pendingIntent, filters, techs);
    }

    public void onPause(){
        super.onPause();
        adapter.disableForegroundDispatch(this);
    }

    public void onNewIntent(Intent intent){
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] id = tag.getId();
        ByteBuffer wrapped = ByteBuffer.wrap(id);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        int signedInt = wrapped.getInt();
        long number = signedInt & 0xfffffff1;
        Evt(number);

    }

    public void Evt(long number){
        Animation Animation = AnimationUtils.loadAnimation(this, R.anim.shake);


        imageView.startAnimation(Animation);
        Log.i("Number","num nfc : " + number);
        if (number == 710374656)startActivity(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
    }
}
