package binplus.foodiswill.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.Fragment;

import android.app.FragmentManager;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;

import binplus.foodiswill.AppController;
import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.MainActivity;
import binplus.foodiswill.My_Order_activity;
import binplus.foodiswill.R;
import binplus.foodiswill.util.CustomVolleyJsonRequest;

import static android.content.Context.MODE_PRIVATE;



public class Thanks_fragment extends Fragment implements View.OnClickListener {

    TextView tv_info,tv_note;
    RelativeLayout btn_home, btn_order,btn_rate;
    Dialog loadingBar ;
    SharedPreferences preferences;
    Module module;
    String language;
    public Thanks_fragment() {
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
        View view = inflater.inflate(R.layout.fragment_order_thanks, container, false);
        loadingBar=new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        loadingBar.setContentView( R.layout.progressbar );
        loadingBar.setCanceledOnTouchOutside(false);
        module=new Module(getActivity());

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.thank_you));
        preferences = getActivity().getSharedPreferences("lan", MODE_PRIVATE);
        language=preferences.getString("language","");

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
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
  getAppSettingData();
        String data = getArguments().getString("msg");
        String dataarb=getArguments().getString("msg");
        tv_info = (TextView) view.findViewById(R.id.tv_thank_info);
        tv_note = (TextView) view.findViewById(R.id.tv_note);
        btn_home = (RelativeLayout) view.findViewById(R.id.btn_thank_home);
        btn_order = (RelativeLayout) view.findViewById(R.id.btn_track_order);
        btn_rate = (RelativeLayout) view.findViewById(R.id.btn_rate);

        if (language.contains("english")) {
            tv_info.setText(Html.fromHtml(data));
        }else {
            tv_info.setText(Html.fromHtml(dataarb));       }


        btn_home.setOnClickListener(this);
        btn_order.setOnClickListener(this);
        btn_rate.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_thank_home) {
            Fragment fm = new HomeFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                    .addToBackStack(null).commit();
        }
        else if (id == R.id.btn_track_order) {
            Intent myIntent = new Intent(getActivity(), My_Order_activity.class);
            getActivity().startActivity(myIntent);
        }else if(id == R.id.btn_rate){
            new Module(getActivity()).rateApp();
        }


    }

    public void getAppSettingData()
    {
        loadingBar.show();
        String json_tag="json_app_tag";
        HashMap<String,String> map=new HashMap<>();

        CustomVolleyJsonRequest request=new CustomVolleyJsonRequest(Request.Method.POST, BaseURL.GET_VERSTION_DATA, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("app_setting",response.toString());
                loadingBar.dismiss();
                try
                {
                    boolean sts=response.getBoolean("responce");

                    if(sts)
                    {

                        JSONObject object=response.getJSONObject("data");
                        String rate_msg=object.getString("rate_msg");
                        tv_note.setText(Html.fromHtml(rate_msg));

                    }
                    else
                    {
                        Toast.makeText(getActivity(),""+response.getString("error"),Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                String msg=module.VolleyErrorMessage(error);
                if(!(msg.isEmpty() || msg.equals("")))
                {
                    Toast.makeText(getActivity(),""+msg,Toast.LENGTH_SHORT).show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(request,json_tag);
    }

}
