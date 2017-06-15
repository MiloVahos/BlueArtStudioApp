/**
 * @Developer: Juan Camilo Peña Vahos
 * @Description: Actividad con fragments para agregar nuevas cosas a la base de datos
 * @Date: 17/05/2017
 * TODO:
 */

package com.evilgeniuses.raykgeneer.blueartstudioapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddActivity extends AppCompatActivity {

    //Declaración de componentes
    //Declaración de Componentes del Layout
    private ViewPager AddViewPager;
    private TabLayout AddTabLaytout;
    private int[] tabIcons = { //Iconos de los tab
            R.drawable.ic_bulletin_board_white_36dp,
            R.drawable.ic_skull_white_36dp,
            R.drawable.ic_hanger_white_36dp};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>INICIALIZACIÓN DE COMPONENTE<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        AddViewPager = (ViewPager) findViewById(R.id.AddViewPager);
        AddTabLaytout = (TabLayout) findViewById(R.id.AddTabLayout);
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>MANEJOR DE LA PARTE VISUAL<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        //MANEJO DEL VIEWPAGER Y LOS TABS
        setupViewPager(AddViewPager);//Se configura el ViewPager
        AddTabLaytout.setupWithViewPager(AddViewPager);
        setupTabIcons();
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    }
    private void setupTabIcons() {
        AddTabLaytout.getTabAt(0).setIcon(tabIcons[0]).setText("Noticias");//Bulleting
        AddTabLaytout.getTabAt(1).setIcon(tabIcons[1]).setText("Artistas");//Skull
        AddTabLaytout.getTabAt(2).setIcon(tabIcons[2]).setText("Tienda");//Information
    }

    private void setupViewPager(ViewPager viewPager) {
        AddActivity.ViewPagerAdapter adapter = new AddActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new AddNoticiaFragment(), "Noticias");
        adapter.addFrag(new AddArtistaFragment(), "Artistas");
        adapter.addFrag(new AddTiendaFragment(), "Tienda");
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
