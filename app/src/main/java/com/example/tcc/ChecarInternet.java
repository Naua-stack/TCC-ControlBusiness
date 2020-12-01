package com.example.tcc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ChecarInternet {
    public boolean conectado(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo dadosmoveis = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(wifi != null && wifi.isConnected() || (dadosmoveis != null && dadosmoveis.isConnected())){
            return  true;
        }else{
            return  false;
        }
    }
}
