package net.eletroseg.iadecclouvor.activity;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import net.eletroseg.iadecclouvor.modelo.Hino;
import net.eletroseg.iadecclouvor.util.Base64Custom;
import net.eletroseg.iadecclouvor.util.Constantes;
import net.eletroseg.iadecclouvor.util.InstanciaFirebase;
import net.eletroseg.iadecclouvor.util.MusicUtils;
import net.eletroseg.iadecclouvor.util.Parametro;
import net.eletroseg.iadecclouvor.util.Progresso;
import net.eletroseg.iadecclouvor.util.SPM;
import net.eletroseg.iadecclouvor.util.Util;
import net.eletroseg.iadecclouvor.widget.LineItemDecoration;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListaHinosActivity extends AppCompatActivity {


    private EditText editLetra;
    private ImageView voltar, apagar;
    private ImageButton prev, next, repetir, aleatorio;
    private TextView tempoInicial, tempoFinal;
    private RecyclerView recyclerView;
    private LinearLayout principal;
    private AppCompatSeekBar seek_song_progressbar;
    private FloatingActionButton fab, play;
    private SPM spm = new SPM(ListaHinosActivity.this);
    private AdapterListaHino mAdapter;
    private ActionMode actionMode;
    Hino hino2;
    MediaPlayer mp;
    ObjDld objDld;
    ArrayList<Hino> arrayListHino = new ArrayList<>();
    ArrayList<Hino> arrayListPlayList = new ArrayList<>();
    String sPesquisa = "";
    Hino obj;
    ArrayList<ObjDld> position = new ArrayList<>();
    File myFile;
    File pdfFolder;
    DownloadManager dm;
    int cont = 0;
    int cont2 = 0;
    ArrayList<String> arrayList = new ArrayList<>();
    boolean bRepetir = false;
    private Handler mHandler = new Handler();
    private MusicUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_hinos);
