/**
 * @Developer: Juan Camilo Peña Vahos
 * @Description: Fragmente para agregar un artista a la base de datos
 * @Date: 17/05/2017
 * TODO:
 */

package com.evilgeniuses.raykgeneer.blueartstudioapp;

//Declaración de librerias
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

import static com.facebook.FacebookSdk.getApplicationContext;

public class AddArtistaFragment extends Fragment {

    //Declaración de componentes
    private ImageButton IBFoto;
    private EditText ENombre;
    private EditText EApodo;
    private EditText EEstilos;
    private EditText EDescripción;
    private EditText ELinkInsta;
    private EditText ELinkFace;
    private Button BPublicar;
    private ProgressDialog mProgress;

    //Componentes de la base de datos
    private DatabaseReference mRef;
    private StorageReference mStorage;

    //Rutas a la base de datos
    private static final String Artistas = "Artistas"; //Tambien es la ruta al storage

    //Constantes
    private static final int GALLERY_REQUEST = 1;

    //Variables
    private String Nombre;
    private String Apodo;
    private String Estilos;
    private String Descripcion;
    private String LinkInsta;
    private String LinkFace;
    private String CodeFace;
    private Uri ImageURI = null;

    public AddArtistaFragment() {
    }//Empty constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_artista, container, false);
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>INSTANCIACIÓN DE COMPONENTES<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        mRef = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        IBFoto = (ImageButton) view.findViewById(R.id.IBFoto);
        ENombre = (EditText) view.findViewById(R.id.ENombre);
        EApodo = (EditText) view.findViewById(R.id.EApodo);
        EEstilos = (EditText) view.findViewById(R.id.EEstilos);
        EDescripción = (EditText) view.findViewById(R.id.EDescripcion);
        ELinkInsta = (EditText) view.findViewById(R.id.EInsta);
        ELinkFace = (EditText) view.findViewById(R.id.EFace);
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
        IBFoto.setOnClickListener(new View.OnClickListener() {
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
        Nombre = ENombre.getText().toString().trim();
        Apodo = EApodo.getText().toString().trim();
        Estilos = EEstilos.getText().toString().trim();
        Descripcion = EDescripción.getText().toString().trim();
        LinkInsta = ELinkInsta.getText().toString().trim();
        LinkFace = ELinkFace.getText().toString().trim();
        CodeFace = "corregir";
        if (!TextUtils.isEmpty(Nombre) && !TextUtils.isEmpty(Apodo) && !TextUtils.isEmpty(Estilos) &&
                !TextUtils.isEmpty(Descripcion) && !TextUtils.isEmpty(LinkFace) && !TextUtils.isEmpty(LinkInsta)
                && (ImageURI != null)) {

            mProgress.setMessage("Posting on App...");
            mProgress.show(); //Muestra el Progress Dialog
            StorageReference filepath = mStorage.child(Artistas).child(Nombre);
            filepath.putFile(ImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {//Se carga al storage
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    //Se publica en la base de datos
                    ArtistaModel artistaModel = new ArtistaModel(Nombre,Apodo,Estilos,Descripcion,downloadUri.toString(),LinkInsta,LinkFace,CodeFace);
                    mRef.child(Artistas).child(Nombre).setValue(artistaModel);
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
            IBFoto.setImageURI(ImageURI);
        }
    }
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}