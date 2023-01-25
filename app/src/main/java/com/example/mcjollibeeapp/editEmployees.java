package com.example.mcjollibeeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class editEmployees extends AppCompatActivity {
    ArrayList<String> employeeIDAL, employeeFirstNameAL, employeeLastNameAL,employeePositionAL,employeeAgeAL,employeeContactAL,employeeHourlyRateAL;
    ListView employeeTableLV;
    Button addEmployee, editEmployee, deleteEmployee, saveEmployee;
    TextView employeeDescritpion, sort, tableName;
    DatabaseOperations db;
    ImageView sortASCDESC;
    TextInputEditText firstName, lastName,age,position,contactNumber, employeeHourlyRate;
    String orderColumn = "attendantId",
            orderSort="ASC";
    String orderBy = orderColumn+" "+orderSort;
    int highlightEmployeePosition = -1;
    boolean edit = false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employees);

        employeeDescritpion = (TextView)findViewById(R.id.employeeDescription);
        employeeTableLV = (ListView)findViewById(R.id.employeeTableLV);
        addEmployee = (Button)findViewById(R.id.addEmployee);
        editEmployee = (Button)findViewById(R.id.editEmployeeBtn);
        deleteEmployee = (Button)findViewById(R.id.deleteEmployeeBtn);
        saveEmployee = (Button)findViewById(R.id.saveEmployeeBtn);
        firstName = (TextInputEditText)findViewById(R.id.employeeFirstNameET);
        lastName = (TextInputEditText)findViewById(R.id.employeeLastNameET);
        age = (TextInputEditText)findViewById(R.id.employeeAgeET);
        position = (TextInputEditText)findViewById(R.id.employeePositionET);
        employeeHourlyRate = (TextInputEditText)findViewById(R.id.hourlyRateET);
        contactNumber = (TextInputEditText)findViewById(R.id.employeeContactET);
        sort = (TextView) findViewById(R.id.sortEmployee);
        sortASCDESC = (ImageView)findViewById(R.id.sortASCDESC);
        tableName = (TextView) findViewById(R.id.textView4);

        db=new DatabaseOperations(this);
        db.foreignKeyEnable(true);

        employeeIDAL = new ArrayList<String>();
        employeeFirstNameAL = new ArrayList<String>();
        employeeLastNameAL = new ArrayList<String>();
        employeePositionAL = new ArrayList<String>();
        employeeAgeAL = new ArrayList<String>();
        employeeContactAL = new ArrayList<String>();
        employeeHourlyRateAL = new ArrayList<String>();

        employeeIDAL = db.getColumn("attendantTable", "attendantId",orderBy);
        employeeFirstNameAL = db.getColumn("attendantTable", "firstName",orderBy);
        employeeLastNameAL = db.getColumn("attendantTable", "lastName",orderBy);
        employeePositionAL = db.getColumn("attendantTable", "position",orderBy);
        employeeAgeAL = db.getColumn("attendantTable", "age",orderBy);
        employeeContactAL = db.getColumn("attendantTable", "contactNumber",orderBy);
        employeeHourlyRateAL = db.getColumn("attendantTable", "hourlyRate",orderBy);

        employeeTableArrayAdapter employeeTableArrayAdapter = new employeeTableArrayAdapter(getApplicationContext(),employeeIDAL,employeeFirstNameAL,employeeLastNameAL,employeePositionAL,employeeAgeAL,employeeContactAL, employeeHourlyRateAL);
        employeeTableLV.setAdapter(employeeTableArrayAdapter);

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
                refreshEmployeeTable();

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
                getMenuInflater().inflate(R.menu.sort_employee,contextMenu);
            }
        });

        employeeTableLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(highlightEmployeePosition == position) {
                    highlightEmployeePosition = -1;
                    editEmployee.setEnabled(false);
                    deleteEmployee.setEnabled(false);


                }
                else{
                    highlightEmployeePosition = position;
                    editEmployee.setEnabled(true);
                    deleteEmployee.setEnabled(true);

                }
                refreshEmployeeTable();
            }

        });

        addEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unselectfromTable();
                employeeDescritpion.setText("Add employee to Table");
                clearETs();
                edit = false;
            }
        });

        saveEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(firstName.getText()) && !TextUtils.isEmpty(lastName.getText()) && !TextUtils.isEmpty(age.getText()) && !TextUtils.isEmpty(position.getText()) && !TextUtils.isEmpty(contactNumber.getText()) && !TextUtils.isEmpty(employeeHourlyRate.getText())) {
                    if(!edit) {
                        if (db.insertAttendant(firstName.getText().toString(), lastName.getText().toString(), Integer.parseInt(age.getText().toString()), position.getText().toString(), contactNumber.getText().toString(),Integer.parseInt(employeeHourlyRate.getText().toString()))) {
                            refreshEmployeeTable();
                            Toast.makeText(getApplicationContext(), "Inserted Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        if (db.updateAttendant(Integer.parseInt(employeeIDAL.get(highlightEmployeePosition)) ,firstName.getText().toString(), lastName.getText().toString(), Integer.parseInt(age.getText().toString()), position.getText().toString(), contactNumber.getText().toString(), Integer.parseInt(employeeHourlyRate.getText().toString()))){
                            edit = false;
                            employeeDescritpion.setText("Add employee to Table");
                            clearETs();
                            unselectfromTable();
                            Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(editEmployees.this, "Incomplete item information", Toast.LENGTH_SHORT).show();
                }

            }
        });

        deleteEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id =Integer.parseInt(employeeIDAL.get(highlightEmployeePosition));
                if(db.deleteTable(new String[]{String.valueOf(id)},"attendantTable","attendantId")){
                    clearETs();
                    unselectfromTable();
                    Toast.makeText(editEmployees.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

        editEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                employeeDescritpion.setText("Edit employee "+employeeFirstNameAL.get(highlightEmployeePosition)+" in Table");
                firstName.setText(""+employeeFirstNameAL.get(highlightEmployeePosition));
                lastName.setText(""+employeeLastNameAL.get(highlightEmployeePosition));
                position.setText(""+employeePositionAL.get(highlightEmployeePosition));
                age.setText(""+employeeAgeAL.get(highlightEmployeePosition));
                contactNumber.setText(""+employeeContactAL.get(highlightEmployeePosition));
                employeeHourlyRate.setText(""+employeeHourlyRateAL.get(highlightEmployeePosition));
                edit = true;

            }
        });

    }


    class employeeTableArrayAdapter extends ArrayAdapter {
        ArrayList<String> employeeIDAL, employeeFirstNameAL, employeeLastNameAL,employeePositionAL,employeeAgeAL,employeeContactAL,employeeHourlyRateAL;

        public employeeTableArrayAdapter(Context context, ArrayList<String> employeeIDAL, ArrayList<String> employeeFirstNameAL
                ,ArrayList<String> employeeLastNameAL,ArrayList<String> employeePositionAL,ArrayList<String> employeeAgeAL,ArrayList<String> employeeContactAL,ArrayList<String> employeeHourlyRateAL){
            super(context,R.layout.attendant_layout,R.id.employeeIDTV,employeeIDAL);

            this.employeeIDAL = employeeIDAL;
            this.employeeFirstNameAL = employeeFirstNameAL;
            this.employeeLastNameAL = employeeLastNameAL;
            this.employeePositionAL = employeePositionAL;
            this.employeeAgeAL = employeeAgeAL;
            this.employeeContactAL = employeeContactAL;
            this.employeeHourlyRateAL = employeeHourlyRateAL;

        }

        @NonNull
        @Override
        public View getView(int indexPosition, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.attendant_layout, parent, false);

            TextView employeeID = (TextView) row.findViewById(R.id.employeeIDTV);
            TextView firsName = (TextView) row.findViewById(R.id.employeeFirstNameTV);
            TextView lastName = (TextView) row.findViewById(R.id.employeeLastNameTV);
            TextView position = (TextView) row.findViewById(R.id.employeePositionTV);
            TextView age = (TextView) row.findViewById(R.id.employeeAgeTV);
            TextView contactNumber = (TextView) row.findViewById(R.id.employeeContactTV);
            TextView hourlyRate = (TextView) row.findViewById(R.id.employeeHourlyRateTV);
            LinearLayout tableRow = (LinearLayout)row.findViewById(R.id.employeeTable);



            employeeID.setText(""+employeeIDAL.get(indexPosition));
            firsName.setText(""+employeeFirstNameAL.get(indexPosition));
            lastName.setText(""+employeeLastNameAL.get(indexPosition));
            position.setText(""+employeePositionAL.get(indexPosition));
            age.setText(""+employeeAgeAL.get(indexPosition));
            contactNumber.setText(""+employeeContactAL.get(indexPosition));
            hourlyRate.setText(""+employeeHourlyRateAL.get(indexPosition));

            if (highlightEmployeePosition == indexPosition) {
                tableRow.setBackgroundColor(getResources().getColor(R.color.jollibee));
                employeeID.setTextColor(Color.WHITE);
                firsName.setTextColor(Color.WHITE);
                lastName.setTextColor(Color.WHITE);
                position.setTextColor(Color.WHITE);
                age.setTextColor(Color.WHITE);
                contactNumber.setTextColor(Color.WHITE);
                hourlyRate.setTextColor(Color.WHITE);
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
            case R.id.employeeID:
                sort.setText("ID");
                orderColumn="attendantId";
                refreshEmployeeTable();
                return true;
            case R.id.employeeFirstName:
                sort.setText("First Name");
                orderColumn="firstName";
                refreshEmployeeTable();
                return true;
            case R.id.employeeLastName:
                sort.setText("Last Name");
                orderColumn="lastName";
                refreshEmployeeTable();
                return true;
            case R.id.employeeAge:
                sort.setText("Age");
                orderColumn="age";
                refreshEmployeeTable();
                return true;
            case R.id.employeePosition:
                sort.setText("Position");
                orderColumn="position";
                refreshEmployeeTable();
                return true;
            case R.id.employeeContactNumber:
                sort.setText("Contact No.");
                orderColumn="contactNumber";
                refreshEmployeeTable();
                return true;
            case R.id.employeeRate:
                sort.setText("Hourly Rate");
                orderColumn="hourlyRate";
                refreshEmployeeTable();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void unselectfromTable(){
        if(highlightEmployeePosition>=0) {
            highlightEmployeePosition = -1;
            deleteEmployee.setEnabled(false);
            editEmployee.setEnabled(false);
            refreshEmployeeTable();
        }
    }

    public void refreshEmployeeTable() {
        String orderBy = orderColumn+" "+orderSort;

        employeeIDAL = db.getColumn("attendantTable", "attendantId",orderBy);
        employeeFirstNameAL = db.getColumn("attendantTable", "firstName",orderBy);
        employeeLastNameAL = db.getColumn("attendantTable", "lastName",orderBy);
        employeePositionAL = db.getColumn("attendantTable", "position",orderBy);
        employeeAgeAL = db.getColumn("attendantTable", "age",orderBy);
        employeeContactAL = db.getColumn("attendantTable", "contactNumber",orderBy);
        employeeHourlyRateAL = db.getColumn("attendantTable", "hourlyRate",orderBy);

        employeeTableArrayAdapter employeeTableArrayAdapter = new employeeTableArrayAdapter(getApplicationContext(),employeeIDAL,employeeFirstNameAL,employeeLastNameAL,employeePositionAL,employeeAgeAL,employeeContactAL, employeeHourlyRateAL);
        int index = employeeTableLV.getFirstVisiblePosition();
        View v = employeeTableLV.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - employeeTableLV.getPaddingTop());
        employeeTableLV.setAdapter(employeeTableArrayAdapter);
        employeeTableLV.setSelectionFromTop(index, top);

    }

    public void clearETs(){
        firstName.setText("");
        lastName.setText("");
        position.setText("");
        age.setText("");
        contactNumber.setText("");
        employeeHourlyRate.setText("");
    }


}