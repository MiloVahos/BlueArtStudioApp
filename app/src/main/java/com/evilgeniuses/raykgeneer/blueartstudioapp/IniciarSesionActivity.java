/**
 * @Developer: Juan Camilo Peña Vahos
 * @Description: Actividad de registro o loggeo a la aplicación
 * @Date: 17/05/2017
 * TODO: Dar la opción de modificar las imagenes de incio de sesión
 */

package com.evilgeniuses.raykgeneer.blueartstudioapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class IniciarSesionActivity extends AppCompatActivity {

    //Declaración de objetos de la vista
    private Button BFacebookLogin;
    private Button BIniciarSesion;
    private Button BRegistrarse;
    private EditText ECorreo;
    private EditText EPassword;
    private ProgressBar PBLoading;

    //Viewpager para el manejo de las imagenes de fondo
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int[] layouts;

    //Objetos de Facebook y Firebase para poder completar el registro
    private CallbackManager callbackManager;
    private FirebaseAuth mAuthFacebook, mAuthEmail; //Autentificación con facebook y firebase

    //Variables
    private String Email;
    private String Pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        //Inicializar
        callbackManager = CallbackManager.Factory.create();//Sirve para el incio de sesión con facebook
        mAuthFacebook = FirebaseAuth.getInstance();
        mAuthEmail = FirebaseAuth.getInstance();
        layouts = new int[]{R.layout.image_slide1,R.layout.image_slide2, R.layout.image_slide3, R.layout.image_slide4};

        //Declaración de elementos
        BFacebookLogin = (Button) findViewById(R.id.BFacebookLogin);
        ECorreo = (EditText) findViewById(R.id.ECorreo);
        EPassword = (EditText) findViewById(R.id.EPass);
        BIniciarSesion = (Button) findViewById(R.id.BIniciarSesion);
        BRegistrarse = (Button) findViewById(R.id.BRegistrarse);
        PBLoading = (ProgressBar) findViewById(R.id.PBLoading);
        getWindow().setBackgroundDrawableResource(R.color.colorAccent);

        //****************************INICIO DE SESIÓN CON FACEBOOK**********************************//
        //Cuando se presiona el botón de facebook, el dispará el este método que
        //asigna al objeto LoginManager los permisos solicitados
        BFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(IniciarSesionActivity.this,
                        Arrays.asList("email","public_profile","user_friends"));
                //Se necesita el email, los datos del perfil y los amigos
                //Este método automáticamente llama al register Callback
            }
        });
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());//Se ejecuta el método
            }
            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),R.string.CancelarLogin, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //Si el intercambio de credenciales fué exitoso, este método es llamado
        //**********************************************************************************************//
        //*******************************REGISTRO CON CORREO Y CONTRASEÑA*******************************//
        BRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtener los datos
                Pass = EPassword.getText().toString().trim();
                Email = ECorreo.getText().toString().trim();
                PBLoading.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(Pass) || TextUtils.isEmpty(Email)){
                    Toast.makeText(getApplicationContext(),R.string.CamposVacios,Toast.LENGTH_SHORT).show();
                }else{
                    Registrarse();
                }
            }
        });
        //Botón de inició de sesión.
        BIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Obtener los datos
                Pass = EPassword.getText().toString().trim();
                Email = ECorreo.getText().toString().trim();
                PBLoading.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(Pass) || TextUtils.isEmpty(Email)){
                    Toast.makeText(getApplicationContext(),R.string.CamposVacios,Toast.LENGTH_SHORT).show();
                }else{
                    IniciarSesion();
                }
            }
        });
        //***************FIN DEL INICIO DE SESIÓN CON CORREO******************************************
        //CONFIGURACIÓN DE LA PARTE VISUAL
        viewPager = (ViewPager) findViewById(R.id.SlidesViewPager);
        changeStatusBarColor(); //Notification bar is now transparent
        myViewPagerAdapter = new MyViewPagerAdapter();//Se crea un objeto de la clase MyViewPager Adapter
        viewPager.setAdapter(myViewPagerAdapter);//Se pasa el objeto al adapter
    }
    //*****************************MÉTODOS RELACIONADOS CON EL INICIO DE SESIÓN CON EMAIL***************************//
    private void IniciarSesion(){
        mAuthEmail.signInWithEmailAndPassword(Email, Pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuthEmail.getCurrentUser();
                            if(user.isEmailVerified()){
                                PBLoading.setVisibility(View.GONE);
                                Toast.makeText(IniciarSesionActivity.this, R.string.Bienvenida, Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(IniciarSesionActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }else{
                                Toast.makeText(IniciarSesionActivity.this, R.string.EmailIsNotVal, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(IniciarSesionActivity.this, R.string.EmailIsNotVal, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void Registrarse(){
        mAuthEmail.createUserWithEmailAndPassword(Email,Pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuthEmail.getCurrentUser();
                            user.sendEmailVerification();
                            PBLoading.setVisibility(View.GONE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(IniciarSesionActivity.this);
                            builder.setMessage(R.string.EmailValidation)
                                    .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                            builder.show();
                        }
                    }
                });
    }
    //*************************************************************************************************************//
    //*****************************MÉTODOS RELACIONADOS CON EL INICIO DE SESIÓN CON FACEBOOK***********************//
    //Este método intercambia la credencial de Facebook por una de Firebase, de este modo se pasa a hacer
    //el login directamente con firebase y no con facebook
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuthFacebook.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(IniciarSesionActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),R.string.Bienvenida,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(IniciarSesionActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }
    //*************************************************************************************************************//
    //**********************************HACE PARTE DE LA CONFIGURACIÓN VISUAL *************************************//
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
    //View pager adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;
        public MyViewPagerAdapter(){}//Constructor

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);//Se infla el layout
            View view = layoutInflater.inflate(layouts[position], container, false);//El layout se carga al objeto view
            container.addView(view);
            return view;//Se retorna el layout
            //El proceso se repite para
        }
        @Override
        public int getCount() {
            return layouts.length;
        }
        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
    //*************************************************************************************************************//
    //*********************************************OVERRIDE METHODS************************************************//
    @Override
    public void onBackPressed() {}//No se puede retroceder
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    //************************************************************************************************************//
}
