package binplus.foodiswill.Fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import binplus.foodiswill.Adapter.My_order_detail_adapter;
import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.Model.My_order_detail_model;

import binplus.foodiswill.AppController;
import binplus.foodiswill.MainActivity;
import binplus.foodiswill.R;
import binplus.foodiswill.util.ConnectivityReceiver;
import binplus.foodiswill.util.CustomVolleyJsonArrayRequest;
import binplus.foodiswill.util.CustomVolleyJsonRequest;
import binplus.foodiswill.util.Session_management;



public class My_order_detail_fragment extends Fragment {

    private static String TAG = My_order_detail_fragment.class.getSimpleName();
    Module module;
    private TextView tv_date, tv_time, tv_total, tv_delivery_charge;
    private RelativeLayout btn_cancle;
    private RecyclerView rv_detail_order;
    List<String> image_list;
    private String sale_id;
    Dialog loadingBar;
    private List<My_order_detail_model> my_order_detail_modelList = new ArrayList<>();

    public My_order_detail_fragment() {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_order_detail, container, false);
        loadingBar=new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        loadingBar.setContentView( R.layout.progressbar );
        loadingBar.setCanceledOnTouchOutside(false);
        module=new Module(getActivity());
        tv_date = (TextView) view.findViewById(R.id.tv_order_Detail_date);
        tv_time = (TextView) view.findViewById(R.id.tv_order_Detail_time);
        tv_delivery_charge = (TextView) view.findViewById(R.id.tv_order_Detail_deli_charge);
        tv_total = (TextView) view.findViewById(R.id.tv_order_Detail_total);
        btn_cancle = (RelativeLayout) view.findViewById(R.id.btn_order_detail_cancle);
        rv_detail_order = (RecyclerView) view.findViewById(R.id.rv_order_detail);
        image_list=new ArrayList<>();
        rv_detail_order.setLayoutManager(new LinearLayoutManager(getActivity()));
        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.order_fail));

        sale_id = getArguments().getString("sale_id");
        String total_rs = getArguments().getString("total");
        String date = getArguments().getString("date");
        String time = getArguments().getString("time");
        String status = getArguments().getString("status");
        String deli_charge = getArguments().getString("deli_charge");
        String type = getArguments().getString("type");

        if (status.equals("0")) {
            btn_cancle.setVisibility(View.VISIBLE);
        } else {
            btn_cancle.setVisibility(View.GONE);
        }

        tv_total.setText(total_rs);
        tv_date.setText(getResources().getString(R.string.date) + date);
        tv_time.setText(getResources().getString(R.string.time) + time);
        tv_delivery_charge.setText(getResources().getString(R.string.delivery_charge) + deli_charge);

        // check internet connection
        if (ConnectivityReceiver.isConnected()) {
            makeGetOrderDetailRequest(sale_id);
        } else {
            ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
        }

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();



            }
        });

        return view;
    }

    // alertdialog for cancle order
    private void showDeleteDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(getResources().getString(R.string.cancle_order_note));
        alertDialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Session_management sessionManagement = new Session_management(getActivity());
                String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

                // check internet connection
                if (ConnectivityReceiver.isConnected()) {
                    makeDeleteOrderRequest(sale_id, user_id);
                }

                dialogInterface.dismiss();

            }
        });

        alertDialog.show();

    }

    /**
     * Method to make json array request where json response starts wtih
     */
    private void makeGetOrderDetailRequest(String sale_id) {

        // Tag used to cancel the request
        String tag_json_obj = "json_order_detail_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("sale_id", sale_id);

        CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.POST,
                BaseURL.ORDER_DETAIL_URL, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d("saleeee", response.toString());

                Gson gson = new Gson();
                Type listType = new TypeToken<List<My_order_detail_model>>() {
                }.getType();

                my_order_detail_modelList = gson.fromJson(response.toString(), listType);

                My_order_detail_adapter adapter = new My_order_detail_adapter(my_order_detail_modelList);
                rv_detail_order.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (my_order_detail_modelList.isEmpty()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
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
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeDeleteOrderRequest(String sale_id, String user_id) {

        // Tag used to cancel the request
        String tag_json_obj = "json_delete_order_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("sale_id", sale_id);
        params.put("user_id", user_id);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.DELETE_ORDER_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        String msg = response.getString("message");
                        Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();

                        ((MainActivity) getActivity()).onBackPressed();

                    } else {
                        String error = response.getString("error");
                        Toast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
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

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


}
