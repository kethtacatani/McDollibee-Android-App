package com.example.mcjollibeeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.InetAddresses;
import android.net.Uri;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    DatabaseOperations db;
    ListView chooseMealLV, selectedMealLV;
    ArrayList<String>  mealPriceAL,mealNameAL;
    ArrayList<String>  selectedPriceAL,selectedNameAL;
    ArrayList<Bitmap> selectedPicAL;
    ArrayList<Bitmap> mealPicAL, userPicAL;
    ArrayList<Integer> mealQtyAL;
    ArrayList<Double> mealCostAL;
    ArrayList<String> userFirstNameAL, userLastNameAL;
    TextView mealsubTotal,fullName;
    Double subTotal=0.00;
    ImageView sortASCDESC,settings, orderedFood;
    TextView sort, nextMealBtn;
    String orderColumn = "foodPrice",
            orderSort="ASC";
    String orderBy = orderColumn+" "+orderSort;
    CheckBox mealCB,drinksCB,dessertCB;
    String mealWhere="Meal", drinksWhere="null", dessertWhere="null";
    String whereArgs = "foodType = \""+mealWhere+"\"";
    CircleImageView profilePic;
    double orderTotal=0;

    int lastValue = 0;
    boolean customQty = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        nextMealBtn = (TextView)findViewById(R.id.mealNextBtn);
        chooseMealLV = (ListView)findViewById(R.id.chooseMealListView);
        selectedMealLV =(ListView)findViewById(R.id.selectedMealListView);
        mealsubTotal = (TextView)findViewById(R.id.mealSubTotal);
        sort = (TextView)findViewById(R.id.sort);
        sortASCDESC = (ImageView)findViewById(R.id.sortASCDESCBuy);
        mealCB = (CheckBox)findViewById(R.id.editFoodMealCB);
        drinksCB = (CheckBox)findViewById(R.id.editFoodDrinksCB);
        dessertCB = (CheckBox)findViewById(R.id.editFoodDessertCB);
        profilePic = (CircleImageView) findViewById(R.id.imageView2);
        fullName = (TextView)findViewById(R.id.fullName);
        settings = (ImageView)findViewById(R.id.settings);
        orderedFood = (ImageView)findViewById(R.id.orderedFoodHome);

        mealNameAL= new ArrayList<String>();
        mealPicAL= new ArrayList<Bitmap>();
        mealPriceAL= new ArrayList<String>();
        mealQtyAL = new ArrayList<Integer>();
        mealCostAL = new ArrayList<Double>();

        userFirstNameAL = new ArrayList<String>();
        userLastNameAL = new ArrayList<String>();
        userPicAL = new ArrayList<Bitmap>();

        System.out.println("commit sample");
        System.out.println("commite received");
        System.out.println("confirm");


        db= new DatabaseOperations(this);
        String userID = getIntent().getStringExtra("id");
        userFirstNameAL = db.getUser("firstName",null,"id="+userID);
        userLastNameAL = db.getUser("lastName",null,"id="+userID);
        userPicAL= db.getPic("userTable",null,userID);
        fullName.setText(""+userFirstNameAL.get(0)+" "+userLastNameAL.get(0));
        profilePic.setImageBitmap(userPicAL.get(0));

        mealPicAL = db.getPic("menuTable",orderBy,whereArgs);
        mealNameAL = db.getMenu("menuTable","foodName",whereArgs,orderBy);
        mealPriceAL= db.getMenu("menuTable","foodPrice",whereArgs,orderBy);

        selectedNameAL =new ArrayList<String>();
        selectedPriceAL =new ArrayList<String>();
        selectedPicAL =new ArrayList<Bitmap>();

        mealCB.callOnClick();
        dessertCB.callOnClick();
        drinksCB.callOnClick();



        mealArrayAdapter  mealArrayAdapter= new mealArrayAdapter(getApplicationContext(),mealNameAL,mealPriceAL,mealPicAL);
        chooseMealLV.setAdapter(mealArrayAdapter);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), settings.class);
                intent.putExtra("id",userID);
                startActivity(intent);
            }
        });

        orderedFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), myOrders.class);
                intent.putExtra("id",userID);
                startActivity(intent);
            }
        });

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
                getMenuInflater().inflate(R.menu.sort_items,contextMenu);
            }
        });

        chooseMealLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(!selectedNameAL.contains(""+mealNameAL.get(position))) {
                    selectedNameAL.add("" + mealNameAL.get(position));
                    selectedPriceAL.add("" + mealPriceAL.get(position));
                    selectedPicAL.add(mealPicAL.get(position));
                    mealQtyAL.add(1);
                    mealCostAL.add(Double.parseDouble(mealPriceAL.get(position)));
                    subTotal = Double.parseDouble(mealPriceAL.get(position)) + subTotal;
                    displaySubTotal();
                    selectedArrayAdapter selectedArrayAdapter = new selectedArrayAdapter(getApplicationContext(), selectedNameAL, selectedPriceAL, selectedPicAL, mealQtyAL, mealCostAL);
                    selectedMealLV.setAdapter(selectedArrayAdapter);
                }
                else{
                    Toast.makeText(HomeActivity.this, "You already selected that meal", Toast.LENGTH_SHORT).show();
                }
            }
        });

        nextMealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(this,)
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateSdf = new SimpleDateFormat("MMM/dd/yyyy");
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String currentTime = sdf.format(calendar.getTime());
                String currentDate = dateSdf.format(calendar.getTime());

                String date= ""+currentDate+" "+currentTime;
                if(orderTotal >0) {
                    Intent intent = new Intent(getApplicationContext(),checkOut.class);
                    intent.putExtra("mealName",selectedNameAL);
                    intent.putExtra("mealPrice",selectedPriceAL);
//                    intent.putExtra("mealPic",menuPic);
                    intent.putExtra("mealQty",mealQtyAL);
                    intent.putExtra("mealCost",mealCostAL);
                    intent.putExtra("subTotal",String.valueOf(orderTotal));
                    intent.putExtra("id",userID);

                    startActivity(intent);


                }
                else{
                    Toast.makeText(HomeActivity.this, "Please order something first", Toast.LENGTH_SHORT).show();
                }


            }
        });




    }
    public void editFoodType(View view) {
        Boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {

            case R.id.editFoodMealCB:
                if(checked){
                    mealCB.setBackgroundResource(R.drawable.radio_bacground_no_shadow);
                    mealCB.setTextColor(ContextCompat.getColor(this,R.color.jollibee));
                    mealWhere="\"Meal\"";
                }
                else{
                    mealCB.setBackgroundResource(R.drawable.radio_home);
                    mealCB.setTextColor(ContextCompat.getColor(this, R.color.white));
                    mealWhere="null";
                }
                System.out.println("yawa1");
                refreshTable();
                break;
            case R.id.editFoodDessertCB:
                if(checked){
                    dessertCB.setBackgroundResource(R.drawable.radio_bacground_no_shadow);
                    dessertCB.setTextColor(ContextCompat.getColor(this,R.color.jollibee));
                    dessertWhere = "\"Dessert\"";
                }
                else{
                    dessertCB.setBackgroundResource(R.drawable.radio_home);
                    dessertCB.setTextColor(ContextCompat.getColor(this, R.color.white));
                    dessertWhere = "null";
                }
                System.out.println("yawa2");
                refreshTable();
                break;
            case R.id.editFoodDrinksCB:
                if(checked){
                    drinksCB.setBackgroundResource(R.drawable.radio_bacground_no_shadow);
                    drinksCB.setTextColor(ContextCompat.getColor(this,R.color.jollibee));
                    drinksWhere="\"Drinks\"";
                }
                else{
                    drinksCB.setBackgroundResource(R.drawable.radio_home);
                    drinksCB.setTextColor(ContextCompat.getColor(this, R.color.white));
                    drinksWhere="null";
                }
                System.out.println("yawa3");
                refreshTable();
                break;
            default:
                mealWhere="null";
                dessertWhere = "null";
                drinksWhere="null";
                refreshTable();
                break;

        }

    }


    class mealArrayAdapter extends ArrayAdapter{
        ArrayList<String>  mealPriceAL,mealNameAL;
        ArrayList<Bitmap> mealPicAL;

        public mealArrayAdapter(Context context, ArrayList<String> mealNameAL,ArrayList<String> mealPriceAL, ArrayList<Bitmap> mealPicAL){
            super(context,R.layout.meal_menu_info,R.id.mealPicChoose,mealPicAL);

            this.mealNameAL = mealNameAL;
            this.mealPriceAL=mealPriceAL;
            this.mealPicAL = mealPicAL;



        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.meal_menu_info, parent, false);

            ImageView imageViews = (ImageView) row.findViewById(R.id.mealPicChoose);
            TextView mealName = (TextView) row.findViewById(R.id.mealNameChoose);
            TextView mealPrice = (TextView) row.findViewById(R.id.mealPriceChoose);


            mealPrice.setText("₱"+mealPriceAL.get(position));
            mealName.setText(""+mealNameAL.get(position));
            System.out.println("position is "+mealPicAL.get(position));
            imageViews.setImageBitmap(mealPicAL.get(position));

            return row;

        }
    }

    class selectedArrayAdapter extends ArrayAdapter{
        ArrayList<String>  selectedPriceAL,selectedNameAL;
        ArrayList<Bitmap> selectedPicAL;
        ArrayList<Integer> mealQtyAL;
        ArrayList<Double> mealCostAL;

        public selectedArrayAdapter(Context context, ArrayList<String> selectedNameAL,ArrayList<String> selectedPriceAL, ArrayList<Bitmap> selectedPicAL, ArrayList<Integer> mealQtyAL,ArrayList<Double> mealCostAL){
            super(context,R.layout.meal_menu_selected,R.id.mealPicSelected,selectedPicAL);

            this.selectedNameAL = selectedNameAL;
            this.selectedPriceAL=selectedPriceAL;
            this.selectedPicAL = selectedPicAL;
            this.mealQtyAL = mealQtyAL;
            this.mealCostAL = mealCostAL;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.meal_menu_selected, parent, false);

            ImageView imageViews = (ImageView) row.findViewById(R.id.mealPicSelected);
            TextView mealName = (TextView) row.findViewById(R.id.mealNameChoose);
            TextView mealPrice = (TextView) row.findViewById(R.id.mealPriceChoose);
            ImageView minus = (ImageView) row.findViewById(R.id.subMealQty);
            ImageView add = (ImageView) row.findViewById(R.id.addMealQty);
            ImageView delete = (ImageView) row.findViewById(R.id.deleteMeal);
            EditText qty = (EditText) row.findViewById(R.id.customMealQty);

            mealPrice.setText("₱"+mealCostAL.get(position)+"0");
            mealName.setText(""+selectedNameAL.get(position));
            qty.setText(""+mealQtyAL.get(position));
            imageViews.setImageBitmap(selectedPicAL.get(position));



            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Integer.parseInt(qty.getText().toString()) > 1){
                        qty.setText(""+(Integer.parseInt(qty.getText().toString())-1));
                        mealQtyAL.set(position,Integer.parseInt(qty.getText().toString()));
                        mealCostAL.set(position,Double.parseDouble(selectedPriceAL.get(position))*Double.parseDouble(qty.getText().toString()));
                        mealPrice.setText("₱"+(Double.parseDouble(selectedPriceAL.get(position))*Double.parseDouble(qty.getText().toString()))+"0");
                        displaySubTotal();
                    }
                }
            });
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int qtyInt;
                    if (TextUtils.isEmpty(qty.getText())) {
                        qtyInt = 0;
                    }
                    else{
                        qtyInt= Integer.parseInt(qty.getText().toString());
                    }

                    qty.setText(""+(qtyInt+1));
                    mealQtyAL.set(position,Integer.parseInt(qty.getText().toString()));
                    mealCostAL.set(position,Double.parseDouble(selectedPriceAL.get(position))*Double.parseDouble(qty.getText().toString()));
                    mealPrice.setText("₱"+(Double.parseDouble(selectedPriceAL.get(position))*Double.parseDouble(qty.getText().toString()))+"0");
                    displaySubTotal();
                }
            });



            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.printf("delete");
                    deleteSelectedFunc(position);
                }
            });

            qty.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if(keyEvent.getAction() == keyEvent.ACTION_DOWN){
                        if (!TextUtils.isEmpty(qty.getText())) {
                            mealCostAL.set(position,0.0);
                            mealPrice.setText("₱" +selectedPriceAL.get(position)+".00");
                            displaySubTotal();
                        }
                    }
                    if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        if (!TextUtils.isEmpty(qty.getText())) {
                            if(Double.parseDouble(qty.getText().toString()) <=50) {
                                mealQtyAL.set(position, Integer.parseInt(qty.getText().toString()));
                                mealPrice.setText("₱" + (Double.parseDouble(selectedPriceAL.get(position)) * Double.parseDouble(qty.getText().toString())) + "0");
                                mealCostAL.set(position, Double.parseDouble(selectedPriceAL.get(position)) * Double.parseDouble(qty.getText().toString()));
                                displaySubTotal();
                            }
                            else{
                                qty.setText("50");
                                mealQtyAL.set(position, Integer.parseInt(qty.getText().toString()));
                                mealPrice.setText("₱" + (Double.parseDouble(selectedPriceAL.get(position)) * Double.parseDouble(qty.getText().toString()))+"0");
                                mealCostAL.set(position, Double.parseDouble(selectedPriceAL.get(position)) * Double.parseDouble(qty.getText().toString()));
                                qty.setSelection(qty.length());
                                displaySubTotal();
                            }
                        }
                    }

                    return false;
                }
            });


            return row;

        }
    }

    public void deleteSelectedFunc(int position){
        selectedNameAL.remove(position);
        selectedPriceAL.remove(position);
        selectedPicAL.remove(position);
        mealQtyAL.remove(position);
        mealCostAL.remove(position);
        displaySubTotal();
        selectedArrayAdapter  selectedArrayAdapter= new selectedArrayAdapter(getApplicationContext(),selectedNameAL,selectedPriceAL,selectedPicAL, mealQtyAL,mealCostAL);
        selectedMealLV.setAdapter(selectedArrayAdapter);
        Toast.makeText(HomeActivity.this, "Removed ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.itemNameBuy:
                sort.setText("Name");
                orderColumn="foodName";
                refreshTable();
                return true;
            case R.id.itemPriceBuy:
                sort.setText("Price");
                orderColumn="foodPrice";
                refreshTable();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    public void refreshTable(){
        String orderBy = orderColumn+" "+orderSort;
        String whereArgs="foodType ="+mealWhere+" OR foodType="+dessertWhere+" OR foodType="+drinksWhere+"";
        mealPicAL = db.getPic("menuTable",orderBy,whereArgs);
        mealNameAL = db.getMenu("menuTable","foodName",whereArgs,orderBy);
        mealPriceAL= db.getMenu("menuTable","foodPrice",whereArgs,orderBy);
        mealArrayAdapter  mealArrayAdapter= new mealArrayAdapter(getApplicationContext(),mealNameAL,mealPriceAL,mealPicAL);
        chooseMealLV.setAdapter(mealArrayAdapter);

    }
    public  void displaySubTotal(){
        if(!mealCostAL.isEmpty()) {
            double sum = 0.0;
            for (double num : mealCostAL) {
                sum += num;
            }
            mealsubTotal.setText("₱" + sum + "0");
            orderTotal= sum;
        }
        else{
            mealsubTotal.setText("₱0.00");
        }
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }


}