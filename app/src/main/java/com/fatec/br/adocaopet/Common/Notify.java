package com.fatec.br.adocaopet.Common;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Leo on 18/03/2018.
 */

public class Notify {
    public static void showNotify(Context context, String strMessage){
        Toast.makeText(context,strMessage, Toast.LENGTH_SHORT).show();
    }

    public static void showSnackbar(View view, String strMessage) {
        Snackbar
                .make(view, strMessage, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }
}
