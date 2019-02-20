package net.eletroseg.iadecclouvor.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.Letra;
import net.eletroseg.iadecclouvor.util.InstanciaFirebase;
import net.eletroseg.iadecclouvor.util.SPM;

import java.util.ArrayList;

public class ListaLetrasActivity extends AppCompatActivity {

    private ArrayList<Letra> arrayListLetra = new ArrayList<Letra>();

    private EditText editProduto;
    private LinearLayout linearLayout;
    private ImageView voltar, apagar;
    private ListView lvProduto;
    private MenuItem menuNovo;
    private SPM spm = new SPM(ListaLetrasActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_letras);
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
            editProduto.requestFocus();
           // aparecerTeclado(editProduto);


        } else if (id == android.R.id.home) {

            Intent intent = new Intent(ListaLetrasActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    private void buscarLetras(){
        arrayListLetra.clear();

        DatabaseReference reference = InstanciaFirebase.getDatabase().getReference("letras");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Letra letra =  dataSnapshot.getValue(Letra.class);
                arrayListLetra.add(letra);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
