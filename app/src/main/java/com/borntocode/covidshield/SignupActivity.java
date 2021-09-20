package com.borntocode.covidshield;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.borntocode.covidshield.dto.User;
import com.borntocode.covidshield.managers.SharedPreferencesManager;
import com.borntocode.covidshield.ui.AppSnackbar;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignupActivity extends BaseActivity {
    private FirebaseAuth mAuth;
    private SharedPreferencesManager sharedPreferencesManager;

    private TextInputLayout txt_firstName, txt_lastName, txt_emailAddress, txt_password, txt_CPassword;
    private RadioButton rbn_0, rbn_1, rbn_2;
    private Button btn_signup;
    private ConstraintLayout link_signin;
    private ImageView profilePicture;

    private String[] PERMISSIONS_CAMERA;
    private String[] PERMISSIONS_CALL_LOCATION;
    private String downloadUrl = "";
    private Uri imageUri;
    private String userID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txt_firstName = (TextInputLayout) findViewById(R.id.txt_signupFName);
        txt_lastName = (TextInputLayout) findViewById(R.id.txt_signupLName);
        txt_emailAddress = (TextInputLayout) findViewById(R.id.txt_signupEmail);
        txt_password = (TextInputLayout) findViewById(R.id.txt_signupPassword);
        txt_CPassword = (TextInputLayout) findViewById(R.id.txt_signupCPassword);
        btn_signup = (Button) findViewById(R.id.btn_updateProfile_dialog);
        rbn_0 = (RadioButton) findViewById(R.id.rbn_00_dialog);
        rbn_1 = (RadioButton) findViewById(R.id.rbn_01_dialog);
        rbn_2 = (RadioButton) findViewById(R.id.rbn_02_dialog);
        profilePicture = (ImageView) findViewById(R.id.signup_userImg);
        link_signin = (ConstraintLayout) findViewById(R.id.signup_link_signin);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferencesManager = new SharedPreferencesManager(this);

        PERMISSIONS_CAMERA = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        PERMISSIONS_CALL_LOCATION = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CALL_PHONE
        };


        link_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp_newUser(view);
            }
        });

        init();
    }

    private void init() {
        rbn_0.setChecked(true);
        textChangedListener(txt_firstName, txt_firstName.getEditText());
        textChangedListener(txt_lastName, txt_lastName.getEditText());
        textChangedListener(txt_emailAddress, txt_emailAddress.getEditText());
        textChangedListener(txt_password, txt_password.getEditText());
        textChangedListener(txt_CPassword, txt_CPassword.getEditText());
    }

    @SuppressLint("ResourceType")
    private void signUp_newUser(View view) {
        String fName = txt_firstName.getEditText().getText().toString();
        String lName = txt_lastName.getEditText().getText().toString();
        String email = txt_emailAddress.getEditText().getText().toString();
        String pwd = txt_password.getEditText().getText().toString();
        String cPwd = txt_CPassword.getEditText().getText().toString();
        String vaccinationDetails = "";
        if (rbn_0.isChecked()) {
            vaccinationDetails = "type_00";
        } else if (rbn_1.isChecked()) {
            vaccinationDetails = "type_01";
        } else if (rbn_2.isChecked()) {
            vaccinationDetails = "type_02";
        } else {
            vaccinationDetails = "type_00";
        }

        if (!isNetworkConnected()) {
            AppSnackbar.SnackbarINFO(view, this, "NO INTERNET CONNECTION");
        } else if (profilePicture.getDrawable() == null || profilePicture.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.ic_user_profile).getConstantState()) {
            AppSnackbar.SnackbarOK(view, this, "Please select a profile picture");
        } else if (fName.length() == 0 || !fName.matches("[a-zA-Z ]+")) {
            txt_firstName.requestFocus();
            txt_firstName.setError("Invalid First Name");
        } else if (lName.length() == 0 || !lName.matches("[a-zA-Z ]+")) {
            txt_lastName.requestFocus();
            txt_lastName.setError("Invalid First Name");
        } else if (email.length() == 0) {
            txt_emailAddress.requestFocus();
            txt_emailAddress.setError("Email cannot be empty");
        } else if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            txt_emailAddress.requestFocus();
            txt_emailAddress.setError("Please enter valid Email");
        } else if (pwd.length() <= 5) {
            txt_password.requestFocus();
            txt_password.setError("Please enter valid Password");
        } else if (!pwd.equals(cPwd)) {
            txt_CPassword.requestFocus();
            txt_CPassword.setError("The password confirmation does not match");
            Log.e("Log: ", pwd + " " + cPwd);
        } else {
            SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor(getResources().getString(R.color.colorPrimary)));
            pDialog.setTitleText("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();

            String final_VaccinationDetails = vaccinationDetails;
            mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                    if (task.getResult().getSignInMethods().isEmpty()) {
                        mAuth.createUserWithEmailAndPassword(email, pwd)
                                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("TAG", "createUserWithEmail:success");
                                            final String randomKey = UUID.randomUUID().toString();
                                            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("User_Images/" + randomKey);

                                            Task<Uri> urlTask = filePath.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                @Override
                                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                    if (!task.isSuccessful()) {
                                                        throw task.getException();
                                                    }
                                                    return filePath.getDownloadUrl();
                                                }
                                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if (task.isSuccessful()) {
                                                        Uri downloadUri = task.getResult();
                                                        downloadUrl = downloadUri.toString();
                                                        User dtoUser = new User(fName, lName, email, pwd, final_VaccinationDetails, downloadUrl);
                                                        FirebaseDatabase.getInstance("https://covid-shield-f940b-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .setValue(dtoUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    pDialog.cancel();
                                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                                    userID = user.getUid();
                                                                    loadMainActivity(user);
                                                                } else {
                                                                    pDialog.cancel();
                                                                    Toast.makeText(SignupActivity.this, "Failed to save data.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        pDialog.cancel();
                                                        Toast.makeText(SignupActivity.this, "Failed to save Image.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            pDialog.cancel();
                                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(SignupActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else {
                        pDialog.cancel();
                        AppSnackbar.SnackbarOK(view,SignupActivity.this,"This Email Address has already registered. please check your Email Address!");
                    }
                }
            });
        }
    }

    private void loadMainActivity(FirebaseUser user) {
        if (ActivityCompat.checkSelfPermission(SignupActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(SignupActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(SignupActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            new SweetAlertDialog(SignupActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setContentText("This app requires permissions for Access Location and Call Phone.\nPlease grant access for following requests.")
                    .setConfirmText("Sure!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(PERMISSIONS_CALL_LOCATION, 88);
                            }
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            Toast.makeText(SignupActivity.this, "Permission Denied ", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        } else {
            sharedPreferencesManager.savePreferences(SharedPreferencesManager.FIREBASE_USER_ID, user.getUid());
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void setProfile_Clicked(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            new SweetAlertDialog(SignupActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setContentText("This app requires permissions for Camera and File Manager.\nPlease grant access for following requests.")
                    .setConfirmText("Sure!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(PERMISSIONS_CAMERA, 80);
                            }
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            Toast.makeText(SignupActivity.this, "Permission Denied ", Toast.LENGTH_SHORT).show();
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        } else {
            ImagePicker.with(SignupActivity.this)
                    .cropSquare()                    //Crop image(Optional), Check Customization for more option
                    .compress(1024)            //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 80:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.with(SignupActivity.this)
                            .cropSquare()                    //Crop image(Optional), Check Customization for more option
                            .compress(1024)            //Final image size will be less than 1 MB(Optional)
                            .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                            .start();
                } else {
                    Toast.makeText(this, "Permission Denied ", Toast.LENGTH_SHORT).show();
                }
                break;
            case 88:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sharedPreferencesManager.savePreferences(SharedPreferencesManager.FIREBASE_USER_ID, userID);
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Permission Denied ", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUri = data.getData();
        profilePicture.setImageURI(imageUri);
    }
}
