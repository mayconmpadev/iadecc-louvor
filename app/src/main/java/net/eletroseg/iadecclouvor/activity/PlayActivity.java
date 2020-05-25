package net.eletroseg.iadecclouvor.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import net.eletroseg.iadecclouvor.R;
import net.eletroseg.iadecclouvor.modelo.Hino;
import net.eletroseg.iadecclouvor.util.MusicUtils;
import net.eletroseg.iadecclouvor.util.Parametro;
import net.eletroseg.iadecclouvor.util.Tools;
import net.eletroseg.iadecclouvor.util.Util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity {

    private View parent_view;
    private AppCompatSeekBar seek_song_progressbar;
    private FloatingActionButton bt_play;
    private Button a, b, trexo;
    private CardView controles;
    private AppBarLayout appBarLayout;
    private ScrollView scrollView;
    private TextView tv_song_current_duration, tv_song_total_duration, nome_hino, nome_cantor, letra_hino;
    ArrayList<String> arrayList = new ArrayList<>();
    int cont = 0;
    Hino obj;
    boolean repetir = false;
    boolean bControles = true;
    boolean bLiberarControles = true;
    long inicio, fim;
    // Media Player
    public MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    int velocidade = 0;
    MinhaThread thread = new MinhaThread();
    Dialog dialog;
    //private SongsManager songManager;
    private MusicUtils utils;
    boolean ativar = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initToolbar();
        initComponent();
        recuperarIntent();
        thread.start();
        letra_hino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bLiberarControles){
                    if (bControles) {

                        controles.setVisibility(View.GONE);
                        appBarLayout.setVisibility(View.GONE);
                        bControles = !bControles;

                    } else {

                        controles.setVisibility(View.VISIBLE);
                        appBarLayout.setVisibility(View.VISIBLE);
                        bControles = !bControles;
                    }
                }


            }
        });

        letra_hino.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                bLiberarControles = !bLiberarControles;
                if (bLiberarControles){

                    dialogCadeadoAberto();
                }else {
                    dialogCadeado();
                }

                return true;
            }
        });

    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    private void initComponent() {
        parent_view = findViewById(R.id.parent_view_play);
        controles = findViewById(R.id.player_control);
        scrollView = findViewById(R.id.letra_sv);
        nome_hino = findViewById(R.id.text_nome_do_hino);
        nome_cantor = findViewById(R.id.text_nome_do_cantor);
        letra_hino = findViewById(R.id.text_letra_hino);
        appBarLayout = findViewById(R.id.appbar);
        a = findViewById(R.id.btn_a);
        b = findViewById(R.id.btn_b);
        trexo = findViewById(R.id.btn_repetir);

        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a.setBackgroundResource(R.drawable.btn_rounded_roxo);
                a.setTextColor(Color.WHITE);
                inicio = mp.getCurrentPosition();
                Toast.makeText(PlayActivity.this, utils.milliSecondsToTimer(inicio), Toast.LENGTH_SHORT).show();

            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inicio > 0){
                    b.setBackgroundResource(R.drawable.btn_rounded_roxo);
                    b.setTextColor(Color.WHITE);
                    fim = mp.getCurrentPosition();
                    Toast.makeText(PlayActivity.this, utils.milliSecondsToTimer(fim), Toast.LENGTH_SHORT).show();
                }else {
                    Util.vibrar(getApplicationContext(), 100 );
                }

            }
        });

        trexo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inicio > 0 & fim > 0) {
                    if (!repetir) {
                        trexo.setBackgroundResource(R.drawable.borda_roxa);
                        trexo.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        trexo.setText("parar");
                        repetir = true;
                        mp.seekTo((int) inicio);
                    } else {
                        trexo.setBackgroundResource(R.drawable.borda_pesquisa);
                        trexo.setTextColor(getResources().getColor(R.color.preto));
                        trexo.setText("repetir");
                        repetir = false;
                        mp.seekTo(0);
                        inicio = 0;
                        fim = 0;
                        a.setBackgroundResource(R.drawable.borda_roxa_redolda);
                        a.setTextColor(Color.BLACK);
                        b.setBackgroundResource(R.drawable.borda_roxa_redolda);
                        b.setTextColor(Color.BLACK);
                    }
                }


            }
        });

        seek_song_progressbar = (AppCompatSeekBar) findViewById(R.id.seek_song_progressbar);
        bt_play = (FloatingActionButton) findViewById(R.id.bt_play);

        // set Progress bar values
        seek_song_progressbar.setProgress(0);


        seek_song_progressbar.setMax(MusicUtils.MAX_PROGRESS);


        tv_song_current_duration = (TextView) findViewById(R.id.tv_song_current_duration);
        tv_song_total_duration = (TextView) findViewById(R.id.tv_song_total_duration);

        // Media Player
        mp = new MediaPlayer();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                cont++;
                if (cont < arrayList.size()) {
                    Parametro.nome = arrayList.get(cont);
                    Toast.makeText(PlayActivity.this, Parametro.nome, Toast.LENGTH_SHORT).show();
                    mp.seekTo(0);
                    mp.start();
                    play();

                } else {
                    if (repetir) {
                        cont = 0;
                        mp.reset();
                        play();
                    } else {
                        bt_play.setImageResource(R.drawable.ic_play_arrow);
                    }


                }


            }
        });
        if (mp.isPlaying()) {
            mp.release();
        }
        play();


        utils = new MusicUtils();
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
                int totalDuration;

                totalDuration = mp.getDuration();


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
        bt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (mp.isPlaying()) {
                    mp.pause();
                    // Changing button image to play button
                    bt_play.setImageResource(R.drawable.ic_play_arrow);
                } else {
                    // Resume song
                    mp.start();
                    // Changing button image to pause button
                    bt_play.setImageResource(R.drawable.ic_pause);
                    // Updating progress bar
                    mHandler.post(mUpdateTimeTask);
                }

            }
        });
    }

    private void recuperarIntent() {


        Intent intent = getIntent();
        obj = (Hino) intent.getSerializableExtra("hino");

        nome_hino.setText(obj.nome);
        nome_cantor.setText(obj.cantor);
        letra_hino.setText(obj.letra);
        Util.textoNegrito(letra_hino.getText().toString(), letra_hino, null);
    }

    public void controlClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_repeat: {
                toggleButtonColor((ImageButton) v);
                Snackbar.make(parent_view, "Repeat", Snackbar.LENGTH_SHORT).show();
                repetir = !repetir;
                break;
            }

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
        long totalDuration;
        long currentDuration;
        if (repetir) {
            totalDuration = fim;
            currentDuration = mp.getCurrentPosition();
        } else {
            totalDuration = mp.getDuration();
            currentDuration = mp.getCurrentPosition();
        }


        if (totalDuration <= currentDuration & repetir) {
            mp.reset();
            play();
            mp.seekTo((int) inicio);
        }

        // Displaying Total Duration time
        tv_song_total_duration.setText(utils.milliSecondsToTimer(totalDuration));
        // Displaying time completed playing
        tv_song_current_duration.setText(utils.milliSecondsToTimer(currentDuration));

        // Updating progress bar
        int progress = (int) (utils.getProgressSeekBar(currentDuration, totalDuration));
        seek_song_progressbar.setProgress(progress);
        // rangeSeekBar.setSelectedMinValue(progress);
    }

    private void play() {
        try {
            FileDescriptor fd = null;

            android.os.Environment.getExternalStorageDirectory();

            File baseDir = this.getExternalFilesDir(null);
            String audioPath = baseDir.getAbsolutePath() + File.separator + "Iadecc/hinos" + File.separator + Parametro.nome + ".mp3";
            FileInputStream fis = new FileInputStream(audioPath);
            fd = fis.getFD();


            if (fd != null) {
                // mHandler.removeCallbacks(mUpdateTimeTask);
                // mp.release();
                mp.setDataSource(fd);
                mp.prepare();
                mp.start();

                // Changing button image to pause button
                bt_play.setImageResource(R.drawable.ic_pause);
                // Updating progress bar
                mHandler.post(mUpdateTimeTask);

            }
        } catch (Exception e) {
            Snackbar.make(parent_view, "Cannot load audio file", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void dialogOpcaoLetra() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_opcao_letra);
        //instancia os objetos que estão no layout customdialog.xml
        final TextView p = dialog.findViewById(R.id.text_p);
        final TextView m = dialog.findViewById(R.id.text_m);
        final TextView g = dialog.findViewById(R.id.text_g);
        final ImageView fechar = dialog.findViewById(R.id.image_fechar);
        final TextView valorVelocidade = dialog.findViewById(R.id.exibir_letra_text_velocidade);
       final SeekBar seekBar = dialog.findViewById(R.id.exibir_letra_seekbar);

        seekBar.setProgress(velocidade);
        valorVelocidade.setText(String.valueOf(seekBar.getProgress()));
        velocidade = seekBar.getProgress();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                valorVelocidade.setText(String.valueOf(seekBar.getProgress() ));
                velocidade = seekBar.getProgress() ;



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                letra_hino.setTextSize(15f);

            }
        });

        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                letra_hino.setTextSize(18f);

            }
        });

        g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                letra_hino.setTextSize(25f);

            }
        });

        //exibe na tela o dialog
        dialog.show();

    }

    private void dialogCadeado() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_cadeado);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //instancia os objetos que estão no layout customdialog.xml

        //exibe na tela o dialog
        tempo();

        dialog.show();

    }

    private void dialogCadeadoAberto() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_cadeado_aberto);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //instancia os objetos que estão no layout customdialog.xml

        //exibe na tela o dialog
        tempo();

        dialog.show();

    }

    private void tempo() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                dialog.dismiss();


            }
        }, 1000); // 3000 milliseconds delay
    }
    // stop player when destroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        //   mHandler.removeCallbacks(mUpdateTimeTask);
        //  mp.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_music_setting, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.item_menu_compartilhar) {
            //letra_hino.setTextSize(15f);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(intent.EXTRA_TEXT, obj.letra);
            startActivity(Intent.createChooser(intent, "Compartilhe"));

        } else if (item.getItemId() == R.id.item_menu_menu) {
            dialogOpcaoLetra();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        mp.release();
        finish();
    }


    class MinhaThread extends Thread {
        @Override
        public void run() {


            while (true) {
                if (velocidade > 0) {
                    try {
                        Thread.sleep(300 / velocidade);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {

                            scrollView.scrollTo(0, scrollView.getScrollY() + 1);

                        }
                    });
                }


            }

        }
    }

}

