package binplus.foodiswill;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import binplus.foodiswill.Adapter.CityAdapter;
import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.Model.CityModel;
import binplus.foodiswill.networkconnectivity.NoInternetConnection;
import binplus.foodiswill.util.ConnectivityReceiver;
import binplus.foodiswill.util.CustomVolleyJsonArrayRequest;
import binplus.foodiswill.util.CustomVolleyJsonRequest;
import binplus.foodiswill.util.RecyclerTouchListener;

import static binplus.foodiswill.Config.BaseURL.URL_REGISTER_OTP;

public class Verfication_activity extends AppCompatActivity implements View.OnClickListener{
    private static String TAG = Verfication_activity.class.getSimpleName();
    EditText et_reg_number;
    Button btn_verify_number;
    Dialog loadingBar ;
    String type="";
    Module module;
    RecyclerView rv_city;
    Context context= Verfication_activity.this;
    RadioButton btn_yes,btn_no;
    LinearLayout lin_reg,lin_pass;
    EditText et_f_name,et_l_name,et_phone;
    RelativeLayout btnRegister;
    String msg_status="";
    ArrayList<CityModel> cityList;
    CityAdapter adapter;
    String city_id="",city_name="" ,w_status="";
    public Verfication_activity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        Bundle bundle=getIntent().getExtras();
        type=bundle.getString("type");
      
        loadingBar=new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
        loadingBar.setContentView( R.layout.progressbar );
        loadingBar.setCanceledOnTouchOutside(false);
        module=new Module(context);
        initViews();
        getMessageStatus();
        btn_verify_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int first=0;

                String phone=et_reg_number.getText().toString().trim();
                if(phone.length()>0)
                {
                    first = Integer.parseInt(phone.substring(0,1));
                }

                String f = String.valueOf( first);
//                int first = Integer.parseInt( f );
                String plus = "\u002B" ;

                if(phone.isEmpty())
                {
                    et_reg_number.setError("enter mobile number");
                    et_reg_number.requestFocus();
                }
                else if(!(phone.length()>=10))
                {
                    et_reg_number.setError("invalid mobile number");
                    et_reg_number.requestFocus();
                }
                else if (first<6)
                {
                    et_reg_number.setError("invalid mobile number");
                    et_reg_number.requestFocus();

                }

