package com.example.mcjollibeeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.collections.unsigned.UArraysKt;

public class settings extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;
    Button logout, save;
    TextInputEditText firstName, lastName, address, age, contactNo,username,pass;
    CircleImageView profilePic;
    byte[] image;
    ImageView settings,back;
    DatabaseOperations db;
    ArrayList<Bitmap> userPicAL;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_settings);

        logout = (Button) findViewById(R.id.logoutBtn   );
        firstName = (TextInputEditText)findViewById(R.id.profileFirstName);
        lastName = (TextInputEditText)findViewById(R.id.profileLastName);
        address = (TextInputEditText)findViewById(R.id.profileAddress);
        age = (TextInputEditText)findViewById(R.id.profileAge);
        contactNo = (TextInputEditText)findViewById(R.id.profileContactNo);
        username = (TextInputEditText)findViewById(R.id.profileUsername);
        pass = (TextInputEditText)findViewById(R.id.profilePassword);
        save = (Button)findViewById(R.id.saveProfileBtn);
        profilePic = (CircleImageView)findViewById(R.id.profilePicSettings);
        settings=(ImageView)findViewById(R.id.settings);
        back = (ImageView)findViewById(R.id.backSettings);




        db = new DatabaseOperations(this);
        userPicAL = new ArrayList<>();

        userID = getIntent().getStringExtra("id");
        userPicAL= db.getPic("userTable",null,userID);
        profilePic.setImageBitmap(userPicAL.get(0));

        firstName.setText(""+(db.getUser("firstName",null,"id="+userID).get(0)));
        lastName.setText(""+(db.getUser("lastName",null,"id="+userID).get(0)));
        address.setText(""+(db.getUser("address",null,"id="+userID).get(0)));
        age.setText(""+(db.getUser("age",null,"id="+userID).get(0)));
        contactNo.setText(""+(db.getUser("contactNumber",null,"id="+userID).get(0)));
        username.setText(""+(db.getUser("username",null,"id="+userID).get(0)));
        pass.setText(""+(db.getUser("password",null,"id="+userID).get(0)));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profilePic.callOnClick();
            }
        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(firstName.getText()) || TextUtils.isEmpty(lastName.getText()) || TextUtils.isEmpty(address.getText()) || TextUtils.isEmpty(age.getText()) ||
                        TextUtils.isEmpty(contactNo.getText()) || TextUtils.isEmpty(username.getText()) || TextUtils.isEmpty(pass.getText())){
                    Toast.makeText(getApplicationContext(), "Incomplete Information!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(image == null){

                        Drawable imageDrawable = getDrawable(R.drawable.no_boarder_pic);
                        Bitmap bitmap = ((BitmapDrawable) imageDrawable).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG,100, stream);
                        image = stream.toByteArray();
                        System.out.println("image is null");
                    }
                    if(db.updateUser(Integer.parseInt(userID), image, firstName.getText().toString(),lastName.getText().toString(),Integer.parseInt(age.getText().toString()),address.getText().toString(),contactNo.getText().toString(),
                            username.getText().toString(),pass.getText().toString(),"customer")){
                        Toast.makeText(settings.this, "Changes saved!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            try {
                // Convert the image to a byte array
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if(bitmap.getWidth() >= 500 || bitmap.getHeight() >= 500) {
                    double newHeight = (((double) bitmap.getWidth()/(double) bitmap.getHeight()) * 500);
                    bitmap = Bitmap.createScaledBitmap(bitmap,  (int) newHeight,500, false);
                }
                System.out.println(bitmap.getWidth());

                bitmap.compress(Bitmap.CompressFormat.PNG,100, stream);
                image = stream.toByteArray();
                profilePic.setImageBitmap(bitmap);
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();


            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Nay error bugo ka");
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("id",userID);
        startActivity(intent);
    }
}