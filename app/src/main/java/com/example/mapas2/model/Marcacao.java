package com.example.mapas2.model;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by windows on 06/11/2014.
 */
@DatabaseTable()
public class Marcacao implements Serializable{

    private static final long serialVersionUID = 1L;

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true)
    private Usuario user;

    @DatabaseField(canBeNull = false)
    private Double lat;
    @DatabaseField(canBeNull = false)
    private Double lng;
    @DatabaseField(canBeNull = false)
    private String descricao;
    @DatabaseField(canBeNull = false)
    private int tipoMarcacao;

    public Marcacao(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public int getTipoMarcacao() {
        return tipoMarcacao;
    }

    public void setTipoMarcacao(int tipoMarcacao) {
        this.tipoMarcacao = tipoMarcacao;
    }

    public Double getLat() { return lat; }

    public void setLat(Double lat) { this.lat = lat;}

    public Double getLng() { return lng; }

    public void setLng(Double lng) { this.lng = lng;}

    public String getDescricao(String descricao) {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
