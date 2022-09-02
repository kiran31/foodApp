package binplus.foodiswill.Fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import binplus.foodiswill.Config.Module;
import binplus.foodiswill.Model.Support_info_model;
import binplus.foodiswill.AppController;
import binplus.foodiswill.MainActivity;
import binplus.foodiswill.R;
import binplus.foodiswill.util.ConnectivityReceiver;

public class About_us_fragment extends Fragment {

    private static String TAG = About_us_fragment.class.getSimpleName();

    private TextView tv_info;
    Dialog loadingBar;
    Module module;

    public About_us_fragment() {
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
        View view = inflater.inflate(R.layout.fragment_terms_condition, container, false);
        loadingBar=new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        loadingBar.setContentView( R.layout.progressbar );
        loadingBar.setCanceledOnTouchOutside(false);
        module=new Module(getActivity());
        tv_info = (TextView) view.findViewById(R.id.tv_info);

        String geturl = getArguments().getString("url");
        //   String title = getArguments().getString("title");

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.nav_about));

        // check internet connection
        if (ConnectivityReceiver.isConnected()) {
            makeGetInfoRequest(geturl);
        } else {
            ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
        }

        return view;
    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeGetInfoRequest(String url) {
        loadingBar.show();
        // Tag used to cancel the request
        String tag_json_obj = "json_info_req";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                loadingBar.dismiss();
                try {
                    // Parsing json array response
                    // loop through each json object

                    boolean status = response.getBoolean("responce");
                    if (status) {

                        List<Support_info_model> support_info_modelList = new ArrayList<>();

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Support_info_model>>() {
                        }.getType();

                        support_info_modelList = gson.fromJson(response.getString("data"), listType);

                        String desc = support_info_modelList.get(0).getPg_descri();
                        String title = support_info_modelList.get(0).getPg_title();

                        tv_info.setText(Html.fromHtml(desc));

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

