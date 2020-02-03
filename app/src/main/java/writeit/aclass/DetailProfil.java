package writeit.aclass;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailProfil extends AppCompatActivity {

    CircleImageView circleProfil;
    TextView tvUsername, tvStatus, btnTambahPertemanan, btnTolakPertemanan;
    ImageView bgImgLayout;

    DatabaseReference database;
    DatabaseReference databaseRequest;
    DatabaseReference databaseFriend;
    DatabaseReference databaseNotification;
    FirebaseUser MyUser;

    //   App Bar
    AppBarLayout appBarLayout;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    //   Req
    String Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_profil);

        tvUsername = (TextView) findViewById(R.id.tvUsername);
        circleProfil = (CircleImageView) findViewById(R.id.circleProfil);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        bgImgLayout = (ImageView) findViewById(R.id.bgImgLayout);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        btnTambahPertemanan = (TextView) findViewById(R.id.btnTambahPertemanan);
        btnTolakPertemanan = (TextView) findViewById(R.id.btnTolakPertemanan);

//      ID USER YG DI KLIK
        final String UserId = getIntent().getStringExtra("Uid");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setTitleEnabled(false);
        toolbar.setTitle("");

//      DB REFRENCE
        database = FirebaseDatabase.getInstance().getReference().child("users").child(UserId);
        databaseFriend = FirebaseDatabase.getInstance().getReference().child("friend");
        databaseRequest = FirebaseDatabase.getInstance().getReference().child("friend_req");
        databaseNotification = FirebaseDatabase.getInstance().getReference().child("notification");
        //      Get My User
        MyUser = FirebaseAuth.getInstance().getCurrentUser();
        final String MyUid = MyUser.getUid();

//      GET DATA

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String getUsername = dataSnapshot.child("username").getValue().toString();
                String getStatus = dataSnapshot.child("status").getValue().toString();
                String getThumbImage = dataSnapshot.child("thumb_image").getValue().toString();
                String getBGImage = dataSnapshot.child("bg_image").getValue().toString();
                final String getImageReal = dataSnapshot.child("image").getValue().toString();

//              APP BAR SET NAMA ORANG
                appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    boolean isVisible = true;
                    int scrollRange = -1;

                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        if (scrollRange == -1) {
                            scrollRange = appBarLayout.getTotalScrollRange();
                        }
                        if (scrollRange + verticalOffset == 0) {
                            toolbar.setTitle(getUsername);
                            isVisible = true;
                        } else if (isVisible) {
                            toolbar.setTitle("");
                            isVisible = false;
                        }
                    }
                });
                tvUsername.setText(getUsername);
                tvStatus.setText(getStatus);
                Picasso.with(DetailProfil.this).load(getBGImage).into(bgImgLayout);

//              ZOOM POP UP IMAGE
                circleProfil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /**
                         * Initiate Custom Dialog
                         */
                        Dialog dialog = new Dialog(DetailProfil.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.detail_image_profil);

                        /**
                         * Mengeset komponen dari custom dialog
                         */
                        PhotoView imageProfilDetail = (PhotoView) dialog.findViewById(R.id.ProfilDetail);
                        if (!getImageReal.equals("default_profil")) {
                            Picasso.with(DetailProfil.this).load(getImageReal).placeholder(R.drawable.progressbar_anim).into(imageProfilDetail);
                        }
                        dialog.show();
                    }
                });

