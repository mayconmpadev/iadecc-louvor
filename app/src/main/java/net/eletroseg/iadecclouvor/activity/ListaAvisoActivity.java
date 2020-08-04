package net.eletroseg.iadecclouvor.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.adapter.AdapterListaAviso;
import net.eletroseg.iadecclouvor.modelo.Avisos;
import net.eletroseg.iadecclouvor.modelo.Hino;
import net.eletroseg.iadecclouvor.util.Constantes;
import net.eletroseg.iadecclouvor.util.InstanciaFirebase;
import net.eletroseg.iadecclouvor.util.Progresso;
import net.eletroseg.iadecclouvor.util.SPM;
import net.eletroseg.iadecclouvor.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListaAvisoActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ImageView voltar;


    private AdapterListaAviso mAdapter;
    ArrayList<Avisos> arrayListHino = new ArrayList<>();
    String sPesquisa = "";
    private SPM spm = new SPM(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_aviso);
        iniciarComponentes();
        buscarClienteWeb();
        if (spm.getPreferencia("USUARIO_LOGADO", "MODERADOR", "").equals("sim")) {
            fab.show();
        } else {

            fab.hide();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), CadastroAvisoActivity.class);
                startActivity(intent);


            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void iniciarComponentes() {
        recyclerView = findViewById(R.id.recycler_lista_aviso);
        fab = findViewById(R.id.fab_lista_aviso);
        voltar = findViewById(R.id.iv_avisos_voltar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        mAdapter = new AdapterListaAviso(this, arrayListHino);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new AdapterListaAviso.OnClickListener() {
            @Override
            public void onItemClick(View view, Avisos obj, int pos) {

            }

            @Override
            public void onItemLongClick(View view, Avisos objeto, int pos) {
                if (spm.getPreferencia("USUARIO_LOGADO", "MODERADOR", "").equals("sim")) {

                    dialogEditar(objeto);

                } else {
                    Toast.makeText(getApplicationContext(), "Essa função é só para moderadores", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void dialogEditar(final Avisos avisos) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_padrao_ok_cancelar);

        Button ok = dialog.findViewById(R.id.dialog_padrao_btn_direita);
        Button cancelar = dialog.findViewById(R.id.dialog_padrao_btn_esquerda);
        TextView titulo = dialog.findViewById(R.id.dialog_padrao_text_titulo);
        TextView msg = dialog.findViewById(R.id.dialog_padrao_text_msg);
        titulo.setText("Excluir");
        Util.textoNegrito("Deseja excluir o aviso *" + avisos.titulo + "*?", msg, null);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                excluirAviso(avisos.id);
                dialog.dismiss();
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void editar(Hino obj) {
        Intent intent = new Intent(getApplicationContext(), CadastroAvisoActivity.class);
        intent.putExtra("hino", obj);
        startActivity(intent);
    }

    //---------------------------------------------------- BUSCA OS DADOS NO FIREBASE -----------------------------------------------------------------

    private void buscarClienteWeb() {
        Progresso.progressoCircular(this);
        arrayListHino.clear();
        FirebaseDatabase database = InstanciaFirebase.getDatabase();
        DatabaseReference reference = database.getReference().child(Constantes.AVISOS);
        reference.keepSynced(true);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final Avisos avisos = dataSnapshot.getValue(Avisos.class);
                // if (!hino.id.equals("master")) {
                arrayListHino.add(avisos);
                //  }
                mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Avisos avisos = dataSnapshot.getValue(Avisos.class);
                for (int i = 0; i < arrayListHino.size(); i++) {
                    if (arrayListHino.get(i).titulo.equals(avisos.titulo)) {
                        arrayListHino.remove(i);
                    }
                }
                arrayListHino.add(avisos);
                ordenaPorNumero(arrayListHino);
                mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Avisos avisos = dataSnapshot.getValue(Avisos.class);
                for (int i = 0; i < arrayListHino.size(); i++) {
                    if (arrayListHino.get(i).titulo.equals(avisos.titulo)) {
                        arrayListHino.remove(i);
                    }
                }
                if (!sPesquisa.equals("")) {
                    recyclerView.setAdapter(mAdapter);
                    // mAdapter.getFilter().filter(sPesquisa);

                }
                ordenaPorNumero(arrayListHino);

                mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                ordenaPorNumero(arrayListHino);
                mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ordenaPorNumero(arrayListHino);
                mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void excluirAviso(String id) {
        FirebaseDatabase database = InstanciaFirebase.getDatabase();
        DatabaseReference reference = database.getReference().child(Constantes.AVISOS).child(id);
        reference.removeValue();
    }

    //---------------------------------------------------- ORDENA A LISTA EM ORDEM ALFABETICA ----------------------------------------------

    private static void ordenaPorNumero(ArrayList<Avisos> lista) {
        Collections.sort(lista, new Comparator<Avisos>() {
            @Override
            public int compare(Avisos o1, Avisos o2) {
                return o2.data.toLowerCase().compareTo(o1.data.toLowerCase());
            }
        });
    }


}
