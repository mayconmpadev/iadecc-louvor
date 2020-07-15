package net.eletroseg.iadecclouvor.util;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.eletroseg.iadecclouvor.modelo.Cronograma;
import net.eletroseg.iadecclouvor.modelo.Hino;

public class Servico extends Service {
Cronograma cronograma;

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
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "destroi", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private void fire(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child(Constantes.CRONOGRAMA);
        reference.keepSynced(true);

reference.addChildEventListener(new ChildEventListener() {
    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Toast.makeText(Servico.this, "onChildAdded", Toast.LENGTH_SHORT).show();
        cronograma = dataSnapshot.getValue(Cronograma.class);

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Toast.makeText(Servico.this, "onChildChanged", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        Toast.makeText(Servico.this, "onChildRemoved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Toast.makeText(Servico.this, "onChildMoved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Toast.makeText(Servico.this, "onCancelled", Toast.LENGTH_SHORT).show();
    }
});
reference.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (int i = 0; i < cronograma.vocal.size(); i++) {


        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});
    }
}
