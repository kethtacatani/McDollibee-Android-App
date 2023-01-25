package com.example.mcjollibeeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class editUsers extends AppCompatActivity {
    ArrayList<String> userIDAL, usernameAL, userPassAL, userFirstNameAL, userLastNameAL,userAddressAL,userAgeAL,userContactAL;
    ListView userTableLV;
    Button addUser, editUser, deleteUser, saveUser, makeAdmin, removeAdmin, save;
    TextView sort, tableName;
    DatabaseOperations db;
    ImageView sortASCDESC;
    ConstraintLayout manageUserPanel;
    String orderColumn = "id",
            orderSort="ASC";
    String orderBy = orderColumn+" "+orderSort;
    String whereClause = "";
    int highlightUserPosition = -1;
    boolean edit = false;
    String userType = "customer";
    byte[] image;
    BottomSheetDialog bottomSheetDialog;
    TextInputEditText firstName, lastName, username,password,age,address,contactNumber;
    TextView userDescritpion;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_users);

        manageUserPanel = (ConstraintLayout)findViewById(R.id.manageUserPanel);
        userTableLV = (ListView)findViewById(R.id.editOrderTableLV);
        addUser = (Button)findViewById(R.id.addUser);
        editUser = (Button)findViewById(R.id.editUserBtn);
        deleteUser = (Button)findViewById(R.id.deleteUserBtn);
        sort = (TextView) findViewById(R.id.sortUser);
        sortASCDESC = (ImageView)findViewById(R.id.sortASCDESC);
        makeAdmin = (Button)findViewById(R.id.addAdmin) ;
        removeAdmin = (Button)findViewById(R.id.removeAdmin) ;
        tableName = (TextView) findViewById(R.id.textView4);

        if(getIntent().getExtras().getBoolean("adminView")){
            tableName.setText("Admin Table");
            whereClause = "userType";
            makeAdmin.setVisibility(View.GONE);
            removeAdmin.setVisibility(View.VISIBLE);
            userType = "admin";
            setTitle("Manage Admins");

        }else{
            tableName.setText("User Table");
            whereClause = "";
            makeAdmin.setVisibility(View.VISIBLE);
            removeAdmin.setVisibility(View.GONE);
            userType = "customer";
            setTitle("Manage Users");
        }
//        }

        bottomSheetDialog = new BottomSheetDialog(this,R.style.BottomSheetDialog);
        viewEditTable();


        db=new DatabaseOperations(this);
        db.foreignKeyEnable(true);

        userIDAL = new ArrayList<String>();
        usernameAL = new ArrayList<String>();
        userPassAL = new ArrayList<String>();
        userFirstNameAL = new ArrayList<String>();
        userLastNameAL = new ArrayList<String>();
        userAddressAL = new ArrayList<String>();
        userAgeAL = new ArrayList<String>();
        userContactAL = new ArrayList<String>();

        userIDAL = db.getUser("id",orderBy,whereClause);
        usernameAL = db.getUser("username",orderBy,whereClause);
        userPassAL = db.getUser("password",orderBy,whereClause);
        userFirstNameAL = db.getUser("firstName",orderBy,whereClause);
        userLastNameAL = db.getUser("lastName",orderBy,whereClause);
        userAddressAL = db.getUser("address",orderBy,whereClause);
        userAgeAL = db.getUser("age",orderBy,whereClause);
        userContactAL = db.getUser("contactNumber",orderBy,whereClause);

        userTableArrayAdapter userTableArrayAdapter = new userTableArrayAdapter(getApplicationContext(),userIDAL,usernameAL,userPassAL,userFirstNameAL,userLastNameAL,userAddressAL,userAgeAL,userContactAL);
        userTableLV.setAdapter(userTableArrayAdapter);

        getDefaultImage();



        sortASCDESC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderSort == "ASC"){
                    sortASCDESC.setImageDrawable(getDrawable(R.drawable.ic_action_down));
                    orderSort = "DESC";

                }
                else{
                    sortASCDESC.setImageDrawable(getDrawable(R.drawable.ic_action_up));
                    orderSort = "ASC";
                }
                unselectfromTable();
                refreshUserTable();

            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.showContextMenu();

            }
        });
        sort.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                getMenuInflater().inflate(R.menu.sort_user,contextMenu);
            }
        });

        userTableLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(highlightUserPosition == position) {
                    highlightUserPosition = -1;
                    editUser.setEnabled(false);
                    deleteUser.setEnabled(false);
                    removeAdmin.setEnabled(false);
                    makeAdmin.setEnabled(false);


                }
                else{
                    highlightUserPosition = position;
                    editUser.setEnabled(true);
                    deleteUser.setEnabled(true);
                    removeAdmin.setEnabled(true);
                    makeAdmin.setEnabled(true);
                }
                refreshUserTable();
