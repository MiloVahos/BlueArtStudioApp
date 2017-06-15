/**
 * @Developer: Juan Camilo Peña Vahos
 * @Description: Fragmento para mostrar los productos de la tienda
 * @Date: 7/06/2017
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
import com.squareup.picasso.Picasso;

public class TiendaFragment extends Fragment {

    public TiendaFragment() {}

    //Declaración de componentes
    private RecyclerView RVTienda;

    //Componentes de Firebase
    private FirebaseRecyclerAdapter mAdapter;
    private DatabaseReference mRef;

    //Declaración de rutas de la base de datos
    private static final String Tienda = "Tienda";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tienda, container, false);

        //Instanciación de componentes
        RVTienda = (RecyclerView) view.findViewById(R.id.RVTienda);
        RVTienda.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRef = FirebaseDatabase.getInstance().getReference().child(Tienda);
        mRef.keepSynced(true);//Para mantener los datos cuando offline

        mAdapter = new FirebaseRecyclerAdapter<TiendaModel, TiendaFragment.TiendaHolder>
                (TiendaModel.class, R.layout.cardrow_tienda, TiendaFragment.TiendaHolder.class, mRef) {
            @Override
            protected void populateViewHolder(TiendaFragment.TiendaHolder viewHolder, TiendaModel model, int position) {
                viewHolder.setProducto(model.getProducto());
                viewHolder.setPrecio(model.getPrecio());
                viewHolder.setImage(getContext(),model.getImageURL());
            }
        };
        RVTienda.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        return view;
    }

    //ViewHolder for firebaseUI pattern to populate recyclerview
    public static class TiendaHolder extends RecyclerView.ViewHolder{

        View mView;

        public TiendaHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProducto(String producto) {
            TextView TProducto = (TextView) mView.findViewById(R.id.TProducto);
            TProducto.setText(producto);
        }

        public void setPrecio(String precio){
            TextView TPrecio = (TextView) mView.findViewById(R.id.TPrecio);
            TPrecio.setText(precio);
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
