package com.example.rajdeeprao.hw_09_a;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {
    ImageView profilePicture;
    String spinnerVal, photoURL;
    FirebaseStorage storage;
    String path;
    FirebaseDatabase db;
    DatabaseReference rootRef;
    DatabaseReference ref2;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        db = FirebaseDatabase.getInstance();
        rootRef = db.getReference("Users");
        FirebaseAuth temp=FirebaseAuth.getInstance();
        firebaseUser=temp.getCurrentUser();
        final EditText firstName= (EditText) findViewById(R.id.fName);
        final EditText lastName= (EditText) findViewById(R.id.lName);
        final ProgressDialog progressDialog=new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Loading..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        profilePicture= (ImageView) findViewById(R.id.profilePicture);
        ref2 = db.getReference("Users");
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    user=snapshot.getValue(User.class);
                    Log.d("USER1:",user.toString());
                    Log.d("USER2:",snapshot.getKey());
                    if(snapshot.getKey().toString().equals(firebaseUser.getUid().toString())){
                        if(!user.getPhotoURL().equals("tempUrL")){
                            Picasso.with(ProfileActivity.this)
                                    .load(user.getPhotoURL())
                                    .into(profilePicture);
                            photoURL=user.getPhotoURL();
                            progressDialog.dismiss();
                        }


                        firstName.setText(user.getfName());
                        lastName.setText(user.getlName());
                    }


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Demo", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

//        Picasso.with(ProfileActivity.this)
//                .load()
//                .into(profilePicture);



        final Spinner spinner= (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerVal= parent.getItemAtPosition(position).toString();
                Log.d("Demo",spinnerVal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
            }
        });

        findViewById(R.id.set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname=firstName.getText().toString();
                String lname=lastName.getText().toString();
                user.setfName(fname);
                user.setlName(lname);
                user.setPhotoURL(photoURL);
                user.setGender(spinnerVal);
                rootRef.child(firebaseUser.getUid()).setValue(user);
                Toast.makeText(ProfileActivity.this,"Changes made successfully",Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.discoverFriends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfileActivity.this,DiscoverFriendsAcitivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    profilePicture.setImageURI(selectedImage);

                    path="profilePicture.png";
                    StorageReference storageRef = storage.getReference(path);

                    profilePicture.setDrawingCacheEnabled(true);
                    profilePicture.buildDrawingCache();
                    Bitmap bitmap = profilePicture.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data2 = baos.toByteArray();


                    UploadTask uploadTask = storageRef.putBytes(data2);
                    Log.d("CURRENT USER1:",mAuth.getCurrentUser().toString());

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(ProfileActivity.this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("CURRENT USER2:",mAuth.getCurrentUser().toString());
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.d("URL",downloadUrl.toString());
                            rootRef.child(firebaseUser.getUid()).child("photoURL").setValue(downloadUrl.toString());
                            Picasso.with(ProfileActivity.this)
                                .load(downloadUrl.toString())
                                .into(profilePicture);

                        }
                    });

                    Log.d("UPLOAD:","Successsful");
                }
                break;
        }



    }
}
