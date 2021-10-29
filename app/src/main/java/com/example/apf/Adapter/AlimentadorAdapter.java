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
import android.widget.AdapterView;
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
import com.example.apf.ComandoActivity;
import com.example.apf.R;
import com.example.apf.RefeicaoActivity;
import com.example.apf.SensorActivity;
import com.example.apf.model.Alimentador;
import com.example.apf.model.Dieta;
import com.example.apf.model.Pet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AlimentadorAdapter extends RecyclerView.Adapter<AlimentadorAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<Dieta> dietas;
    private ArrayList<Pet> pets;
    private ArrayList<Alimentador> alimentadores;



    public AlimentadorAdapter(Context context, ArrayList<Alimentador> alimentadores, ArrayList<Dieta> dietas, ArrayList<Pet> pets) {
        this.context = context;
        this.pets = pets;
        this.dietas = dietas;
        this.alimentadores = alimentadores;

    }
    
    @NonNull
    @Override
    public AlimentadorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.activity_lista_alimentador, parent, false);

        return new AlimentadorAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlimentadorAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.id.setText("#" + String.valueOf(position+1));
        holder.serial.setText(alimentadores.get(position).getSerial());
        

        //exibe o nome do pet no txtfield abaixo do nome da Dieta.
        //Caso a dieta tenha sido cadastrada com uma pet que posteriormente foi removido será exibido "---" no txtfeild
        Pet petVinculadoaoAlimentador = getPetFromID(alimentadores.get(position).getIdpet());
        if(petVinculadoaoAlimentador!=null){
            holder.nomePetAlimentador.setText(petVinculadoaoAlimentador.getNome());
        }else{
            holder.nomePetAlimentador.setText("---");
        }
        Dieta dietaVinculadoaoAlimentador = getDietaFromID(alimentadores.get(position).getIddieta());
        if(dietaVinculadoaoAlimentador!=null){
            holder.nomeDietaAlimentador.setText(dietaVinculadoaoAlimentador.getNome());
        }else{
            holder.nomeDietaAlimentador.setText("---");
        }

        holder.editAlimentador.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editAlimentador(alimentadores.get(position));
            }
        });

        holder.deleteAlimentador.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                deleteAlimentador(alimentadores.get(position));
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Dieta selecionanda: "  + dietas.get(position).getNome(), Toast.LENGTH_SHORT).show();
                if(getPetFromID(alimentadores.get(position).getIdpet())!=null && getDietaFromID(alimentadores.get(position).getIddieta())!=null) {
                    opcoesDoAlimentador(alimentadores.get(position));
                }else{
                    Toast.makeText(context, "Pet ou Dieta invalida atribuida ao alimentador", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void deleteAlimentador(Alimentador alimentador) {

        TextView txtViewclose, txtViewtitulo;
        Button btnexcluir;

        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_deleta_alimentador);

        txtViewclose = (TextView)  dialog.findViewById(R.id.txtclose_deleta_alimentador);
        txtViewtitulo = (TextView)  dialog.findViewById(R.id.txtitulo_deleta_alimentador);
        txtViewtitulo.setText("Excluir Alimentador");



        txtViewclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnexcluir = (Button)  dialog.findViewById(R.id.btndeletar_deleta_alimentador);

        btnexcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                submit("DELETE", alimentador, dialog);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void editAlimentador(Alimentador alimentador) {

        TextView txtViewclose, txtViewtitulo;
        EditText eTxtserial;
        Button btnsalvar;
        Spinner spinnerPetsAlimentador;
        Spinner spinnerDietasAlimentador;

        final Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_cria_modifica_alimentador);

        txtViewclose = (TextView)  dialog.findViewById(R.id.txtclose_modifica_alimentador);
        txtViewtitulo = (TextView)  dialog.findViewById(R.id.txtitulo_modifica_alimentador);

        txtViewtitulo.setText("Editar Alimentador");

        txtViewclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        eTxtserial = (EditText) dialog.findViewById(R.id.serial_modifica_alimentador);
        
        spinnerPetsAlimentador = (Spinner) dialog.findViewById(R.id.spinnerPetsAlimentador);
        spinnerDietasAlimentador = (Spinner) dialog.findViewById(R.id.spinnerDietasAlimentador);

        //crie um spinner com os pet desse usuario
        ArrayAdapter<Pet> adapterPet = new ArrayAdapter<Pet>(context.getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, pets);
        adapterPet.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spinnerPetsAlimentador.setAdapter(adapterPet);

        // assim que um pet é selecionado carregue as dietas desse pet no spinner
        spinnerPetsAlimentador.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ArrayAdapter<Dieta> adapterDieta = new ArrayAdapter<Dieta>(context.getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, getDietasFromPet((Pet)spinnerPetsAlimentador.getSelectedItem()));
                adapterDieta.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
                spinnerDietasAlimentador.setAdapter(adapterDieta);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        //seta o campo serial com o serial atualmente gravado neste alimentador
        eTxtserial.setText(alimentador.getSerial());

        //ja deixa selecionado o pet atualmente definido para o alimentador
        Pet petVinculadoAoAlimentador = getPetFromID(alimentador.getIdpet());
        if(petVinculadoAoAlimentador!=null){
            spinnerPetsAlimentador.setSelection(pets.indexOf(petVinculadoAoAlimentador));
        }

        //ja deixa selecionada a dieta atualmente definida para o alimentador
        Dieta dietaVinculadaAoAlimentador = getDietaFromID(alimentador.getIddieta());
        if(dietaVinculadaAoAlimentador!=null){
            spinnerDietasAlimentador.setSelection(dietas.indexOf(dietaVinculadaAoAlimentador));
        }

        btnsalvar = (Button)  dialog.findViewById(R.id.btnsubmit_modifica_alimentador);

        btnsalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Alimentador atualizadoAlimentador = new Alimentador();
                atualizadoAlimentador.setIdalimentador(alimentador.getIdalimentador());
                atualizadoAlimentador.setSerial(eTxtserial.getText().toString());
                atualizadoAlimentador.setIdusuario(alimentador.getIdusuario());
                atualizadoAlimentador.setIdpet(((Pet)spinnerPetsAlimentador.getSelectedItem()).getIdpet());
                atualizadoAlimentador.setIddieta(((Dieta)spinnerDietasAlimentador.getSelectedItem()).getIddieta());
                submit("PUT", atualizadoAlimentador, dialog);

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        
    }

    private void opcoesDoAlimentador(Alimentador alimentadorSelecionado){

        TextView txtViewclose, txtViewtitulo;
        Button btnComandos, btnSensores;

        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_opcao_alimentador);

        txtViewclose = (TextView)  dialog.findViewById(R.id.txtclose_opcao_alimentador);
        txtViewtitulo = (TextView)  dialog.findViewById(R.id.txtitulo_opcao_alimentador);
        txtViewtitulo.setText("Opções do Alimentador");

        txtViewclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnComandos = (Button)  dialog.findViewById(R.id.btnenviar_comando_alimentador);
        btnSensores = (Button)  dialog.findViewById(R.id.btnler_sensores);

        btnComandos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(context, "Botao de enviar comandos pressionado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context.getApplicationContext(), ComandoActivity.class);
                intent.putExtra("idAlimentadorSelecionado", String.valueOf(alimentadorSelecionado.getIdalimentador()));
                context.startActivity(intent);
                dialog.dismiss();
            }
        });
        btnSensores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(context, "Botao de ler sensores pressionado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context.getApplicationContext(), SensorActivity.class);
                intent.putExtra("idAlimentadorSelecionado", String.valueOf(alimentadorSelecionado.getIdalimentador()));
                context.startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void submit(String method, Alimentador alimentador, Dialog dialog) {

        if(method=="PUT") {
            String url = "http://177.44.248.45:3300/alimentador";

            Map<String, String> params = new HashMap<>();
            params.put("idalimentador", String.valueOf(alimentador.getIdalimentador()));
            params.put("serial", alimentador.getSerial());
            params.put("idusuario", String.valueOf(alimentador.getIdusuario()));
            params.put("idpet", String.valueOf(alimentador.getIdpet()));
            params.put("iddieta", String.valueOf(alimentador.getIddieta()));


            JSONObject parameters = new JSONObject(params);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    Toast.makeText(context, "Dados atualizados com suscesso", Toast.LENGTH_LONG).show();
                    //atualiza o arraylist de alimentadores com os valores atualizados para essa alimentador
                    Alimentador alimentadorAux = getAlimentadorFromID(alimentador.getIdalimentador());
                    int index = alimentadores.indexOf(alimentadorAux);
                    alimentadores.set(index, alimentador);
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

            String url = "http://177.44.248.45:3300/alimentador/"+String.valueOf(alimentador.getIdalimentador());


            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    Toast.makeText(context, "Dados removidos com suscesso", Toast.LENGTH_LONG).show();
                    //remova esse alimentador do arraylist e atualize o recyclerView
                    int index = alimentadores.indexOf(alimentador);
                    alimentadores.remove(alimentador);
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

    private Alimentador getAlimentadorFromID(int idalimentador) {
        for(Alimentador alimentadorAux: alimentadores){
            if(alimentadorAux.getIdalimentador()==idalimentador){
                return alimentadorAux;
            }
        }
        return null;
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
    public int getItemCount() {
        return alimentadores.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView id, serial, nomePetAlimentador, nomeDietaAlimentador;
        private ImageView editAlimentador, deleteAlimentador;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.idAlimentador);
            serial = (TextView) itemView.findViewById(R.id.serialAlimentador);
            nomePetAlimentador = (TextView) itemView.findViewById(R.id.nomePetAlimentador);
            nomeDietaAlimentador = (TextView) itemView.findViewById(R.id.nomeDietaAlimentador);
            editAlimentador = (ImageView) itemView.findViewById(R.id.editAlimentador);
            deleteAlimentador = (ImageView) itemView.findViewById(R.id.deleteAlimentador);
        }
    }
}
