package binplus.foodiswill;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import binplus.foodiswill.Adapter.CityAdapter;
import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.Config.SmsBroadcastReceiver;
import binplus.foodiswill.Model.CityModel;
import binplus.foodiswill.util.CustomVolleyJsonArrayRequest;
import binplus.foodiswill.util.LoadingBar;
import binplus.foodiswill.util.RecyclerTouchListener;
import binplus.foodiswill.util.Session_management;

import static binplus.foodiswill.Config.BaseURL.CITY_ID;
import static binplus.foodiswill.Config.BaseURL.CITY_NAME;

public class SelectCity extends AppCompatActivity {
    private final String TAG=SelectCity.class.getSimpleName();
    Session_management session_management;
    Dialog loadingBar;
    Module module;
    Activity ctx=SelectCity.this;
    ArrayList<CityModel> cityList;
    RecyclerView rv_city;
    TextView tv_close;
    CityAdapter adapter;
    Button btn_submit;
    String city_id="",city_name="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        initViews();
    }

    private void initViews() {
        module=new Module(ctx);
        loadingBar=new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar);
        loadingBar.setContentView( R.layout.progressbar );
        loadingBar.setCanceledOnTouchOutside(false);

        session_management=new Session_management(ctx);
        cityList=new ArrayList<>();

        rv_city=findViewById(R.id.rv_city);
        rv_city.setLayoutManager(new GridLayoutManager(ctx,4));
        tv_close=findViewById(R.id.tv_close);
        btn_submit=findViewById(R.id.btn_submit);
        rv_city.setNestedScrollingEnabled(false);


        getCities();

        rv_city.addOnItemTouchListener(new RecyclerTouchListener(ctx, rv_city, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                for(int i=0;i<cityList.size();i++){
                    if(i==position){
                   cityList.get(position).setSelected(true);
                    }else{
                        cityList.get(i).setSelected(false);
                    }

                }
                Log.e(TAG, "onItemClick: "+cityList.get(position).getCity_name() );
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index=-1;
              for(int i=0;i<cityList.size();i++){
                  if(cityList.get(i).isSelected()){
                      index=i;
                      break;
                  }
              }

              if(index>-1){
                  city_id=cityList.get(index).getCity_id();
                  city_name=cityList.get(index).getCity_name();

                  Intent startmain = null;
                  switch (getIntent().getStringExtra("from").toLowerCase())
                  {
                      case "home":
                          session_management.addCity(city_id,city_name);
                          startmain= new Intent(ctx, MainActivity.class);

                          break;
//                      case "profile":
//                          startmain=  new Intent();
//                          startmain.putExtra("city_name",city_name);
//                          startmain.putExtra("city_id",city_id);
//                          setResult(Activity.RESULT_OK,startmain);
//                          finish();
//                          break;
                      case "splash":
                          session_management.addCity(city_id,city_name);
                         startmain= new Intent(ctx, Verfication_activity.class);

                          break;

                  }

                  if (startmain!=null) {
                      startmain.putExtra("type", "r");
                      startActivity(startmain);
                      finish();
                  }
              }else{
                  module.showToast("Select at least one city");
              }

            }
        });
    }

    private void getCities(){
        loadingBar.show();
        cityList.clear();
        HashMap<String,String> params=new HashMap<>();
        CustomVolleyJsonArrayRequest request=new CustomVolleyJsonArrayRequest(Request.Method.POST, BaseURL.GET_CITY_URL, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
            loadingBar.dismiss();
            try{
                Gson gson=new Gson();
                Type typeList=new TypeToken<List<CityModel>>(){}.getType();
                cityList=gson.fromJson(response.toString(),typeList);
                cityList.get(0).setSelected(true);
                adapter=new CityAdapter(ctx,cityList,"city");
                rv_city.setAdapter(adapter);
                if (cityList.size()>0)
                {
                    btn_submit.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn_submit.setVisibility(View.GONE);
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                module.showToast(""+error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(request);
    }
}