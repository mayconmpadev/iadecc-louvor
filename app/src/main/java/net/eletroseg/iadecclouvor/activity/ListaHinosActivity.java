package net.eletroseg.iadecclouvor.activity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.adapter.AdapterListInbox;
import net.eletroseg.iadecclouvor.adapter.LetraAdapter;
import net.eletroseg.iadecclouvor.modelo.Hino;
import net.eletroseg.iadecclouvor.util.Constantes;
import net.eletroseg.iadecclouvor.util.Parametro;
import net.eletroseg.iadecclouvor.util.Progresso;
import net.eletroseg.iadecclouvor.util.SPM;
import net.eletroseg.iadecclouvor.widget.LineItemDecoration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListaHinosActivity extends AppCompatActivity {


    private EditText editLetra;
    private ImageView voltar, apagar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ArrayList<Hino> arrayListHino = new ArrayList<Hino>();
    private SPM spm = new SPM(ListaHinosActivity.this);
    private AdapterListInbox mAdapter;
    private ActionMode actionMode;
    ArrayList<Hino> arrayListProduto = new ArrayList<>();
    String sPesquisa = "";
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_hinos);
//        getSupportActionBar().hide();
        iniciarComponentes();
        buscarClienteWeb();
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CadastrarLetraActivity.class);
                startActivity(intent);
            }
        });


    }

    private void iniciarComponentes() {
        editLetra = findViewById(R.id.edit_pesquisar);
        voltar = findViewById(R.id.iv_voltar);
        apagar = findViewById(R.id.iv_pesquisar);
        fab = findViewById(R.id.fab);
        webView = findViewById(R.id.webview);
        recyclerView = findViewById(R.id.recycler_hino);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new LineItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        mAdapter = new AdapterListInbox(this, arrayListProduto);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new AdapterListInbox.OnClickListener() {
            @Override
            public void onItemClick(View view, Hino obj, int pos) {
                if (mAdapter.getSelectedItemCount() > 0) {
                    enableActionMode(pos);
                } else {
                    // read the inbox which removes bold from the row
                    Hino inbox = mAdapter.getItem(pos);
                    Parametro.nome = obj.nome;
             Intent intent = new Intent(getApplicationContext(), PlayActivity.class);

             startActivity(intent);

                    //downloadfile(obj);
                }
            }

            @Override
            public void onItemLongClick(View view, Hino obj, int pos) {
                enableActionMode(pos);
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
        arrayListProduto.clear();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child(Constantes.HINO);
        reference.keepSynced(true);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final Hino hino = dataSnapshot.getValue(Hino.class);
               // if (!hino.id.equals("master")) {
                    arrayListProduto.add(hino);
              //  }
                mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Hino produto = dataSnapshot.getValue(Hino.class);
                for (int i = 0; i < arrayListProduto.size(); i++) {
                    if (arrayListProduto.get(i).nome.equals(produto.nome)) {
                        arrayListProduto.remove(i);
                    }
                }
                arrayListProduto.add(produto);
                ordenaPorNumero(arrayListProduto);
                mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Hino produto = dataSnapshot.getValue(Hino.class);
                for (int i = 0; i < arrayListProduto.size(); i++) {
                    if (arrayListProduto.get(i).nome.equals(produto.nome)) {
                        arrayListProduto.remove(i);
                    }
                }
                if (!sPesquisa.equals("")) {
                    recyclerView.setAdapter(mAdapter);
                   // mAdapter.getFilter().filter(sPesquisa);

                }
                ordenaPorNumero(arrayListProduto);

                mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                ordenaPorNumero(arrayListProduto);
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
                ordenaPorNumero(arrayListProduto);
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

    private void play(String midia){
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

    private void downloadfile(final Hino hino) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(hino.audioHino);


      storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
          @Override
          public void onSuccess(Uri uri) {
              String url = uri.toString();
              donwloadFiles(hino.nome,url);
          }
      }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {

          }
      });
    }

    private void exibirPDF(final int tipo, final Hino hino) {
        Progresso.progressoCircular(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(hino.audioHino);


        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                File pdfFolder = new File(android.os.Environment.getExternalStorageDirectory()
                        + File.separator
                        + "iadecc/hinos"
                        + File.separator);
                if (!pdfFolder.exists()) {
                    pdfFolder.mkdirs();
                }
                File myFile = new File(pdfFolder + File.separator + hino.nome + ".pdf");
                if (myFile.exists()) {
                    myFile.delete();
                }
                Uri.fromFile(myFile);

                request.setDestinationUri(Uri.fromFile(myFile));
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Progresso.dialog.dismiss();
             //  if (tipo == 1) {
               //    registerReceiver(onCompleteVisualizar, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
              //  }

            }
        });


    }

    BroadcastReceiver onCompleteVisualizar = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {

            File pdfFolder = new File(android.os.Environment.getExternalStorageDirectory()
                    + File.separator
                    + "iadecc/hinos"
                    + File.separator);
            if (!pdfFolder.exists()) {
                pdfFolder.mkdirs();
            }
            File myFile = new File(pdfFolder + File.separator + "Hh" + ".mp3");
            Uri uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getApplicationContext().getPackageName() + ".provider", myFile);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);
            Progresso.dialog.dismiss();
        }
    };

    private void donwloadFiles(String fileNome  , String url){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        File pdfFolder = new File(android.os.Environment.getExternalStorageDirectory()
                + File.separator
                + "Iadecc/hinos"
                + File.separator);
        if (!pdfFolder.exists()) {
            pdfFolder.mkdirs();
        }
        File myFile = new File(pdfFolder + File.separator + fileNome + ".mp3");
        if (myFile.exists()) {
            myFile.delete();
        }
        Uri.fromFile(myFile);

        request.setDestinationUri(Uri.fromFile(myFile));
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(request);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ListaHinosActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
