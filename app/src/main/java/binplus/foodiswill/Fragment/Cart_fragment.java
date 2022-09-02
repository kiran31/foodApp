package binplus.foodiswill.Fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


import binplus.foodiswill.Adapter.Cart_adapter;
import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.AppController;
import binplus.foodiswill.MainActivity;
import binplus.foodiswill.R;
import binplus.foodiswill.Verfication_activity;
import binplus.foodiswill.util.ConnectivityReceiver;
import binplus.foodiswill.util.CustomVolleyJsonArrayRequest;
import binplus.foodiswill.util.DatabaseCartHandler;
import binplus.foodiswill.util.Session_management;

import static binplus.foodiswill.Config.BaseURL.KEY_ID;
import static binplus.foodiswill.MainActivity.max_free_delivery_amt;


public class Cart_fragment extends Fragment implements View.OnClickListener {

  String user_id="";
  private static String TAG = Cart_fragment.class.getSimpleName();
  Module module;
  public static RecyclerView rv_cart;
  public static TextView tv_clear, tv_total, tv_item;
  public static RelativeLayout btn_checkout;
  public static LinearLayout linear_empty ,linear_cart ;

  //  private DatabaseHandler db;
  private DatabaseCartHandler db_cart;
  private Session_management sessionManagement;
  public static  TextView tvDiscount , tvDelivary ,tvSubTotal ,tvMrp ;
  String deli_charge ;
  Dialog loadingBar ;
  ScrollView cart_scroll ;
  public static RelativeLayout rel_out ;
  public Cart_fragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadingBar=new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
    loadingBar.setContentView( R.layout.progressbar );
    loadingBar.setCanceledOnTouchOutside(false);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_cart, container, false);
    ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.cart));

    module=new Module(getActivity());
    loadingBar=new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
    loadingBar.setContentView( R.layout.progressbar );
    loadingBar.setCanceledOnTouchOutside(false);
    sessionManagement = new Session_management(getActivity());
    sessionManagement.cleardatetime();

        user_id=sessionManagement.getUserDetails().get(KEY_ID);

    cart_scroll = view.findViewById( R.id.cart_scroll );
    rel_out = view.findViewById( R.id.rel_no );
    tv_clear = (TextView) view.findViewById(R.id.tv_cart_clear);
    // tv_total = (TextView) view.findViewById(R.id.tv_cart_total);
    tv_item = (TextView) view.findViewById(R.id.tv_cart_item);
    tvDiscount=(TextView)view.findViewById( R.id.totaldisc );
    tvMrp =(TextView)view.findViewById( R.id.totalmrp);
    tvDelivary=(TextView)view.findViewById( R.id.delivery_charge );
    tvSubTotal =(TextView)view.findViewById( R.id.subtotal ) ;
    btn_checkout = (RelativeLayout) view.findViewById(R.id.btn_cart_checkout);
    rv_cart = (RecyclerView) view.findViewById(R.id.rv_cart);
   rv_cart.setNestedScrollingEnabled(false);
    linear_cart = (LinearLayout)view.findViewById(R.id.lin_cart);
    linear_empty =view.findViewById(R.id.linear_empty);

//    rv_cart.setLayoutManager(new LinearLayoutManager(getActivity()));
    rv_cart.setLayoutManager(new GridLayoutManager(getActivity(),2));

    //db = new DatabaseHandler(getActivity());
    db_cart=new DatabaseCartHandler(getActivity());
//    deli_charge = getArguments().getString("deli_charges");
    // int deli_charges = Integer.parseInt( deli_charge);
    int items = db_cart.getCartCount();
    if (items== 0)
    {
      linear_empty.setVisibility(View.VISIBLE);
      rv_cart.setVisibility(View.GONE);
      linear_cart.setVisibility(View.GONE);
      btn_checkout.setVisibility(View.GONE);

    }
    getLinks();
    // int deli_charges = 10*items;
    String mrp= getTotMRp();
    String price=String.valueOf(db_cart.getTotalAmount());
    tvMrp.setText(getResources().getString(R.string.currency)+mrp);
    double m=Double.parseDouble(mrp);
    double p=Double.parseDouble(price);
    double d=m-p;
    double db = m-d ;
    tvDiscount.setText("-"+getResources().getString(R.string.currency)+String.valueOf(d));
    // double db = (m-d)+deli_charges ;
    // tvDelivary.setText(getResources().getString(R.string.currency)+deli_charges);
    tvSubTotal.setText(getResources().getString(R.string.currency)+db);



    ArrayList<HashMap<String, String>> map = db_cart.getCartAll();
//        final HashMap<String, String> map1 = map.get(0);
       Log.d("cart all ",""+map);

    Cart_adapter adapter = new Cart_adapter( map,getActivity());
    rv_cart.setAdapter(adapter);
    adapter.notifyDataSetChanged();

    updateData();
