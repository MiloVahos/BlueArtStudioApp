/**
 * @Developer: Juan Camilo Peña Vahos
 * @Description: Fragmente para agregar una novedad a la base de datos
 * @Date: 17/05/2017
 * TODO:
 */

package com.evilgeniuses.raykgeneer.blueartstudioapp;

//Librerias
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AddNoticiaFragment extends Fragment {

    //Declaración de componentes
    private ImageButton IBNoticia;
    private EditText ETitulo;
    private EditText EDescripción;
    private Button BPublicar;
    private ProgressDialog mProgress;

    //Componentes de la base de datos
    private DatabaseReference mRef;
    private StorageReference mStorage;

    //Rutas a la base de datos
    private static final String Config = "Config";
    private static final String ContNews = "ContNews";
    private static final String ContValor = "ContValor";
    private static final String Noticias = "Noticias"; //Tambien es la ruta al storage
    private static final String Noticia = "Noticia";

    //Constantes
    private static final int GALLERY_REQUEST = 1;

    //Variables
    private String Titulo;
    private String Descripcion;
    private String Fecha;
    private String UniqueID;
    private Uri ImageURI = null;
    private int ContNew;
    private int ContVal;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_noticia, container, false);

        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>INSTANCIACIÓN DE COMPONENTES<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        mRef = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        IBNoticia = (ImageButton) view.findViewById(R.id.IBNoticia);
        ETitulo = (EditText) view.findViewById(R.id.ETitulo);
        EDescripción = (EditText) view.findViewById(R.id.EDescripcion);
        BPublicar = (Button) view.findViewById(R.id.BPublicar);
        mProgress = new ProgressDialog(view.getContext());
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>SE OBTIENE EL CONTADOR<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        obtenerContador();
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
        IBNoticia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent =  new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        return view;
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>MÉTODO PARA PUBLICAR<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    private void startPosting(){
        //SE OBTIENEN LOS DATOS
        Titulo = ETitulo.getText().toString().trim();
        Descripcion = EDescripción.getText().toString().trim();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        Fecha = sdf.format(c.getTime());
        UniqueID = UUID.randomUUID().toString();//Unique ID of the post

        //Hay dos tipos de publicación una con imagen y otra sin imagen
        //1.Publicación con imagen
        if(!TextUtils.isEmpty(Titulo) && !TextUtils.isEmpty(Descripcion) &&  (ImageURI != null)){
            mProgress.setMessage("Posting on App...");
            mProgress.show(); //Muestra el Progress Dialog
            StorageReference filepath =  mStorage.child(Noticias).child(UniqueID);
            filepath.putFile(ImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {//Se carga al storage
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    //Se publica en la base de datos
                    NoticiaModel noticiaModel = new NoticiaModel(Titulo,Descripcion,downloadUri.toString(),Fecha,ContVal);
                    mRef.child(Noticias).child(Noticia+Integer.toString(ContNew)).setValue(noticiaModel);//Se carga la novedad
                    if(ContNew == 20){ContNew = 1;}else{ContNew++;}
                    ContVal--;
                    mRef.child(Config).child(ContNews).setValue(ContNew);//Se actualiza el contador
                    mRef.child(Config).child(ContValor).setValue(ContVal);
                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(),R.string.PostCargado,Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//Se carga nuevamente la interfaz principal sin
                    startActivity(i);//la posibilidad de volver atrás
                }
            });
        /*}else if(!TextUtils.isEmpty(Titulo) && !TextUtils.isEmpty(Descripcion) &&  (ImageURI == null)){
            mProgress.setMessage("Posting on App...");
            mProgress.show();
            //En este caso no hay imagen
            NoticiaModel noticiaModel = new NoticiaModel(Titulo,Descripcion, null, Fecha, ContVal);
            mRef.child(Noticias).child(Noticia+Integer.toString(ContNew)).setValue(noticiaModel);//Se carga la novedad
            if(ContNew == 20){ContNew = 1;}else{ContNew++;}
            ContVal--;
            mRef.child(Config).child(ContNews).setValue(ContNew);//Se actualiza el contador
            mRef.child(Config).child(ContValor).setValue(ContVal);

            mProgress.dismiss();
            Toast.makeText(getApplicationContext(),R.string.PostCargado,Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//Se carga nuevamente la interfaz principal sin
            startActivity(i);//la posibilidad de volver atrás*/
        }else{
            mProgress.dismiss();
            Toast.makeText(getApplicationContext(),R.string.MensajeCamposTexto,Toast.LENGTH_SHORT).show();
        }
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>OBTENER EL CONTADOR<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    private void obtenerContador(){
        mRef.child(Config).child(ContNews).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ContNew = dataSnapshot.getValue(Integer.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        mRef.child(Config).child(ContValor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ContVal = dataSnapshot.getValue(Integer.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>OVERRIDE METHODS<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST &&  resultCode == getActivity().RESULT_OK){
            ImageURI = data.getData();
            IBNoticia.setImageURI(ImageURI);
        }
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}
