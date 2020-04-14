package net.eletroseg.iadecclouvor.activity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CadastrarLetraActivity extends AppCompatActivity {
    EditText nome, cantor, tom, audio, editLetra;
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
    boolean ativar = false;
    ArrayList<Selecao> negrito = new ArrayList<>();

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_letra);
        getSupportActionBar().hide();
        nome = findViewById(R.id.cadastro_hino_edit_nome);
        cantor = findViewById(R.id.cadastro_hino_edit_cantor);
        tom = findViewById(R.id.cadastro_hino_edit_tom);
        audio = findViewById(R.id.edit_audio);
        editLetra = findViewById(R.id.cadastro_hino_edit_letra);
        linearLayout = findViewById(R.id.linear_cadastro);
        procurar = findViewById(R.id.procurar_audio);
        spinner = findViewById(R.id.spinner_categoria);
        salvar = findViewById(R.id.btn_salvar);

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificarEditText()){
                    verificarNome(nome.getText().toString(), nome);
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
        editLetra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LetraActivity.class);
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

    private void contrV(String texto) {
        abilitar = false;
        for (int i = 0; i < texto.length(); i++) {
            if (texto.substring(i, i + 1).equals("*")) {
                if (a == 0) {
                    a = i + 1;

                } else {

                    b = i + 1;
                    Selecao selecao = new Selecao();
                    selecao.comeco = a;
                    selecao.fim = b;
                    negrito.add(selecao);
                    a = 0;
                    b = 0;

                }
            }
        }

        setarTexto();
        abilitar = true;

    }

    private void setarTexto() {
        runOnUiThread(new Runnable() {

            public void run() {
                final SpannableString text = new SpannableString(editLetra.getText().toString());
                for (int i = 0; i < negrito.size(); i++) {

                    text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), negrito.get(i).comeco, negrito.get(i).fim - 1, 0);
                    // text.setSpan(new ForegroundColorSpan(Color.RED), negrito.get(i).comeco, negrito.get(i).fim - 1, 0);
                    text.setSpan(new ForegroundColorSpan(Color.GRAY), negrito.get(i).comeco - 1, negrito.get(i).comeco, 0);
                    text.setSpan(new ForegroundColorSpan(Color.GRAY), negrito.get(i).fim - 1, negrito.get(i).fim, 0);
                    // text.setSpan(new ForegroundColorSpan(Color.RED), 5, 9, 0);


                }
                abilitar = false;
                negrito.clear();
                editLetra.setText(text, EditText.BufferType.SPANNABLE);
                a = 0;
                b = 0;

                editLetra.setSelection(posicao);


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


    private void dialogTons() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_tons);

        dialog.setCanceledOnTouchOutside(false);
        //instancia os objetos que estão no layout customdialog.xml

        final Button A = dialog.findViewById(R.id.btn_A);
        final Button Bb = dialog.findViewById(R.id.btn_Bb);
        final Button B = dialog.findViewById(R.id.btn_B);
        final Button C = dialog.findViewById(R.id.btn_C);
        final Button Db = dialog.findViewById(R.id.btn_Db);
        final Button D = dialog.findViewById(R.id.btn_D);
        final Button Eb = dialog.findViewById(R.id.btn_Eb);
        final Button E = dialog.findViewById(R.id.btn_E);
        final Button F = dialog.findViewById(R.id.btn_F);
        final Button Gb = dialog.findViewById(R.id.btn_Gb);
        final Button G = dialog.findViewById(R.id.btn_G);
        final Button Ab = dialog.findViewById(R.id.btn_Ab);
        final Button Am = dialog.findViewById(R.id.btn_Am);
        final Button Bbm = dialog.findViewById(R.id.btn_Bbm);
        final Button Bm = dialog.findViewById(R.id.btn_Bm);
        final Button Cm = dialog.findViewById(R.id.btn_Cm);
        final Button Csm = dialog.findViewById(R.id.btn_Csm);
        final Button Dm = dialog.findViewById(R.id.btn_Dm);
        final Button Ebm = dialog.findViewById(R.id.btn_Ebm);
        final Button Em = dialog.findViewById(R.id.btn_Em);
        final Button Fm = dialog.findViewById(R.id.btn_Fm);
        final Button Fsm = dialog.findViewById(R.id.btn_Fsm);
        final Button Gm = dialog.findViewById(R.id.btn_Gm);
        final Button Gsm = dialog.findViewById(R.id.btn_Gsm);
        final Button view = new Button(this);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tom.setText(view.getText().toString());
            }
        });

        //exibe na tela o dialog
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

        hino = new Hino();
        hino.nome = nome.getText().toString();
        hino.cantor = cantor.getText().toString();
        hino.tom = tom.getText().toString();
        hino.data = data();
        hino.categoria = spinner.getSelectedItem().toString();
        hino.letra = Parametro.letra;

        if (uri == null) {
            uri = Uri.parse("android.resource://com.example.compacpdv/drawable/sem_foto");
        }
        hino.audioHino = uri.toString();


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

                    databaseReference.child(Base64Custom.codificarBase64(hino.nome)).setValue(hino).addOnCompleteListener(new OnCompleteListener<Void>() {
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


    private void toast(String msg, int corTexto, int colorFundo) { // toast personalizado
        View layout = getLayoutInflater().inflate(R.layout.toast_custom, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setTextColor(getResources().getColor(corTexto));
        text.setText(msg);
        CardView lyt_card = (CardView) layout.findViewById(R.id.lyt_card);
        lyt_card.setCardBackgroundColor(getResources().getColor(colorFundo));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Util.textoNegrito(Parametro.letra, null, editLetra);

    }

    private boolean verificarEditText(){
        boolean a = false;
        ArrayList<EditText> arrayList = new ArrayList<>();
        arrayList.add(nome);
        arrayList.add(cantor);
        arrayList.add(editLetra);
        arrayList.add(tom);
        arrayList.add(audio);

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).getText().toString().isEmpty()){
                arrayList.get(i).requestFocus();
                arrayList.get(i).setError("o campo não pode ser vazio");
                a = false;
                break;
            }else {
                arrayList.get(i).setError(null);
                a = true;
            }
        }


       return a;
    }

}