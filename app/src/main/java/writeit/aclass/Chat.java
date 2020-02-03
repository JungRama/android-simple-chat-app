package writeit.aclass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import writeit.aclass.Adapter.DataMessagesAdapter;
import writeit.aclass.Adapter.ListMessageAdapter;

public class Chat extends AppCompatActivity {

    ImageView btnSend, btnAdd;
    EditText etInput;
    RecyclerView rvMessages;

    List<DataMessagesAdapter> messagesList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    ListMessageAdapter adapter;


    DatabaseReference database;
    String Uid, UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btnSend = (ImageView)findViewById(R.id.btnSend);
        btnAdd = (ImageView)findViewById(R.id.btnAdd);
        etInput = (EditText)findViewById(R.id.etInput);
        rvMessages = (RecyclerView)findViewById(R.id.rvMessages);

        linearLayoutManager = new LinearLayoutManager(this);
        adapter = new ListMessageAdapter(messagesList);


        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        UserId = getIntent().getStringExtra("Uid");
        database = FirebaseDatabase.getInstance().getReference();

//       SET MESSAGE ADAPTER /TAMPILIM MESSAGE
        rvMessages.setHasFixedSize(true);
        rvMessages.setLayoutManager(linearLayoutManager);
        rvMessages.setAdapter(adapter);

        loadMessages();


        database.child("users").child(UserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String getUsername = dataSnapshot.child("username").getValue().toString();

                getSupportActionBar().setTitle(getUsername);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//      ADDING CHAT CONVERSATION
        database.child("chat").child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(UserId)){

//                  Map Chat
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);


                    Map chatUserMap = new HashMap();
                    chatUserMap.put("chat/" + Uid + "/" + UserId, chatAddMap);
                    chatUserMap.put("chat/" + UserId + "/" + Uid, chatAddMap);

                    database.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null ){
                                Toast.makeText(Chat.this, "Error : "+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//      SEND MESSAGE

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();

            }
        });



    }

//  LOAD
    private void loadMessages() {

        database.child("messages").child(Uid).child(UserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            DataMessagesAdapter dataMessagesAdapter = dataSnapshot.getValue(DataMessagesAdapter.class);

            messagesList.add(dataMessagesAdapter);
            adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    //  SEND
    private void sendMessage() {

        String getMessage = etInput.getText().toString();

        if (!TextUtils.isEmpty(getMessage)){

            String MyUser_Message_Ref = "messages/" + Uid + "/" + UserId;
            String User_Message_Ref = "messages/" + UserId + "/" + Uid;
            String Notif_Ref = "messages_notif/" + UserId ;


//          GET KEY / MAKE KEY
            DatabaseReference userMessagePush  = database.child("messages")
                    .child(Uid).child(UserId).push();

            DatabaseReference userNotifPush = database.child("messages_notif").child(UserId).push();

            String PushId = userMessagePush.getKey();
            String NotifPushId = userNotifPush.getKey();


            Map messageMap = new HashMap();
            messageMap.put("message", getMessage);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", Uid);

//                  Map Notif
            Map notificationData = new HashMap();
            notificationData.put("from", Uid);
            notificationData.put("message", getMessage);


            Map userMessageMap = new HashMap();
            userMessageMap.put(MyUser_Message_Ref + "/" + PushId, messageMap);
            userMessageMap.put(User_Message_Ref + "/" + PushId, messageMap);
            userMessageMap.put(Notif_Ref + "/" + NotifPushId, notificationData);

            etInput.setText("");

            database.updateChildren(userMessageMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null){
                        Toast.makeText(Chat.this, "Error : "+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

    }

}
