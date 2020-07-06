package net.eletroseg.iadecclouvor.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.activity.ListaHinosActivity;
import net.eletroseg.iadecclouvor.adapter.AdapterGradeUsuario;
import net.eletroseg.iadecclouvor.adapter.AdapterSelecaoHino;
import net.eletroseg.iadecclouvor.modelo.Cronograma;
import net.eletroseg.iadecclouvor.modelo.Hino;
import net.eletroseg.iadecclouvor.modelo.Usuario;
import net.eletroseg.iadecclouvor.util.Constantes;
import net.eletroseg.iadecclouvor.util.Progresso;
import net.eletroseg.iadecclouvor.widget.LineItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DomingoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DomingoFragment extends Fragment {

    private static DomingoFragment fragment;
    private TextView titulo, observacao, nomeMinistrante;
    private ImageView fotoMinistrante;
    private RecyclerView vocal, instrumental, hino;
    private Button playList;
    Dialog dialog;

    public Uri resultUri;
    ArrayList<Cronograma> arrayCronograma = new ArrayList<>();
    Cronograma cronograma;

    //---------------------------------------------------- RECYCLERVIEW VOCAL -----------------------------------------------------------------
    private AdapterGradeUsuario mAdapter;
    ArrayList<Usuario> arrayListVocal = new ArrayList<>();

    //---------------------------------------------------- RECYCLERVIEW INSTRUMENTAL -----------------------------------------------------------------
    private AdapterGradeUsuario mAdapter2;
    ArrayList<Usuario> arrayListInstrumental = new ArrayList<>();

    //---------------------------------------------------- RECYCLERVIEW HINO -----------------------------------------------------------------
    private AdapterSelecaoHino mAdapter3;
    ArrayList<Hino> arrayListHino = new ArrayList<>();

    public DomingoFragment() {
    }

    public static DomingoFragment newInstance() {
        fragment = new DomingoFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_domingo, container, false);
        titulo = root.findViewById(R.id.text_main_titulo);
        fotoMinistrante = root.findViewById(R.id.image_main_ministrante);
        observacao = root.findViewById(R.id.text_main_observacao);
        nomeMinistrante = root.findViewById(R.id.text_main_ministrante);
        vocal = root.findViewById(R.id.recycler_main_vocal);
        instrumental = root.findViewById(R.id.recycler_main_instrumental);
        hino = root.findViewById(R.id.recycler_main_hino);
        playList = root.findViewById(R.id.btn_main_playlist);
        hino.setLayoutManager(new GridLayoutManager(getContext(), 2));
       // hino.addItemDecoration(new LineItemDecoration(getContext(), LinearLayout.VERTICAL));
       // hino.addItemDecoration(new LineItemDecoration(getContext(), LinearLayout.HORIZONTAL));
        hino.setHasFixedSize(true);
        mAdapter3 = new AdapterSelecaoHino(getContext(), arrayListHino);
        hino.setAdapter(mAdapter3);
        buscarClienteWeb();
        playList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ListaHinosActivity.class);
                intent.putExtra("tipo", "lista");
                intent.putExtra("playlist", arrayListHino);
                startActivity(intent);
            }
        });

        return root;
    }

    //---------------------------------------------------- BUSCA OS DADOS NO FIREBASE -----------------------------------------------------------------

    private void buscarClienteWeb() {
        cronograma = new Cronograma();
        Progresso.progressoCircular(getContext());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child(Constantes.CRONOGRAMA).child("domingo");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    cronograma = dataSnapshot.getValue(Cronograma.class);
                    // if (!hino.id.equals("master")) {
                    // arrayCronograma.add(cronograma);
                    //arrayListIds.add(usuario.id);
                    //  }
                } else {
                    Progresso.dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Progresso.dialog.dismiss();
                    arrayListVocal.clear();
                    arrayListHino.clear();
                    arrayListInstrumental.clear();
                    arrayListVocal.addAll(cronograma.vocal);
                    arrayListInstrumental.addAll(cronograma.instrumental);
                    arrayListHino.addAll(cronograma.musicas);
                    ordenaPorNumero(arrayListVocal);
                    ordenaPorNumero(arrayListInstrumental);
                    int colVocal = arrayListVocal.size();
                    if (colVocal >= 4 || colVocal == 0){
                        colVocal = 4;
                    }
                    vocal.setLayoutManager(new GridLayoutManager(getContext(), colVocal));
                    vocal.setHasFixedSize(true);
                    mAdapter = new AdapterGradeUsuario(getContext(), arrayListVocal);
                    vocal.setAdapter(mAdapter);
                    int col = arrayListInstrumental.size();
                    if (col >= 4 || col == 0){
                        col = 4;
                    }
                    instrumental.setLayoutManager(new GridLayoutManager(getContext(), col));
                    instrumental.setHasFixedSize(true);
                    mAdapter2 = new AdapterGradeUsuario(getContext(), arrayListInstrumental);
                    instrumental.setAdapter(mAdapter2);
                    mAdapter.notifyDataSetChanged();
                    mAdapter2.notifyDataSetChanged();
                    mAdapter3.notifyDataSetChanged();
                    titulo.setText(cronograma.diaDoCulto);
                    observacao.setText(cronograma.observacao);
                    nomeMinistrante.setText(cronograma.ministrante.nome.substring(0, 1).toUpperCase() + cronograma.ministrante.nome.substring(1));
                    if (fragment.getContext() != null){
                        Glide.with(fragment.getContext()).load(cronograma.ministrante.foto).into(fotoMinistrante);
                    }


                } else {
                    Progresso.dialog.dismiss();
                }

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

    @Override
    public void onResume() {

        super.onResume();

    }
}