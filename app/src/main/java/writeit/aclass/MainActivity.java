package writeit.aclass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button btn_register, btn_login;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_register = (Button)findViewById(R.id.btn_register);
        btn_login    = (Button)findViewById(R.id.btn_login);
        auth         = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go = new Intent(MainActivity.this, Register.class);
                startActivity(go);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go2 = new Intent(MainActivity.this, Login.class);
                startActivity(go2);
            }
        });
    }
    protected void onStart() {
        super.onStart();

//       cek user apakah sudah pernah login sebelumnya
        if (auth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(MainActivity.this, Home.class));
        }
    }
}
