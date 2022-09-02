package binplus.foodiswill;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import binplus.foodiswill.Adapter.My_order_detail_adapter;
import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.Fragment.My_order_detail_fragment;
import binplus.foodiswill.Model.My_order_detail_model;
import binplus.foodiswill.util.ConnectivityReceiver;
import binplus.foodiswill.util.CustomVolleyJsonArrayRequest;
import binplus.foodiswill.util.CustomVolleyJsonRequest;
import binplus.foodiswill.util.Session_management;

import static binplus.foodiswill.Config.BaseURL.URL_GENERATE_PDF;

public class MyOrderDetail extends AppCompatActivity {
    private static String TAG = My_order_detail_fragment.class.getSimpleName();
    Dialog dialog;
    EditText et_remark;
    Button btn_yes,btn_no;
    Module module;
    RelativeLayout tv_pdf;
    LinearLayout lin_coupon,lin_dis_amt,input_bar,linView;
    Activity activity=MyOrderDetail.this;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private TextView tv_date, tv_time, tv_total, tv_delivery_charge,tv_address,tv_name,tv_mobile
            ,tv_disinf_charge,tv_dis_amt,tv_coupon;
    private RelativeLayout btn_cancle;
    private RecyclerView rv_detail_order;
    Dialog loadingBar ;
    private String sale_id;
    ImageView back_button;
    SharedPreferences preferences;
    Session_management sessionManagement;
    String user_id ;
    String coupon_code="",disinf_charge="",discount_amt="";
    private List<My_order_detail_model> my_order_detail_modelList = new ArrayList<>();

    public MyOrderDetail() {
        // Required empty public constructor
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleHelper.onAttach(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_order_detail);
        loadingBar=new Dialog(MyOrderDetail.this,android.R.style.Theme_Translucent_NoTitleBar);
        loadingBar.setContentView( R.layout.progressbar );
        loadingBar.setCanceledOnTouchOutside(false);
        dialog=new Dialog(MyOrderDetail.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_cancel_order_layout);
        dialog.setCanceledOnTouchOutside(false);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        module=new Module(MyOrderDetail.this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.order_detail));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MyOrderDetail.this, MainActivity.class);
//                startActivity(intent);
                finish();
            }
        });
        getPermission();
        tv_date = (TextView) findViewById(R.id.tv_order_Detail_date);
        tv_time = (TextView) findViewById(R.id.tv_order_Detail_time);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_mobile = (TextView) findViewById(R.id.tv_mobile);
        input_bar=(LinearLayout)findViewById(R.id.input_bar);
        linView=(LinearLayout)findViewById(R.id.linView);
        tv_pdf=findViewById(R.id.tv_pdf);
        tv_delivery_charge = (TextView) findViewById(R.id.tv_order_Detail_deli_charge);
        tv_total = (TextView) findViewById(R.id.tv_order_Detail_total);
        tv_disinf_charge = (TextView) findViewById(R.id.tv_disinf_charge);
        tv_coupon = (TextView) findViewById(R.id.tv_coupon);
        tv_dis_amt = (TextView) findViewById(R.id.tv_dis_amt);
        btn_cancle = (RelativeLayout) findViewById(R.id.btn_order_detail_cancle);
        rv_detail_order = (RecyclerView) findViewById(R.id.rv_order_detail);
        rv_detail_order.setNestedScrollingEnabled(false);
        lin_coupon=(LinearLayout) findViewById(R.id.lin_coupon);
        lin_dis_amt=(LinearLayout) findViewById(R.id.lin_dis_amt);
        rv_detail_order.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        sessionManagement = new Session_management(MyOrderDetail.this);
        user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
        sale_id = getIntent().getStringExtra("sale_id");
        String total_rs = getIntent().getStringExtra("total");
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String status = getIntent().getStringExtra("status");
        String deli_charge = getIntent().getStringExtra("deli_charge");
        String type = getIntent().getStringExtra("type");
        discount_amt=getIntent().getStringExtra("discount_amt");
        disinf_charge=getIntent().getStringExtra("disinfection_charge");
        coupon_code=getIntent().getStringExtra("coupon_code");