//              FRIEND LIST / REQUEST FEATURE
                databaseRequest.child(MyUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(UserId)) {
                            String req_type = dataSnapshot.child(UserId).child("type").getValue().toString();

                            if (req_type.equals("received")) {

                                Status = "req_received";
                                btnTambahPertemanan.setText("Terima Permintaan Pertemanan");
                                btnTolakPertemanan.setVisibility(View.VISIBLE);
                                btnTolakPertemanan.setEnabled(true);

//                              Tolak Pertemanan
                                btnTolakPertemanan.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        databaseRequest.child(MyUid).child(UserId).removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        databaseRequest.child(UserId).child(MyUid).removeValue()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                        btnTambahPertemanan.setEnabled(true);
                                                                        Status = "not_friend";
                                                                        btnTambahPertemanan.setText("Tambah Pertemanan");

                                                                        btnTolakPertemanan.setVisibility(View.GONE);
                                                                        btnTolakPertemanan.setEnabled(false);

                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });

                            } else if (req_type.equals("sent")) {

                                Status = "request";
                                btnTambahPertemanan.setText("Batalkan Permintaan Pertemanan");

                            }
                        } else {

                            databaseFriend.child(MyUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(UserId)) {
                                        Status = "friends";
                                        btnTambahPertemanan.setText("Unfriend");

                                        btnTolakPertemanan.setVisibility(View.GONE);
                                        btnTolakPertemanan.setEnabled(false);


                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                if (!getThumbImage.equals("default_profil")) {
                    Picasso.with(DetailProfil.this).load(getThumbImage).placeholder(R.drawable.userdef).into(circleProfil);
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//      Friend Req
        Status = "not_friend";

        btnTambahPertemanan.setText("Tambah Pertemanan");
        btnTambahPertemanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//              IF NOT FRIEND
                btnTambahPertemanan.setEnabled(false);

                if (Status.equals("not_friend")) {

                    databaseRequest.child(MyUid).child(UserId).child("type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    btnTambahPertemanan.setEnabled(false);

                                    if (task.isSuccessful()) {

                                        databaseRequest.child(UserId).child(MyUid).child("type").setValue("received")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        HashMap<String, String> notificationData = new HashMap<>();
                                                        notificationData.put("from", MyUid);
                                                        notificationData.put("type", "request");

//                                                      DATABASE NOTIFICATION
//                                                      PUSH = RANDOM KEY ID
                                                        databaseNotification.child(UserId).push().setValue(notificationData)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Status = "request";
                                                                            btnTambahPertemanan.setText("Batalkan Permintaan Pertemanan");
                                                                        } else {
                                                                            Toast.makeText(DetailProfil.this, "Gagal Menambah Pertemanan", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });

                                                    }
                                                });

                                    } else {
                                        Toast.makeText(DetailProfil.this, "Gagal Menambah Pertemanan", Toast.LENGTH_SHORT).show();
                                    }

                                    btnTambahPertemanan.setEnabled(true);

                                }
                            });

                }
//              IF REQUEST HAS SEND - then U want cancle
                if (Status.equals("request")) {
                    databaseRequest.child(MyUid).child(UserId).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    databaseRequest.child(UserId).child(MyUid).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    btnTambahPertemanan.setEnabled(true);
                                                    Status = "not_friend";
                                                    btnTambahPertemanan.setText("Tambah Pertemanan");

                                                }
                                            });
                                }
                            });
                }

//              IF REQ RECEIVED  - Its so confusing like shit
                if (Status.equals("req_received")) {

                    final String currentDate = DateFormat.getDateInstance().format(new Date());

                    databaseFriend.child(MyUid).child(UserId).child("date").setValue(currentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    databaseFriend.child(UserId).child(MyUid).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            databaseRequest.child(MyUid).child(UserId).removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            databaseRequest.child(UserId).child(MyUid).removeValue()
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                            btnTambahPertemanan.setEnabled(true);
                                                                            Status = "friends";
                                                                            btnTambahPertemanan.setText("Unfriend");

                                                                            btnTolakPertemanan.setVisibility(View.GONE);
                                                                            btnTolakPertemanan.setEnabled(false);

                                                                        }
                                                                    });
                                                        }
                                                    });

                                        }
                                    });

                                }
                            });

                }
//              UNFRIEND IF STATUS FIREND
                if (Status.equals("friends")) {

                    final Dialog dialogUnfriend = new Dialog(DetailProfil.this);
                    dialogUnfriend.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogUnfriend.setContentView(R.layout.unfriend_message);

                    btnTambahPertemanan.setEnabled(true);

                    Button ya = (Button) dialogUnfriend.findViewById(R.id.ya);
                    Button tidak = (Button) dialogUnfriend.findViewById(R.id.tidak);

                    ya.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

//                          Unfriend
                            databaseFriend.child(MyUid).child(UserId).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            databaseFriend.child(UserId).child(MyUid).removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            Status = "not_friend";
                                                            btnTambahPertemanan.setText("Tambah Pertemanan");
                                                            dialogUnfriend.cancel();

                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
                    tidak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogUnfriend.cancel();
                        }
                    });
                    dialogUnfriend.show();


                }

            }
        });

    }

}
