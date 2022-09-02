package binplus.foodiswill.Fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.Config.SharedPref;
import binplus.foodiswill.AppController;
import binplus.foodiswill.MainActivity;
import binplus.foodiswill.PaymentActivities.WebViewActivity;
import binplus.foodiswill.Utility.AvenuesParams;
import binplus.foodiswill.Utility.ServiceUtility;
import binplus.foodiswill.networkconnectivity.NetworkConnection;
import binplus.foodiswill.networkconnectivity.NetworkError;

import binplus.foodiswill.R;
import binplus.foodiswill.util.ConnectivityReceiver;
import binplus.foodiswill.util.CustomVolleyJsonArrayRequest;
import binplus.foodiswill.util.CustomVolleyJsonRequest;
import binplus.foodiswill.util.DatabaseCartHandler;
import binplus.foodiswill.util.Session_management;

import static binplus.foodiswill.Config.BaseURL.KEY_ID;
import static binplus.foodiswill.Utility.Constants.PAY_AMT;
import static binplus.foodiswill.Utility.Constants.PAY_DELIVERY;
import static binplus.foodiswill.Utility.Constants.PAY_LOCATION;
import static binplus.foodiswill.Utility.Constants.PAY_METHOD;
import static binplus.foodiswill.Utility.Constants.PAY_STATUS;
import static binplus.foodiswill.Utility.Constants.PAY_TIME;
import static com.android.volley.VolleyLog.TAG;


public class Payment_fragment extends Fragment {
    //RelativeLayout confirm;
    Button confirm,btn_pay;
    Module module;
    private DatabaseCartHandler db_cart;
    private Session_management sessionManagement;
    TextView payble_ammount, my_wallet_ammount, used_wallet_ammount, used_coupon_ammount, order_ammount;
    private String getlocation_id = "";
    private String getstore_id = "",order_pay_id="",arr_cart_data="";
    String couponValue="0",couponCode="";
    private double wamt=0;
    private String gettime = "";
    private String getdate = "";
    private String getuser_id = "";
    private Double rewards;
    RadioButton rb_Store, rb_Cod,pay_now, rb_card, rb_Netbanking, rb_paytm;
    CheckBox checkBox_Wallet;
    CheckBox checkBox_coupon;
    EditText et_Coupon;
    String getvalue="";
    String text;
    String deli_charges="";
    Dialog loadingBar;
    String Used_Wallet_amount , Wallet_amount;
    String total_amount;
    String order_total_amount,fAmount="0";
    RadioGroup radioGroup;
    String Prefrence_TotalAmmount;
    String getwallet;
    String getcharge ;
    LinearLayout Promo_code_layout, Coupon_and_wallet;
    RelativeLayout Apply_Coupon_Code, Relative_used_wallet, Relative_used_coupon;
    String bill_name="",bill_address="",bill_pincode="",bill_mobile="",bill_email="";
    String cod_status="",extra_charges="",gateway_status="";
    public Payment_fragment() {

    }

    public static Payment_fragment newInstance(String param1, String param2) {
        Payment_fragment fragment = new Payment_fragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingBar=new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        loadingBar.setContentView( R.layout.progressbar );
        loadingBar.setCanceledOnTouchOutside(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_payment_method, container, false);
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.payment));

        Prefrence_TotalAmmount = SharedPref.getString(getActivity(), BaseURL.TOTAL_AMOUNT);
        loadingBar=new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        loadingBar.setContentView( R.layout.progressbar );
        loadingBar.setCanceledOnTouchOutside(false);
        module=new Module(getActivity());

        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                getvalue = radioButton.getText().toString();
            }
        });


        bill_name = getArguments().getString("bill_name");
        bill_mobile = getArguments().getString("bill_mobile");
        bill_address = getArguments().getString("bill_address");
        bill_pincode = getArguments().getString("bill_pincode");
        bill_email = getArguments().getString("bill_email");
        Log.e("addresss_data",""+bill_name+" - "+bill_mobile+" - "+bill_address+" - "+bill_pincode+" - "+bill_email);
//        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/bold.ttf" );
        checkBox_Wallet = (CheckBox) view.findViewById(R.id.use_wallet);
        //     checkBox_Wallet.setTypeface(font);
        rb_Store = (RadioButton) view.findViewById(R.id.use_store_pickup);
