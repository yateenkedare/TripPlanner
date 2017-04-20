package com.example.rajdeeprao.hw_09_a;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestsActivity extends AppCompatActivity {

    DatabaseReference ref2;
    DatabaseReference ref3;
    FirebaseDatabase db;
    User user;
    FirebaseUser firebaseUser;
    ArrayList<User> requested;
    ArrayList<User> received;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        db = FirebaseDatabase.getInstance();

        requested=new ArrayList<User>();
        received=new ArrayList<User>();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        ref2 = db.getReference("Users").child(firebaseUser.getUid());
        ref3 = db.getReference("Users");

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("UserIS:",snapshot.getValue(User.class).toString());
                user=snapshot.getValue(User.class);
                final ArrayList<String> requestReceivedString=user.getRequestsReceived();
                ref3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot1: dataSnapshot.getChildren()){
                            for(int i=0;i<requestReceivedString.size();i++){
                                Log.d("Testing:",requestReceivedString.get(i));
                                if(snapshot1.getKey().toString().equals(requestReceivedString.get(i))){
                                    Log.d("Testing:","Entered");
                                    received.add(snapshot1.getValue(User.class));
                                }
                                Log.d("ReceivedList:",received.toString());
                                ListView lv= (ListView) findViewById(R.id.received);
                                //ListView lv2= (ListView) findViewById(R.id.sent);
                                //ArrayAdapter<Color> adapter=new ArrayAdapter<Color>(this,android.R.layout.simple_list_item_1,colors);
                                RequestAdapter adapter=new RequestAdapter(RequestsActivity.this,R.layout.requests,received,user,ref3);
                                lv.setAdapter(adapter);
                                adapter.setNotifyOnChange(true);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Demo", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });



    }
}
