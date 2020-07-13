package net.eletroseg.iadecclouvor.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.Selecao;
import net.eletroseg.iadecclouvor.util.Parametro;

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
//        getSupportActionBar().hide();
        recuperarComponentes();
        //letra.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        //letra.setText(Parametro.letra);
        textoNegrito2(Parametro.letra, null, letra);
        limpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                letra.setText("");
            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (letra.getText().toString().isEmpty()) {
                    Toast.makeText(LetraActivity.this, "vazio", Toast.LENGTH_SHORT).show();
                } else {
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
                        if ("*".equals(String.valueOf(s.toString().charAt(start))) || "+".equals(String.valueOf(s.toString().charAt(start))) || "/".equals(String.valueOf(s.toString().charAt(start)))) {

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
                    if ("*".equals(String.valueOf(letra.getText().toString().charAt(start))) || "+".equals(String.valueOf(letra.getText().toString().charAt(start))) || "/".equals(String.valueOf(letra.getText().toString().charAt(start)))) {
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
                int d = 0;
                int e = texto.length();
                int f = 0;
                int g = 0;
                int h = texto.length();
                int j = 0;
                for (int i = 0; i < texto.length(); i++) {

                    if ("+".equals(texto.substring(i, i + 1))) {
                        if (b == texto.length()) {
                            b = a;
                        } else {
                            c = a;
                            text.setSpan(new ForegroundColorSpan(Color.BLUE), b, c, 0);
                            text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), b, c, 0);
                            text.setSpan(new ForegroundColorSpan(Color.GRAY), b, b + 1, 0);
                            text.setSpan(new ForegroundColorSpan(Color.GRAY), c, c + 1, 0);
                            b = texto.length();
                            c = 0;

                        }
                    }
                    a++;

                    if ("*".equals(texto.substring(i, i + 1))) {
                        if (e == texto.length()) {
                            e = d;
                        } else {
                            f = d;
                            text.setSpan(new ForegroundColorSpan(Color.RED), e, f, 0);
                            text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), e, f, 0);
                            text.setSpan(new ForegroundColorSpan(Color.GRAY), e, e + 1, 0);
                            text.setSpan(new ForegroundColorSpan(Color.GRAY), f, f + 1, 0);


                            e = texto.length();
                            f = 0;

                        }
                    }

                    d++;

                    if ("/".equals(texto.substring(i, i + 1))) {
                        if (h == texto.length()) {
                            h = g;
                        } else {
                            j = g;
                            text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), h, j, 0);
                            text.setSpan(new ForegroundColorSpan(Color.GRAY), h, h + 1, 0);
                            text.setSpan(new ForegroundColorSpan(Color.GRAY), j, j + 1, 0);


                            h = texto.length();
                            j = 0;

                        }
                    }

                    g++;
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
