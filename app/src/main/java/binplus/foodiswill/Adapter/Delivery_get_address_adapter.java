package binplus.foodiswill.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.Fragment.Add_delivery_address_fragment;
import binplus.foodiswill.Model.Delivery_address_model;
import binplus.foodiswill.AppController;
import binplus.foodiswill.R;
import binplus.foodiswill.util.ConnectivityReceiver;
import binplus.foodiswill.util.CustomVolleyJsonRequest;
import binplus.foodiswill.util.Session_management;


public class Delivery_get_address_adapter extends RecyclerView.Adapter<Delivery_get_address_adapter.MyViewHolder> {

    private static String TAG = Delivery_get_address_adapter.class.getSimpleName();

    private List<Delivery_address_model> modelList;
    Dialog loadingBar ;
    private Context context;

    private static RadioButton lastChecked = null;
    private static int lastCheckedPos = 0;
    private boolean ischecked = false;
    private Button edit_address;
    private String location_id = "";
    private String getsocity, gethouse, getphone, getpin, getname, getcharge ,getaddress,getemail;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_address, tv_name, tv_phone, tv_charges,txtEdit,txtDelete,tv_email;
        public RadioButton rb_select;
        LinearLayout lin_email;

//        SwipeLayout swipeLayout;
//        Button buttonDelete, btn_edit;

        public MyViewHolder(View view) {
            super(view);

//            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
//            buttonDelete = (Button) itemView.findViewById(R.id.delete);
            lin_email = (LinearLayout) itemView.findViewById(R.id.lin_email);
            tv_email = (TextView) itemView.findViewById(R.id.tv_email);

            tv_address = (TextView) view.findViewById(R.id.tv_adres_address);
            tv_name = (TextView) view.findViewById(R.id.tv_adres_pincode);
            tv_phone = (TextView) view.findViewById(R.id.tv_adres_phone);
            tv_charges = (TextView) view.findViewById(R.id.tv_adres_charge);
            txtEdit = (TextView) view.findViewById(R.id.txtEdit);
            txtDelete = (TextView) view.findViewById(R.id.txtDelete);
            rb_select = (RadioButton) view.findViewById(R.id.rb_adres);
            loadingBar=new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
            loadingBar.setContentView( R.layout.progressbar );
            loadingBar.setCanceledOnTouchOutside(false);
            rb_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    RadioButton cb = (RadioButton) view;
                    int clickedPos = getAdapterPosition();

                    location_id = modelList.get(clickedPos).getLocation_id();

                    gethouse = modelList.get(clickedPos).getHouse_no();
                    getname = modelList.get(clickedPos).getReceiver_name();
                    getphone = modelList.get(clickedPos).getReceiver_mobile();
                    getsocity = modelList.get(clickedPos).getSocity_name();
                    getpin = modelList.get(clickedPos).getPincode();
                    getcharge = modelList.get(clickedPos).getDelivery_charge();
                    getemail = modelList.get(clickedPos).getReceiver_email();


                    if (modelList.size() > 1) {
                        if (cb.isChecked()) {
                            if (lastChecked != null) {
                                lastChecked.setChecked(false);
                                modelList.get(lastCheckedPos).setIscheckd(false);
                            }

                            lastChecked = cb;
                            lastCheckedPos = clickedPos;
                        } else {
                            lastChecked = null;
                        }
                    }
                    modelList.get(clickedPos).setIscheckd(cb.isChecked());

                    if (cb.isChecked()) {
                        ischecked = true;

                        Intent updates = new Intent("Grocery_delivery_charge");
                        updates.putExtra("type", "update");
                        updates.putExtra("charge", getcharge);
                        context.sendBroadcast(updates);
                    } else {
                        ischecked = false;

                        Intent updates = new Intent("Grocery_delivery_charge");
                        updates.putExtra("type", "update");
                        updates.putExtra("charge", "00");
                        context.sendBroadcast(updates);
                    }

                }
            });
        }
    }

    public Delivery_get_address_adapter(List<Delivery_address_model> modelList) {
        this.modelList = modelList;
    }

    @Override
    public Delivery_get_address_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_delivery_time_rv, parent, false);

        context = parent.getContext();

        return new Delivery_get_address_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final Delivery_get_address_adapter.MyViewHolder holder, final int position) {
        final Delivery_address_model mList = modelList.get(position);

        holder.tv_address.setText(mList.getReceiver_name());
        holder.tv_phone.setText(mList.getReceiver_mobile());
        holder.tv_name.setText(mList.getHouse_no());

        //  holder.tv_charges.setText(mList.getDelivery_charge()+context.getResources().getString(R.string.currency));

        if(mList.getReceiver_email() == null || mList.getReceiver_email().isEmpty())
        {
            holder.lin_email.setVisibility(View.GONE);

        }
        else
        {
            if(holder.lin_email.getVisibility()==View.GONE)
            {
                holder.lin_email.setVisibility(View.VISIBLE);
            }
            holder.tv_email.setText(mList.getReceiver_email().toString());
        }
        holder.rb_select.setChecked(mList.getIscheckd());
        holder.rb_select.setTag(new Integer(position));

        //for default check in first item
        if (position == 0 /*&& mList.getIscheckd() && holder.rb_select.isChecked()*/) {
            holder.rb_select.setChecked(true);
            modelList.get(position).setIscheckd(true);

            lastChecked = holder.rb_select;
            lastCheckedPos = 0;

            location_id = modelList.get(0).getLocation_id();

             gethouse = modelList.get(0).getHouse_no();
            getname = modelList.get(0).getReceiver_name();
            getphone = modelList.get(0).getReceiver_mobile();
            getsocity = modelList.get(0).getHouse_no();
            getpin = modelList.get(0).getPincode();
            getcharge = modelList.get(0).getDelivery_charge();
            getemail = modelList.get(0).getReceiver_email();

            ischecked = true;

            Intent updates = new Intent("Grocery_delivery_charge");
            updates.putExtra("type", "update");
            updates.putExtra("charge", getcharge);
            context.sendBroadcast(updates);
        }

        //holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

//        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
//            @Override
//            public void onOpen(SwipeLayout layout) {
//                //YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
//            }
//        });

//        holder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
//            @Override
//            public void onDoubleClick(SwipeLayout layout, boolean surface) {
//                Toast.makeText(context, "DoubleClick", Toast.LENGTH_SHORT).show();
//            }
//        });



        holder.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ConnectivityReceiver.isConnected()){
                    makeDeleteAddressRequest(mList.getLocation_id(),position);
                }
            }
        });


