package com.mhmtrightname.yildiz_game1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity {
    private Button buttonBasla;
    private TextView textViewMehmetEminYildiz;
    private ImageView imageViewKarsilamaBalik;
    private AdView banner;
    private AdRequest adRequest;
    private Animation anim_mehmetemin, anim_oyunabasla, anim_girisbalik;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adRequest = new AdRequest.Builder().build();
        buttonBasla = findViewById(R.id.buttonBasla);
        textViewMehmetEminYildiz = findViewById(R.id.textViewMehmetEminYildiz);
        imageViewKarsilamaBalik = findViewById(R.id.imageViewKarsilamaBalik);
        banner = findViewById(R.id.banner);

        anim_oyunabasla = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_oyunabasla);
        anim_mehmetemin = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_mehmetemin);
        anim_girisbalik = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_girisbalik);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        banner = findViewById(R.id.banner);
        buttonBasla.setClickable(true);

        bannerYapici(adRequest);
        buttonBasla.setAnimation(anim_oyunabasla);
        textViewMehmetEminYildiz.setAnimation(anim_mehmetemin);
        imageViewKarsilamaBalik.setAnimation(anim_girisbalik);


        buttonBasla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonBasla.setClickable(false);
                startActivity(new Intent(MainActivity.this, OyunEkraniActivity.class));
                finish();
            }
        });
    }

    public void bannerYapici(AdRequest adRequest) {
        banner.loadAd(adRequest);

        banner.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() { // Reklam yüklenmesi tamamlanınca
                Log.e("-----AdMob: ", "onAdClosed()");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.e("-----AdMob: ", "onAdFailedToLoad()");
            }

            @Override
            public void onAdOpened() {
                Log.e("-----AdMob: ", "onAdOpened()");
            }

            @Override
            public void onAdLoaded() {
                Log.e("-----AdMob: ", "onAdLoaded()");
            }

            @Override
            public void onAdClicked() {
                Log.e("-----AdMob: ", "onAdClicked()");
            }
        });


    }


}