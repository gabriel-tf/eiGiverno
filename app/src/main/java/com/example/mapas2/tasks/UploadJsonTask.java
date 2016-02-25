package com.example.mapas2.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.mapas2.infra.ConnectionFactory;
import com.example.mapas2.model.Marcacao;
import com.example.mapas2.model.Usuario;

import java.io.IOException;

/**
 * Created by windows on 01/12/2014.
 */
public class UploadJsonTask extends AsyncTask<String, Void, String> {
    private Marcacao marcacao;
    private Usuario usuario;

    public UploadJsonTask(Marcacao marcacao, Usuario usuario){
        this.marcacao = marcacao;
        this.usuario = usuario;
    }

    @Override
    protected String doInBackground(String... urls){
        String result = null;
        try {
            if(marcacao != null)
                result = ConnectionFactory.salvarMarcacao(marcacao);
            else
                result = ConnectionFactory.salvarUsuario(usuario);
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
      Log.e("Dados Enviados","Dados Evnsiado!!!");
    }
}
