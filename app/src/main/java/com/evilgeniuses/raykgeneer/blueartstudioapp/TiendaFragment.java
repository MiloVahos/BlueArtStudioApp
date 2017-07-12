/**
 * @Developer: Juan Camilo Peña Vahos
 * @Description: Fragmento para mostrar los productos de la tienda
 * @Date: 7/06/2017
 */

package com.evilgeniuses.raykgeneer.blueartstudioapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
        RVTienda.setHasFixedSize(true);
        RVTienda.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));

        mRef = FirebaseDatabase.getInstance().getReference().child(Tienda);
        mRef.keepSynced(true);//Para mantener los datos cuando offline

        mAdapter = new FirebaseRecyclerAdapter<TiendaModel, TiendaFragment.TiendaHolder>
                (TiendaModel.class, R.layout.cardrow_tienda, TiendaFragment.TiendaHolder.class, mRef) {
            @Override
            protected void populateViewHolder(TiendaFragment.TiendaHolder viewHolder, TiendaModel model, final int position) {
                viewHolder.setImage(getContext(),model.getImageURL());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final DatabaseReference databaseReference = mAdapter.getRef(position);
                        final Intent i = new Intent(getContext(), ExpandImageActivity.class);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                TiendaModel model = dataSnapshot.getValue(TiendaModel.class);
                                i.putExtra("IMAGE", model.getImageURL());
                                i.putExtra("PRODUCTO", model.getProducto());
                                i.putExtra("PRECIO", model.getPrecio());
                                i.putExtra("TYPE", "Tienda");
                                startActivity(i);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                });
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
