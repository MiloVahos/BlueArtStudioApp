//Ingeniero: Juan Camilo Peña Vahos
//Fecha: 23/03/2017
//Descripción: Esta clase contiene funciones utiles y que se pueden modelar de Firebase

package com.evilgeniuses.raykgeneer.blueartstudioapp;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {

    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

}
