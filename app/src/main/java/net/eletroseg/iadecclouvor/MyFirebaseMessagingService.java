package net.eletroseg.iadecclouvor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.eletroseg.iadecclouvor.activity.MainActivity;
import net.eletroseg.iadecclouvor.util.ConfiguracaoFiribase;
import net.eletroseg.iadecclouvor.util.Constantes;
import net.eletroseg.iadecclouvor.util.InstanciaFirebase;
import net.eletroseg.iadecclouvor.util.SPM;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage notificacao) {
        if( notificacao.getNotification() != null ) {
            String titulo = notificacao.getNotification().getTitle();
            String corpo = notificacao.getNotification().getBody();

            enviarNotificacao(titulo, corpo);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        SPM spm = new SPM(this);
        DatabaseReference databaseReference = InstanciaFirebase.getDatabase().getReference().child(Constantes.USUARIOS).child(spm.getPreferencia("USUARIO_LOGADO", "USUARIO","")).child("token");
        databaseReference.setValue(s);
    }

    private void enviarNotificacao(String titulo, String corpo){

        //Configuraçõe para notificação
        String canal = getString(R.string.default_notification_channel_id);
        Uri uriSom = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION );
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Criar notificação
        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(this, canal)
                .setContentTitle( titulo )
                .setContentText( corpo )
                .setSmallIcon( R.drawable.ic_audiotrack_black_24dp )
                .setSound( uriSom )
                .setAutoCancel( true )
                .setContentIntent( pendingIntent );

        //Recupera notificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Verifica versão do Android a partir do Oreo para configurar canal de notificação
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            NotificationChannel channel = new NotificationChannel(canal, "canal", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel( channel );
        }

        //Envia notificação
        notificationManager.notify(0, notificacao.build() );

    }
}
