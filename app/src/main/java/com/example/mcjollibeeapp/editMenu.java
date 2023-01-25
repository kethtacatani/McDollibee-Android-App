package com.example.mcjollibeeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class editMenu extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;
    ArrayList<String>  foodIDAL, foodNameAL, foodTypeAL, foodPriceAL;
    ArrayList<Bitmap> foodPicAL;
    ImageView foodImageIV, sortASCDESC;
    ListView foodTableLV;
    DatabaseOperations db;
    Button addItem, editItem ,addImage, save,deleteItem;
    TextView menuStat, editDescription, sort;
    TextInputEditText foodName, foodPrice;
    byte[] image;
    RadioButton mealRB,driinksRB, dessertRB;
    String selectedRadio="None";
    ConstraintLayout manageMenuPanel;
    RadioGroup editRadioGroup;
    String orderColumn = "foodId",
            orderSort="ASC";
    String orderBy = orderColumn+" "+orderSort;
    boolean edit = false;
    int highlightPosition = -1;
    View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu);

        foodImageIV = (ImageView)findViewById(R.id.foodPic2);
        manageMenuPanel = (ConstraintLayout)findViewById(R.id.manageUserPanel);
        mealRB = (RadioButton)findViewById(R.id.codPayment);
        driinksRB = (RadioButton)findViewById(R.id.gcashPayment);
        dessertRB = (RadioButton)findViewById(R.id.paymayaPayment);
        addItem= (Button) findViewById(R.id.addItem);
        addImage= (Button) findViewById(R.id.editAddImage);
        deleteItem= (Button) findViewById(R.id.deleteItemBtn);
        foodName = (TextInputEditText)findViewById(R.id.editFoodName);
        foodPrice = (TextInputEditText)findViewById(R.id.editPrice);
        editRadioGroup =(RadioGroup)findViewById(R.id.editRadioGroup);
        save = (Button)findViewById(R.id.editSaveBtn);
        editItem = (Button)findViewById(R.id.editItemBtn);
        foodTableLV = (ListView)findViewById(R.id.editMenuTableLV);
        editDescription = (TextView) findViewById(R.id.editDescription);
        sort = (TextView) findViewById(R.id.sort);
        sortASCDESC = (ImageView)findViewById(R.id.sortASCDESC);

        foodIDAL = new ArrayList<String>();
        foodNameAL = new ArrayList<String>();
        foodTypeAL = new ArrayList<String>();
        foodPriceAL = new ArrayList<String>();
        foodPicAL = new ArrayList<Bitmap>();

        db = new DatabaseOperations(this);
        db.foreignKeyEnable(true);
        foodIDAL = db.getColumn("menuTable","foodId",orderBy);
        foodNameAL = db.getColumn("menuTable","foodName",orderBy);
        foodPriceAL = db.getColumn("menuTable","foodPrice",orderBy);
        foodTypeAL = db.getColumn("menuTable","foodType",orderBy);
        foodPicAL = db.getPic("menuTable",orderBy,null);

        foodTableArrayAdapter  foodTableArrayAdapter = new foodTableArrayAdapter(getApplicationContext(),foodIDAL,foodNameAL,foodPriceAL,foodTypeAL,foodPicAL);
        foodTableLV.setAdapter(foodTableArrayAdapter);

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
                refreshTable();

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
                    getMenuInflater().inflate(R.menu.sort_menu,contextMenu);
                }
        });


        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unselectfromTable();
                clearETs();
                editDescription.setText("Add Food to Menu");
                edit=false;
            }
        });

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id =Integer.parseInt(foodIDAL.get(highlightPosition));
                if(db.deleteTable(new String[]{String.valueOf(id)},"menuTable","foodId")){
                    unselectfromTable();
                    clearETs();
                    Toast.makeText(editMenu.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

        editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDescription.setText("Edit Food in Menu");
                foodName.setText(""+foodNameAL.get(highlightPosition));
                foodPrice.setText(""+foodPriceAL.get(highlightPosition));
                Bitmap bitmap = foodPicAL.get(highlightPosition);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                image = stream.toByteArray();
                foodImageIV.setImageBitmap(bitmap);
                setCheckedRB(""+foodTypeAL.get(highlightPosition));
                edit = true;
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(foodName.getText()) && !TextUtils.isEmpty(foodPrice.getText()) && image != null && !selectedRadio.equals("None"))  {
                    if(!edit) {
                        if (db.insertMenu(image, foodName.getText().toString(), selectedRadio, Integer.parseInt(foodPrice.getText().toString()))) {
                            refreshTable();
                            Toast.makeText(getApplicationContext(), "Inserted Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(editMenu.this, foodName.getText().toString()+" alreay exists!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        if (db.updateMenu(Integer.parseInt(foodIDAL.get(highlightPosition)),image, foodName.getText().toString(), selectedRadio, Integer.parseInt(foodPrice.getText().toString()))) {
                            edit=false;
                            editDescription.setText("Add Food to Menu");
                            clearETs();
                            unselectfromTable();
                            Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(editMenu.this, foodName.getText().toString()+" alreay exists!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(editMenu.this, "Incomplete item information", Toast.LENGTH_SHORT).show();
                }
            }
        });


        foodTableLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int positiion, long l) {

                if(highlightPosition == positiion) {
                    highlightPosition = -1;
                    editItem.setEnabled(false);
                    deleteItem.setEnabled(false);

                }
                else{
                    highlightPosition = positiion;
                    editItem.setEnabled(true);
                    deleteItem.setEnabled(true);
                }
                refreshTable();
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
                if(bitmap.getWidth() >= 4000 || bitmap.getHeight() >= 4000){
                    bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/8,bitmap.getHeight()/8,false);
                    System.out.println("pixelated");
                }
                else if(bitmap.getWidth() >= 2000 || bitmap.getHeight() >= 2000){
                    bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/4,bitmap.getHeight()/4,false);
                    System.out.println("pixelated from > 2k");
                }
                else if(bitmap.getWidth() > 1000 || bitmap.getHeight() > 1000){
                    bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/2,bitmap.getHeight()/2,false);
                    System.out.println("pixelated from > 4k");
                }
                
                System.out.println(bitmap.getWidth());


                bitmap.compress(Bitmap.CompressFormat.PNG,100, stream);
                image = stream.toByteArray();
                foodImageIV.setImageBitmap(bitmap);
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();


            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Nay error bugo ka");
            }
        }
    }

    public void editMealType(View view) {
        Boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {

            case R.id.codPayment:
                    setCheckedRB("Meal");
                break;
            case R.id.paymayaPayment:
                setCheckedRB("Dessert");
                break;
            case R.id.gcashPayment:
                setCheckedRB("Drinks");
                break;


        }

    }

    class foodTableArrayAdapter extends ArrayAdapter{
        ArrayList<String>  foodIDAL;
        ArrayList<String>  foodNameAL;
        ArrayList<String>  foodPriceAL;
        ArrayList<String>  foodTypeAL;
        ArrayList<Bitmap> foodPicAL;

        public foodTableArrayAdapter(Context context, ArrayList<String> foodIDAL,ArrayList<String> foodNameAL,ArrayList<String> foodPriceAL, ArrayList<String> foodTypeAL, ArrayList<Bitmap> foodPicAL){
            super(context,R.layout.menu_table,R.id.foodPic,foodPicAL);

            this.foodIDAL = foodIDAL;
            this.foodNameAL = foodNameAL;
            this.foodPriceAL = foodPriceAL;
            this.foodTypeAL = foodTypeAL;
            this.foodPicAL=foodPicAL;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.menu_table, parent, false);

            ImageView imageViews = (ImageView) row.findViewById(R.id.foodPic);
            TextView foodID = (TextView) row.findViewById(R.id.foodIdTV);
            TextView foodName = (TextView) row.findViewById(R.id.foodNameTV);
            TextView foodType = (TextView) row.findViewById(R.id.foodTypeTV);
            TextView foodPrice = (TextView) row.findViewById(R.id.foodPriceTV);
            LinearLayout tableRow = (LinearLayout)row.findViewById(R.id.tableRowMenu);

            foodID.setText(""+foodIDAL.get(position));
            foodName.setText(""+foodNameAL.get(position));
            foodType.setText(""+foodTypeAL.get(position));
            foodPrice.setText(""+foodPriceAL.get(position));
            imageViews.setImageBitmap(foodPicAL.get(position));

            if (highlightPosition == position) {
                tableRow.setBackgroundColor(getResources().getColor(R.color.jollibee));
                foodName.setTextColor(Color.WHITE);
                foodID.setTextColor(Color.WHITE);
                foodType.setTextColor(Color.WHITE);
                foodPrice.setTextColor(Color.WHITE);
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
                Intent intentUsers = new Intent(this,editUsers.class);
                intentUsers.putExtra("adminView",false);
                startActivity(intentUsers);
                break;
            case R.id.manageEmployee:
                Intent intentEmployees = new Intent(this,editEmployees.class);
                startActivity(intentEmployees);
                break;
            case R.id.manageAdmins:
                Intent intentAdmin = new Intent(this,editUsers.class);
                intentAdmin.putExtra("adminView",true);
                startActivity(intentAdmin);
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
            case R.id.id:
                    sort.setText("ID");
                    orderColumn="foodId";
                refreshTable();
                return true;
            case R.id.itemName:
                sort.setText("Name");
                orderColumn="foodName";
                refreshTable();
                return true;
            case R.id.itemType:
                sort.setText("Type");
                orderColumn="foodType";
                refreshTable();
                return true;
            case R.id.itemPrice:
                sort.setText("Price");
                orderColumn="foodPrice";
                refreshTable();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    public void setCheckedRB(String food){
        if(food.equals("Meal")){
            mealRB.setBackgroundResource(R.drawable.radio_checked);
            mealRB.setTextColor(Color.WHITE);

            dessertRB.setBackgroundResource(R.drawable.radio_background_shadow);
            dessertRB.setTextColor(ContextCompat.getColor(this, R.color.jollibee));
            driinksRB.setBackgroundResource(R.drawable.radio_background_shadow);
            driinksRB.setTextColor(ContextCompat.getColor(this, R.color.jollibee));
        }
        else if(food.equals("Dessert")){
            dessertRB.setBackgroundResource(R.drawable.radio_checked);
            dessertRB.setTextColor(Color.WHITE);

            mealRB.setBackgroundResource(R.drawable.radio_background_shadow);
            mealRB.setTextColor(ContextCompat.getColor(this, R.color.jollibee));
            driinksRB.setBackgroundResource(R.drawable.radio_background_shadow);
            driinksRB.setTextColor(ContextCompat.getColor(this, R.color.jollibee));
        }
        else if(food.equals("Drinks")){
            driinksRB.setBackgroundResource(R.drawable.radio_checked);
            driinksRB.setTextColor(Color.WHITE);

            dessertRB.setBackgroundResource(R.drawable.radio_background_shadow);
            dessertRB.setTextColor(ContextCompat.getColor(this, R.color.jollibee));
            mealRB.setBackgroundResource(R.drawable.radio_background_shadow);
            mealRB.setTextColor(ContextCompat.getColor(this, R.color.jollibee));
        }
        else if(food.equals("None")){
            driinksRB.setBackgroundResource(R.drawable.radio_background_shadow);
            driinksRB.setTextColor(ContextCompat.getColor(this, R.color.jollibee));
            dessertRB.setBackgroundResource(R.drawable.radio_background_shadow);
            dessertRB.setTextColor(ContextCompat.getColor(this, R.color.jollibee));
            mealRB.setBackgroundResource(R.drawable.radio_background_shadow);
            mealRB.setTextColor(ContextCompat.getColor(this, R.color.jollibee));
        }
        selectedRadio = food;


    }

    public void unselectfromTable(){
        if(highlightPosition>=0) {
            highlightPosition = -1;
            deleteItem.setEnabled(false);
            editItem.setEnabled(false);
            refreshTable();
        }
    }
    public void refreshTable(){
        String orderBy = orderColumn+" "+orderSort;
        foodIDAL = db.getColumn("menuTable","foodId",orderBy);
        foodNameAL = db.getColumn("menuTable","foodName",orderBy);
        foodPriceAL = db.getColumn("menuTable","foodPrice",orderBy);
        foodTypeAL = db.getColumn("menuTable","foodType",orderBy);
        foodPicAL = db.getPic("menuTable",orderBy,null);
        foodTableArrayAdapter foodTableArrayAdapters = new foodTableArrayAdapter(getApplicationContext(), foodIDAL, foodNameAL, foodPriceAL, foodTypeAL, foodPicAL);
        int index = foodTableLV.getFirstVisiblePosition();
        View v = foodTableLV.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - foodTableLV.getPaddingTop());
        foodTableLV.setAdapter(foodTableArrayAdapters);
        foodTableLV.setSelectionFromTop(index, top);

    }
    private void clearETs(){
        foodName.setText("");
        foodPrice.setText("");
        setCheckedRB("None");
    }

}