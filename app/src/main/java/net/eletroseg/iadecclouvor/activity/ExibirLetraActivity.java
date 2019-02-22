package net.eletroseg.iadecclouvor.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.Selecao;

import java.util.ArrayList;

public class ExibirLetraActivity extends AppCompatActivity {
    TextView letraDoHino, valorVelocidade;
    ScrollView scrollView;
    LinearLayout linearLayout;
    SeekBar seekBar;
    String hino;
    boolean c = true;
    boolean d = true;
    int a = 0;
    int b = 0;
    boolean ativar = true;
    boolean abilitar = true;
    int numero = 0;
    MinhaThread thread = new MinhaThread();
    ArrayList<Selecao> negrito = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exibir_letra);
        letraDoHino = findViewById(R.id.exibir_letra_text_letra);
        scrollView = findViewById(R.id.letra_sv);
        linearLayout = findViewById(R.id.exibir_letra_layout_velocidade);
        valorVelocidade = findViewById(R.id.exibir_letra_text_velocidade);
        seekBar = findViewById(R.id.exibir_letra_seekbar);

        getSupportActionBar().hide();
        Intent intent = getIntent();
        hino = intent.getStringExtra("letra");

        letraDoHino.setText(hino);

        contrV(letraDoHino.getText().toString());

        letraDoHino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (d){
                    thread.start();
                    d = false;
                }

                if (c){

                    ativar = true;
                    Toast.makeText(ExibirLetraActivity.this, "inicio", Toast.LENGTH_SHORT).show();
                  //  numero = scrollView.getScrollX();
                    Toast.makeText(ExibirLetraActivity.this, String.valueOf(scrollView.getScrollX()), Toast.LENGTH_SHORT).show();
                    c = false;
                }else {
                    ativar = false;
                    Toast.makeText(ExibirLetraActivity.this, "parar", Toast.LENGTH_SHORT).show();
                    c = true;
                }
            }
        });

        letraDoHino.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
              if (c){
                  linearLayout.setVisibility(View.VISIBLE);
                  c = false;
              }else {
                  linearLayout.setVisibility(View.GONE);
                  c = true;
              }

                return true;
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

    class MinhaThread extends Thread {
        @Override
        public void run() {

            if (ativar ) {
                for (int i = 0; i < scrollView.getScrollX(); i++) {
                    numero = i;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (ativar){
                                scrollView.scrollTo(0, numero);
                            }



                        }
                    });
                }


            }

        }
    }

}
