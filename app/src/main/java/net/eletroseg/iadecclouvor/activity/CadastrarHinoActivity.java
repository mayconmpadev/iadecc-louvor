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
import android.view.View;
import android.widget.EditText;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.Selecao;

import java.util.ArrayList;

public class CadastrarHinoActivity extends AppCompatActivity {
EditText nome, cantor, tom, letra;
    MenuItem menuSalvar;
    MenuItem menuEditar;

    int a = 0;
    int b = 0;
    int posicao = 0;
    boolean abilitar = true;
    boolean ativar = false;
    ArrayList<Selecao> negrito = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_hino);

        nome = findViewById(R.id.cadastro_hino_edit_nome);
        cantor = findViewById(R.id.cadastro_hino_edit_cantor);
        tom = findViewById(R.id.cadastro_hino_edit_tom);
        letra = findViewById(R.id.cadastro_hino_edit_letra);

        letra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (abilitar) {
                    posicao = letra.getSelectionStart();
                    contrV(letra.getText().toString());


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
                final SpannableString text = new SpannableString(letra.getText().toString());
                for (int i = 0; i < negrito.size(); i++) {

                     text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), negrito.get(i).comeco, negrito.get(i).fim - 1, 0);
                   // text.setSpan(new ForegroundColorSpan(Color.RED), negrito.get(i).comeco, negrito.get(i).fim - 1, 0);
                    text.setSpan(new ForegroundColorSpan(Color.GRAY), negrito.get(i).comeco - 1, negrito.get(i).comeco, 0);
                    text.setSpan(new ForegroundColorSpan(Color.GRAY), negrito.get(i).fim - 1, negrito.get(i).fim, 0);
                    // text.setSpan(new ForegroundColorSpan(Color.RED), 5, 9, 0);


                }
                abilitar = false;
                negrito.clear();
                letra.setText(text, EditText.BufferType.SPANNABLE);
                a = 0;
                b = 0;

                letra.setSelection(posicao);


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.salvar, menu);

        menuSalvar = menu.findItem(R.id.item_menu_produto_salvar);
        menuEditar = menu.findItem(R.id.item_menu_produto_editar);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

//
        if (id == R.id.item_menu_novo_hino) {

            Intent intent = new Intent(CadastrarHinoActivity.this, CadastrarHinoActivity.class);
            intent.putExtra("modo", false);
            startActivity(intent);
            finish();
        } else if (id == R.id.item_menu_pesquisa_hino) {
            getSupportActionBar().hide();
        } else if (id == android.R.id.home) {

            Intent intent = new Intent(CadastrarHinoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
