package net.eletroseg.iadecclouvor.util;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by maycon on 26/05/2018.
 */

public class InstanciaFirebase {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            //obs
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

}