//        Toast.makeText(MyOrderDetail.this,""+String.valueOf(coupon_code),Toast.LENGTH_SHORT).show();
        if(disinf_charge.equalsIgnoreCase("0"))
        {
            tv_disinf_charge.setText("Free");

        }
        else
        {
            tv_disinf_charge.setText(getResources().getString(R.string.currency)+" "+disinf_charge);

        }
        if(coupon_code==null || coupon_code.isEmpty())
        {
            lin_coupon.setVisibility(View.GONE);
            lin_dis_amt.setVisibility(View.GONE);
        }
        else
        {
           tv_coupon.setText(coupon_code);
           tv_dis_amt.setText("-"+getResources().getString(R.string.currency)+" "+discount_amt);
        }
        if (status.equals("0")) {
            btn_cancle.setVisibility(View.VISIBLE);
        } else {
            btn_cancle.setVisibility(View.GONE);
        }

        if(status.equals("4"))
        {
            tv_pdf.setVisibility(View.VISIBLE);
        }
        else
        {
            tv_pdf.setVisibility(View.GONE);
        }
        tv_total.setText(total_rs);
        tv_date.setText(date);
        preferences = getSharedPreferences("lan", MODE_PRIVATE);
        String language=preferences.getString("language","");
        if (language.contains("spanish")) {
            time=time.replace("pm","م");
            time=time.replace("am","ص");
            tv_time.setText( time);

        }else {
            tv_time.setText(time);


        }

        if(deli_charge.equals("0"))
        {
            tv_delivery_charge.setText(" Free");

        }
        else
        {
            tv_delivery_charge.setText(" "+getResources().getString(R.string.currency)+deli_charge);

        }
