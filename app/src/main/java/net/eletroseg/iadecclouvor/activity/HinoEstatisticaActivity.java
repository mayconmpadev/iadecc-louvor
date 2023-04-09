package net.eletroseg.iadecclouvor.activity;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.adapter.AdapterHinoEstatistica;
import net.eletroseg.iadecclouvor.modelo.Cronograma;
import net.eletroseg.iadecclouvor.modelo.FiltroCron;
import net.eletroseg.iadecclouvor.modelo.Usuario;
import net.eletroseg.iadecclouvor.util.InstanciaFirebase;
import net.eletroseg.iadecclouvor.util.Progresso;
import net.eletroseg.iadecclouvor.util.Timestamp;
import net.eletroseg.iadecclouvor.widget.LineItemDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class HinoEstatisticaActivity extends AppCompatActivity implements AdapterHinoEstatistica.OnClickListener {
    private ImageView inversao;
    private RecyclerView recyclerView;
    private Button filtroInicio, filtroFim, todos, ebd, domingo, quarta, especial;
    ArrayList<Cronograma> arrayListUsuario = new ArrayList<>();
    ArrayList<FiltroCron> filtroCrons = new ArrayList<>();
    private AdapterHinoEstatistica mAdapter;
    String sPesquisa = "";
    String caminho = "";
    boolean bInversao = false;
    boolean bBotao = false;
    DatabaseReference reference;
    Query query;
    private DatePickerDialog fromDatePickerDialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hino_estatistica);
        inversao = findViewById(R.id.image_inversao);
        recyclerView = findViewById(R.id.recycler_hino_mais);
        filtroInicio = findViewById(R.id.btn_filtro_data_inicio_minis);
        filtroFim = findViewById(R.id.btn_filtro_data_final_minis);
        todos = findViewById(R.id.btn_filtro_todos);
        ebd = findViewById(R.id.btn_filtro_ebd);
        domingo = findViewById(R.id.btn_filtro_domingo);
        quarta = findViewById(R.id.btn_filtro_quarta);
        especial = findViewById(R.id.btn_filtro_especial);

        mAdapter = new AdapterHinoEstatistica(this, filtroCrons, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new LineItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        inversao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bInversao = !bInversao;
                if (bInversao) {
                    //   inversao.setBackgroundColor(getResources().getColor(R.color.vermelho));
                    inversao.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.vermelho), android.graphics.PorterDuff.Mode.MULTIPLY);

                } else {
                    //  inversao.setBackgroundColor(getResources().getColor(R.color.verde));
                    inversao.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.verde), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
                if (!caminho.equals("todos")) {
                    buscarClienteWeb(caminho);
                } else {
                    buscarTodosWeb();
                }

            }
        });
        filtroInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateTimeField(filtroInicio);
                fromDatePickerDialog.show();
            }
        });

        filtroFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateTimeField(filtroFim);
                fromDatePickerDialog.show();
            }
        });
        todos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                caminho = "todos";
                bBotao = !bBotao;
                btnDesapertado();
                buscarTodosWeb();
                todos.setTextColor(getColor(R.color.branco));
                todos.setBackgroundColor(getColor(R.color.colorPrimaryDark));
            }
        });
        filtroFim.setText(Timestamp.getFormatedDateTime(Timestamp.getUnixTimestamp(), "dd/MM/yyyy"));
        btnApertado(ebd);
        btnApertado(domingo);
        btnApertado(quarta);
        btnApertado(especial);
        todos.setTextColor(getColor(R.color.branco));
        todos.setBackgroundColor(getColor(R.color.colorPrimaryDark));
        buscarTodosWeb();
    }

    //---------------------------------------------------- BUSCA OS DADOS NO FIREBASE -----------------------------------------------------------------
    private void btnApertado(final Button button) {

        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                caminho = button.getText().toString().toLowerCase(Locale.ROOT);
                btnDesapertado();
                button.setTextColor(getColor(R.color.branco));
                button.setBackgroundColor(getColor(R.color.colorPrimaryDark));
                buscarClienteWeb(button.getText().toString().toLowerCase(Locale.ROOT));
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void btnDesapertado() {

        todos.setTextColor(getColor(R.color.preto));
        todos.setBackgroundColor(getColor(R.color.cinza));

        ebd.setTextColor(getColor(R.color.preto));
        ebd.setBackgroundColor(getColor(R.color.cinza));

        domingo.setTextColor(getColor(R.color.preto));
        domingo.setBackgroundColor(getColor(R.color.cinza));

        quarta.setTextColor(getColor(R.color.preto));
        quarta.setBackgroundColor(getColor(R.color.cinza));

        especial.setTextColor(getColor(R.color.preto));
        especial.setBackgroundColor(getColor(R.color.cinza));
    }

    private void buscarClienteWeb(String cominho) {
        Progresso.progressoCircular(this);
        arrayListUsuario.clear();
        FirebaseDatabase database = InstanciaFirebase.getDatabase();

        DatabaseReference reference = database.getReference().child("estatistica").child(cominho);
        Query query = reference.orderByChild("id").startAt(Timestamp.convert(filtroInicio.getText().toString())).endAt(Timestamp.convert(filtroFim.getText().toString()));
        reference.keepSynced(true);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final Cronograma cronograma = dataSnapshot.getValue(Cronograma.class);
                if (cronograma.musicas != null){
                    arrayListUsuario.add(cronograma);
                }


                // mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Cronograma usuario = dataSnapshot.getValue(Cronograma.class);
                for (int i = 0; i < arrayListUsuario.size(); i++) {
                    if (arrayListUsuario.get(i).ministrante.nome.equals(usuario.ministrante.nome)) {
                        arrayListUsuario.remove(i);
                    }
                }
                arrayListUsuario.add(usuario);
                // ordenaPorNumero(arrayListUsuario);
                //  mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                for (int i = 0; i < arrayListUsuario.size(); i++) {
                    if (arrayListUsuario.get(i).ministrante.nome.equals(usuario.nome)) {
                        arrayListUsuario.remove(i);
                    }
                }
                if (!sPesquisa.equals("")) {
                    recyclerView.setAdapter(mAdapter);
                    // mAdapter.getFilter().filter(sPesquisa);

                }
                //ordenaPorNumero(arrayListUsuario);

                mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {


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
                filtroCrons.clear();
                int qtd = 0;
                ArrayList<String> arrayListNome = new ArrayList<>();
                try {
                    if (arrayListUsuario.size() > 0) {
                        if (arrayListUsuario.get(0).musicas != null) {
                            for (int i = 0; i < arrayListUsuario.size(); i++) {
                                for (int j = 0; j < arrayListUsuario.get(i).musicas.size(); j++) {
                                    if (!arrayListNome.contains(arrayListUsuario.get(i).musicas.get(j).id)) {
                                        arrayListNome.add(arrayListUsuario.get(i).musicas.get(j).id);

                                    }
                                }


                            }

                            for (int i = 0; i < arrayListNome.size(); i++) {
                                for (int j = 0; j < arrayListUsuario.size(); j++) {

                                    for (int h = 0; h < arrayListUsuario.get(j).musicas.size(); h++) {
                                        if (!arrayListNome.get(i).equals(arrayListUsuario.get(j).musicas.get(h).id)) {

                                        } else {
                                            qtd++;
                                        }
                                    }


                                }
                                FiltroCron filtroCron = new FiltroCron();
                                filtroCron.id = arrayListNome.get(i);
                                filtroCron.numero = "Foi cantada *" + qtd + "* vezes";
                                filtroCrons.add(filtroCron);
                                qtd = 0;

                            }

                            if (bInversao) {
                                ordenInversa(filtroCrons);
                            } else {
                                ordenaPorNumero(filtroCrons);
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void buscarTodosWeb() {
        caminho = "todos";
        arrayListUsuario.clear();
        Progresso.progressoCircular(this);
        FirebaseDatabase database = InstanciaFirebase.getDatabase();

        String[] todos = {"especial", "domingo", "quarta", "ebd"};
        for (int i = 0; i < todos.length; i++) {

            reference = database.getReference().child("estatistica").child(todos[i]);
            query = reference.orderByChild("id").startAt(Timestamp.convert(filtroInicio.getText().toString())).endAt(Timestamp.convert(filtroFim.getText().toString()));

            reference.keepSynced(true);
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.exists()) {
                        final Cronograma cronograma = dataSnapshot.getValue(Cronograma.class);
                        if (cronograma.musicas != null){
                            arrayListUsuario.add(cronograma);
                        }
                    }


                    // mAdapter.notifyDataSetChanged();
                    Progresso.dialog.dismiss();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    Cronograma usuario = dataSnapshot.getValue(Cronograma.class);
                    for (int i = 0; i < arrayListUsuario.size(); i++) {
                        if (arrayListUsuario.get(i).ministrante.nome.equals(usuario.ministrante.nome)) {
                            arrayListUsuario.remove(i);
                        }
                    }
                    arrayListUsuario.add(usuario);
                    // ordenaPorNumero(arrayListUsuario);
                    //  mAdapter.notifyDataSetChanged();
                    Progresso.dialog.dismiss();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    for (int i = 0; i < arrayListUsuario.size(); i++) {
                        if (arrayListUsuario.get(i).ministrante.nome.equals(usuario.nome)) {
                            arrayListUsuario.remove(i);
                        }
                    }
                    if (!sPesquisa.equals("")) {
                        recyclerView.setAdapter(mAdapter);
                        // mAdapter.getFilter().filter(sPesquisa);

                    }
                    //ordenaPorNumero(arrayListUsuario);

                    mAdapter.notifyDataSetChanged();
                    Progresso.dialog.dismiss();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {


                    mAdapter.notifyDataSetChanged();
                    Progresso.dialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mAdapter.notifyDataSetChanged();
                    Progresso.dialog.dismiss();
                }
            });

        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                filtroCrons.clear();
                int qtd = 0;
                try {
                    ArrayList<String> arrayListNome = new ArrayList<>();
                  //  if (arrayListUsuario.size() > 0) {
                     //   if (arrayListUsuario.get(0).musicas != null) {

                            for (int i = 0; i < arrayListUsuario.size(); i++) {
                                for (int j = 0; j < arrayListUsuario.get(i).musicas.size(); j++) {
                                    if (!arrayListNome.contains(arrayListUsuario.get(i).musicas.get(j).id)) {
                                        arrayListNome.add(arrayListUsuario.get(i).musicas.get(j).id);

                                    }
                                }


                            }

                            for (int i = 0; i < arrayListNome.size(); i++) {
                                for (int j = 0; j < arrayListUsuario.size(); j++) {

                                    for (int h = 0; h < arrayListUsuario.get(j).musicas.size(); h++) {
                                        if (!arrayListNome.get(i).equals(arrayListUsuario.get(j).musicas.get(h).id)) {

                                        } else {
                                            qtd++;
                                        }
                                    }


                                }
                                FiltroCron filtroCron = new FiltroCron();
                                filtroCron.id = arrayListNome.get(i);
                                filtroCron.numero = "Foi cantada *" + qtd + "* vezes";
                                filtroCrons.add(filtroCron);
                                qtd = 0;

                            }

                            if (bInversao) {
                                ordenInversa(filtroCrons);
                            } else {
                                ordenaPorNumero(filtroCrons);
                            }
                      //  }

                  //  }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mAdapter.notifyDataSetChanged();
                Progresso.dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setDateTimeField(final Button button) {

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
                long date_ship_millis = calendar.getTimeInMillis() / 1000;

                button.setText(Timestamp.getFormatedDateTime(date_ship_millis, "dd/MM/yyyy"));
                if (caminho.equals("todos")) {
                    buscarTodosWeb();
                } else {
                    buscarClienteWeb(caminho);
                }

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }


    //---------------------------------------------------- ORDENA A LISTA EM ORDEM ALFABETICA ----------------------------------------------

    private static void ordenaPorNumero(ArrayList<FiltroCron> lista) {
        Collections.sort(lista, new Comparator<FiltroCron>() {
            @Override
            public int compare(FiltroCron o1, FiltroCron o2) {
                return o2.numero.toLowerCase().compareTo(o1.numero.toLowerCase());
            }
        });
    }

    private static void ordenInversa(ArrayList<FiltroCron> lista) {
        Collections.sort(lista, new Comparator<FiltroCron>() {
            @Override
            public int compare(FiltroCron o1, FiltroCron o2) {
                return o1.numero.toLowerCase().compareTo(o2.numero.toLowerCase());
            }
        });
    }

    @Override
    public void onItemClick(View view, FiltroCron obj, int pos) {
        Toast.makeText(getApplicationContext(), obj.id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View view, FiltroCron obj, int pos) {

    }
}