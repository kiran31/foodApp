package binplus.foodiswill.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import binplus.foodiswill.Verfication_activity;

import static binplus.foodiswill.Config.BaseURL.CITY_ID;
import static binplus.foodiswill.Config.BaseURL.CITY_NAME;
import static binplus.foodiswill.Config.BaseURL.IS_CITY;
import static binplus.foodiswill.Config.BaseURL.IS_LOGIN;
import static binplus.foodiswill.Config.BaseURL.KEY_CAT;
import static binplus.foodiswill.Config.BaseURL.KEY_CNT;
import static binplus.foodiswill.Config.BaseURL.KEY_DATE;
import static binplus.foodiswill.Config.BaseURL.KEY_EMAIL;
import static binplus.foodiswill.Config.BaseURL.KEY_HOUSE;
import static binplus.foodiswill.Config.BaseURL.KEY_ID;
import static binplus.foodiswill.Config.BaseURL.KEY_IMAGE;
import static binplus.foodiswill.Config.BaseURL.KEY_MOBILE;
import static binplus.foodiswill.Config.BaseURL.KEY_NAME;
import static binplus.foodiswill.Config.BaseURL.KEY_PASSWORD;
import static binplus.foodiswill.Config.BaseURL.KEY_PAYMENT_METHOD;
import static binplus.foodiswill.Config.BaseURL.KEY_PINCODE;
import static binplus.foodiswill.Config.BaseURL.KEY_REWARDS_POINTS;
import static binplus.foodiswill.Config.BaseURL.KEY_SOCITY_ID;
import static binplus.foodiswill.Config.BaseURL.KEY_SOCITY_NAME;
import static binplus.foodiswill.Config.BaseURL.KEY_TIME;
import static binplus.foodiswill.Config.BaseURL.KEY_USER_CITY;
import static binplus.foodiswill.Config.BaseURL.KEY_USER_CITY_ID;
import static binplus.foodiswill.Config.BaseURL.KEY_WALLET_Ammount;
import static binplus.foodiswill.Config.BaseURL.PREFS_NAME;
import static binplus.foodiswill.Config.BaseURL.PREFS_NAME2;
import static binplus.foodiswill.Config.BaseURL.TOTAL_AMOUNT;
import static binplus.foodiswill.Utility.Constants.*;


public class Session_management {

    SharedPreferences prefs;
    SharedPreferences prefs2;

    SharedPreferences.Editor editor;
    SharedPreferences.Editor editor2;

    Context context;

    int PRIVATE_MODE = 0;

    public Session_management(Context context) {

        this.context = context;
        prefs = context.getSharedPreferences(PREFS_NAME, PRIVATE_MODE);
        editor = prefs.edit();

        prefs2 = context.getSharedPreferences(PREFS_NAME2, PRIVATE_MODE);
        editor2 = prefs2.edit();

    }

    public void createLoginSession(String id, String email, String name
            , String mobile, String image, String wallet_ammount, String reward_point, String pincode, String city_id,
                                   String city_name, String house, String password) {

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_IMAGE, image);
        editor.putString(KEY_WALLET_Ammount, wallet_ammount);
        editor.putString(KEY_REWARDS_POINTS, reward_point);
        editor.putString(KEY_PINCODE, pincode);
        editor.putString(KEY_USER_CITY_ID, city_id);
        editor.putString(KEY_USER_CITY, city_name);
        editor.putString(KEY_HOUSE, house);
        editor.putString(KEY_PASSWORD, password);

