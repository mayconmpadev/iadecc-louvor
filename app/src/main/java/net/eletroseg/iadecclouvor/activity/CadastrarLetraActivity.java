package net.eletroseg.iadecclouvor.activity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.Hino;
import net.eletroseg.iadecclouvor.modelo.Selecao;
import net.eletroseg.iadecclouvor.util.Base64Custom;
import net.eletroseg.iadecclouvor.util.Constantes;
import net.eletroseg.iadecclouvor.util.InstanciaFirebase;
import net.eletroseg.iadecclouvor.util.Parametro;
import net.eletroseg.iadecclouvor.util.Progresso;
import net.eletroseg.iadecclouvor.util.Util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CadastrarLetraActivity extends AppCompatActivity {
    EditText nome, cantor, tom, audio, editLetra, editCifra;
    Spinner spinner;
    Button procurar, salvar;
    MenuItem menuSalvar;
    MenuItem menuEditar;
    LinearLayout linearLayout;
    PopupWindow popUp;
    Hino hino;
    Uri uri;
    String usuario;
    int a = 0;
    int b = 0;
    int posicao = 0;
    boolean abilitar = true;
    boolean bEdit = false;
    ArrayList<Selecao> negrito = new ArrayList<>();

    Dialog dialog;
    File myFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_letra);
