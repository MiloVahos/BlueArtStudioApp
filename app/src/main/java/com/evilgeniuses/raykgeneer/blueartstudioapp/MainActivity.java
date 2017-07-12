/**
 * @Developer: Juan Camilo Peña Vahos
 * @Description: Main Activity
 * @Date: 17/05/2017
 * TODO: TIEMPO DE CARGA DEL BOTÓN FLOTANTE CUANDO INICIA EL ADMINISTRADOR
 * TODO: EL SISTEMA DE RIFAS SI ES SOLICITADO
 * TODO: ELIMINAR LAS THIRD PARTIES DE CALIGRAPHY Y CIRCLE IMAGE VIEW
 */

package com.evilgeniuses.raykgeneer.blueartstudioapp;

//Declaración de librerías
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    //Declaración de componentes

    //Declaración de componentes de Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser User;
    private DatabaseReference mRef;

    //Declaración de Componentes del Layout
    private Toolbar MainToolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton FABAdd;
    private int[] tabIcons = { //Iconos de los tab
            R.drawable.ic_bulletin_board_white_36dp,
            R.drawable.ic_skull_white_36dp,
            R.drawable.ic_hanger_white_36dp};

    //Rutas
    private static final String Admin = "Admin";

    //Variables
    private String UID;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //>>>>>>>>>>>>>>>>>>>>>>>>>>CARGAR FIREBASE Y ACTIVAR MODO OFFLINE<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        FirebaseUtils.getDatabase();
        FirebaseMessaging.getInstance().subscribeToTopic("NoticiasNotify");
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>INICIALIZACIÓN DE COMPONENTE<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        MainToolbar = (Toolbar) findViewById(R.id.MainToolbar);
        MainToolbar.setTitle("");
        viewPager = (ViewPager) findViewById(R.id.MainViewPager);
        tabLayout = (TabLayout) findViewById(R.id.MainTabLayout);
        FABAdd = (FloatingActionButton) findViewById(R.id.FABAdd);
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>CONTROL DEL INICIO DE SESIÓN<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        User = mAuth.getCurrentUser();
        if (User == null){//Si User==null significa que no hay usuario loggeado y se envia a la actividad de iniciar sesión
            Intent i = new Intent(MainActivity.this,IniciarSesionActivity.class);
            startActivity(i);
        }else{
            UID = User.getUid();
        }
        //CHEQUEO DE ADMIN
        mRef.child(Admin).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ID = (String) dataSnapshot.getValue();
                if (UID.equals(ID)){
                    FABAdd.setVisibility(View.VISIBLE);
                }else{
                    FABAdd.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>MANEJOR DE LA PARTE VISUAL<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        //MANEJO DE LA TOOLBAR
        setSupportActionBar(MainToolbar);//Se cambia el Action bar por nuestra toolbar
        MainToolbar.setTitle(R.string.MainToolbarName);
        MainToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        //MANEJO DEL VIEWPAGER Y LOS TABS
        setupViewPager(viewPager);//Se configura el ViewPager
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>BOTÓN DE ADMINISTRADOR<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        FABAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se inicia una actividad para añadir nuevas cosas
                Intent i = new Intent(MainActivity.this, AddActivity.class);
                startActivity(i);
            }
        });
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    }

    @Override //Se infla el menú
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.cerrar_sesion){
            //Esta opción del menú es el LogOut de la aplicación, se crear un dialogo de confirmación.
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(R.string.ConfirmCS)
                    .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(MainActivity.this, R.string.LogOut, Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(MainActivity.this, IniciarSesionActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){
                    dialog.dismiss();
                }
            });
            builder.show();
        }else if(id ==  R.id.instagram){
            Uri uri = Uri.parse("https://www.instagram.com/blueartstudio");
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
            likeIng.setPackage("com.instagram.android");
            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/xxx")));
            }
        }else if(id == R.id.facebook){
            Uri uri = Uri.parse("fb://page/191531284250444");
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
            likeIng.setPackage("com.facebook.katana");
            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.facebook.com/blueartstudiotattoo/")));
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() { } //No permite que el usuario retroceda

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]).setText("Noticias");//Bulleting
        tabLayout.getTabAt(1).setIcon(tabIcons[1]).setText("Artistas");//Skull
        tabLayout.getTabAt(2).setIcon(tabIcons[2]).setText("Tienda");//Information
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new NoticiasFragment(), "Noticias");
        adapter.addFrag(new ArtistasFragment(), "Artistas");
        adapter.addFrag(new TiendaFragment(), "Tienda");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
