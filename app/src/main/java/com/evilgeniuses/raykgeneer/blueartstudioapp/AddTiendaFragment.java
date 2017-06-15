/**
 * @Developer: Juan Camilo Peña Vahos
 * @Description: Fragment para agregar nuevos productos a la tienda
 * @Date: 18/05/2017
 * TODO:
 */

package com.evilgeniuses.raykgeneer.blueartstudioapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AddTiendaFragment extends Fragment {

    public AddTiendaFragment() {
    }

    //Declaración de componentes
    private ImageButton IBProducto;
    private EditText EProducto;
    private EditText EPrecio;
    private Button BPublicar;
    private ProgressDialog mProgress;

    //Componentes de la base de datos
    private DatabaseReference mRef;
    private StorageReference mStorage;

    //Rutas a la base de datos
    private static final String Tienda = "Tienda"; //Tambien es la ruta al storage

    //Constantes
    private static final int GALLERY_REQUEST = 1;

    //Variables
    private String Producto;
    private String Precio;
    private String UniqueID;
    private Uri ImageURI = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_tienda, container, false);

        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>INSTANCIACIÓN DE COMPONENTES<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        mRef = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        IBProducto = (ImageButton) view.findViewById(R.id.IBProducto);
        EProducto = (EditText) view.findViewById(R.id.EProducto);
        EPrecio = (EditText) view.findViewById(R.id.EPrecio);
        BPublicar = (Button) view.findViewById(R.id.BPublicar);
        mProgress = new ProgressDialog(view.getContext());
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>ON CLICK DEL BOTÓN PUBLICAR<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        BPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>BOTÓN PARA AGREGAR LA IMAGEN<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        IBProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        return view;
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>MÉTODO PARA PUBLICAR<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    private void startPosting() {
        //SE OBTIENEN LOS DATOS
        Producto = EProducto.getText().toString().trim();
        Precio = "$"+EPrecio.getText().toString().trim();
        UniqueID = UUID.randomUUID().toString();//Unique ID of the post
        if (!TextUtils.isEmpty(Producto) && !TextUtils.isEmpty(Precio) && (ImageURI != null)) {
            mProgress.setMessage("Posting on App...");
            mProgress.show(); //Muestra el Progress Dialog
            StorageReference filepath = mStorage.child(Tienda).child(UniqueID);
            filepath.putFile(ImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {//Se carga al storage
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    //Se publica en la base de datos
                    TiendaModel tiendaModel = new TiendaModel(Producto, Precio, downloadUri.toString());
                    mRef.child(Tienda).child(UniqueID).setValue(tiendaModel);
                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.PostCargado, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//Se carga nuevamente la interfaz principal sin
                    startActivity(i);//la posibilidad de volver atrás
                }
            });
        } else {
            mProgress.dismiss();
            Toast.makeText(getApplicationContext(), R.string.MensajeCamposTexto, Toast.LENGTH_SHORT).show();
        }
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>OVERRIDE METHODS<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == getActivity().RESULT_OK) {
            ImageURI = data.getData();
            IBProducto.setImageURI(ImageURI);
        }
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}