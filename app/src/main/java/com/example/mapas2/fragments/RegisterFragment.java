package com.example.mapas2.fragments;

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
import android.widget.Toast;
import android.util.Log;
import android.view.View.OnClickListener;

import com.example.mapas2.R;
import com.example.mapas2.infra.ConnectionFactory;
import com.example.mapas2.listener.ChangeFragmentListener;
import com.example.mapas2.model.Usuario;
import com.example.mapas2.resource.DatabaseHelper;
import com.example.mapas2.tasks.UploadJsonTask;

import java.sql.SQLException;

public class RegisterFragment extends Fragment {

    public RegisterFragment() {

    }

    private ChangeFragmentListener changeFragment;
    private EditText nome, email, telefone, senha, confirmSenha;
    private Button btnSalvar;
    private DatabaseHelper helper = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        nome = (EditText) rootView.findViewById(R.id.ed_nome);
        email = (EditText) rootView.findViewById(R.id.ed_email);
        senha = (EditText) rootView.findViewById(R.id.ed_senha);
        confirmSenha = (EditText) rootView.findViewById(R.id.ed_confirmSenha);

        btnSalvar = (Button) rootView.findViewById(R.id.btn_salvar);


        helper = DatabaseHelper.getHelper(getActivity().getApplicationContext());

        btnSalvar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String strSenha = senha.getText().toString();
                    String strConfirmSenha = confirmSenha.getText().toString();
                    String strEmail = email.getText().toString();
                    if (!(strEmail.contains("@") && strEmail.contains(".com"))) {
                        Toast.makeText(getActivity(), "Erro na validação do formulario", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!(strConfirmSenha.equals(strSenha))){
                        Toast.makeText(getActivity(), "Erro na validação do formulario", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //save();
                    Usuario usuario = new Usuario();
                    usuario.setEmail(email.getText().toString());
                    usuario.setNome(nome.getText().toString());
                    usuario.setSenha(senha.getText().toString());
                    new UploadJsonTask(null,usuario).execute();
                    Toast.makeText(getActivity(), "Cadastro efetivado com sucesso!", Toast.LENGTH_LONG).show();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    //startActivity(new Intent(getActivity(),MainActivity.class));
                    changeFragment.changeFragment();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }

    public void save() throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setEmail(email.getText().toString());
        usuario.setNome(nome.getText().toString());
        usuario.setSenha(senha.getText().toString());
        helper.getUsuarioDao().create(usuario);
        Toast.makeText(getActivity().getApplicationContext(), "Inserido com Sucesso!", Toast.LENGTH_LONG).show();
    }
    public void setChanger(ChangeFragmentListener changeFragment) {
        this.changeFragment = changeFragment;
    }
}