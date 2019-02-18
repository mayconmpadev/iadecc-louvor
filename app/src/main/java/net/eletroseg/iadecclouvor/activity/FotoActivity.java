package net.eletroseg.iadecclouvor.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.Usuario;
import net.eletroseg.iadecclouvor.util.ConfiguracaoFiribase;
import net.eletroseg.iadecclouvor.util.InstanciaFirebase;
import net.eletroseg.iadecclouvor.util.SPM;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FotoActivity extends AppCompatActivity {
    ImageView foto;
    Button galeria, excluir;

    FirebaseAuth firebaseAuth;
    SPM spm = new SPM(FotoActivity.this);
    private static final int GALLERY_INTENT = 2;
    Uri uri;
    Usuario usuario;
    Dialog dialog;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String fotoGaleria;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);

        foto =  findViewById(R.id.foto_image_perfil);
        galeria =  findViewById(R.id.foto_btn_alterar);
        excluir =  findViewById(R.id.foto_btn_excluir);

        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarFoto();
            }
        });

        buscarPerfilWeb();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {


            uri = data.getData();

            try {
                Bitmap imagem = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);


                Bitmap rotatedBitmap = null;

                rotatedBitmap = rotateBitmap1(this, uri, imagem);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 5, stream);


                foto.setImageBitmap(rotatedBitmap);
                uri = getImageUri(this, rotatedBitmap);


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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void salvarFoto() {
       // exibeProgresso();
        if (uri != null) {
            firebaseStorage = FirebaseStorage.getInstance();
            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference filepath = storageReference.child("Fotos")
                    .child("perfil_do_usuario" + spm.getPreferencia("USUARIO_LOGADO", "USUARIO", "erro"));
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

                            salvar();
                        }
                    });

                    filepath.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(FotoActivity.this, "falha", Toast.LENGTH_SHORT).show();
                        }
                    });

                }


            });

        } else {
            salvar();
        }

    }

    private void salvar() {

    Usuario usuario = new Usuario();

        firebaseAuth = ConfiguracaoFiribase.getFirebaseAutenticacao();
        DatabaseReference reference = InstanciaFirebase.getDatabase().getReference("usuarios").child( spm.getPreferencia("USUARIO_LOGADO", "USUARIO", "erro"));
        reference.setValue(usuario);
        reference.keepSynced(true);
        //dialog.dismiss();
        Toast.makeText(this, "Salvo com sucesso", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(FotoActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void buscarPerfilWeb() {

        DatabaseReference reference = InstanciaFirebase.getDatabase().getReference("usuarios").child(spm.getPreferencia("USUARIO_LOGADO", "USUARIO", "erro"));
        reference.keepSynced(true);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                usuario =(Usuario) dataSnapshot.getValue(Usuario.class);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(FotoActivity.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (usuario == null) {

                } else {


                }
                if (usuario != null) {

                    Picasso.with(FotoActivity.this).load(usuario.foto).into(foto);

                    galeria.setEnabled(false);



                } else {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
