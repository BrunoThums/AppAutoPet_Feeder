package com.example.apf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.apf.model.Alimentador;
import com.example.apf.model.Comando;
import com.example.apf.model.Pet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ComandoActivity extends AppCompatActivity {

    String idAlimentadorSelecionado = "";
    private RadioGroup radioComandoGroup;
    private RadioButton radioComandoButton;
    private Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comando);

        //transforma a cor em preto do text header
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name) + "</font>"));

        Intent intent = getIntent();
        idAlimentadorSelecionado = intent.getStringExtra("idAlimentadorSelecionado"); //captura o id do alimentador selecionado, vem do AlimentadorAdapter

        radioComandoGroup = (RadioGroup) findViewById(R.id.radioGroupComando);
        btnEnviar = (Button) findViewById(R.id.btn_enviar_comando_alimentador_comando);

        btnEnviar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // get selected radio button from radioGroup
                int selectedId = radioComandoGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioComandoButton = (RadioButton) findViewById(selectedId);

                if (radioComandoButton.getText().equals("Despejar Ração")) {
                    //entao selecionou a acao de despeja uma quantidade especifica de ração
                    String quantidadeEmGramas = ((EditText) findViewById(R.id.quantidadegramas_comando)).getText().toString();
                    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String dataTimestamp = s.format(new Date());
                    Comando novoComando = new Comando();
                    novoComando.setIdalimentador(Integer.parseInt(idAlimentadorSelecionado));
                    novoComando.setData(dataTimestamp);
                    //exemplo de json com o comando de liberar uma quantidade especifica de ração:
                    //{"liberar":{"quantidade":"50"}}
                    novoComando.setComando("{\"liberar\":{\"quantidade\":\"" + quantidadeEmGramas + "\"}}");
                    novoComando.setComandoExecutado(false);

                    Toast.makeText(ComandoActivity.this, "ID do alimentador: " + String.valueOf(novoComando.getIdalimentador()) + " Data: " + novoComando.getData() + " Comando: " + novoComando.getComando() + "Foi executado: " + novoComando.isComandoExecutado(), Toast.LENGTH_SHORT).show();
                    Submit(novoComando);

                } else {
                    if (radioComandoButton.getText().equals("Limpar Agendamentos do Alimentador")) {
                        //entao selecionou a acao de limpar todos os agendamentos que estão salvos no alimentador
                        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String dataTimestamp = s.format(new Date());
                        Comando novoComando = new Comando();
                        novoComando.setIdalimentador(Integer.parseInt(idAlimentadorSelecionado));
                        novoComando.setData(dataTimestamp);
                        //exemplo de json com o comando de limpar os agendamentos:
                        //{"limpar":{}}
                        novoComando.setComando("{\"limpar\":{}}");
                        novoComando.setComandoExecutado(false);

                        Toast.makeText(ComandoActivity.this, "ID do alimentador: " + String.valueOf(novoComando.getIdalimentador()) + " Data: " + novoComando.getData() + " Comando: " + novoComando.getComando() + "Foi executado: " + novoComando.isComandoExecutado(), Toast.LENGTH_SHORT).show();
                        Submit(novoComando);

                    }else {
                        if (radioComandoButton.getText().equals("Ativar FeedBack por Email")) {
                            //chama o metodo que vai recuperar o email do usuario vinculado a esse alimentador
                            getEmailDoUsuarioDoAlimentador();
                        }else{
                            if (radioComandoButton.getText().equals("Desativar FeedBack por Email")) {

                                //entao selecionou a acao de desativar o envio de emails de feedback por parte de alimentador
                                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                String dataTimestamp = s.format(new Date());
                                Comando novoComando = new Comando();
                                novoComando.setIdalimentador(Integer.parseInt(idAlimentadorSelecionado));
                                novoComando.setData(dataTimestamp);
                                //exemplo de json com o comando de desativar o feedback por email (simplesmente envie o comando de "envioDeEmail" informando um email em null)
                                //{"envioDeEmail":{"email":""}}
                                novoComando.setComando("{\"envioDeEmail\":{\"email\":\"nulo\"}}");
                                novoComando.setComandoExecutado(false);

                                Toast.makeText(ComandoActivity.this, "ID do alimentador: " + String.valueOf(novoComando.getIdalimentador()) + " Data: " + novoComando.getData() + " Comando: " + novoComando.getComando() + "Foi executado: " + novoComando.isComandoExecutado(), Toast.LENGTH_SHORT).show();
                                Submit(novoComando);

                            }else{
                                if (radioComandoButton.getText().equals("Enviar Agendamento da Dieta")) {
                                    //entao selecionou a opção de enviar uma nova dieta para o alimentador
                                    //vamos chamar o metodo getdata que vai recuperar todas as refeicões horario e quantidades que esse alimentador deve liberar
                                    getData();
                                }
                            }
                        }

                    }

                }


            }

        });


    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        findViewById(R.id.quantidadegramas_comando).setVisibility(View.INVISIBLE);
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_despejar_racao_comando:
                if (checked)
                    findViewById(R.id.quantidadegramas_comando).setVisibility(View.VISIBLE);
                break;
            case R.id.radio_enviar_dietas_comando:
                if (checked)
                    break;
            case R.id.radio_limparAgendamentos_comando:
                if (checked)
                    break;
            case R.id.radio_ativa_envio_de_email:
                if (checked)
                    break;
            case R.id.radio_desativa_envio_de_email:
                if (checked)
                    break;
        }
    }

    private void Submit(Comando novoComando) {

        String url = "http://177.44.248.45:3300/comando";

        Map<String, String> params = new HashMap<>();
        params.put("idalimentador", String.valueOf(novoComando.getIdalimentador()));
        params.put("data", String.valueOf(novoComando.getData()));
        params.put("comando", String.valueOf(novoComando.getComando()));
        params.put("comandoexecutado", String.valueOf(novoComando.isComandoExecutado()));


        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonObj = new JSONObject(params);

        // Request a json response from the provided URL
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Dados salvos com suscesso", Toast.LENGTH_LONG).show();
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Falha ao salvar os dados", Toast.LENGTH_LONG).show();
                            }
                        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjRequest);


    }


    //esse metodo busca todas as refeições com suas quantidades e horarios que o alimentador deve liberar
    private void getData() {

        String url = "http://177.44.248.45:3300/alimentador/dieta/"+idAlimentadorSelecionado;
        JsonArrayRequest arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for(int i=0 ; i<response.length(); i++){
                    try{

                        jsonObject = response.getJSONObject(i);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(ComandoActivity.this, "Response: " +response.toString(), Toast.LENGTH_SHORT).show();

                //assim que recebermos todos os horarios e quantidades da dieta podemos enviar para o alimentador com o comando específico que inicia com a palavra "dieta"

                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String dataTimestamp = s.format(new Date());
                Comando novoComando = new Comando();
                novoComando.setIdalimentador(Integer.parseInt(idAlimentadorSelecionado));
                novoComando.setData(dataTimestamp);
                //exemplo de json com o comando de horarios da dieta:
                //{"dieta":[{"horario":"07:30","quantidade":"50"},{"horario":"12:30","quantidade":"100"}]}
                novoComando.setComando("{\"dieta\":"+response.toString()+"}");
                novoComando.setComandoExecutado(false);

                Toast.makeText(ComandoActivity.this, "ID do alimentador: " + String.valueOf(novoComando.getIdalimentador()) + " Data: " + novoComando.getData() + " Comando: " + novoComando.getComando() + "Foi executado: " + novoComando.isComandoExecutado(), Toast.LENGTH_SHORT).show();
                Submit(novoComando);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ComandoActivity.this, "Não foi possivel obter o agendamento de dieta para o alimentador", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(ComandoActivity.this);
        requestQueue.add(arrayRequest);
    }

    //esse metodo busca o email do usuario atualmente vinculado ao alimentador
    private void getEmailDoUsuarioDoAlimentador() {

        String url = "http://177.44.248.45:3300/alimentador/emailUsuario/"+idAlimentadorSelecionado;
        JsonRequest arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for(int i=0 ; i<response.length(); i++){
                    try{

                        jsonObject = response.getJSONObject(i);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(ComandoActivity.this, "Response: " +response.toString(), Toast.LENGTH_SHORT).show();

                //assim que recebermos o email do suaurio vinculado a esse alimentador podemos enviar o comando

                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String dataTimestamp = s.format(new Date());
                Comando novoComando = new Comando();
                novoComando.setIdalimentador(Integer.parseInt(idAlimentadorSelecionado));
                novoComando.setData(dataTimestamp);
                //exemplo de json com o comando de habilitar o FeedBack Por Email:
                //{"envioDeEmail":{"email":"juca@juca.com"}}
                try {
                    novoComando.setComando("{\"envioDeEmail\":"+response.getJSONObject(0).toString()+"}");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                novoComando.setComandoExecutado(false);

                Toast.makeText(ComandoActivity.this, "ID do alimentador: " + String.valueOf(novoComando.getIdalimentador()) + " Data: " + novoComando.getData() + " Comando: " + novoComando.getComando() + "Foi executado: " + novoComando.isComandoExecutado(), Toast.LENGTH_SHORT).show();
                Submit(novoComando);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ComandoActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(ComandoActivity.this, "Não foi possivel obter o email do usuario desse alimentador", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(ComandoActivity.this);
        requestQueue.add(arrayRequest);
    }







}