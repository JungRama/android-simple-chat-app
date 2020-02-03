package writeit.aclass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import static android.view.View.GONE;

public class Register extends AppCompatActivity {

    EditText etEmail, etPass, etUsername;
    Button btnRegister;
    LinearLayout notConLayout;
    SwipeRefreshLayout swipeRefresh;
    ProgressBar ProgressReg;

    ProgressDialog message;

    FirebaseAuth auth;

    DatabaseReference database, checkUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPass);
        etUsername = (EditText) findViewById(R.id.etUsername);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        notConLayout = (LinearLayout) findViewById(R.id.notConLayout);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        ProgressReg = (ProgressBar) findViewById(R.id.ProgressReg);

        auth = FirebaseAuth.getInstance();

        message = new ProgressDialog(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//       Cek Koneksi Internet on create

        if (!isNetworkAvailable()) {
            notConLayout.setVisibility(View.VISIBLE);
        } else {
            notConLayout.setVisibility(GONE);
        }

//       On Refresh
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(true);
                //       Cek Koneksi Internet

                if (!isNetworkAvailable()) {
                    notConLayout.setVisibility(View.VISIBLE);
                } else {
                    notConLayout.setVisibility(GONE);
                }

                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                    }
                }, 3000);
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getEmail = etEmail.getText().toString().trim();
                final String getUsername = etUsername.getText().toString().trim();
                final String getPassword = etPass.getText().toString().trim();

                if (getUsername.isEmpty()) {
                    etUsername.setError("Username Harus Diisi");
                    etUsername.requestFocus();
                    return;
                }

                if (getEmail.isEmpty()) {
                    etEmail.setError("Email Harus Diisi");
                    etEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {
                    etEmail.setError("Isi Email Dengan Benar");
                    etEmail.requestFocus();
                    return;
                }

                if (getPassword.isEmpty()) {
                    etPass.requestFocus();
                    etPass.setError("Mohon Isi Password");
                    return;
                }

                if (getPassword.length() < 6) {
                    etPass.requestFocus();
                    etPass.setError("Minimal Password 6 Karakter");
                    return;
                }

                message.setTitle("Membuat Akun");
                message.setMessage("Mohon tunggu akun anda akan segera dibuat!");
                message.setCanceledOnTouchOutside(false);
                message.show();

                ProgressReg.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(getEmail, getPassword)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                ProgressReg.setVisibility(GONE);
                                if (task.isSuccessful()) {

                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    String Uid = currentUser.getUid();
                                        database = FirebaseDatabase.getInstance().getReference().child("users").child(Uid);
                                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

//                                  Upload Default Data Ke Firebase
                                    HashMap<String, String> userProfil = new HashMap<>();
                                    userProfil.put("device_token", deviceToken);
                                    userProfil.put("username", getUsername);
                                    userProfil.put("display_name", getUsername);
                                    userProfil.put("status", "Not Set");
                                    userProfil.put("image", "default_profil");
                                    userProfil.put("thumb_image", "default_profil");
                                    userProfil.put("bg_image", "default_profil");
                                    userProfil.put("birthday", "00-00-0000");

                                    database.setValue(userProfil).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            message.dismiss();
                                            finish();
                                            startActivity(new Intent(Register.this, Home.class));
                                        }
                                    });

                                } else {
                                    message.hide();
//                                  Cek Pengguna User
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        etEmail.setError("Email Telah Digunakan");
                                        etEmail.requestFocus();
                                    } else {
                                        Toast.makeText(Register.this, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        });
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
