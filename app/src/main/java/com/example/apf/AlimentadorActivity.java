package com.example.apf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.apf.Adapter.AlimentadorAdapter;
import com.example.apf.Adapter.DietaAdapter;
import com.example.apf.Adapter.PetAdapter;
import com.example.apf.model.Alimentador;
import com.example.apf.model.Dieta;
import com.example.apf.model.Pet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AlimentadorActivity extends AppCompatActivity  implements SwipeRefreshLayout.OnRefreshListener{

    private RequestQueue requestQueue;
    private SwipeRefreshLayout refresh;
    private ArrayList<Pet> pets = new ArrayList<>();
    private ArrayList<Dieta> dietas = new ArrayList<>();
    private ArrayList<Alimentador> alimentadores = new ArrayList<>();
    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private AlimentadorAdapter alimentadorAdapter;

    private Spinner spinnerDietasAlimentador;
    Spinner spinnerPetsAlimentador;


    String idUsuarioRecebido = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alimentador);

        //transforma a cor em preto do text header
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">"+getString(R.string.app_name)+"</font>"));

        Intent intent = getIntent();
        idUsuarioRecebido = intent.getStringExtra("usuarioLogado"); //captura o id do usuario logado vindo da mainactivity


        refresh = (SwipeRefreshLayout) findViewById(R.id.swiperdown);
        recyclerView = (RecyclerView) findViewById(R.id.listalimentadores);

        dialog = new Dialog( this);

        refresh.setOnRefreshListener(this);
        refresh.post(new Runnable() {
            @Override
            public void run() {
                pets.clear();
                dietas.clear();
                alimentadores.clear();
                getData();
            }
        });

    }

    private void getData() {
        refresh.setRefreshing(true);

        //primeiramente capture todos os alimentadores que o usuario com a id recebida possui cadastrado
        String url = "http://177.44.248.45:3300/alimentador/"+idUsuarioRecebido;

        arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for(int i=0 ; i<response.length(); i++){
                    try{
                        jsonObject = response.getJSONObject(i);
                        Alimentador alimentador = new Alimentador();
                        alimentador.setIdalimentador(jsonObject.getInt("idalimentador"));
                        alimentador.setSerial(jsonObject.getString("serial"));
                        alimentador.setIdusuario(jsonObject.getInt("idusuario"));
                        alimentador.setIdpet(jsonObject.getInt("idpet"));
                        alimentador.setIddieta(jsonObject.getInt("iddieta"));
                        alimentadores.add(alimentador);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                refresh.setRefreshing(true);
                //depois capture todos os pet que o usuario com a id recebida possui cadastrado para que ele possa selecionar qual pet esta vunculado ao alimentador
                String url = "http://177.44.248.45:3300/pet/"+idUsuarioRecebido;

                arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>(){

                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject jsonObject = null;
                        for(int i=0 ; i<response.length(); i++){
                            try{
                                jsonObject = response.getJSONObject(i);
                                Pet pet = new Pet();
                                pet.setIdpet(jsonObject.getInt("idpet"));
                                pet.setNome(jsonObject.getString("nome"));
                                pet.setAnimal(jsonObject.getString("animal"));
                                pet.setDtnascimento(jsonObject.getString("dtnascimento"));
                                pet.setRaca(jsonObject.getString("raca"));
                                pet.setPorte(jsonObject.getString("porte"));
                                pet.setIdusuario(jsonObject.getInt("idusuario"));
                                pets.add(pet);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        refresh.setRefreshing(true);
                        //por ultimo capture todos as dietas que o usuario com a id recebida possui cadastrado para que ele possa selecionar qual dieta do pet em questão  será vinculado ao alimentador
                        String url = "http://177.44.248.45:3300/dieta/usuario/"+idUsuarioRecebido;

                        arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>(){

                            @Override
                            public void onResponse(JSONArray response) {

                                JSONObject jsonObject = null;
                                for(int i=0 ; i<response.length(); i++){
                                    try{
                                        jsonObject = response.getJSONObject(i);
                                        Dieta dieta = new Dieta();
                                        dieta.setIddieta(jsonObject.getInt("iddieta"));
                                        dieta.setNome(jsonObject.getString("nome"));
                                        dieta.setIdpet(jsonObject.getInt("idpet"));
                                        dietas.add(dieta);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                //atualize o adapter para que ele preencha o recycler view com as informações recebidas
                                adapterPush(alimentadores, dietas, pets);
                                refresh.setRefreshing(false);

                            }
                        },  new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Falha ao recuperar o Pets", Toast.LENGTH_LONG).show();
                            }
                        });

                        requestQueue = Volley.newRequestQueue(AlimentadorActivity.this);
                        requestQueue.add(arrayRequest);


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Falha ao recuperar o Pets", Toast.LENGTH_LONG).show();
                    }
                });

                requestQueue = Volley.newRequestQueue(AlimentadorActivity.this);
                requestQueue.add(arrayRequest);



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Falha ao recuperar as Dietas", Toast.LENGTH_LONG).show();
            }
        });

        requestQueue = Volley.newRequestQueue(AlimentadorActivity.this);
        requestQueue.add(arrayRequest);

    }

    public void addAlimentador(View v){
        TextView txtViewclose, txtViewtitulo;
        EditText eTxtSerial;
        Button btnsalvar;



        dialog.setContentView(R.layout.activity_cria_modifica_alimentador);

        txtViewclose = (TextView)  dialog.findViewById(R.id.txtclose_modifica_alimentador);
        txtViewtitulo = (TextView)  dialog.findViewById(R.id.txtitulo_modifica_alimentador);

        txtViewtitulo.setText("Adicionar Alimentador");

        txtViewclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        eTxtSerial = (EditText) dialog.findViewById(R.id.serial_modifica_alimentador);

        spinnerPetsAlimentador = (Spinner) dialog.findViewById(R.id.spinnerPetsAlimentador);
        spinnerDietasAlimentador = (Spinner) dialog.findViewById(R.id.spinnerDietasAlimentador);

        ArrayAdapter<Pet> adapterPet = new ArrayAdapter<Pet>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, pets);
        adapterPet.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spinnerPetsAlimentador.setAdapter(adapterPet);

        //ao selecionarmos um Pet, criamos o spinner de dietas disponiveis deste Pet. Para que possa ser setada uma dieta para este alimentador
        spinnerPetsAlimentador.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ArrayAdapter<Dieta> adapterDieta = new ArrayAdapter<Dieta>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, getDietasFromPet(((Pet)spinnerPetsAlimentador.getSelectedItem())));
                adapterDieta.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
                spinnerDietasAlimentador.setAdapter(adapterDieta);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });




        btnsalvar = (Button)  dialog.findViewById(R.id.btnsubmit_modifica_alimentador);


        btnsalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Alimentador novoAlimentador = new Alimentador();
                novoAlimentador.setSerial(eTxtSerial.getText().toString());
                novoAlimentador.setIdpet(((Pet) spinnerPetsAlimentador.getSelectedItem()).getIdpet());
                novoAlimentador.setIddieta(((Dieta) spinnerDietasAlimentador.getSelectedItem()).getIddieta());
                novoAlimentador.setIdusuario(Integer.valueOf(idUsuarioRecebido));
                Submit(novoAlimentador);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void Submit(Alimentador novoAlimentador) {

        String url = "http://177.44.248.45:3300/alimentador";

        Map<String, String> params = new HashMap<>();
        params.put("serial", novoAlimentador.getSerial());
        params.put("idusuario", String.valueOf(novoAlimentador.getIdusuario()));
        params.put("idpet", String.valueOf(novoAlimentador.getIdpet()));
        params.put("iddieta", String.valueOf(novoAlimentador.getIddieta()));


        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                refresh.post(new Runnable() {
                    @Override
                    public void run() {
                        dietas.clear();
                        pets.clear();
                        alimentadores.clear();
                        getData();
                    }
                });
                Toast.makeText(getApplicationContext(), "Dados salvos com suscesso", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), "Falha ao salvar os dados", Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);

    }

    private void adapterPush(ArrayList<Alimentador> alimentadores, ArrayList<Dieta> dietas, ArrayList<Pet> pets) {

        alimentadorAdapter = new AlimentadorAdapter(this, alimentadores, dietas, pets);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(alimentadorAdapter);


    }

    public ArrayList<Dieta> getDietasFromPet(Pet pet){
        ArrayList<Dieta> dietasDoPet = new ArrayList<>();
        for(Dieta dietaAux: dietas){
            if(dietaAux.getIdpet()==pet.getIdpet()){
                dietasDoPet.add(dietaAux);
            }
        }
        return dietasDoPet;
    }

    @Override
    public void onRefresh() {
        alimentadores.clear();
        dietas.clear();
        pets.clear();
        getData();
    }
}