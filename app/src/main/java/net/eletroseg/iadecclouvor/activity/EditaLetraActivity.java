package net.eletroseg.iadecclouvor.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.Letra;
import net.eletroseg.iadecclouvor.modelo.Selecao;
import net.eletroseg.iadecclouvor.util.InstanciaFirebase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EditaLetraActivity extends AppCompatActivity {

    EditText nome, cantor, tom, link, editLetra;
    MenuItem menuSalvar;
    MenuItem menuEditar;
    String sId, sNome, sCantor, sTom, sLink, sEditarLetra, sData;

    int a = 0;
    int b = 0;
    int posicao = 0;
    boolean abilitar = true;
    boolean ativar = false;
    ArrayList<Selecao> negrito = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edita_letra);

        nome = findViewById(R.id.editar_letra_edit_nome);
        cantor = findViewById(R.id.editar_letra_edit_cantor);
        tom = findViewById(R.id.editar_letra_edit_tom);
        link = findViewById(R.id.editar_letra_edit_link);
        editLetra = findViewById(R.id.editar_letra_edit_letra);

        Intent intent = getIntent();

        sId = intent.getStringExtra("id");
        sNome = intent.getStringExtra("nome");
        sCantor = intent.getStringExtra("cantor");
        sTom = intent.getStringExtra("tom");
        sLink = intent.getStringExtra("link");
        sEditarLetra = intent.getStringExtra("letra");
        sData = intent.getStringExtra("data");

        nome.setText(sNome);
        cantor.setText(sCantor);
        tom.setText(sTom);
        link.setText(sLink);
        editLetra.setText(sEditarLetra);
        contrV(editLetra.getText().toString());


        editLetra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (abilitar) {
                    posicao = editLetra.getSelectionStart();
                    contrV(editLetra.getText().toString());


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        Letra letra = new Letra();
        if (validar()) {

            DatabaseReference reference = InstanciaFirebase.getDatabase().getReference("letras").child(sId);

            letra.id = sId;
            letra.nome = nome.getText().toString();
            letra.cantor = cantor.getText().toString();
            letra.tom = tom.getText().toString();
            letra.link = link.getText().toString();
            letra.letra = editLetra.getText().toString();
            letra.data = sData;
            reference.setValue(letra);
            Intent intent = new Intent(EditaLetraActivity.this, ListaLetrasActivity.class);
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

            Intent intent = new Intent(EditaLetraActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
