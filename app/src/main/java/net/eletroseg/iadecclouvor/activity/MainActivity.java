package net.eletroseg.iadecclouvor.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.Usuario;
import net.eletroseg.iadecclouvor.util.Base64Custom;
import net.eletroseg.iadecclouvor.util.ConfiguracaoFiribase;
import net.eletroseg.iadecclouvor.util.InstanciaFirebase;
import net.eletroseg.iadecclouvor.util.Permissao;
import net.eletroseg.iadecclouvor.util.SPM;
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView usuario, empresa;
    ImageView foto;
    Dialog dialog;
    Button sincronizar;
    public Uri resultUri;

   // Perfil perfil;

    SPM spm = new SPM(MainActivity.this);
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.INTERNET

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sincronizar = findViewById(R.id.btn_sincronizar);
        sincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
//hhh
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        usuario = (TextView) headerView.findViewById(R.id.text_nome_usuario_logado);
        empresa = (TextView) headerView.findViewById(R.id.text_nome_da_empresa);
        foto = (ImageView) headerView.findViewById(R.id.image_empresa);
        buscarPerfilWeb();
        usuario.setText(spm.getPreferencia("VENDEDOR_LOGADO", "VENDEDOR", ""));
        empresa.setText(Base64Custom.decodificarBase64(spm.getPreferencia("USUARIO_LOGADO", "USUARIO", "")));
foto.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent =  new Intent(MainActivity.this, FotoActivity.class);
        startActivity(intent);
       // finish();
    }
});
        Permissao.validaPermissoes(1, this, permissoesNecessarias);

     //   if (!drawer.isDrawerOpen(GravityCompat.START)) {
       //     drawer.openDrawer(GravityCompat.START);
        //}
        verificarConexao();
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sair) {
            FirebaseAuth firebaseAuth;
            firebaseAuth = ConfiguracaoFiribase.getFirebaseAutenticacao();
            firebaseAuth.signOut();
            spm.setPreferencia("USUARIO_LOGADO", "USUARIO", "");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_letras) {
            // Handle the camera action
            Intent intent = new Intent(MainActivity.this, ListaHinosActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_cifras) {
            Intent intent = new Intent(MainActivity.this, ListaCifrasActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_escala) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();


        } else if (id == R.id.nav_configuracao) {
            Intent intent = new Intent(MainActivity.this, ConfiguracaoActivity.class);
            startActivity(intent);
            finish();

        }else if (id == R.id.nav_avisos) {
            Intent intent = new Intent(MainActivity.this, ListaAvisosActivity.class);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void verificarConexao() {
        DatabaseReference connectedRef = InstanciaFirebase.getDatabase().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }


    private void buscarPerfilWeb() {
        final Usuario usuario = new Usuario();
        DatabaseReference reference = InstanciaFirebase.getDatabase().getReference("usuarios").child(spm.getPreferencia("USUARIO_LOGADO", "USUARIO", "erro")).child("foto");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() & dataSnapshot != null) {

                    usuario.foto = dataSnapshot.getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() & dataSnapshot != null) {
                    if (!usuario.foto.equals("")) {

                        Picasso.with(MainActivity.this).load(usuario.foto).placeholder(getResources().getDrawable(R.drawable.cifra_48)).into(foto);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
