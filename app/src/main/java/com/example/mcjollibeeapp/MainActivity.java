package com.example.mcjollibeeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button login,adminLogin;
    TextView signUp, forgotPass;
    DatabaseOperations db;
    ConstraintLayout loginPanel,adminPanel;
    ImageButton adminButton, loginButton;
    ImageView info;
    TextInputLayout usernameIL, passwordIL, adminUserIL, adminPassIL;
    TextInputEditText loginUserName,loginPassword,adminUserName,adminPassword;
    ArrayList<String> userPassAL = new ArrayList<>();
    ProgressBar loginProgress, adminProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (Button)findViewById(R.id.userLogin);
        adminLogin = (Button)findViewById(R.id.adminLogin);
        loginPanel = (ConstraintLayout)findViewById(R.id.loginPanel);
        adminPanel = (ConstraintLayout)findViewById(R.id.adminPanel);
        signUp = (TextView)findViewById(R.id.regLogin);
        adminButton = (ImageButton)findViewById(R.id.adminPanelBtn);
        loginButton = (ImageButton)findViewById(R.id.loginPanelBtn);
        loginUserName = (TextInputEditText)findViewById(R.id.loginUsername);
        loginPassword = (TextInputEditText)findViewById(R.id.loginPass);
        adminUserName = (TextInputEditText)findViewById(R.id.adminUsername);
        adminPassword = (TextInputEditText)findViewById(R.id.adminPass);
        usernameIL = (TextInputLayout)findViewById(R.id.usernameIL);
        passwordIL = (TextInputLayout)findViewById(R.id.passIL);
        adminPassIL = (TextInputLayout)findViewById(R.id.adminUserIL);
        adminUserIL = (TextInputLayout)findViewById(R.id.adminPassIL);
        loginProgress = (ProgressBar)findViewById(R.id.progressBar);
        adminProgress = (ProgressBar)findViewById(R.id.progressBarAdmin);
        forgotPass = (TextView) findViewById(R.id.forgotPass);
        info = (ImageView)findViewById(R.id.aboutInfo);


        loginButton.setVisibility(View.GONE);
        adminPanel.setVisibility(View.GONE);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AppInfo.class);
                startActivity(intent);
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Please contact the support!", Toast.LENGTH_SHORT).show();
            }
        });

        loginUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usernameIL.setErrorEnabled(false);
            }
        });

        loginPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordIL.setErrorEnabled(false);

            }
        });

        adminUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminUserIL.setErrorEnabled(false);

            }
        });

        adminPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminPassIL.setErrorEnabled(false);

            }
        });
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminButton.setVisibility(View.GONE);
                adminButton.setElevation(4);
                loginPanel.setVisibility(View.GONE);

                loginButton.setVisibility(View.VISIBLE);
                loginButton.setElevation(100);
                adminPanel.setVisibility(View.VISIBLE);
                System.out.println("admin");


            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 loginButton.setVisibility(View.GONE);
                adminPanel.setVisibility(View.GONE);
                loginButton.setElevation(4);

                adminButton.setVisibility(View.VISIBLE);
                adminButton.setElevation(100);
                loginPanel.setVisibility(View.VISIBLE);
            }
        });


        db = new DatabaseOperations(this);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),registerActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("adminss1");
                
                if(TextUtils.isEmpty(loginUserName.getText()) || TextUtils.isEmpty(loginPassword.getText())){
                    usernameIL.setError("Required!");
                    passwordIL.setError("Required!");
//                    Toast.makeText(MainActivity.this, "Incomplete information!", Toast.LENGTH_SHORT).show();
                }
                else{

                    userPassAL = db.getUserLogin(new String[]{loginUserName.getText().toString(),"customer"});
                    if(!userPassAL.isEmpty()) {
                        String[] userPass = userPassAL.get(0).split("-");
                        String userName = userPass[0];
                        String password = userPass[1];
                        if (loginUserName.getText().toString().equals(userName) && loginPassword.getText().toString().equals(password)) {
                            String id = db.getUserId(new String[]{loginUserName.getText().toString(),loginPassword.getText().toString()});
                            login.setText("");
                            loginProgress.setVisibility(View.VISIBLE);
                            System.out.println("id is "+id);
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            intent.putExtra("id",id);
                            startActivity(intent);
                        }
                        else{

                            usernameIL.setError("Username did not match");
                            passwordIL.setError("Password did not match");
                            //Toast.makeText(MainActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        usernameIL.setError("Username did not match");
                        passwordIL.setError("Password did not match");
//                        Toast.makeText(MainActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
                    }
                    
                }
//                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//                startActivity(intent);
            }
        });

        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("admins2s");
                if(TextUtils.isEmpty(adminUserName.getText()) || TextUtils.isEmpty(adminPassword.getText())){
                    adminUserIL.setError("Required!");
                    adminPassIL.setError("Required!");
                    System.out.println("adminss");
                }
                else if(adminUserName.getText().toString().equals("admin") && adminPassword.getText().toString().equals("pass")){
                    adminLogin.setText("");
                    adminProgress.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(getApplicationContext(), editOrders.class);
                    startActivity(intent);
                }
                else{
                    System.out.println("adasd");
                    System.out.println(adminUserName.getText().toString()+" asd "+adminPassword.getText().toString());

                    userPassAL = db.getUserLogin(new String[]{adminUserName.getText().toString(),"admin"});
                    if(!userPassAL.isEmpty()) {
                        String[] userPass = userPassAL.get(0).split("-");
                        String userName = userPass[0];
                        String password = userPass[1];

                        if (adminUserName.getText().toString().equals(userName) && adminPassword.getText().toString().equals(password)) {

                            adminLogin.setText("");
                            adminProgress.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(getApplicationContext(), editOrders.class);
                            startActivity(intent);
                        }
                        else{
                            adminUserIL.setError("Username did not match");
                            adminPassIL.setError("Password did not match");
                            Toast.makeText(MainActivity.this, "Wrong username or passwords", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        adminUserIL.setError("Username did not match");
                        adminPassIL.setError("Password did not match");
                        Toast.makeText(MainActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }
}