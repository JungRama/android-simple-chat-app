package writeit.aclass;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import writeit.aclass.Adapter.DataUserAdapter;


public class FindUser extends AppCompatActivity {

    RecyclerView rvListUser;
    DatabaseReference UserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UserData = FirebaseDatabase.getInstance().getReference().child("users");
        UserData.keepSynced(true);

        rvListUser = (RecyclerView) findViewById(R.id.rvListUser);
        rvListUser.setHasFixedSize(true);
        rvListUser.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<DataUserAdapter, UserViewHolder> UserAdapter = new FirebaseRecyclerAdapter<DataUserAdapter, UserViewHolder>(
                DataUserAdapter.class,
                R.layout.user_list,
                UserViewHolder.class,
                UserData) {
            protected void populateViewHolder(UserViewHolder userViewHolder, DataUserAdapter listUserAdapter, int i) {
                UserViewHolder.setName(listUserAdapter.getUsername());
                UserViewHolder.setStatus(listUserAdapter.getStatus());
                UserViewHolder.setUserImage(listUserAdapter.getThumb_image(), getApplicationContext());

                final String id_user = getRef(i).getKey();

                userViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent detail = new Intent(FindUser.this, DetailProfil.class);
                        detail.putExtra("Uid", id_user);
                        startActivity(detail);

                    }
                });

            }
        };

        rvListUser.setAdapter(UserAdapter);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        static View view;

        public UserViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
        public static void setName(String username){

            TextView UsernameView = (TextView) view.findViewById(R.id.tvUsername);
            UsernameView.setText(username);

        }
        public static void setStatus(String status){

            TextView StatusView = (TextView) view.findViewById(R.id.tvStatus);
            StatusView.setText(status);

        }

        public static void setUserImage(String thumbImage, Context ctx) {
            CircleImageView userProfil = (CircleImageView)view.findViewById(R.id.profil_pic);
            Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.userdef).into(userProfil);
        }
    }
}
