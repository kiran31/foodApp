package binplus.foodiswill.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import binplus.foodiswill.Adapter.My_Pending_Order_adapter;
import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.Model.My_Pending_order_model;
import binplus.foodiswill.AppController;
import binplus.foodiswill.MainActivity;
import binplus.foodiswill.MyOrderDetail;
import binplus.foodiswill.R;
import binplus.foodiswill.util.ConnectivityReceiver;
import binplus.foodiswill.util.CustomVolleyJsonArrayRequest;
import binplus.foodiswill.util.RecyclerTouchListener;
import binplus.foodiswill.util.Session_management;

public class My_Pending_Order extends Fragment {

  private static String TAG = My_Pending_Order.class.getSimpleName();
  Module module;
  private RecyclerView rv_myorder;
  RelativeLayout rel_no ;
  private List<My_Pending_order_model> my_order_modelList = new ArrayList<>();
  TabHost tHost;
  Dialog loadingBar;
  String user_id="";
  SwipeRefreshLayout swipe;

  boolean swipe_flag=false;
  public My_Pending_Order() {

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
    View view = inflater.inflate(R.layout.fragment_my_pending_order, container, false);
    rel_no =view.findViewById( R.id.rel_no );

    loadingBar=new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
    loadingBar.setContentView( R.layout.progressbar );
    loadingBar.setCanceledOnTouchOutside(false);
    module=new Module(getActivity());
    swipe=view.findViewById(R.id.swipe);
    // handle the touch event if true
    view.setFocusableInTouchMode(true);
    view.requestFocus();
    view.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        // check user can press back button or not
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

//                    binplus.Jabico.Fragment fm = new Home_fragment();
//                    android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                            .addToBackStack(null).commit();
          return true;
        }
        return false;
      }
    });

    rv_myorder = (RecyclerView) view.findViewById(R.id.rv_myorder);
    rv_myorder.setLayoutManager(new LinearLayoutManager(getActivity()));

    Session_management sessionManagement = new Session_management(getActivity());
     user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

    // check internet connection
    if (ConnectivityReceiver.isConnected())

    {
      makeGetOrderRequest(user_id);
    } else

    {
      ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
    }

    swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {

        if (ConnectivityReceiver.isConnected())
        {
          swipe_flag=true;
          makeGetOrderRequest(user_id);
        } else

        {
          ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
        }

      }
    });

    // recyclerview item click listener
    rv_myorder.addOnItemTouchListener(new
            RecyclerTouchListener(getActivity(), rv_myorder, new RecyclerTouchListener.OnItemClickListener()
    {
      @Override
      public void onItemClick(View view, int position) {
        Bundle args = new Bundle();
        String sale_id = my_order_modelList.get(position).getSale_id();
        String date = my_order_modelList.get(position).getOn_date();
        String time = my_order_modelList.get(position).getDelivery_time_from() + "-" + my_order_modelList.get(position).getDelivery_time_to();
        String total = my_order_modelList.get(position).getTotal_amount();
        String status = my_order_modelList.get(position).getStatus();
        String deli_charge = my_order_modelList.get(position).getDelivery_charge();
        Intent intent=new Intent(getContext(), MyOrderDetail.class);
        intent.putExtra("sale_id", sale_id);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("total", total);
        intent.putExtra("status", status);
        intent.putExtra("deli_charge", deli_charge);
        intent.putExtra("type", "pending");
        intent.putExtra("r_name",my_order_modelList.get(position).getReceiver_name());
        intent.putExtra("r_mobile",my_order_modelList.get(position).getReceiver_mobile());
        intent.putExtra("s_name", my_order_modelList.get(position).getSocity_name());
        intent.putExtra("pincode", my_order_modelList.get(position).getPincode());
        intent.putExtra("address",my_order_modelList.get(position).getDelivery_address());
        intent.putExtra("coupon_code",my_order_modelList.get(position).getCoupan_code());
        intent.putExtra("disinfection_charge",my_order_modelList.get(position).getDisinfection_charge());
        intent.putExtra("discount_amt",my_order_modelList.get(position).getDiscount_amt());
        startActivity(intent);
      }

      @Override
      public void onLongItemClick(View view, int position) {

      }
    }));

    return view;
  }

  /**
   * Method to make json array request where json response starts wtih
   */
  private void makeGetOrderRequest(String userid) {
    loadingBar.show();
    String tag_json_obj = "json_socity_req";
    my_order_modelList.clear();
    Map<String, String> params = new HashMap<String, String>();
    params.put("user_id", userid);

    CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.POST,
            BaseURL.GET_ORDER_URL, params, new Response.Listener<JSONArray>() {

      @Override
      public void onResponse(JSONArray response) {
        loadingBar.dismiss();
        if(swipe_flag){
          swipe_flag=false;
          swipe.setRefreshing(false);
        }
        Log.d("pending_orders", response.toString());

        Gson gson = new Gson();
        Type listType = new TypeToken<List<My_Pending_order_model>>() {
        }.getType();

        my_order_modelList = gson.fromJson(response.toString(), listType);
        My_Pending_Order_adapter myPendingOrderAdapter = new My_Pending_Order_adapter(my_order_modelList);
        rv_myorder.setAdapter(myPendingOrderAdapter);
        myPendingOrderAdapter.notifyDataSetChanged();

        if (my_order_modelList.isEmpty()) {
          rel_no.setVisibility(View.VISIBLE);
          rv_myorder.setVisibility(View.GONE);
          Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
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

    // Adding request to request queue
    AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

  }

}
