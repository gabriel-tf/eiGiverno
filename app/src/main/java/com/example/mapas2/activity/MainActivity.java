package com.example.mapas2.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.mapas2.R;
import com.example.mapas2.fragments.AboutFragment;
import com.example.mapas2.fragments.HomeFragment;
import com.example.mapas2.fragments.LoginFragment;
import com.example.mapas2.fragments.LogoutFragment;
import com.example.mapas2.fragments.MyMarkerFragment;
import com.example.mapas2.fragments.RegisterFragment;
import com.example.mapas2.listener.JsonListener;
import com.example.mapas2.listener.UsuarioListener;
import com.example.mapas2.model.Usuario;
import com.example.mapas2.tasks.BuscaUsuarioTask;
import com.example.mapas2.tasks.DownloadJsonTask;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mapas2.adapter.NavDrawerListAdapter;
import com.example.mapas2.listener.ChangeFragmentListener;
import com.example.mapas2.model.Marcacao;
import com.example.mapas2.model.NavDrawerItem;
import com.example.mapas2.resource.DatabaseHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity implements JsonListener{
    private GoogleMap map;
    private LocationManager locationManager;
    private LatLng latLng;

    private boolean logado;
    private String username;
    private String email;
    private DatabaseHelper helper = null;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    // nav drawer title
    private CharSequence mDrawerTitle;
    // used to store app title
    private CharSequence mTitle;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        helper =  DatabaseHelper.getHelper(this);
        setContentView(R.layout.main);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        createMap();
        createMenu();

        if (arg0 == null) {
            // on first time display view for first nav item
            displayView(0);
        }

        if(isConnected()) {
            new DownloadJsonTask(this, this).execute();
        }else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(R.string.faltaConexao);
            alertDialogBuilder.setPositiveButton(R.string.btnConfigText,
                    new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    });
            alertDialogBuilder.setNegativeButton(R.string.btnNaoText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            logado = extras.getBoolean("true");
            username = extras.getString("username");
            email = extras.getString("email");
            createMenu();
            Log.e("Nome:",username+"!");
            Log.e("LOGADO",logado+"!");
            Log.e("email",email+"!");
        }
    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if(info == null){
            return false;
        }
        return info.isConnected();
    }


    private void createMap() {
        if (map == null) {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            requestGPS();
        }
        else{
            Toast.makeText(getApplicationContext(),"Desculpe! Não foi possível criar o mapa.", Toast.LENGTH_SHORT).show();
        }
    }

    public void requestGPS(){
        map.setMyLocationEnabled(true);
        if (map != null) {
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!gpsEnabled){
                gpsOffAlert();
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }else{
                //gpsOnAlert();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    public final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            locationManager.removeUpdates(locationListener);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    private void createMenu(){
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();
        if(logado) {
            // adding nav drawer items to array
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));

            navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));

            navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));

            navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
        }
        else{
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));

            navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));

            navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));

            navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        }
        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayUseLogoEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.icon_menu, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        if(logado) {
            switch (position) {
                case 0:
                    fragment = new HomeFragment();
                    setTitle(navMenuTitles[0]);
                    break;
                case 1:
                    fragment = new MyMarkerFragment(map, email);
                    setTitle(navMenuTitles[1]);
                    break;
                case 2:
                    fragment = new AboutFragment();
                    setTitle(navMenuTitles[4]);
                    break;
                case 3:
                    fragment = new LogoutFragment();
                    setTitle(navMenuTitles[5]);
                    break;
                default:
                    break;
            }
        }else{
            switch (position) {
                case 0:
                    fragment = new HomeFragment();
                    setTitle(navMenuTitles[0]);
                    break;
                case 1:
                    LoginFragment loginFragment = new LoginFragment();
                    loginFragment.setChanger(openMenu);
                    fragment = loginFragment;
                    setTitle(navMenuTitles[2]);
                    break;
                case 2:
                    RegisterFragment registerFragment = new RegisterFragment();
                    registerFragment.setChanger(openMenu);
                    fragment = registerFragment;
                    setTitle(navMenuTitles[3]);
                    break;
                case 3:
                    fragment = new AboutFragment();
                    setTitle(navMenuTitles[4]);
                    break;
                default:
                    break;
            }
        }
        if (fragment != null) {
            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
            // update selected item then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

//    private ChangeFragmentListener changeToMyMarker = new ChangeFragmentListener() {
//        @Override
//        public void changeFragment() {
//            Fragment fragment;
//            fragment = new MyMarkerFragment(map, email);
//            setTitle(navMenuTitles[1]);
//            android.app.FragmentManager fragmentManager = getFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.frame_container, fragment).commit();
//            mDrawerLayout.closeDrawer(mDrawerList);
//        }
//    };

    private ChangeFragmentListener openMenu = new ChangeFragmentListener() {
        @Override
        public void changeFragment() {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.normal:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite:
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain:
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.normal).setVisible(!drawerOpen);
        menu.findItem(R.id.hybrid).setVisible(!drawerOpen);
        menu.findItem(R.id.satellite).setVisible(!drawerOpen);
        menu.findItem(R.id.terrain).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void gpsOffAlert(){
        Toast.makeText(MainActivity.this, "Ative o GPS para maior precisão", Toast.LENGTH_LONG).show();
    }

    //public void gpsOnAlert(){
    //    Toast.makeText(MainActivity.this, "Buscando posição", Toast.LENGTH_LONG).show();
    //}

    @Override
    public void dadosRecebidos(List<MarkerOptions> status) {
        if(status != null) {
            for (int i = 1; i <= status.size(); i++) {
                map.addMarker(status.get(i - 1));
            }
        }else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(R.string.webServiceForaDoAr);
            alertDialogBuilder.setNegativeButton(R.string.btnNaoText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    }
}