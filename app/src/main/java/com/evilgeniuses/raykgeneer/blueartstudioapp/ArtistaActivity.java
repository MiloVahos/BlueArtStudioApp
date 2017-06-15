package com.evilgeniuses.raykgeneer.blueartstudioapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ArtistaActivity extends AppCompatActivity {

    //DECLARACIÓN DE COMPONENTES
    private ImageView IVFoto;
    private TextView TNombre;
    private TextView TEstilos;
    private TextView TDescripcion;
    private RecyclerView RVTatuajes;
    private Button BFacebookLink;
    private Button BInstagramLink;

    //COMPONENTES DE LA BASE DE DATOS
    private DatabaseReference mRef;
    private DatabaseReference mRefTatuajes;
    private FirebaseRecyclerAdapter mAdapter;

    //Declaración de rutas de la base de datos
    private static final String Artistas = "Artistas";
    private static final String Tatuajes = "Tatuajes";

    //DECLARACIÓN DE VARIABLES
    private String Nombre;
    private String InstagramLink;
    private String FacebookLink;
    private String FacebookCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artista);

        //INSTANCIACIÓN DE COMPONENTES
        IVFoto = (ImageView) findViewById(R.id.IVFoto);
        TNombre = (TextView) findViewById(R.id.TNombre);
        TEstilos = (TextView) findViewById(R.id.TEstilos);
        TDescripcion = (TextView) findViewById(R.id.TDescripcion);
        BFacebookLink = (Button) findViewById(R.id.BFacebookLink);
        BInstagramLink = (Button) findViewById(R.id.BInstagramLink);
        mRef = FirebaseDatabase.getInstance().getReference();
        RVTatuajes = (RecyclerView) findViewById(R.id.RVTatuajes);
        RVTatuajes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //GETTING DE NOMBRE
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Nombre = extras.getString("NOMBRE");
        }else{
            Toast.makeText(getApplicationContext(),R.string.Problema , Toast.LENGTH_SHORT).show();
            finish();
        }

        mRef.child(Artistas).child(Nombre).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArtistaModel artistaModel = dataSnapshot.getValue(ArtistaModel.class);
                String Nombre = artistaModel.getNombre();
                String Estilos = artistaModel.getEstilos();
                String Descripcion = artistaModel.getDescripcion();
                String FotoURL = artistaModel.getImageURL();
                InstagramLink = artistaModel.getLinkInsta();
                FacebookLink = artistaModel.getLinkFace();
                FacebookCode = artistaModel.getFaceCode();
                BFacebookLink.setText(Nombre);
                BInstagramLink.setText("@"+InstagramLink);

                TNombre.setText(Nombre);
                TEstilos.setText(Estilos);
                TDescripcion.setText(Descripcion);
                Picasso.with(ArtistaActivity.this).load(FotoURL).fit().into(IVFoto);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        mRefTatuajes =  FirebaseDatabase.getInstance().getReference().child(Tatuajes).child(Nombre);
        mRefTatuajes.keepSynced(true);

        mAdapter = new FirebaseRecyclerAdapter<Model, TatuajesHolder>(Model.class,
                R.layout.cardrow_tatuajes, TatuajesHolder.class, mRefTatuajes) {

            @Override
            protected void populateViewHolder(final TatuajesHolder viewHolder, Model model, final int position) {
                viewHolder.setImage(getApplicationContext(),model.getLink());

                /*viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final DatabaseReference databaseReference = mAdapter.getRef(position);
                        final Intent i = new Intent(TatuadorProfileActivity.this, ViewArtistDesignActivity.class);
                        databaseReference.child("Link").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String ImageLink = (String) dataSnapshot.getValue();
                                i.putExtra("LINK", ImageLink);
                                startActivity(i);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                });*/
            }
        };
        RVTatuajes.setAdapter(mAdapter);

        BInstagramLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://instagram.com/_u/"+InstagramLink);
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage("com.instagram.android");
                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/xxx")));
                }
            }
        });

        BFacebookLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("fb://profile/"+FacebookCode);
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage("com.facebook.katana");
                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.facebook.com/" + FacebookLink)));
                }
            }
        });

    }

    //ViewHolder for firebaseUI pattern to populate recyclerview
    public static class TatuajesHolder extends RecyclerView.ViewHolder{

        View mView;

        public TatuajesHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setImage (Context ctx, String image){
            ImageView Imagen = (ImageView) mView.findViewById(R.id.IVDesignCard);
            Picasso.with(ctx).load(image).resize(120,120).into(Imagen);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    public static class Model {
        private String Link;
        public Model() {}
        public Model(String Link) {
            this.Link = Link;
        }
        public String getLink() {
            return Link;
        }

    }

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /*
        Intent facebookIntent = getOpenFacebookIntent(getContext());
                startActivity(facebookIntent);
     */

    /*public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://profile/191531284250444")); //Trys to make intent with FB's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/blueartstudiotattoo")); //catches and opens a url to the desired page
        }
    }*/

     /*BLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://instagram.com/_u/camilo.vahos");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/xxx")));
                }
            }
        });

        BFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = getOpenFacebookIntent(getContext());
                startActivity(facebookIntent);
            }
        });*/

}
