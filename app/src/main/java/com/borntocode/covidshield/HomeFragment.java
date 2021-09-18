package com.borntocode.covidshield;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.borntocode.covidshield.ui.SliderAdapter;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeFragment extends Fragment {

    private PieChart pieChart;
    private BarChart barChart;
    private ArrayList<PieEntry> patients;
    private ArrayList<BarEntry> pcrTesting;
    private ArrayList<BarEntry> antigenTesting;
    private ArrayList<String> barChartDates;
    private SliderView imageSlider;
    private TextView totCases, totDeaths, totRecovered, newCases, newDeaths, activeCases, updateTime;

    private List<Integer> sliderImages;
    private SliderAdapter sliderAdapter;

    private DecimalFormat decimalFormat;
    private SimpleDateFormat dateFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        imageSlider = v.findViewById(R.id.imageSlider);
        pieChart = v.findViewById(R.id.pieChart);
        barChart = v.findViewById(R.id.barChart);
        totCases = v.findViewById(R.id.txt_totCases);
        totDeaths = v.findViewById(R.id.txt_totDeaths);
        totRecovered = v.findViewById(R.id.txt_totRecovered);
        newCases = v.findViewById(R.id.txt_newCases);
        newDeaths = v.findViewById(R.id.txt_newDeaths);
        activeCases = v.findViewById(R.id.txt_activeCases);
        updateTime = v.findViewById(R.id.txt_lastUpdate);


        sliderImages = new ArrayList<>();
        sliderImages.add(R.drawable.slider_img02);
        sliderImages.add(R.drawable.slider_img01);
        sliderImages.add(R.drawable.slider_img03);

        sliderAdapter = new SliderAdapter(sliderImages);
        imageSlider.setSliderAdapter(sliderAdapter);
        imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        imageSlider.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
        imageSlider.startAutoCycle();

        decimalFormat = new DecimalFormat("#,###,###");
        dateFormat = new SimpleDateFormat("MMM dd, HH:mm a");

        init();

        return v;
    }

    @SuppressLint("ResourceType")
    private void init() {
        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getResources().getString(R.color.colorPrimary)));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue Queue = Volley.newRequestQueue(getContext());
        String url = "https://www.hpb.health.gov.lk/api/get-current-statistical";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject ResponseJSON = new JSONObject(response);
                    JSONObject DataJSON = ResponseJSON.getJSONObject("data");
                    JSONArray PcrData = DataJSON.getJSONArray("daily_pcr_testing_data");
                    JSONArray AntigenData = DataJSON.getJSONArray("daily_antigen_testing_data");

                    //set data to card view
                    totCases.setText(decimalFormat.format(DataJSON.getInt("local_total_cases")));
                    totDeaths.setText(decimalFormat.format(DataJSON.getInt("local_deaths")));
                    totRecovered.setText(decimalFormat.format(DataJSON.getInt("local_recovered")));
                    newCases.setText("+" + decimalFormat.format(DataJSON.getInt("local_new_cases")));
                    newDeaths.setText("+" + decimalFormat.format(DataJSON.getInt("local_new_deaths")));
                    activeCases.setText(decimalFormat.format(DataJSON.getInt("local_active_cases")));
                    updateTime.setText(dateFormat.format(new Date()));

                    //pie chart data
                    patients = new ArrayList<>();
                    patients.add(new PieEntry(Float.parseFloat(DataJSON.getString("local_recovered")), "Recovered Patients"));
                    patients.add(new PieEntry(Float.parseFloat(DataJSON.getString("local_active_cases")), "Active Cases"));
                    patients.add(new PieEntry(Float.parseFloat(DataJSON.getString("local_deaths")), "Deaths"));

                    setPieChartValues();

                    //line chart data
                    pcrTesting = new ArrayList<>();
                    antigenTesting = new ArrayList<>();
                    barChartDates = new ArrayList<>();
                    for (int i = 0; i < PcrData.length(); i++) {
                        pcrTesting.add(new BarEntry(i,Float.parseFloat(PcrData.getJSONObject(i).getString("pcr_count"))));
                        barChartDates.add(PcrData.getJSONObject(i).getString("date"));
                    }
                    for (int j = 0; j < AntigenData.length(); j++) {
                        antigenTesting.add(new BarEntry(j,Float.parseFloat(AntigenData.getJSONObject(j).getString("antigen_count"))));
                    }
                    setBarChartValues();

                    pDialog.cancel();
                } catch (Exception ex) {
                    pDialog.cancel();
                    Log.d("onResponse Error", ex.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    pDialog.cancel();
                    Log.d("onErrorResponse", error.getLocalizedMessage());
                }catch (Exception ex)
                {
                    pDialog.cancel();
                    Log.d("onErrorResponse", ex.getLocalizedMessage());
                }
            }
        });
        Queue.add(request);
    }

    private void setPieChartValues() {
        //Pie Chart set values
        PieDataSet pieDataSet = new PieDataSet(patients, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setSliceSpace(2f);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(14f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.invalidate();
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Total Cases");
        pieChart.setHoleRadius(48f);
        pieChart.setTransparentCircleRadius(52f);
        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }

    private void setBarChartValues() {
        //Bar Chart set values
        BarDataSet barDataSet01 = new BarDataSet(pcrTesting,"PCR Testings");
        BarDataSet barDataSet02 = new BarDataSet(antigenTesting,"Antigen Testings");
        barDataSet01.setColors(Color.RED);
        barDataSet02.setColors(Color.BLUE);

        BarData barData = new BarData(barDataSet01,barDataSet02);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(barChartDates));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
        barChart.getAxisRight().setEnabled(false);

        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(5);

        float barSpace = 0f;
        float groupSpace = 0f;
        barData.setBarWidth(0.5f);

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);

        barChart.groupBars(0,groupSpace,barSpace);
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.animateY(1500);
        barChart.invalidate();
    }
}