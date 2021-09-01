package com.mhmtrightname.yildiz_game1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.Timer;
import java.util.TimerTask;

public class OyunEkraniActivity extends AppCompatActivity {
    private ConstraintLayout cl;
    private ImageView imageViewAnaKarakter, imageViewBomba, imageViewCan, imageViewBalik1, imageViewBalik2, imageViewKirikKalp;
    private ImageView imageViewBalik3, imageViewBalik4, imageViewBalik5, imageViewKilicBaligi;
    private TextView textViewHazirsanTikla, textViewSkor, textViewKalanCan;
    private int kalan_can = 5;
    private Button buttonOynamayaDevamEt, buttonOyunuBitir;
    private int gelen_skor;
    private int gelen_can;
    private RewardedAd myRewardedAd;
    private final String TAG = "VİDEO_REKLAM";
    private Animation anim_hazirsan, anim_oynamayadevamet;
    private int gonderilecek_can = 0;
    MediaPlayer mp_balikYemek, mp_bomba, mp_kalpKazan, mp_kilicbaligi;

    // Pozisyonlar:
    private int anaKarakterX, anaKarakterY;
    private int bombaX, bombaY;
    private int canX, canY;
    private int balik1X, balik1Y;
    private int balik2X, balik2Y;
    private int balik3X, balik3Y;
    private int balik4X, balik4Y;
    private int balik5X, balik5Y;
    private int kilicBaligiX, kilicBaligiY;

    // Hızlar:
    private int anaKarakterHiz;
    private int bombaHiz;
    private int canHiz;
    private int balik1Hiz;
    private int balik2Hiz;
    private int balik3Hiz;
    private int balik4Hiz;
    private int balik5Hiz;
    private int kilicBaligiHiz;


    // Kontroller:
    private boolean dokunmaKontrol = false;
    private boolean baslangicKontrol = false;

    private int skor;

    // Boyutlar:
    private int ekranGenisligi;
    private int ekranYuksekligi;
    private int anaKarakterGenisligi;
    private int anaKarakterYuksekligi;
    AdRequest adRequest;


