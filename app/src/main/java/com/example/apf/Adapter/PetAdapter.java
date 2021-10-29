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
import com.example.apf.model.Pet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Pet> pets;
    private String url ="";

    public PetAdapter(Context context, ArrayList<Pet> pets) {
        this.context = context;
        this.pets = pets;
    }


    @NonNull
    @Override
    public PetAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.activity_lista_pet, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.nome.setText(pets.get(position).getNome());
        holder.id.setText("#" + String.valueOf(position+1));
        holder.editPet.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                editPet(pets.get(position));
            }
        });
        holder.deletePet.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                deletePet(pets.get(position));
            }
        });
    }

    private void deletePet(Pet pet) {
        TextView txtViewclose, txtViewtitulo;
        Button btnexcluir;

        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_deleta_pet);

        txtViewclose = (TextView)  dialog.findViewById(R.id.txtclose_deleta_pet);
        txtViewtitulo = (TextView)  dialog.findViewById(R.id.txtitulo_deleta_pet);
        txtViewtitulo.setText("excluir Pet");



        txtViewclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnexcluir = (Button)  dialog.findViewById(R.id.btndeletar_deleta_pet);

        btnexcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Submit("DELETE", pet, dialog);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void editPet(Pet pet) {

        TextView txtViewclose, txtViewtitulo;
        EditText eTxtnome, eTxtanimal, eTxtdtnascimento, eTxtraca, eTxtporte;
        Button btnsalvar;

        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_cria_modifica_pet);

        txtViewclose = (TextView)  dialog.findViewById(R.id.txtclose_modifica_pet);
        txtViewtitulo = (TextView)  dialog.findViewById(R.id.txtitulo_modifica_pet);
        txtViewtitulo.setText("Editar Pet");



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

        eTxtnome.setText(pet.getNome());
        eTxtanimal.setText(pet.getAnimal());
        eTxtdtnascimento.setText((pet.getDtnascimento()));
        eTxtraca.setText(pet.getRaca());
        eTxtporte.setText(pet.getPorte());

        btnsalvar = (Button)  dialog.findViewById(R.id.btnsubmit_modifica_pet);

        btnsalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pet petAtualizado = new Pet(pet.getIdpet(), eTxtnome.getText().toString(),eTxtanimal.getText().toString(), eTxtdtnascimento.getText().toString(),eTxtraca.getText().toString(), eTxtporte.getText().toString(), pet.getIdusuario());
                Submit("PUT", petAtualizado, dialog);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void Submit(String method, Pet pet, Dialog dialog) {

        if(method=="PUT") {
            String url = "http://177.44.248.45:3300/pet";

            Map<String, String> params = new HashMap<>();
            params.put("idpet", String.valueOf(pet.getIdpet()));
            params.put("nome", pet.getNome());
            params.put("animal", pet.getAnimal());
            params.put("dtnascimento", pet.getDtnascimento());
            params.put("raca", pet.getRaca());
            params.put("porte", pet.getPorte());
            params.put("idusuario", String.valueOf(pet.getIdusuario()));

            JSONObject parameters = new JSONObject(params);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    Toast.makeText(context, "Dados atualizados com suscesso", Toast.LENGTH_LONG).show();
                    //atualiza o arraylist de pets com os valores atualizados para esse pet
                    Pet petAux= getPetFromId(pet.getIdpet());
                    int index = pets.indexOf(petAux);
                    pets.set(index, pet);
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

            String url = "http://177.44.248.45:3300/pet/"+String.valueOf(pet.getIdpet());


            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    Toast.makeText(context, "Dados removidos com suscesso", Toast.LENGTH_LONG).show();
                    //remova esse pet do arraylist e atualize o recyclerview
                    int index = pets.indexOf(pet);
                    pets.remove(pet);
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

    public Pet getPetFromId(int idPet){
        for(Pet petAux : pets){
            if(petAux.getIdpet()== idPet){
                return petAux;
            }

        }
        return null;

    }


    @Override
    public int getItemCount() {
        return pets.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView nome, id;
        private ImageView editPet, deletePet;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.idPet);
            nome = (TextView) itemView.findViewById(R.id.nomePet);
            editPet = (ImageView) itemView.findViewById(R.id.editPet);
            deletePet = (ImageView) itemView.findViewById(R.id.deletePet);
        }
    }
}
