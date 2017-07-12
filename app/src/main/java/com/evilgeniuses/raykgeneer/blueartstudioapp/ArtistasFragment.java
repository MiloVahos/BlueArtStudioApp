/**
 * @Developer: Juan Camilo Peña Vahos
 * @Description: Fragmento para mostrar los artistas
 * @Date: 6/06/2017
 * TODO:
 */

package com.evilgeniuses.raykgeneer.blueartstudioapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class ArtistasFragment extends Fragment {


    public ArtistasFragment() {
        // Required empty public constructor
    }
    //Declaración de componentes
    private RecyclerView RVArtistas;

    //Componentes de Firebase
    private FirebaseRecyclerAdapter mAdapter;
    private DatabaseReference mRef;

    //Declaración de rutas de la base de datos
    private static final String Artistas = "Artistas";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_artistas, container, false);

        //Instanciación de componentes
        RVArtistas = (RecyclerView) view.findViewById(R.id.RVArtistas);
        RVArtistas.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRef = FirebaseDatabase.getInstance().getReference().child(Artistas);
        mRef.keepSynced(true);//Para mantener los datos cuando offline

        mAdapter = new FirebaseRecyclerAdapter<ArtistaModel, ArtistasHolder>
                (ArtistaModel.class, R.layout.cardrow_artistas, ArtistasFragment.ArtistasHolder.class, mRef) {
            @Override
            protected void populateViewHolder(ArtistasFragment.ArtistasHolder viewHolder, ArtistaModel model, final int position) {
                viewHolder.setNombre(model.getNombre());
                viewHolder.setEstilos(model.getEstilos());
                viewHolder.setImage(getContext(), model.getImageURL());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        final Intent i = new Intent(view.getContext(), ArtistaActivity.class);
                        DatabaseReference databaseRef = mAdapter.getRef(position);
                        databaseRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ArtistaModel artist = dataSnapshot.getValue(ArtistaModel.class);
                                String apodo = artist.getNombre();
                                i.putExtra("NOMBRE", apodo);
                                startActivity(i);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(view.getContext(),R.string.Problema ,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        };
        RVArtistas.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        return view;
    }


    //ViewHolder for firebaseUI pattern to populate recyclerview
    public static class ArtistasHolder extends RecyclerView.ViewHolder{

        View mView;

        public ArtistasHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setNombre(String nombre) {
            TextView TNombre = (TextView) mView.findViewById(R.id.TNombre);
            TNombre.setText(nombre);
        }

        public void setEstilos(String estilos){
            TextView TEstilos = (TextView) mView.findViewById(R.id.TEstilos);
            TEstilos.setText(estilos);
        }

        public void setImage (Context ctx, String image){
            ImageView Imagen = (ImageView) mView.findViewById(R.id.IVFoto);
            Picasso.with(ctx).load(image).fit().into(Imagen);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}