        editor.commit();
    }

    public void createPaySection()
    {
        editor2.putBoolean(PAY_CHECK,false);
        editor2.putString(PAY_ID,"");
        editor2.putString(PAY_STATUS,"");
        editor2.putString(PAY_AMT,"");
        editor2.putString(PAY_DATE,"");
        editor2.putString(PAY_TIME,"");
        editor2.putString(PAY_LOCATION,"");
        editor2.putString(PAY_DELIVERY,"");
        editor2.putString(PAY_METHOD,"");
        editor2.putString(PAY_DISCOUNT,"");
        editor2.putString(PAY_DISINFECTION,"");
        editor2.putString(PAY_COUPON_CODE,"");
        editor2.commit();

    }

    public void updatePaySection(String date,String time,
                                 String location,String deli_charges,String method,String dis_amt,String disinfection,String coupon_code)
    {
        editor2.putBoolean(PAY_CHECK,true);
        editor2.putString(PAY_DATE,date);
        editor2.putString(PAY_TIME,time);
        editor2.putString(PAY_LOCATION,location);
        editor2.putString(PAY_DELIVERY,deli_charges);
        editor2.putString(PAY_METHOD,method);
        editor2.putString(PAY_DISCOUNT,dis_amt);
        editor2.putString(PAY_DISINFECTION,disinfection);
        editor2.putString(PAY_COUPON_CODE,coupon_code);
        editor2.apply();

    }
    public void updatePaymentSection(String pay_order_id,String pay_status,String amt)
    {
        editor2.putString(PAY_ID,pay_order_id);
        editor2.putString(PAY_STATUS,pay_status);
        editor2.putString(PAY_AMT,amt);
        editor2.apply();

    }
    public void updateIsPayValue(boolean value){
      editor2.putBoolean(PAY_CHECK,value);
      editor2.apply();
    }
    public boolean isOnlinePay()
    {
        return prefs2.getBoolean(PAY_CHECK,false);
    }



    public HashMap<String,String> getPayDetails()
    {
        HashMap<String, String> pay = new HashMap<String, String>();
        pay.put(PAY_ID, prefs2.getString(PAY_ID, null));
        pay.put(PAY_STATUS, prefs2.getString(PAY_STATUS, null));
        pay.put(PAY_AMT, prefs2.getString(PAY_AMT, null));
        pay.put(PAY_DATE, prefs2.getString(PAY_DATE, null));
        pay.put(PAY_TIME, prefs2.getString(PAY_TIME, null));
        pay.put(PAY_LOCATION, prefs2.getString(PAY_LOCATION, null));
        pay.put(PAY_DELIVERY, prefs2.getString(PAY_DELIVERY, null));
        pay.put(PAY_METHOD, prefs2.getString(PAY_METHOD, null));
        pay.put(PAY_DISCOUNT, prefs2.getString(PAY_DISCOUNT, null));
        pay.put(PAY_DISINFECTION, prefs2.getString(PAY_DISINFECTION, null));
        pay.put(PAY_COUPON_CODE, prefs2.getString(PAY_COUPON_CODE, null));
        return pay;
    }

    public void clearPay()
    {
        editor2.clear();
        editor2.commit();
    }
    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_ID, prefs.getString(KEY_ID, null));
        // user email id
        user.put(KEY_EMAIL, prefs.getString(KEY_EMAIL, null));
        // user name
        user.put(KEY_NAME, prefs.getString(KEY_NAME, null));
        user.put(KEY_MOBILE, prefs.getString(KEY_MOBILE, null));
        user.put(KEY_IMAGE, prefs.getString(KEY_IMAGE, null));
        user.put(KEY_WALLET_Ammount, prefs.getString(KEY_WALLET_Ammount, null));
        user.put(KEY_REWARDS_POINTS, prefs.getString(KEY_REWARDS_POINTS, null));
        user.put(KEY_PAYMENT_METHOD, prefs.getString(KEY_PAYMENT_METHOD, ""));
        user.put(TOTAL_AMOUNT, prefs.getString(TOTAL_AMOUNT, null));
        user.put(KEY_PINCODE, prefs.getString(KEY_PINCODE, null));
        user.put(KEY_SOCITY_ID, prefs.getString(KEY_SOCITY_ID, null));
        user.put(KEY_USER_CITY_ID, prefs.getString(KEY_USER_CITY_ID, null));
        user.put(KEY_USER_CITY, prefs.getString(KEY_USER_CITY, null));
        user.put(KEY_SOCITY_NAME, prefs.getString(KEY_SOCITY_NAME, null));
        user.put(KEY_HOUSE, prefs.getString(KEY_HOUSE, null));
        user.put(KEY_PASSWORD, prefs.getString(KEY_PASSWORD, null));

        // return user
        return user;
    }

    public void updateData(String name, String mobile, String pincode
            , String socity_id, String image, String wallet, String rewards, String house) {

        editor.putString(KEY_NAME, name);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_PINCODE, pincode);
        editor.putString(KEY_SOCITY_ID, socity_id);
        editor.putString(KEY_IMAGE, image);
        editor.putString(KEY_WALLET_Ammount, wallet);
        editor.putString(KEY_REWARDS_POINTS, rewards);
        editor.putString(KEY_HOUSE, house);

        editor.apply();
    }

    public void updateSocity(String socity_name, String socity_id) {
        editor.putString(KEY_SOCITY_NAME, socity_name);
        editor.putString(KEY_SOCITY_ID, socity_id);

        editor.apply();
    }

    public void logoutSession() {
        editor.clear();
        editor.commit();
        cleardatetime();
    }


    public HashMap<String, String> getdatetime() {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_DATE, prefs2.getString(KEY_DATE, null));
        user.put(KEY_TIME, prefs2.getString(KEY_TIME, null));

        return user;
    }

    // Get Login State
    public boolean isLoggedIn() {
        return prefs.getBoolean(IS_LOGIN, false);
    }
    public void updateProfile(String image,String name,String cnt)
    {
        editor.putString(KEY_IMAGE,image);
        editor.putString(KEY_NAME,name);
        editor.putString(KEY_CNT,cnt);
        editor.commit();
    }

    public HashMap<String,String> getUpdateProfile()
    {
        HashMap<String,String> map=new HashMap<>();
        map.put(KEY_IMAGE,prefs.getString(KEY_IMAGE,null));
        map.put(KEY_NAME,prefs.getString(KEY_NAME,null));
        map.put(KEY_CNT,prefs.getString(KEY_CNT,null));
        return map;
    }

    public void updateUserName(String name)
    {
        editor.putString(KEY_NAME,name);
        editor.commit();
    }
    public void updateSessionItem(String key,String value)
    {
        editor.putString(key,value);
        editor.commit();
    }


    public void setCategoryId(String id)
    {
        editor.putString(KEY_CAT,id);
        editor.commit();
    }

    public String getCategoryId()
    {
        return prefs.getString(KEY_CAT,"");
    }

    public void creatdatetime(String date, String time) {
        editor2.putString(KEY_DATE, date);
        editor2.putString(KEY_TIME, time);

        editor2.commit();
    }

    public void addCity(String id,String name){
        editor.putBoolean(IS_CITY,true);
        editor.putString(CITY_ID,id);
        editor.putString(CITY_NAME,name);
        editor.commit();
    }
    public boolean isCityAdded(){
        return prefs.getBoolean(IS_CITY,false);
    }
    public String getSessionItem(String key){
        return prefs.getString(key,"");
    }
    public void cleardatetime() {
        editor2.clear();
        editor2.commit();
    }
}
