package net.eletroseg.iadecclouvor.util;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import net.eletroseg.iadecclouvor.api.NotificacaoService;
import net.eletroseg.iadecclouvor.modelo.Notificacao;
import net.eletroseg.iadecclouvor.modelo.NotificacaoDados;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Teste {
    private Retrofit retrofit;
    private String baseUrl;

    public void enviarNotificacao(String token, String titulo, String msg) {
        baseUrl = "https://fcm.googleapis.com/fcm/";
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();




        //Monta objeto notificação
        Notificacao notificacao = new Notificacao(titulo, msg);
        NotificacaoDados notificacaoDados = new NotificacaoDados(token, notificacao);

        NotificacaoService service = retrofit.create(NotificacaoService.class);
        Call<NotificacaoDados> call = service.salvarNotificacao(notificacaoDados);

        call.enqueue(new Callback<NotificacaoDados>() {
            @Override
            public void onResponse(Call<NotificacaoDados> call, Response<NotificacaoDados> response) {


                if (response.isSuccessful()) {



                }
            }

            @Override
            public void onFailure(Call<NotificacaoDados> call, Throwable t) {

            }
        });


    }

    public void recuperarToken() {

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                String token = instanceIdResult.getToken();
                Log.i("getInstanceId", "token getInstanceId: " + token);

            }
        });

    }
}
