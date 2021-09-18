package com.borntocode.covidshield;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.borntocode.covidshield.managers.SharedPreferencesManager;
import com.borntocode.covidshield.ui.AppSnackbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends BaseActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ChipNavigationBar bottomNav;
    private ActionBarDrawerToggle toggle;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Dialog dialog;

    private Button btn_logout, btn_updateProfile;
    private TextView txt_userName, txt_userEmail, txt_appVersion, txt_vaccineStatus, btn_vaccineStatus;
    private ImageView img_UserImg;
    private RadioButton rbn_00, rbn_01, rbn_02;

    private SharedPreferencesManager sharedPreferencesManager;
    private boolean doubleBackToExitPressedOnce = false;
    private Fragment bottomFragment = null;

    private FirebaseAuth mAuth;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        bottomNav = findViewById(R.id.bottom_navigation);
        btn_logout = findViewById(R.id.btn_logout);

        View headerView = navigationView.getHeaderView(0);
        txt_userName = headerView.findViewById(R.id.txt_drawerName);
        txt_userEmail = headerView.findViewById(R.id.txt_drawerEmail);
        txt_vaccineStatus = headerView.findViewById(R.id.txt_navVaccineStatus);
        txt_appVersion = headerView.findViewById(R.id.txt_appVersion);
        img_UserImg = headerView.findViewById(R.id.drawer_image);
        btn_vaccineStatus = headerView.findViewById(R.id.btn_navVaccineStatus);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferencesManager = new SharedPreferencesManager(this);

        init();

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Are you sure you want to logout?")
                        .setConfirmText("Sure!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @SuppressLint("ResourceType")
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                pDialog.getProgressHelper().setBarColor(Color.parseColor(getResources().getString(R.color.colorPrimary)));
                                pDialog.setTitleText("Loading");
                                pDialog.setCancelable(false);
                                pDialog.show();

                                mAuth.signOut();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                                pDialog.cancel();
                            }
                        })
                        .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });

        //Side Navigation Drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_vaccineDetails:
                        boolean is_finish_tutorial = sharedPreferencesManager.getBooleanPreferences(SharedPreferencesManager.IS_DONE_TUTORIAL);
                        if (!is_finish_tutorial) {
                            startActivity(new Intent(MainActivity.this, OnboardingActivity.class));
                        } else {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.presidentsoffice.gov.lk/index.php/vaccination-dashboard/")));
                        }
                        break;
                    case R.id.nav_moreDetails:
                        startActivity(new Intent(MainActivity.this, InformationsActivity.class));
                        break;
                    case R.id.nav_emergencyDial:
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "1999"));
                            startActivity(intent);
                        }
                        else{
                            AppSnackbar.SnackbarOK(headerView,MainActivity.this,"You haven't allow call phone permission. please allow the permission and try again!");
                        }
                        break;
                    case R.id.nav_aboutUs:
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

        //Bottom Navigation Bar
        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i) {
                    case R.id.nav_home:
                        bottomFragment = new HomeFragment();
                        loadBottomFragment(bottomFragment);
                        break;
                    case R.id.nav_gallery:
                        bottomFragment = new GalleryFragment();
                        loadBottomFragment(bottomFragment);
                        break;
                    case R.id.nav_slideshow:
                        bottomFragment = new SlideShowFragment();
                        loadBottomFragment(bottomFragment);
                        break;
                    default:
                        return;
                }
                return;
            }
        });

        btn_vaccineStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_vaccineDetails();
            }
        });
    }

    private void change_vaccineDetails() {
        drawerLayout.closeDrawer(GravityCompat.START);

        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.change_vaccine_details_layout);
        dialog.getWindow().setLayout(850, 850);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        rbn_00 = dialog.findViewById(R.id.rbn_00_dialog);
        rbn_01 = dialog.findViewById(R.id.rbn_01_dialog);
        rbn_02 = dialog.findViewById(R.id.rbn_02_dialog);
        btn_updateProfile = dialog.findViewById(R.id.btn_updateProfile_dialog);

        btn_updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rbn_00.isChecked()) {
                    updateProfile("type_00");
                } else if (rbn_01.isChecked()) {
                    updateProfile("type_01");
                } else if (rbn_02.isChecked()) {
                    updateProfile("type_02");
                } else {
                    updateProfile("type_00");
                }
            }
        });
    }

    private void updateProfile(String type) {
        HashMap vaccineDetail = new HashMap();
        vaccineDetail.put("vaccinationDetails", type);

        String user_Id = sharedPreferencesManager.getPreferences(SharedPreferencesManager.FIREBASE_USER_ID);
        mReference = FirebaseDatabase.getInstance("https://covid-shield-f940b-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(user_Id);
        mReference.updateChildren(vaccineDetail).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    update_vaccineUI(type);
                    drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    dialog.dismiss();
                    Log.e("TAG", "onComplete: Failed");
                }
            }
        });
    }


    private void loadBottomFragment(Fragment bottomFragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, bottomFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void init() {
        toolbar.setTitle("Covid Shield");
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bottomFragment = new HomeFragment();
        loadBottomFragment(bottomFragment);
        bottomNav.setItemSelected(R.id.nav_home, true);

        getFirebaseUser();
    }

    private void getFirebaseUser() {
        String user_Id = sharedPreferencesManager.getPreferences(SharedPreferencesManager.FIREBASE_USER_ID);
        if (user_Id != null) {
            mReference = FirebaseDatabase.getInstance("https://covid-shield-f940b-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(user_Id);
            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Picasso.get().load(snapshot.child("userImage").getValue(String.class)).into(img_UserImg);
                        String username = snapshot.child("firstName").getValue(String.class) + " " + snapshot.child("lastName").getValue(String.class);
                        String vaccineDetail = snapshot.child("vaccinationDetails").getValue(String.class);

                        update_vaccineUI(vaccineDetail);

                        txt_userName.setText(username);
                        txt_userEmail.setText(snapshot.child("emailAddress").getValue(String.class));
                        txt_appVersion.setText("V " + BuildConfig.VERSION_NAME);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Error Occurred!" + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void update_vaccineUI(String type) {
        if (type.equals("type_00")) {
            txt_vaccineStatus.setText(getResources().getString(R.string.not_vaccinated));
            txt_vaccineStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error, 0);
            btn_vaccineStatus.setEnabled(true);
            btn_vaccineStatus.setVisibility(View.VISIBLE);
        } else if (type.equals("type_01")) {
            txt_vaccineStatus.setText(getResources().getString(R.string.first_dose_only));
            txt_vaccineStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_exclamation, 0);
            btn_vaccineStatus.setEnabled(true);
            btn_vaccineStatus.setVisibility(View.VISIBLE);
        } else if (type.equals("type_02")) {
            txt_vaccineStatus.setText(getResources().getString(R.string.fully_vaccinated));
            txt_vaccineStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified, 0);
            btn_vaccineStatus.setEnabled(false);
            btn_vaccineStatus.setVisibility(View.INVISIBLE);
        } else {
            txt_vaccineStatus.setText(getResources().getString(R.string.not_vaccinated));
            txt_vaccineStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error, 0);
            btn_vaccineStatus.setEnabled(true);
            btn_vaccineStatus.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}