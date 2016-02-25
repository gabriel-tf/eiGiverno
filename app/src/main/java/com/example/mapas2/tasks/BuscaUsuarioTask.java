package com.example.mapas2.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.mapas2.activity.MainActivity;
import com.example.mapas2.infra.ConnectionFactory;
import com.example.mapas2.listener.JsonListener;
import com.example.mapas2.listener.UsuarioListener;
import com.example.mapas2.model.Usuario;

import org.jasypt.util.text.BasicTextEncryptor;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by user on 23/12/2014.
 */
public class BuscaUsuarioTask  extends AsyncTask<Void, Void, Usuario> {

    private String email;
    private Activity activity = null;
    private UsuarioListener listener;
    private ProgressDialog progressDialog;
    public BuscaUsuarioTask(UsuarioListener listener, Activity activity,String email) {
        this.email = email;
        this.listener = listener;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Buscando usuario...");
        progressDialog.show();
    }

    @Override
    protected Usuario doInBackground(Void... params){
        String result = null;
        Usuario user = null;
        try {
            result = ConnectionFactory.getUsuarioPorEmailConnection(email);
            user = receberUserFromJson(result);
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }catch (JSONException e2) {
            e2.printStackTrace();
            return null;
        }
        return user;
    }
    public Usuario receberUserFromJson(String result) throws JSONException {
        BasicTextEncryptor textEncryptor = ConnectionFactory.textEncryptor;
        textEncryptor.setPassword("senha");

        JSONObject jsonObject = new JSONObject(result);
        Usuario usuario = new Usuario();
        String email = jsonObject.optString("email");
        String nome = jsonObject.optString("nome");
        String senha = jsonObject.optString("senha");
        String senhaEncrypted = textEncryptor.decrypt(senha);
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senhaEncrypted);
        return usuario;
    }

    @Override
    protected void onPostExecute(Usuario usuario) {
        progressDialog.dismiss();
         listener.usuarioRecebido(usuario);
    }
}


