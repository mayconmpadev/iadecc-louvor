package net.eletroseg.iadecclouvor.activity;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.api.NotificacaoService;
import net.eletroseg.iadecclouvor.modelo.Cronograma;
import net.eletroseg.iadecclouvor.modelo.Notificacao;
import net.eletroseg.iadecclouvor.modelo.NotificacaoDados;
import net.eletroseg.iadecclouvor.util.Constantes;
import net.eletroseg.iadecclouvor.util.Parametro;
import net.eletroseg.iadecclouvor.util.Progresso;
import net.eletroseg.iadecclouvor.util.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListaAvisosActivity extends AppCompatActivity {
    private Button culto, ministrante, vocal, instrumental, musica, salvar;
    private EditText observacao;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    String tipo = "";

    private Retrofit retrofit;
    private String baseUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_avisos);
        recuperaIntent();
        iniciarComponentes();
        baseUrl = "https://fcm.googleapis.com/fcm/";
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        culto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateTimeField();
                fromDatePickerDialog.show();
            }
        });

        ministrante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SelecaoUsuarioActivity.class);
                intent.putExtra("tipo", "ministrante");
                startActivity(intent);
            }
        });

        vocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SelecaoUsuarioActivity.class);
                intent.putExtra("tipo", "vocal");
                startActivity(intent);
            }
        });

        instrumental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SelecaoUsuarioActivity.class);
                intent.putExtra("tipo", "instrumental");
                startActivity(intent);
            }
        });
        musica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SelecaoHinoActivity.class);
                startActivity(intent);
            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (validarCampos()){
                   salvarProduto();
               }

            }
        });

    }
    private void recuperaIntent() {

        Intent intent = getIntent();
        tipo = intent.getStringExtra("tipo");

    }
    private void iniciarComponentes() {
        culto = findViewById(R.id.btn_aviso_culto);
        ministrante = findViewById(R.id.btn_aviso_ministrante);
        vocal = findViewById(R.id.btn_aviso_vocal);
        instrumental = findViewById(R.id.btn_aviso_instrumental);
        musica = findViewById(R.id.btn_aviso_musicas);
        observacao = findViewById(R.id.edit_aviso_observacao);
        salvar = findViewById(R.id.btn_aviso_salvar);

    }

    private void corBotaoRoxo(final Button button) {

        button.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        button.setBackgroundResource(R.drawable.btn_roxo_transparente);
        button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    private void corBotaoVerde(final Button button) {

        button.setTextColor(getResources().getColor(R.color.green_400));
        button.setBackgroundResource(R.drawable.btn_verde_transparente);
        button.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_action_check), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Parametro.staticArrayVocal.size() == 0) {
            corBotaoRoxo(vocal);
        } else {
            corBotaoVerde(vocal);
        }

        if (Parametro.staticArrayInstrumental.size() == 0) {
            corBotaoRoxo(instrumental);
        } else {
            corBotaoVerde(instrumental);
        }

        if (Parametro.staticArrayMusica.size() == 0) {
            corBotaoRoxo(musica);
        } else {
            corBotaoVerde(musica);
        }

        if (Parametro.staticArrayMinistrante.size() == 0) {

            corBotaoRoxo(ministrante);
        } else {
            corBotaoVerde(ministrante);
        }

        if (culto.getText().toString().equals("Culto")) {
            corBotaoRoxo(culto);
        } else {
            corBotaoVerde(culto);
        }

    }

    private void setDateTimeField() {

        final Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Calendar newDate = Calendar.getInstance();
                //newDate.set(year, monthOfYear, dayOfMonth);
                // culto.setText(dateFormatter.format(newDate.getTime()));

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                long date_ship_millis = calendar.getTimeInMillis();
                culto.setText(Tools.getFormattedDateSimple(date_ship_millis));
                corBotaoVerde(culto);


            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    private void salvarProduto() {

        Progresso.progressoCircular(this);
        Cronograma cronograma = new Cronograma();
        cronograma.vocal = new ArrayList<>();
        cronograma.instrumental = new ArrayList<>();
        cronograma.musicas = new ArrayList<>();
        cronograma.diaDoCulto = culto.getText().toString();
        cronograma.ministrante = Parametro.staticArrayMinistrante.get(0);
        cronograma.vocal.addAll(Parametro.staticArrayVocal);
        cronograma.instrumental.addAll(Parametro.staticArrayInstrumental);
        cronograma.musicas.addAll(Parametro.staticArrayMusica);
        cronograma.observacao = observacao.getText().toString();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference().child(Constantes.CRONOGRAMA);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        cronograma.id = tipo;
        databaseReference.child(cronograma.id).setValue(cronograma).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                enviarNotificacao();
                Progresso.dialog.dismiss();
                Parametro.staticArrayMinistrante.clear();
                Parametro.staticArrayVocal.clear();
                Parametro.staticArrayInstrumental.clear();
                Parametro.staticArrayMusica.clear();
                Intent intent =  new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private boolean validarCampos() {
        boolean a;

        if (culto.getText().toString().equals("Culto")) {
            Toast.makeText(this, "Falta escolher o dia do culto ", Toast.LENGTH_SHORT).show();


            a = false;
            return a;
        } else {

            a = true;
        }
        if (Parametro.staticArrayMinistrante.size() == 0) {
            a = false;
            Toast.makeText(this, "Falta escolher um Ministrante", Toast.LENGTH_SHORT).show();

            return a;

        } else {
            a = true;

        }

        if (Parametro.staticArrayVocal.size() == 0) {
            Toast.makeText(this, "Falta escolher o vocal ", Toast.LENGTH_SHORT).show();
            a = false;
            return a;
        } else {

            a = true;

        }

        if (Parametro.staticArrayInstrumental.size() == 0) {
            Toast.makeText(this, "Falta escolher o instrumental ", Toast.LENGTH_SHORT).show();
            a = false;
            return a;
        } else {

            a = true;


        }



        if (Parametro.staticArrayMusica.size() == 0) {
            Toast.makeText(this, "Falta escolher os hinos ", Toast.LENGTH_SHORT).show();
            a = false;
            return a;
        } else {

            a = true;
        }


        return a;


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void enviarNotificacao() {

        String tokenAluno = "cvnzDymYyBU:APA91bHNpn9T1RfLK6oSg00onzQIMNh3iJ6BRN-Q9mlo62d1kDrJ-oFVEZecHB4I8oRGZxC9AgHqjuqJx5V0R7Jtq_W9sAj9C8W7ujxLQNV2IYGyvvlxs3usfE5YUst2-qHi14Vr3JMf";
        String tokenJamilton = "fBwBVkoic1E:APA91bEUkRNc200w1Xx9Wwj1-ULjFa_WRyskcdlZJYbVuF8ThxYDFWNKKFfCFJkaaq-ydl3sS-6QmP4uNAIkITQjxqLZwmzghtXJAWmRxN_oaJNJFKmA53fgeXliHTEaaBxPWS_uu32m";

        String to = "";//Tópico ou token
        // to = tokenJamilton;
        //to = tokenAluno;
        to = "/topics/todos";

        //Monta objeto notificação
        Notificacao notificacao = new Notificacao("Título da notificação!!", "Corpo da notificação");
        NotificacaoDados notificacaoDados = new NotificacaoDados(to, notificacao);

        NotificacaoService service = retrofit.create(NotificacaoService.class);
        Call<NotificacaoDados> call = service.salvarNotificacao(notificacaoDados);

        call.enqueue(new Callback<NotificacaoDados>() {
            @Override
            public void onResponse(Call<NotificacaoDados> call, Response<NotificacaoDados> response) {


                if (response.isSuccessful()) {

                    Toast.makeText(getApplicationContext(),
                            "codigo: " + response.code(),
                            Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<NotificacaoDados> call, Throwable t) {

            }
        });


    }

    public void recuperarToken() {

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                String token = instanceIdResult.getToken();
                Log.i("getInstanceId", "token getInstanceId: " + token);

            }
        });

    }
}
