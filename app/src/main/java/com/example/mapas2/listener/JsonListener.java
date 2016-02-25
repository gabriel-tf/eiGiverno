package com.example.mapas2.listener;

import com.example.mapas2.model.Usuario;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by windows on 22/11/2014.
 */
public interface JsonListener {

    void dadosRecebidos(List<MarkerOptions> status);

}
