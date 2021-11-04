package com.example.apf;

import android.widget.EditText;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Utils {
    public static void formataCelular(EditText celular){
        SimpleMaskFormatter simpleMaskFormatter = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher maskTextWatcher = new MaskTextWatcher(celular, simpleMaskFormatter);
        celular.addTextChangedListener(maskTextWatcher);
    }

    public static void formataData(EditText data){
        SimpleMaskFormatter simpleMaskFormatter = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher maskTextWatcher = new MaskTextWatcher(data, simpleMaskFormatter);
        data.addTextChangedListener(maskTextWatcher);
    }

    public static boolean verificaData(EditText data){
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
    }
}
