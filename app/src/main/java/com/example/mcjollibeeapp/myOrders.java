package com.example.mcjollibeeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class myOrders extends AppCompatActivity {
    ListView orderedFoodLV;
    ArrayList<String> orderIdAL,orderStatusIdAL;
    ArrayList<String> orderCostAL;
    ArrayList<Bitmap> orderPicAL, itemPicAL;
    DatabaseOperations db;
    ArrayList<String> itemNameAL,itemPriceAL,quantityAL;
    String userID;
    ImageView back;
    BottomSheetDialog bottomSheetDialog;
    TextView itemName,orderIDTV;
    TextView itemPrice;
    TextView itemQty ;
    ImageView itemPic;
    ListView orderedItemLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_my_orders);
        
        bottomSheetDialog = new BottomSheetDialog(this,R.style.BottomSheetDialog);
        itemOrderedDialog();
        bottomSheetDialog.dismiss();
        orderedFoodLV = (ListView) findViewById(R.id.orderedFoodListViewOrdered);
        db= new DatabaseOperations(this);
        back = (ImageView)findViewById(R.id.backOderFood);

        orderIdAL = new ArrayList<>();
        orderStatusIdAL = new ArrayList<>();
        orderCostAL = new ArrayList<>();
        orderPicAL = new ArrayList<>();

        itemNameAL = new ArrayList<>();
        itemPriceAL = new ArrayList<>();
        quantityAL = new ArrayList<>();
        itemPicAL = new ArrayList<>();


        userID = getIntent().getStringExtra("id");
        orderIdAL = db.getMenu("ordersTable","orderId","id="+userID,"status DESC");
        orderStatusIdAL= db.getMenu("ordersTable","status","id="+userID,"status DESC");
        orderCostAL = db.getMenu("ordersTable","orderCost","id="+userID,"status DESC");
        orderPicAL = db.getPic("ordersTable","status DESC","id="+userID);
        orderedFoodArrayList orderedFoodArrayList = new orderedFoodArrayList(getApplicationContext(),orderIdAL,orderStatusIdAL,orderPicAL,orderCostAL);
        orderedFoodLV.setAdapter(orderedFoodArrayList);
      



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                intent.putExtra("id",userID);
                startActivity(intent);
            }
        });

        orderedFoodLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int highlightPosition, long l) {
                bottomSheetDialog.show();
                orderIDTV.setText("Order #"+orderIdAL.get(highlightPosition));
                itemNameAL = db.getMenu("itemOrdersTable","itemName","orderId ="+orderIdAL.get(highlightPosition),null);
                itemPriceAL = db.getMenu("itemOrdersTable","itemPrice","orderId ="+orderIdAL.get(highlightPosition),null);
                quantityAL = db.getMenu("itemOrdersTable","itemQuantity","orderId ="+orderIdAL.get(highlightPosition),null);
                for(String name: itemNameAL){
                    itemPicAL.add(db.getPic("menuTable",null,"foodName="+"\""+name+"\"").get(0));
                }
                itemOrderedTableArrayAdapter itemOrderedTableArrayAdapter = new itemOrderedTableArrayAdapter(getApplicationContext(),itemNameAL, itemPriceAL,quantityAL,itemPicAL);
                orderedItemLV.setAdapter(itemOrderedTableArrayAdapter);
            }
        });

    }

    class orderedFoodArrayList extends ArrayAdapter {
        ArrayList<String> orderIdAL,orderStatusAL;
        ArrayList<String> orderCostAL;
        ArrayList<Bitmap> orderPicAL;

        public orderedFoodArrayList(Context context, ArrayList<String> orderIdAL, ArrayList<String> orderStatusIdAL, ArrayList<Bitmap> orderPicAL, ArrayList<String> orderCostAL){
            super(context,R.layout.orderedfoodcustom,R.id.orderIdOrdered,orderIdAL);

            this.orderIdAL = orderIdAL;
            this.orderStatusAL=orderStatusIdAL;
            this.orderCostAL = orderCostAL;
            this.orderPicAL = orderPicAL;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.orderedfoodcustom, parent, false);

            ImageView imageViews = (ImageView) row.findViewById(R.id.orderPicOrders);
            TextView orderId = (TextView) row.findViewById(R.id.orderIdOrdered);
            TextView orderCost = (TextView) row.findViewById(R.id.orderCostOrdered);
            TextView orderStatus = (TextView) row.findViewById(R.id.orderStatusOrdered);
            TextView orderReceived= (TextView)row.findViewById(R.id.orderReceivedOrdered);

            orderId.setText("Oder ID: "+orderIdAL.get(position));
            orderCost.setText("₱"+orderCostAL.get(position)+".00");
            orderStatus.setText(""+orderStatusAL.get(position));
            imageViews.setImageBitmap(orderPicAL.get(position));


            if(orderStatusAL.get(position)=="Complete"){
                orderReceived.setEnabled(false);
                System.out.println(orderStatusAL.get(position));
            }

            orderReceived.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!orderReceived.getText().toString().equals("Complete")) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("status", "Complete");
                        if (db.updateTable("ordersTable", "orderId", orderIdAL.get(position), contentValues)) {
                            refreshOrdered();
                        }
                    }
                }
            });


            return row;

        }

    }

    class itemOrderedTableArrayAdapter extends ArrayAdapter {
        ArrayList<String>  itemNameAL,itemPriceAL,quantityAL;
        ArrayList<Bitmap> itemPicAL;

        public itemOrderedTableArrayAdapter(Context context, ArrayList<String> itemNameAL, ArrayList<String> itemPriceAL, ArrayList<String> quantityAL, ArrayList<Bitmap> itemPicAL) {
            super(context,R.layout.ordered_food_item,R.id.itemNameOrdered,itemNameAL);

            this.itemNameAL = itemNameAL;
            this.itemPriceAL = itemPriceAL;
            this.quantityAL = quantityAL;
            this.itemPicAL = itemPicAL;


        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.ordered_food_item, parent, false);

            TextView itemName = (TextView)row.findViewById(R.id.itemNameOrdered);
            TextView itemPrice  = (TextView)row.findViewById(R.id.itemPriceOrdered);
            TextView itemQty  = (TextView)row.findViewById(R.id.qtyOrdered);
            ImageView itemPic  = (ImageView) row.findViewById(R.id.orderPicItem);



            itemName.setText(""+itemNameAL.get(position));
            itemPrice.setText("₱"+itemPriceAL.get(position));
            itemQty.setText(""+quantityAL.get(position)+"x");
            itemPic.setImageBitmap(itemPicAL.get(position));


            return row;

        }
    }

    public void refreshOrdered(){
        userID = getIntent().getStringExtra("id");
        orderIdAL = db.getMenu("ordersTable","orderId","id="+userID,"status DESC");
        orderStatusIdAL= db.getMenu("ordersTable","status","id="+userID,"status DESC");
        orderCostAL = db.getMenu("ordersTable","orderCost","id="+userID,"status DESC");
        orderPicAL = db.getPic("ordersTable","status DESC","id="+userID);
        orderedFoodArrayList orderedFoodArrayList = new orderedFoodArrayList(getApplicationContext(),orderIdAL,orderStatusIdAL,orderPicAL,orderCostAL);
        orderedFoodLV.setAdapter(orderedFoodArrayList);
    }

    public void itemOrderedDialog(){
            View dialog = getLayoutInflater().inflate(R.layout.ordered_food_bottom,null,false);
            bottomSheetDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

             orderIDTV = (TextView)dialog.findViewById(R.id.orderItemIDOrdered);
             orderedItemLV = (ListView)dialog.findViewById(R.id.orderedItemLV);


            bottomSheetDialog.setContentView(dialog);
    }
}