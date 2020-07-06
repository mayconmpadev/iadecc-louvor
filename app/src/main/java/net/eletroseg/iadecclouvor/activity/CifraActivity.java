package net.eletroseg.iadecclouvor.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.Selecao;
import net.eletroseg.iadecclouvor.util.Parametro;

import java.util.ArrayList;

public class CifraActivity extends AppCompatActivity {
    private Button limpar, salvar, notas;
    private EditText cifra, dialogNotas;
    private LinearLayout linearLayout;
    int a = 0;
    int b = 0;
    int posicao2 = 0;
    int posicao3 = 0;
    PopupWindow popUp;
    boolean abilitar = true;
    boolean ativar = false;
    boolean bB = true;
    ArrayList<Selecao> negrito = new ArrayList<>();
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cifra);
        // getSupportActionBar().hide();
        recuperarComponentes();
        cifra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posicao2 = cifra.getSelectionStart();
            }
        });
        if (Parametro.cifra == null) {
            cifra.setText(addNewLine(Parametro.letra));
        } else {
            cifra.setText(Parametro.cifra);
        }

        textoNegrito2(cifra.getText().toString(), null, cifra);
        //letra.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        //letra.setText(Parametro.letra);
        //textoNegrito2(Parametro.letra, null, cifra);
        limpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cifra.setText("");
            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cifra.getText().toString().isEmpty()) {
                    Toast.makeText(CifraActivity.this, "vazio", Toast.LENGTH_SHORT).show();
                } else {
                    Parametro.cifra = cifra.getText().toString();
                    finish();
                }
            }
        });
       notas.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               dialogNota();
           }
       });

        limpar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dialogTons2(view);
                return true;
            }
        });


    }

    private void recuperarComponentes() {
        linearLayout = findViewById(R.id.linear_cifra);
        limpar = findViewById(R.id.btn_limpar_cifra);
        notas = findViewById(R.id.btn_notas_cifra);
        salvar = findViewById(R.id.btn_salvar_cifra);
        cifra = findViewById(R.id.edit_cifra);
    }

    public void textoNegrito2(final String texto, final TextView textView, final EditText editText) {
        runOnUiThread(new Runnable() {
            public void run() {
                abilitar = false;
                final SpannableString text = new SpannableString(texto);
                int a = 0;
                int b = texto.length();
                int c = 0;

                // String notas = "CDEFGAB/#bmM123456789";
                String[] arrayNotas = getResources().getStringArray(R.array.todas_notas_musicais);
                for (int j = 0; j < arrayNotas.length; j++) {
                    String nota = arrayNotas[j];


                    for (int i = -1; (i = texto.indexOf(nota, i + 1)) != -1; i++) {
                        b = i;
                        c = i + nota.length();
                        // prints "4", "13", "22"
                        text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), b, c, 0);
                        text.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6D00")), b, c, 0);

                    }
                }

                if (textView == null) {
                    editText.setText(text, EditText.BufferType.SPANNABLE);
                } else {
                    textView.setText(text, EditText.BufferType.SPANNABLE);
                }
                cifra.setSelection(posicao2);
                abilitar = true;
            }
        });
    }

    public void textoNegrito3(final String texto, final TextView textView, final EditText editText) {
        runOnUiThread(new Runnable() {
            public void run() {
                abilitar = false;
                final SpannableString text = new SpannableString(texto);
                int a = 0;
                int b = texto.length();
                int c = 0;

                // String notas = "CDEFGAB/#bmM123456789";
                String[] arrayNotas = getResources().getStringArray(R.array.notas_musicais);
                for (int j = 0; j < arrayNotas.length; j++) {
                    String nota = arrayNotas[j];

                    for (int i = -1; (i = texto.indexOf(nota, i + 1)) != -1; i++) {
                        b = i;
                        c = i + nota.length();
                        // prints "4", "13", "22"
                        text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), b, c, 0);
                        text.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6D00")), b, c, 0);

                    }
                }

                if (textView == null) {
                    editText.setText(text, EditText.BufferType.SPANNABLE);
                } else {
                    textView.setText(text, EditText.BufferType.SPANNABLE);
                }
                dialogNotas.setSelection(posicao3);
                abilitar = true;
            }
        });
    }

    public String addNewLine(String string) {
        int count = string.split("\n", -1).length - 1;
        StringBuilder sb = new StringBuilder(count);
        String[] splitString = string.split("\n");
        for (int i = 0; i < splitString.length; i++) {
            sb.append(splitString[i]);
            if (i != splitString.length - 1) {
                sb.append("\n" + "                                                                                   ");
            }
        }
        return sb.toString();
    }

    private void digitado(final Button button, final PopupWindow popUp) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cifra.setText(addChar(cifra.getText().toString(), "\u00A0" + button.getText().toString() + "\u00A0", cifra.getSelectionStart()));
                textoNegrito2(cifra.getText().toString(), null, cifra);
                popUp.dismiss();
            }
        });

    }

    private void dialogTons2(View v) {
        int[] location = new int[2];
        cifra.getLocationOnScreen(location);
        LayoutInflater inflater = (LayoutInflater) CifraActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    private void dialogNota() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_nota);
        Button ok = dialog.findViewById(R.id.btn_dialog_nota_ok);
        dialogNotas = dialog.findViewById(R.id.edit_dialog_nota);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cifra.setText(addChar(cifra.getText().toString(), dialogNotas.getText().toString() + "  ", posicao2));
                posicao2 = posicao2 + dialogNotas.getText().toString().length() + 2;
                textoNegrito2(cifra.getText().toString(), null, cifra);

                dialog.dismiss();
            }
        });
        dialogNotas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (abilitar) {
                    posicao3 = dialogNotas.getSelectionStart();
                    textoNegrito3(dialogNotas.getText().toString(), null, dialogNotas);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }

    public String addChar(String str, String ch, int position) {
        StringBuilder sb = new StringBuilder(str);
        sb.insert(position, ch);
        return sb.toString();
    }
}