String address=""+getIntent().getStringExtra("address")+", "+getIntent().getStringExtra("s_name")
      +"("+getIntent().getStringExtra("pincode")+")";
        tv_address.setText(address);
        tv_name.setText(""+getIntent().getStringExtra("r_name").toString());
        tv_mobile.setText(""+getIntent().getStringExtra("r_mobile").toString());

        // check internet connection
        if (ConnectivityReceiver.isConnected()) {
            if(type.equals("cancel"))
            {
                makeCancelOrderDetailRequest(sale_id);
            }
            else
            {
                makeGetOrderDetailRequest(sale_id);
            }

        } else {
            Toast.makeText(MyOrderDetail.this, "Error Network Issues", Toast.LENGTH_SHORT).show();
            // ((MainActivity) getApplication()).onNetworkConnectionChanged(false);
        }
        tv_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String appName=activity.getResources().getString(R.string.app_name);
                File direct = new File(Environment.getExternalStorageDirectory()
                        + "/" + appName + "/Orders/"+"FW_ID"+sale_id+".pdf");
                if (direct.exists()) {
                    createDialogPdfViewer(direct);
                }
                else
                {
                    String urrrl=URL_GENERATE_PDF+"?order_id="+sale_id+"&user_id="+user_id;
                    downloadVideo(urrrl,"FW_ID"+sale_id);
                }

            }
        });

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_no=(Button)dialog.findViewById(R.id.btn_no);
                btn_yes=(Button)dialog.findViewById(R.id.btn_yes);
                et_remark=(EditText) dialog.findViewById(R.id.et_remark);
                dialog.show();

                btn_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {




                        String remark=et_remark.getText().toString();
                        if(remark.isEmpty())
                        {
                            et_remark.setError("Please provide a reason");
                            et_remark.requestFocus();
                        }
                        else if(remark.length()<20)
                        {
                            et_remark.setError("Too short");
                            et_remark.requestFocus();

                        }
                        else
                        {
                            if (ConnectivityReceiver.isConnected()) {
                                makeDeleteOrderRequest(sale_id, user_id,remark);

                            }

                        }
                        // check internet connection
                    }
                });

                btn_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {




                        dialog.dismiss();
                    }
                });

                //finish();
            }
        });

        input_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

    }

    public void getPermission()
    {
        if (!checkPermission()) {
            requestPermission(); // Code for permission
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    // alertdialog for cancle order
    private void showDeleteDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyOrderDetail.this);
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

                Session_management sessionManagement = new Session_management(MyOrderDetail.this);
                String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

                // check internet connection
                if (ConnectivityReceiver.isConnected()) {
//                      makeDeleteOrderRequest(sale_id, user_id);
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

        loadingBar.show();
        // Tag used to cancel the request
        String tag_json_obj = "json_order_detail_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("sale_id", sale_id);

        CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.POST,
                BaseURL.ORDER_DETAIL_URL, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());
                loadingBar.dismiss();
                Gson gson = new Gson();
                Type listType = new TypeToken<List<My_order_detail_model>>() {
                }.getType();

                my_order_detail_modelList = gson.fromJson(response.toString(), listType);

                My_order_detail_adapter adapter = new My_order_detail_adapter(my_order_detail_modelList);
                rv_detail_order.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (my_order_detail_modelList.isEmpty()) {
                    loadingBar.dismiss();
                    Toast.makeText(MyOrderDetail.this, getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(MyOrderDetail.this, getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    private void makeCancelOrderDetailRequest(String sale_id) {

        loadingBar.show();
        // Tag used to cancel the request
        String tag_json_obj = "json_order_detail_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("sale_id", sale_id);

        CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.POST,
                BaseURL.URL_CANCEL_ORDER_DETAILS, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());
                loadingBar.dismiss();
                Gson gson = new Gson();
                Type listType = new TypeToken<List<My_order_detail_model>>() {
                }.getType();

                my_order_detail_modelList = gson.fromJson(response.toString(), listType);

                My_order_detail_adapter adapter = new My_order_detail_adapter(my_order_detail_modelList);
                rv_detail_order.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (my_order_detail_modelList.isEmpty()) {
                    loadingBar.dismiss();
                    Toast.makeText(MyOrderDetail.this, getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(MyOrderDetail.this, getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeDeleteOrderRequest(String sale_id, String user_id,String remark) {

        // Tag used to cancel the request
        String tag_json_obj = "json_delete_order_req";
        loadingBar.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("sale_id", sale_id);
        params.put("user_id", user_id);
        params.put("comment", remark);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.DELETE_ORDER_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                loadingBar.dismiss();
                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        String msg = response.getString("message");
                        dialog.dismiss();
                        Toast.makeText(MyOrderDetail.this, "" + msg, Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(MyOrderDetail.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        // ((MainActivity) getActivity()).onBackPressed();

                    } else {
                        String error = response.getString("error");
                        Toast.makeText(MyOrderDetail.this, "" + error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                String msg=module.VolleyErrorMessage(error);
                if(!msg.equals(""))
                {
                    Toast.makeText(MyOrderDetail.this,""+msg,Toast.LENGTH_LONG).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }

    //For download receipt in pdf

    private void downloadVideo(final String mediaUrl,final String name) {

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                downloadFile(mediaUrl, name);
            }
        };
        handler.post(runnable);


    }

    private void downloadFile(String mediaUrl, final String s) {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("Loading..");
        dialog.setCanceledOnTouchOutside(false);

        Log.e("adsdasdasd",""+mediaUrl.toString()+" - "+s);

        final String appName = activity.getResources().getString(R.string.app_name);
        // creating the download directory
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/" + appName + "/Orders");
        if (!direct.exists()) {
            direct.mkdirs();
        }

        String pdfFileName = s + ".pdf";

        DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(mediaUrl));
        request1.setTitle("File Doo");
        request1.setDescription("Downloading.....");
//        request1.setVisibleInDownloadsUi(false);
        request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request1.setShowRunningNotification(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request1.allowScanningByMediaScanner();
            request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
//        if (getFileExists(s,vvExt))
//        {
//            new ToastMsg(activity).toastIconError("File is already downloaded!");
//            return;
//        }
        request1.setDestinationInExternalPublicDir("/" + appName + "/Orders", pdfFileName);
        final DownloadManager manager1 = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        // start the download
        final long downloadId = manager1.enqueue(request1);
        if (DownloadManager.STATUS_SUCCESSFUL == 8) {
            module.showToast("Download will be started soon.");
        }

        BroadcastReceiver mCompleted = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadId == id) {
                    Date date = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:MM:ss");
                    String ss = simpleDateFormat.format(date);

//                    File movieFile = new File(Environment.getExternalStorageDirectory()
//                            +movieFileName);
//                    String appName22 = getResources().getString(R.string.app_namee)+getResources().getString(R.string.original_file_path);
                    module.showToast("File Download Successfully.");
                    File direct = new File(Environment.getExternalStorageDirectory()
                            + "/" + appName + "/Orders/"+"FW_ID"+sale_id+".pdf");
                    createDialogPdfViewer(direct);

                }
            }
        };
        activity.registerReceiver(mCompleted, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public void createDialogPdfViewer(final File file)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder.setMessage("Would you like to open downloaded order receipt?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        try
                        {
                            Intent intent=new Intent(Intent.ACTION_VIEW);
                            Uri uri = Uri.fromFile(file);
                            intent.setDataAndType(uri, "application/pdf");
                            startActivity(intent);
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                });
          final AlertDialog dialog=builder.create();
          dialog.setOnShowListener(new DialogInterface.OnShowListener() {
              @Override
              public void onShow(DialogInterface dialogInterface) {
                  dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                  dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));

              }
          });
          dialog.show();
    }

}
