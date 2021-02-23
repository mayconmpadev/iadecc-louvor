package net.eletroseg.iadecclouvor.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.util.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CadastroEscalaMesActivity extends AppCompatActivity {
    private Button mesEcala, integrantes, salvar;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private int qtdDias = 0;
    private int mes = 0;
    private int ano = 0;
    ArrayList<Integer> domingos = new ArrayList<>();
    ArrayList<Integer> quartas = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_escala_mes);
        iniciarComponentes();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        mesEcala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateTimeField();
                fromDatePickerDialog.show();

            }
        });

        integrantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EscalaMesActivity.class);
                intent.putExtra("domingo", domingos);
                intent.putExtra("quarta", quartas);
                startActivity(intent);
            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void iniciarComponentes() {
        mesEcala = findViewById(R.id.btn_nova_escala_mes);
        integrantes = findViewById(R.id.btn_escala);
        salvar = findViewById(R.id.btn_escala_mes_salvar);


    }

    private void setDateTimeField() {

        final Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                view.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
                long date_ship_millis = calendar.getTimeInMillis();
                qtdDias = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                if (day == Calendar.SUNDAY) {
                    Toast.makeText(CadastroEscalaMesActivity.this, "é domingo" + dayOfMonth, Toast.LENGTH_SHORT).show();
                }
                mesEcala.setText(Tools.getFormattedDateSimple2(date_ship_millis));

                corBotaoVerde(mesEcala);
                mes = monthOfYear;
                ano = year;
                qtdLinhas();

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    private void qtdLinhas() {


        for (int i = 1; i <= qtdDias; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, ano);
            calendar.set(Calendar.MONTH, mes);
            calendar.set(Calendar.DAY_OF_MONTH, i);
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            if (day == Calendar.SUNDAY) {
                domingos.add(i);
            }else if(day == Calendar.WEDNESDAY){
                quartas.add(i);
            }
        }
        Toast.makeText(this, String.valueOf(quartas.size()), Toast.LENGTH_SHORT).show();
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
}