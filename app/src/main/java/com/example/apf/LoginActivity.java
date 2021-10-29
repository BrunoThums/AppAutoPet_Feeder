package com.example.apf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.apf.model.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //transforma a cor em preto do text header
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">"+getString(R.string.app_name)+"</font>"));

        Button btnLogin = findViewById(R.id.buttonLogin);
        Button btncriarConta = findViewById(R.id.buttonCriarConta);

        EditText password =  findViewById(R.id.editTextPassword);
        login =  findViewById(R.id.editTextLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Usuario usuario = new Usuario();
                usuario.setNome(login.getText().toString());
                usuario.setSenha(md5(password.getText().toString()));
                Submit(usuario);

            }
        });

        btncriarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chama a activity de criarConta, lembrando que se conseguimos criar uma conta com sucesso ela retorna o nome de usuario criado
                Intent intent = new Intent(getApplicationContext(), UsuarioActivity.class);
                startActivityForResult(intent, 1);

            }
        });


    }

    //esse metodo efetua a criptografia da senha, lembrando que no banco a senha está criptografada
    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    //esse metodo é invocado quando chamamos a activity CriarContaActivity e conseguimos criar uma conta com sucesso nela, ela retorna o nome de usuario criado. Apenas para facilitar as coisas
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //quando nao temos um login e queremos criar um, chamamos a activity CriarContaActivity, se conseguimos criar uma conta com sucesso o nome de usuario criado naquela activity volta para essa activity e ja deixa preenchido o campo login
                    login.setText(data.getStringExtra("extra_return"));

                }
                break;
            default:
        }
    }


    private void Submit(Usuario usuario) {
        //nessa url enviamos um json com o nome do usuario e a senha para ver se corresponde ao que tem no banco de dados
        String url = "http://177.44.248.45:3300/usuario/login";

        Map<String, String> params = new HashMap<>();
        params.put("nome", usuario.getNome());
        params.put("senha", usuario.getSenha());



        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //se recebemos uma resposta valida é porque o usuario e senha estão corretos, caso contrario recebemos uma resposta vazia que cai no metodo onErrorResponse logo abaixo
                //como o login obteve sucesso podemos capturar o id desse usuario para repassa-lo para a main activity, esse id é repassado atraves da variavel "extra_return"
                Intent intent = new Intent();
                //intent.putExtra("extra_return", response.getString("idusuario"));
                intent.putExtra("extra_return", response.toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error.printStackTrace();
                Toast.makeText(getApplicationContext(), "Falha ao fazer login", Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);


    }

    //impede de voltar para a mainactivity sem ter o login aceito
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}