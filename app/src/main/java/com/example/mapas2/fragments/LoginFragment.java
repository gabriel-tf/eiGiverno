package com.example.mapas2.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapas2.activity.MainActivity;
import com.example.mapas2.R;
import com.example.mapas2.listener.ChangeFragmentListener;
import com.example.mapas2.listener.JsonListener;
import com.example.mapas2.listener.UsuarioListener;
import com.example.mapas2.model.Usuario;
import com.example.mapas2.resource.DatabaseHelper;
import com.example.mapas2.tasks.BuscaUsuarioTask;
import com.example.mapas2.tasks.DownloadJsonTask;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class LoginFragment extends Fragment implements UsuarioListener, View.OnClickListener  {

	public LoginFragment(){
    };
    private ChangeFragmentListener changeFragment;
    private Button login = null;
    private EditText email= null;
    private EditText senha= null;
    private TextView textoCadastro= null;
    private DatabaseHelper helper= null;
    private Usuario usuario = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        login = (Button) rootView.findViewById(R.id.btn_login);
        email = (EditText) rootView.findViewById(R.id.ed_emailLogin);
        senha = (EditText) rootView.findViewById(R.id.ed_senhaLogin);

        login.setOnClickListener(this);
        textoCadastro = (TextView) rootView.findViewById(R.id.texto_cadatre_se);
        textoCadastro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeFragment.changeFragment();
            }
        });
        //new BuscaUsuarioTask(this, getActivity(),"br@br.com").execute();
        return rootView;
    }

    @Override
    public void onClick(View v){
        String strEmail = email.getText().toString();
        String strSenha = senha.getText().toString();

        if(!(strEmail.equals("") || strSenha.equals(""))) {
            new BuscaUsuarioTask(this, getActivity(),strEmail).execute();
            int i = 1;

        }else{
            Toast.makeText(getActivity(), "Os dois campos possuem carater obrigatorio!", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    public void setChanger(ChangeFragmentListener changeFragment) {
       this.changeFragment = changeFragment;
    }

    @Override
    public void usuarioRecebido(Usuario usuario) {

        this.usuario = usuario;

        if(usuario != null) {
            String strSenha = senha.getText().toString();
            if (strSenha.equals(usuario.getSenha())) {
                Toast.makeText(getActivity(), "Usuário logado!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),MainActivity.class);
                boolean logado = true;
                intent.putExtra("true",logado);
                intent.putExtra("username", usuario.getNome());
                intent.putExtra("email", usuario.getEmail());
                startActivity(intent);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                changeFragment.changeFragment();
            }else{
                Toast.makeText(getActivity(), "Senha ou usuario incorreto!", Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            Toast.makeText(getActivity(), "Erro na autenticação!", Toast.LENGTH_SHORT).show();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            return;
        }
    }
}
