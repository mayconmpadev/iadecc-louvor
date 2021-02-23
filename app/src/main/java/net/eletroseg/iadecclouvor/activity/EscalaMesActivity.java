package net.eletroseg.iadecclouvor.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.adapter.AdapterEscalaMes;
import net.eletroseg.iadecclouvor.adapter.AdapterListaAviso;
import net.eletroseg.iadecclouvor.modelo.Avisos;
import net.eletroseg.iadecclouvor.modelo.EscalaMes;
import net.eletroseg.iadecclouvor.modelo.Usuario;

import java.util.ArrayList;

public class EscalaMesActivity extends AppCompatActivity {
    RecyclerView recyclerViewEbd, recyclerviewDomingo, recyclerviewQuarta;
    ArrayList<Integer> domingos = new ArrayList<>();
    ArrayList<Integer> quartas = new ArrayList<>();
    private AdapterEscalaMes mAdapter;
    ArrayList<EscalaMes> arrayListHino = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escala_mes);
        recyclerViewEbd = findViewById(R.id.recycler_escala_ebd);
        recyclerviewDomingo = findViewById(R.id.recycler_escala_domingo);
        recyclerviewQuarta = findViewById(R.id.recycler_escala_quarta);
        recuperarIntent();
        buscarEscala();
        recyclerViewEbd.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEbd.setHasFixedSize(true);

        //set data and list adapter
        mAdapter = new AdapterEscalaMes(this, arrayListHino);
        recyclerViewEbd.setAdapter(mAdapter);
    }

    private void recuperarIntent() {


        Intent intent = getIntent();
        domingos = (ArrayList<Integer>) intent.getSerializableExtra("domingo");
        quartas = (ArrayList<Integer>) intent.getSerializableExtra("quarta");


    }
    private void buscarEscala(){
        for (int i = 0; i < domingos.size(); i++) {
            EscalaMes escalaMes = new EscalaMes();
            Usuario usuario = new Usuario();
            escalaMes.diaDoCulto = String.valueOf(domingos.get(i));
            escalaMes.ministrante = "teste";

            usuario.nome = "maycon";
            usuario.email = "maycon";
            usuario.foto = "maycon";
            usuario.nome = "maycon";
            escalaMes.instrumental.add(usuario);
            Usuario usuario1 = new Usuario();
            usuario.nome = "sanlay";
            escalaMes.instrumental.add(usuario1);
            Usuario usuario2 = new Usuario();
            usuario.nome = "ney";
            escalaMes.instrumental.add(usuario2);
            Usuario usuario3 = new Usuario();
            usuario.nome = "kenayt";
            escalaMes.instrumental.add(usuario3);
            arrayListHino.add(escalaMes);

        }
    }


}
