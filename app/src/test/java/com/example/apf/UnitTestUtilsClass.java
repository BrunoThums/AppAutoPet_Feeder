package com.example.apf;

import org.junit.Test;

import static org.junit.Assert.*;


import android.widget.EditText;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTestUtilsClass {

    @Test
    public void email_isCorrect() throws Exception {

        assertEquals(false, Utils.isEmailValido("mateus..@gmail.com"));
        assertEquals(true, Utils.isEmailValido("mateus@gmail.com"));
        assertEquals(true, Utils.isEmailValido("mateus_@gmail.com"));
        assertEquals(false, Utils.isEmailValido("@gmail.com"));
        assertEquals(false, Utils.isEmailValido("mateus@"));
        assertEquals(false, Utils.isEmailValido("@"));
        assertEquals(false, Utils.isEmailValido("mateus@.com"));
    }

    @Test
    public void date_isCorrect() throws Exception {
        assertEquals(true, Utils.isDataValida("07/07/1980"));
        assertEquals(true, Utils.isDataValida("07/07/1980"));
        assertEquals(true, Utils.isDataValida("29/02/2000")); //bissexto
        assertEquals(false, Utils.isDataValida("29/02/2001"));
        assertEquals(false, Utils.isDataValida("129/02/2001"));
        assertEquals(false, Utils.isDataValida("129/02/20011"));
        assertEquals(false, Utils.isDataValida("29/22/2001"));
        assertEquals(false, Utils.isDataValida("32/22/2001"));
        assertEquals(false, Utils.isDataValida("a2/22/2001"));
        assertEquals(false, Utils.isDataValida("a2/22/200"));

    }
    @Test
    public void password_isCorrect() throws Exception {
        assertEquals(true, Utils.isSenhaValida("15268"));
        assertEquals(true, Utils.isSenhaValida("asdrg2418!@##$%#$"));
        assertEquals(false, Utils.isSenhaValida(""));
        assertEquals(false, Utils.isSenhaValida("123"));
    }

    @Test
    public void passwords_isEquals() throws Exception {
        assertEquals(true, Utils.isSenhasIguais("15268", "15268"));
        assertEquals(false, Utils.isSenhasIguais("15268", "1526"));
    }

    @Test
    public void name_isCorrect() throws Exception {
        assertEquals(true, Utils.isNomeValido("carlos"));
        assertEquals(false, Utils.isNomeValido("c"));
        assertEquals(false, Utils.isNomeValido(""));
    }
}