package net.eletroseg.iadecclouvor.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.Selecao;
import net.eletroseg.iadecclouvor.util.Parametro;
import net.eletroseg.iadecclouvor.util.Util;

import java.util.ArrayList;

public class LetraActivity extends AppCompatActivity {
    private Button limpar, salvar;
    private EditText letra;
    int a = 0;
    int b = 0;
    int posicao = 0;
    boolean abilitar = true;
    boolean ativar = false;
    ArrayList<Selecao> negrito = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letra);
        getSupportActionBar().hide();
        recuperarComponentes();
        //letra.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        //letra.setText(Parametro.letra);
        textoNegrito2(Parametro.letra, null, letra);
        limpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sletra = letra.getText().toString();
                Util.textoNegrito(sletra, null, letra);
            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (letra.getText().toString().isEmpty()){
                    Toast.makeText(LetraActivity.this, "vazio", Toast.LENGTH_SHORT).show();
                }else {
                    Parametro.letra = letra.getText().toString();
                    finish();
                }
            }
        });

        letra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (abilitar) {
                    if (s.toString().length() > start) {
                        if ("*".equals(String.valueOf(s.toString().charAt(start)))) {
                            ativar = true;
                        } else {
                            ativar = false;
                        }
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (ativar) {
                    if (abilitar) {
                        posicao = letra.getSelectionStart();
                        textoNegrito2(letra.getText().toString(), null, letra);
                    }
                }
                if (letra.getText().toString().length() > start) {
                    if ("*".equals(String.valueOf(letra.getText().toString().charAt(start)))) {
                        if (abilitar) {
                            posicao = letra.getSelectionStart();
                            textoNegrito2(letra.getText().toString(), null, letra);
                        }
                    }
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

    private void recuperarComponentes() {
        limpar = findViewById(R.id.btn_limpar);
        salvar = findViewById(R.id.btn_salvar);
        letra = findViewById(R.id.edit_letra);
    }

    public void textoNegrito2(final String texto, final TextView textView, final EditText editText) {
        runOnUiThread(new Runnable() {
            public void run() {
                abilitar = false;
                final SpannableString text = new SpannableString(texto);
                int a = 0;
                int b = texto.length();
                int c = 0;
                for (int i = 0; i < texto.length(); i++) {

                    if ("*".equals(texto.substring(i, i + 1))) {
                        if (b == texto.length()) {
                            b = a;
                        } else {
                            c = a;
                            text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), b, c, 0);
                            text.setSpan(new ForegroundColorSpan(Color.GRAY), b, b + 1, 0);
                            text.setSpan(new ForegroundColorSpan(Color.GRAY), c, c + 1, 0);


                            b = texto.length();
                            c = 0;

                        }
                    }
                    a++;
                }

                if (textView == null) {
                    editText.setText(text, EditText.BufferType.SPANNABLE);
                } else {
                    textView.setText(text, EditText.BufferType.SPANNABLE);
                }
                letra.setSelection(posicao);
                abilitar = true;
            }
        });
    }


}