//        rb_Store.setTypeface(font);
        getLinks();
        rb_Cod = (RadioButton) view.findViewById(R.id.use_COD);
        pay_now = (RadioButton) view.findViewById(R.id.pay_now);
        btn_pay=view.findViewById(R.id.btn_pay);

        //   rb_Cod.setTypeface(font);
        rb_card = (RadioButton) view.findViewById(R.id.use_card);
        //    rb_card.setTypeface(font);
        rb_Netbanking = (RadioButton) view.findViewById(R.id.use_netbanking);
        //   rb_Netbanking.setTypeface(font);
        rb_paytm = (RadioButton) view.findViewById(R.id.use_wallet_ammount);
        //   rb_paytm.setTypeface(font);
        checkBox_coupon = (CheckBox) view.findViewById(R.id.use_coupon);
        //   checkBox_coupon.setTypeface(font);
        et_Coupon = (EditText) view.findViewById(R.id.et_coupon_code);
        Promo_code_layout = (LinearLayout) view.findViewById(R.id.prommocode_layout);
        Apply_Coupon_Code = (RelativeLayout) view.findViewById(R.id.apply_coupoun_code);

       Apply_Coupon_Code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cpn=et_Coupon.getText().toString().trim();
                if(cpn.isEmpty())
                {
                    et_Coupon.setError("Enter Code");
                    et_Coupon.requestFocus();
                }
                else
                {
                    Coupon_code(cpn,total_amount);
                }


            }
        });

        sessionManagement = new Session_management(getActivity());


        Coupon_and_wallet = (LinearLayout) view.findViewById(R.id.coupon_and_wallet);
        Relative_used_wallet = (RelativeLayout) view.findViewById(R.id.relative_used_wallet);
        Relative_used_coupon = (RelativeLayout) view.findViewById(R.id.relative_used_coupon);

        //Show  Wallet
        getwallet = SharedPref.getString(getActivity(), BaseURL.KEY_WALLET_Ammount);
        my_wallet_ammount = (TextView) view.findViewById(R.id.user_wallet);
        // my_wallet_ammount.setText(getwallet+getActivity().getString(R.string.currency));
        db_cart = new DatabaseCartHandler(getActivity());
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener()

        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Fragment fm = new HomeFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();
                    return true;
                }
                return false;
            }
        });


        total_amount = getArguments().getString("total");
        order_total_amount = getArguments().getString("total");
        getdate = getArguments().getString("getdate");
        gettime = getArguments().getString("gettime");
        getlocation_id = getArguments().getString("getlocationid");
        deli_charges = getArguments().getString("deli_charges");
        getstore_id = getArguments().getString("getstoreid");
        getcharge = getArguments().getString( "deli_charges" );

        payble_ammount = (TextView) view.findViewById(R.id.payable_ammount);
        order_ammount = (TextView) view.findViewById(R.id.order_ammount);
        //  used_wallet_ammount = (TextView) view.findViewById(R.id.used_wallet_ammount);
         used_coupon_ammount = (TextView) view.findViewById(R.id.used_coupon_ammount);
        payble_ammount.setText(getActivity().getString(R.string.currency)+total_amount);
        order_ammount.setText(getActivity().getString(R.string.currency)+order_total_amount);

        //    Toast.makeText( getActivity(),"charge" +getcharge,Toast.LENGTH_LONG).show();


