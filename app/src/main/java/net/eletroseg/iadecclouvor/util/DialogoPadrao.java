package net.eletroseg.iadecclouvor.util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.eletroseg.iadecclouvor.R;


/**
 * Created by maycon on 20/06/2018.
 */

public class DialogoPadrao {

    public static void exibeDialogNivel(Context context, final String sTitulo, String menssagem) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_sair);
        //instancia os objetos que estão no layout customdialog.xml
        final TextView msg = (TextView) dialog.findViewById(R.id.text_sair_msg);
        final Button cancelar = (Button) dialog.findViewById(R.id.btn_sair_cancelar);
        final Button ok = (Button) dialog.findViewById(R.id.btn_sair_ok);

        msg.setText(menssagem);
        dialog.setTitle(sTitulo);
        cancelar.setVisibility(View.GONE);

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        //exibe na tela o dialog
        dialog.show();
    }
}
