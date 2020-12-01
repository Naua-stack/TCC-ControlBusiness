package com.example.tcc;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Produtos implements Parcelable {
    private String foto;
    private String nome;
    private Float valor;

    public String getDatafab() {
        return datafab;
    }

    public void setDatafab(String datafab) {
        this.datafab = datafab;
    }



    private String datafab;










    public Float getValor() {
        return valor;
    }

    public void setValor(Float valor) {
        this.valor = valor;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    private Integer quantidade;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getProfileurl() {
        return foto;
    }

   public  Produtos (){

   }

    protected Produtos(Parcel in) {
        foto = in.readString();
        nome = in.readString();
        valor = in.readFloat();
        quantidade = in.readInt();


    }
    public Produtos(String nome, String profileurl, float valor, int quantidade, String idusuario, String datafab ) {
        this.valor = valor;
        this.quantidade = quantidade;
        this.nome = nome;
        this.foto = profileurl;
        this.datafab = datafab;

    }
    public static final Creator<Produtos> CREATOR = new Creator<Produtos>() {
        @Override
        public Produtos createFromParcel(Parcel in) {
            return new Produtos(in);
        }

        @Override
        public Produtos[] newArray(int size) {
            return new Produtos[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel in, int i) {
        in.writeFloat(valor);
        in.writeInt(quantidade);
        in.writeString(nome);
        in.writeString(foto);
    }
}
