package com.example.ovulitos.currentUser;

public class UserData {

    private static String usuario = "usuarioDefault!";
    public UserData(){
        this.usuario = "usuarioDefault";
    }

    public static String getUsuario(){
        return usuario;
    }

    public static void setUsuario(String u){
        usuario = u;
    }

}
