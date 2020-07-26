package net.eletroseg.iadecclouvor.util;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.activity.LoginActivity;
import net.eletroseg.iadecclouvor.activity.MainActivity;
import net.eletroseg.iadecclouvor.modelo.Cronograma;
import net.eletroseg.iadecclouvor.modelo.Hino;

import java.util.ArrayList;

import static net.eletroseg.iadecclouvor.util.App.CHANNEL_ID;

public class Servico extends Service {
ArrayList<Cronograma> cronograma = new ArrayList<>();
    SPM spm = new SPM(this);
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        fire();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
     //   Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
     //   Toast.makeText(this, "destroi", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private void fire(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child(Constantes.CRONOGRAMA);
        reference.keepSynced(true);

reference.addChildEventListener(new ChildEventListener() {
    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
       // Toast.makeText(Servico.this, "onChildAdded", Toast.LENGTH_SHORT).show();
        Cronograma cro = dataSnapshot.getValue(Cronograma.class);
        cronograma.add(cro);

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        //Toast.makeText(Servico.this, spm.getPreferencia("USUARIO_LOGADO","USUARIO", ""), Toast.LENGTH_SHORT).show();

        for (int i = 0; i < cronograma.get(1).vocal.size(); i++) {
            if (cronograma.get(1).vocal.get(i).id.equals(spm.getPreferencia("USUARIO_LOGADO","USUARIO", ""))){

                createNotificationChannel();
                addNotification("Escala", cronograma.get(1).vocal.get(i).nome + ", você esta na escala da EBD", 1);
                break;

            }
            if (cronograma.get(1).instrumental.get(i).id.equals(spm.getPreferencia("USUARIO_LOGADO","USUARIO", ""))){

                createNotificationChannel();
                addNotification("Escala", cronograma.get(1).instrumental.get(i).nome + ", você esta na escala da EBD", 1);
                break;

            }

            if (cronograma.get(1).ministrante.id.equals(spm.getPreferencia("USUARIO_LOGADO","USUARIO", ""))){

                createNotificationChannel();
                addNotification("Escala", cronograma.get(1).ministrante.nome + ", você esta na escala da EBD, e " +
                        "é o Ministrante", 1);
                break;

            }
        }

        for (int i = 0; i < cronograma.get(0).vocal.size(); i++) {
            if (cronograma.get(0).vocal.get(i).id.equals(spm.getPreferencia("USUARIO_LOGADO","USUARIO", ""))){

                createNotificationChannel();
                addNotification("Escala", cronograma.get(0).vocal.get(i).nome + ", você esta na escala de domingo", 0);
                break;

            }
            if (cronograma.get(0).instrumental.get(i).id.equals(spm.getPreferencia("USUARIO_LOGADO","USUARIO", ""))){

                createNotificationChannel();
                addNotification("Escala", cronograma.get(0).instrumental.get(i).nome + ", você esta na escala de domingo", 0);
                break;

            }

            if (cronograma.get(0).ministrante.id.equals(spm.getPreferencia("USUARIO_LOGADO","USUARIO", ""))){

                createNotificationChannel();
                addNotification("Escala", cronograma.get(0).ministrante.nome + ", você esta na escala de domingo, e " +
                        "é o Ministrante", 0);
                break;

            }
        }

        for (int i = 0; i < cronograma.get(3).vocal.size(); i++) {
            if (cronograma.get(3).vocal.get(i).id.equals(spm.getPreferencia("USUARIO_LOGADO","USUARIO", ""))){

                createNotificationChannel();
                addNotification("Escala", cronograma.get(3).vocal.get(i).nome + ", você esta na escala de quarta", 3);
                break;

            }

            if (cronograma.get(3).instrumental.get(i).id.equals(spm.getPreferencia("USUARIO_LOGADO","USUARIO", ""))){

                createNotificationChannel();
                addNotification("Escala", cronograma.get(3).instrumental.get(i).nome + ", você esta na escala de quarta", 3);
                break;

            }

            if (cronograma.get(3).ministrante.id.equals(spm.getPreferencia("USUARIO_LOGADO","USUARIO", ""))){

                createNotificationChannel();
                addNotification("Escala", cronograma.get(3).ministrante.nome + ", você esta na escala de quarta, " +
                        "e é o Ministrante", 3);
                break;

            }
        }

        for (int i = 0; i < cronograma.get(2).vocal.size(); i++) {
            if (cronograma.get(2).vocal.get(i).id.equals(spm.getPreferencia("USUARIO_LOGADO","USUARIO", ""))){

                createNotificationChannel();
                addNotification("Escala", cronograma.get(2).vocal.get(i).nome + ", você esta na escala do especial", 2);
                break;

            }

            if (cronograma.get(2).instrumental.get(i).id.equals(spm.getPreferencia("USUARIO_LOGADO","USUARIO", ""))){

                createNotificationChannel();
                addNotification("Escala", cronograma.get(2).instrumental.get(i).nome + ", você esta na escala do especial", 2);
                break;

            }

            if (cronograma.get(2).ministrante.id.equals(spm.getPreferencia("USUARIO_LOGADO","USUARIO", ""))){

                createNotificationChannel();
                addNotification("Escala", cronograma.get(2).ministrante.nome + ", você esta na escala do especial, e " +
                        "é o Ministrante", 2);
                break;

            }
        }


    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});
reference.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});
    }

    public void addNotification(String titulo, String msg, int numero) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.redondo)
                .setContentTitle(titulo)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);




        // Add as notification
        // NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // manager.notify(0, builder.build());

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
// notificationId is a unique int for each notification that you must define

        notificationManager.notify(numero, builder.build());
    }

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.notification_activity);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
