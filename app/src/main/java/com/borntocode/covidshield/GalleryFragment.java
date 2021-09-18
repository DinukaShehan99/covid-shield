package com.borntocode.covidshield;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GalleryFragment extends Fragment {

    private SupportMapFragment supportMapFragment;
    private LocationManager locationManager;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private Location location;

    private TextInputEditText txt_countryName;
    private TextView countryName_Text, txt_totCases, txt_totDeaths, txt_totRecovered, txt_newCases, txt_newDeaths, txt_newRecovered, txt_activeCases, txt_updateTime;
    private ConstraintLayout country;
    private ImageView flag;

    private Dialog dialog;
    private ListView countryList;
    private ArrayList<String> countryNames;
    private ArrayAdapter<String> countryAdapter;

    private DecimalFormat decimalFormat;
    private SimpleDateFormat dateFormat;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        bottomSheetView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.bottomsheet_map, null);

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        country = v.findViewById(R.id.txt_countryNameGlobal);
        countryName_Text = v.findViewById(R.id.txt_countryNameG);
        flag = v.findViewById(R.id.circleImageViewGlobal);
        txt_totCases = bottomSheetView.findViewById(R.id.txt_totCasesGlobal);
        txt_totDeaths = bottomSheetView.findViewById(R.id.txt_totDeathsGlobal);
        txt_totRecovered = bottomSheetView.findViewById(R.id.txt_totRecoveredGlobal);
        txt_newCases = bottomSheetView.findViewById(R.id.txt_newCasesGlobal);
        txt_newDeaths = bottomSheetView.findViewById(R.id.txt_newDeathsGlobal);
        txt_newRecovered = bottomSheetView.findViewById(R.id.txt_newRecoveredGlobal);
        txt_activeCases = bottomSheetView.findViewById(R.id.txt_activeCasesGlobal);
        txt_updateTime = bottomSheetView.findViewById(R.id.txt_lastUpdateGlobal);

        decimalFormat = new DecimalFormat("#,###,###");
        dateFormat = new SimpleDateFormat("MMM dd, HH:mm a");

        init();

        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCountryListDialog();
            }
        });

        return v;
    }

    private void getCountryListDialog() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_searchable_spinner);
        dialog.getWindow().setLayout(850, 1200);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        txt_countryName = dialog.findViewById(R.id.txt_selectDialog);
        countryList = dialog.findViewById(R.id.ListView_countryGlobal);

        countryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, countryNames);
        countryList.setAdapter(countryAdapter);

        txt_countryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                countryAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        countryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getCountryDetails(i, 1);
                dialog.dismiss();
            }
        });
    }

    private void initBottomSheet(String totCases, String totDeaths, String totRecovered, String activeCases, String newCases, String newDeaths, String newRecovered, String updateTime) {
        txt_totCases.setText(totCases);
        txt_totDeaths.setText(totDeaths);
        txt_totRecovered.setText(totRecovered);
        txt_activeCases.setText(activeCases);
        txt_newCases.setText(newCases);
        txt_newDeaths.setText(newDeaths);
        txt_newRecovered.setText(newRecovered);
        txt_updateTime.setText(updateTime);

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void init() {
        getCountries();
        getCountryDetails(0, 0);
    }

    @SuppressLint("ResourceType")
    private void getCountries() {
        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getResources().getString(R.color.colorPrimary)));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue Queue = Volley.newRequestQueue(getContext());
        String url = "https://disease.sh/v3/covid-19/countries";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray ResponseJSON = new JSONArray(response);

                    countryNames = new ArrayList<>();
                    for (int i = 0; i < ResponseJSON.length(); i++) {
                        countryNames.add(ResponseJSON.getJSONObject(i).getString("country"));
                    }
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
                } catch (Exception ex) {
                    pDialog.cancel();
                    Log.d("onErrorResponse", ex.getLocalizedMessage());
                }
            }
        });
        Queue.add(request);
    }

    private void getCountryDetails(int index, int callFrom) {
        if (callFrom == 0) {
            RequestQueue Queue = Volley.newRequestQueue(getContext());
            String url = "https://disease.sh/v3/covid-19/countries";
            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray ResponseJSON = new JSONArray(response);
                        JSONObject ResponseObject = ResponseJSON.getJSONObject(index);

                        countryName_Text.setText(ResponseObject.getString("country"));
                        Picasso.get().load(ResponseObject.getJSONObject("countryInfo").getString("flag")).into(flag);

                        String totCases, totDeaths, totRecovered, activeCases, newCases, newDeaths, newRecovered, updateTime;

                        totCases = (decimalFormat.format(ResponseObject.getInt("cases")));
                        totDeaths = (decimalFormat.format(ResponseObject.getInt("deaths")));
                        totRecovered = (decimalFormat.format(ResponseObject.getInt("recovered")));
                        activeCases = (decimalFormat.format(ResponseObject.getInt("active")));
                        newCases = ("+" + decimalFormat.format(ResponseObject.getInt("todayCases")));
                        newDeaths = ("+" + decimalFormat.format(ResponseObject.getInt("todayDeaths")));
                        newRecovered = ("+" + decimalFormat.format(ResponseObject.getInt("todayRecovered")));
                        updateTime = (dateFormat.format(new Date()));

                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                LatLng latLng = null;
                                try {
                                    latLng = new LatLng(ResponseObject.getJSONObject("countryInfo").getInt("lat"), ResponseObject.getJSONObject("countryInfo").getInt("long"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(latLng)
                                        .zoom(4)
                                        .build();
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                googleMap.clear();

                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                googleMap.addMarker(markerOptions);

                                if (location != null) {
                                    googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your Location")
                                            .icon(bitmapDescriptor(getActivity(), R.drawable.ic_flag)));
                                }

                                initBottomSheet(totCases, totDeaths, totRecovered, activeCases, newCases, newDeaths, newRecovered, updateTime);

                                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                    @Override
                                    public void onMapClick(@NonNull LatLng latLng) {
                                        bottomSheetDialog.show();
                                    }
                                });
                            }
                        });
                    } catch (Exception ex) {
                        Log.d("onResponse Error", ex.getLocalizedMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        Log.d("onErrorResponse", error.getLocalizedMessage());
                    } catch (Exception ex) {
                        Log.d("onErrorResponse", ex.getLocalizedMessage());
                    }
                }
            });
            Queue.add(request);
        } else {
            int i = countryNames.indexOf(countryAdapter.getItem(index));
            RequestQueue Queue = Volley.newRequestQueue(getContext());
            String url = "https://disease.sh/v3/covid-19/countries";
            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray ResponseJSON = new JSONArray(response);
                        JSONObject ResponseObject = ResponseJSON.getJSONObject(i);

                        countryName_Text.setText(ResponseObject.getString("country"));
                        Picasso.get().load(ResponseObject.getJSONObject("countryInfo").getString("flag")).into(flag);

                        String totCases, totDeaths, totRecovered, activeCases, newCases, newDeaths, newRecovered, updateTime;

                        totCases = (decimalFormat.format(ResponseObject.getInt("cases")));
                        totDeaths = (decimalFormat.format(ResponseObject.getInt("deaths")));
                        totRecovered = (decimalFormat.format(ResponseObject.getInt("recovered")));
                        activeCases = (decimalFormat.format(ResponseObject.getInt("active")));
                        newCases = ("+" + decimalFormat.format(ResponseObject.getInt("todayCases")));
                        newDeaths = ("+" + decimalFormat.format(ResponseObject.getInt("todayDeaths")));
                        newRecovered = ("+" + decimalFormat.format(ResponseObject.getInt("todayRecovered")));
                        updateTime = (dateFormat.format(new Date()));

                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                LatLng latLng = null;
                                try {
                                    latLng = new LatLng(ResponseObject.getJSONObject("countryInfo").getInt("lat"), ResponseObject.getJSONObject("countryInfo").getInt("long"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(latLng)
                                        .zoom(4)
                                        .build();
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                googleMap.clear();

                                googleMap.animateCamera(cameraUpdate);
                                googleMap.addMarker(markerOptions);

                                if (location != null) {
                                    googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your Location")
                                            .icon(bitmapDescriptor(getActivity(), R.drawable.ic_flag)));
                                }

                                initBottomSheet(totCases, totDeaths, totRecovered, activeCases, newCases, newDeaths, newRecovered, updateTime);

                                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                    @Override
                                    public void onMapClick(@NonNull LatLng latLng) {
                                        bottomSheetDialog.show();
                                    }
                                });
                            }
                        });
                    } catch (Exception ex) {
                        Log.d("onResponse Error", ex.getLocalizedMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        Log.d("onErrorResponse", error.getLocalizedMessage());
                    } catch (Exception ex) {
                        Log.d("onErrorResponse", ex.getLocalizedMessage());
                    }
                }
            });
            Queue.add(request);
        }
    }

    private BitmapDescriptor bitmapDescriptor(Context context, int vectorResId) {
        Drawable vecDrawable = ContextCompat.getDrawable(context, vectorResId);
        vecDrawable.setBounds(0, 0, vecDrawable.getIntrinsicWidth(), vecDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vecDrawable.getIntrinsicWidth(), vecDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vecDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onPause() {
        super.onPause();
        bottomSheetDialog.dismiss();
    }
}