package com.example.mapas2.infra;

import android.util.Log;

import com.example.mapas2.model.Marcacao;
import com.example.mapas2.model.Usuario;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jasypt.util.text.BasicTextEncryptor;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by windows on 01/12/2014.
 */
public class ConnectionFactory {
    private static final String URL_STRING_POST = "http://192.168.0.105:8080/eiService2/rest/resource/novaMarcacao";
    private static final String URL_STRING_POST_USER = "http://192.168.0.105:8080/eiService2/rest/resource/novoUsuario";
    private static final String URL_STRING_GET_FIND_USER = "http://192.168.0.105:8080/eiService2/rest/resource/getUsuario/";
    private static final String URL_STRING_GET = "http://192.168.0.105:8080/eiService2/rest/resource/listMarcacoes";
    public static final BasicTextEncryptor textEncryptor = new BasicTextEncryptor();

    public static String getUsuarioPorEmailConnection(String email) throws IOException{

        InputStream is = null;
        try{
            URL url = new URL(URL_STRING_GET_FIND_USER+email);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            conn.getResponseCode();
            is = conn.getInputStream();

            Reader reader = null;
            reader = new InputStreamReader(is);
            char[] buffer = new char[2048];
            reader.read(buffer);
            return new String(buffer);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static String salvarUsuario(Usuario usuario) throws IOException{
        InputStream inputStream = null;
        String result = "";
        textEncryptor.setPassword("senha");
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL_STRING_POST_USER);
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("nome", usuario.getNome());
            jsonObject.accumulate("email", usuario.getEmail());
            String ecryptedPass = textEncryptor.encrypt(usuario.getSenha());
            jsonObject.accumulate("senha",ecryptedPass);
            json = jsonObject.toString();
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpPost);
            //inputStream = httpResponse.getEntity().getContent();
            //if(inputStream != null)
            //    result = convertInputStreamToString(inputStream);
            //else
            //pesquisar a respeito dessas linhas se iram influir no futuro.
            result = "Did not work!";
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    public static String salvarMarcacao(Marcacao marcarcao) throws IOException{
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL_STRING_POST);
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("latitude", marcarcao.getLat());
            jsonObject.accumulate("longitude", marcarcao.getLng());
            jsonObject.accumulate("descricao", "descricao");
            jsonObject.accumulate("autor", marcarcao.getUser().getId());
            jsonObject.accumulate("tipoMarcacao", marcarcao.getTipoMarcacao());
            json = jsonObject.toString();
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpPost);
            //inputStream = httpResponse.getEntity().getContent();
            //if(inputStream != null)
            //    result = convertInputStreamToString(inputStream);
            //else
            //pesquisar a respeito dessas linhas se iram influir no futuro.
                result = "Did not work!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    public static String baixarMarcacoes() throws IOException{
        InputStream is = null;
        try{
            URL url = new URL(URL_STRING_GET);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            conn.getResponseCode();
            is = conn.getInputStream();

            Reader reader = null;
            reader = new InputStreamReader(is);
            char[] buffer = new char[2048];
            reader.read(buffer);
            return new String(buffer);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;

    }
}
