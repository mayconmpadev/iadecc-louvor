package net.eletroseg.iadecclouvor.activity;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.adapter.AdapterSelecaoUsuario;
import net.eletroseg.iadecclouvor.modelo.Usuario;
import net.eletroseg.iadecclouvor.util.Constantes;
import net.eletroseg.iadecclouvor.util.InstanciaFirebase;
import net.eletroseg.iadecclouvor.util.Parametro;
import net.eletroseg.iadecclouvor.util.Progresso;
import net.eletroseg.iadecclouvor.util.SPM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SelecaoUsuarioActivity extends AppCompatActivity {
    private EditText editLetra;
    private ImageView voltar, apagar;
    private TextView quantidade;
    private RecyclerView recyclerView;
    private LinearLayout principal;
    private FloatingActionButton fab;
    private SPM spm = new SPM(this);
    private AdapterSelecaoUsuario mAdapter;
    private ActionMode actionMode;
    private String tipo;
    Query query;

    ArrayList<Usuario> arrayListHino = new ArrayList<>();
    public static ArrayList<String> arrayListIds = new ArrayList<>();
    String sPesquisa = "";
    Usuario obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecao_usuario);
        iniciarComponentes();
        recuperaIntent();
        buscarClienteWeb();
        selecionarTipoIds();

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        apagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editLetra.setText("");
            }
        });

        editLetra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selecionarTipoObjetos();
            }
        });


    }

    private void recuperaIntent() {

        Intent intent = getIntent();
        tipo = intent.getStringExtra("tipo");

    }

    private void iniciarComponentes() {
        editLetra = findViewById(R.id.edit_pesquisar_selecao_usuario);
        quantidade = findViewById(R.id.text_qtd_selecao_usuario);
        voltar = findViewById(R.id.iv_voltar_selecao_usuario);
        apagar = findViewById(R.id.iv_pesquisar_selecao_usuario);
        fab = findViewById(R.id.fab_selecao_usuario);
        principal = findViewById(R.id.root_selecao_usuario);
        recyclerView = findViewById(R.id.recycler_usuario_selecao_usuario);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        mAdapter = new AdapterSelecaoUsuario(this, arrayListHino);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new AdapterSelecaoUsuario.OnClickListener() {
            @Override
            public void onItemClick(View view, Usuario objeto, int pos) {
                if (!tipo.equals("ministrante")) {
                    if (arrayListIds.contains(objeto.id)) {
                        arrayListIds.remove(objeto.id);


                    } else {
                        arrayListIds.add(objeto.id);


                    }
                    quantidade.setText(String.valueOf(arrayListIds.size()));
                    mAdapter.notifyDataSetChanged();
                }else {
                    arrayListIds.clear();
                    arrayListIds.add(objeto.id);
                    quantidade.setText(String.valueOf(arrayListIds.size()));
                    mAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onItemLongClick(View view, Usuario objeto, int pos) {
                enableActionMode(pos);
                obj = objeto;
            }
        });
    }


    private void enableActionMode(int position) {
        if (actionMode == null) {
            // actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }


    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            //  actionMode.finish();
        } else {
            //  actionMode.setTitle(String.valueOf(count));
//            actionMode.invalidate();
        }
    }


    //---------------------------------------------------- BUSCA OS DADOS NO FIREBASE -----------------------------------------------------------------

    private void buscarClienteWeb() {
        Progresso.progressoCircular(this);
        arrayListHino.clear();
        FirebaseDatabase database = InstanciaFirebase.getDatabase();
        query  = database.getReference().child(Constantes.USUARIOS).orderByChild("status").equalTo("ativo");
      //  reference.keepSynced(true);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final Usuario usuario = dataSnapshot.getValue(Usuario.class);
                // if (!hino.id.equals("master")) {
                arrayListHino.add(usuario);
                //arrayListIds.add(usuario.id);
                //  }
                mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                for (int i = 0; i < arrayListHino.size(); i++) {
                    if (arrayListHino.get(i).nome.equals(usuario.nome)) {
                        arrayListHino.remove(i);
                    }
                }
                arrayListHino.add(usuario);
                ordenaPorNumero(arrayListHino);
                mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                for (int i = 0; i < arrayListHino.size(); i++) {
                    if (arrayListHino.get(i).nome.equals(usuario.nome)) {
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

        query.addValueEventListener(new ValueEventListener() {
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

    //---------------------------------------------------- ORDENA A LISTA EM ORDEM ALFABETICA ----------------------------------------------

    private static void ordenaPorNumero(ArrayList<Usuario> lista) {
        Collections.sort(lista, new Comparator<Usuario>() {
            @Override
            public int compare(Usuario o1, Usuario o2) {
                return o1.nome.toLowerCase().compareTo(o2.nome.toLowerCase());
            }
        });
    }

    private void play(String midia) {
        MediaPlayer mediaPlayer = new MediaPlayer();


        try {
            mediaPlayer.setDataSource(midia);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    mp.start();
                }

            });
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void dialogOpcao() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_opcao_play);
        final LinearLayout ouvir = dialog.findViewById(R.id.ll_enviar);
        LinearLayout ouvirComLetra = dialog.findViewById(R.id.ll_status);
        LinearLayout ouvirComCifra = dialog.findViewById(R.id.ll_pagamento);
        LinearLayout adicionarPlayList = dialog.findViewById(R.id.ll_pdf);

        ouvir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });


        ouvirComCifra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        adicionarPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    private void selecionarTipoIds() {
        switch (tipo) {
            case "vocal": {
                recuperarIds(Parametro.staticArrayVocal);
                break;
            }
            case "instrumental": {
                recuperarIds(Parametro.staticArrayInstrumental);
                break;
            }
            case "ministrante": {
                recuperarIds(Parametro.staticArrayMinistrante);
                break;
            }
        }
    }

    private void selecionarTipoObjetos() {
        switch (tipo) {
            case "vocal": {
                formarObjeto(Parametro.staticArrayVocal);
                break;
            }
            case "instrumental": {
                formarObjeto(Parametro.staticArrayInstrumental);
                break;
            }
            case "ministrante": {
                formarObjeto(Parametro.staticArrayMinistrante);
                break;
            }
        }
    }

    private void recuperarIds(ArrayList<Usuario> arrayList) {

        arrayListIds.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            arrayListIds.add(arrayList.get(i).id);
        }

        quantidade.setText(String.valueOf(Parametro.staticArrayVocal.size()));
        mAdapter.notifyDataSetChanged();
    }

    private void formarObjeto(ArrayList<Usuario> arrayList) {
        arrayList.clear();
        for (int j = 0; j < arrayListIds.size(); j++) {
            for (int i = 0; i < arrayListHino.size(); i++) {
                if (arrayListHino.get(i).id.equals(arrayListIds.get(j))) {
                    arrayList.add(arrayListHino.get(i));
                }

            }
        }
        finish();
    }


    @Override
    public void onBackPressed() {
        finish();
    }


}
