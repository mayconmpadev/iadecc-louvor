package net.eletroseg.iadecclouvor.util;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import net.eletroseg.iadecclouvor.R;


public class Progresso {
    public static Dialog dialog;

    public static void progressoCircular(Context context) {

        dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.progresso_circular);
        TextView msg = dialog.findViewById(R.id.progress_text_msg);
        // msg.setText(sMsg);
        //instancia os objetos que estão no layout customdialog.xml
        // final ProgressBar progressBar = dialog.findViewById(R.id.dialog_termos_ibtn_fechar);

        dialog.show();

    }
}
