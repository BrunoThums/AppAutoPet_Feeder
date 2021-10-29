package com.example.apf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.apf.model.Comando;
import com.example.apf.model.Sensor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SensorActivity extends AppCompatActivity {

    private String idAlimentadorSelecionado="";
    private TextView textViewData, textViewUmidade, textViewTemperatura, textViewNivel, textViewDataLiberacao, textViewQuantidadeRequisitada, textViewQuantidadeDespejada, textViewStatus;
    private Button btnAtualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        //transforma a cor em preto do text header
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">"+getString(R.string.app_name)+"</font>"));

        Intent intent = getIntent();
        idAlimentadorSelecionado = intent.getStringExtra("idAlimentadorSelecionado"); //captura o id do alimentador selecionado, vem do AlimentadorAdapter

        textViewData = findViewById(R.id.textViewData);
        textViewUmidade = findViewById(R.id.textViewUmidade);
        textViewTemperatura = findViewById(R.id.textViewTemperatura);
        textViewNivel = findViewById(R.id.textViewNivel);
        textViewDataLiberacao=findViewById(R.id.textViewDataLiberacao);
        textViewQuantidadeRequisitada = findViewById(R.id.textViewQuantidadeRequisitada);
        textViewQuantidadeDespejada = findViewById(R.id.textViewQuantidadeDespejada);
        textViewStatus = findViewById(R.id.textViewStatus);


        getData();

        btnAtualizar = findViewById(R.id.btn_atualizar_leitura_sensores);
        btnAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });


    }

    private void getData() {

        Sensor sensor = new Sensor();

        String url = "http://177.44.248.45:3300/sensor/ultimaLeitura/"+idAlimentadorSelecionado;
        JsonArrayRequest arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for(int i=0 ; i<response.length(); i++){
                    try{

                        jsonObject = response.getJSONObject(i);

                        sensor.setIdsensor(jsonObject.getInt("idsensor"));
                        sensor.setIdalimentador(jsonObject.getInt("idalimentador"));
                        sensor.setData(jsonObject.getString("data"));
                        sensor.setSensores(jsonObject.getString("sensores"));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //Toast.makeText(SensorActivity.this, "Response: " +response.toString(), Toast.LENGTH_SHORT).show();

                //assim que recebermos a ultima leitura dos sensores podemos preencher os textfields com os dados recebidos
                try {
                    //verifica se o resultado de getSensores é diferente de null, pois pode ser que estamos querendo exibir dados de sensores de um alimentador que ainda não enviou nenhuma informação
                    if(sensor.getSensores()!=null) {
                        JSONObject sensores = new JSONObject(sensor.getSensores());
                        textViewData.setText(sensor.getData());
                        textViewNivel.setText(sensores.getString("nivel"));
                        textViewTemperatura.setText(sensores.getString("temperatura"));
                        textViewUmidade.setText(sensores.getString("umidade"));

                        //criamos um novo jsonObject com o json que está na key "ultimaliberação", é nessa key que está o json com as informações relevantes sobre a ultima liberação de ração que o alimentador fez
                        JSONObject ultimaLiberacaoObject = sensores.getJSONObject("ultimaLiberacao");
                        //checa se tem um key no json ultimaLiberacaoObject chamadada "dataUltimaLiberacao", ou seja ja houve alguma liberação de racão nesse alimentador entao podemos preencher os textfields
                        if(ultimaLiberacaoObject.has("dataUltimaLiberacao")){
                            textViewDataLiberacao.setText(ultimaLiberacaoObject.getString("dataUltimaLiberacao"));
                            textViewQuantidadeRequisitada.setText((ultimaLiberacaoObject.getString("quantidadeSolicitada")) + "g");
                            textViewQuantidadeDespejada.setText((ultimaLiberacaoObject.getString("quantidadeLiberada")) + "g");
                            textViewStatus.setText(ultimaLiberacaoObject.getString("status"));
                        }else{
                            //caso contrario, ainda nao houve nenhuma liberação de ração para esse alimentador, então os textfields ficam em branco
                            textViewDataLiberacao.setText("---");
                            textViewQuantidadeRequisitada.setText("---");
                            textViewQuantidadeDespejada.setText("---");
                            textViewStatus.setText("---");
                        }

                    }else{
                    //se for igual a null entao é um sensor que nunca enviou nenhuma informação, entao vamos preencher o textfield com '---'
                        textViewData.setText("---");
                        textViewNivel.setText("---");
                        textViewTemperatura.setText("---");
                        textViewUmidade.setText("---");
                        textViewDataLiberacao.setText("---");
                        textViewQuantidadeRequisitada.setText("---");
                        textViewQuantidadeDespejada.setText("---");
                        textViewStatus.setText("---");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }






            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(SensorActivity.this);
        requestQueue.add(arrayRequest);
    }
}