package com.fatec.br.adocaopet.Common;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.fatec.br.adocaopet.R;

public class Notificacao {

    public void enviarNotificacao(Context context, int id, CharSequence titulo, CharSequence mensagem, Intent intent ) {

        PendingIntent pIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .setAutoCancel(true)
                .setContentIntent(pIntent)
                .setVibrate(new long[]{ 100, 250, 100, 500 })
                .setLights(100, 500, 100);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());

    }
}