//
//        CheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if (isChecked) {
//                   // Use_Wallet_Ammont();
//
//                    //getWalletAmount("18");
//
//                   // Coupon_and_wallet.setVisibility(View.VISIBLE);
//                   // Relative_used_wallet.setVisibility(View.VISIBLE);
//                  /*  if (rb_card.isChecked() || rb_Netbanking.isChecked() || rb_paytm.isChecked()) {
//                        rb_card.setChecked(false);
//                        rb_Netbanking.setChecked(false);
//                        rb_paytm.setChecked(false);
//                    }
//                } else {
//                    if (payble_ammount != null) {
//                        rb_Cod.setText(getResources().getString(R.string.cash));
//                        rb_card.setClickable(true);
//                        rb_card.setTextColor(getResources().getColor(R.color.dark_black));
//                        rb_Netbanking.setClickable(true);
//                        rb_Netbanking.setTextColor(getResources().getColor(R.color.dark_black));
//                        rb_paytm.setClickable(true);
//                        rb_paytm.setTextColor(getResources().getColor(R.color.dark_black));
//
//                        checkBox_coupon.setClickable(true);
//                        checkBox_coupon.setTextColor(getResources().getColor(R.color.dark_black));
//                    }*/
//                    final String Ammount = SharedPref.getString(getActivity(), BaseURL.TOTAL_AMOUNT);
//                    final String WAmmount = SharedPref.getString(getActivity(), BaseURL.KEY_WALLET_Ammount);
//                    my_wallet_ammount.setText(WAmmount+getActivity().getResources().getString(R.string.currency));
//                    payble_ammount.setText(Ammount+getResources().getString(R.string.currency));
////                    used_wallet_ammount.setText("");
//                    Relative_used_wallet.setVisibility(View.GONE);
//                    if (checkBox_coupon.isChecked()) {
//                        final String ammount = SharedPref.getString(getActivity(), BaseURL.COUPON_TOTAL_AMOUNT);
//                        payble_ammount.setText(ammount+getResources().getString(R.string.currency));
//                    }
//                }
//            }
//        });
        checkBox_coupon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()

        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Promo_code_layout.setVisibility(View.VISIBLE);
                    Coupon_and_wallet.setVisibility(View.VISIBLE);
                    Relative_used_coupon.setVisibility(View.VISIBLE);
                    payble_ammount.setText(getResources().getString(R.string.currency)+total_amount);
                    used_coupon_ammount.setVisibility(View.GONE);

                    if (rb_Store.isChecked() || rb_Cod.isChecked() || rb_card.isChecked() || rb_Netbanking.isChecked() || rb_paytm.isChecked()) {
                        rb_Store.setChecked(false);
//                        rb_Cod.setChecked(false);
                        rb_card.setChecked(false);
                        rb_Netbanking.setChecked(false);
                        rb_paytm.setChecked(false);
                    }
                } else {
                    total_amount=order_total_amount;
                    payble_ammount.setText(getResources().getString(R.string.currency)+total_amount);
                    couponCode="";
                    couponValue="0";
                    et_Coupon.setText("");
                    Relative_used_coupon.setVisibility(View.GONE);
                    Promo_code_layout.setVisibility(View.GONE);
                }
            }
        });


        //confirm = (RelativeLayout) view.findViewById(R.id.confirm_order);
        confirm = (Button) view.findViewById(R.id.confirm_order);
        confirm.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (ConnectivityReceiver.isConnected()) {

                    // confirm.setEnabled(false);
                    //  Toast.makeText(getActivity(),"p"+total_amount+"\n o"+order_total_amount,Toast.LENGTH_LONG).show();
//                    if (checkBox_Wallet.isChecked()){
//                        getuser_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
//
//                        Usewalletfororder(getuser_id,Used_Wallet_amount);
//                        checked();
//
//                    }
//                    else {
//                        checked();
//
//                    }
//

                    checked();

                } else {
                    confirm.setEnabled(true);
                    
                    ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
                }
            }
        });

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //module.showToast("Please wait..");
                onlinePay();
            }
        });
        return view;
    }

    private void Coupon_code(final String coupon, final String tl_amount) {
        loadingBar.show();
        String json_tag="json_coupon_code";
        HashMap<String,String> params=new HashMap<>();
        params.put("coupon",coupon);
        params.put("user_id",sessionManagement.getUserDetails().get(KEY_ID));
        CustomVolleyJsonRequest customVolleyJsonRequest=new CustomVolleyJsonRequest(Request.Method.POST, BaseURL.URL_GET_COUPON, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingBar.dismiss();
         try
         {
             Log.e("ressssss",""+response.toString());
             boolean resp=response.getBoolean("responce");
             if(resp)
             {
                 couponCode=coupon;
               String type=response.getString("type");
                   double mnOrderLimit=Double.parseDouble(response.getString("minimum_order_value").toString());
                   double orderAmount=Double.parseDouble(tl_amount);
                   if(orderAmount>=mnOrderLimit)
                   {
                       if(type.equalsIgnoreCase("flat"))
                       {
                           double cpValue=Double.parseDouble(response.getString("value"));
                           couponValue=String.valueOf(cpValue);
                           fAmount=String.valueOf(orderAmount-cpValue);
                           payble_ammount.setText(getResources().getString(R.string.currency)+fAmount);
                           total_amount=fAmount;
                           used_coupon_ammount.setText("-(" + getActivity().getResources().getString(R.string.currency) + String.valueOf(cpValue) + ")");
                           used_coupon_ammount.setVisibility(View.VISIBLE);
                           Promo_code_layout.setVisibility(View.GONE);


                       }
                       else if(type.equalsIgnoreCase("percentage"))
                       {
                           double cpValue=0;
                           double db_pr=Double.parseDouble(response.getString("value"));
                           double maxLimit=Double.parseDouble(response.getString("maximum_discount_offered_in_rs"));
                         double per=(orderAmount*db_pr)/100;
                           DecimalFormat df=new DecimalFormat("#.##");
                           per=Double.valueOf(df.format(per));
                         if(per>=maxLimit)
                         {
                            cpValue=maxLimit;
                         }
                         else
                         {
                           cpValue=per;
                         }
                           couponValue=String.valueOf(cpValue);
                           fAmount=String.valueOf(orderAmount-cpValue);
                         total_amount=fAmount;
                           payble_ammount.setText(getResources().getString(R.string.currency)+fAmount);
                           used_coupon_ammount.setText("-(" + getActivity().getResources().getString(R.string.currency) + String.valueOf(cpValue) + ")");
                           used_coupon_ammount.setVisibility(View.VISIBLE);
                           Promo_code_layout.setVisibility(View.GONE);
                       }
                   }
                   else
                   {
                       module.showToast("Minimum Order Amount is "+String.valueOf(mnOrderLimit));
                   }

             }
             else
             {
              module.showToast(""+response.getString("msg"));
             }

         }
         catch (Exception ex)
         {
             ex.printStackTrace();
             module.showToast("Something Went Wrong");

         }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                String msg=module.VolleyErrorMessage(error);
                if(!msg.equals(""))
                {
                    Toast.makeText(getActivity(),""+msg,Toast.LENGTH_LONG).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(customVolleyJsonRequest,json_tag);

    }


    @Override
    public void onStart() {
        super.onStart();

        getuser_id = sessionManagement.getUserDetails().get(KEY_ID);
        // Toast.makeText(getActivity(),""+getuser_id,Toast.LENGTH_LONG).show();
        getWalletAmount(getuser_id);

        Integer randomNum = ServiceUtility.randInt(0, 9999999);
        order_pay_id=randomNum.toString();

    }

    private void getWalletAmount(String user_id)
    {
        String json_wallet_tag="json_wallet_tag";
        HashMap<String,String> params=new HashMap<String, String>();
        params.put("user_id",user_id);

        CustomVolleyJsonRequest customVolleyJsonRequest=new CustomVolleyJsonRequest(Request.Method.POST,BaseURL.WALLET_AMOUNT_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    //Toast.makeText(getActivity(),"Something went wrong"+response,Toast.LENGTH_LONG).show();
                    String status=response.getString("status");
                    if(status.equals("success"))
                    {
                        wamt=Double.parseDouble(response.getString("data"));
                    }
                    else if(status.equals("failed"))
                    {
                        wamt=Double.parseDouble(response.getString("data"));
                    }
                    my_wallet_ammount.setText(getActivity().getString(R.string.currency)+" "+wamt);
                }
                catch (Exception ex)
                {
                    // Toast.makeText(getActivity(),"Something went wrong"+ex.getMessage(),Toast.LENGTH_LONG).show();
                }

                // Toast.makeText(getActivity(),"Response :"+response,Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String msg=module.VolleyErrorMessage(error);
                if(!msg.equals(""))
                {
                    Toast.makeText(getActivity(),""+msg,Toast.LENGTH_LONG).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(customVolleyJsonRequest,json_wallet_tag);
    }

    private void attemptOrder() {
        ArrayList<HashMap<String, String>> items = db_cart.getCartAll();
        //rewards = Double.parseDouble(db_cart.getColumnRewards());
        rewards = Double.parseDouble("0");
        if (items.size() > 0) {
            JSONArray passArray = new JSONArray();
            for (int i = 0; i < items.size(); i++) {
                HashMap<String, String> map = items.get(i);
                // String unt=
                JSONObject jObjP = new JSONObject();
                try {
                    jObjP.put("product_id", map.get("product_id"));
                    jObjP.put("product_name", map.get("product_name"));
                    jObjP.put("qty", map.get("qty"));
                    jObjP.put("unit_value", map.get("unit_price"));
                    jObjP.put("unit", map.get("unit"));
                    jObjP.put("price", map.get("price"));
                    jObjP.put("mrp",map.get("mrp"));
                    jObjP.put("rewards", "0");
                    passArray.put(jObjP);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            getuser_id = sessionManagement.getUserDetails().get(KEY_ID);

            if (ConnectivityReceiver.isConnected()) {
                Date date=new Date();
                SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
                String g=dateFormat.format(date);
                SimpleDateFormat dateFormat1=new SimpleDateFormat("hh:mm a");
                String t=dateFormat1.format(date);

                //    gettime=t+" - "+t.toString();
                //   Toast.makeText(getActivity(),"Time"+t,Toast.LENGTH_LONG).show();
                getdate=g;
         if(sessionManagement.isOnlinePay())
         {
            gettime=sessionManagement.getPayDetails().get(PAY_TIME);
            getlocation_id=sessionManagement.getPayDetails().get(PAY_LOCATION);
            getvalue=sessionManagement.getPayDetails().get(PAY_METHOD);
            total_amount=sessionManagement.getPayDetails().get(PAY_AMT);
            getcharge=sessionManagement.getPayDetails().get(PAY_DELIVERY);
//            module.showToast("problem");
            Log.e("sessssss",""+sessionManagement.getPayDetails().toString());

         }else{
//             module.showToast("No problem");
             getvalue="Cash On Delivery";
         }

                //gettime="03:00 PM - 03:30 PM";
                // getdate="2019-7-23";
//Toast.makeText(getActivity(), "from:" + gettime + "\ndate:" + getdate +
                //      "\n" + "\nuser_id:" + getuser_id + "\n" + getlocation_id + getstore_id + "\ndata:" + passArray.toString(),Toast.LENGTH_LONG).show();

                //    Toast.makeText(getActivity(),""+deli_charges,Toast.LENGTH_SHORT).show();
                makeAddOrderRequest(getdate, gettime, getuser_id, getlocation_id, getstore_id, passArray);


            }
        }
    }

    private void makeAddOrderRequest(String date, String gettime, String userid, String
            location, String store_id, JSONArray passArray) {

        loadingBar.show();
        String tag_json_obj = "json_add_order_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("date", date);
        params.put("time", gettime);
        params.put("user_id", userid);
        params.put("location", location);
        params.put( "delivery_charges",getcharge );
        //   params.put("store_id", store_id);
        params.put("total_ammount",total_amount);
        params.put("delivery_charges",deli_charges);
        params.put("payment_method", getvalue);
        //coupon code
        params.put("coupon_code", couponCode);
        params.put("discount_amt", couponValue);
        params.put("disinfection_charge", String.valueOf(extra_charges));
        params.put("data", passArray.toString());
        Log.e("order_details",""+params.toString());
//        // Toast.makeText(getActivity(),""+passArray,Toast.LENGTH_LONG).show();
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.ADD_ORDER_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("odd", response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        // JSONObject object = response.getJSONObject("data");
                        String msg=response.getString("data");
                        db_cart.clearCart();
                        sessionManagement.clearPay();
                        loadingBar.dismiss();
                        Bundle args = new Bundle();
                        Fragment fm = new Thanks_fragment();
                        args.putString("msg", msg);
                        // args.putString("msgarb",msg_arb);
                        fm.setArguments(args);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                .addToBackStack(null).commit();

                        //      Toast.makeText(getActivity(),"success",Toast.LENGTH_LONG).show();
                    }
                    else

                    {
                        loadingBar.dismiss();
                        Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    loadingBar.dismiss();
                    Toast.makeText(getActivity(),""+e.getMessage() ,Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                String msg=module.VolleyErrorMessage(error);
                error.printStackTrace();
                if(!msg.equals(""))
                {
                    Toast.makeText(getActivity(),""+msg,Toast.LENGTH_LONG).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void Usewalletfororder(String userid, String Wallet) {
        String tag_json_obj = "json_add_order_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userid);
        params.put("wallet_amount", Wallet);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.Wallet_CHECKOUT, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Toast.makeText(getActivity(),""+response,Toast.LENGTH_LONG).show();
                    String status = response.getString("responce");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String msg=module.VolleyErrorMessage(error);
                if(!msg.equals(""))
                {
                    Toast.makeText(getActivity(),""+msg,Toast.LENGTH_LONG).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void Use_Wallet_Ammont() {
        final String Wallet_Ammount = SharedPref.getString(getActivity(), BaseURL.KEY_WALLET_Ammount);
        final String Coupon_Ammount = SharedPref.getString(getActivity(), BaseURL.COUPON_TOTAL_AMOUNT);
        final String Ammount = SharedPref.getString(getActivity(), BaseURL.TOTAL_AMOUNT);
        if (NetworkConnection.connectionChecking(getActivity())) {
            RequestQueue rq = Volley.newRequestQueue(getActivity());
            StringRequest postReq = new StringRequest(Request.Method.POST, BaseURL.BASE_URL+"index.php/api/wallet_on_checkout",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("eclipse", "Response=" + response);
                            try {
                                JSONObject object = new JSONObject(response);
                                JSONArray Jarray = object.getJSONArray("final_amount");
                                for (int i = 0; i < Jarray.length(); i++) {
                                    JSONObject json_data = Jarray.getJSONObject(i);
                                    Wallet_amount = json_data.getString("wallet");
//                                     Used_Wallet_amount = json_data.getString("used_wallet");
                                    total_amount = json_data.getString("total");


                                  /*  if (total_amount.equals(Wallet_amount)) {
                                        rb_Cod.setText("Home Delivery");
                                        getvalue = rb_Cod.getText().toString();
                                        rb_card.setClickable(false);
                                        rb_card.setTextColor(getResources().getColor(R.color.gray));
                                        rb_Netbanking.setClickable(false);
                                        rb_Netbanking.setTextColor(getResources().getColor(R.color.gray));
                                        rb_paytm.setClickable(false);
                                        rb_paytm.setTextColor(getResources().getColor(R.color.gray));
                                        checkBox_coupon.setClickable(false);
                                        checkBox_coupon.setTextColor(getResources().getColor(R.color.gray));
                                    } else {
                                        if (total_amount != null) {
                                            rb_Cod.setText("Cash On Delivery");
                                            rb_card.setClickable(true);
                                            rb_card.setTextColor(getResources().getColor(R.color.dark_black));
                                            rb_Netbanking.setClickable(true);
                                            rb_Netbanking.setTextColor(getResources().getColor(R.color.dark_black));
                                            rb_paytm.setClickable(true);
                                            rb_paytm.setTextColor(getResources().getColor(R.color.dark_black));
                                            checkBox_coupon.setClickable(true);
                                            checkBox_coupon.setTextColor(getResources().getColor(R.color.dark_black));
                                        }
                                    }*/
                                    payble_ammount.setText(total_amount+getResources().getString(R.string.currency));
                                    // used_wallet_ammount.setText("(" + getResources().getString(R.string.currency) + Used_Wallet_amount + ")");
                                    SharedPref.putString(getActivity(), BaseURL.WALLET_TOTAL_AMOUNT, total_amount);
                                    my_wallet_ammount.setText(Wallet_amount+getResources().getString(R.string.currency));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error [" + error + "]");
                }
            }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    if (checkBox_Wallet.isChecked()){
                        params.put("wallet_amount", Wallet_Ammount);
                    }else {
                        params.put("total_amount", Ammount);

                    }

                   /* if (checkBox_coupon.isChecked()) {
                        params.put("total_amount", Coupon_Ammount);
                    } else {
                        params.put("total_amount", Ammount);

                    }*/
                    return params;
                }
            };
            rq.add(postReq);
        } else {
            Intent intent = new Intent(getActivity(), NetworkError.class);
            startActivity(intent);
        }
    }

   /* private void Coupon_code() {
        final String Ammount = SharedPref.getString(getActivity(), BaseURL.TOTAL_AMOUNT);
        final String Wallet_Ammount = SharedPref.getString(getActivity(), BaseURL.WALLET_TOTAL_AMOUNT);
        final String Coupon_code = et_Coupon.getText().toString();
        if (NetworkConnection.connectionChecking(getActivity())) {
            RequestQueue rq = Volley.newRequestQueue(getActivity());
            StringRequest postReq = new StringRequest(Request.Method.POST, BaseURL.COUPON_CODE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("eclipse", "Response=" + response);
                            try {
                                JSONObject obj = new JSONObject(response);
                                total_amount = obj.getString("Total_amount");
                                String Used_coupon_amount = obj.getString("coupon_value");
                                if (obj.optString("responce").equals("true")) {
                                    payble_ammount.setText(total_amount+getResources().getString(R.string.currency));
                                    SharedPref.putString(getActivity(), BaseURL.COUPON_TOTAL_AMOUNT, total_amount);
                                    Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                    used_coupon_ammount.setText("(" + getActivity().getResources().getString(R.string.currency) + Used_coupon_amount + ")");
                                    Promo_code_layout.setVisibility(View.GONE);

                                } else {
                                    Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                    et_Coupon.setText("");
                                    used_coupon_ammount.setText("");
                                    payble_ammount.setText(total_amount+getResources().getString(R.string.currency));
                                    Promo_code_layout.setVisibility(View.GONE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error [" + error + "]");
                }
            }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("coupon_code", Coupon_code);
                    if (checkBox_Wallet.isChecked()) {
                        params.put("payable_amount", Wallet_Ammount);
                    } else {
                        params.put("payable_amount", Ammount);
                    }
                    return params;
                }
            };
            rq.add(postReq);
        } else {
            Toast.makeText(getActivity(), "Somthing Went Wrong", Toast.LENGTH_SHORT).show();
        }

    }*/


    private void checked() {
        if (checkBox_Wallet.isChecked()) {

            // Toast.makeText(getActivity(),"checkBox_Wallet",Toast.LENGTH_LONG).show();
            double t=Double.parseDouble(total_amount);

            if(wamt>0)
            {
                rb_Cod.setClickable( false );
                rb_Cod.setVisibility( View.INVISIBLE );
                Usewalletfororder(getuser_id,String.valueOf(t));

                String amt = String.valueOf( t );



                attemptOrder();

            }

            else
            {
                Toast.makeText(getActivity(),"You don't have enough wallet amount.\n Please select another option",Toast.LENGTH_LONG).show();
            }



        }
        else if (rb_Store.isChecked()) {
            // Toast.makeText(getActivity(),"rb_Store",Toast.LENGTH_LONG).show();
            attemptOrder();
        }
        else if (rb_Cod.isChecked()) {

            //Toast.makeText(getActivity(),"rb_Cod",Toast.LENGTH_LONG).show();
            attemptOrder();
        }
        else if(pay_now.isChecked())
        {
//              sessionManagement.updatePaySection(getdate,gettime,getlocation_id,deli_charges,getvalue,couponCode,couponValue,String.valueOf(extra_charges));
//            Log.e("sesstion_data",""+getdate+" - "+gettime+" - "+getlocation_id+" - "+deli_charges+" - "+getvalue+" - "+couponCode+" - "+couponValue+" - "+String.valueOf(extra_charges));
            onlinePay();
        }
        else {
            Toast.makeText(getActivity(), "Please Select Payment Method", Toast.LENGTH_SHORT).show();
        }
       /* if (rb_card.isChecked()) {
            Intent myIntent = new Intent(getActivity(), PaymentGatWay.class);
            if (checkBox_Wallet.isChecked()) {
                myIntent.putExtra("total", total_amount);
            } else {
                myIntent.putExtra("total", Prefrence_TotalAmmount);
                myIntent.putExtra("getdate", getdate);
                myIntent.putExtra("gettime", gettime);
                myIntent.putExtra("getlocationid", getlocation_id);
                myIntent.putExtra("getstoreid", getstore_id);
                myIntent.putExtra("getpaymentmethod", getvalue);
            }
            getActivity().startActivity(myIntent);
        }*/
       /* if (rb_Netbanking.isChecked()) {
            Intent myIntent1 = new Intent(getActivity(), PaymentGatWay.class);
            if (checkBox_Wallet.isChecked()) {
                myIntent1.putExtra("total", total_amount);

            } else {
                myIntent1.putExtra("total", Prefrence_TotalAmmount);
                myIntent1.putExtra("getdate", getdate);
                myIntent1.putExtra("gettime", gettime);
                myIntent1.putExtra("getlocationid", getlocation_id);
                myIntent1.putExtra("getstoreid", getstore_id);
                myIntent1.putExtra("getpaymentmethod", getvalue);
            }
            getActivity().startActivity(myIntent1);
        }*/
              /* if (rb_paytm.isChecked()) {
            Intent myIntent1 = new Intent(getActivity(), Paytm.class);
            if (checkBox_Wallet.isChecked()) {
                myIntent1.putExtra("total", total_amount);

            } else {
                myIntent1.putExtra("total", Prefrence_TotalAmmount);
                myIntent1.putExtra("getdate", getdate);
                myIntent1.putExtra("gettime", gettime);
                myIntent1.putExtra("getlocationid", getlocation_id);
                myIntent1.putExtra("getstoreid", getstore_id);
                myIntent1.putExtra("getpaymentmethod", getvalue);
            }
            getActivity().startActivity(myIntent1);

        }*/
       /* if (checkBox_coupon.isChecked()) {
            if (rb_Store.isChecked() || rb_Cod.isChecked()) {
                attemptOrder();
            } else {
                Toast.makeText(getActivity(), "Select Store Or Cod", Toast.LENGTH_SHORT).show();
            }


        }*/



    }

    @Override
    public void onResume() {
        super.onResume();
        if(sessionManagement.isOnlinePay() && sessionManagement.getPayDetails().get(PAY_STATUS).equalsIgnoreCase("success"))
        {
            attemptOrder();
        }
    }

    private void onlinePay() {
        String vAccessCode = ServiceUtility.chkNull(getResources().getString(R.string.access_code)).toString().trim();
        String vMerchantId = ServiceUtility.chkNull(getResources().getString(R.string.merchant_id)).toString().trim();
        String vCurrency = ServiceUtility.chkNull(getResources().getString(R.string.pay_currency)).toString().trim();
        String vAmount = ServiceUtility.chkNull(total_amount).toString().trim();
        String vEmail="";
        if(bill_email == null || bill_email.isEmpty())
        {
            vEmail=getResources().getString(R.string.billing_email);
        }
        else
        {
            vEmail=bill_email;
        }
        if(!vAccessCode.equals("") && !vMerchantId.equals("") && !vCurrency.equals("") && !vAmount.equals("")){
           getvalue="Pay Now";
            sessionManagement.updatePaySection(getdate,gettime,getlocation_id,deli_charges,getvalue,couponCode,couponValue,String.valueOf(extra_charges));
            Intent intent = new Intent(getActivity(), WebViewActivity.class);
            intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull(getResources().getString(R.string.access_code)).toString().trim());
            intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull(getResources().getString(R.string.merchant_id)).toString().trim());
            intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(order_pay_id).toString().trim());
            intent.putExtra(AvenuesParams.CURRENCY, ServiceUtility.chkNull(getResources().getString(R.string.pay_currency)).toString().trim());
            intent.putExtra(AvenuesParams.AMOUNT, ServiceUtility.chkNull(total_amount).toString().trim());
//            intent.putExtra(AvenuesParams.AMOUNT, ServiceUtility.chkNull("2").toString().trim());
            intent.putExtra(AvenuesParams.BILLING_NAME, ServiceUtility.chkNull(bill_name).toString().trim());
            intent.putExtra(AvenuesParams.BILLING_ADDRESS, ServiceUtility.chkNull(bill_address).toString().trim());
            intent.putExtra(AvenuesParams.BILLING_ZIP, ServiceUtility.chkNull(bill_pincode).toString().trim());
            intent.putExtra(AvenuesParams.BILLING_EMAIL, ServiceUtility.chkNull(vEmail).toString().trim());
            intent.putExtra(AvenuesParams.BILLING_TEL, ServiceUtility.chkNull(bill_mobile).toString().trim());
            intent.putExtra(AvenuesParams.BILLING_CITY, ServiceUtility.chkNull(getResources().getString(R.string.billing_city)).toString().trim());
            intent.putExtra(AvenuesParams.BILLING_STATE, ServiceUtility.chkNull(getResources().getString(R.string.billing_state)).toString().trim());
            intent.putExtra(AvenuesParams.BILLING_COUNTRY, ServiceUtility.chkNull(getResources().getString(R.string.billing_country)).toString().trim());
            intent.putExtra(AvenuesParams.REDIRECT_URL, ServiceUtility.chkNull(BaseURL.PAY_REDIRECT_URL).toString().trim());
            intent.putExtra(AvenuesParams.CANCEL_URL, ServiceUtility.chkNull(BaseURL.PAY_CANCEL_URL).toString().trim());
            intent.putExtra(AvenuesParams.RSA_KEY_URL, ServiceUtility.chkNull(BaseURL.PAY_RSA_URL).toString().trim());
            startActivity(intent);
        }else{
            module.showToast("All parameters are mandatory.");
        }
    }

    public void getLinks()
    {
        loadingBar.show();
        HashMap<String ,String> params = new HashMap<>();
        CustomVolleyJsonArrayRequest jsonRequest = new CustomVolleyJsonArrayRequest(Request.Method.POST,BaseURL.GET_LINKS, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                loadingBar.dismiss();
                try {
                    Log.e("link_data",""+response.toString());
                    JSONObject object = response.getJSONObject(0);
                    cod_status = object.getString("cod");
                    gateway_status = object.getString("gateway_status");
                    if (cod_status.equals("1"))
                    {
                        rb_Cod.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        rb_Cod.setVisibility(View.GONE);
                    }

                    if(gateway_status.equals("1")){
                        btn_pay.setVisibility(View.VISIBLE);
                    }else{
                        btn_pay.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest);
    }
}
