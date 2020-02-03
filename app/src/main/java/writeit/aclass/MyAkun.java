package writeit.aclass;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class MyAkun extends AppCompatActivity {

    CircleImageView circleProfil;
    TextView btnSettingAkun, tvUsername, tvStatus, btnUpdateBg;
    ImageView bgImgLayout;

    EditText et_status;
    Button btn_postSts;

    DatabaseReference database, databaseStatus;
    FirebaseUser user;
    StorageReference storage;
    ProgressDialog message;

//   App Bar
    AppBarLayout appBarLayout;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_akun);

        circleProfil = (CircleImageView)findViewById(R.id.circleProfil);
        btnSettingAkun = (TextView)findViewById(R.id.btnSettingAkun);
        tvUsername = (TextView)findViewById(R.id.tvUsername);
        tvStatus = (TextView)findViewById(R.id.tvStatus);
        btnUpdateBg = (TextView)findViewById(R.id.btnUpdateBg);
        bgImgLayout = (ImageView)findViewById(R.id.bgImgLayout);
        appBarLayout = (AppBarLayout)findViewById(R.id.appBarLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout= (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        btn_postSts = (Button)findViewById(R.id.btn_postSts);
        et_status = (EditText)findViewById(R.id.et_status) ;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setTitleEnabled(false);
        toolbar.setTitle("");

//
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener(){
            boolean isVisible = true;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolbar.setTitle("Akun Saya");
                    isVisible = true;
                } else if(isVisible) {
                    toolbar.setTitle("");
                    isVisible = false;
                }
            }
        });


        user = FirebaseAuth.getInstance().getCurrentUser();
        final String Uid = user.getUid();
        database = FirebaseDatabase.getInstance().getReference().child("users").child(Uid);
        storage = FirebaseStorage.getInstance().getReference();
//      Offline Capabilities
        database.keepSynced(true);

        //        GET DATA
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String getUserName = dataSnapshot.child("username").getValue().toString();
                String getStatus = dataSnapshot.child("status").getValue().toString();
                final String getImage = dataSnapshot.child("thumb_image").getValue().toString();
                final String getImageReal = dataSnapshot.child("image").getValue().toString();
                final String getBgImage = dataSnapshot.child("bg_image").getValue().toString();

                tvUsername.setText(getUserName);
                tvStatus.setText(getStatus);
                Picasso.with(MyAkun.this).load(getBgImage).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.gradient_red).into(bgImgLayout, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                    }
                });
//              LOAD IMAGE FROM INTERNET
//                Picasso.with(MyAkun.this).load(getImage).placeholder(R.drawable.userdef).into(circleProfil);
                if (!getImage.equals("default_profil")) {
                    Picasso.with(MyAkun.this).load(getImage).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.userdef).into(circleProfil, new Callback() {
                        @Override
//                      On succes Do nothing
                        public void onSuccess(){}
                        @Override
                        public void onError() {
//                          Load Pic Online
                            Picasso.with(MyAkun.this).load(getImage).placeholder(R.drawable.userdef).into(circleProfil);
                        }
                    });
                }

//              ZOOM POP UP IMAGE
                circleProfil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /**
                         * Initiate Custom Dialog
                         */
                        Dialog dialog = new Dialog(MyAkun.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.detail_image_profil);

                        /**
                         * Mengeset komponen dari custom dialog
                         */
                        PhotoView imageProfilDetail = (PhotoView) dialog.findViewById(R.id.ProfilDetail);
                        if (!getImageReal.equals("default_profil")) {
                            Picasso.with(MyAkun.this).load(getImageReal).into(imageProfilDetail);
                        }
                        dialog.show();
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        btnUpdateBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Pilih Foto Profil"), 0);
            }
        });

        btnSettingAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAkun.this, EditAkun.class));
            }
        });

        btn_postSts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Status =  et_status.getText().toString();


                databaseStatus = FirebaseDatabase.getInstance().getReference();

                Map dataStatus = new HashMap();
                dataStatus.put("status", Status);
                dataStatus.put("foto", "0");

                Map data = new HashMap();
                data.put("status/" + Uid, dataStatus);

                databaseStatus.updateChildren(data, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(MyAkun.this, "Status Masuk", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

//  Activity Crop Photo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if (resultCode == RESULT_OK) {
                message = new ProgressDialog(MyAkun.this);
                message.setTitle("Mengupload Gambar");
                message.setMessage("Mohon Tunggu Sedang Mengupload Gambar");
                message.setCanceledOnTouchOutside(false);
                message.show();

                Uri resultUri = result.getUri();
                String Uid = user.getUid();
                final StorageReference linkImg = storage.child("gambarProfil").child("bg_image").child(Uid + ".jpg");

                // COMPRESS IMAGE
                File imageBgFile = new File(resultUri.getPath());
                Bitmap bg_bitmap = null;

                try {
                    bg_bitmap = new Compressor(MyAkun.this)
                            .setMaxWidth(400)
                            .setMaxHeight(400)
                            .setQuality(80)
                            .compressToBitmap(imageBgFile);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bg_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                    linkImg.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                UploadTask uploadTask = linkImg.putBytes(thumb_byte);

                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        String downloadImageBg = task.getResult().getDownloadUrl().toString();

                                        if (task.isSuccessful()){
                                            database.child("bg_image").setValue(downloadImageBg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    message.dismiss();
                                                }
                                            });
                                        }else {
                                            message.dismiss();
                                            Toast.makeText(MyAkun.this, "Failed Upload", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            } else {
                                message.dismiss();
                                Toast.makeText(MyAkun.this, "Failed Upload Photo", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
