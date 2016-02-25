package com.example.mapas2.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mapas2.R;
import com.example.mapas2.model.Marcacao;
import com.example.mapas2.model.Usuario;
import com.example.mapas2.resource.DatabaseHelper;
import com.example.mapas2.tasks.UploadJsonTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLException;
import java.util.List;

public class MyMarkerFragment extends Fragment implements OnMapLongClickListener,OnMapClickListener,OnMarkerDragListener {
    private ImageButton icon;
    private GoogleMap map;
    private String email;
    private DatabaseHelper helper = null;
    private double dragLat;
    private double dragLng;
    AlertDialog dialog = null;
    AlertDialog confirmDialog = null;

    public MyMarkerFragment(GoogleMap map, String email){
        this.map = map;
        this.email = email;
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        map.setOnMarkerDragListener(this);
        map.setOnMapLongClickListener(this);
        map.setOnMapClickListener(this);
        View rootView = inflater.inflate(R.layout.fragment_my_marker, container, false);

        helper = DatabaseHelper.getHelper(getActivity().getApplicationContext());
//        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//            @Override
//            public View getInfoWindow(Marker marker) {
//                return null;
//            }
//
//            @Override
//            public View getInfoContents(Marker marker) {
//                //LatLng latLng = marker.getPosition();
//                // Getting view from the layout file info_window_layout
//                View v = inflater.inflate(R.layout.info_window_layout, null);
////                TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
////                TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
////                tvLat.setText("Latitude:" + latLng.latitude);
////                tvLng.setText("Longitude:"+ latLng.longitude);
//                // Returning the view containing InfoWindow contents
//                return v;
//            }
//        });

        icon = (ImageButton)  rootView.findViewById(R.id.btnMenuMarker);
        final String[] option = new String[] { "Falta de Energia", "Acúmulo de Buracos", "Falta de Saneamento", "Acúmulo de Lixo" };
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, option);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Insira sua marcação");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    map.addMarker((new MarkerOptions()
                            .position(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()))
                            .title("Energia").snippet("Segure e arraste para salvar").draggable(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.energy))));
                }
                if(which==1){
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()))
                            .title("Buraco").snippet("Segure e arraste para salvar").draggable(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.hole)));
                }
                if(which==2){
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()))
                            .title("Saneamento").snippet("Segure e arraste para salvar").draggable(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.water)));
                }
                if(which==3){
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()))
                            .title("Lixo").snippet("Segure e arraste para salvar").draggable(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.garbage)));
                }
            }
        });

        dialog = builder.create();

        icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.show();
            }
        });
        return rootView;
    }

    public void saveMarker(Marker marker, double lat, double lng) throws SQLException {
        Marcacao marcacao = new Marcacao();
        marker.setSnippet("Marcação registrada");
        marker.setDraggable(false);
        List<Usuario> userList = helper.getUsuarioDao().queryForEq("email", email);
        if(userList.size() != 0){
            Usuario user = userList.get(0);
            marcacao.setUser(user);
        }
        if(marker.getTitle().equals("Energia")){
            marcacao.setTipoMarcacao(0);
        }
        else if (marker.getTitle().equals("Buraco")){
            marcacao.setTipoMarcacao(1);
        }
        else if(marker.getTitle().equals("Saneamento")){
            marcacao.setTipoMarcacao(2);
        }
        else if(marker.getTitle().equals("Lixo")){
            marcacao.setTipoMarcacao(3);
        }
        marcacao.setLat(lat);
        marcacao.setLng(lng);
        marcacao.setDescricao("Teste");
        //helper.getMarcacaoDao().create(marcacao);
        new UploadJsonTask(marcacao, null).execute();
        Toast.makeText(getActivity().getApplicationContext(), "Sua marcação foi salva!", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onMarkerDrag(Marker arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMarkerDragEnd(final Marker marker) {
        LatLng dragPosition = marker.getPosition();
        dragLat = dragPosition.latitude;
        dragLng = dragPosition.longitude;
        Log.i("info", "on drag end :" + dragLat + " dragLong :" + dragLng);

        final String[] optionConfirm = new String[]{"Sim", "Não"};
        final ArrayAdapter<String> adapterConfirm = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, optionConfirm);
        AlertDialog.Builder builderConfirm = new AlertDialog.Builder(getActivity());
        builderConfirm.setTitle("Confirma a inserção so marcador?");
        builderConfirm.setAdapter(adapterConfirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    try {
                        saveMarker(marker, dragLat, dragLng);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (which == 1) {
                    marker.remove();
                }
            }
        });
        confirmDialog = builderConfirm.create();
        confirmDialog.show();
    }
    @Override
    public void onMarkerDragStart(Marker arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapClick(LatLng arg0) {
        // TODO Auto-generated method stub
        // map.animateCamera(CameraUpdateFactory.newLatLng(arg0));
    }


    @Override
    public void onMapLongClick(LatLng arg0) {
        // TODO Auto-generated method stub

       // dialog.show();
    }

}