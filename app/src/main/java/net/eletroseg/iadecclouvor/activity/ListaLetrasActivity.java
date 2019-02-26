package net.eletroseg.iadecclouvor.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.adapter.LetraAdapter;
import net.eletroseg.iadecclouvor.modelo.Letra;
import net.eletroseg.iadecclouvor.util.InstanciaFirebase;
import net.eletroseg.iadecclouvor.util.SPM;
import java.util.ArrayList;

public class ListaLetrasActivity extends AppCompatActivity {



    private EditText editLetra;
    private LinearLayout linearLayout;
    private ImageView voltar, apagar;
    private ListView lvLetra;
    private MenuItem menuNovo;
    private Letra letra;

    private ArrayList<Letra> arrayListLetra = new ArrayList<Letra>();
    private SPM spm = new SPM(ListaLetrasActivity.this);
    private LetraAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_letras);

        editLetra = findViewById(R.id.lista_edit_letra_pesquisa);
        linearLayout = findViewById(R.id.lista_layout_letra_pesquisa);
        voltar = findViewById(R.id.lista_image_letra_voltar);
        apagar = findViewById(R.id.lista_image_letra_apagar);
        lvLetra = findViewById(R.id.lista_listview_letra);
        adapter = new LetraAdapter(ListaLetrasActivity.this, arrayListLetra);
        lvLetra.setAdapter(adapter);
        buscarLetras();


        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.GONE);
                getSupportActionBar().show();
               // esconderTeclado(ListaCliente2Activity.this);


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

                adapter.getFilter().filter(s.toString());


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lvLetra.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           letra = (Letra) lvLetra.getItemAtPosition(position);

           Intent intent = new Intent(ListaLetrasActivity.this, ExibirLetraActivity.class);
                intent.putExtra("letra", letra.letra);
            startActivity(intent);}
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.novo_hino, menu);

        menuNovo = menu.findItem(R.id.item_menu_novo_hino);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

//
        if (id == R.id.item_menu_novo_hino) {

            Intent intent = new Intent(ListaLetrasActivity.this, CadastrarHinoActivity.class);
            intent.putExtra("modo", false);
            startActivity(intent);
            finish();
        } else if (id == R.id.item_menu_pesquisa_hino) {
            linearLayout.setVisibility(View.VISIBLE);
            getSupportActionBar().hide();
            editLetra.requestFocus();
           // aparecerTeclado(editProduto);


        } else if (id == android.R.id.home) {

            Intent intent = new Intent(ListaLetrasActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    private void buscarLetras() {
        arrayListLetra.clear();

        DatabaseReference reference = InstanciaFirebase.getDatabase().getReference("letras");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Letra letra = dataSnapshot.getValue(Letra.class);
                arrayListLetra.add(letra);

                // constroi o adapter passando os itens.
                adapter = new LetraAdapter(ListaLetrasActivity.this, arrayListLetra);
                lvLetra.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Letra letra = dataSnapshot.getValue(Letra.class);
                for (int i = 0; i < arrayListLetra.size(); i++) {
                    if (arrayListLetra.get(i).id.equals(letra.id)) {
                        arrayListLetra.remove(i);
                    }
                }
                arrayListLetra.add(letra);
                // constroi o adapter passando os itens.
                adapter = new LetraAdapter(ListaLetrasActivity.this, arrayListLetra);
                lvLetra.setAdapter(adapter);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

    }
}