//

    tv_clear.setOnClickListener(this);
    btn_checkout.setOnClickListener(this);

    return view;
  }

  @Override
  public void onClick(View view) {
    int id = view.getId();

    if (id == R.id.tv_cart_clear) {
      // showdialog
      // Toast.makeText(getActivity(),""+db_cart.getCartCount(),Toast.LENGTH_LONG).show();
      showClearDialog();
    } else if (id == R.id.btn_cart_checkout) {

      if (ConnectivityReceiver.isConnected()) {
        if (db_cart.getCartCount()>0)
        {
          makeGetLimiteRequest();
        }
        else
        {
          Toast.makeText( getActivity(),"Cart is Empty",Toast.LENGTH_LONG ).show();
        }
      } else {
        ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
      }
      module.preventMultipleClick(btn_checkout);
    }
  }

  // update UI
  private void updateData() {
    // tv_total.setText(getActivity().getString(R.string.currency)+ db_cart.getTotalAmount());
    tv_item.setText("" + db_cart.getCartCount());
    ((MainActivity) getActivity()).setCartCounter("" + db_cart.getCartCount());
  }

  private void showClearDialog() {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
    alertDialog.setMessage(getResources().getString(R.string.sure_del));
    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
      }
    });
    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        // clear cart data
        db_cart.clearCart();
        ArrayList<HashMap<String, String>> map = db_cart.getCartAll();
        Log.d("cart all 2",""+map);
        Cart_adapter adapter = new Cart_adapter( map,getActivity());
        rv_cart.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        updateData();
        linear_cart.setVisibility( View.GONE );
        btn_checkout.setVisibility( View.GONE );
        linear_empty.setVisibility( View.VISIBLE );
        dialogInterface.dismiss();



      }
    });

    alertDialog.show();
  }

  /**
   * Method to make json array request where json response starts wtih
   */
  private void makeGetLimiteRequest() {

    JsonArrayRequest req = new JsonArrayRequest(BaseURL.GET_LIMITE_SETTING_URL,
            new Response.Listener<JSONArray>() {
              @Override
              public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                Double total_amount = Double.parseDouble(db_cart.getTotalAmount());


                try {
                  // Parsing json array response
                  // loop through each json object

                  boolean issmall = false;
                  boolean isbig = false;

                  // arraylist list variable for store data;
                  ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

                  for (int i = 0; i < response.length(); i++) {

                    JSONObject jsonObject = (JSONObject) response
                            .get(i);
                    int value;

                    if (jsonObject.getString("id").equals("1")) {
                      value = Integer.parseInt(jsonObject.getString("value"));

                      if (total_amount < value) {
                        issmall = true;
                        Toast.makeText(getActivity(), "" + jsonObject.getString("title") + " : " + value, Toast.LENGTH_SHORT).show();
                      }
                    } else if (jsonObject.getString("id").equals("2")) {
                      value = Integer.parseInt(jsonObject.getString("value"));

                      if (total_amount > value) {
                        isbig = true;
                        Toast.makeText(getActivity(), "" + jsonObject.getString("title") + " : " + value, Toast.LENGTH_SHORT).show();
                      }
                    }

                  }

                  if (!issmall && !isbig) {
                    if (sessionManagement.isLoggedIn()) {
                      Bundle args = new Bundle();
                      Fragment fm = new Delivery_fragment();
                      fm.setArguments(args);
                      FragmentManager fragmentManager = getFragmentManager();
                      fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                              .addToBackStack(null).commit();

                    } else {
                      //Toast.makeText(getActivity(), "Please login or regiter.\ncontinue", Toast.LENGTH_SHORT).show();
                      Intent i = new Intent(getActivity(), Verfication_activity.class);
                      startActivity(i);
                    }
                  }

                } catch (JSONException e) {
                  e.printStackTrace();
                  Toast.makeText(getActivity(),
                          "Error: " + e.getMessage(),
                          Toast.LENGTH_LONG).show();
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

    // Adding request to request queue
    AppController.getInstance().addToRequestQueue(req);

  }

  @Override
  public void onPause() {
    super.onPause();
    // unregister reciver
    getActivity().unregisterReceiver(mCart);
  }

  @Override
  public void onResume() {
    super.onResume();
    // register reciver
    getActivity().registerReceiver(mCart, new IntentFilter("Grocery_cart"));
  }
  public String getTotMRp()
  {
    ArrayList<HashMap<String, String>> list = db_cart.getCartAll();
    float sum=0;
    for(int i=0;i<list.size();i++)
    {
      final HashMap<String, String> map = list.get(i);

      float q=Float.parseFloat(map.get("qty"));
      float m=Float.parseFloat(map.get("mrp"));

      sum=sum+(q*m);
      //   Toast.makeText(getActivity(),""+q+"\n"+m,Toast.LENGTH_LONG).show();

    }
    if(sum!=0)
    {
      return String.valueOf(sum);
    }
    else
      return "0";
    //Toast.makeText(getActivity(),""+sum,Toast.LENGTH_LONG).show();
  }
  // broadcast reciver for receive data
  private BroadcastReceiver mCart = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {

      String type = intent.getStringExtra("type");

      if (type.contentEquals("update")) {
        updateData();
      }
    }
  };

  public void getLinks()
  {
    loadingBar.show();
    HashMap<String ,String> params = new HashMap<>();
    CustomVolleyJsonArrayRequest jsonRequest = new CustomVolleyJsonArrayRequest(Request.Method.POST,BaseURL.GET_LINKS, params, new Response.Listener<JSONArray>() {
      @Override
      public void onResponse(JSONArray response) {
        loadingBar.dismiss();
        try {
          JSONObject object = response.getJSONObject(0);
          max_free_delivery_amt= object.getString("max_amount");
        } catch (Exception e) {
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