    private Timer timer = new Timer();
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oyun_ekrani);
        anim_hazirsan = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_hazirsan);
        anim_oynamayadevamet = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_oynamayadevamet);


        gelen_can = getIntent().getIntExtra("kalan_can", 3);
        gelen_skor = getIntent().getIntExtra("son_skor", 0);
        Log.e("İntent gelenler : ", gelen_can + " ve " + gelen_skor);
        adRequest = new AdRequest.Builder().build();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        kalan_can = gelen_can;
        skor = gelen_skor;


        cl = findViewById(R.id.cl);
        imageViewAnaKarakter = findViewById(R.id.imageViewAnaKarakter);
        imageViewBomba = findViewById(R.id.imageViewBomba);
        imageViewCan = findViewById(R.id.imageViewCan);
        imageViewBalik1 = findViewById(R.id.imageViewBalik1);
        imageViewBalik2 = findViewById(R.id.imageViewBalik2);
        imageViewBalik3 = findViewById(R.id.imageViewBalik3);
        imageViewBalik4 = findViewById(R.id.imageViewBalik4);
        imageViewBalik5 = findViewById(R.id.imageViewBalik5);
        imageViewKilicBaligi = findViewById(R.id.imageViewKilicBaligi);

        textViewHazirsanTikla = findViewById(R.id.textViewHazirsanTikla);
        textViewSkor = findViewById(R.id.textViewSkor);
        textViewKalanCan = findViewById(R.id.textViewKalanCan);
        buttonOynamayaDevamEt = findViewById(R.id.buttonOynamayaDevamEt);
        buttonOyunuBitir = findViewById(R.id.buttonOyunuBitir);
        imageViewKirikKalp = findViewById(R.id.imageViewKirikKalp);

        // Sesler:
        mp_balikYemek = MediaPlayer.create(this, R.raw.balikyemek);
        mp_bomba = MediaPlayer.create(this, R.raw.bomba2);
        mp_kalpKazan = MediaPlayer.create(this, R.raw.kalpkazanmak);
        mp_kilicbaligi = MediaPlayer.create(this, R.raw.kilicbaligi);

        textViewKalanCan.setText(String.valueOf(kalan_can));
        textViewSkor.setText(String.valueOf(skor));
        buttonOynamayaDevamEt.setVisibility(View.INVISIBLE);
        buttonOyunuBitir.setVisibility(View.INVISIBLE);
        imageViewKirikKalp.setVisibility(View.INVISIBLE);

        textViewHazirsanTikla.setAnimation(anim_hazirsan);

        cisimleriBaslangictaDisariCikar();

        cl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textViewHazirsanTikla.setVisibility(View.INVISIBLE);

                if (baslangicKontrol) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) { // ekrana basınca
                        //Log.e("MotionEvent", "Ekrana Dokunuldu");
                        dokunmaKontrol = true;
                    }

                    if (event.getAction() == MotionEvent.ACTION_UP) { // parmağı ekrandan çekince
                        //Log.e("MotionEvent", "Ekranı bıraktı");
                        dokunmaKontrol = false;
                    }

                } else {
                    baslangicKontrol = true;

                    anaKarakterX = (int) imageViewAnaKarakter.getX();
                    anaKarakterY = (int) imageViewAnaKarakter.getY();

                    anaKarakterGenisligi = imageViewAnaKarakter.getWidth();
                    anaKarakterYuksekligi = imageViewAnaKarakter.getHeight();
                    ekranGenisligi = cl.getWidth();
                    ekranYuksekligi = cl.getHeight();


                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    anaKarakterHareketEttirme();
                                    cisimleriHareketEttir();
                                    carpismaKontrol();
                                    canKontrol();
                                }
                            });

                        }
                    }, 0, 20); // delay: gecikme, period: belli aralıkla çalışmasını istiyoruz


                }


                return true;
            }
        });


    }

    private void cisimleriBaslangictaDisariCikar() {
        imageViewBomba.setX(-500);
        imageViewBomba.setY(0);
        imageViewCan.setX(-500);
        imageViewCan.setY(0);
        imageViewBalik1.setX(-500);
        imageViewBalik1.setY(0);
        imageViewBalik2.setX(-500);
        imageViewBalik2.setY(0);
        imageViewBalik3.setX(-500);
        imageViewBalik3.setY(0);
        imageViewBalik4.setX(-500);
        imageViewBalik4.setY(0);
        imageViewBalik5.setX(-500);
        imageViewBalik5.setY(0);
        imageViewKilicBaligi.setX(-500);
        imageViewKilicBaligi.setY(0);
    }

    private void anaKarakterHareketEttirme() {
        anaKarakterHiz = Math.round(ekranYuksekligi / 50);

        if (dokunmaKontrol) {
            anaKarakterY -= anaKarakterHiz;
        } else {
            anaKarakterY += anaKarakterHiz;
        }
        if (anaKarakterY <= -100) {
            anaKarakterY = -100; // eğer ekranın en üst kısmına ulaştıysa daha fazla ileri gitmesin
        }
        if (anaKarakterY >= ekranYuksekligi - anaKarakterYuksekligi + 100) {
            anaKarakterY = ekranYuksekligi - anaKarakterYuksekligi + 100;
            // eğer ekranın en alt kısmına ulaştıysa daha fazla ileri gitmesin.
            // fakat burada ana karakterin alt kısmı ulaştıysa ilerlemeyecektir
        }
        imageViewAnaKarakter.setY(anaKarakterY);
    }

    private void cisimleriHareketEttir() {
        // round metodu sayıyı yuvarlar.
        balik1Hiz = Math.round(ekranGenisligi / 50);
        balik2Hiz = Math.round(ekranGenisligi / 50);
        balik3Hiz = Math.round(ekranGenisligi / 50);
        balik4Hiz = Math.round(ekranGenisligi / 50);
        balik5Hiz = Math.round(ekranGenisligi / 50);
        kilicBaligiHiz = Math.round(ekranGenisligi / 30);
        bombaHiz = Math.round(ekranGenisligi / 30);
        canHiz = Math.round(ekranGenisligi / 70);

        int hizArtisi = hizArtir();

        balik1Hiz += hizArtisi;
        balik2Hiz += hizArtisi;
        balik3Hiz += hizArtisi;
        balik4Hiz += hizArtisi;
        balik5Hiz += hizArtisi;
        kilicBaligiHiz += hizArtisi;
        bombaHiz += hizArtisi;
        canHiz += hizArtisi;
        Log.e("Can Hız : ", String.valueOf(canHiz));

        balik1X -= balik1Hiz;
        balik2X -= balik2Hiz;
        balik3X -= balik3Hiz;
        balik4X -= balik4Hiz;
        balik5X -= balik5Hiz;
        kilicBaligiX -= kilicBaligiHiz;
        bombaX -= bombaHiz;
        canX -= canHiz;


        if (balik1X < -200) {
            balik1X = ekranGenisligi + 500;
            balik1Y = (int) Math.floor(Math.random() * ekranYuksekligi);
        }
        imageViewBalik1.setX(balik1X);
        imageViewBalik1.setY(balik1Y);
        //
        if (balik2X < -200) {
            balik2X = ekranGenisligi + 1200;
            balik2Y = (int) Math.floor(Math.random() * ekranYuksekligi);
        }
        imageViewBalik2.setX(balik2X);
        imageViewBalik2.setY(balik2Y);
        //
        if (balik3X < -200) {
            balik3X = ekranGenisligi + 3000;
            balik3Y = (int) Math.floor(Math.random() * ekranYuksekligi);
        }
        imageViewBalik3.setX(balik3X);
        imageViewBalik3.setY(balik3Y);
        //
        if (balik4X < -200) {
            balik4X = ekranGenisligi + 2200;
            balik4Y = (int) Math.floor(Math.random() * ekranYuksekligi);
        }
        imageViewBalik4.setX(balik4X);
        imageViewBalik4.setY(balik4Y);
        //
        if (balik5X < -200) {
            balik5X = ekranGenisligi + 4000;
            balik5Y = (int) Math.floor(Math.random() * ekranYuksekligi);
        }
        imageViewBalik5.setX(balik5X);
        imageViewBalik5.setY(balik5Y);
        //
        if (kilicBaligiX < -200) {
            kilicBaligiX = ekranGenisligi + 6000;
            kilicBaligiY = (int) Math.floor(Math.random() * ekranYuksekligi);
        }
        imageViewKilicBaligi.setX(kilicBaligiX);
        imageViewKilicBaligi.setY(kilicBaligiY);
        //
        if (bombaX < -200) {
            bombaX = ekranGenisligi + 4500;
            bombaY = (int) Math.floor(Math.random() * ekranYuksekligi);
        }
        imageViewBomba.setX(bombaX);
        imageViewBomba.setY(bombaY);
        //
        if (canX < -200) {
            canX = ekranGenisligi + 18000;
            canY = (int) Math.floor(Math.random() * ekranYuksekligi);
        }
        imageViewCan.setX(canX);
        imageViewCan.setY(canY);
    }

    private int hizArtir() {
        return (int) skor / 200;

    }

    private void carpismaKontrol() {
        int balik1MerkezX = balik1X + imageViewBalik1.getWidth() / 2;
        int balik1MerkezY = balik1Y + imageViewBalik1.getHeight() / 2;

        int balik2MerkezX = balik2X + imageViewBalik2.getWidth() / 2;
        int balik2MerkezY = balik2Y + imageViewBalik2.getHeight() / 2;

        int balik3MerkezX = balik3X + imageViewBalik3.getWidth() / 2;
        int balik3MerkezY = balik3Y + imageViewBalik3.getHeight() / 2;

        int balik4MerkezX = balik4X + imageViewBalik4.getWidth() / 2;
        int balik4MerkezY = balik4Y + imageViewBalik4.getHeight() / 2;

        int balik5MerkezX = balik5X + imageViewBalik5.getWidth() / 2;
        int balik5MerkezY = balik5Y + imageViewBalik5.getHeight() / 2;

        int kilicBaligiMerkezX = kilicBaligiX + imageViewKilicBaligi.getWidth() / 2;
        int kilicBaligiMerkezY = kilicBaligiY + imageViewKilicBaligi.getHeight() / 2;

        int canMerkezX = canX + imageViewCan.getWidth() / 2;
        int can2MerkezY = canY + imageViewCan.getHeight() / 2;

        int bombaMerkezX = bombaX + imageViewBomba.getWidth() / 2;
        int bombaMerkezY = bombaY + imageViewBomba.getHeight() / 2;


        if (0 <= balik1MerkezX && balik1MerkezX <= anaKarakterGenisligi && anaKarakterY <= balik1MerkezY
                && balik1MerkezY <= anaKarakterY + anaKarakterYuksekligi) {
            skor += 10;
            balik1X = -5000; // çarpışma olduğunda sarı daire ekrandan kaybolsun istiyoruz anında...
            balikYemeSesi();
        }

        if (0 <= balik2MerkezX && balik2MerkezX <= anaKarakterGenisligi && anaKarakterY <= balik2MerkezY
                && balik2MerkezY <= anaKarakterY + anaKarakterYuksekligi) {
            skor += 5;
            balik2X = -5000; // çarpışma olduğunda sarı daire ekrandan kaybolsun istiyoruz anında...
            balikYemeSesi();
        }

        if (0 <= balik3MerkezX && balik3MerkezX <= anaKarakterGenisligi && anaKarakterY <= balik3MerkezY
                && balik3MerkezY <= anaKarakterY + anaKarakterYuksekligi) {
            skor += 20;
            balik3X = -5000; // çarpışma olduğunda sarı daire ekrandan kaybolsun istiyoruz anında...
            balikYemeSesi();
        }

        if (0 <= balik4MerkezX && balik4MerkezX <= anaKarakterGenisligi && anaKarakterY <= balik4MerkezY
                && balik4MerkezY <= anaKarakterY + anaKarakterYuksekligi) {
            skor += 10;
            balik4X = -5000; // çarpışma olduğunda sarı daire ekrandan kaybolsun istiyoruz anında...
            balikYemeSesi();
        }

        if (0 <= balik5MerkezX && balik5MerkezX <= anaKarakterGenisligi && anaKarakterY <= balik5MerkezY
                && balik5MerkezY <= anaKarakterY + anaKarakterYuksekligi) {
            skor += 50;
            balik5X = -5000; // çarpışma olduğunda sarı daire ekrandan kaybolsun istiyoruz anında...
            balikYemeSesi();
        }

        if (0 <= canMerkezX && canMerkezX <= anaKarakterGenisligi && anaKarakterY <= can2MerkezY
                && can2MerkezY <= anaKarakterY + anaKarakterYuksekligi) {
            kalan_can += 1;
            canX = -5000; // çarpışma olduğunda sarı daire ekrandan kaybolsun istiyoruz anında...
            kalpkazanmaSesi();
        }

        if (0 <= bombaMerkezX && bombaMerkezX <= anaKarakterGenisligi && anaKarakterY <= bombaMerkezY
                && bombaMerkezY <= anaKarakterY + anaKarakterYuksekligi) {

            bombaX = -5000; // çarpışma olduğunda bomba ekrandan kaybolsun istiyoruz anında...
            kalan_can -= 1;
            bombaSesi();
        }

        if (0 <= kilicBaligiMerkezX && kilicBaligiMerkezX <= anaKarakterGenisligi && anaKarakterY <= kilicBaligiMerkezY
                && kilicBaligiMerkezY <= anaKarakterY + anaKarakterYuksekligi) {

            kilicBaligiX = -5000; // çarpışma olduğunda bomba ekrandan kaybolsun istiyoruz anında...
            kalan_can -= 1;
            kilicBaligiSesi();
        }

        textViewSkor.setText(String.valueOf(skor));
    }

    private void canKontrol() {
        textViewKalanCan.setText(String.valueOf(kalan_can));
        if (kalan_can <= 0) {
            imageViewKirikKalp.setVisibility(View.VISIBLE);
            cisimleriBaslangictaDisariCikar();

            timer.cancel();
            timer = null;
            buttonOynamayaDevamEt.setVisibility(View.VISIBLE);
            buttonOyunuBitir.setVisibility(View.VISIBLE);
            buttonOynamayaDevamEt.setAnimation(anim_oynamayadevamet);

            //Log.e("Son Can ve Skor : ", "Can : " + kalan_can + "  Skor : " + skor);
            rewardedAdYukleyici(adRequest);
            //buttonOynamayaDevamEt.setClickable(true);


            buttonOynamayaDevamEt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //buttonOynamayaDevamEt.setClickable(false);
                    // Burada reklam gösterilecek (videolu
                    rewardedAdGoster();


                }
            });
            buttonOyunuBitir.setClickable(true);

            buttonOyunuBitir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonOyunuBitir.setClickable(false);


                    // Burada da bir Tam Ekran kaplayan geçiş reklamı koyalım. (Interstitial Ads)

                    Intent intent = new Intent(OyunEkraniActivity.this, SonucEkraniActivity.class);
                    intent.putExtra("sonuc_skor", skor);
                    startActivity(intent);
                    finish();

                }
            });


            //Intent intent = new Intent(OyunEkraniActivity.this, SonucEkraniActivity.class);
            //startActivity(intent);
        }
    }

    // reklam tamamen izlenince can 3 yapılmalı
    private void rewardedAdGoster() {
        if (myRewardedAd != null) {
            myRewardedAd.show(this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) { // ödül alındığında:
                    Log.e(TAG, "Ödül Alındı!");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    gonderilecek_can = 3;
                }
            });
        } else {
            Log.e(TAG, "Ödüllü Reklam henüz hazır değil");
            Toast.makeText(getApplicationContext(), "Ödüllü Reklam henüz hazır değil", Toast.LENGTH_SHORT).show();
        }

    }

    private void rewardedAdYukleyici(AdRequest adRequest) {
        RewardedAd.load(this, "ca-app-pub-8319014340387969/1377779556",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.e(TAG, loadAdError.getMessage());
                        myRewardedAd = null;
                        Log.e(TAG, "Reklam yükleme hatası!");
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        myRewardedAd = rewardedAd;
                        Log.e(TAG, "Reklam Yüklendi");

                        myRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.e(TAG, "Video Reklam Gösterildi");
                                myRewardedAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(TAG, "Reklam Gösterilemedi");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Don't forget to set the ad reference to null so you
                                // don't show the ad a second time.
                                Log.e(TAG, "Reklam Kapatıldı");
                                int son_skor = Integer.parseInt(textViewSkor.getText().toString());


                                Intent intent = new Intent(OyunEkraniActivity.this, OyunEkraniActivity.class);
                                intent.putExtra("kalan_can", gonderilecek_can);
                                intent.putExtra("son_skor", son_skor);
                                finish();
                                startActivity(intent);
                                //buttonOynamayaDevamEt.setClickable(true);
                            }


                        });
                    }
                });
    }
    private void balikYemeSesi(){
        try {
            if (mp_balikYemek.isPlaying()) {
                mp_balikYemek.stop();
                mp_balikYemek.release();
                mp_balikYemek = MediaPlayer.create(getApplicationContext(), R.raw.balikyemek);
            } mp_balikYemek.start();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void bombaSesi() {
        try {
            if (mp_bomba.isPlaying()) {
                mp_bomba.stop();
                mp_bomba.release();
                mp_bomba = MediaPlayer.create(getApplicationContext(), R.raw.bomba2);
            } mp_bomba.start();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void kilicBaligiSesi() {
        try {
            if (mp_kilicbaligi.isPlaying()) {
                mp_kilicbaligi.stop();
                mp_kilicbaligi.release();
                mp_kilicbaligi = MediaPlayer.create(getApplicationContext(), R.raw.kilicbaligi);
            } mp_kilicbaligi.start();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void kalpkazanmaSesi() {
        try {
            if (mp_kalpKazan.isPlaying()) {
                mp_kalpKazan.stop();
                mp_kalpKazan.release();
                mp_kalpKazan = MediaPlayer.create(getApplicationContext(), R.raw.kalpkazanmak);
            } mp_kalpKazan.start();

        }catch (Exception e){
            e.printStackTrace();
        }
    }





}