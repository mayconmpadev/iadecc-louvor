package net.eletroseg.iadecclouvor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import net.eletroseg.iadecclouvor.R;


public class EstatisticaActivity extends AppCompatActivity {
private LinearLayout linearMinistrante, linearVocal, linearInstrumental, linearHino;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estatistica);
        linearMinistrante = findViewById(R.id.linear_ministrante_estatistica);
        linearVocal = findViewById(R.id.linear_vocal_estatisca);
        linearInstrumental = findViewById(R.id.linear_instrumental_estatistica);
        linearHino = findViewById(R.id.linear_hinos_estatistica);


        linearMinistrante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MinistranteEstatisticaActivity.class);
                startActivity(intent);
            }
        });

        linearVocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), VocalEstatisticaActivity.class);
                startActivity(intent);
            }
        });

        linearInstrumental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InstrumentalEstatisticaActivity.class);
                startActivity(intent);
            }
        });

        linearHino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HinoEstatisticaActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}