                else if(f.equals( plus ))
                {
                    et_reg_number.setError("invalid mobile number");
                    et_reg_number.requestFocus();
                }
                else
                {
                    if(ConnectivityReceiver.isConnected())
                    {
                        String otp=getRandomKey(6);


                    }
                }

            }
        });
    }



    private void initViews() {
        btn_yes=findViewById(R.id.btn_yes);
        btn_no=findViewById(R.id.btn_no);
        lin_reg=findViewById(R.id.lin_reg);
        lin_pass=findViewById(R.id.lin_pass);
        et_f_name=findViewById(R.id.et_f_name);
        et_l_name=findViewById(R.id.et_l_name);
        et_phone=findViewById(R.id.et_phone);
        btn_yes.setChecked(true);
        et_reg_number=(EditText)findViewById(R.id.et_reg_number);
        btn_verify_number=(Button)findViewById(R.id.btn_verify_number);
        btnRegister=findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        cityList=new ArrayList<>();

        rv_city=findViewById(R.id.rv_city);
        rv_city.setLayoutManager(new GridLayoutManager(context,2));

        rv_city.setNestedScrollingEnabled(false);


        getCities();
        rv_city.addOnItemTouchListener(new RecyclerTouchListener(context, rv_city, new RecyclerTouchListener.OnItemClickListener() {
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


    }





    public static String getRandomKey(int i)
    {
        final String characters="0123456789";
        StringBuilder stringBuilder=new StringBuilder();
        while (i>0)
        {
            Random ran=new Random();
            stringBuilder.append(characters.charAt(ran.nextInt(characters.length())));
            i--;
        }
        return stringBuilder.toString();
    }


    private void getVerificationCode(final String phone, final String name , final String otp, final int status) {
        loadingBar.show();

        String json_tag="json_verifiaction_tag";
        HashMap<String,String> mp=new HashMap<String, String>();
        mp.put("mobile",phone);
        mp.put("otp",otp);
        CustomVolleyJsonRequest customVolleyJsonRequest=new CustomVolleyJsonRequest(Request.Method.POST, URL_REGISTER_OTP, mp, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    boolean responce=response.getBoolean("responce");
                    if(responce)
                    {
                        Log.e("regiiii",""+response);
                        loadingBar.dismiss();
                        Intent intent=new Intent(context, SmsVerificationActivity.class);
                        intent.putExtra("mobile",phone);
                        intent.putExtra("otp",otp);
                        intent.putExtra("type",type);
                        intent.putExtra("name",name);
                        intent.putExtra("city_id",city_id);
                        intent.putExtra("city_name",city_name);
                        intent.putExtra("msg_status",msg_status);
                        intent.putExtra("status",String.valueOf(status));
                        Log.e("extras",intent.getExtras().toString());
                        startActivity(intent);

                    }
                    else
                    {
                        loadingBar.dismiss();
                        Toast.makeText(context,""+response.getString("error").toString(),Toast.LENGTH_LONG).show();

                    }
                }
                catch (Exception ex)
                {
                    loadingBar.dismiss();
                    ex.printStackTrace();
                    Toast.makeText(context,"Something Went Wrong",Toast.LENGTH_LONG).show();
                }
                // Toast.makeText(context,""+response,Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                error.printStackTrace();
                String msg=module.VolleyErrorMessage(error);
                module.showToast(""+error.getMessage());
//                if(!msg.isEmpty())
//                {
//                    Toast.makeText(context,""+msg,Toast.LENGTH_LONG).show();
//                }
                //Toast.makeText(context,""+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        AppController.getInstance().addToRequestQueue(customVolleyJsonRequest,json_tag);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnRegister)
        {
            int first=0;
            String fName=et_f_name.getText().toString();
            String lName=et_l_name.getText().toString();
            String phone=et_phone.getText().toString().trim();
            int index=-1;
            for(int i=0;i<cityList.size();i++){
                if(cityList.get(i).isSelected()){
                    city_id = cityList.get(i).getCity_id();
                    city_name = cityList.get(i).getCity_name();
                    index=i;
                    break;
                }
            }



            if(phone.length()>0)
            {
                first = Integer.parseInt(phone.substring(0,1));
            }

            String f = String.valueOf( first);
//                int first = Integer.parseInt( f );
            String plus = "\u002B" ;

            if(phone.isEmpty())
            {
                et_phone.setError("enter mobile number");
                et_phone.requestFocus();
            }
            else if(!(phone.length()==10))
            {
                et_phone.setError("invalid mobile number");
                et_phone.requestFocus();
            }
            else if (first<6)
            {
                et_phone.setError("invalid mobile number");
                et_phone.requestFocus();

            }

            else if(f.equals( plus ))
            {
                et_phone.setError("invalid mobile number");
                et_phone.requestFocus();
            }
            else if(fName.isEmpty())
            {
                et_f_name.setError("Enter First Name");
                et_f_name.requestFocus();
            }
          else if(index==-1)
          {

                module.showToast("Select at least one city");
            }
            else
            {
                if(ConnectivityReceiver.isConnected())
                {
                    String otp=getRandomKey(6);
                    if(type.equals("r"))
                    {
                        int status=0;
                        if(btn_yes.isChecked())
                        {
                            status=1;
                        }
                        else if(btn_no.isChecked())
                        {
                            status=0;
                        }

                        getVerificationCode(phone,fName+" "+lName,otp,status);

                    }
                }
                else
                {
                    Intent intent=new Intent(context, NoInternetConnection.class);
                    context.startActivity(intent);
                }
            }

        }
    }

    public void getMessageStatus()
    {
        loadingBar.show();
        String json_tag="json_app_tag";
        HashMap<String,String> map=new HashMap<>();

        CustomVolleyJsonRequest request=new CustomVolleyJsonRequest(Request.Method.POST, BaseURL.GET_VERSTION_DATA, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingBar.dismiss();
                try
                {
                    boolean sts=response.getBoolean("responce");

                    if(sts)
                    {
                        JSONObject object=response.getJSONObject("data");
                         msg_status=object.getString("msg_status");


                    }
                    else
                    {
                        Toast.makeText(context,""+response.getString("error"),Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context,""+msg,Toast.LENGTH_SHORT).show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(request,json_tag);
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
                    adapter=new CityAdapter(context,cityList,"city_name");
                    rv_city.setAdapter(adapter);

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
