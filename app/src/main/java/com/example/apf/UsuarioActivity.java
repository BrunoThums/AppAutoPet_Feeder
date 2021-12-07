package com.example.apf;

import static com.example.apf.Utils.formataCelular;
import static com.example.apf.Utils.formataData;
import static com.example.apf.Utils.isDataValida;
import static com.example.apf.Utils.isEmailValido;
import static com.example.apf.Utils.isNomeValido;
import static com.example.apf.Utils.isSenhaValida;

import static com.example.apf.Utils.isSenhasIguais;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

public class UsuarioActivity extends AppCompatActivity {

    private Usuario usuario = new Usuario();
    JSONObject jsonUsuarioAserModificado = null; //essa activity pode receber um usuario para alterarmos algumas configurações dele

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_modificar_usuario);

        //transforma a cor em preto do text header
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">"+getString(R.string.app_name)+"</font>"));

        Intent intent = getIntent();
        //Activity bicomportamental:
        // Criar um novo usuario (caso usuário = null)
        // Ou alterar as configurações de um usuario (se usuário != null)
        String usuarioAserModificado = intent.getStringExtra("usuario");

        Button btnCriarModificarConta = (Button) findViewById(R.id.btnCriarModificarContaTelaCriarConta);
        EditText editTextNome = (EditText) findViewById(R.id.txtNome);
        EditText editTextDataNascimento = (EditText) findViewById(R.id.txtDataNascimento);
        EditText editTextEmail = (EditText) findViewById(R.id.txtEmail);
        EditText editTextTextSenha1 = (EditText) findViewById(R.id.txtSenha1);
        EditText editTextTextSenha2 = (EditText) findViewById(R.id.txtSenha2);
        EditText editTextCelular = (EditText) findViewById(R.id.txtCelular);
        formataCelular(editTextCelular);
        formataData(editTextDataNascimento);

        if(usuarioAserModificado!=null){
            btnCriarModificarConta.setText("Atualizar");

            try {
                jsonUsuarioAserModificado = new JSONObject(usuarioAserModificado);
                editTextNome.setText(jsonUsuarioAserModificado.getString("nome"));
                editTextNome.setFocusable(false);
                editTextNome.setEnabled(false);
                editTextDataNascimento.setText(jsonUsuarioAserModificado.getString("dtnascimento"));
                editTextEmail.setText(jsonUsuarioAserModificado.getString("email"));
                editTextCelular.setText(jsonUsuarioAserModificado.getString("celular"));

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        btnCriarModificarConta.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {

                //verifica:
                //se as senhas são validas e não nulas
                //se as senhas são iguais
                //se a data é válida
                //se o email é valido
                //se o nome é valido
                if(isCamposValidos()){

                    //se jsonUsuarioAserModificado!=null entao estamos alterando as configurações de um usuario que ja existe, logo esse usuario possui uma ID
                    if(jsonUsuarioAserModificado!=null){
                        try {
                            usuario.setIdUsuario(jsonUsuarioAserModificado.getInt("idusuario"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    usuario.setNome(editTextNome.getText().toString());
                    usuario.setDtnascimento(editTextDataNascimento.getText().toString());
                    usuario.setEmail(editTextEmail.getText().toString());
                    usuario.setSenha(md5(editTextTextSenha1.getText().toString()));
                    usuario.setCelular(editTextCelular.getText().toString());
                    Submit(usuario);

                }else {
                    feedbackUser();
                }
            }
            private boolean isCamposValidos(){
                if(isSenhaValida(editTextTextSenha1) && isSenhaValida(editTextTextSenha2) &&
                        isSenhasIguais(editTextTextSenha1, editTextTextSenha2) &&
                        isDataValida(editTextDataNascimento) &&
                        isEmailValido(editTextEmail)&&
                        isNomeValido(editTextNome)){
                    return true;
                }
                return false;
            }

            private void feedbackUser(){
                //Nome ok?
                if(!isNomeValido(editTextNome)) {
                    Toast.makeText(getApplicationContext(), "O campo nome deve conter ao menos 2 caracteres!", Toast.LENGTH_LONG).show();
                }
                //Data Nascimento ok?
                else if (!isDataValida(editTextDataNascimento)) {
                    Toast.makeText(getApplicationContext(), "Data inválida!", Toast.LENGTH_LONG).show();
                }
                //Email ok?
                else if (!isEmailValido(editTextEmail)) {
                    Toast.makeText(getApplicationContext(), "Email inválido!", Toast.LENGTH_LONG).show();
                }
                //Senhas ok?
                else if (!isSenhaValida(editTextTextSenha1) && !isSenhaValida(editTextTextSenha2)) {
                    Toast.makeText(getApplicationContext(), "A senha não é valida. Precisa ter no mínimo 5 caracteres", Toast.LENGTH_LONG).show();
                }
                //Senhas iguais?
                else if (!isSenhasIguais(editTextTextSenha1, editTextTextSenha2)) {
                    Toast.makeText(getApplicationContext(), "As senhas não conferem!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



    private void Submit(Usuario usuario) {

        String url = "http://177.44.248.45:3300/usuario";

        //se jsonUsuarioAserModificado for igual a null entao estamos criando um novo usuario, devemos dar um post na url, atenção para o fato de nao ter idusuario nos params pois é um usuario novo
        if(jsonUsuarioAserModificado==null){

            Map<String, String> params = new HashMap<>();
            params.put("nome", usuario.getNome());
            params.put("dtnascimento",usuario.getDtnascimento());
            params.put("email", usuario.getEmail());
            params.put("senha", usuario.getSenha());
            params.put("celular", usuario.getCelular());


            JSONObject parameters = new JSONObject(params);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(getApplicationContext(), "Usuario criado com sucesso", Toast.LENGTH_LONG).show();
                    //se o usuario foi crinado com sucesso podemos finalizar essa activity e retornar o nome do usuario recem criado para a LoginActivity, apenas para o campo login ja ficar prenchido
                    Intent intent = new Intent();
                    intent.putExtra("extra_return", usuario.getNome());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //error.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Falha ao salvar os dados do usuario", Toast.LENGTH_LONG).show();
                }
            });

            Volley.newRequestQueue(this).add(jsonRequest);

        }else{
            //se jsonUsuarioAserModificado for diferente de null entao estamos alterando configurações de um novo usuario que ja existe, então  devemos dar um put na url, atenção para o fato de ter idusuario nos params pois é um usuario que ja existe
            Map<String, String> params = new HashMap<>();
            params.put("idusuario", String.valueOf(usuario.getIdUsuario()));
            params.put("nome", usuario.getNome());
            params.put("dtnascimento",usuario.getDtnascimento());
            params.put("email", usuario.getEmail());
            params.put("senha", usuario.getSenha());
            params.put("celular", usuario.getCelular());


            JSONObject parameters = new JSONObject(params);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(getApplicationContext(), "Usuario Atualizado com sucesso", Toast.LENGTH_LONG).show();
                    //se o usuario foi atualizado  com sucesso podemos finalizar essa activity
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //error.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Falha ao atualizar os dados do usuario "+ error.toString(), Toast.LENGTH_LONG).show();
                }
            });

            Volley.newRequestQueue(this).add(jsonRequest);

        }

    }

    //esse metodo criptografa a senha
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




}