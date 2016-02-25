package com.example.mapas2.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mapas2.R;

public class ConsumirJsonFragment extends Fragment{



    public static ConsumirJsonFragment newInstance() {
        ConsumirJsonFragment fragment = new ConsumirJsonFragment();

        return fragment;
    }
    public ConsumirJsonFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_consumir_json, container, false);
       // new DownloadJsonTask(this).execute();
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    //@Override
    //public void dadosRecebidos(String status) {
    //    TextView resultadoJson = (TextView) getView().findViewById(R.id.resultadoJson);
    //    resultadoJson.setText(status);
   // }
}
