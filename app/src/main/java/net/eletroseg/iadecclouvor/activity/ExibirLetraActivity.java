package net.eletroseg.iadecclouvor.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.Selecao;

import java.util.ArrayList;

public class ExibirLetraActivity extends AppCompatActivity {
    TextView letraDoHino;
    String hino;
    int a = 0;
    int b = 0;
    int posicao = 0;
    boolean abilitar = true;
    ArrayList<Selecao> negrito = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exibir_letra);
        letraDoHino = findViewById(R.id.exibir_letra_text_letra);
        getSupportActionBar().hide();

       Intent intent =  getIntent();
       hino = intent.getStringExtra("letra");

       letraDoHino.setText(hino);
        contrV(letraDoHino.getText().toString());

       letraDoHino.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               contrV(letraDoHino.getText().toString());
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
                final SpannableString text = new SpannableString(letraDoHino.getText().toString());
                for (int i = 0; i < negrito.size(); i++) {

                    text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), negrito.get(i).comeco, negrito.get(i).fim - 1, 0);
                    // text.setSpan(new ForegroundColorSpan(Color.RED), negrito.get(i).comeco, negrito.get(i).fim - 1, 0);
                    text.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), negrito.get(i).comeco - 1, negrito.get(i).comeco, 0);
                    text.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), negrito.get(i).fim - 1, negrito.get(i).fim, 0);
                    // text.setSpan(new ForegroundColorSpan(Color.RED), 5, 9, 0);


                }
                abilitar = false;
                negrito.clear();
                letraDoHino.setText(text, EditText.BufferType.SPANNABLE);
                a = 0;
                b = 0;
            }
        });

    }
}
