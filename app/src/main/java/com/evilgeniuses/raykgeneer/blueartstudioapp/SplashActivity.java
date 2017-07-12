/**
 * @Developer: Juan Camilo Peña Vahos
 * @Date: 12/07/2017
 * @Description: Esta actividad muestra el logo de blue al inciio de la aplicación y luego inicia la actividad principal
 */

package com.evilgeniuses.raykgeneer.blueartstudioapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
    }
}
