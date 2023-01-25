package com.example.mcjollibeeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;


public class DatabaseOperations extends SQLiteOpenHelper {

    public static final String dbName="mcJollibee.db";

    public static final String userTable="userTable";
    public static final String menuTable="menuTable";
    public static final String ordersTable="ordersTable";
    public static final String attendantTable="attendantTable";
    public static final String itemOrdersTable="itemOrdersTable";
    public static final String loginTable="loginTable";



    public DatabaseOperations(Context context){
        super(context,dbName,null,1);
        System.out.println("working");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("pre working");
        db.execSQL("create table "+userTable+" (id integer primary key,userImage BLOB, username text UNIQUE, password text,firstName text,lastName text,age integer, address text," +
                "contactNumber text,userType text)");
        System.out.println("Table "+userTable+" has been created successfully");
        db.execSQL("create table "+menuTable+" (foodId integer primary key,foodImage BLOB, foodName text UNIQUE,foodType text,foodPrice integer)");
        System.out.println("Table "+menuTable+" has been created successfully");

        db.execSQL("create table "+attendantTable+" (attendantId integer primary key, firstName text,lastName text,age integer, position text, contactNumber text,hourlyRate integer)");
        System.out.println("Table attendantTable has been created successfully");

        db.execSQL("create table "+ordersTable+" (orderId integer primary key,orderImage BLOB, id integer, dateTime text, attendantId  integer,paymentMethod text, orderCost double, status text," +
                "FOREIGN KEY (id) REFERENCES userTable(id)  ON DELETE CASCADE ON UPDATE CASCADE," +
                "FOREIGN KEY (attendantId) REFERENCES attendantTable(attendantId)  ON DELETE CASCADE ON UPDATE CASCADE)");

        db.execSQL("create table "+itemOrdersTable+" (itemName text, itemPrice integer, itemQuantity int, orderId integer, itemTotal integer," +
                "FOREIGN KEY (itemName) REFERENCES menuTable(foodName)  ON DELETE CASCADE ON UPDATE CASCADE," +
                "FOREIGN KEY (orderId) REFERENCES ordersTable(orderId)  ON DELETE CASCADE ON UPDATE CASCADE)");
        System.out.println("Table ordersTable has been created successfully");
        db.execSQL("PRAGMA foreign_keys = ON");
        System.out.println("Foreign key Enabled");




    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int newVersion, int oldVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+userTable);
        onCreate(db);
    }

    public void foreignKeyEnable(boolean trulse){
        SQLiteDatabase db = this.getWritableDatabase();
        if(trulse) {
            db.execSQL("PRAGMA foreign_keys = ON");
        }
        else{
            db.execSQL("PRAGMA foreign_keys = OFF");
        }
    }

    public ArrayList<String> getColumn(String tableName, String column,String orderBy) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.query(tableName,null,null,null,null,null,orderBy);
        ArrayList<String> getMenu = new ArrayList<String>();
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                int columnIndexNum = res.getColumnIndex(column);
                getMenu.add(res.getString(columnIndexNum));
                res.moveToNext();
            }
        return getMenu;
    }

    public ArrayList<String> getMenu(String tableName, String columnIndex, String whereArgs, String orderBy) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.query(tableName,null,whereArgs,null,null,null,orderBy);
        ArrayList<String> getMenu = new ArrayList<String>();

        res.moveToFirst();
        while (res.isAfterLast() == false) {
            int columnIndexNum = res.getColumnIndex(columnIndex);
            getMenu.add(res.getString(columnIndexNum));
            res.moveToNext();
        }
        return getMenu;
    }


    public ArrayList<Bitmap> getPic(String table, String orderBy, String whereArgs){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Bitmap> getMenuPic = new ArrayList<>();
        Cursor res;
        String column;
        if(table=="menuTable"){
            column = "foodImage";
            res = db.query(table,null,whereArgs,null,null,null,orderBy);
        }
        else if(table=="ordersTable"){
            column = "orderImage";
            res = db.query(table,null,whereArgs,null,null,null,orderBy);
        }
        else{
            System.out.println("userIMage");
            column = "userImage";
            res = db.rawQuery("select userImage from userTable where id=?", new String[]{whereArgs});
        }
        res.moveToFirst();
            while (res.isAfterLast() == false) {
                int imageColumnIndex = res.getColumnIndex(column);
                byte[] image = res.getBlob(imageColumnIndex);
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                getMenuPic.add(bitmap);
                res.moveToNext();
                System.out.println("getpic OWrk "+getMenuPic);
            }
        return getMenuPic;
    }



    public boolean insertMenu(byte[] foodImage,String foodName, String foodType,int foodPrice){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("foodImage",foodImage);
        cv.put("foodName",foodName);
        cv.put("foodType",foodType);
        cv.put("foodPrice",foodPrice);
        try {
            db.insertOrThrow("menuTable",null,cv);
            return true;
        } catch (SQLiteConstraintException e) {
            System.out.println(e);

            return false;
        }

    }

    public boolean updateMenu( int id,byte[] image, String foodName, String foodType,int foodPrice){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("foodImage",image);
        cv.put("foodName",foodName);
        cv.put("foodType",foodType);
        cv.put("foodPrice",foodPrice);
        try {
            db.update("menuTable",cv,"foodId=?",new String[]{Integer.toString(id)});
            return  true;
        } catch (Exception e) {
            return  false;
        }

    }

    public boolean deleteTable(String[] whereArgs, String tableName, String whereClause){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, whereClause+"= ?", whereArgs);
        return true;
    }

    public boolean insertUser(byte[] userImage, String firstName, String lastName, int age, String address, String contactNumber, String username, String password, String userType){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if(userImage != null) {
            cv.put("userImage", userImage);
        }
        cv.put("username",username);
        cv.put("password",password);
        cv.put("firstName",firstName);
        cv.put("lastName",lastName);
        cv.put("age",age);
        cv.put("address",address);
        cv.put("contactNumber",contactNumber);
        cv.put("userType",userType);
        try {
            db.insertOrThrow("userTable",null,cv);
            return  true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteUser(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("userTable", "id = ?", new String[] { Integer.toString(id)});
        return true;
    }

    public ArrayList<String> getUserLogin( String[] userName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT username, password FROM userTable WHERE username=? AND userType=?" , userName);
        ArrayList<String> getUser = new ArrayList<>();
        if (res.moveToFirst()) {
            res.moveToFirst();
            getUser.add(res.getString(0)+"-"+res.getString(1));
        }
        System.out.println("res is"+ getUser);
        return getUser;
    }

    public String getUserId( String[] usernamePass ){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT id FROM userTable WHERE username=? AND password=?" , usernamePass);
        String id="";
        if (res.moveToFirst()) {
            res.moveToFirst();
            id = res.getString(0);
            System.out.println("res is in "+ id);

        }
        System.out.println("res is"+ id);
        return id;
    }



    public ArrayList<String> getUser( String column, String orderBy,String whereClause) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> getUser = new ArrayList<String>();
        Cursor res;
        if(whereClause == "userType"){
            String query = "select * from userTable WHERE "+whereClause+" =? ORDER BY "+" "+orderBy;
            res = db.rawQuery(query, new String[]{"admin"});
        }
        else if(orderBy== null){
            String query = "select "+column+" from userTable WHERE "+whereClause;
            res = db.rawQuery(query,null);
        }
        else{
            String query = "select * from userTable WHERE userType =? ORDER BY "+" "+orderBy;
            res = db.rawQuery(query, new String[]{"customer"});
            System.out.println("query 3");
        }
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            int columnIndexNum = res.getColumnIndex(column);
            getUser.add(res.getString(columnIndexNum));
            res.moveToNext();
        }
        return getUser;
    }

    public boolean updateUser(int id, byte[] image, String firstName, String lastName, int age, String address, String contactNumber, String username, String passowrd, String userType){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("firstName",firstName);
        if(image != null) {
            cv.put("userImage", image);
        }
        cv.put("lastName",lastName);
        cv.put("age",age);
        cv.put("address",address);
        cv.put("contactNumber",contactNumber);
        cv.put("username",username);
        cv.put("password",passowrd);
        cv.put("userType",userType);
        try {
            db.update("userTable",cv,"id=?",new String[] {Integer.toString(id)});
            return  true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean insertAttendant(String firstName, String lastName, int age, String address, String contactNumber, int hourylRate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("firstName",firstName);
        cv.put("lastName",lastName);
        cv.put("age",age);
        cv.put("position",address);
        cv.put("contactNumber",contactNumber);
        cv.put("hourlyRate",hourylRate);
        System.out.println("attendant");
        try{
        db.insertOrThrow("attendantTable",null,cv);
        return  true;
    } catch (Exception e) {
        return false;
    }
    }

    public boolean updateAttendant(int attendantId, String firstName, String lastName, int age, String address, String contactNumber, int hourlyRate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("firstName",firstName);
        cv.put("lastName",lastName);
        cv.put("age",age);
        cv.put("position",address);
        cv.put("contactNumber",contactNumber);
        cv.put("hourlyRate",hourlyRate);
        db.update("attendantTable",cv,"attendantId=?",new String[] {Integer.toString(attendantId)});
        return  true;
    }

    public boolean insertOrder(String table, ContentValues cv){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
        db.insertOrThrow(table,null,cv);
        return  true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateTable(String tableName,String columnArgs, String whereArgs, ContentValues cv){
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(tableName,cv,columnArgs+"=?", new String[]{whereArgs});
        return  true;
    }

    public ArrayList<String> getSum(String tableName, String column, String whereArgs){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM("+column+") as sumTotal FROM "+tableName+" WHERE orderId="+"?";
        System.out.println(query);
        Cursor res = db.rawQuery(query, new String[]{whereArgs});
        ArrayList<String> getSum = new ArrayList<>();

        res.moveToFirst();
        while (res.isAfterLast() == false) {
            int columnIndexNum = res.getColumnIndex("sumTotal");
            getSum.add(res.getString(columnIndexNum));
            res.moveToNext();
        }
        System.out.println(getSum);
        return getSum;
    }

    public ArrayList<String> getRandom(String tableName, String column,String orderBy) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.query(tableName,null,null,null,null,null,orderBy,"1");
        ArrayList<String> getMenu = new ArrayList<String>();
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            int columnIndexNum = res.getColumnIndex(column);
            getMenu.add(res.getString(columnIndexNum));
            res.moveToNext();
        }
        return getMenu;
    }



}

