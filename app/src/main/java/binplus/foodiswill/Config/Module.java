package binplus.foodiswill.Config;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import binplus.foodiswill.AppController;
import binplus.foodiswill.Model.Product_model;
import binplus.foodiswill.R;
import binplus.foodiswill.Utility.Constants;
import binplus.foodiswill.util.CartHandler;
import binplus.foodiswill.util.DatabaseCartHandler;
import binplus.foodiswill.util.Session_management;
import binplus.foodiswill.util.WishlistHandler;

import static binplus.foodiswill.Config.BaseURL.CITY_ID;

public class Module {

    Context context;
    Session_management session_management;

    public Module(Context context) {
        this.context = context;
        session_management=new Session_management(context);
    }

    public int getDiscount(String price, String mrp)
    {
        double mrp_d= Double.parseDouble(mrp);
        double price_d= Double.parseDouble(price);
        double per=((mrp_d-price_d)/mrp_d)*100;
        double df= Math.round(per);
        int d=(int)df;
        return d;
    }


    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    public String VolleyErrorMessage(VolleyError error)
    {
        String str_error ="";
        if (error instanceof TimeoutError) {
            str_error="Connection Timeout";
        } else if (error instanceof AuthFailureError) {
            str_error="Session Timeout";
            //TODO
        } else if (error instanceof ServerError) {
            str_error="Server not responding please try again later";
            //TODO
        } else if (error instanceof NetworkError) {
            str_error="Server not responding please try again later";
            //TODO
        } else if (error instanceof ParseError) {
            //TODO
            str_error="An Unknown error occur";
        }else if(error instanceof NoConnectionError){
            str_error="No Internet Connection";
        }

        return str_error;
    }

