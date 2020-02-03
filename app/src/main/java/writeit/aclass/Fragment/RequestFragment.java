package writeit.aclass.Fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import writeit.aclass.Adapter.DataFriendAdapter;
import writeit.aclass.Chat;
import writeit.aclass.DetailProfil;
import writeit.aclass.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    RecyclerView rvListFriend;

    String Uid;

    View view;

    FirebaseAuth auth;
    DatabaseReference databaseFriend;
    DatabaseReference databaseUser;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_request, container, false);

        rvListFriend = (RecyclerView) view.findViewById(R.id.rvListFriend);

        auth = FirebaseAuth.getInstance();

        Uid = auth.getCurrentUser().getUid();

        databaseFriend = FirebaseDatabase.getInstance().getReference().child("friend").child(Uid);
        databaseUser = FirebaseDatabase.getInstance().getReference().child("users");

        rvListFriend.setHasFixedSize(true);
        rvListFriend.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<DataFriendAdapter, FriendViewHolder>
                FriendAdapter = new FirebaseRecyclerAdapter<DataFriendAdapter, FriendViewHolder>(
                        DataFriendAdapter.class,
                        R.layout.user_list,
                        FriendViewHolder.class,
                        databaseFriend
        ) {
            @Override
            protected void populateViewHolder(FriendViewHolder viewHolder, DataFriendAdapter listFriendAdapter, int position) {


                final String ListUserId = getRef(position).getKey();
                databaseUser.child(ListUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String getUsername = dataSnapshot.child("username").getValue().toString();
                        String getStatus = dataSnapshot.child("status").getValue().toString();
                        String getThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        FriendViewHolder.setUsername(getUsername);
                        FriendViewHolder.setStatus(getStatus);
                        FriendViewHolder.setUserImage(getThumb, getContext());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                FriendViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent detail = new Intent(getActivity(), DetailProfil.class);
                        detail.putExtra("Uid", ListUserId);
                        startActivity(detail);

                    }
                });

                FriendViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        CharSequence pilihan[] = new CharSequence[]{"Lihat Profil","Mulai Chat","Block User"};

                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                        alert.setTitle("Pilih");

                        alert.setItems(pilihan, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (i==0){
                                    Intent detail = new Intent(getActivity(), DetailProfil.class);
                                    detail.putExtra("Uid", ListUserId);
                                    startActivity(detail);
                                }else if (i==1){
                                    Intent chat = new Intent(getActivity(), Chat.class);
                                    chat.putExtra("Uid", ListUserId);
                                    startActivity(chat);
                                }

                            }
                        });

                        alert.show();

                        return false;
                    }
                });

            }
        };
        rvListFriend.setAdapter(FriendAdapter);
    }


    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        static View view;

        public FriendViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public static void setDate(String date){

            TextView UsernameView = (TextView) view.findViewById(R.id.tvStatus);
            UsernameView.setText(date);

        }

        public static void setUsername(String username){

            TextView UsernameView = (TextView) view.findViewById(R.id.tvUsername);
            UsernameView.setText(username);

        }

        public static void setStatus(String status){

            TextView UsernameView = (TextView) view.findViewById(R.id.tvStatus);
            UsernameView.setText(status);

        }

        public static void setUserImage(String thumbImage, Context ctx) {
            CircleImageView userProfil = (CircleImageView)view.findViewById(R.id.profil_pic);
            Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.userdef).into(userProfil);
        }

    }
}
