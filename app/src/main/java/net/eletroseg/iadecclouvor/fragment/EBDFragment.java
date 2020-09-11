package net.eletroseg.iadecclouvor.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EBDFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EBDFragment extends Fragment {

    private static EBDFragment fragment;
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
    ArrayList<String> sArrayListVocal = new ArrayList<>();

    //---------------------------------------------------- RECYCLERVIEW INSTRUMENTAL -----------------------------------------------------------------
    private AdapterGradeUsuario mAdapter2;
    ArrayList<Usuario> arrayListInstrumental = new ArrayList<>();
    ArrayList<String> sArrayListInstrumental = new ArrayList<>();

    //---------------------------------------------------- RECYCLERVIEW HINO -----------------------------------------------------------------
    private AdapterSelecaoHino mAdapter3;
    ArrayList<Hino> arrayListHino = new ArrayList<>();
    ArrayList<String> sArrayListHino = new ArrayList<>();

    public EBDFragment() {
    }

    public static EBDFragment newInstance() {
        fragment = new EBDFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ebd, container, false);
        titulo = root.findViewById(R.id.text_ebd_titulo);
        fotoMinistrante = root.findViewById(R.id.image_ebd_ministrante);
        observacao = root.findViewById(R.id.text_ebd_observacao);
        nomeMinistrante = root.findViewById(R.id.text_ebd_ministrante);
        vocal = root.findViewById(R.id.recycler_ebd_vocal);
        instrumental = root.findViewById(R.id.recycler_ebd_instrumental);
        hino = root.findViewById(R.id.recycler_ebd_hino);
        playList = root.findViewById(R.id.btn_ebd_playlist);
        hino.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // hino.addItemDecoration(new LineItemDecoration(getContext(), LinearLayout.VERTICAL));
        // hino.addItemDecoration(new LineItemDecoration(getContext(), LinearLayout.HORIZONTAL));
        hino.setHasFixedSize(true);
        mAdapter3 = new AdapterSelecaoHino(getContext(), arrayListHino);
        hino.setAdapter(mAdapter3);
        buscarIdsWeb();
        playList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayListHino.size() > 0) {
                    Intent intent = new Intent(getContext(), ListaHinosActivity.class);
                    intent.putExtra("tipo", "lista");
                    intent.putExtra("playlist", arrayListHino);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Nenhum hino foi adicionado a playlist ainda.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    //---------------------------------------------------- BUSCA OS DADOS NO FIREBASE -----------------------------------------------------------------

    private void buscarIdsWeb() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child(Constantes.CRONOGRAMA).child("ebd");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    cronograma = dataSnapshot.getValue(Cronograma.class);

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
                    sArrayListVocal.clear();
                    sArrayListHino.clear();
                    sArrayListInstrumental.clear();

                    for (int i = 0; i < cronograma.vocal.size(); i++) {
                        sArrayListVocal.add(cronograma.vocal.get(i).id);
                    }
                    if (cronograma.musicas != null) {
                        for (int i = 0; i < cronograma.musicas.size(); i++) {
                            sArrayListHino.add(cronograma.musicas.get(i).id);
                        }
                    }


                    for (int i = 0; i < cronograma.instrumental.size(); i++) {
                        sArrayListInstrumental.add(cronograma.instrumental.get(i).id);
                    }

                    buscarUsuarioWeb();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void buscarUsuarioWeb() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = null;
        for (int i = 0; i < sArrayListVocal.size(); i++) {
            reference = database.getReference().child(Constantes.USUARIOS).child(sArrayListVocal.get(i));
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Usuario usuario = snapshot.getValue(Usuario.class);
                        for (int i = 0; i < arrayListVocal.size(); i++) {
                            if (arrayListVocal.get(i).nome.equals(usuario.nome)) {
                                arrayListVocal.remove(i);
                            }
                        }
                        arrayListVocal.add(usuario);
                        atualizar();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        for (int i = 0; i < sArrayListInstrumental.size(); i++) {
            reference = database.getReference().child(Constantes.USUARIOS).child(sArrayListInstrumental.get(i));
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Usuario usuario = snapshot.getValue(Usuario.class);
                        for (int i = 0; i < arrayListInstrumental.size(); i++) {
                            if (arrayListInstrumental.get(i).nome.equals(usuario.nome)) {
                                arrayListInstrumental.remove(i);
                            }
                        }
                        arrayListInstrumental.add(usuario);
                        atualizar();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        for (int i = 0; i < sArrayListHino.size(); i++) {
            reference = database.getReference().child(Constantes.HINO).child(sArrayListHino.get(i));
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Hino hino = snapshot.getValue(Hino.class);
                        for (int i = 0; i < arrayListHino.size(); i++) {
                            if (arrayListHino.get(i).nome.equals(hino.nome)) {
                                arrayListHino.remove(i);
                            }
                        }
                        arrayListHino.add(hino);
                        atualizar();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                atualizar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

    }

    private void atualizar() {

        ordenaPorNumero(arrayListVocal);
        ordenaPorNumero(arrayListInstrumental);
        int colVocal = arrayListVocal.size();
        if (colVocal >= 4 || colVocal == 0) {
            colVocal = 4;
        }
        vocal.setLayoutManager(new GridLayoutManager(getContext(), colVocal));
        vocal.setHasFixedSize(true);
        mAdapter = new AdapterGradeUsuario(getContext(), arrayListVocal);
        vocal.setAdapter(mAdapter);
        int col = arrayListInstrumental.size();
        if (col >= 4 || col == 0) {
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
        if (fragment.getContext() != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference().child(Constantes.USUARIOS).child(cronograma.ministrante.id);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    Glide.with(fragment.getContext()).load(usuario.foto).placeholder(R.drawable.ic_action_foto_user).into(fotoMinistrante);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        } else {
            Progresso.dialog.dismiss();
        }

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