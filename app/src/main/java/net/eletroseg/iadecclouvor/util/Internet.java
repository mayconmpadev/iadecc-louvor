package net.eletroseg.iadecclouvor.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by maycon on 08/04/2018.
 */

public class Internet extends Activity {

    Context context = this;

    @SuppressLint("WrongConstant")
    public boolean haveNetworkConnection() {


        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        for (NetworkInfo ni : ((ConnectivityManager) this.context.getSystemService("connectivity")).getAllNetworkInfo()) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI") && ni.isConnected()) {
                haveConnectedWifi = true;
            }

            if (ni.getTypeName().equalsIgnoreCase("MOBILE") && ni.isConnected()) {
                haveConnectedMobile = true;
            }
        }
        if (haveConnectedWifi || haveConnectedMobile) {

            return true;
        }
        return false;

    }
}
