package net.eletroseg.iadecclouvor.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.adapter.AdapterListaHino;
import net.eletroseg.iadecclouvor.adapter.AdapterListaUsuario;
import net.eletroseg.iadecclouvor.modelo.Usuario;
import net.eletroseg.iadecclouvor.util.Base64Custom;
import net.eletroseg.iadecclouvor.util.Constantes;
import net.eletroseg.iadecclouvor.util.MusicUtils;
import net.eletroseg.iadecclouvor.util.Parametro;
import net.eletroseg.iadecclouvor.util.Progresso;
import net.eletroseg.iadecclouvor.util.SPM;
import net.eletroseg.iadecclouvor.util.Util;
import net.eletroseg.iadecclouvor.widget.LineItemDecoration;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListaUsuarioActivity extends AppCompatActivity {


    private EditText editLetra;
    private ImageView voltar, apagar;


    private RecyclerView recyclerView;
    private LinearLayout principal;

    private FloatingActionButton fab;
    private SPM spm = new SPM(ListaUsuarioActivity.this);
    private AdapterListaUsuario mAdapter;
    private ActionMode actionMode;
    Usuario hino2;


    ArrayList<Usuario> arrayListHino = new ArrayList<>();
    String sPesquisa = "";
    Usuario obj;




    int cont = 0;
    ArrayList<String> arrayList = new ArrayList<>();
    boolean bRepetir = false;
    boolean bControles = true;

    // Media Player

    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();

    //private SongsManager songManager;
    private MusicUtils utils;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_usuario);
//        getSupportActionBar().hide();
        iniciarComponentes();
if (spm.getPreferencia("USUARIO_LOGADO", "MODERADOR", "não").equals("não")){
    fab.setVisibility(View.GONE);
        }
        buscarClienteWeb();
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.notifyDataSetChanged();

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
                Intent intent = new Intent(getApplicationContext(), CadastroLoginActivity.class);
                startActivity(intent);
            }
        });



    }

    private void iniciarComponentes() {
        editLetra = findViewById(R.id.edit_pesquisar);
        voltar = findViewById(R.id.iv_voltar);
        apagar = findViewById(R.id.iv_pesquisar);
        fab = findViewById(R.id.fab);
        principal = findViewById(R.id.root);
        recyclerView = findViewById(R.id.recycler_usuario);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        mAdapter = new AdapterListaUsuario(this, arrayListHino);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new AdapterListaUsuario.OnClickListener() {
            @Override
            public void onItemClick(View view, Usuario objeto, int pos) {
                if (objeto.id.equals(spm.getPreferencia("USUARIO_LOGADO", "USUARIO",""))){
                    Intent intent = new Intent(getApplicationContext(), FotoActivity.class);
                    startActivity(intent);
                }else {
                    dialogPerfil(objeto);
                }


            }

            @Override
            public void onItemLongClick(View view, Usuario objeto, int pos) {
                enableActionMode(pos);
                obj = objeto;
                dialogEditar(objeto.nome);
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

    private void deleteInboxes() {
        List<Integer> selectedItemPositions = mAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mAdapter.removeData(selectedItemPositions.get(i));
        }
        mAdapter.notifyDataSetChanged();
    }

    //---------------------------------------------------- BUSCA OS DADOS NO FIREBASE -----------------------------------------------------------------

    private void buscarClienteWeb() {
        Progresso.progressoCircular(this);
        arrayListHino.clear();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child(Constantes.USUARIOS);
        reference.keepSynced(true);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final Usuario usuario = dataSnapshot.getValue(Usuario.class);
                // if (!hino.id.equals("master")) {
                arrayListHino.add(usuario);
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

    private void dialogEditar(final String nome) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_opcao_edit);
        final LinearLayout ouvir = dialog.findViewById(R.id.ll_enviar);
        LinearLayout ouvirComLetra = dialog.findViewById(R.id.ll_status);
        LinearLayout ouvirComCifra = dialog.findViewById(R.id.ll_pagamento);
        LinearLayout adicionarPlayList = dialog.findViewById(R.id.ll_pdf);

        ouvir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editar(obj);
                dialog.dismiss();
            }
        });

        ouvirComLetra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogExcluir("Excluir", "Deseja excluir o hino *" + nome + "*?", nome);
                dialog.dismiss();

            }
        });


        dialog.show();
    }

    private void dialogPerfil(Usuario usuario) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_light);
        CircleImageView foto = dialog.findViewById(R.id.image_perfil);
        TextView nome = dialog.findViewById(R.id.text_perfil_nome);
        TextView email = dialog.findViewById(R.id.text_perfil_email);
        TextView telefone = dialog.findViewById(R.id.text_perfil_telefone);
        TextView voz = dialog.findViewById(R.id.text_perfil_voz);
        AppCompatButton ligar = dialog.findViewById(R.id.btn_perfil_ligar);
        ImageButton fechar = dialog.findViewById(R.id.btn_perfil_fechar);
        nome.setText(usuario.nome);
        email.setText(usuario.email);
        telefone.setText(usuario.telefone);
        Glide.with(this).load(usuario.foto).into(foto);

        fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });



        dialog.show();
    }

    private void editar(Usuario obj) {
        Intent intent = new Intent(getApplicationContext(), CadastrarLetraActivity.class);
        intent.putExtra("hino", obj);
        startActivity(intent);
    }

    private boolean toggleButtonColor(ImageButton bt) {
        String selected = (String) bt.getTag(bt.getId());
        if (selected != null) { // selected
            bt.setColorFilter(getResources().getColor(R.color.grey_90), PorterDuff.Mode.SRC_ATOP);
            bt.setTag(bt.getId(), null);
            return false;
        } else {
            bt.setTag(bt.getId(), "selected");
            bt.setColorFilter(getResources().getColor(R.color.red_500), PorterDuff.Mode.SRC_ATOP);
            return true;
        }
    }

    private void excluirUsuario(String nome) {
        Progresso.progressoCircular(this);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final StorageReference storageReferencere = storage.getReference().child(Constantes.AUDIO)
                .child(Base64Custom.codificarBase64(nome));
        DatabaseReference reference = database.getReference().child(Constantes.HINO)
                .child(Base64Custom.codificarBase64(nome));
        reference.removeValue();
        storageReferencere.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Progresso.dialog.dismiss();

            }
        });
    }

    private void dialogExcluir(final String sTitulo, String menssagem, final String nome) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_padrao_ok_cancelar);
        //instancia os objetos que estão no layout customdialog.xml
        final TextView titulo = dialog.findViewById(R.id.dialog_padrao_text_titulo);
        final TextView msg = dialog.findViewById(R.id.dialog_padrao_text_msg);
        final Button cancelar = dialog.findViewById(R.id.dialog_padrao_btn_esquerda);
        final Button ok = dialog.findViewById(R.id.dialog_padrao_btn_direita);
        final LinearLayout layout = dialog.findViewById(R.id.root);
        layout.setVisibility(View.VISIBLE);


        Util.textoNegrito(menssagem, msg, null);
        titulo.setText(sTitulo);


        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excluirUsuario(nome);
                dialog.dismiss();
            }
        });

        //exibe na tela o dialog
        dialog.show();

    }




    @Override
    public void onBackPressed() {

        finish();
    }


}
