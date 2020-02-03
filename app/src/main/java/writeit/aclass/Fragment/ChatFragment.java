package writeit.aclass.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import writeit.aclass.FindUser;
import writeit.aclass.Home;
import writeit.aclass.MainActivity;
import writeit.aclass.MyAkun;
import writeit.aclass.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    public ChatFragment() {
        // Required empty public constructor
    }


    Button btnLogout, btnEditAkun, btnFindUser;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat,container,false);


        btnLogout = (Button)v.findViewById(R.id.btnLogout);
        btnEditAkun = (Button)v.findViewById(R.id.btnEditAkun);
        btnFindUser = (Button)v.findViewById(R.id.btnFindUser);

        auth = FirebaseAuth.getInstance();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent logout = new Intent(getActivity(), MainActivity.class);
                startActivity(logout);
            }
        });

        btnEditAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent akun = new Intent(getActivity(), MyAkun.class);
                startActivity(akun);
            }
        });

        btnFindUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Find = new Intent(getActivity(), FindUser.class);
                startActivity(Find);
            }
        });

        return v;
    }
}
