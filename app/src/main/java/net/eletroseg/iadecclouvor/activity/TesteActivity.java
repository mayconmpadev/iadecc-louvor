package net.eletroseg.iadecclouvor.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.Hino;
import net.eletroseg.iadecclouvor.util.Constantes;
import net.eletroseg.iadecclouvor.util.InstanciaFirebase;
import net.eletroseg.iadecclouvor.util.Progresso;

import java.util.ArrayList;

public class TesteActivity extends AppCompatActivity {
    private EditText editText;
    private Button buscar;
    private Spinner spinner;
    private ArrayList<Hino> arrayListHino = new ArrayList<>();
    private ArrayList<String> listaHino = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste2);
        editText = findViewById(R.id.editTeste);
        spinner = findViewById(R.id.spinnerTeste);
        buscar = findViewById(R.id.btnBuscarTeste);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarClienteWeb();
            }
        });
    }

    private void buscarClienteWeb() {
        Progresso.progressoCircular(this);
        arrayListHino.clear();
        //  Query produtoRef = FirebaseHelper.getDatabaseReference()
        //     .child("empresas").child(Base64Custom.codificarBase64(spm.getPreferencia("PREFERENCIAS", "CAMINHO", ""))).child("produtos").orderByChild("codigo");

        FirebaseDatabase database = InstanciaFirebase.getDatabase();
        Query reference = database.getReference().child(Constantes.HINO);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Hino hino = ds.getValue(Hino.class);
                        arrayListHino.add(hino);
                    }
                    String texto = "";
                    for (int i = 0; i < arrayListHino.size(); i++) {
                        if (arrayListHino.get(i).categoria.trim().equals(spinner.getSelectedItem().toString().trim())){
                            texto = texto + arrayListHino.get(i).nome + "\n";
                        }


                    }

                    editText.setText(texto);

                }

                Progresso.dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}