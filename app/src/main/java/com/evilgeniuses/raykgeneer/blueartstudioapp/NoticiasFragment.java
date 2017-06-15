/**
 * @Developer: Juan Camilo Peña Vahos
 * @Description: Fragmento para mostrar las novedades
 * @Date: 18/05/2017
 * TODO:
 */
package com.evilgeniuses.raykgeneer.blueartstudioapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class NoticiasFragment extends Fragment {

    public NoticiasFragment() {}

    //Declaración de componentes
    private RecyclerView RVNoticias;

    //Componentes de Firebase
    private FirebaseRecyclerAdapter mAdapter;
    private DatabaseReference mRef;

    //Declaración de variables
    private String ImageURI;

    //Declaración de rutas de la base de datos
    private static final String Noticias = "Noticias";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_noticias, container, false);

        //Instanciación de componentes
        RVNoticias = (RecyclerView) view.findViewById(R.id.RVNoticias);
        RVNoticias.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRef = FirebaseDatabase.getInstance().getReference().child(Noticias);
        mRef.keepSynced(true);//Para mantener los datos cuando offline

        Query queryRef = mRef.orderByChild("valor").limitToFirst(20);
        mAdapter = new FirebaseRecyclerAdapter<NoticiaModel, NoticiaHolder>(NoticiaModel.class,
                R.layout.cardrow_noticia,   NoticiaHolder.class, queryRef) {
            @Override
            protected void populateViewHolder(NoticiaHolder viewHolder, NoticiaModel model, final int position) {
                ImageURI = model.getImageURL();
                viewHolder.setTitulo(model.getTitulo());
                viewHolder.setFecha(model.getFecha());
                if (ImageURI != null){
                    viewHolder.setImage(getContext(),model.getImageURL(),true);
                }else{
                    viewHolder.setImage(getContext(),model.getImageURL(),false);
                }
            }
        };
        RVNoticias.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        return view;
    }


    //ViewHolder for firebaseUI pattern to populate recyclerview
    public static class NoticiaHolder extends RecyclerView.ViewHolder{

        View mView;

        public NoticiaHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitulo(String titulo) {
            TextView Titulo = (TextView) mView.findViewById(R.id.TTitulo);
            Titulo.setText(titulo);
        }

        public void setFecha(String fecha){
            TextView Fecha = (TextView) mView.findViewById(R.id.TFecha);
            Fecha.setText(fecha);
        }

        public void setImage (Context ctx, String image, boolean ImageExist){
            ImageView Imagen = (ImageView) mView.findViewById(R.id.IVFoto);
            if (ImageExist){
                Imagen.setVisibility(View.VISIBLE);
                Picasso.with(ctx).load(image).fit().into(Imagen);
            }else{
                Imagen.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

}
