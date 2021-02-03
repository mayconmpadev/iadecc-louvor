package net.eletroseg.iadecclouvor.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.util.Tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CadastroEscalaMesActivity extends AppCompatActivity {
    private Button novaEscala, escala;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_escala_mes);
        iniciarComponentes();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        novaEscala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateTimeField();
                fromDatePickerDialog.show();
            }
        });
    }

    private void iniciarComponentes() {
        novaEscala = findViewById(R.id.btn_nova_escala_mes);
        escala = findViewById(R.id.btn_escala);


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
                view.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
                long date_ship_millis = calendar.getTimeInMillis();
                novaEscala.setText(Tools.getFormattedDateSimple(date_ship_millis));
                corBotaoVerde(novaEscala);


            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

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