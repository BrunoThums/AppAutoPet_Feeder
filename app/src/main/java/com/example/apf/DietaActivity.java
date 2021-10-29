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
import android.view.View;
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
import com.example.apf.Adapter.DietaAdapter;
import com.example.apf.Adapter.PetAdapter;
import com.example.apf.model.Dieta;
import com.example.apf.model.Pet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DietaActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RequestQueue requestQueue;
    private SwipeRefreshLayout refresh;
    private ArrayList<Dieta> dietas = new ArrayList<>();
    private ArrayList<Pet> pets = new ArrayList<>();
    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private DietaAdapter dietaAdapter;
    String idUsuarioRecebido = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dieta);



        //transforma a cor em preto do text header
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">"+getString(R.string.app_name)+"</font>"));

        Intent intent = getIntent();
        idUsuarioRecebido = intent.getStringExtra("usuarioLogado"); //captura o id do usuario logado vindo da mainactivity

        refresh = (SwipeRefreshLayout) findViewById(R.id.swiperdown);
        recyclerView = (RecyclerView) findViewById(R.id.listdietas);

        dialog = new Dialog( this);

        refresh.setOnRefreshListener(this);
        refresh.post(new Runnable() {
            @Override
            public void run() {
                pets.clear();
                dietas.clear();
                getData();
            }
        });

    }
    private void getData() {
        refresh.setRefreshing(true);

        //inicialmente capture todas as dietas que o usuario com a id recebida tem cadastrado
        String url = "http://177.44.248.45:3300/dieta/usuario/"+idUsuarioRecebido;

        arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
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

                refresh.setRefreshing(true);
                //depois capture todos os pets que o usuario com a id recebida tem cadastrado para que ele possa criar uma dieta e vincular com um de seus pets
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
                        //chama o metodo que vai atualizar o recycler view com as dietas e pets recebidos
                        adapterPush(dietas, pets);
                        refresh.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Falha ao recuperar o Pets", Toast.LENGTH_LONG).show();
                    }
                });

                requestQueue = Volley.newRequestQueue(DietaActivity.this);
                requestQueue.add(arrayRequest);



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Falha ao recuperar as Dietas", Toast.LENGTH_LONG).show();
            }
        });

        requestQueue = Volley.newRequestQueue(DietaActivity.this);
        requestQueue.add(arrayRequest);
    }

    private void adapterPush(ArrayList<Dieta> dietas, ArrayList<Pet> pets) {

        dietaAdapter = new DietaAdapter(this,  dietas, pets);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dietaAdapter);

    }

    public void addDieta(View v){
        TextView txtViewclose, txtViewtitulo;
        EditText eTxtnome;
        Button btnsalvar;
        Spinner spinnerPetsDieta;

        dialog.setContentView(R.layout.activity_cria_modifica_dieta);

        txtViewclose = (TextView)  dialog.findViewById(R.id.txtclose_modifica_dieta);
        txtViewtitulo = (TextView)  dialog.findViewById(R.id.txtitulo_modifica_dieta);

        txtViewtitulo.setText("Adicionar Dieta");

        txtViewclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        eTxtnome = (EditText) dialog.findViewById(R.id.nome_modifica_dieta);
        spinnerPetsDieta = (Spinner) dialog.findViewById(R.id.spinnerPetsDieta);
        ArrayAdapter<Pet> adapter = new ArrayAdapter<Pet>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, pets);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spinnerPetsDieta.setAdapter(adapter);


        btnsalvar = (Button)  dialog.findViewById(R.id.btnsubmit_modifica_dieta);

        btnsalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dieta novaDieta = new Dieta();
                novaDieta.setNome(eTxtnome.getText().toString());
                novaDieta.setIdpet(((Pet)spinnerPetsDieta.getSelectedItem()).getIdpet());  //captura a id do pet selecionado na combobox de pets
                Submit(novaDieta);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void Submit(Dieta novaDieta) {
        String url = "http://177.44.248.45:3300/dieta";

        Map<String, String> params = new HashMap<>();
        params.put("nome", novaDieta.getNome());
        params.put("idpet", String.valueOf(novaDieta.getIdpet()));


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

    @Override
    public void onRefresh() {
        dietas.clear();
        pets.clear();
        getData();

    }
}