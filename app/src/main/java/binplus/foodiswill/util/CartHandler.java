package binplus.foodiswill.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class CartHandler extends SQLiteOpenHelper {
    private static String DB_NAME = "dbcartTable";
    private static int DB_VERSION = 3;
    private SQLiteDatabase db;

    public static final String CART_TABLE = "cart33";
    public static final String COLUMN_ID = "product_id";
    public static final String COLUMN_CID = "cart_id";
    public static final String COLUMN_QTY = "qty";
    public static final String COLUMN_IS_OFFER = "is_offer";
    public static final String COLUMN_IMAGE = "product_images";
    public static final String COLUMN_CAT_ID = "cat_id";
    public static final String COLUMN_NAME = "product_name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_MRP = "mrp";
    public static final String COLUMN_IN_STOCK = "in_stock";
    public static final String COLUMN_UNIT_VALUE = "unit_value";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_DESC = "product_description";
    public static final String COLUMN_STOCK = "stock";
    public static final String COLUMN_PRODUCT_ATTRIBUTE = "product_attribute";
    public static final String COLUMN_REWARDS = "rewards";
    public static final String COLUMN_INCREMENT = "increment";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_HINDI_NAME = "product_hindi_name";
    public static final String COLUMN_TYPE = "type";
    public CartHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db=db;
        String exe = "CREATE TABLE IF NOT EXISTS " + CART_TABLE
                + "(" +  COLUMN_CID + " integer primary key, "
                + COLUMN_ID + " integer NOT NULL,"
                + COLUMN_QTY + " DOUBLE NOT NULL,"
                + COLUMN_IMAGE + " TEXT NOT NULL, "
                + COLUMN_CAT_ID + " TEXT NOT NULL, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_PRICE + " DOUBLE NOT NULL, "
                + COLUMN_MRP + " DOUBLE NOT NULL, "
                + COLUMN_IN_STOCK + " TEXT NOT NULL, "
                + COLUMN_UNIT + " TEXT NOT NULL, "
                + COLUMN_UNIT_VALUE + " TEXT NOT NULL, "
                + COLUMN_STOCK + " TEXT NOT NULL, "
                + COLUMN_PRODUCT_ATTRIBUTE + " TEXT NOT NULL, "
                + COLUMN_REWARDS + " TEXT NOT NULL, "
                + COLUMN_INCREMENT + " TEXT, "
                + COLUMN_TITLE + " TEXT NOT NULL, "
                + COLUMN_USER_ID + " TEXT NOT NULL, "
                + COLUMN_DESC + " TEXT NOT NULL, "
                + COLUMN_IS_OFFER + " TEXT NOT NULL, "
                + COLUMN_HINDI_NAME + " TEXT, "
                + COLUMN_TYPE + " TEXT NOT NULL "
                + ")";

        db.execSQL(exe);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean setCartTable(HashMap<String, String> map,float qty) {
        db = getWritableDatabase();
        if (isInCart(map.get(COLUMN_ID),map.get(COLUMN_USER_ID))) {
            //db.execSQL("update " + CART_TABLE + " set " + COLUMN_QTY + " = '" + Qty + "' where " + COLUMN_ID + "=" + map.get(COLUMN_ID));
            return false;
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, map.get(COLUMN_ID));
            values.put(COLUMN_QTY, qty);
            values.put(COLUMN_CAT_ID, map.get(COLUMN_CAT_ID));
            values.put(COLUMN_IMAGE, map.get(COLUMN_IMAGE));
            values.put(COLUMN_NAME, map.get(COLUMN_NAME));
            values.put(COLUMN_PRICE, map.get(COLUMN_PRICE));
            values.put(COLUMN_MRP, map.get(COLUMN_MRP));
            values.put(COLUMN_IN_STOCK, map.get(COLUMN_IN_STOCK));
            values.put(COLUMN_UNIT, map.get(COLUMN_UNIT));
            values.put(COLUMN_DESC, map.get(COLUMN_DESC));
            values.put(COLUMN_UNIT_VALUE, map.get(COLUMN_UNIT_VALUE));
            values.put(COLUMN_STOCK, map.get(COLUMN_STOCK));
            values.put(COLUMN_PRODUCT_ATTRIBUTE, map.get(COLUMN_PRODUCT_ATTRIBUTE));
            values.put(COLUMN_REWARDS, map.get(COLUMN_REWARDS));
            values.put(COLUMN_INCREMENT, map.get(COLUMN_INCREMENT));
            values.put(COLUMN_TITLE, map.get(COLUMN_TITLE));
            values.put(COLUMN_USER_ID, map.get(COLUMN_USER_ID));
            values.put(COLUMN_HINDI_NAME, map.get(COLUMN_HINDI_NAME));
            values.put(COLUMN_IS_OFFER, map.get(COLUMN_IS_OFFER));
            values.put(COLUMN_TYPE, map.get(COLUMN_TYPE));
            db.insert(CART_TABLE, null, values);
            return true;
        }
    }

    public boolean isInCart(String id, String user_id) {
        db = getReadableDatabase();
        String qry = "Select *  from " + CART_TABLE + " where " + COLUMN_ID + " = " + id + " and " + COLUMN_USER_ID + " = " +  user_id;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();

        if (cursor.getCount() > 0) return true;

        return false;
    }


    public String getCartItemQty(String id) {

        db = getReadableDatabase();
        String qry = "Select *  from " + CART_TABLE + " where " + COLUMN_CID + " = " + id;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(COLUMN_QTY));

    }

    public String getInCartItemQty(String id,String user_id) {
        if (isInCart(id,user_id)) {
            db = getReadableDatabase();
            String qry = "Select *  from " + CART_TABLE + " where " + COLUMN_CID + " = " + id;
            Cursor cursor = db.rawQuery(qry, null);
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(COLUMN_QTY));
        } else {
            return "0.0";
        }
    }

    public int getCartCount() {
        db = getReadableDatabase();
        String qry = "Select *  from " + CART_TABLE;
        Cursor cursor = db.rawQuery(qry, null);
        return cursor.getCount();
    }

    public String getTotalAmount() {
        db = getReadableDatabase();
        String qry = "Select SUM(" + COLUMN_PRICE + ") as total_amount  from " + CART_TABLE;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        String total = cursor.getString(cursor.getColumnIndex("total_amount"));
        if (total != null) {

            return total;
        } else {
            return "0";
        }
    }


    public ArrayList<HashMap<String, String>> getCartAll(String user_id) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        db = getReadableDatabase();
        String qry = "Select *  from " + CART_TABLE + " where " + COLUMN_USER_ID + " = " + user_id;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put(COLUMN_ID, cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            map.put(COLUMN_CID, cursor.getString(cursor.getColumnIndex(COLUMN_CID)));
            map.put(COLUMN_QTY, cursor.getString(cursor.getColumnIndex(COLUMN_QTY)));
            map.put(COLUMN_IMAGE, cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));
            map.put(COLUMN_CAT_ID, cursor.getString(cursor.getColumnIndex(COLUMN_CAT_ID)));
            map.put(COLUMN_NAME, cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            map.put(COLUMN_PRICE, cursor.getString(cursor.getColumnIndex(COLUMN_PRICE)));
            map.put(COLUMN_MRP, cursor.getString(cursor.getColumnIndex(COLUMN_MRP)));
            map.put(COLUMN_IN_STOCK, cursor.getString(cursor.getColumnIndex(COLUMN_IN_STOCK)));
            map.put(COLUMN_UNIT, cursor.getString(cursor.getColumnIndex(COLUMN_UNIT)));
            map.put(COLUMN_DESC, cursor.getString(cursor.getColumnIndex(COLUMN_DESC)));
            map.put(COLUMN_UNIT_VALUE, cursor.getString(cursor.getColumnIndex(COLUMN_UNIT_VALUE)));
            map.put(COLUMN_STOCK, cursor.getString(cursor.getColumnIndex(COLUMN_STOCK)));
            map.put(COLUMN_PRODUCT_ATTRIBUTE, cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ATTRIBUTE)));
            map.put(COLUMN_REWARDS, cursor.getString(cursor.getColumnIndex(COLUMN_REWARDS)));
            map.put(COLUMN_INCREMENT, cursor.getString(cursor.getColumnIndex(COLUMN_INCREMENT)));
            map.put(COLUMN_TITLE, cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            map.put(COLUMN_USER_ID, cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
            map.put(COLUMN_HINDI_NAME, cursor.getString(cursor.getColumnIndex(COLUMN_HINDI_NAME)));
            map.put(COLUMN_IS_OFFER, cursor.getString(cursor.getColumnIndex(COLUMN_IS_OFFER)));
            map.put(COLUMN_TYPE, cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
            list.add(map);
            cursor.moveToNext();
        }
        db.close();
        return list;
    }

    public ArrayList<HashMap<String, String>> getCartProduct(int product_id, String user_id) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        db = getReadableDatabase();
        String qry = "Select *  from " + CART_TABLE + " where " + COLUMN_ID + " = " + product_id + " and " + COLUMN_USER_ID + " = " +  user_id;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put(COLUMN_CID, cursor.getString(cursor.getColumnIndex(COLUMN_CID)));
            map.put(COLUMN_ID, cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
             map.put(COLUMN_QTY, cursor.getString(cursor.getColumnIndex(COLUMN_QTY)));
            map.put(COLUMN_IMAGE, cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));
            map.put(COLUMN_CAT_ID, cursor.getString(cursor.getColumnIndex(COLUMN_CAT_ID)));
            map.put(COLUMN_NAME, cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            map.put(COLUMN_PRICE, cursor.getString(cursor.getColumnIndex(COLUMN_PRICE)));
            map.put(COLUMN_MRP, cursor.getString(cursor.getColumnIndex(COLUMN_MRP)));
            map.put(COLUMN_IN_STOCK, cursor.getString(cursor.getColumnIndex(COLUMN_IN_STOCK)));
            map.put(COLUMN_UNIT, cursor.getString(cursor.getColumnIndex(COLUMN_UNIT)));
            map.put(COLUMN_DESC, cursor.getString(cursor.getColumnIndex(COLUMN_DESC)));
            map.put(COLUMN_UNIT_VALUE, cursor.getString(cursor.getColumnIndex(COLUMN_UNIT_VALUE)));
            map.put(COLUMN_STOCK, cursor.getString(cursor.getColumnIndex(COLUMN_STOCK)));
            map.put(COLUMN_PRODUCT_ATTRIBUTE, cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ATTRIBUTE)));
            map.put(COLUMN_REWARDS, cursor.getString(cursor.getColumnIndex(COLUMN_REWARDS)));
            map.put(COLUMN_INCREMENT, cursor.getString(cursor.getColumnIndex(COLUMN_INCREMENT)));
            map.put(COLUMN_TITLE, cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            map.put(COLUMN_USER_ID, cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
            map.put(COLUMN_HINDI_NAME, cursor.getString(cursor.getColumnIndex(COLUMN_HINDI_NAME)));
            map.put(COLUMN_IS_OFFER, cursor.getString(cursor.getColumnIndex(COLUMN_IS_OFFER)));


            list.add(map);
            cursor.moveToNext();
        }
        db.close();
        return list;
    }

    public String getColumnRewards() {
        db = getReadableDatabase();
        String qry = "SELECT rewards FROM " + CART_TABLE;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        String reward = cursor.getString(cursor.getColumnIndex("rewards"));
        if (reward != null) {

            return reward;
        } else {
            return "0";
        }
    }

    public String getFavConcatString() {
        db = getReadableDatabase();
        String qry = "Select *  from " + CART_TABLE;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        String concate = "";
        for (int i = 0; i < cursor.getCount(); i++) {
            if (concate.equalsIgnoreCase("")) {
                concate = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            } else {
                concate = concate + "_" + cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            }
            cursor.moveToNext();
        }
        return concate;
    }
    public void clearCart(String id) {
        db = getReadableDatabase();
        db.execSQL("delete from " + CART_TABLE + " where " + COLUMN_USER_ID + " = " + id);
        db.close();

    }

    public void removeItemFromCart(String id, String user_id) {
        db = getReadableDatabase();
        db.execSQL("delete from " + CART_TABLE + " where " + COLUMN_ID + " = " + id + " and " + COLUMN_USER_ID + " = " +  user_id);
        db.close();

    }


}
