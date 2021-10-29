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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.apf.Adapter.PetAdapter;
import com.example.apf.model.Pet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PetActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RequestQueue requestQueue;
    private SwipeRefreshLayout refresh;
    private ArrayList<Pet> pets = new ArrayList<>();
    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private PetAdapter petAdapter;

    String idUsuarioRecebido = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);

        //transforma a cor em preto do text header
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">"+getString(R.string.app_name)+"</font>"));

        Intent intent = getIntent();
        idUsuarioRecebido = intent.getStringExtra("usuarioLogado"); //captura o id do usuario logado vindo da mainactivity


        refresh = (SwipeRefreshLayout) findViewById(R.id.swiperdown);
        recyclerView = (RecyclerView) findViewById(R.id.listpets);

        dialog = new Dialog ( this);

        refresh.setOnRefreshListener(this);
        refresh.post(new Runnable() {
            @Override
            public void run() {
                pets.clear();
                getData();
            }
        });

    }

    private void getData() {
        refresh.setRefreshing(true);
        String url = "http://177.44.248.45:3300/pet/"+idUsuarioRecebido;
        arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
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
                adapterPush(pets);
                refresh.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue = Volley.newRequestQueue(PetActivity.this);
        requestQueue.add(arrayRequest);
    }

    private void adapterPush(ArrayList<Pet> pets) {
        petAdapter = new PetAdapter(this, pets);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(petAdapter);
    }

    public void addPet(View v){
        TextView txtViewclose, txtViewtitulo;
        EditText eTxtnome, eTxtanimal, eTxtdtnascimento, eTxtraca, eTxtporte;
        Button btnsalvar;

        dialog.setContentView(R.layout.activity_cria_modifica_pet);

        txtViewclose = (TextView)  dialog.findViewById(R.id.txtclose_modifica_pet);
        txtViewtitulo = (TextView)  dialog.findViewById(R.id.txtitulo_modifica_pet);
        txtViewtitulo.setText("Adicionar Pet");

        txtViewclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        eTxtnome = (EditText) dialog.findViewById(R.id.nome_modifica_dieta);
        eTxtanimal = (EditText) dialog.findViewById(R.id.animal_modifica_pet);
        eTxtdtnascimento = (EditText) dialog.findViewById(R.id.dtnascimento_modifica_pet);
        eTxtraca = (EditText) dialog.findViewById(R.id.raca_modifica_pet);
        eTxtporte = (EditText) dialog.findViewById(R.id.porte_modifica_pet);

        btnsalvar = (Button)  dialog.findViewById(R.id.btnsubmit_modifica_pet);

        btnsalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pet novoPet = new Pet();
                novoPet.setNome(eTxtnome.getText().toString());
                novoPet.setAnimal(eTxtanimal.getText().toString());
                novoPet.setDtnascimento(eTxtdtnascimento.getText().toString());
                novoPet.setRaca(eTxtraca.getText().toString());
                novoPet.setPorte(eTxtporte.getText().toString());
                novoPet.setIdusuario(Integer.parseInt(idUsuarioRecebido));
                Submit(novoPet);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void Submit(Pet pet) {
        String url2 = "http://177.44.248.45:3300/pet";

        Map<String, String> params = new HashMap<>();
        params.put("nome", pet.getNome());
        params.put("animal", pet.getAnimal());
        params.put("dtnascimento", pet.getDtnascimento());
        params.put("raca", pet.getRaca());
        params.put("porte", pet.getPorte());
        params.put("idusuario", String.valueOf(pet.getIdusuario()));

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url2, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                refresh.post(new Runnable() {
                    @Override
                    public void run() {
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
        pets.clear();
        getData();
    }
}