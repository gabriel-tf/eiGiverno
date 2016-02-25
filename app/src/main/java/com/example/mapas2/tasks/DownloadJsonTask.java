package com.example.mapas2.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mapas2.R;
import com.example.mapas2.enums.TipoMarcacaoEnum;
import com.example.mapas2.infra.ConnectionFactory;
import com.example.mapas2.listener.JsonListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by windows on 21/11/2014.
 */
public class DownloadJsonTask extends AsyncTask <Void, Void, List<MarkerOptions>> {

    ProgressDialog progressDialog;
    private JsonListener listener;
    private static final String URL_STRING = "http://192.168.0.105:8080/eiService2/rest/resource/listMarcacoes";
    Activity mActivity;
    public DownloadJsonTask(JsonListener listener, Activity mActivity){
        this.listener = listener;
        this.mActivity = mActivity;
    }


    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage("Aguarde...");
        progressDialog.show();
    }



    @Override
    protected List<MarkerOptions> doInBackground(Void... params) {
        try{
            String resultado = ConnectionFactory.baixarMarcacoes();
            List<MarkerOptions> marcas = interpretaResultado(resultado);
            return marcas;

        }catch (IOException e){
            e.printStackTrace();
            return null;
        }catch (JSONException e1){
            e1.printStackTrace();
            return null;
        }


    }

    private List<MarkerOptions> interpretaResultado(String resultado) throws JSONException{

        List<MarkerOptions> opcoes = new ArrayList<MarkerOptions>();
        if(resultado == null){
            Log.e("ERRO CONEXAO: ", "WEBSERVICE FORA DO AR!!");
            return null;
        }

        TipoMarcacaoEnum[] valoresEnum = TipoMarcacaoEnum.values();

        JSONArray array = new JSONArray(resultado);
        for(int i = 1 ; i<=array.length(); i++){
            JSONObject row = array.getJSONObject(i-1);
            double lgt = row.getDouble("longitude");
            double lat = row.getDouble("latitude");
            int tipoMarcaocao = row.getInt("tipoMarcacao");

            for(int k = 0; k<valoresEnum.length ; k++){
                if(tipoMarcaocao == valoresEnum[k].getDesc()){
                    if("LIXO".equals(valoresEnum[k].name())){
                        MarkerOptions opcaoMarcador = new MarkerOptions().position(new LatLng(lat, lgt)).title("Lixo").snippet("Acúmulo de lixo")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.garbage));
                        opcoes.add(opcaoMarcador);
                    }else if("SANEAMENTO".equals(valoresEnum[k].name())){
                        MarkerOptions opcaoMarcador = new MarkerOptions().position(new LatLng(lat, lgt)).title("Saneamento").snippet("Esgosto a céu aberto")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.water));
                        opcoes.add(opcaoMarcador);
                    }else if("ELETRICIDADE".equals(valoresEnum[k].name())){
                        MarkerOptions opcaoMarcador = new MarkerOptions().position(new LatLng(lat, lgt)).title("Eletricidade").snippet("Falta de luz na região")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.energy));
                        opcoes.add(opcaoMarcador);
                    }else if("BURACO".equals(valoresEnum[k].name())){
                        MarkerOptions opcaoMarcador = new MarkerOptions().position(new LatLng(lat, lgt)).title("Buraco").snippet("Pista esburacada")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.hole));
                        opcoes.add(opcaoMarcador);
                    }

                }
            }

           // MarkerOptions opcaoMarcador = new MarkerOptions().position(new LatLng(lat, lgt)).title("Buraco").snippet("Pista esburacada")
           //         .icon(BitmapDescriptorFactory.fromResource(R.drawable.hole));
           // opcoes.add(opcaoMarcador);
        }


        return opcoes ;
    }

    @Override
    protected void onPostExecute(List<MarkerOptions> result) {
        progressDialog.dismiss();
        listener.dadosRecebidos(result);
    }
}
