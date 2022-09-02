package binplus.foodiswill.Fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import binplus.foodiswill.Adapter.Home_adapter;
import binplus.foodiswill.Adapter.View_time_adapter;
import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.Model.Category_model;
import binplus.foodiswill.Model.Time_slot_model;
import binplus.foodiswill.AppController;
import binplus.foodiswill.MainActivity;
import binplus.foodiswill.R;
import binplus.foodiswill.util.ConnectivityReceiver;
import binplus.foodiswill.util.CustomVolleyJsonRequest;
import binplus.foodiswill.util.RecyclerTouchListener;
import binplus.foodiswill.util.Session_management;

public class View_time_fragment extends Fragment {

    private static String TAG = View_time_fragment.class.getSimpleName();
    Module module;
    private RecyclerView rv_time;

    private List<String> time_list = new ArrayList<>();
    private List<Category_model> category_modelList = new ArrayList<>();
    private Home_adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private String getdate;
    private List<String> time_c_list = new ArrayList<>();
    String time  ;
    Date getctime ,getstims ,getetime ;
    String currentTime ;
    SimpleDateFormat formatter2 ;
    public int flag=0;
    TextView txtnote ;


    private Session_management sessionManagement;
    Dialog loadingBar ;

    public View_time_fragment() {
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
        View view = inflater.inflate(R.layout.fragment_time_list, container, false);
        loadingBar=new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        loadingBar.setContentView( R.layout.progressbar );
        loadingBar.setCanceledOnTouchOutside(false);
        module=new Module(getActivity());

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.delivery_time));

        sessionManagement = new Session_management(getActivity());
        txtnote = view.findViewById( R.id.txtNote );

        rv_time = (RecyclerView) view.findViewById(R.id.rv_times);
        rv_time.setLayoutManager(new LinearLayoutManager(getActivity()));

        getdate = getArguments().getString("date");
        Calendar calendar = Calendar.getInstance();
        formatter2 = new SimpleDateFormat("h:mm a");
        currentTime = formatter2.format(calendar.getTime());



        // check internet connection
        if (ConnectivityReceiver.isConnected()) {
            //       Toast.makeText(getActivity(),""+currentTime + "\n" +getctime,Toast.LENGTH_LONG).show();
            makeGetTimeRequest(getdate);
        } else {
            ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
        }

        rv_time.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_time, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String gettime ="";

                if(flag<=0)
                {
                    gettime = time_list.get( position );

                    txtnote.setVisibility( View.VISIBLE );
                }
                else
                {
                    gettime = time_c_list.get( position );

                }


                //Toast.makeText( getActivity(),""+gettime,Toast.LENGTH_LONG ).show();

                sessionManagement.cleardatetime();

                sessionManagement.creatdatetime(getdate,gettime);

                ((MainActivity) getActivity()).onBackPressed();

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        return view;
    }

    /**
     * Method to make json object request where json response starts wtih {
     */
    private void makeGetTimeRequest(String date) {

        loadingBar.show();
        // Tag used to cancel the request
        String tag_json_obj = "json_time_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("date",date);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_TIME_SLOT_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("tm_slot", response.toString());
                try {
                    loadingBar.dismiss();
                    String status=response.getString("status");
                    if(status.equals("success"))
                    {
                        JSONArray array=response.getJSONArray("data");

                        for(int i=0; i<array.length();i++)
                        {
                            JSONObject object=array.getJSONObject(i);
                            Time_slot_model model=new Time_slot_model();
                            model.setFrom_time(object.getString("opening_time"));
                            model.setTo_time(object.getString("closing_time"));
                            model.setSlot(object.getString("slot"));
                            model.setId(object.getString("id"));
                            String time=object.getString("opening_time");
                            String to_time=object.getString("closing_time");
                            String tm=getTimeFormat(time);
                            String to_tm=getTimeFormat(to_time);
                            SimpleDateFormat format = new SimpleDateFormat("hh:mm a");

                            Date tmstart = format.parse(tm);
                            Date tmend = format.parse( to_tm );
                            Date current = format.parse( currentTime );



                            long difference = current.getTime() - tmstart.getTime();
                            long as=(difference/1000)/60;

                            long diff_close=current.getTime()-tmend.getTime();
                            long c=(diff_close/1000)/60;



//                            long current_time=date3.getTime();
//                            long cur=(current_time/1000)/60;
//                            if (as <=0)
//                            {
                                flag++;
                                time_c_list.add( tm + " - " + to_tm );
                                //      Toast.makeText( getActivity(), "< \n" + as + "\n" + c, Toast.LENGTH_LONG ).show();

//                            }

//                            else
//                            //if (as>0)
//                            {
////                                if (c<0)
////                                {
////                                    time_list.add( tm + " - " + to_tm );
////                                    Toast.makeText( getActivity(), "< \n" + as + "\n" + c, Toast.LENGTH_LONG ).show();
////                                }
////                            }
////
////                            else
////                                {
////
////                               Toast.makeText( getActivity(), "time \n" + as + "\n" + c, Toast.LENGTH_LONG ).show();
//                                //  Toast.makeText( getActivity(),"time \n" +current + "\n"+tmstart +"\n"+tmend,Toast.LENGTH_LONG ).show();
//
//                                time_list.add(tm+" - "+to_tm);
//                            }

                        }
                        View_time_adapter view_time_adapter;
                        if(flag<=0)
                        {
                            view_time_adapter=new View_time_adapter(time_list);

                            txtnote.setVisibility( View.VISIBLE );
                        }
                        else
                        {
                            view_time_adapter=new View_time_adapter(time_c_list);

                        }
                        rv_time.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rv_time.setAdapter(view_time_adapter);
                    }
                    else
                    {
                        Toast.makeText( getActivity(),"something went wrong",Toast.LENGTH_LONG ).show();
                    }
                } catch (Exception e) {
                    loadingBar.dismiss();
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
                    Toast.makeText(getActivity(),""+msg,Toast.LENGTH_LONG).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public String getTimeFormat(String time)
    {
        String dt="";
        try {
            String _24HourTime = time;
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(_24HourTime);
            //System.out.println(_24HourDt);

            dt=_12HourSDF.format(_24HourDt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dt;
    }
    public static String getCurrentTime() {
        //date output format
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
}