//                userTableArrayAdapter.notifyDataSetChanged();
                System.out.println("position is "+highlightUserPosition);
            }

        });

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.show();


                unselectfromTable();
                userDescritpion.setText("Add "+userType+" to Table");
                edit = false;
                save.setText("Add");
            }
        });

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id =Integer.parseInt(userIDAL.get(highlightUserPosition));
                if(db.deleteTable(new String[]{String.valueOf(id)},"userTable","id")){
                    unselectfromTable();
                    Toast.makeText(editUsers.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

        editUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.show();
                bottomSheetDialog.show();
                userDescritpion.setText("Edit user "+userFirstNameAL.get(highlightUserPosition)+" in Table");
                firstName.setText(""+userFirstNameAL.get(highlightUserPosition));
                lastName.setText(""+userLastNameAL.get(highlightUserPosition));
                address.setText(""+userAddressAL.get(highlightUserPosition));
                age.setText(""+userAgeAL.get(highlightUserPosition));
                contactNumber.setText(""+userContactAL.get(highlightUserPosition));
                username.setText(""+usernameAL.get(highlightUserPosition));
                password.setText(""+userPassAL.get(highlightUserPosition));
                edit = true;
                save.setText("Save");

            }
        });

        makeAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (db.updateUser(Integer.parseInt(userIDAL.get(highlightUserPosition)),null,userFirstNameAL.get(highlightUserPosition),userLastNameAL.get(highlightUserPosition),Integer.parseInt(userAgeAL.get(highlightUserPosition)),
                        userAddressAL.get(highlightUserPosition),userContactAL.get(highlightUserPosition),usernameAL.get(highlightUserPosition),userPassAL.get(highlightUserPosition),"admin")) {
                    unselectfromTable();
                    refreshUserTable();
                    Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
        removeAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (db.updateUser(Integer.parseInt(userIDAL.get(highlightUserPosition)),null,userFirstNameAL.get(highlightUserPosition),userLastNameAL.get(highlightUserPosition),Integer.parseInt(userAgeAL.get(highlightUserPosition)),
                        userAddressAL.get(highlightUserPosition),userContactAL.get(highlightUserPosition),usernameAL.get(highlightUserPosition),userPassAL.get(highlightUserPosition),"customer")) {
                    unselectfromTable();
                    refreshUserTable();
                    Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }



    class userTableArrayAdapter extends ArrayAdapter {
        ArrayList<String> userIDAL, usernameAL, userPassAL, userFirstNameAL, userLastNameAL,userAddressAL,userAgeAL,userContactAL;

        public userTableArrayAdapter(Context context, ArrayList<String> userIDAL, ArrayList<String> usernameAL, ArrayList<String> userPassAL, ArrayList<String> userFirstNameAL
                ,ArrayList<String> userLastNameAL,ArrayList<String> userAddressAL,ArrayList<String> userAgeAL,ArrayList<String> userContactAL){
            super(context,R.layout.user_menu_custom_lv,R.id.userIDTV,userIDAL);

            this.userIDAL = userIDAL;
            this.usernameAL = usernameAL;
            this.userPassAL = userPassAL;
            this.userFirstNameAL = userFirstNameAL;
            this.userLastNameAL = userLastNameAL;
            this.userAddressAL = userAddressAL;
            this.userAgeAL = userAgeAL;
            this.userContactAL = userContactAL;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.user_menu_custom_lv, parent, false);

            TextView userID = (TextView) row.findViewById(R.id.userIDTV);
            TextView username = (TextView) row.findViewById(R.id.userNameTV);
            TextView password = (TextView) row.findViewById(R.id.userPassTV);
            TextView firsName = (TextView) row.findViewById(R.id.userFirstNameTV);
            TextView lastName = (TextView) row.findViewById(R.id.userLastNameTV);
            TextView address = (TextView) row.findViewById(R.id.userAddressTV);
            TextView age = (TextView) row.findViewById(R.id.userAgeTV);
            TextView contactNumber = (TextView) row.findViewById(R.id.userContactTV);
            LinearLayout tableRow = (LinearLayout)row.findViewById(R.id.userRow);

            userID.setText(""+userIDAL.get(position));
            username.setText(""+usernameAL.get(position));
            password.setText(""+userPassAL.get(position));
            firsName.setText(""+userFirstNameAL.get(position));
            lastName.setText(""+userLastNameAL.get(position));
            address.setText(""+userAddressAL.get(position));
            age.setText(""+userAgeAL.get(position));
            contactNumber.setText(""+userContactAL.get(position));

            System.out.println("clicked to "+highlightUserPosition+" "+position);

            if (highlightUserPosition == position) {
                tableRow.setBackgroundColor(getResources().getColor(R.color.jollibee));
                userID.setTextColor(Color.WHITE);
                username.setTextColor(Color.WHITE);
                password.setTextColor(Color.WHITE);
                firsName.setTextColor(Color.WHITE);
                lastName.setTextColor(Color.WHITE);
                address.setTextColor(Color.WHITE);
                age.setTextColor(Color.WHITE);
                contactNumber.setTextColor(Color.WHITE);

            }

            return row;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.manageUserPanel:
                Intent intent = new Intent(this,editMenu.class);
                startActivity(intent);
                break;
            case R.id.manageOrders:
                Intent intentOrders = new Intent(this,editOrders.class);
                startActivity(intentOrders);
                break;
            case R.id.manageUsers:
                userDescritpion.setText("Add User to Table");
                tableName.setText("User Table");
                whereClause = "";
                makeAdmin.setVisibility(View.VISIBLE);
                removeAdmin.setVisibility(View.GONE);
                userType = "admin";
                setTitle("Manage Users");
                refreshUserTable();
                break;
            case R.id.manageEmployee:
                Intent intentEmployees = new Intent(this,editEmployees.class);
                startActivity(intentEmployees);
                break;
            case R.id.manageAdmins:
                userDescritpion.setText("Add Admin to Table");
                tableName.setText("Admin Table");
                whereClause = "userType";
                makeAdmin.setVisibility(View.GONE);
                removeAdmin.setVisibility(View.VISIBLE);
                userType = "admin";
                setTitle("Manage Admins");
                refreshUserTable();
                break;
            case R.id.adminLogout:
                Intent intentLogout = new Intent(this,MainActivity.class);
                startActivity(intentLogout);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.userID:
                sort.setText("ID");
                orderColumn="id";
                refreshUserTable();
                return true;
            case R.id.username:
                sort.setText("Username");
                orderColumn="username";
                refreshUserTable();
                return true;
            case R.id.password:
                sort.setText("Password");
                orderColumn="password";
                refreshUserTable();
                return true;
            case R.id.firstName:
                sort.setText("First Name");
                orderColumn="firstName";
                refreshUserTable();
                return true;
            case R.id.lastName:
                sort.setText("Last Name");
                orderColumn="lastName";
                refreshUserTable();
                return true;
            case R.id.age:
                sort.setText("Age");
                orderColumn="age";
                refreshUserTable();
                return true;
            case R.id.address:
                sort.setText("Address");
                orderColumn="address";
                refreshUserTable();
                return true;
            case R.id.contactNumber:
                sort.setText("Contact No.");
                orderColumn="contactNumber";
                refreshUserTable();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void unselectfromTable(){
        if(highlightUserPosition>=0) {
            highlightUserPosition = -1;
            deleteUser.setEnabled(false);
            editUser.setEnabled(false);
            removeAdmin.setEnabled(false);
            makeAdmin.setEnabled(false);
            refreshUserTable();
        }
    }

    public void refreshUserTable() {
        String orderBy = orderColumn+" "+orderSort;
        String where = whereClause;

        userIDAL = db.getUser("id",orderBy,where);
        usernameAL = db.getUser("username",orderBy,where);
        userPassAL = db.getUser("password",orderBy,where);
        userFirstNameAL = db.getUser("firstName",orderBy,where);
        userLastNameAL = db.getUser("lastName",orderBy,where);
        userAddressAL = db.getUser("address",orderBy,where);
        userAgeAL = db.getUser("age",orderBy,where);
        userContactAL = db.getUser("contactNumber",orderBy,where);
        userTableArrayAdapter userTableArrayAdapter = new userTableArrayAdapter(getApplicationContext(),userIDAL,usernameAL,userPassAL,userFirstNameAL,userLastNameAL,userAddressAL,userAgeAL,userContactAL);
        int index = userTableLV.getFirstVisiblePosition();
        View v = userTableLV.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - userTableLV.getPaddingTop());
        userTableLV.setAdapter(userTableArrayAdapter);
        userTableLV.setSelectionFromTop(index, top);
    }

//    public void clearETs(){
//        firstName.setText("");
//        lastName.setText("");
//        username.setText("");
//        password.setText("");
//        address.setText("");
//        age.setText("");
//        contactNumber.setText("");
//
//    }

    public void getDefaultImage(){
        Drawable imageDrawable = getDrawable(R.drawable.no_boarder_pic);
        Bitmap bitmap = ((BitmapDrawable) imageDrawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, stream);
        image = stream.toByteArray();
    }

    public void viewEditTable() {
         View dialog = getLayoutInflater().inflate(R.layout.edit_table_bottom_sheet,null,false);
         bottomSheetDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        save = (Button)dialog.findViewById(R.id.saveProfileBtnDialog);

        userDescritpion = (TextView)dialog.findViewById(R.id.profileFullNameDialog);
         firstName = (TextInputEditText) dialog.findViewById(R.id.profileFirstNameDialog);
         lastName = (TextInputEditText)dialog.findViewById(R.id.profileLastNameDialog);
         username = (TextInputEditText)dialog.findViewById(R.id.profileUsernameDialog);
         password = (TextInputEditText)dialog.findViewById(R.id.profilePasswordDialog);
         age = (TextInputEditText)dialog.findViewById(R.id.peofileAgeDialog);
         address = (TextInputEditText)dialog.findViewById(R.id.profileAddressDialog);
         contactNumber = (TextInputEditText)dialog.findViewById(R.id.profileContactNoDialog);

        if(getIntent().getExtras().getBoolean("adminView")){
            userDescritpion.setText("Add Admin to Table");

        }else{
            userDescritpion.setText("Add User to Table");
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(firstName.getText()) && !TextUtils.isEmpty(lastName.getText()) && !TextUtils.isEmpty(age.getText()) && !TextUtils.isEmpty(address.getText()) && !TextUtils.isEmpty(contactNumber.getText()) &&
                        !TextUtils.isEmpty(username.getText()) && !TextUtils.isEmpty(password.getText())) {
                    String userTypeSave = userType;

                    if(!edit) {
                        if (db.insertUser(image,firstName.getText().toString(), lastName.getText().toString(), Integer.parseInt(age.getText().toString()), address.getText().toString(), contactNumber.getText().toString(),
                                username.getText().toString(), password.getText().toString(),userTypeSave)) {
                            refreshUserTable();
                            Toast.makeText(getApplicationContext(), "Inserted Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Username already exists", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{
                        System.out.println("else type is "+userType);
                        if (db.updateUser(Integer.parseInt(userIDAL.get(highlightUserPosition)) ,null,firstName.getText().toString(), lastName.getText().toString(), Integer.parseInt(age.getText().toString()), address.getText().toString(), contactNumber.getText().toString(),
                                username.getText().toString(), password.getText().toString(),userTypeSave)) {
                            edit = false;
                            userDescritpion.setText("Add "+userType+" to Table");
                            unselectfromTable();
                            Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Username already exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(editUsers.this, "Incomplete item information", Toast.LENGTH_SHORT).show();
                }

            }
        });




        bottomSheetDialog.setContentView(dialog);

    }




}