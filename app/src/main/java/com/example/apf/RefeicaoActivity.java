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
import com.example.apf.Adapter.RefeicaoAdapter;
import com.example.apf.model.Dieta;
import com.example.apf.model.Pet;
import com.example.apf.model.Refeicao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RefeicaoActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RequestQueue requestQueue;
    private SwipeRefreshLayout refresh;
    private ArrayList<Refeicao> refeicoes = new ArrayList<>();
    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private RefeicaoAdapter refeicaoAdapter;

    String idDietaRecebida = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refeicao);

        //transforma a cor em preto do text header
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name) + "</font>"));

        Intent intent = getIntent();
        idDietaRecebida = intent.getStringExtra("dietaSelecionada"); //captura a id da dieta que o usuario selecionou para criar uma refeição, vem do DietaAdapter

        refresh = (SwipeRefreshLayout) findViewById(R.id.swiperdown);
        recyclerView = (RecyclerView) findViewById(R.id.listrefeicoes);

        dialog = new Dialog(this);

        refresh.setOnRefreshListener(this);
        refresh.post(new Runnable() {
            @Override
            public void run() {
                refeicoes.clear();
                getData();
            }
        });


    }

    private void getData() {
        refresh.setRefreshing(true);
        String url = "http://177.44.248.45:3300/refeicao/" + idDietaRecebida;
        arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        Refeicao refeicao = new Refeicao();
                        refeicao.setIdrefeicao(jsonObject.getInt("idrefeicao"));
                        refeicao.setNome(jsonObject.getString("nome"));
                        refeicao.setHorario(jsonObject.getString("horario"));
                        refeicao.setQuantidadegramas(jsonObject.getString("quantidadegramas"));
                        refeicao.setIddieta(jsonObject.getString("iddieta"));
                        refeicoes.add(refeicao);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterPush(refeicoes);
                refresh.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue = Volley.newRequestQueue(RefeicaoActivity.this);
        requestQueue.add(arrayRequest);

    }

    private void adapterPush(ArrayList<Refeicao> refeicoes) {

        refeicaoAdapter = new RefeicaoAdapter(this, refeicoes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(refeicaoAdapter);

    }

    public void addRefeicao(View v) {
        TextView txtViewclose, txtViewtitulo;
        EditText eTxtnome, eTxtHorario, eTxtGramas;
        Button btnsalvar;


        dialog.setContentView(R.layout.activity_cria_modifica_refeicao);

        txtViewclose = (TextView) dialog.findViewById(R.id.txtclose_modifica_refeicao);
        txtViewtitulo = (TextView) dialog.findViewById(R.id.txtitulo_modifica_refeicao);

        txtViewtitulo.setText("Adicionar Refeicao");

        txtViewclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        eTxtnome = (EditText) dialog.findViewById(R.id.nome_modifica_refeicao);
        eTxtHorario = (EditText) dialog.findViewById(R.id.horario_modifica_refeicao);
        eTxtGramas = (EditText) dialog.findViewById(R.id.quantidadeGramas_modifica_refeicao);


        btnsalvar = (Button) dialog.findViewById(R.id.btnsubmit_modifica_refeicao);

        btnsalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Refeicao novaRefeicao = new Refeicao();
                novaRefeicao.setNome(eTxtnome.getText().toString());
                novaRefeicao.setHorario(eTxtHorario.getText().toString());
                novaRefeicao.setQuantidadegramas(eTxtGramas.getText().toString());
                novaRefeicao.setIddieta(idDietaRecebida);
                Submit(novaRefeicao);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void Submit(Refeicao novaRefeicao) {

        String url2 = "http://177.44.248.45:3300/refeicao";

        Map<String, String> params = new HashMap<>();
        params.put("nome", novaRefeicao.getNome());
        params.put("horario", novaRefeicao.getHorario());
        params.put("quantidadegramas", novaRefeicao.getQuantidadegramas());
        params.put("iddieta", idDietaRecebida);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url2, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                refresh.post(new Runnable() {
                    @Override
                    public void run() {
                        refeicoes.clear();
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
        refeicoes.clear();
        getData();

    }
}