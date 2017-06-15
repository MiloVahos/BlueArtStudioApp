/**
 * @Developer: Juan Camilo Peña Vahos
 * @Description: Application class
 * @Date: 17/05/2017
 * TODO:
 */

package com.evilgeniuses.raykgeneer.blueartstudioapp;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MyAppClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/nexa_light.OTF")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
