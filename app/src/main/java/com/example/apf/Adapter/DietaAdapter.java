package com.example.apf.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.apf.DietaActivity;
import com.example.apf.R;
import com.example.apf.RefeicaoActivity;
import com.example.apf.model.Dieta;
import com.example.apf.model.Pet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DietaAdapter extends RecyclerView.Adapter<DietaAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Dieta> dietas;
    private ArrayList<Pet> pets;



    public DietaAdapter(Context context, ArrayList<Dieta> dietas, ArrayList<Pet> pets) {
        this.context = context;
        this.pets = pets;
        this.dietas = dietas;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.activity_lista_dieta, parent, false);

        return new DietaAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.nome.setText(dietas.get(position).getNome());
        holder.id.setText("#" + String.valueOf(position+1));

        //exibe o nome do pet no txtfield abaixo do nome da Dieta.
        //Caso a dieta tenha sido cadastrada com um pet que posteriormente foi removido será exibido "---" no txtfeild
        Pet petVinculadoADieta = getPetFromID(dietas.get(position).getIdpet());
        if(petVinculadoADieta!=null){
            holder.nomePetDieta.setText(petVinculadoADieta.getNome());
        }else{
            holder.nomePetDieta.setText("---");
        }

        holder.editDieta.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editDieta(dietas.get(position));
            }
        });

        holder.deleteDieta.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                deleteDieta(dietas.get(position));
            }
        });

        //captura o click sobre uma dieta do recyclerview para entao abrir a nova activity para criar uma refeicao
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Dieta selecionanda: "  + dietas.get(position).getNome(), Toast.LENGTH_SHORT).show();
                //verifica se o pet vinculado a dieta ainda esta ativo, ou seja não foi excluido, para entao abrir a activity de refeições
                if(getPetFromID(dietas.get(position).getIdpet())!=null) {
                    Intent intent = new Intent(context.getApplicationContext(), RefeicaoActivity.class);
                    intent.putExtra("dietaSelecionada", String.valueOf(dietas.get(position).getIddieta())); //passa a id da dieta selecionada para a activity refeições
                    context.startActivity(intent);
                }else{
                    //se o usuario clicou sobre uma dieta cujo o pet ja foi excluido nao podemos deixa-lo adicionar refeições então exibimos uma mensagem informando que o pet vinculado é invalido
                    Toast.makeText(context, "Pet inválido ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void deleteDieta(Dieta dieta) {

        TextView txtViewclose, txtViewtitulo;
        Button btnexcluir;

        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_deleta_dieta);

        txtViewclose = (TextView)  dialog.findViewById(R.id.txtclose_deleta_dieta);
        txtViewtitulo = (TextView)  dialog.findViewById(R.id.txtitulo_deleta_dieta);
        txtViewtitulo.setText("Excluir Dieta");



        txtViewclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnexcluir = (Button)  dialog.findViewById(R.id.btndeletar_deleta_dieta);

        btnexcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Submit("DELETE", dieta, dialog);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void editDieta(Dieta dieta) {
        TextView txtViewclose, txtViewtitulo;
        EditText eTxtnome;
        Button btnsalvar;
        Spinner spinnerPetsDieta;

        final Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_cria_modifica_dieta);

        txtViewclose = (TextView)  dialog.findViewById(R.id.txtclose_modifica_dieta);
        txtViewtitulo = (TextView)  dialog.findViewById(R.id.txtitulo_modifica_dieta);

        txtViewtitulo.setText("Editar Dieta");

        txtViewclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        eTxtnome = (EditText) dialog.findViewById(R.id.nome_modifica_dieta);
        spinnerPetsDieta = (Spinner) dialog.findViewById(R.id.spinnerPetsDieta);
        ArrayAdapter<Pet> adapter = new ArrayAdapter<Pet>(context.getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, pets);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spinnerPetsDieta.setAdapter(adapter);

        eTxtnome.setText(dieta.getNome());

        Pet petVinculadoADieta = getPetFromID(dieta.getIdpet());
        if(petVinculadoADieta!=null){
            spinnerPetsDieta.setSelection(pets.indexOf(petVinculadoADieta));
        }

        btnsalvar = (Button)  dialog.findViewById(R.id.btnsubmit_modifica_dieta);

        btnsalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dieta atualizadaDieta = new Dieta();
                atualizadaDieta.setIddieta(dieta.getIddieta());
                atualizadaDieta.setNome(eTxtnome.getText().toString());
                atualizadaDieta.setIdpet(((Pet)spinnerPetsDieta.getSelectedItem()).getIdpet());
                Submit("PUT", atualizadaDieta, dialog);

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void Submit(String method, Dieta dieta, Dialog dialog) {

        if(method=="PUT") {
            String url = "http://177.44.248.45:3300/dieta";

            Map<String, String> params = new HashMap<>();
            params.put("iddieta", String.valueOf(dieta.getIddieta()));
            params.put("nome", dieta.getNome());
            params.put("idpet", String.valueOf(dieta.getIdpet()));


            JSONObject parameters = new JSONObject(params);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    Toast.makeText(context, "Dados atualizados com suscesso", Toast.LENGTH_LONG).show();
                    //atualiza o arraylist de dietas com os valores atualizados para essa dieta
                    Dieta dietaAux = getDietaFromID(dieta.getIddieta());
                    int index = dietas.indexOf(dietaAux);
                    dietas.set(index, dieta);
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

            String url = "http://177.44.248.45:3300/dieta/"+String.valueOf(dieta.getIddieta());


            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    Toast.makeText(context, "Dados removidos com suscesso", Toast.LENGTH_LONG).show();
                    //remova essa dieta do arraylist e atualize o recyclerView
                    int index = dietas.indexOf(dieta);
                    dietas.remove(dieta);
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



    @Override
    public int getItemCount() {
        return dietas.size();
    }

    public Dieta getDietaFromID(int iddieta){
        for(Dieta dietaAux: dietas){
            if(dietaAux.getIddieta()==iddieta){
                return dietaAux;
            }
        }
        return null;
    }

    public Pet getPetFromID(int idpet){
        for(Pet petAux: pets){
            if(petAux.getIdpet()==idpet){
                return petAux;
            }
        }
        return null;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView nome, id, nomePetDieta;
        private ImageView editDieta, deleteDieta;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.idDieta);
            nome = (TextView) itemView.findViewById(R.id.nomeDieta);
            nomePetDieta = (TextView) itemView.findViewById(R.id.nomePetDieta);
            editDieta = (ImageView) itemView.findViewById(R.id.editDieta);
            deleteDieta = (ImageView) itemView.findViewById(R.id.deleteDieta);
        }

    }
}


