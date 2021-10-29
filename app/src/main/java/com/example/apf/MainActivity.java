package com.example.apf;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //o usuario logado, deve vir da LoginActivity logo apos a autenticação
    JSONObject usuarioLogadofromLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //transforma a cor em preto do text header
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">"+getString(R.string.app_name)+"</font>"));


        //ao iniciar ja chama a activity de login... se o login der certo vamos receber um json em formato  de string  com as informações do usuario  de volta da LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 1);


        //acao para botao dieta
        Button bt = findViewById(R.id.btDieta);
        bt.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), DietaActivity.class);
                try {
                    intent.putExtra("usuarioLogado", usuarioLogadofromLogin.getString("idusuario"));
                } catch (JSONException e) {
                    e.printStackTrace();
                } //passa a id do usuario logado para a nova activity
                startActivity(intent);

            }
        });

        //acao para botao alimentador
        Button btalimentador = findViewById(R.id.btAlimentador);
        btalimentador.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), AlimentadorActivity.class);
                try {
                    intent.putExtra("usuarioLogado", usuarioLogadofromLogin.getString("idusuario")); //passa a id do usuario logado para a nova activity
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);

            }
        });

        //acao para botao pets
        Button btpets = findViewById(R.id.btPets);
        btpets.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), PetActivity.class);
                try {
                    intent.putExtra("usuarioLogado", usuarioLogadofromLogin.getString("idusuario")); //passa a id do usuario logado para a nova activity
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);

            }
        });

        //acao ao clicar em modificar informações do usuario
        ImageView imgMudarConfContaUsuario = findViewById(R.id.imageViewChangeAccountConf);
        imgMudarConfContaUsuario.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                //Toast.makeText(getApplicationContext(), "O usuario que veio da Activity Login é: "+usuarioLogadofromLogin.toString(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), UsuarioActivity.class);
                intent.putExtra("usuario", usuarioLogadofromLogin.toString()); //passa a id do usuario logado para a nova activity
                startActivity(intent);
            }
        });
    }

    //esse metodo é invocado quando a LoginActivity consegue um login com sucesso
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String extraReturn = data.getStringExtra("extra_return");
                    try {
                        //como recebemos um json em formato de string (vindo atraves do "extra_retrun") com as informações do usuario logado, vamos converter essa string em um json
                        usuarioLogadofromLogin = new JSONObject(extraReturn);
                        //Toast.makeText(getApplicationContext(), "O usuario que veio da Activity Login é: "+extraReturn, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                break;
            default:
        }
    }



}