package com.example.apf;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.RectF;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GraficoBarActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, OnChartValueSelectedListener {

    BarChart mBarGraph;
    String idAlimentadorSelecionado;
    TextView txtDataInicio, txtDataFim, txtTotalLiberado;
    boolean dataInicioFim=true; //essa variavel é utilizada apenas para sabermos se estamos escolhendo um data de inicio ou uma data de fim para podermos setar a data no txtfield apropriado
    Button btnAtualizaGrafico;

    ArrayList<BarEntry> entries = new ArrayList<>(); //aqui vai as entradas com as quantidades de ração liberada
    ArrayList<String> datas = new ArrayList<>(); //aqui vai as datas e horarios em que ocorreu uma liberação de ração
    ArrayList<String> infoComplementares = new ArrayList<>(); //aqui vai informações complementares sobre a liberação como a quantidade que foi solicitada, e o status dessa liberação, se deu certo ou se deu erro e qual o motivo do erro
    double quantidadeTotal = 0.0; //armazena o somatorio da quantidade total liberada no periodo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico_bar);

        //transforma a cor em preto do text header
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">"+getString(R.string.app_name)+"</font>"));

        //obtem o id do alimentador selecionado vindo da classe sensor activity
        Intent intent = getIntent();
        idAlimentadorSelecionado = intent.getStringExtra("idAlimentadorSelecionado"); //captura o id do alimentador selecionado, vem do AlimentadorAdapter

        mBarGraph=(BarChart) findViewById(R.id.barchart);
        txtDataInicio = (TextView)findViewById(R.id.textDataInicio);
        txtDataFim = (TextView)findViewById(R.id.textDataFim);
        btnAtualizaGrafico = (Button)findViewById(R.id.btn_atualizargraficobar);
        txtTotalLiberado = (TextView)findViewById(R.id.textTotalLiberado);

        //capturando a data de hora
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        txtDataInicio.setText(currentDate);
        txtDataFim.setText(currentDate);

        //necessario para que ao clicarmos sobre uma barra do grafico possamos visualizar informações complementares sobre aquela barra
        mBarGraph.setOnChartValueSelectedListener(this);

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

        //apenas verificando de devemos preencher o textfield de data de inicio ou data de fim
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
                GraficoBarActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                this,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


    }


    //esse metodo vai obter as leituras das ultimas liberações da api no servidor
    private void getData() {

        //limpando os arrays caso ja tenhos informaçoes de uma consulta anterior
        entries = new ArrayList<>();
        datas = new ArrayList<>();
        infoComplementares = new ArrayList<>();
        quantidadeTotal = 0.0;



        //titulo do grafico
        String title = "Gramas Liberadas";


        //criar o Json array (pois a classe JsonArrayRequest só aceita um jsonarray como parametros a serem enviados no metodo post)
        JSONArray array = new JSONArray();
        //criando entao o json com os dados de Id do alimentador que selecionamos e as datas de inicio e fim que queremos das liberações
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

        String url = "http://177.44.248.45:3300/sensor/liberacoes";
        JsonArrayRequest arrayRequest  = new JsonArrayRequest(Request.Method.POST, url, array, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);

                        datas.add(jsonObject.getString("data"));
                        //verifica se é menor que 0 pois a balaça, quando nao recebe nenhuma ração para gerar peso, pode apresentar leituras com valores negativos muito proximos de 0. Então para nao aparecer um numero negativo de gramas por exemplo -0.04 no grafico, forçamos esta Entry a ter o valor de 0
                        if((float) jsonObject.getDouble("qtdeliberada")<0.0){
                            entries.add(new BarEntry(i, 0.0F));
                        }else{
                            entries.add(new BarEntry(i, (float) jsonObject.getDouble("qtdeliberada")));
                        }
                        infoComplementares.add("Data da Liberação: "+jsonObject.getString("data")+"\nQuantidade Solicitada: "+jsonObject.getDouble("qtdesolicitada")+"\nQuantidade Liberada: "+entries.get(i).getY()+"\nStatus: "+jsonObject.getString("status"));

                        quantidadeTotal = quantidadeTotal + entries.get(i).getY();

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }


                //assim que terminarmos de receber todos os jsons da api do servidor com as informaçoes de liberações,  começamos a montar o grafico.
                txtTotalLiberado.setText(new DecimalFormat("#.###").format(quantidadeTotal/1000.0)+"Kg");

                mBarGraph.getXAxis().setValueFormatter(new IndexAxisValueFormatter(datas));


                mBarGraph.setTouchEnabled(true);
                mBarGraph.setDragEnabled(true);
                mBarGraph.setScaleEnabled(true);
                mBarGraph.setPinchZoom(true);
                mBarGraph.setDrawGridBackground(false);
                mBarGraph.getXAxis().setGranularity(1.0F);
                mBarGraph.getXAxis().setGranularityEnabled(true);
                mBarGraph.setExtraTopOffset(5);

                //força o eixo y a começar em 0
                mBarGraph.getAxisRight().setStartAtZero(true);
                mBarGraph.getAxisLeft().setStartAtZero(true);

                //to hide background lines
                mBarGraph.getXAxis().setDrawGridLines(false);
                mBarGraph.getAxisLeft().setDrawGridLines(false);
                mBarGraph.getAxisRight().setDrawGridLines(false);
                BarDataSet barDataSet = new BarDataSet(entries, title);

                BarData data = new BarData(barDataSet);
                //mBarGraph.getXAxis().setCenterAxisLabels(false);
                //mBarGraph.getDescription().setTextSize(13f);
                //mBarGraph.getDescription().setText("Total Liberado no Período: "+new DecimalFormat("#.##").format(quantidadeTotal/1000.0)+"Kg");
                mBarGraph.getDescription().setText("");
                mBarGraph.setData(data);
                mBarGraph.invalidate();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Falha em obter as leituras das liberações no periodo selecionado", Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(GraficoBarActivity.this);
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

    //esse metodo é responsavel por exeibir as informações complementares quando clicamos sobre uma barrinha do grafico
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;

        //capturando as informações complementares que estão no array infoComplementares. Obtemos elas apartir da Entry que clicamos em cima, obtemos o valor de X dessa entry e usamos esse valor como indice para recuperar as informações complementares no array de infocomplementares
        Toast.makeText(getApplicationContext(), infoComplementares.get((int)e.getX()), Toast.LENGTH_LONG).show();


    }

    @Override
    public void onNothingSelected() {

    }
}