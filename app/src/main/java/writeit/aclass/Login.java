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
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import static android.view.View.GONE;

public class Login extends AppCompatActivity {

    EditText etEmail, etPass;
    Button btnLogin;
    LinearLayout notConLayout;
    SwipeRefreshLayout swipeRefresh;
    ProgressBar ProgressLog;

    ProgressDialog message;

    FirebaseAuth auth;

    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        ProgressLog = (ProgressBar) findViewById(R.id.ProgressLog);
        notConLayout = (LinearLayout) findViewById(R.id.notConLayout);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        message = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("users");

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


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getEmail = etEmail.getText().toString().trim();
                String getPassword = etPass.getText().toString().trim();

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

                message.setTitle("Mencoba Login");
                message.setMessage("Mohon tunggu sedang mengecek akun!");
                message.setCanceledOnTouchOutside(false);
                message.show();

                ProgressLog.setVisibility(View.VISIBLE);
                auth.signInWithEmailAndPassword(getEmail, getPassword)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                ProgressLog.setVisibility(GONE);
                                if (task.isSuccessful()) {
                                    message.dismiss();

                                    String Uid = auth.getCurrentUser().getUid();
                                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                    database.child(Uid).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(Login.this, Home.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                } else {
                                    message.hide();
                                    Toast.makeText(Login.this, "Terjadi Kesalahan, Coba Cek Ulang Email Dan Password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

//            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//       cek user apakah sudah pernah login sebelumnya
        if (auth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(Login.this, Home.class));
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
