package com.fatec.br.adocaopet.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatec.br.adocaopet.R;

/**
 * Created by Leo on 19/05/2018.
 */

public class FragmentReceiveRequest extends android.support.v4.app.Fragment{

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_solicitacoes_recebidas, container, false);
        return view;
    }
}
