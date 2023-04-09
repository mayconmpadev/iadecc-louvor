package net.eletroseg.iadecclouvor.activity;



import static net.eletroseg.iadecclouvor.util.Parametro.img;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.util.Progresso;


public class EscalaMesActivity extends AppCompatActivity {
    private FloatingActionButton floatingActionButton;
    private TouchImageView imageView;
    private TextView info;
    boolean bBotao = false;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String fotoGaleria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_escala_mes);

        floatingActionButton = findViewById(R.id.btn_add_imagem);
        imageView = findViewById(R.id.imagem_escala);
        info = findViewById(R.id.text_sem_escala);
        buscarFoto();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bBotao = !bBotao;
                if (bBotao) {
                    floatingActionButton.setVisibility(View.GONE);
                } else {
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdicionarImagemEscalaActivity.class);
                startActivity(intent);
            }
        });

    }

    private void buscarFoto() {
        if (img.equals("")){
            Progresso.progressoCircular(this);

            firebaseStorage = FirebaseStorage.getInstance();
            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference filepath = storageReference.child("Fotos")
                    .child("escala");

            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    info.setText("");
                    Uri downloadUrl = uri;
                    String imagem;
                    imagem = downloadUrl.toString();
                    fotoGaleria = imagem;
                    Picasso.with(getApplicationContext()).load(imagem).into(imageView);
                    Progresso.dialog.dismiss();
                    img = imagem;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Progresso.dialog.dismiss();
                    info.setText("Sem escala");
                }
            });
        }else {
            info.setText("");
            Picasso.with(getApplicationContext()).load(img).into(imageView);

        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        buscarFoto();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