//        getSupportActionBar().hide();
        utils = new MusicUtils();
        iniciarComponentes();
        recuperaIntent();

        if (spm.getPreferencia("USUARIO_LOGADO", "MODERADOR", "").equals("sim")) {
            fab.show();
        } else {
            fab.hide();
        }

        if (Parametro.sTipo.equals("lista")) {
            arrayListHino.addAll(arrayListPlayList);
            if (spm.getPreferencia("USUARIO_LOGADO", "MODERADOR", "").equals("sim")) {
                fab.show();
            } else {
                fab.hide();
            }
        } else {
            buscarClienteWeb();
        }

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
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
                mp.stop();
                Intent intent = new Intent(getApplicationContext(), CadastrarLetraActivity.class);
                startActivity(intent);

            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificarNaMemoria(arrayListHino.get(cont).nome)) {
                    ouvir();
                } else {
                    Toast.makeText(ListaHinosActivity.this, "clique no hino para fazer o download", Toast.LENGTH_SHORT).show();
                }

            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cont == 0) {

                    cont = arrayListHino.size() - 1;

                    mAdapter.hinoEmExecucao(cont);
                    mAdapter.notifyDataSetChanged();
                    if (verificarNaMemoria(arrayListHino.get(cont).nome)) {
                        mp.reset();
                        ouvir();
                    } else {
                        Toast.makeText(ListaHinosActivity.this, "clique no hino para fazer o download", Toast.LENGTH_SHORT).show();
                    }
                } else {


                    cont--;
                    mAdapter.hinoEmExecucao(cont);
                    mAdapter.notifyDataSetChanged();
                    if (verificarNaMemoria(arrayListHino.get(cont).nome)) {
                        mp.reset();
                        ouvir();
                    } else {
                        Toast.makeText(ListaHinosActivity.this, "clique no hino para fazer o download", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cont < arrayListHino.size() - 1) {

                    cont++;
                    mAdapter.hinoEmExecucao(cont);
                    mAdapter.notifyDataSetChanged();
                    if (verificarNaMemoria(arrayListHino.get(cont).nome)) {
                        mp.reset();
                        ouvir();
                    } else {
                        Toast.makeText(ListaHinosActivity.this, "clique no hino para fazer o download", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    cont = 0;
                    mAdapter.hinoEmExecucao(cont);
                    mAdapter.notifyDataSetChanged();
                    if (verificarNaMemoria(arrayListHino.get(cont).nome)) {
                        mp.reset();
                        ouvir();
                    } else {
                        Toast.makeText(ListaHinosActivity.this, "clique no hino para fazer o download", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        repetir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleButtonColor((ImageButton) view);
                //Snackbar.make(parent_view, "Repeat", Snackbar.LENGTH_SHORT).show();
                bRepetir = !bRepetir;
            }
        });

        aleatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleButtonColor((ImageButton) view);
            }
        });


    }

    private void iniciarComponentes() {
        editLetra = findViewById(R.id.edit_pesquisar);
        voltar = findViewById(R.id.iv_voltar);
        apagar = findViewById(R.id.iv_pesquisar);
        fab = findViewById(R.id.fab);
        play = findViewById(R.id.bt_play);
        prev = findViewById(R.id.bt_prev);
        next = findViewById(R.id.bt_next);
        repetir = findViewById(R.id.bt_repeat);
        aleatorio = findViewById(R.id.bt_aleatorio);
        tempoInicial = findViewById(R.id.tv_song_current_duration);
        tempoFinal = findViewById(R.id.tv_song_total_duration);
        principal = findViewById(R.id.root);
        seek_song_progressbar = (AppCompatSeekBar) findViewById(R.id.seek_song_progressbar);
        // set Progress bar values
        seek_song_progressbar.setProgress(0);
        seek_song_progressbar.setMax(MusicUtils.MAX_PROGRESS);
        recyclerView = findViewById(R.id.recycler_hino);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new LineItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        mAdapter = new AdapterListaHino(this, arrayListHino);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new AdapterListaHino.OnClickListener() {
            @Override
            public void onItemClick(View view, Hino objeto, int pos) {
                obj = objeto;
                cont2 = pos;
                Parametro.nome = obj.nome;
                if (mAdapter.getSelectedItemCount() > 0) {
                    enableActionMode(pos);
                } else {
                    // read the inbox which removes bold from the row
                    Hino inbox = mAdapter.getItem(pos);

                    if (verificarNaMemoria(Parametro.nome)) {
                        dialogOpcao();
                    } else {

                        objDld = new ObjDld();
                        objDld.nomeDoHino = obj.nome;
                        objDld.posicao = pos;
                        position.add(objDld);
                        hino2 = obj;
                        downloadfile(obj);

                    }


                }
            }

            @Override
            public void onItemLongClick(View view, Hino objeto, int pos) {
                if (spm.getPreferencia("USUARIO_LOGADO", "MODERADOR", "").equals("sim")) {
                    enableActionMode(pos);
                    obj = objeto;
                    dialogEditar(objeto.nome);
                    cont = pos;
                    mAdapter.hinoEmExecucao(cont);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ListaHinosActivity.this, "Essa função é só para moderadores", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mp = new MediaPlayer();
    }

    private void recuperaIntent() {
        Intent intent = getIntent();
        Parametro.sTipo = intent.getStringExtra("tipo");
        arrayListPlayList = (ArrayList<Hino>) getIntent().getSerializableExtra("playlist");

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
        DatabaseReference reference = database.getReference().child(Constantes.HINO);
        //reference.keepSynced(true);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final Hino hino = dataSnapshot.getValue(Hino.class);
                // if (!hino.id.equals("master")) {
                arrayListHino.add(hino);
                //  }
                mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Hino produto = dataSnapshot.getValue(Hino.class);
                for (int i = 0; i < arrayListHino.size(); i++) {
                    if (arrayListHino.get(i).nome.equals(produto.nome)) {
                        arrayListHino.remove(i);
                    }
                }
                arrayListHino.add(produto);
                ordenaPorNumero(arrayListHino);
                mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Hino produto = dataSnapshot.getValue(Hino.class);
                for (int i = 0; i < arrayListHino.size(); i++) {
                    if (arrayListHino.get(i).nome.equals(produto.nome)) {
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

    private static void ordenaPorNumero(ArrayList<Hino> lista) {
        Collections.sort(lista, new Comparator<Hino>() {
            @Override
            public int compare(Hino o1, Hino o2) {
                return o1.nome.toLowerCase().compareTo(o2.nome.toLowerCase());
            }
        });
    }

    private void downloadfile(final Hino hino) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(hino.audioHino);


        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                donwloadFiles(hino.nome, url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    BroadcastReceiver onCompleteVisualizar = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {

            String filename = "";
            Bundle extras = intent.getExtras();
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
            Cursor c = dm.query(q);

            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    String filePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
                    filename = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.length());
                    filename = filename.replace(".mp3", "");
                    Toast.makeText(ctxt, filename, Toast.LENGTH_SHORT).show();
                }
            }
            c.close();


            for (int i = 0; i < position.size(); i++) {
                if (filename.equals(position.get(i).nomeDoHino)) {
                    mAdapter.notifyItemChanged(position.get(i).posicao);
                    break;
                }
            }

        }
    };

    private void donwloadFiles(String fileNome, String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);






        File pdfFolder = new File(this.getExternalFilesDir(null)
                + File.separator
                + "Iadecc/hinos"
                + File.separator);
        if (!pdfFolder.exists()) {
            pdfFolder.mkdirs();
        }
        myFile = new File(pdfFolder + File.separator + fileNome + ".mp3");
        if (myFile.exists()) {
            myFile.delete();
        }
        Uri.fromFile(myFile);

        request.setDestinationUri(Uri.fromFile(myFile));
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(request);

        registerReceiver(onCompleteVisualizar, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    private boolean verificarNaMemoria(String nome) {
        pdfFolder = new File(this.getExternalFilesDir(null)
                + File.separator
                + "Iadecc/hinos"
                + File.separator);
        if (!pdfFolder.exists()) {
            pdfFolder.mkdirs();
        }
        File myFile = new File(pdfFolder + File.separator + nome + ".mp3");
        if (myFile.exists()) {
            return true;
        } else {
            return false;
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
cont = cont2;
                ouvir();
                dialog.dismiss();
            }
        });

        ouvirComLetra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cont = cont2;
                mAdapter.hinoEmExecucao(cont);
                mAdapter.notifyDataSetChanged();
                if (mp.isPlaying()) {
                    mp.pause();
                }


                Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                intent.putExtra("hino", obj);
                intent.putExtra("tipo", "letra");
                startActivity(intent);
                dialog.dismiss();

            }
        });

        ouvirComCifra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                intent.putExtra("hino", obj);
                intent.putExtra("tipo", "cifra");
                startActivity(intent);
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

    private void editar(Hino obj) {
        Intent intent = new Intent(getApplicationContext(), CadastrarLetraActivity.class);
        intent.putExtra("hino", obj);
        startActivity(intent);
    }

    private void ouvir() {
        if (mp == null) {
            mp = new MediaPlayer();
            mp.reset();
        } else {
            mp.reset();
        }

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!bRepetir) {

                    cont++;
                    mAdapter.hinoEmExecucao(cont);
                    mAdapter.notifyDataSetChanged();

                }

                if (cont < arrayListHino.size()) {

                    Parametro.nome = arrayListHino.get(cont).nome;
                    Toast.makeText(ListaHinosActivity.this, Parametro.nome, Toast.LENGTH_SHORT).show();
                    mp.seekTo(0);
                    mp.reset();
                    play();

                } else {
                    if (bRepetir) {

                        cont = 0;
                        mAdapter.hinoEmExecucao(cont);
                        mAdapter.notifyDataSetChanged();

                        mp.stop();
                        play();
                    } else {
                        play.setImageResource(R.drawable.ic_play_arrow);
                    }

                }


            }
        });
        if (mp.isPlaying()) {
            mp.release();
        }
        play();


        // Listeners
        seek_song_progressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // remove message Handler from updating progress bar
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mp.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                mp.seekTo(currentPosition);

                // update timer progress again
                mHandler.post(mUpdateTimeTask);
            }
        });
        buttonPlayerAction();
        updateTimerAndSeekbar();
    }

    /**
     * Play button click event plays a song and changes button to pause image
     * pauses a song and changes button to play image
     */
    private void buttonPlayerAction() {
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (mp.isPlaying()) {
                    mp.pause();
                    // Changing button image to play button
                    play.setImageResource(R.drawable.ic_play_arrow);
                } else {
                    // Resume song
                    mp.start();
                    // Changing button image to pause button
                    play.setImageResource(R.drawable.ic_pause);
                    // Updating progress bar
                    mHandler.post(mUpdateTimeTask);
                }

            }
        });
    }
    private void play() {

        try {
            FileDescriptor fd = null;

            android.os.Environment.getExternalStorageDirectory();

            File baseDir = this.getExternalFilesDir(null);
            String audioPath = baseDir.getAbsolutePath() + File.separator + "Iadecc/hinos" + File.separator + arrayListHino.get(cont).nome + ".mp3";
            FileInputStream fis = new FileInputStream(audioPath);
            fd = fis.getFD();


            if (fd != null) {

                mp.setDataSource(fd);
                mp.prepare();
                mp.start();
                mAdapter.hinoEmExecucao(cont);
                mAdapter.notifyDataSetChanged();
                // Changing button image to pause button
                play.setImageResource(R.drawable.ic_pause);
                // Updating progress bar
                mHandler.post(mUpdateTimeTask);

            }
        } catch (Exception e) {
            Snackbar.make(principal, "Cannot load audio file", Snackbar.LENGTH_SHORT).show();
        }
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

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            updateTimerAndSeekbar();
            // Running this thread after 10 milliseconds
            if (mp.isPlaying()) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    private void updateTimerAndSeekbar() {
        long totalDuration = mp.getDuration();
        long currentDuration = mp.getCurrentPosition();

        // Displaying Total Duration time
        tempoFinal.setText(utils.milliSecondsToTimer(totalDuration));
        // Displaying time completed playing
        tempoInicial.setText(utils.milliSecondsToTimer(currentDuration));

        // Updating progress bar
        int progress = (int) (utils.getProgressSeekBar(currentDuration, totalDuration));
        seek_song_progressbar.setProgress(progress);
    }


    private void excluirUsuario(String nome) {
        Progresso.progressoCircular(this);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseDatabase database = InstanciaFirebase.getDatabase();

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
        editLetra.setText("");
        excluirHinoLocal(nome);


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

    public void excluirHinoLocal(String nome) {

        try {
            File pdfFolder = new File(this.getExternalFilesDir(null)
                    + File.separator
                    + "Iadecc/hinos"
                    + File.separator);
            if (!pdfFolder.exists()) {
                pdfFolder.mkdirs();
            }
            myFile = new File(pdfFolder + File.separator + nome + ".mp3");
            if (myFile.exists()) {

                myFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
        mp.stop();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public class ObjDld {

        public String nomeDoHino;
        public int posicao;

    }

}
