/**
 * @Developer: Juan Camilo Peña Vahos
 * @Description: Fragmento para mostrar las novedades
 * @Date: 30/06/2017
 */
package com.evilgeniuses.raykgeneer.blueartstudioapp;

//Librerias
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NoticiasFragment extends Fragment {

    public NoticiasFragment() {}

    //Declaración de componentes
    private RecyclerView RVNoticias;

    //Componentes de Firebase
    private FirebaseRecyclerAdapter mAdapter;
    private DatabaseReference mRef;

    //Facebook
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;

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
            protected void populateViewHolder(NoticiaHolder viewHolder, final NoticiaModel model, final int position) {
                ImageURI = model.getImageURL();
                viewHolder.setTitulo(model.getTitulo());
                viewHolder.setFecha(model.getFecha());
                viewHolder.setDescripcion(model.getDescripcion());
                viewHolder.setImage(getContext(),model.getImageURL());
                viewHolder.BCompartir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentUrl(Uri.parse(model.getImageURL()))
                                    .build();
                            shareDialog.show(linkContent);
                        }else{
                            Toast.makeText(getApplicationContext(),R.string.Problema,Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        };
        RVNoticias.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        return view;
    }
    //*************VIEWHOLDER CLASS FOR FIREBASEUI PATTERN TO POPULATE RV**************************//
    public static class NoticiaHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView TTitulo;
        private TextView TFecha;
        private TextView TDescripcion;
        private View VSpace;
        public final ImageView IVImagen;
        public Button BCompartir;
        private Button BVerMas;
        private final ImageView IVLoading;
        Animation an = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);

        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                IVLoading.clearAnimation();
                IVLoading.setVisibility(View.GONE);
                IVImagen.setImageBitmap(bitmap);
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {}
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                IVLoading.setVisibility(View.VISIBLE);
                IVLoading.startAnimation(an);
            }
        };

        public NoticiaHolder(View itemView) {
            super(itemView);
            TTitulo = (TextView) itemView.findViewById(R.id.TTitulo);
            TFecha = (TextView) itemView.findViewById(R.id.TFecha);
            TDescripcion = (TextView) itemView.findViewById(R.id.TDescripcion);
            IVImagen = (ImageView) itemView.findViewById(R.id.IVFoto);
            BCompartir = (Button) itemView.findViewById(R.id.BCompartir);
            BVerMas = (Button) itemView.findViewById(R.id.BVerMas);
            IVLoading = (ImageView) itemView.findViewById(R.id.IVLoading);
            VSpace = itemView.findViewById(R.id.VSpace);
            BVerMas.setOnClickListener(this);
        }

        public void setTitulo(String titulo) {
            TTitulo.setText(titulo);
        }

        public void setFecha(String fecha){
            TFecha.setText(fecha);
        }

        public void setDescripcion(String descripcion){
            TDescripcion.setText(descripcion);
        }

        public void setImage (Context ctx, String image){
            Picasso.with(ctx).load(image).into(target);
        }
        @Override
        public void onClick(View v) {
            String Btext = BVerMas.getText().toString();
            String Stext = v.getContext().getString(R.string.VerMenos);
            if(Btext.equals(Stext)){
                VSpace.setVisibility(View.GONE);
                TDescripcion.setVisibility(View.GONE);
                BVerMas.setText(R.string.VerMas);
            }else {
                VSpace.setVisibility(View.VISIBLE);
                TDescripcion.setVisibility(View.VISIBLE);
                BVerMas.setText(R.string.VerMenos);
            }
        }
    }
    //*********************************************************************************************//
    //****************************OVERRIDE METHODS*************************************************//
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);//Se crea el diálogo para compartir contenido en facebook
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
    //*********************************************************************************************//
}
