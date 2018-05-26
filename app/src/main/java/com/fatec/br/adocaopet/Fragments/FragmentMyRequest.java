package com.fatec.br.adocaopet.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatec.br.adocaopet.R;


public class FragmentMyRequest extends android.support.v4.app.Fragment {

    View view;

    public FragmentMyRequest() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_solicitacoes, container, false);

        return view;
    }


}