//        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//           //     mItemManger.removeShownLayouts(holder.swipeLayout);
//                /*modelList.remove(position);
//                notifyItemRemoved(position);
//                notifyItemRangeChanged(position, modelList.size());
//                mItemManger.closeAllItems();*/
//
//                if(ConnectivityReceiver.isConnected()){
//                    makeDeleteAddressRequest(mList.getLocation_id(),position);
//                }
//
//            }
//        });

        holder.txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Session_management sessionManagement = new Session_management(context);
                sessionManagement.updateSocity("","");

                Bundle args = new Bundle();
                Fragment fm = new Add_delivery_address_fragment();
                args.putString("location_id",mList.getLocation_id());
                args.putString("name", mList.getReceiver_name());
                args.putString("mobile", mList.getReceiver_mobile());
                args.putString("pincode", mList.getPincode());
                args.putString("socity_id", mList.getSocity_id());
                args.putString("socity_name", mList.getSocity_name());
                args.putString("house", mList.getHouse_no());
                args.putString("email", mList.getReceiver_email());
                //  args.putString( "address",getaddress );

                fm.setArguments(args);
                FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
//
            }
        });
//        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Session_management sessionManagement = new Session_management(context);
//                sessionManagement.updateSocity("","");
//
//                Bundle args = new Bundle();
//                binplus.Jabico.Fragment fm = new Add_delivery_address_fragment();
//                args.putString("location_id",mList.getLocation_id());
//                args.putString("name", mList.getReceiver_name());
//                args.putString("mobile", mList.getReceiver_mobile());
//                args.putString("pincode", mList.getPincode());
//                args.putString("socity_id", mList.getSocity_id());
//                args.putString("socity_name", mList.getSocity_name());
//                args.putString("house", mList.getHouse_no());
//                fm.setArguments(args);
//                FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                        .addToBackStack(null).commit();
//            }
//        });

        // mItemManger.bindView(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public String getlocation_id() {
        return location_id;
    }

    public String getaddress() {
        String address = context.getResources().getString(R.string.reciver_name) + getname + "\n" + context.getResources().getString(R.string.reciver_mobile) + getphone +
                "\n" + context.getResources().getString(R.string.pincode) + getpin +
                "\n" + "Address:" + gethouse ;
        //  "\n" + context.getResources().getString(R.string.socity) + getsocity;

        return address;
    }
    public HashMap<String,String> getAlladdress() {


        HashMap<String,String> map=new HashMap<String, String>(  );
        map.put("name",getname);
        map.put("phone",getphone);
        map.put("pin",getpin);
        map.put("house",gethouse);
        map.put("society",getsocity);
        map.put("email",getemail);

        return map;
    }


    public boolean ischeckd() {
        return ischecked;
    }

//    @Override
//    public int getSwipeLayoutResourceId(int position) {
//        return R.id.swipe;
//    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeDeleteAddressRequest(String location_id,final int position) {
        loadingBar.show();
        // Tag used to cancel the request
        String tag_json_obj = "json_delete_address_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("location_id", location_id);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.DELETE_ADDRESS_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                loadingBar.dismiss();
                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        String msg = response.getString("message");

                        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show();

                        modelList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, modelList.size());
                        //  mItemManger.closeAllItems();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                Module module=new Module(context);
                String msg=module.VolleyErrorMessage(error);
                if(!msg.equals(""))
                {
                    Toast.makeText(context,""+msg,Toast.LENGTH_LONG).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

}
