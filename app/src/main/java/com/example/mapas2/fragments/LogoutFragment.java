package com.example.mapas2.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mapas2.activity.MainActivity;
import com.example.mapas2.R;

public class LogoutFragment extends Fragment {

	public LogoutFragment(){

    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_logout, container, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Deseja mesmo sair?").setPositiveButton("Sair", dialogClickListener)
                .setNegativeButton("Cancelar", dialogClickListener).show();
        return rootView;
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    boolean logado = false;
                    intent.putExtra("deslogado",logado);
                    startActivity(intent);
                    Toast.makeText(getActivity(), "Usuário deslogado!",Toast.LENGTH_SHORT ).show();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };
}
