package com.example.mcjollibeeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class registerActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;
    TextInputEditText firstName, lastName, address,age,contactNumber,username,password;
    TextView login;
    Button signUp;
    DatabaseOperations db;
    CircleImageView profilePic;
    byte[] image;
    ProgressBar reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);

        firstName = (TextInputEditText) findViewById(R.id.profileFirstName);
        lastName = (TextInputEditText) findViewById(R.id.profileLastName);
        address = (TextInputEditText) findViewById(R.id.profileAddress);
        age = (TextInputEditText) findViewById(R.id.peofileAge);
        contactNumber = (TextInputEditText) findViewById(R.id.profileContactNo);
        username = (TextInputEditText) findViewById(R.id.profileUsername);
        password = (TextInputEditText) findViewById(R.id.profilePassword);
        login = (TextView) findViewById(R.id.regLogin);
        signUp = (Button) findViewById(R.id.saveProfileBtn);
        profilePic = (CircleImageView) findViewById(R.id.profilePicSettings);
        reg = (ProgressBar)findViewById(R.id.progressBarReg);

        db = new DatabaseOperations(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
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


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(firstName.getText()) || TextUtils.isEmpty(lastName.getText()) || TextUtils.isEmpty(address.getText()) || TextUtils.isEmpty(age.getText()) ||
                        TextUtils.isEmpty(contactNumber.getText()) || TextUtils.isEmpty(username.getText()) || TextUtils.isEmpty(password.getText())){
                    Toast.makeText(getApplicationContext(), "Incomplete Information!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(image == null){

                        Drawable imageDrawable = getDrawable(R.drawable.no_boarder_pic);
                        Bitmap bitmap = ((BitmapDrawable) imageDrawable).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG,100, stream);
                        image = stream.toByteArray();
                    }
                    if(db.insertUser(image, firstName.getText().toString(),lastName.getText().toString(),Integer.parseInt(age.getText().toString()),address.getText().toString(),contactNumber.getText().toString(),
                            username.getText().toString(),password.getText().toString(),"customer")){
                        signUp.setText("");
                        reg.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Account Successfully created!", Toast.LENGTH_SHORT).show();
                        String id = db.getUserId(new String[]{username.getText().toString(),password.getText().toString()});
                        System.out.println("id is "+id);
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("id",id);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(registerActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                    }
                }
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
            }
        }
    }
}