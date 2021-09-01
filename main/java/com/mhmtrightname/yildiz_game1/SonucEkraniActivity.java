package com.mhmtrightname.yildiz_game1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class SonucEkraniActivity extends AppCompatActivity {
    private Button buttonTekrarOyna;
    private TextView textViewSonucSkor, textViewSonucHighSkor, textViewNewHighSkor;
    private int gelen_skor;
    private ImageView imageViewSonucOluBalik;
    private InterstitialAd myInterstitialAd;
    private AdRequest adRequest;
    private Animation anim_olmusbalik, anim_tekraroyna;
    MediaPlayer mp_kaybet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sonuc_ekrani);
        adRequest = new AdRequest.Builder().build();

        anim_olmusbalik = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_olmusbalik);
        anim_tekraroyna = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_tekraroyna);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        interstitialYapici(adRequest);


        buttonTekrarOyna = findViewById(R.id.buttonTekrarOyna);
        textViewSonucSkor = findViewById(R.id.textViewSonucSkor);
        textViewSonucHighSkor = findViewById(R.id.textViewSonucHighSkor);
        textViewNewHighSkor = findViewById(R.id.textViewNewHighSkor);
        imageViewSonucOluBalik = findViewById(R.id.imageViewSonucOluBalik);

        mp_kaybet = MediaPlayer.create(this, R.raw.kaybetmek);


        gelen_skor = getIntent().getIntExtra("sonuc_skor", 0); // OyunEkranından geliyor.

        SharedPreferences sp = getSharedPreferences("Sonuc", Context.MODE_PRIVATE);
        int highSkor = sp.getInt("highSkor", 0); // DB'den geliyor.

        if (gelen_skor > highSkor) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("highSkor", gelen_skor);
            editor.commit();

            textViewSonucHighSkor.setText(String.valueOf(gelen_skor));
            textViewSonucSkor.setText(String.valueOf(gelen_skor));
            textViewNewHighSkor.setVisibility(View.VISIBLE);

        } else {
            textViewSonucSkor.setText(String.valueOf(gelen_skor));
            textViewSonucHighSkor.setText(String.valueOf(highSkor));

        }
        kaybetmeSesi();

        buttonTekrarOyna.setAnimation(anim_tekraroyna);
        imageViewSonucOluBalik.setAnimation(anim_olmusbalik);

        buttonTekrarOyna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myInterstitialAd != null) {
                    myInterstitialAd.show(SonucEkraniActivity.this);

                } else {
                    Log.e("TAG", "Interstitial Reklam henüz hazır değil");
                    Intent intent = new Intent(SonucEkraniActivity.this, OyunEkraniActivity.class);
                    finish();
                    startActivity(intent);

                }
            }
        });


    }

    private void interstitialYapici(AdRequest adRequest) {
        InterstitialAd.load(this, "ca-app-pub-8319014340387969/1920705036", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                myInterstitialAd = interstitialAd;
                Log.e("-----Interstital: ", "onAdLoaded");

                myInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        Log.e("-----Interstital: ", "onAdFailedToShowFullScreenContent, gösterme hatası...");

                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.e("-----Interstital: ", "onAdShowedFullScreenContent, Tam ekranda gösterildi!");
                        myInterstitialAd = null;
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Log.e("-----Interstital: ", "onAdDismissedFullScreenContent, tam ekran içerik kapatıldı ");
                        Intent intent = new Intent(SonucEkraniActivity.this, OyunEkraniActivity.class);
                        finish();
                        startActivity(intent);


                    }
                });

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.e("-----Interstital: ", "onAdFailedToLoad");
                myInterstitialAd = null;
                /*Intent intent = new Intent(SonucEkraniActivity.this, OyunEkraniActivity.class);
                finish();
                startActivity(intent);*/
            }
        });
    }
    private void kaybetmeSesi() {
        try {
            if (mp_kaybet.isPlaying()) {
                mp_kaybet.stop();
                mp_kaybet.release();
                mp_kaybet = MediaPlayer.create(getApplicationContext(), R.raw.kaybetmek);
            } mp_kaybet.start();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}