//        getSupportActionBar().hide();
        nome = findViewById(R.id.cadastro_hino_edit_nome);
        cantor = findViewById(R.id.cadastro_hino_edit_cantor);
        tom = findViewById(R.id.cadastro_hino_edit_tom);
        audio = findViewById(R.id.edit_audio);
        editLetra = findViewById(R.id.cadastro_hino_edit_letra);
        editCifra = findViewById(R.id.cadastro_hino_edit_cifra);
        linearLayout = findViewById(R.id.linear_cadastro);
        procurar = findViewById(R.id.procurar_audio);
        spinner = findViewById(R.id.spinner_categoria);
        salvar = findViewById(R.id.btn_salvar);
        recuperarIntent();
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificarEditText()) {
                    if (bEdit) {

                        salvarProduto();
                    } else {
                        verificarNome(nome.getText().toString(), nome);
                    }

                }

            }
        });
        procurar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 1);
            }
        });
        editLetra.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Intent intent = new Intent(getApplicationContext(), LetraActivity.class);
                    startActivity(intent);
                }
            }
        });

        editCifra.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Intent intent = new Intent(getApplicationContext(), CifraActivity.class);
                    startActivity(intent);
                }
            }
        });
        editLetra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LetraActivity.class);
                startActivity(intent);
            }
        });

        editCifra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CifraActivity.class);
                startActivity(intent);
            }
        });
        tom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialogTons2(v);
                }
            }
        });
        tom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTons2(view);
            }
        });

    }

    private void recuperarIntent() {
        Intent intent = getIntent();
        hino = (Hino) intent.getSerializableExtra("hino");
        if (hino != null) {
            bEdit = true;
            nome.setText(hino.nome);
            cantor.setText(hino.cantor);
            tom.setText(hino.tom);
            audio.setText(hino.nome + ".mp3");
            Parametro.letra = hino.letra;
            Parametro.cifra = hino.cifra;
            String sUnidades = hino.categoria;
            String[] arrayCategoria = getResources().getStringArray(R.array.categoria_hinos);
            for (int i = 0; i < arrayCategoria.length; i++) {
                if (arrayCategoria[i].equals(sUnidades)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        } else {
            hino = new Hino();
            bEdit = false;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {

                //the selected audio.
                uri = data.getData();
                audio.setText(queryName(getContentResolver(), uri));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    private void digitado(final Button button, final PopupWindow popUp) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tom.setText(button.getText().toString());
                popUp.dismiss();
            }
        });

    }


    private boolean validar() {
        boolean a = true;

        if (editLetra.getText().toString().isEmpty()) {
            a = false;
        } else if (nome.getText().toString().isEmpty()) {
            a = false;
        }


        return a;
    }

    private void salvar() {
        Hino hino = new Hino();
        if (validar()) {

            DatabaseReference reference = InstanciaFirebase.getDatabase().getReference("letras").push();
            hino.id = reference.getKey();

            hino.nome = nome.getText().toString();
            hino.cantor = cantor.getText().toString();
            hino.tom = tom.getText().toString();
            hino.data = data();
            // hino.link = link.getText().toString();
            hino.letra = editLetra.getText().toString();
            hino.repeticao = "0";
            hino.data = data();
            reference.setValue(hino);
            Intent intent = new Intent(CadastrarLetraActivity.this, ListaHinosActivity.class);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Campos nao podem ser vazios", Toast.LENGTH_SHORT).show();
        }
    }

    private String data() {
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = sdf.format(date);
        return dateString;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.salvar, menu);

        menuSalvar = menu.findItem(R.id.item_menu_letra_salvar);
        menuEditar = menu.findItem(R.id.item_menu_letra_editar);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

//
        if (id == R.id.item_menu_letra_salvar) {

            salvar();
        } else if (id == R.id.item_menu_letra_editar) {

        } else if (id == android.R.id.home) {

            Intent intent = new Intent(CadastrarLetraActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void dialogSair(String texto) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_sair);
        TextView msg = dialog.findViewById(R.id.layout_text_sair_msg);
        Button ok = dialog.findViewById(R.id.layout_btn_sair_ok);
        Button cancelar = dialog.findViewById(R.id.layout_btn_sair_cancelar);

        msg.setText(texto);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CadastrarLetraActivity.this, ListaHinosActivity.class);
                startActivity(intent);
                finish();
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public void onBackPressed() {
        if (nome.getText().toString().isEmpty() & cantor.getText().toString().isEmpty() & tom.getText().toString().isEmpty() &
                audio.getText().toString().isEmpty() & editLetra.getText().toString().isEmpty()) {

            Intent intent = new Intent(CadastrarLetraActivity.this, ListaHinosActivity.class);
            startActivity(intent);
            finish();
        } else {
            dialogSair("Deseja sair sem salvar?");
        }
    }

    private void dialogTons2(View v) {
        int[] location = new int[2];
        tom.getLocationOnScreen(location);
        LayoutInflater inflater = (LayoutInflater) CadastrarLetraActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.dialog_tons, null);

        final Button A = customView.findViewById(R.id.btn_A);
        final Button Bb = customView.findViewById(R.id.btn_Bb);
        final Button B = customView.findViewById(R.id.btn_B);
        final Button C = customView.findViewById(R.id.btn_C);
        final Button Db = customView.findViewById(R.id.btn_Db);
        final Button D = customView.findViewById(R.id.btn_D);
        final Button Eb = customView.findViewById(R.id.btn_Eb);
        final Button E = customView.findViewById(R.id.btn_E);
        final Button F = customView.findViewById(R.id.btn_F);
        final Button Gb = customView.findViewById(R.id.btn_Gb);
        final Button G = customView.findViewById(R.id.btn_G);
        final Button Ab = customView.findViewById(R.id.btn_Ab);
        final Button Am = customView.findViewById(R.id.btn_Am);
        final Button Bbm = customView.findViewById(R.id.btn_Bbm);
        final Button Bm = customView.findViewById(R.id.btn_Bm);
        final Button Cm = customView.findViewById(R.id.btn_Cm);
        final Button Csm = customView.findViewById(R.id.btn_Csm);
        final Button Dm = customView.findViewById(R.id.btn_Dm);
        final Button Ebm = customView.findViewById(R.id.btn_Ebm);
        final Button Em = customView.findViewById(R.id.btn_Em);
        final Button Fm = customView.findViewById(R.id.btn_Fm);
        final Button Fsm = customView.findViewById(R.id.btn_Fsm);
        final Button Gm = customView.findViewById(R.id.btn_Gm);
        final Button Gsm = customView.findViewById(R.id.btn_Gsm);


        //instantiate popup window
        popUp = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        digitado(A, popUp);
        digitado(Bb, popUp);
        digitado(B, popUp);
        digitado(C, popUp);
        digitado(Db, popUp);
        digitado(D, popUp);
        digitado(Eb, popUp);
        digitado(E, popUp);
        digitado(F, popUp);
        digitado(Gb, popUp);
        digitado(G, popUp);
        digitado(Ab, popUp);
        digitado(Am, popUp);
        digitado(Bbm, popUp);
        digitado(Bm, popUp);
        digitado(Cm, popUp);
        digitado(Csm, popUp);
        digitado(Dm, popUp);
        digitado(Ebm, popUp);
        digitado(Em, popUp);
        digitado(Fm, popUp);
        digitado(Fsm, popUp);
        digitado(Gm, popUp);
        digitado(Gsm, popUp);
        popUp.setTouchable(true);
        popUp.setFocusable(true);
        popUp.setOutsideTouchable(true);
        popUp.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]);
        popUp.showAsDropDown(linearLayout);

    }

    private void verificarNome(final String nome, final EditText editText) {
        Progresso.progressoCircular(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference().child(Constantes.HINO)
                .child(Base64Custom.codificarBase64(nome));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    salvarProduto();

                } else {
                    Progresso.dialog.dismiss();
                    editText.requestFocus();
                    editText.setError("Esse nome ja existe");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //---------------------------------------------------- SALVAR DADOS E IMAGEM -----------------------------------------------------------------

    private void salvarProduto() {

        Progresso.progressoCircular(this);
        hino.nome = nome.getText().toString();
        hino.cantor = cantor.getText().toString();
        hino.tom = tom.getText().toString();
        hino.data = data();
        hino.categoria = spinner.getSelectedItem().toString();
        hino.letra = Parametro.letra;
        hino.cifra = Parametro.cifra;

        if (uri == null) {
            uri = Uri.parse("android.resource://com.example.compacpdv/drawable/sem_foto");
        }
        if (!audio.getText().toString().equals(hino.nome + ".mp3")) {
            excluirHinoLocal(hino.nome);
            hino.audioHino = uri.toString();
        }


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference().child(Constantes.HINO);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReferencere = storage.getReference().child(Constantes.AUDIO).child(Base64Custom.codificarBase64(hino.nome));
        UploadTask uploadTask = storageReferencere.putFile(uri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                return storageReferencere.getDownloadUrl();
            }


        }).addOnCompleteListener(new OnCompleteListener<Uri>() {

            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
                    hino.audioHino = uri.toString();
                    hino.id = Base64Custom.codificarBase64(hino.nome);
                    databaseReference.child(hino.id).setValue(hino).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(getApplicationContext(), "audio salvo", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CadastrarLetraActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(getApplicationContext(), "erro ao criar orcamento", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    Toast.makeText(getApplicationContext(), "pdf salvo", Toast.LENGTH_SHORT).show();
                    Progresso.dialog.dismiss();

                } else {
                    Toast.makeText(getApplicationContext(), "erro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    Progresso.dialog.dismiss();

                }
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Parametro.letra != null) {
            Util.textoNegrito(Parametro.letra, null, editLetra);
        }
        if (Parametro.cifra != null) {
            Util.textoNegrito(Parametro.cifra, null, editCifra);
        }


    }

    private boolean verificarEditText() {
        boolean a = false;
        ArrayList<EditText> arrayList = new ArrayList<>();
        arrayList.add(nome);
        arrayList.add(cantor);
        arrayList.add(editLetra);
        arrayList.add(tom);
        arrayList.add(audio);

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).getText().toString().isEmpty()) {
                arrayList.get(i).requestFocus();
                arrayList.get(i).setError("o campo não pode ser vazio");
                a = false;
                break;
            } else {
                arrayList.get(i).setError(null);
                a = true;
            }
        }


        return a;
    }

    public void excluirHinoLocal(String nome) {

        try {
            File pdfFolder = new File(this.getExternalFilesDir(null)
                    + File.separator
                    + "Iadecc/hinos"
                    + File.separator);
            if (!pdfFolder.exists()) {
                pdfFolder.mkdirs();
            }
            myFile = new File(pdfFolder + File.separator + nome + ".mp3");
            if (myFile.exists()) {
                myFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
