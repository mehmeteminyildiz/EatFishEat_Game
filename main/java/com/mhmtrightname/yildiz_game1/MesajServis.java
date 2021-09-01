package com.mhmtrightname.yildiz_game1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MesajServis extends FirebaseMessagingService {
    private NotificationCompat.Builder builder;


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("Başlık : ", remoteMessage.getNotification().getTitle());
        Log.e("İçerik : ", remoteMessage.getNotification().getBody());

        durumaBagli(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());

    }

    public void durumaBagli(String baslik, String icerik){

        NotificationManager bildirimYoneticisi = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Servisimi çalıştırabilecek bir yapı oldu üstteki satır ile.
        // Şimdi; bildirime tıklanınca nereye gitmesini istediğimizi yazalım:
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent gidilecekIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Pending içinde intent yazdığımız yer: gidilecek yer demektir...

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){ // Oreo sürümü ise burası çalışacak
            String kanalId = "kanalId"; // bildirimin küme halinde olmasını sağlıyor.
            String kanalAd = "kanalAd";
            String kanalTanım = "kanalTanım";
            int kanalOnceligi = NotificationManager.IMPORTANCE_HIGH; // bildirim öncelik düzeyi

            NotificationChannel kanal = bildirimYoneticisi.getNotificationChannel(kanalId);

            if (kanal == null){ // kanal yeni oluşturulmuşsa:
                kanal = new NotificationChannel(kanalId, kanalAd, kanalOnceligi);
                kanal.setDescription(kanalTanım); // Description : "Açıklama" demektir.
                bildirimYoneticisi.createNotificationChannel(kanal);
            }

            builder = new NotificationCompat.Builder(this, kanalId);

            builder.setContentTitle(baslik);
            builder.setContentText(icerik);
            builder.setSmallIcon(R.drawable.kalp1);
            builder.setAutoCancel(true);
            builder.setContentIntent(gidilecekIntent);

        }else { // Oreo'dan başka bir sürüm ise burası çalışacak.

            builder = new NotificationCompat.Builder(this);

            builder.setContentTitle(baslik);
            builder.setContentText(icerik);
            builder.setSmallIcon(R.drawable.kalp2);
            builder.setAutoCancel(true);
            builder.setContentIntent(gidilecekIntent);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        bildirimYoneticisi.notify(1, builder.build()); // artık bildir diyoruz.



    }


}
