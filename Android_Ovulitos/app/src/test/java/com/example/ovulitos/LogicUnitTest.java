package com.example.ovulitos;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.ovulitos.validaciones.Validacion;

public class LogicUnitTest {

    @Test
    public void email_isCorrect() {
        assertTrue(Validacion.validarEmail("usuario123@gmail.com"));
    }

    @Test
    public void email_isIncorrect() {
        // No es de gmail.com
        assertFalse(Validacion.validarEmail("usuario@outlook.com"));
        // No tiene formato de email
        assertFalse(Validacion.validarEmail("usuario123"));
    }

    @Test
    public void password_isCorrect() {
        assertTrue(Validacion.validarPass("password123"));
    }

    @Test
    public void password_isIncorrect() {
        // Contiene caracteres especiales no permitidos por el regex
        assertFalse(Validacion.validarPass("pass@word!"));
        // Vacío
        assertFalse(Validacion.validarPass(""));
    }
}
