package net.eletroseg.iadecclouvor.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.Avisos;
import net.eletroseg.iadecclouvor.util.Constantes;
import net.eletroseg.iadecclouvor.util.InstanciaFirebase;
import net.eletroseg.iadecclouvor.util.Progresso;
import net.eletroseg.iadecclouvor.util.Teste;
import net.eletroseg.iadecclouvor.util.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CadastroAvisoActivity extends AppCompatActivity {
    private EditText titulo, corpo;
    private Button salvar;
    Avisos avisos;

    int posicao = 0;
    boolean abilitar = true;
    boolean ativar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_aviso);
        iniciarComponentes();

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificarEditText()) {


                    salvarProduto();


                }
            }
        });

        corpo.addTextChangedListener(new TextWatcher() {
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
                        posicao = corpo.getSelectionStart();
                        textoNegrito2(corpo.getText().toString(), null, corpo);
                    }
                }
                if (corpo.getText().toString().length() > start) {
                    if ("*".equals(String.valueOf(corpo.getText().toString().charAt(start))) || "+".equals(String.valueOf(corpo.getText().toString().charAt(start))) || "/".equals(String.valueOf(corpo.getText().toString().charAt(start)))) {
                        if (abilitar) {
                            posicao = corpo.getSelectionStart();
                            textoNegrito2(corpo.getText().toString(), null, corpo);
                        }
                    }

                }

            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private void iniciarComponentes() {
        titulo = findViewById(R.id.cadastro_aviso_edit_titulo);
        corpo = findViewById(R.id.cadastro_aviso_edit_corpo);
        salvar = findViewById(R.id.btn_cadastro_aviso_salvar);
    }


    //---------------------------------------------------- SALVAR DADOS E IMAGEM -----------------------------------------------------------------

    private void salvarProduto() {
        avisos = new Avisos();
        Progresso.progressoCircular(this);
        avisos.titulo = titulo.getText().toString();
        avisos.corpo = corpo.getText().toString();
        avisos.data = String.valueOf(Timestamp.getUnixTimestamp());

        FirebaseDatabase database = InstanciaFirebase.getDatabase();
        final DatabaseReference databaseReference = database.getReference().child(Constantes.AVISOS);
        avisos.id = databaseReference.push().getKey();


        databaseReference.child(avisos.id).setValue(avisos).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Progresso.dialog.dismiss();
                    Teste teste = new Teste();
                    teste.enviarNotificacao("/topics/todos", avisos.titulo, avisos.corpo);
                    finish();

                } else {
                    Progresso.dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "erro ao criar Aviso", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }


    private boolean verificarEditText() {
        boolean a = false;
        ArrayList<EditText> arrayList = new ArrayList<>();
        arrayList.add(titulo);
        arrayList.add(corpo);


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
                            text.setSpan(new UnderlineSpan(), b, c, 0);
                            text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), b, c, 0);
                            text.setSpan(new ForegroundColorSpan(Color.GRAY), b, b + 1, 0);
                            text.setSpan(new ForegroundColorSpan(Color.GRAY), c, c + 1, 0);
                            b = texto.length();
                            c = 0;

                        }
                    }
                    a++;

                    if ("/".equals(texto.substring(i, i + 1))) {
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

                    if ("*".equals(texto.substring(i, i + 1))) {
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
                corpo.setSelection(posicao);
                abilitar = true;
            }
        });
    }


    private String data() {
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = sdf.format(date);
        return dateString;
    }
}
