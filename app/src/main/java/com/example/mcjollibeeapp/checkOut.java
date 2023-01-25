package com.example.mcjollibeeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class checkOut extends AppCompatActivity {
    ImageView back,settings;
    TextView fullName, contactNo,address,orderSubTotal,paymentMethod,subTotalDetails,discountDetails,totalPayment,placeOrder;
    TextInputEditText customAddress;
    CheckBox grabFood, foodPanda,lalaFood;
    RadioButton cod,gcash,paymaya;
    ListView orderedFoodLV;
    ArrayList<String>  menuNameAL, menuPriceAL;
    ArrayList<Double> menuCostAL;
    ArrayList<Integer> menuQtyAL;
    ArrayList<Bitmap> menuPicAL;
    DatabaseOperations db;
    String userID;
    String age;
    double subTotal = 0.0,totalPayable;
    String selectedPayment = "", courier="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_check_out);

        fullName = (TextView) findViewById(R.id.fullNamePlace);
        contactNo = (TextView) findViewById(R.id.contactNoPlace);
        address = (TextView) findViewById(R.id.addressPlace);
        customAddress = (TextInputEditText) findViewById(R.id.profileAddress);
        orderSubTotal = (TextView) findViewById(R.id.subTotalPlace);
        subTotalDetails = (TextView) findViewById(R.id.subTotalDetails);
        paymentMethod = (TextView) findViewById(R.id.paymentMethodDetails);
        totalPayment = (TextView) findViewById(R.id.totalPaymentDetails);
        placeOrder = (TextView) findViewById(R.id.placeOrder);
        grabFood = (CheckBox) findViewById(R.id.grabFood);
        foodPanda = (CheckBox) findViewById(R.id.foodPanda);
        lalaFood = (CheckBox) findViewById(R.id.lalaFood);
        cod = (RadioButton) findViewById(R.id.codPayment);
        gcash = (RadioButton) findViewById(R.id.gcashPayment);
        paymaya = (RadioButton) findViewById(R.id.paymayaPayment);
        orderedFoodLV = (ListView) findViewById(R.id.orderedFoodListView);
        back = (ImageView)findViewById(R.id.backPlaceOrder);
        settings = (ImageView)findViewById(R.id.settingsPlace);

        menuNameAL = new ArrayList<>();
        menuPriceAL = new ArrayList<>();
        menuQtyAL = new ArrayList<>();
        menuCostAL = new ArrayList<>();
        menuPicAL = new ArrayList<>();
        db = new DatabaseOperations(this);

        menuNameAL= (ArrayList<String>)getIntent().getSerializableExtra("mealName");
        menuPriceAL= (ArrayList<String>)getIntent().getSerializableExtra("mealPrice");
        menuQtyAL= (ArrayList<Integer>)getIntent().getSerializableExtra("mealQty");
        menuCostAL= (ArrayList<Double>)getIntent().getSerializableExtra("mealCost");
        System.out.println("menu cost "+menuCostAL);
        orderSubTotal.setText("₱"+subTotal);
        for (String menuName : menuNameAL) {
            menuPicAL.add(db.getPic("menuTable",null,"foodName=\""+menuName+"\"").get(0));
        }
        orderFoodArrayList orderFoodArrayList = new orderFoodArrayList(getApplicationContext(),menuNameAL,menuPriceAL,menuPicAL,menuQtyAL, menuCostAL);
        orderedFoodLV.setAdapter(orderFoodArrayList);

        userID = getIntent().getStringExtra("id");
        String firstName=(db.getUser("firstName",null,"id="+userID).get(0));
        String lastName =(db.getUser("lastName",null,"id="+userID).get(0));

        fullName.setText(""+firstName+" "+lastName);
        address.setText(""+(db.getUser("address",null,"id="+userID).get(0)));
        contactNo.setText(""+(db.getUser("contactNumber",null,"id="+userID).get(0)));
        age= db.getUser("age",null,"id="+userID).get(0);
        displaySubTotal();
        viewPaymentDetails();

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),settings.class);
                intent.putExtra("id",userID);
                startActivity(intent);
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                onBackPressed();
            }
        });

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!selectedPayment.equals("") && !courier.equals("")) {
                    if (subTotal > 0) {

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat dateSdf = new SimpleDateFormat("MMM/dd/yyyy");
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                        String currentTime = sdf.format(calendar.getTime());
                        String currentDate = dateSdf.format(calendar.getTime());

                        String date = "" + currentDate + " " + currentTime;

                        int min = 1000;
                        int max = 9999;
                        int randomNum = (int) (Math.random() * (max - min + 1)) + min;
                        int orderID = Integer.parseInt("3" + randomNum);

                        ContentValues cv = new ContentValues();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        menuPicAL.get(0).compress(Bitmap.CompressFormat.PNG,100, stream);
                        byte[] image = stream.toByteArray();

                        int randomCook= 0;
                        try {
                            randomCook = Integer.parseInt(db.getRandom("attendantTable","attendantId","RANDOM()").get(0));
                        } catch (IndexOutOfBoundsException e) {
                                e.printStackTrace();
                        }

                        System.out.println("cook "+randomCook);
                        if(randomCook>0) {
                            cv.put("orderId", orderID);
                            cv.put("orderImage", image);
                            cv.put("id", Integer.parseInt(userID));
                            cv.put("dateTime", date);

                            cv.put("attendantId", randomCook);
                            cv.put("paymentMethod", selectedPayment);
                            cv.put("orderCost", totalPayable);
                            cv.put("status", "Pending");
                            db.insertOrder("ordersTable", cv);

                            ContentValues contentValues = new ContentValues();
                            int count = 0;
                            for (String mealName : menuNameAL) {

                                contentValues.put("orderId", orderID);
                                contentValues.put("itemName", mealName);
                                contentValues.put("itemPrice", menuPriceAL.get(count));
                                contentValues.put("itemQuantity", menuQtyAL.get(count));
                                contentValues.put("itemTotal", menuCostAL.get(count));
                                db.insertOrder("itemOrdersTable", contentValues);
                                count++;
                            }

                            Intent intent = new Intent(getApplicationContext(), myOrders.class);
                            intent.putExtra("id", userID);
                            startActivity(intent);
                            Toast.makeText(checkOut.this, "Inserted Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(checkOut.this, "No cook available", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    if(selectedPayment.equals("")){
                        Toast.makeText(checkOut.this, "Please select payment method", Toast.LENGTH_SHORT).show();
                    }
                    else if(courier.equals("")){
                        Toast.makeText(checkOut.this, "Please select courier", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(checkOut.this, "Please select payment method and courier", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    public void shippingOption (View view) {
        Boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.grabFood:
                if (checked){
                    grabFood.setBackgroundResource(R.drawable.border_color_selected);
                    courier="Grab Food";
                }
                else{
                    grabFood.setBackgroundResource(R.drawable.border_color);

                }
                break;
            case R.id.foodPanda:
                if (checked){
                    foodPanda.setBackgroundResource(R.drawable.border_color_selected);
                    courier="Food Panda";

                }
                else{
                    foodPanda.setBackgroundResource(R.drawable.border_color);

                }
                break;
            case R.id.lalaFood:
                if (checked){
                    lalaFood.setBackgroundResource(R.drawable.border_color_selected);
                    courier="Lala Food";
                }
                else{
                    lalaFood.setBackgroundResource(R.drawable.border_color);

                }
                break;

        }
    }
    public void paymentMethod(View view) {
        Boolean selected = ((RadioButton) view).isChecked();
        switch (view.getId()) {

            case R.id.codPayment:
                cod.setBackgroundResource(R.drawable.border_color_selected);
                gcash.setChecked(false);
                paymaya.setChecked(false);
                gcash.setBackgroundResource(R.drawable.border_color);
                paymaya.setBackgroundResource(R.drawable.border_color);
                selectedPayment="COD";
                viewPaymentDetails();
                break;
            case R.id.paymayaPayment:
                paymaya.setBackgroundResource(R.drawable.border_color_selected);
                gcash.setChecked(false);
                cod.setChecked(false);
                gcash.setBackgroundResource(R.drawable.border_color);
                cod.setBackgroundResource(R.drawable.border_color);
                selectedPayment="Maya";
                viewPaymentDetails();
                break;

            case R.id.gcashPayment:
                gcash.setBackgroundResource(R.drawable.border_color_selected);
                cod.setChecked(false);
                paymaya.setChecked(false);
                cod.setBackgroundResource(R.drawable.border_color);
                paymaya.setBackgroundResource(R.drawable.border_color);
                selectedPayment="Gcash";
                viewPaymentDetails();
                break;



        }

    }

    class orderFoodArrayList extends ArrayAdapter {
        ArrayList<String>  menuNameAL, menuPriceAL;
        ArrayList<Double> menuCostAL;
        ArrayList<Integer> menuQtyAL;
        ArrayList<Bitmap> menuPicAL;

        public orderFoodArrayList(Context context, ArrayList<String> menuName, ArrayList<String> menuPrice, ArrayList<Bitmap> menuPic,ArrayList<Integer> menuQty,ArrayList<Double> menuCost){
            super(context,R.layout.meal_menu_selected,R.id.mealPicSelected,menuPic);

            this.menuNameAL = menuName;
            this.menuPriceAL=menuPrice;
            this.menuPicAL = menuPic;
            this.menuQtyAL = menuQty;
            this.menuCostAL = menuCost;

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

            mealPrice.setText("₱"+menuCostAL.get(position)+"0");
            mealName.setText(""+menuNameAL.get(position));
            qty.setText(""+menuQtyAL.get(position));
            imageViews.setImageBitmap(menuPicAL.get(position));



            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Integer.parseInt(qty.getText().toString()) > 1){
                        qty.setText(""+(Integer.parseInt(qty.getText().toString())-1));
                        menuQtyAL.set(position,Integer.parseInt(qty.getText().toString()));
                        menuCostAL.set(position,Double.parseDouble(menuPriceAL.get(position))*Double.parseDouble(qty.getText().toString()));
                        mealPrice.setText("₱"+(Double.parseDouble(menuPriceAL.get(position))*Double.parseDouble(qty.getText().toString()))+"0");
                        displaySubTotal();
                        viewPaymentDetails();
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
                    menuQtyAL.set(position,Integer.parseInt(qty.getText().toString()));
                    menuCostAL.set(position,Double.parseDouble(menuPriceAL.get(position))*Double.parseDouble(qty.getText().toString()));
                    mealPrice.setText("₱"+(Double.parseDouble(menuPriceAL.get(position))*Double.parseDouble(qty.getText().toString()))+"0");
                    displaySubTotal();
                    viewPaymentDetails();
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
                            menuCostAL.set(position,0.0);
                            mealPrice.setText("₱" +menuPriceAL.get(position)+".00");
                            displaySubTotal();
                            viewPaymentDetails();
                        }
                    }
                    if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        if (!TextUtils.isEmpty(qty.getText())) {
                            if(Double.parseDouble(qty.getText().toString()) <=50) {
                                menuQtyAL.set(position, Integer.parseInt(qty.getText().toString()));
                                mealPrice.setText("₱" + (Double.parseDouble(menuPriceAL.get(position)) * Double.parseDouble(qty.getText().toString())) + "0");
                                menuCostAL.set(position, Double.parseDouble(menuPriceAL.get(position)) * Double.parseDouble(qty.getText().toString()));
                                displaySubTotal();
                                viewPaymentDetails();
                            }
                            else{
                                qty.setText("50");
                                menuQtyAL.set(position, Integer.parseInt(qty.getText().toString()));
                                mealPrice.setText("₱" + (Double.parseDouble(menuPriceAL.get(position)) * Double.parseDouble(qty.getText().toString())) + "0");
                                menuCostAL.set(position, Double.parseDouble(menuPriceAL.get(position)) * Double.parseDouble(qty.getText().toString()));
                                qty.setSelection(qty.length());
                                displaySubTotal();
                                viewPaymentDetails();
                            }
                        }
                    }

                    return false;
                }
            });


            return row;

        }

    }

    public  void displaySubTotal(){
        if(!menuCostAL.isEmpty()) {
            double sub = 0;
            for (double num : menuCostAL) {
                sub += num;
            }
            subTotal=sub;
            System.out.println("sub "+sub);
            orderSubTotal.setText("₱" + sub + "0");
        }
        else{
            orderSubTotal.setText("₱0.00");

        }
        System.out.println("menu cost Als"+menuCostAL);
    }

    public void deleteSelectedFunc(int position){
        menuNameAL.remove(position);
        menuPriceAL.remove(position);
        menuQtyAL.remove(position);
        menuCostAL.remove(position);
        menuPicAL.remove(position);
        displaySubTotal();
        viewPaymentDetails();
        orderFoodArrayList orderFoodArrayList = new orderFoodArrayList(getApplicationContext(),menuNameAL,menuPriceAL,menuPicAL,menuQtyAL, menuCostAL);
        orderedFoodLV.setAdapter(orderFoodArrayList);
        Toast.makeText(getApplicationContext(), "Removed ", Toast.LENGTH_SHORT).show();

    }

    public void viewPaymentDetails(){

        paymentMethod.setText(selectedPayment);
        subTotalDetails.setText(""+subTotal+"0");
        totalPayable= subTotal;
        totalPayment.setText("₱"+totalPayable+"0");
    }
}