    public void preventMultipleClick(final View view) {
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 1000);
    }
    public void onShakeImage(RelativeLayout rel_variant, Context context) {
        Animation shake;
        shake = AnimationUtils.loadAnimation(context, R.anim.shake);
        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(200);
        rel_variant.startAnimation(shake); // starts animation
    }

    public void addToCart(Context context ,String product_id ,String product_images ,String cat_id ,String product_name ,String price,
                              String product_desc ,String rewards ,String unit_value ,String unit ,String increment ,String stock , String in_stock,
                              String title ,String mrp,String product_attribute,String user_id,String hndName ,String qty,String type,String is_offer)

    {
       CartHandler db_cart = new CartHandler( context );
        HashMap<String, String> mapProduct = new HashMap<String, String>();
        mapProduct.put( "product_id",product_id );
        mapProduct.put( "product_images",product_images );
        mapProduct.put( "cat_id", cat_id );
        mapProduct.put( "product_name",product_name );
        mapProduct.put( "price",price );
        mapProduct.put( "product_description",product_desc );
        mapProduct.put( "rewards",rewards);
        mapProduct.put("user_id",user_id);
        mapProduct.put( "unit_value",unit_value );
        mapProduct.put( "unit", unit );
        mapProduct.put( "increment",increment );
        mapProduct.put( "stock",stock );
        mapProduct.put( "title",title);
        mapProduct.put( "mrp", mrp );
        mapProduct.put( "product_attribute",product_attribute );
        mapProduct.put( "in_stock", in_stock );
        mapProduct.put( "product_hindi_name", hndName );
        mapProduct.put( "qty", hndName );
        mapProduct.put( "type", hndName );
        mapProduct.put( "is_offer", hndName );
        try {

            boolean tr = db_cart.setCartTable(mapProduct, Float.parseFloat(qty));
            if (tr == true) {
                Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                updateCartintent();
            } else if (tr == false) {
                Toast.makeText(context, "Cart Updated", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            Toast.makeText(context, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    public void addToWishlist(Context context ,String product_id ,String product_images ,String cat_id ,String product_name ,String price,
                              String product_desc ,String rewards ,String unit_value ,String unit ,String increment ,String stock , String in_stock,
                              String title ,String mrp,String product_attribute,String user_id,String hndName, String arbName, String arbDesc,
                              String dealPrice,String sDate, String sTime, String eDate, String eTime, String staus, String size, String color, String city )

    {
       WishlistHandler db_wish = new WishlistHandler( context );
        HashMap<String, String> mapProduct = new HashMap<String, String>();
        mapProduct.put( "product_id",checkNullString(product_id) );
        mapProduct.put( "product_images",checkNullString(product_images) );
        mapProduct.put( "cat_id", checkNullString(cat_id) );
        mapProduct.put( "product_name",checkNullString(product_name) );
        mapProduct.put( "price",checkNullString(price) );
        mapProduct.put( "product_description",checkNullString(product_desc) );
        mapProduct.put( "rewards",checkNullString(rewards));
        mapProduct.put("user_id",checkNullString(user_id));
        mapProduct.put( "unit_value",checkNullString(unit_value) );
        mapProduct.put( "unit", checkNullString(unit) );
        mapProduct.put( "increment",checkNullString(increment) );
        mapProduct.put( "stock",checkNullString(stock) );
        mapProduct.put( "title",checkNullString(title));
        mapProduct.put( "mrp", checkNullString(mrp) );
        mapProduct.put( "product_attribute",checkNullString(product_attribute) );
        mapProduct.put( "in_stock", checkNullString(in_stock) );
        mapProduct.put( "product_hindi_name", checkNullString(hndName) );

        mapProduct.put( "product_name_arb", checkNullString(arbName) );
        mapProduct.put( "product_description_arb", checkNullString(arbDesc) );
        mapProduct.put( "deal_price", checkNullString(dealPrice) );
        mapProduct.put( "start_date", checkNullString(sDate) );
        mapProduct.put( "start_time", checkNullString(sTime) );
        mapProduct.put( "end_date", checkNullString(eDate) );
        mapProduct.put( "end_time", checkNullString(eTime) );
        mapProduct.put( "status", checkNullString(staus) );
        mapProduct.put( "size", checkNullString(size) );
        mapProduct.put( "color", checkNullString(color) );
        mapProduct.put( "city", checkNullString(city) );


        try {

            boolean tr =db_wish.setwishTable(mapProduct);
            if (tr == true) {

                //   context.setCartCounter("" + holder.db_cart.getCartCount());
                Toast.makeText(context, "Added to Wishlist" , Toast.LENGTH_SHORT).show();
                updatewishintent();



            }
            else
            {
                Toast.makeText(context, "Something Went Wrong" , Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            //  Toast.makeText(context, "" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void showToast(String s)
    {
        Toast.makeText(context,""+s,Toast.LENGTH_SHORT).show();
    }

    public void updatewishintent() {
        Intent updates = new Intent("Grocery_wish");
        updates.putExtra("type", "update");
        context.sendBroadcast(updates);
    }
 public void updateCartintent() {
        Intent updates = new Intent("Grocery_cart");
        updates.putExtra("type", "update");
        context.sendBroadcast(updates);
    }

    public Product_model addExtraOneTopSelling()
    {
        Product_model m=new Product_model();
        m.setProduct_id("00");
        m.setProduct_name("Shop By Categories");
        m.setProduct_name_hindi("");
        m.setCategory_id("01");
        m.setProduct_description("");
        m.setPrice("0");
        m.setProduct_attribute("[]");
        m.setMrp("0");
        m.setProduct_image("[\"shop_by_category.png\"]");
        m.setIn_stock("1");
        m.setUnit_value("0");
        m.setUnit("KG");
        m.setRewards("0");
        m.setStock("100");
        m.setTitle("Shop By Category");
        return m;
    }
    public Product_model addExtraOneNew()
    {
        Product_model m=new Product_model();
        m.setProduct_id("00");
        m.setProduct_name("Shop By Categories");
        m.setProduct_name_hindi("");
        m.setCategory_id("01");
        m.setProduct_description("");
        m.setPrice("0");
        m.setProduct_attribute("[]");
        m.setMrp("0");
        m.setProduct_image("[\"shop_by_category.png\"]");
        m.setIn_stock("1");
        m.setUnit_value("0");
        m.setUnit("KG");
        m.setRewards("0");
        m.setStock("100");
        m.setTitle("Shop By Category");
        return m;
    }

    public void rateApp()
    {
        try
        {
            Intent rateIntent = rateIntentForUrl("market://details");
            context.startActivity(rateIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            context.startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, context.getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21)
        {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else
        {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    public boolean checkNull(String str){
        if(str==null||str.isEmpty()||str.equalsIgnoreCase("null")){
            return true;
        }else{
            return false;
        }
    }
    public String getCityID(){
        return session_management.getSessionItem(CITY_ID);
    }

    public void postRequest(String url, final HashMap<String,String>params, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        Log.e ("postmethod", "postRequest: "+url+"\n"+params );

        StringRequest stringRequest=new StringRequest(Request.Method.POST,url,listener, errorListener){
            @Override
            protected Map<String, String> getParams()  {
                Log.e ("params", "check"+params );
                return params;
                // return super.getParams ( );
            }
        };
        RetryPolicy retryPolicy=new DefaultRetryPolicy(BaseURL.REQUEST_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy (retryPolicy);

        AppController.getInstance ().addToRequestQueue (stringRequest,"tag");

    }

    public String checkNullNumber(String str){
        if(str==null || str.isEmpty() || str.equalsIgnoreCase("null")){
            return "0";
        }else{
            return str;
        }
    }
    public String checkNullString(String str)
    {
        String st = "";
        if(str==null || str.isEmpty() || str.equalsIgnoreCase("null"))
        {
            st="";
        }else {
            st = str;
        }
        return st;
    }

}
