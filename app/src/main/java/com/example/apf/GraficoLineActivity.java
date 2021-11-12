package com.example.apf;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GraficoLineActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    LineChart mLineGraph;
    String idAlimentadorSelecionado;
    TextView txtDataInicio, txtDataFim;
    boolean dataInicioFim=true; //essa variavel é utilizada apenas para sabermos se estamos escolhendo um data de inicio ou uma data de fim para podermos setar a data no txtfield apropriado
    Button btnAtualizaGrafico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico_line);

        //transforma a cor em preto do text header
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">"+getString(R.string.app_name)+"</font>"));

        //força o layout a ficar na horizontal
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        //obtem o id do alimentador selecionado vindo da classe sensor activity
        Intent intent = getIntent();
        idAlimentadorSelecionado = intent.getStringExtra("idAlimentadorSelecionado"); //captura o id do alimentador selecionado, vem do AlimentadorAdapter

        mLineGraph=(LineChart) findViewById(R.id.line_chart);
        txtDataInicio = (TextView)findViewById(R.id.textDataInicio);
        txtDataFim = (TextView)findViewById(R.id.textDataFim);
        btnAtualizaGrafico = (Button)findViewById(R.id.btn_atualizargraficoLine);

        //capturando a data de hoje
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        txtDataInicio.setText(currentDate);
        txtDataFim.setText(currentDate);

        //quando a activity é aberta chamamos o getdata() para criar um grafico e por padrao usará a data do dia de hoje como data de inicio e fim
        getData();

        txtDataInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gerarDialogComCalendario(txtDataInicio);

            }
        });

        txtDataFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gerarDialogComCalendario(txtDataFim);
            }
        });

        btnAtualizaGrafico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });


    }

    //esse metodo irá gerar um dialog onde podemos selecionar uma data
    private void gerarDialogComCalendario(TextView textView){

        ////apenas verificando de devemos preencher o textfield de data de inicio ou data de fim
        if(textView==txtDataInicio){
            dataInicioFim=true;
        }else{
            dataInicioFim=false;
        }

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                GraficoLineActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                this,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


    }

    //esse metodo vai obter as leituras dos sensores de umidade e temperatura da api no servidor
    private void getData() {

        List<String> xAxisDatas = new ArrayList<>();
        ArrayList<Entry> temperaturas = new ArrayList<>();
        ArrayList<Entry> umidades = new ArrayList<>();

        String url = "http://177.44.248.45:3300/sensor/temperaturaumidade";

        //criar o Json array (pois a classe JsonArrayRequest só aceita um jsonarray como parametros a serem enviados no metodo post)
        JSONArray array = new JSONArray();
        //criando entao o json com os dados de Id do alimentador que selecionamos e as datas de inicio e fim que queremos dos sensores
        JSONObject jsonParam = new JSONObject();
        try {
            //Add string params
            jsonParam.put("idalimentador", idAlimentadorSelecionado);
            jsonParam.put("dataInicio", txtDataInicio.getText().toString());
            jsonParam.put("dataFim", txtDataFim.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //adicionando o json recem criado ao jsonarray
        array.put(jsonParam);



        JsonArrayRequest arrayRequest  = new JsonArrayRequest(Request.Method.POST, url, array, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);

                        xAxisDatas.add(jsonObject.getString("data"));
                        temperaturas.add(new Entry(i, (float) jsonObject.getDouble("temperatura")));
                        umidades.add(new Entry(i, (float) jsonObject.getDouble("umidade")));

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

                //assim que terminarmos de receber todos os jsons da api do servidor começamos a montar o grafico

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                List<String> xAxisValues = xAxisDatas;
                List<Entry> temperatureEntries = temperaturas;
                List<Entry> umidadeEntries = umidades;
                dataSets = new ArrayList<>();
                LineDataSet set1, set2;

                set1 = new LineDataSet(temperatureEntries, "Temperatura");
                set1.setColor(Color.rgb(255, 0, 0));
                set1.setValueTextColor(Color.rgb(55, 70, 73));
                set1.setValueTextSize(10f);
                set1.setMode(LineDataSet.Mode.LINEAR);

                set2 = new LineDataSet(umidadeEntries, "Umidade");
                set2.setColor(Color.rgb(0, 0, 255));
                set2.setValueTextColor(Color.rgb(55, 70, 73));
                set2.setValueTextSize(10f);
                set2.setMode(LineDataSet.Mode.LINEAR);


                dataSets.add(set1);
                dataSets.add(set2);

                //customization
                mLineGraph.setTouchEnabled(true);
                mLineGraph.setDragEnabled(true);
                mLineGraph.setScaleEnabled(true);
                mLineGraph.setPinchZoom(true);
                mLineGraph.setDrawGridBackground(false);
                mLineGraph.setExtraLeftOffset(8);
                mLineGraph.setExtraRightOffset(30);
                //to hide background lines
                mLineGraph.getXAxis().setDrawGridLines(false);
                mLineGraph.getAxisLeft().setDrawGridLines(false);
                mLineGraph.getAxisRight().setDrawGridLines(false);

                //to hide right Y and top X border
                YAxis rightYAxis = mLineGraph.getAxisRight();
                rightYAxis.setEnabled(false);
                YAxis leftYAxis = mLineGraph.getAxisLeft();
                leftYAxis.setEnabled(true);
                XAxis topXAxis = mLineGraph.getXAxis();
                topXAxis.setEnabled(false);


                XAxis xAxis = mLineGraph.getXAxis();
                xAxis.setGranularity(1f);
                //xAxis.setCenterAxisLabels(true);
                xAxis.setEnabled(true);
                xAxis.setDrawGridLines(false);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


                mLineGraph.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

                LineData data = new LineData(dataSets);
                mLineGraph.setData(data);
                //mLineGraph.animateX(2000);
                mLineGraph.invalidate();
                mLineGraph.getLegend().setEnabled(true);
                mLineGraph.getDescription().setText("Gráfico de Temperatura e Umidade");
                mLineGraph.getDescription().setEnabled(true);
                //mLineGraph.setVisibleXRange(2,2);
                //mLineGraph.setMaxVisibleValueCount(2);
                //mLineGraph.saveToGallery("umidadeEtemperatura2.png", 100);
                //mLineGraph.zoom(50,1,1,1);

                //mLineGraph.getChartBitmap().compress()

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Falha em obter as leituras dos sensores no periodo selecionado", Toast.LENGTH_LONG).show();
            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(GraficoLineActivity.this);
        requestQueue.add(arrayRequest);



    }

    //esse metodo é responsavel por setar a data escolhida no textview apropriado
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        month = month + 1;


        String date = day + "/" + month + "/" + year;
        if(dataInicioFim==true) {
            txtDataInicio.setText(date);
        }else{
            txtDataFim.setText(date);
        }
    }
}

