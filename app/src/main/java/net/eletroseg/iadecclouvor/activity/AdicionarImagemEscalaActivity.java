package net.eletroseg.iadecclouvor.activity;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import static net.eletroseg.iadecclouvor.util.Parametro.img;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.util.Progresso;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AdicionarImagemEscalaActivity extends AppCompatActivity {
    private ImageView imagem;
    private Button salvar;
    private static final int GALLERY_INTENT = 2;
    Bitmap rotatedBitmap = null;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String fotoGaleria;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_imagem_escala);
        imagem = findViewById(R.id.imagem_add_escala);
        salvar = findViewById(R.id.foto_btn_salvar_escala);

        imagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);

            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarFoto();

            }
        });
    }

    private void salvarFoto() {
        Progresso.progressoCircular(this);
        if (uri != null) {
            firebaseStorage = FirebaseStorage.getInstance();
            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference filepath = storageReference.child("Fotos")
                    .child("escala");
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;
                            String imagem;
                            imagem = downloadUrl.toString();
                            fotoGaleria = imagem;
                            //usuario.foto = fotoGaleria;
                            Progresso.dialog.dismiss();
                            img = "";

                        }
                    });

                    filepath.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Progresso.dialog.dismiss();
                            Toast.makeText(getBaseContext(), "falha", Toast.LENGTH_SHORT).show();
                        }
                    });

                }


            });

        } else {

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {


            uri = data.getData();

            try {
                Bitmap imagem = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);


                rotatedBitmap = rotateBitmap1(this, uri, imagem);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 2, stream);


                this.imagem.setImageBitmap(rotatedBitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public static Bitmap rotateBitmap1(Context context, Uri photoUri, Bitmap bitmap) {
        int orientation = getOrientation(context, photoUri);
        if (orientation <= 0) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return bitmap;
    }

    private static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            cursor.close();
            return -1;
        }

        cursor.moveToFirst();
        int orientation = cursor.getInt(0);
        cursor.close();
        cursor = null;
        return orientation;
    }
}