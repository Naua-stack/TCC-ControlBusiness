package com.example.tcc;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationData  {

    public LocationData() {
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        this.Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        this.Longitude = longitude;
    }

   private double Latitude;
   private double Longitude;

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    private  String Estado;
    private String Cidade;
    private String Rua;

    public String getCidade() {
        return Cidade;
    }

    public void setCidade(String cidade) {
        Cidade = cidade;
    }

    public String getRua() {
        return Rua;
    }

    public void setRua(String rua) {
        Rua = rua;
    }

    public String getCEP() {
        return CEP;
    }

    public void setCEP(String CEP) {
        this.CEP = CEP;
    }

    public String getPaís() {
        return País;
    }

    public void setPaís(String país) {
        País = país;
    }

    public String getBairro() {
        return Bairro;
    }

    public void setBairro(String bairro) {
        Bairro = bairro;
    }

    private String CEP;
    private String País;
    private String Bairro;

    public LocationData(double latitude, double longitude) {
        this.Latitude = latitude;
        this.Longitude = longitude;
    }


}
