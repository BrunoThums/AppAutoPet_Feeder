package com.example.apf;

import android.os.Build;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.format.ResolverStyle;
import java.util.Date;

public class Utils {
    //formata uma string para o formato de celular - (DDD) e 9 digitos
    public static void formataCelular(EditText celular){
        SimpleMaskFormatter simpleMaskFormatter = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher maskTextWatcher = new MaskTextWatcher(celular, simpleMaskFormatter);
        celular.addTextChangedListener(maskTextWatcher);
    }

    //formata a data para o padrão europeu dia/mês/ano com 4 digitos
    public static void formataData(EditText data){
        SimpleMaskFormatter simpleMaskFormatter = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher maskTextWatcher = new MaskTextWatcher(data, simpleMaskFormatter);
        data.addTextChangedListener(maskTextWatcher);
    }


    //verifica se a data é valida
    public static boolean isDataValida(@NonNull EditText dataImportada) {
        // Configure o SimpleDateFormat no onCreate ou onCreateView
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        sdf.setLenient(false);

        // Durante a confirmacao de cadastro, faça a validacao

        String data =dataImportada.getText().toString();

        try {
            Date date = sdf.parse(data);
            return true;
            // Data formatada corretamente
        } catch (ParseException e) {
            // Erro de parsing!!
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isDataValida(String dataImportada) {
        // Configure o SimpleDateFormat no onCreate ou onCreateView
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        sdf.setLenient(false);

        // Durante a confirmacao de cadastro, faça a validacao

        String data =dataImportada;

        try {
            Date date = sdf.parse(data);
            return true;
            // Data formatada corretamente
        } catch (ParseException e) {
            // Erro de parsing!!
            e.printStackTrace();
            return false;
        }
    }

    /*
    public static boolean verificaData(@NonNull EditText data){
            try {

                //SimpleDateFormat é usada para trabalhar com formatação de datas
                //formato "dd/MM/yyyy"
                //dd = dia, MM = mes, yyyy = ano
                //"M" maiusculo = mês
                //"m" minusculo = minutos
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                //setLenient() = "false" = não aceitar datas falsas como 31/02/2021
                sdf.setLenient(false);
                //converter String em um objeto do tipo date se funcionar
                //se true = a data é valida
                sdf.parse(data.getText().toString());
                return true;
            } catch (ParseException ex) {
                //se der errado = a data é falsa
                return false;
            }
    }*/

    public static boolean isEmailValido(@NonNull EditText email) {
        if (email.getText().toString().matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
            return true;
        }
        return false;
    }
    public static boolean isEmailValido(String email) {
        if (email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
            return true;
        }
        return false;
    }

    //Verifica se a é maior que 4 e não nula
    public static boolean isSenhaValida(@NonNull EditText password) {
        String senha = password.getText().toString();
        if(!senha.isEmpty() && senha.length()>4){
            return true;
        }
        return false;
    }
    public static boolean isSenhaValida(String password) {
        String senha = password;
        if(!senha.isEmpty() && senha.length()>4){
            return true;
        }
        return false;
    }

    //Verifica se a senha1 é igual senha2
    public static boolean isSenhasIguais(@NonNull EditText password1, @NonNull EditText password2){
        String senha1 = password1.getText().toString();
        String senha2 = password2.getText().toString();
        if(senha1.equals(senha2)){
            return true;
        }
        return false;
    }
    public static boolean isSenhasIguais(String password1, String password2){
        String senha1 = password1;
        String senha2 = password2;
        if(senha1.equals(senha2)){
            return true;
        }
        return false;
    }

    //Verifica se nome não é nulo e é possui ao menos 2 letras
    public static boolean isNomeValido(EditText editTextNome){
        String nome = editTextNome.getText().toString();
        if(!nome.isEmpty() && nome.length()>1){
            return true;
        }
        return false;
    }
    public static boolean isNomeValido(String editTextNome){
        String nome = editTextNome;
        if(!nome.isEmpty() && nome.length()>1){
            return true;
        }
        return false;
    }


}
