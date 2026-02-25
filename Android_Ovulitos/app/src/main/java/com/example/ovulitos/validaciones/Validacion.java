package com.example.ovulitos.validaciones;

import java.util.regex.Pattern;

public class Validacion {

    public static boolean validarEmail(String mail){
        String regex = "^[a-zA-Z0-9]{1,}@gmail.com$";
        return Pattern.matches(regex, mail);
    }

    public static boolean validarPass(String pass){
        String regex = "^[a-zA-Z0-9]{1,}$";
        return Pattern.matches(regex, pass);
    }
}
