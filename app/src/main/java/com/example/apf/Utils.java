package com.example.apf;

import android.widget.EditText;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

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
}
