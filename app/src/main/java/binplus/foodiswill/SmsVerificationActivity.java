package binplus.foodiswill;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.Config.SmsBroadcastReceiver;
import binplus.foodiswill.util.ConnectivityReceiver;
import binplus.foodiswill.util.CustomVolleyJsonRequest;
import binplus.foodiswill.util.Session_management;
import binplus.foodiswill.util.SmsListener;

public class SmsVerificationActivity extends AppCompatActivity implements View.OnClickListener {
    private final int REQUEST_ID_MULTIPLE_PERMISSIONS=1;
    private static final int REQ_USER_CONSENT = 200;
    SmsBroadcastReceiver smsBroadcastReceiver;
    Button btn_msg;
    EditText et_otp;
    String type="";
    String Otp="";
    String number="",name="",wStatus="" ,city_name="",city_id="";
    ProgressDialog loadingBar;
    TextView back ;
    String msg_status="";
    Module module;
    CountDownTimer countDownTimer;
    Session_management session_management;
    public static final String OTP_REGEX = "[0-9]{3,6}";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification);

        btn_msg=(Button)findViewById(R.id.btn_msg);
        et_otp=(EditText) findViewById(R.id.et_otp);
        back = findViewById(R.id.txt_reg_back);
        back.setOnClickListener(this);
        loadingBar=new ProgressDialog(SmsVerificationActivity.this);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        module=new Module(SmsVerificationActivity.this);
        type=getIntent().getStringExtra("type");
        number=getIntent().getStringExtra("mobile");
        Otp=getIntent().getStringExtra("otp");
        if(type.equalsIgnoreCase("r"))
        {
         name=getIntent().getStringExtra("name");
         city_name=getIntent().getStringExtra("city_name");
        city_id=getIntent().getStringExtra("city_id");
         wStatus=getIntent().getStringExtra("status");
            msg_status=getIntent().getStringExtra("msg_status");
            if(msg_status.equals("0"))
            {
                countDownTimer=new CountDownTimer(5000,1000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                     et_otp.setText(Otp);
                    }
                };
                countDownTimer.start();

//             module.showToast("Your One Time Password is : "+Otp);
            }
            else
            {
                startSmsUserConsent();
            }

        }
        else
        {
            startSmsUserConsent();
        }

        session_management=new Session_management(SmsVerificationActivity.this);
//        checkAndRequestPermissions();
//        getSmsOtp();
        //btn_msg.setText(Otp+" - "+number);
        btn_msg.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int id=view.getId();
        if(id == R.id.btn_msg)
        {
            if(ConnectivityReceiver.isConnected())
            {
                verification();

            }

        }
        else if (id == R.id.txt_reg_back)
        {
            finish();
        }

    }



    private void verification() {
        String otp=et_otp.getText().toString();
        if(otp.isEmpty())
        {
            et_otp.setError("Enter OTP");
            et_otp.requestFocus();
        }
        else if(otp.length()<4)
        {
            et_otp.setError("OTP is too short");
            et_otp.requestFocus();
        }
        else {
            if(type.equals("f"))
            {
                verifyMobileWithOtp(number,otp);

            }
            else if(type.equals("r"))
            {
                verifyRegisterMobileWithOtp(number,otp,name,wStatus);

            }

        }
    }

    private void verifyRegisterMobileWithOtp(final String number, String otp, final String name, String wStatus) {
        loadingBar.show();
        String json_tag="json_verification";
        HashMap<String,String> map=new HashMap<>();
        map.put("mobile",number);
        map.put("otp",otp);
        map.put("name",name);
        map.put("city_name",city_name);
        map.put("city_id",city_id);
        map.put("wstatus",wStatus);
        Log.e("params",map.toString());

        CustomVolleyJsonRequest customVolleyJsonRequest=new CustomVolleyJsonRequest(Request.Method.POST, BaseURL.URL_VERIFY_REGISTER_OTP, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    Log.d("verify",response.toString());
                    boolean status=response.getBoolean("responce");
                    if(status)
                    {
                        JSONObject obj = response.getJSONObject("data");
                        String user_id = obj.getString("user_id");
                        String user_fullname = obj.getString("user_fullname");
                        String user_email = obj.getString("user_email");
                        String user_phone = obj.getString("user_phone");
                        String user_image = obj.getString("user_image");
                        String wallet_ammount = obj.getString("wallet");
                        String reward_points = obj.getString("rewards");
                        String city_name = obj.getString("city_name");
                        String city_id = obj.getString("city_id");

                        session_management.createLoginSession(user_id, user_phone, name, user_phone, "", "0", "0", "", city_id, city_name, "", "");

                        Intent intent = new Intent( SmsVerificationActivity.this,MainActivity.class );
                        intent.putExtra( "mobile", number );
                        startActivity( intent );
                        finish();



                    }
                    else
                    {
                        loadingBar.dismiss();
                        Toast.makeText(SmsVerificationActivity.this,""+response.getString("error").toString(),Toast.LENGTH_LONG).show();
                    }

                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    Toast.makeText(SmsVerificationActivity.this,""+ex.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(customVolleyJsonRequest,json_tag);
    }


    private void verifyMobileWithOtp(final String number, String otp) {
        loadingBar.show();
        String json_tag="json_verification";
        HashMap<String,String> map=new HashMap<>();
        map.put("mobile",number);
        map.put("otp",otp);

        CustomVolleyJsonRequest customVolleyJsonRequest=new CustomVolleyJsonRequest(Request.Method.POST, BaseURL.URL_VERIFY_OTP, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    Log.d("verify",response.toString());
                    boolean status=response.getBoolean("responce");
                    if(status)
                    {
                        String data=response.getString("data");

//                        Intent intent = new Intent( SmsVerificationActivity.this, ForgotActivity.class );
//                        intent.putExtra( "mobile", number );
//                        startActivity( intent );
//                        finish();

                    }
                    else
                    {
                        loadingBar.dismiss();
                        Toast.makeText(SmsVerificationActivity.this,""+response.getString("error").toString(),Toast.LENGTH_LONG).show();
                    }

                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    Toast.makeText(SmsVerificationActivity.this,""+ex.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(customVolleyJsonRequest,json_tag);
    }

    private boolean checkAndRequestPermissions()
    {
        int sms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

        if (sms != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void startSmsUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        //We can add sender phone number or leave it blank
        // I'm adding null here
        client.startSmsUserConsent(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Toast.makeText(getApplicationContext(), "On Success", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "On OnFailure", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_USER_CONSENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                //That gives all message to us.
                // We need to get the code from inside with regex
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                getOtpFromMessage(message);
            }
        }
    }

    private void getOtpFromMessage(String message) {
        // This will match any 6 digit number in the message
        Pattern pattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            et_otp.setText(matcher.group(0));
        }
    }

    private void registerBroadcastReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.smsBroadcastReceiverListener =
                new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, REQ_USER_CONSENT);
                    }

                    @Override
                    public void onFailure() {

                    }
                };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }
}
