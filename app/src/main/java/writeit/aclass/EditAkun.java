package writeit.aclass;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditAkun extends AppCompatActivity {

    TextView tvUsername, tvEmail, btnEditBg;
    CircleImageView circleProfil;
    EditText etChangeStatus, editTglClick, etChangeDName;
    Button btnSaveStatus;
    ProgressDialog message;

    DatabaseReference database;
    FirebaseUser user;
    StorageReference storage;

    //  DATEPICKER
    DatePickerDialog datePicker;
    SimpleDateFormat dateFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_akun);

        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        circleProfil = (CircleImageView) findViewById(R.id.circleProfil);
        etChangeStatus = (EditText) findViewById(R.id.etChangeStatus);
        etChangeDName = (EditText) findViewById(R.id.etChangeDName);
        btnSaveStatus = (Button) findViewById(R.id.btnSaveStatus);
        btnEditBg = (TextView) findViewById(R.id.btnEditBg);

        editTglClick = (EditText) findViewById(R.id.editTglClick);

//      Animation
        final Animation AnmUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_up);
        final Animation AnmDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_down);

//      DatePicker
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        user = FirebaseAuth.getInstance().getCurrentUser();
        String Uid = user.getUid();
        database = FirebaseDatabase.getInstance().getReference().child("users").child(Uid);
        storage = FirebaseStorage.getInstance().getReference();

//        GET DATA
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String getUserName = dataSnapshot.child("username").getValue().toString();
                String getName = dataSnapshot.child("display_name").getValue().toString();
                String getEmail = user.getEmail().toString();
                String getStatus = dataSnapshot.child("status").getValue().toString();
                String getImage = dataSnapshot.child("thumb_image").getValue().toString();
                String getBirthday = dataSnapshot.child("birthday").getValue().toString();

                etChangeDName.setText(getName);
                tvUsername.setText(getUserName);
                tvEmail.setText(getEmail);
                etChangeStatus.setText(getStatus);
                editTglClick.setText(getBirthday);
//              LOAD IMAGE FROM INTERNET
                if (!getImage.equals("default_profil")) {
                    Picasso.with(EditAkun.this).load(getImage).placeholder(R.drawable.userdef).into(circleProfil);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//      UPDATE DATA
        btnSaveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String StatusNow = etChangeStatus.getText().toString();
                String DNameNow = etChangeDName.getText().toString();
                String BirthNow = editTglClick.getText().toString();
                database.child("status").setValue(StatusNow);
                database.child("display_name").setValue(DNameNow);
                database.child("birthday").setValue(BirthNow);
                startActivity(new Intent(EditAkun.this, MyAkun.class));
            }
        });

//      Buka Galeri
        circleProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Pilih Foto Profil"), 0);
            }
        });

        btnEditBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Pilih Foto Background"), 1);
            }
        });

//      Set DatePicker
        editTglClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
    }

    //  SET FOTO PROFIL
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMaxCropResultSize(500,500)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                message = new ProgressDialog(EditAkun.this);
                message.setTitle("Mengupload Gambar");
                message.setMessage("Mohon Tunggu Sedang Mengupload Gambar");
                message.setCanceledOnTouchOutside(false);
                message.show();

                Uri resultUri = result.getUri();
                String Uid = user.getUid();

//              DB REFRENCE
                StorageReference linkImg = storage.child("gambarProfil").child(Uid + ".jpg");
                final StorageReference linkBitmap = storage.child("gambarProfil").child("thumbs").child(Uid + ".jpg");

                //              COMPRESS IMAGE
                File imageFile = new File(resultUri.getPath());
                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(EditAkun.this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(imageFile);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                    linkImg.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                final String downloadImage = task.getResult().getDownloadUrl().toString();
                                UploadTask uploadTask = linkBitmap.putBytes(thumb_byte);

                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                        String downloadImageBitmap = task.getResult().getDownloadUrl().toString();

                                        if (task.isSuccessful()){

                                            Map update_hashMap = new HashMap();
                                            update_hashMap.put("image", downloadImage);
                                            update_hashMap.put("thumb_image", downloadImageBitmap);

                                            database.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    message.dismiss();
                                                }
                                            });
                                        }else {
                                            message.dismiss();
                                            Toast.makeText(EditAkun.this, "Failed Upload Thumb", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });



                            } else {
                                message.dismiss();
                                Toast.makeText(EditAkun.this, "Failed Upload Photo", Toast.LENGTH_SHORT).show();
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


    //  DATEPICKER
    private void showDateDialog() {
        Calendar newCalendar = Calendar.getInstance();
        datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);


                editTglClick.setText(dateFormat.format(newDate.getTime()));
//                database.child("birthday").setValue(dateFormat.format(newDate.getTime()));


            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }
}
