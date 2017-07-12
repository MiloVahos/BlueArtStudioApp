package com.evilgeniuses.raykgeneer.blueartstudioapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ExpandImageActivity extends AppCompatActivity {

    private ImageView IVImage;
    private TextView TProducto;
    private TextView TPrecio;

    private String Link;
    private String Type;
    private String Producto;
    private String Precio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_image);

        IVImage = (ImageView) findViewById(R.id.IVImage);
        TPrecio = (TextView) findViewById(R.id.TPrecio);
        TProducto = (TextView) findViewById(R.id.TProducto);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Link = extras.getString("IMAGE");
            Type = extras.getString("TYPE");
            if (Type.equals("Tienda")){
                Producto = extras.getString("PRODUCTO");
                Precio = extras.getString("PRECIO");
            }
        }else{
            Toast.makeText(getApplicationContext(),"Something went wrong!!", Toast.LENGTH_SHORT).show();
            finish();
        }

        Picasso.with(this).load(Link).into(IVImage);

        if (Type.equals("Tienda")){
            TProducto.setVisibility(View.VISIBLE);
            TProducto.setText(Producto);
            TPrecio.setVisibility(View.VISIBLE);
            TPrecio.setText(Precio);
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
