package com.example.tcc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CustomInfoMaps implements GoogleMap.InfoWindowAdapter {
    private final View mwindow;
    private Context mcontext;

    public CustomInfoMaps(Context mcontext) {
        this.mcontext = mcontext;
        mwindow = LayoutInflater.from(mcontext).inflate(R.layout.custom_info_maps, null);
    }
    private void renderwindowText(Marker markerOptions, View view){
        String title = markerOptions.getTitle();
        TextView tvtitle = (TextView)  view.findViewById(R.id.titulo);
        if(!title.equals("")){
            tvtitle.setText(title);

        }
        String snippet = markerOptions.getSnippet();
        TextView tvsnippet = (TextView)  view.findViewById(R.id.snippet);
        if(!snippet.equals("")){
            tvsnippet.setText(snippet);

        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderwindowText(marker, mwindow);
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderwindowText(marker, mwindow);
        return null;
    }
}
