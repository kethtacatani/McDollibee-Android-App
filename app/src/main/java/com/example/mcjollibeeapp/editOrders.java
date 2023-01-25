package com.example.mcjollibeeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class editOrders extends AppCompatActivity {
    ListView orderLV, itemOrderLV;
    Button deleteOrder, fulfillOrder, deleteItem, completeOrder;
    BottomSheetDialog bottomSheetDialog;
    DatabaseOperations db;
    LinearLayout tableRow;
    ArrayList<String> orderIdAL, customerIdAL,dateTimeAL,attendantIdAL,paymentMethodAL,orderCostAL,statusAL;
    ArrayList<String> orderItemIdAL, itemNameAL,itemPriceAL,quantityAL,itemTotalAL;
    TextView itemDescription,itemStatus, itemName,itemPrice,quantity,itemTotal, orderTotal, sort;
    ImageView sortASCDESC;
    String orderColumn = "status",
            orderSort="DESC";
    String orderBy = orderColumn+" "+orderSort;
    int highlightPosition = -1, highlightItemPosition = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_orders);


        orderLV = (ListView) findViewById(R.id.editOrderTableLV);
        deleteOrder = (Button) findViewById(R.id.deleteOrderBtn);
        fulfillOrder = (Button) findViewById(R.id.fullFillOrder);
        sort = (TextView)findViewById(R.id.sortOrder);
        sortASCDESC = (ImageView)findViewById(R.id.sortASCDESCOrder);


        db= new DatabaseOperations(this);
        db.foreignKeyEnable(true);
        bottomSheetDialog = new BottomSheetDialog(this,R.style.BottomSheetDialog);
        itemOrderDialog();
        bottomSheetDialog.dismiss();

        orderIdAL = new ArrayList<>();
        customerIdAL = new ArrayList<>();
        dateTimeAL = new ArrayList<>();
        attendantIdAL = new ArrayList<>();
        paymentMethodAL = new ArrayList<>();
        orderCostAL = new ArrayList<>();
        statusAL = new ArrayList<>();

        orderItemIdAL = new ArrayList<>();
        itemNameAL = new ArrayList<>();
        itemPriceAL = new ArrayList<>();
        quantityAL = new ArrayList<>();
        itemTotalAL = new ArrayList<>();

        orderIdAL = db.getColumn("ordersTable","orderId",orderBy);
        customerIdAL = db.getColumn("ordersTable","id",orderBy);
        dateTimeAL = db.getColumn("ordersTable","dateTime",orderBy);
        attendantIdAL = db.getColumn("ordersTable","attendantId",orderBy);
        paymentMethodAL = db.getColumn("ordersTable","paymentMethod",orderBy);
        orderCostAL = db.getColumn("ordersTable","orderCost",orderBy);
        statusAL = db.getColumn("ordersTable","status",orderBy);

        orderTableArrayAdapter orderTableArrayAdapter = new orderTableArrayAdapter(this,orderIdAL,customerIdAL,dateTimeAL,attendantIdAL,paymentMethodAL,orderCostAL,statusAL);
        orderLV.setAdapter(orderTableArrayAdapter);

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
                getMenuInflater().inflate(R.menu.sort_order,contextMenu);
            }
        });

        orderLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(highlightPosition == position) {
                    highlightPosition = -1;
                    fulfillOrder.setEnabled(false);
                    deleteOrder.setEnabled(false);
                }
                else{
                        highlightPosition = position;
                    System.out.println(statusAL.get(position));
                        if((statusAL.get(position).equals("Pending"))) {
                            fulfillOrder.setEnabled(true);
                        }
                        else {
                            fulfillOrder.setEnabled(false);
                        }
                        deleteOrder.setEnabled(true);
                }
                refreshTable();
            }
        });

        fulfillOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.show();
                orderItemIdAL = db.getMenu("itemOrdersTable","orderId","orderId ="+orderIdAL.get(highlightPosition),null);
                itemNameAL = db.getMenu("itemOrdersTable","itemName","orderId ="+orderIdAL.get(highlightPosition),null);
                itemPriceAL = db.getMenu("itemOrdersTable","itemPrice","orderId ="+orderIdAL.get(highlightPosition),null);
                quantityAL = db.getMenu("itemOrdersTable","itemQuantity","orderId ="+orderIdAL.get(highlightPosition),null);
                itemTotalAL = db.getMenu("itemOrdersTable","itemTotal","orderId ="+orderIdAL.get(highlightPosition),null);
                itemDescription.setText("Items in Order #"+orderIdAL.get(highlightPosition));
                itemStatus.setText("Status: "+statusAL.get(highlightPosition));
                orderTotal.setText("Order Total: P"+orderCostAL.get(highlightPosition)+".00");

                itemTableArrayAdapter itemTableArrayAdapter = new itemTableArrayAdapter(getApplicationContext(),orderItemIdAL, itemNameAL,itemPriceAL,quantityAL,itemTotalAL);
                itemOrderLV.setAdapter(itemTableArrayAdapter);

            }
        });

        deleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(db.deleteTable(new String[]{orderIdAL.get(highlightPosition)},"ordersTable","orderId")){
                    Toast.makeText(editOrders.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    highlightPosition=-1;
                    refreshTable();
                }
            }
        });

        itemOrderLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(highlightItemPosition == position) {
                    highlightItemPosition = -1;
                    deleteItem.setEnabled(false);
                }
                else{
                    highlightItemPosition = position;
                    deleteItem.setEnabled(true);
                }
                refreshItemTable();
            }
        });

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(db.deleteTable(new String[]{itemNameAL.get(highlightItemPosition),orderItemIdAL.get(highlightItemPosition)},"itemOrdersTable","itemName =? AND orderId")){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("orderCost",db.getSum("itemOrdersTable","itemTotal",orderIdAL.get(highlightPosition)).get(0));
                    if(db.updateTable("ordersTable","orderId",orderIdAL.get(highlightPosition),contentValues));
                    System.out.println("id is "+orderCostAL.get(highlightPosition));


                    if(db.getSum("itemOrdersTable","itemTotal",orderIdAL.get(highlightPosition)).get(0)==null){
                        if(db.deleteTable(new String[]{orderIdAL.get(highlightPosition)},"ordersTable","orderId"));
                        bottomSheetDialog.dismiss();
                    }
                    unselectFromTable();
                    refreshTable();
                    itemStatus.setText("Status: "+statusAL.get(highlightPosition));
                    orderTotal.setText("Order Total: P"+orderCostAL.get(highlightPosition)+".00");
                }
            }
        });
        completeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("status","Complete");
                if(db.updateTable("ordersTable","orderId",orderIdAL.get(highlightPosition),contentValues)){
                    highlightPosition=-1;
                    refreshTable();
                    bottomSheetDialog.dismiss();
                }

            }
        });








    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.orderIdSort:
                sort.setText("Order ID");
                orderColumn="orderId";
                refreshTable();
                return true;
            case R.id.customerIdSort:
                sort.setText("Customer ID");
                orderColumn="id";
                refreshTable();
                return true;
            case R.id.dateTimeSort:
                sort.setText("Date and Time");
                orderColumn="dateTime";
                refreshTable();
                return true;
            case R.id.attendantIdSort:
                sort.setText("Attendant ID No");
                orderColumn="attendantId";
                refreshTable();
                return true;
            case R.id.paymentMethodSort:
                sort.setText("Payment Method");
                orderColumn="paymentMethod";
                refreshTable();
                return true;
            case R.id.orderCOstSort:
                sort.setText("Order Cost");
                orderColumn="orderCost";
                refreshTable();
                return true;
            case R.id.statusSort:
                sort.setText("Status");
                orderColumn="status";
                refreshTable();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    class orderTableArrayAdapter extends ArrayAdapter {
        ArrayList<String> orderIdAL, customerIdAL,dateTimeAL,attendantIdAL,paymentMethodAL,orderCostAL,statusAL;

        public orderTableArrayAdapter(Context context, ArrayList<String> orderId,ArrayList<String> customerId, ArrayList<String> dateTime, ArrayList<String> attendantId, ArrayList<String> paymentMethod
                , ArrayList<String> orderCost, ArrayList<String> status){
            super(context,R.layout.order_table_layout,R.id.orderIdOrder,orderId);

            this.orderIdAL = orderId;
            this.customerIdAL = customerId;
            this.dateTimeAL = dateTime;
            this.attendantIdAL = attendantId;
            this.paymentMethodAL = paymentMethod;
            this.orderCostAL = orderCost;
            this.statusAL = status;


        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.order_table_layout, parent, false);

            TextView orderId = (TextView) row.findViewById(R.id.orderIdOrders);
            TextView customerId = (TextView) row.findViewById(R.id.customerIdOrders);
            TextView dateTime = (TextView) row.findViewById(R.id.dateAndTimeOrders);
            TextView attendantId = (TextView) row.findViewById(R.id.attendantIdOrders);
            TextView paymentMethod = (TextView) row.findViewById(R.id.paymentMethodOrders);
            TextView orderCost = (TextView) row.findViewById(R.id.orderCostOrders);
            TextView status = (TextView) row.findViewById(R.id.statusOrders);
            LinearLayout tableRow = (LinearLayout)row.findViewById(R.id.orderRow);

            orderId.setText(""+orderIdAL.get(position));
            customerId.setText(""+customerIdAL.get(position));
            dateTime.setText(""+dateTimeAL.get(position));
            attendantId.setText(""+attendantIdAL.get(position));
            paymentMethod.setText(""+paymentMethodAL.get(position));
            orderCost.setText("₱"+orderCostAL.get(position));
            status.setText(""+statusAL.get(position));

            if (highlightPosition == position) {
                tableRow.setBackgroundColor(getResources().getColor(R.color.jollibee));
                orderId.setTextColor(Color.WHITE);
                customerId.setTextColor(Color.WHITE);
                dateTime.setTextColor(Color.WHITE);
                attendantId.setTextColor(Color.WHITE);
                paymentMethod.setTextColor(Color.WHITE);
                orderCost.setTextColor(Color.WHITE);
                status.setTextColor(Color.WHITE);

            }

            return row;

        }
    }

    class itemTableArrayAdapter extends ArrayAdapter {
        ArrayList<String> orderItemIdAL, itemNameAL,itemPriceAL,quantityAL,itemTotalAL;

        public itemTableArrayAdapter(Context context, ArrayList<String> orderItemIdAL,ArrayList<String> itemNameAL, ArrayList<String> itemPriceAL, ArrayList<String> quantityAL, ArrayList<String> itemTotalAL) {
            super(context,R.layout.item_orders_layout,R.id.itemNameItem,itemNameAL);

            this.orderItemIdAL = orderItemIdAL;
            this.itemNameAL = itemNameAL;
            this.itemPriceAL = itemPriceAL;
            this.quantityAL = quantityAL;
            this.itemTotalAL = itemTotalAL;


        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.item_orders_layout, parent, false);

            itemName = (TextView)row.findViewById(R.id.itemNameItem);
            itemPrice = (TextView)row.findViewById(R.id.itemPriceItem);
            quantity = (TextView)row.findViewById(R.id.quantityItem);
            itemTotal = (TextView)row.findViewById(R.id.itemTotalItem);
            tableRow = (LinearLayout)row.findViewById(R.id.itemOrdersItem);


            itemName.setText(""+itemNameAL.get(position));
            itemPrice.setText("₱"+itemPriceAL.get(position));
            quantity.setText(""+quantityAL.get(position));
            itemTotal.setText("₱"+itemTotalAL.get(position));

            if (highlightItemPosition == position) {
                tableRow.setBackgroundColor(getResources().getColor(R.color.jollibee));
                itemName.setTextColor(Color.WHITE);
                itemPrice.setTextColor(Color.WHITE);
                quantity.setTextColor(Color.WHITE);
                itemTotal.setTextColor(Color.WHITE);
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


    public void refreshTable(){
        String orderBy = orderColumn+" "+orderSort;
        System.out.println(orderBy);
        orderIdAL = db.getColumn("ordersTable","orderId",orderBy);
        customerIdAL = db.getColumn("ordersTable","id",orderBy);
        dateTimeAL = db.getColumn("ordersTable","dateTime",orderBy);
        attendantIdAL = db.getColumn("ordersTable","attendantId",orderBy);
        paymentMethodAL = db.getColumn("ordersTable","paymentMethod",orderBy);
        orderCostAL = db.getColumn("ordersTable","orderCost",orderBy);
        statusAL = db.getColumn("ordersTable","status",orderBy);

        orderTableArrayAdapter orderTableArrayAdapter = new orderTableArrayAdapter(this,orderIdAL,customerIdAL,dateTimeAL,attendantIdAL,paymentMethodAL,orderCostAL,statusAL);
        int index = orderLV.getFirstVisiblePosition();
        View v = orderLV.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - orderLV.getPaddingTop());
        orderLV.setAdapter(orderTableArrayAdapter);
        orderLV.setSelectionFromTop(index, top);
    }

    public void itemOrderDialog(){
        View dialog = getLayoutInflater().inflate(R.layout.orders_bottom_sheet,null,false);
        bottomSheetDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


        itemDescription = (TextView)dialog.findViewById(R.id.orderDescriptiom);
        itemStatus = (TextView)dialog.findViewById(R.id.orderStatus);
        deleteItem = (Button) dialog.findViewById(R.id.deleteOrderItem);
        completeOrder = (Button) dialog.findViewById(R.id.completeOrderItem);
        orderTotal = (TextView)dialog.findViewById(R.id.orderTotalItem);
        itemOrderLV =(ListView) dialog.findViewById(R.id.itemTableLV);


        bottomSheetDialog.setContentView(dialog);
    }
     public void  refreshItemTable(){
         orderItemIdAL = db.getMenu("itemOrdersTable","orderId","orderId ="+orderIdAL.get(highlightPosition),null);
         itemNameAL = db.getMenu("itemOrdersTable","itemName","orderId ="+orderIdAL.get(highlightPosition),null);
         itemPriceAL = db.getMenu("itemOrdersTable","itemPrice","orderId ="+orderIdAL.get(highlightPosition),null);
         quantityAL = db.getMenu("itemOrdersTable","itemQuantity","orderId ="+orderIdAL.get(highlightPosition),null);
         itemTotalAL = db.getMenu("itemOrdersTable","itemTotal","orderId ="+orderIdAL.get(highlightPosition),null);

         itemTableArrayAdapter itemTableArrayAdapter = new itemTableArrayAdapter(getApplicationContext(),orderItemIdAL, itemNameAL,itemPriceAL,quantityAL,itemTotalAL);
         int index = itemOrderLV.getFirstVisiblePosition();
         View v = itemOrderLV.getChildAt(0);
         int top = (v == null) ? 0 : (v.getTop() - itemOrderLV.getPaddingTop());
         itemOrderLV.setAdapter(itemTableArrayAdapter);
         itemOrderLV.setSelectionFromTop(index, top);

     }

     public void unselectFromTable(){
        deleteItem.setEnabled(false);
        deleteOrder.setEnabled(false);
        fulfillOrder.setEnabled(false);
         highlightItemPosition= -1;
        refreshItemTable();

     }
}