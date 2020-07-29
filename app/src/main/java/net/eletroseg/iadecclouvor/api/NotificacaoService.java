package net.eletroseg.iadecclouvor.api;






import net.eletroseg.iadecclouvor.modelo.NotificacaoDados;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificacaoService {

    @Headers({
            "Authorization:key=AAAAiDSMKZQ:APA91bHvqAfnB8vJ3KhodDbOX-AsmP8qyZIYrrpX8mM1gAs02zlgCY0mY-X9QmXsoAOq5Gx8TZyBCx3Go9pLbpjS-fu1PCijQoPwpnMjCyfo8Wxhs-9bKXeNiCkuHrK5MV9jtZnLK5VJ",
            "Content-Type:application/json"
    })
    @POST("send")
    Call<NotificacaoDados> salvarNotificacao(@Body NotificacaoDados notificacaoDados);

}
