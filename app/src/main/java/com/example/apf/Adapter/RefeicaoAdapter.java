package com.example.apf.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.apf.R;
import com.example.apf.model.Dieta;
import com.example.apf.model.Pet;
import com.example.apf.model.Refeicao;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RefeicaoAdapter extends RecyclerView.Adapter<RefeicaoAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<Refeicao> refeicoes;

    public RefeicaoAdapter(Context context, ArrayList<Refeicao> refeicoes) {
        this.context = context;
        this.refeicoes = refeicoes;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.activity_lista_refeicao, parent, false);

        return new RefeicaoAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.nome.setText(refeicoes.get(position).getNome());
        holder.id.setText("#" + String.valueOf(position+1));
        holder.horario.setText(refeicoes.get(position).getHorario());
        holder.quantidadegramas.setText(refeicoes.get(position).getQuantidadegramas());

        holder.editRefeicao.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editRefeicao(refeicoes.get(position));
            }
        });

        holder.deleteRefeicao.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                deleteRefeicao(refeicoes.get(position));
            }
        });
    }

    private void deleteRefeicao(Refeicao refeicao) {
        TextView txtViewclose, txtViewtitulo;
        Button btnexcluir;

        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_deleta_refeicao);

        txtViewclose = (TextView)  dialog.findViewById(R.id.txtclose_deleta_refeicao);
        txtViewtitulo = (TextView)  dialog.findViewById(R.id.txtitulo_deleta_refeicao);
        txtViewtitulo.setText("Excluir Refeicao");



        txtViewclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnexcluir = (Button)  dialog.findViewById(R.id.btndeletar_deleta_refeicao);

        btnexcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Submit("DELETE", refeicao, dialog);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void editRefeicao(Refeicao refeicao) {

        TextView txtViewclose, txtViewtitulo;
        EditText eTxtnome, eTxtHorario, eTxtGramas;
        Button btnsalvar;

        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_cria_modifica_refeicao);

        txtViewclose = (TextView)  dialog.findViewById(R.id.txtclose_modifica_refeicao);
        txtViewtitulo = (TextView)  dialog.findViewById(R.id.txtitulo_modifica_refeicao);
        txtViewtitulo.setText("Editar Pet");



        txtViewclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        eTxtnome = (EditText) dialog.findViewById(R.id.nome_modifica_refeicao);
        eTxtHorario = (EditText) dialog.findViewById(R.id.horario_modifica_refeicao);
        eTxtGramas = (EditText) dialog.findViewById(R.id.quantidadeGramas_modifica_refeicao);


        eTxtnome.setText(refeicao.getNome());
        eTxtHorario.setText(refeicao.getHorario());
        eTxtGramas.setText((refeicao.getQuantidadegramas()));

        btnsalvar = (Button)  dialog.findViewById(R.id.btnsubmit_modifica_refeicao);

        btnsalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Refeicao refeicaoAtualizada = new Refeicao();
                refeicaoAtualizada.setIdrefeicao(refeicao.getIdrefeicao());
                refeicaoAtualizada.setNome(eTxtnome.getText().toString());
                refeicaoAtualizada.setHorario(eTxtHorario.getText().toString());
                refeicaoAtualizada.setQuantidadegramas(eTxtGramas.getText().toString());
                refeicaoAtualizada.setIddieta(refeicao.getIddieta());
                Submit("PUT", refeicaoAtualizada, dialog);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void Submit(String method, Refeicao refeicao, Dialog dialog) {
        if(method=="PUT") {
            String url = "http://177.44.248.45:3300/refeicao";

            Map<String, String> params = new HashMap<>();
            params.put("idrefeicao", String.valueOf(refeicao.getIdrefeicao()));
            params.put("nome", refeicao.getNome());
            params.put("horario", String.valueOf(refeicao.getHorario()));
            params.put("quantidadegramas", String.valueOf(refeicao.getQuantidadegramas()));
            params.put("iddieta", String.valueOf(refeicao.getIddieta()));


            JSONObject parameters = new JSONObject(params);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    Toast.makeText(context, "Dados atualizados com suscesso", Toast.LENGTH_LONG).show();
                    //atualiza o arraylist de refeicoes  com os valores atualizados para essa refeicao
                    Refeicao refeicaoAux = getRefeicaoFromID(refeicao.getIdrefeicao());
                    int index = refeicoes.indexOf(refeicaoAux);
                    refeicoes.set(index, refeicao);
                    notifyItemChanged(index);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(context, "Falha ao atualizar os dados", Toast.LENGTH_LONG).show();
                }
            });

            Volley.newRequestQueue(context).add(jsonRequest);

        }else if(method=="DELETE"){

            String url = "http://177.44.248.45:3300/refeicao/"+String.valueOf(refeicao.getIdrefeicao());


            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    Toast.makeText(context, "Dados removidos com suscesso", Toast.LENGTH_LONG).show();
                    //remova essa refeicao do arraylist e atualize o recyclerView
                    int index = refeicoes.indexOf(refeicao);
                    refeicoes.remove(index);
                    notifyItemRemoved(index);
                    notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(context, "Falha ao remover os dados", Toast.LENGTH_LONG).show();
                }
            });

            Volley.newRequestQueue(context).add(jsonRequest);

        }

    }

    private Refeicao getRefeicaoFromID(int idrefeicao) {
        for(Refeicao refeicaoAux: refeicoes){
            if(refeicaoAux.getIdrefeicao()==idrefeicao){
                return refeicaoAux;
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return refeicoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView id, nome, horario, quantidadegramas;
        private ImageView editRefeicao, deleteRefeicao;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.idRefeicao);
            nome = (TextView) itemView.findViewById(R.id.nomeRefeicao);
            horario = (TextView) itemView.findViewById(R.id.horarioRefeicao);
            quantidadegramas = (TextView) itemView.findViewById(R.id.quantidadeRefeicao);
            editRefeicao = (ImageView) itemView.findViewById(R.id.editRefeicao);
            deleteRefeicao = (ImageView) itemView.findViewById(R.id.deleteRefeicao);
        }
    }
}
