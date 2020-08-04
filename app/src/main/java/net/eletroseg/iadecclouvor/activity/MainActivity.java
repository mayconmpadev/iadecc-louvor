package net.eletroseg.iadecclouvor.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.fragment.DomingoFragment;
import net.eletroseg.iadecclouvor.fragment.EBDFragment;
import net.eletroseg.iadecclouvor.fragment.EspecialFragment;
import net.eletroseg.iadecclouvor.fragment.QuartaFragment;
import net.eletroseg.iadecclouvor.modelo.Usuario;
import net.eletroseg.iadecclouvor.util.Base64Custom;
import net.eletroseg.iadecclouvor.util.ConfiguracaoFiribase;
import net.eletroseg.iadecclouvor.util.InstanciaFirebase;
import net.eletroseg.iadecclouvor.util.Permissao;
import net.eletroseg.iadecclouvor.util.SPM;

import net.eletroseg.iadecclouvor.util.Tools;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView usuario, empresa;
    ImageView foto;
    private FloatingActionButton ebd, domingo, quarta, especial;
    private FloatingActionMenu fabMenu;
    boolean sair = false;
    private ViewPager view_pager;
    private TabLayout tab_layout;
    Toolbar toolbar;
public static boolean visivel = false;

    SPM spm = new SPM(MainActivity.this);
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.INTERNET

    };

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        initToolbar();
        iniciarComponentes();
        FirebaseMessaging.getInstance().subscribeToTopic("todos");
        if (spm.getPreferencia("USUARIO_LOGADO", "MODERADOR", "").equals("sim")) {
            fabMenu.setVisibility(View.VISIBLE);
        } else {
            fabMenu.setVisibility(View.INVISIBLE);
        }
        ebd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), CadastroEscalaActivity.class);
                intent.putExtra("tipo", "ebd");
                startActivity(intent);
                finish();
            }
        });

        domingo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), CadastroEscalaActivity.class);
                intent.putExtra("tipo", "domingo");
                startActivity(intent);
                finish();
            }
        });

        quarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), CadastroEscalaActivity.class);
                intent.putExtra("tipo", "quarta");
                startActivity(intent);
                finish();
            }
        });

        especial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), CadastroEscalaActivity.class);
                intent.putExtra("tipo", "especial");
                startActivity(intent);
                finish();
            }
        });


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
                Intent intent = new Intent(MainActivity.this, FotoActivity.class);
                startActivity(intent);
            }
        });
        Permissao.validaPermissoes(1, this, permissoesNecessarias);

        verificarConexao();
    }

    private void initToolbar() {

        toolbar.setNavigationIcon(R.drawable.ic_cloud_download);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Escalas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    private void iniciarComponentes() {
        fabMenu = findViewById(R.id.fab_menu);
        ebd = findViewById(R.id.fab_main_ebd);
        domingo = findViewById(R.id.fab_main_domingo);
        quarta = findViewById(R.id.fab_main_quarta);
        especial = findViewById(R.id.fab_main_especial);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(view_pager);

        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        tab_layout.setupWithViewPager(view_pager);
        view_pager.setOffscreenPageLimit(3);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (sair) {
                finish();
            } else {
                Toast.makeText(this, "aperte novamente para fechar", Toast.LENGTH_SHORT).show();
                sair = true;
            }
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


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_letras) {
            // Handle the camera action
            Intent intent = new Intent(MainActivity.this, ListaHinosActivity.class);
            intent.putExtra("tipo", "web");
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_usuario) {
            Intent intent = new Intent(MainActivity.this, ListaUsuarioActivity.class);
            startActivity(intent);
            finish();


        } else if (id == R.id.nav_configuracao) {
            Intent intent = new Intent(MainActivity.this, ListaAvisoActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_avisos) {
            Toast.makeText(this, "Este menu está em construção", Toast.LENGTH_SHORT).show();

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

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(EBDFragment.newInstance(), "EBD");
        adapter.addFragment(DomingoFragment.newInstance(), "DOMINGO");
        adapter.addFragment(QuartaFragment.newInstance(), "QUARTA");
        adapter.addFragment(EspecialFragment.newInstance(), "ESPECIAL");


        viewPager.setAdapter(adapter);

    }
    //---------------------------------------------------- BUSCA OS DADOS NO FIREBASE -----------------------------------------------------------------

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        visivel = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        visivel = false;
    }


}
