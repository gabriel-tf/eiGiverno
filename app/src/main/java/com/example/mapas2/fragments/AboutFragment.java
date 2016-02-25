package com.example.mapas2.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mapas2.R;

public class AboutFragment extends Fragment {

	public AboutFragment(){

    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
         
        return rootView;
    }
}
