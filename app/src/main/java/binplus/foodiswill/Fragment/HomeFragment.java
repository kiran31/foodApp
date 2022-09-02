package binplus.foodiswill.Fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import binplus.foodiswill.Adapter.AllProductsAdapter;
import binplus.foodiswill.Adapter.Home_Icon_Adapter;
import binplus.foodiswill.Adapter.Top_Selling_Adapter;
import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.CustomSlider;
import binplus.foodiswill.MainActivity;
import binplus.foodiswill.Model.Category_model;
import binplus.foodiswill.Model.Product_model;
import binplus.foodiswill.R;
import binplus.foodiswill.SelectCity;
import binplus.foodiswill.networkconnectivity.NoInternetConnection;
import binplus.foodiswill.util.ConnectivityReceiver;
import binplus.foodiswill.util.DatabaseCartHandler;
import binplus.foodiswill.util.LoadingBar;
import binplus.foodiswill.util.RecyclerTouchListener;
import binplus.foodiswill.util.Session_management;
import binplus.foodiswill.util.WishlistHandler;

import static android.view.View.GONE;
import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import static binplus.foodiswill.Config.BaseURL.CITY_ID;
import static binplus.foodiswill.Config.BaseURL.CITY_NAME;
import static binplus.foodiswill.Config.BaseURL.GET_ALL_CATEGORY_URL;
import static binplus.foodiswill.Config.BaseURL.GET_BANNER_URL;
import static binplus.foodiswill.Config.BaseURL.GET_DEAL_OF_THE_DAY;
import static binplus.foodiswill.Config.BaseURL.GET_NEW_PRODUCTS;
import static binplus.foodiswill.Config.BaseURL.GET_SLIDER_URL;
import static binplus.foodiswill.Config.BaseURL.GET_TOP_SELLING_PRODUCTS;
import static binplus.foodiswill.Config.BaseURL.GET_VERSTION_DATA;
import static binplus.foodiswill.Config.BaseURL.KEY_ID;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private final String TAG=HomeFragment.class.getSimpleName();
    LoadingBar loadingBar ;
    Session_management session_management ;
    RelativeLayout rel_cat ,rel_slider,rel_banner,rel_featured;
    LinearLayout lin_top ,lin_recommed;
    ImageView img_call,img_whtsapp ;
    RecyclerView rv_cat ,rv_top , rv_new ,rv_deals;
    SliderLayout home_slider,home_banner,home_featured;
    DatabaseCartHandler db_cart;
    WishlistHandler db_wish;
    SwipeRefreshLayout swipe;
   AllProductsAdapter top_adapter, new_adapter,deals_adapter;
    Home_Icon_Adapter cat_adapter;
    ArrayList<Category_model>catList;
    ArrayList<Product_model>topList,newList,dealsList;
    int version_code=0;
    Module module ;
    String app_link="" ,whtsapp_number ,phone_number;
    TextView tv_location;
    CardView card_loc,card_deals;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home,container,false);
        initViews(v);
        return v;
    }

    void initViews(View v)
    {
        topList = new ArrayList<>();
        catList = new ArrayList<>();
        newList = new ArrayList<>();
        dealsList = new ArrayList<>();
        module = new Module(getActivity());
        loadingBar = new LoadingBar(getActivity());
        session_management = new Session_management(getActivity());
        db_cart=new DatabaseCartHandler(getActivity());
        db_wish=new WishlistHandler(getActivity());
        rel_cat = v.findViewById(R.id.rel_category);
        rel_slider = v.findViewById(R.id.rel_slider);
        rel_banner = v.findViewById(R.id.rel_banner);
        rel_featured = v.findViewById(R.id.rel_features);
        lin_recommed = v.findViewById(R.id.lin_recommend);
        lin_top = v.findViewById(R.id.lin_top);
        img_call = v.findViewById(R.id.iv_call);
        img_whtsapp = v.findViewById(R.id.iv_whatsapp);
        swipe = v.findViewById(R.id.swipe);
        card_loc = v.findViewById(R.id.card_loc);
        card_deals = v.findViewById(R.id.card_deals);
        tv_location = v.findViewById(R.id.tv_loc);
      home_banner = v.findViewById(R.id.relative_banner);
      home_slider= v.findViewById(R.id.home_img_slider);
      home_featured= v.findViewById(R.id.featured_img_slider);
      rv_cat = v.findViewById(R.id.collapsing_recycler);
      rv_top = v.findViewById(R.id.top_selling_recycler);
      rv_new = v.findViewById(R.id.new_products_recycler);
      rv_deals = v.findViewById(R.id.deals_recycler);
      img_whtsapp.setOnClickListener(this);
      img_call.setOnClickListener(this);
      card_loc.setOnClickListener(this);
      tv_location.setText(session_management.getSessionItem(CITY_NAME));
        v.setFocusableInTouchMode(true);
        v.requestFocus();

        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are you sure want to exit?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            //((MainActivity) getActivity()).finish();
                            getActivity().finishAffinity();


                        }
                    })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    final AlertDialog dialog=builder.create();
                    dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                    });
                    dialog.show();
                    return true;
                }
                return false;
            }
        });
        rv_top.setLayoutManager( new GridLayoutManager(getActivity(),2, LinearLayoutManager.HORIZONTAL,false));
        rv_new.setLayoutManager( new GridLayoutManager(getActivity(),2, LinearLayoutManager.HORIZONTAL,false));
        rv_deals.setLayoutManager( new GridLayoutManager(getActivity(),2, LinearLayoutManager.HORIZONTAL,false));
        rv_cat.setLayoutManager(new LinearLayoutManager(getActivity(), HORIZONTAL,false));

        if (ConnectivityReceiver.isConnected()) {

            getAppSettingData();

        }
        else
        {
            startActivity(new Intent(getActivity(), NoInternetConnection.class));
        }
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getAppSettingData();

            }
        });
        rv_cat.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_cat, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(catList.get(position).getStatus().equalsIgnoreCase("0"))
                {
                    module.showToast(getResources().getString(R.string.undelirable));
                }
                else {

                    String parent = catList.get(position).getCount();
                    Log.e("selected_cat_count",parent.toString());

                    if (parent.equals("0")) {
                        Bundle args = new Bundle();
                        Fragment fm = new Product_fragment();
                        args.putString("cat_id", catList.get(position).getId());
                        args.putString("title", catList.get(position).getTitle());
                        session_management.setCategoryId(catList.get(position).getId());

                        fm.setArguments(args);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                .addToBackStack(null).commit();

                    } else {
                        Bundle args = new Bundle();
                        Fragment fm = new SubCategory_Fragment();
                        args.putString("cat_id", catList.get(position).getId());
                        args.putString("title", catList.get(position).getTitle());

                        fm.setArguments(args);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                .addToBackStack(null).commit();

                    }
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }
    @SuppressLint("NewApi")
    public void contactWhatsapp( String phone,String message) {

        PackageManager packageManager = getActivity().getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);

        try {
            String url = "https://api.whatsapp.com/send?phone=+91"+ phone +"&text=" + URLEncoder.encode(message, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iv_whatsapp:
                contactWhatsapp(whtsapp_number,"Hi!"+getActivity().getResources().getString(R.string.app_name));
                break;
                case R.id.iv_call:
                    Intent intent=new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel: "+phone_number));
                    getActivity().startActivity(intent);
                break;
            case R.id.card_loc:
                Intent i = new Intent(getActivity(), SelectCity.class);
                i.putExtra("from","home");
                getActivity().startActivity(i);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e( "onStart: ","newwewe" );
    }

    public void getAppSettingData()
    {
        loadingBar.show();
        String json_tag="json_app_tag";
        HashMap<String,String> map=new HashMap<>();
        module.postRequest(GET_VERSTION_DATA, map, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("app_setting",response.toString());
                if(swipe.isRefreshing()){
                    swipe.setRefreshing(false);
                }
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getBoolean("responce")) {
                        JSONObject object = obj.getJSONObject("data");
                        version_code = Integer.parseInt(object.getString("app_version"));
                        app_link = object.getString("data");
                        whtsapp_number = object.getString("home_whatsapp");
                        phone_number = object.getString("home_call");
                        String update_msg = object.getString("update_msg");

                            PackageInfo packageInfo=getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),0);
                            int ver_code=packageInfo.versionCode;
                            if(ver_code == version_code)
                            {
                            getCategory();
                            getBanner();
                            getslider();
                            getFeatured();
                            getNewProducts();
                            getTopProducts();
                            getDealProducts();
                        } else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setCancelable(false);
//                            builder.setMessage("The new version of app is available please update to get access.");
                            builder.setMessage(Html.fromHtml(update_msg));
                            builder.setPositiveButton("Update now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    String url = app_link;
                                    Intent in = new Intent(Intent.ACTION_VIEW);
                                    in.setData(Uri.parse(url));
                                    startActivity(in);
                                    getActivity().finish();
                                    //Toast.makeText(getActivity(),"updating",Toast.LENGTH_SHORT).show();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.dismiss();
                                    getActivity().finishAffinity();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                    }
                    else
                    {

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                loadingBar.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                module.showToast(module.VolleyErrorMessage(error));

            }
        });


    }


    @Override
    public void onPause() {
        super.onPause();
        // unregister reciver
        getActivity().unregisterReceiver(mCart);
        getActivity().unregisterReceiver(mWish);
    }

    @Override
    public void onResume() {
        super.onResume();
        // register reciver
        getActivity().registerReceiver(mCart, new IntentFilter("Grocery_cart"));
        getActivity().registerReceiver(mWish, new IntentFilter("Grocery_wish"));
    }

    // broadcast reciver for receive data
    private BroadcastReceiver mCart = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");

            if (type.contentEquals("update")) {
                updateData();
            }
        }
    };

    private BroadcastReceiver mWish = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");

            if (type.contentEquals("update")) {
                updateData();
            }
        }
    };

    private void updateData() {

        ((MainActivity) getActivity()).setCartCounter("" + db_cart.getCartCount());
        ((MainActivity) getActivity()).setWishCounter("" + db_wish.getWishtableCount(session_management.getUserDetails().get(KEY_ID)));
    }

    public boolean getUpdaterInfo()
    {
        boolean st=false;
        try
        {
            PackageInfo packageInfo=getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),0);
            int ver_code=packageInfo.versionCode;
            if(ver_code == version_code)
            {
                st=true;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return st;
    }



    private void getCategory() {
        loadingBar.show ();
        catList.clear();
        HashMap<String, String> params = new HashMap<> ( );
        params.put ("parent", "");
        params.put("city_id", session_management.getSessionItem(CITY_ID));

        module.postRequest (GET_ALL_CATEGORY_URL, params, new Response.Listener<String> ( ) {
            @Override
            public void onResponse(String response) {

                Log.e ("category_list", "onResponse: " + response.toString ( ));

                    try {
                        JSONObject object = new JSONObject (response);
                        boolean res = object.getBoolean ("responce");
                        if (res) {
                            JSONArray jsonArray =object.getJSONArray ("data");
                            Gson gson = new Gson ( );
                            Type listtype = new TypeToken<List<Category_model>>( ) {
                            }.getType ( );
                            catList = gson.fromJson (jsonArray.toString ( ), listtype);
                            rv_cat.setLayoutManager( new LinearLayoutManager(getActivity (),RecyclerView.HORIZONTAL,false));
                            cat_adapter = new Home_Icon_Adapter (catList);
                            rv_cat.setAdapter(cat_adapter);
                            cat_adapter.notifyDataSetChanged ();
                            rel_cat.setVisibility(View.VISIBLE);
                            loadingBar.dismiss ();
                        } else {

                           rel_cat.setVisibility(GONE);
                        }

                    } catch (JSONException e) {

                        e.printStackTrace ( );
                    }
                loadingBar.dismiss ();

                }


        }, new Response.ErrorListener ( ) {

            @Override
            public void onErrorResponse(VolleyError error) {

                module.showToast(module.VolleyErrorMessage(error));
                loadingBar.dismiss ();
            }
        });

    }



    public  void getslider(){
        loadingBar.show ();
        HashMap<String, String> params = new HashMap<> ( );
        params.put("city_id",session_management.getSessionItem(CITY_ID));

        module.postRequest (GET_SLIDER_URL, params, new Response.Listener<String> ( ) {
            @Override
            public void onResponse(String response) {
                Log.e ("slidder_list", ""+response .toString ());
                try {
                    JSONArray array = new JSONArray(response);

                    if (array.length()>0) {
                        ArrayList<HashMap<String, String>> listarray = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = (JSONObject) array.get(i);
                            HashMap<String, String> url_maps = new HashMap<String, String>();
                            url_maps.put("slider_title", jsonObject.getString("slider_title"));
                            url_maps.put("sub_cat", jsonObject.getString("sub_cat"));
                            url_maps.put("parent", jsonObject.getString("parent"));
                            url_maps.put("leval", jsonObject.getString("leval"));
                            url_maps.put("slider_image", BaseURL.IMG_SLIDER_URL + jsonObject.getString("slider_image"));
                            listarray.add(url_maps);
                        }
                        for (final HashMap<String, String> name : listarray) {
                            CustomSlider textSliderView = new CustomSlider(getActivity());
                            textSliderView.description(name.get("")).image(name.get("slider_image")).setScaleType(BaseSliderView.ScaleType.Fit);
                            textSliderView.bundle(new Bundle());
                            textSliderView.getBundle().putString("extra", name.get("slider_title"));
                            textSliderView.getBundle().putString("extra", name.get("sub_cat"));
                            home_slider.addSlider(textSliderView);
                            final String sub_cat = (String) textSliderView.getBundle().get("extra");
                            textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {

                                    Bundle args = new Bundle();
                                    Fragment fm = null;
                                    args.putString("cat_id", sub_cat);
                                    args.putString("title", name.get("slider_title"));
                                    session_management.setCategoryId(sub_cat);
                                    if (name.get("parent").equals("0")) {
                                        fm = new SubCategory_Fragment();
                                    } else {
                                        //   Toast.makeText(getActivity(), "" + sub_cat, Toast.LENGTH_SHORT).show();
                                        fm = new Product_fragment();
                                    }
                                    fm.setArguments(args);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                            .addToBackStack(null).commit();
                                }
                            });

                        }
                            rel_slider.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        rel_slider.setVisibility(GONE);
                    }
                    loadingBar.dismiss ();
                } catch (JSONException e) {
                    e.printStackTrace ( );
                }
            }
        }, new Response.ErrorListener ( ) {
            @Override
            public void onErrorResponse(VolleyError error) {

                module.showToast(module.VolleyErrorMessage(error));
            }
        });

    }

    public  void getBanner(){
        loadingBar.show ();
        HashMap<String, String> params = new HashMap<> ( );
        params.put("city_id",session_management.getSessionItem(CITY_ID));
        module.postRequest (GET_BANNER_URL, params, new Response.Listener<String> ( ) {
            @Override
            public void onResponse(String response) {
                Log.e ("banner_list", ""+response .toString ());
                try {
                    JSONArray array = new JSONArray(response);
                    if (array.length()>0) {
                        ArrayList<HashMap<String, String>> listarray = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = (JSONObject) array.get(i);
                            HashMap<String, String> url_maps = new HashMap<String, String>();
                            url_maps.put("banner_id", jsonObject.getString("banner_id"));
                            url_maps.put("slider_title", jsonObject.getString("slider_title"));
                            url_maps.put("slider_image", BaseURL.IMG_SLIDER_URL + jsonObject.getString("slider_image"));
                            url_maps.put("sub_cat",jsonObject.getString("sub_cat"));
                            url_maps.put("parent",jsonObject.getString("parent"));
                            url_maps.put("leval",jsonObject.getString("leval"));

                            //   Toast.makeText(context,""+modelList.get(position).getProduct_image(),Toast.LENGTH_LONG).show();

                            listarray.add(url_maps);
                        }
                        for (final HashMap<String, String> name : listarray) {
                            CustomSlider textSliderView = new CustomSlider(getActivity());
                            textSliderView.description(name.get("")).image(name.get("slider_image")).setScaleType( BaseSliderView.ScaleType.Fit);
                            textSliderView.bundle(new Bundle());
                            textSliderView.getBundle().putString("extra", name.get("slider_title"));
                            textSliderView.getBundle().putString("extra", name.get("sub_cat"));
                            home_banner.addSlider(textSliderView);
                            final String sub_cat = (String) textSliderView.getBundle().get("extra");
                            textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    String cat_id=name.get("sub_cat").toString();
                                    String cat_title=name.get("slider_title").toString();
                                    Bundle args = new Bundle();
                                    Fragment fm =null;
                                    args.putString("cat_id",cat_id);
                                    args.putString("title",cat_title);
                                    if(name.get("parent").equals("0"))
                                    {
                                        fm=new SubCategory_Fragment();
                                    }
                                    else
                                    {
                                        fm=new Product_fragment();
                                    }
                                    fm.setArguments(args);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                            .addToBackStack(null).commit();

                                }
                            });

                        }
                        rel_banner.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        rel_banner.setVisibility(GONE);
                    }
                    loadingBar.dismiss ();
                } catch (JSONException e) {
                    e.printStackTrace ( );
                }
            }
        }, new Response.ErrorListener ( ) {
            @Override
            public void onErrorResponse(VolleyError error) {

                module.showToast(module.VolleyErrorMessage(error));
            }
        });

    }
    public  void getFeatured(){
        loadingBar.show ();
        HashMap<String, String> params = new HashMap<> ( );
        params.put("city_id",session_management.getSessionItem(CITY_ID));
        module.postRequest (GET_BANNER_URL, params, new Response.Listener<String> ( ) {
            @Override
            public void onResponse(String response) {
                Log.e ("feature_list", ""+response .toString ());
                try {
                    JSONArray array = new JSONArray(response);
                    if (array.length()>0) {
                        ArrayList<HashMap<String, String>> listarray = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = (JSONObject) array.get(i);
                            HashMap<String, String> url_maps = new HashMap<String, String>();
                            url_maps.put("slider_title", jsonObject.getString("slider_title"));
                            url_maps.put("sub_cat", jsonObject.getString("sub_cat"));
                            url_maps.put("slider_status", jsonObject.getString("slider_status"));

                            url_maps.put("slider_image", BaseURL.IMG_SLIDER_URL + jsonObject.getString("slider_image"));
                            listarray.add(url_maps);
                        }
                        for (final HashMap<String, String> name : listarray) {
                            CustomSlider textSliderView = new CustomSlider(getActivity());
                            textSliderView.description(name.get("")).image(name.get("slider_image")).setScaleType( BaseSliderView.ScaleType.Fit);
                            textSliderView.bundle(new Bundle());
                            textSliderView.getBundle().putString("extra", name.get("slider_title"));
                            textSliderView.getBundle().putString("extra", name.get("sub_cat"));
                           home_featured.addSlider(textSliderView);
                            final String sub_cat = (String) textSliderView.getBundle().get("extra");

                        }
                        rel_featured.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        rel_featured.setVisibility(GONE);
                    }
                    loadingBar.dismiss ();
                } catch (JSONException e) {
                    e.printStackTrace ( );
                }
            }
        }, new Response.ErrorListener ( ) {
            @Override
            public void onErrorResponse(VolleyError error) {

                module.showToast(module.VolleyErrorMessage(error));
            }
        });

    }

    void getTopProducts()
    {
        loadingBar.show();
        topList.clear();
        final HashMap<String,String> params = new HashMap<>();
        params.put("user_id",session_management.getSessionItem(KEY_ID));
        params.put("city_id",session_management.getSessionItem(CITY_ID));
        module.postRequest(GET_TOP_SELLING_PRODUCTS,params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("top_product",params+"\n"+response.toString());
                try {
                    JSONObject jsonObject =  new JSONObject(response);
                    if (jsonObject.getBoolean("responce"))
                    {
                        if (jsonObject.has("top_selling_product")) {
                            Gson gson = new Gson();
                            Type typelist = new TypeToken<List<Product_model>>() {
                            }.getType();
                            topList = gson.fromJson(jsonObject.getJSONArray("top_selling_product").toString(), typelist);
                            Log.e("topList", topList.size() + "");
                            if (topList.size() > 0) {
                                lin_top.setVisibility(View.VISIBLE);
                                top_adapter = new AllProductsAdapter( topList,getActivity(),"top");
                               rv_top.setAdapter(top_adapter);
                                top_adapter.notifyDataSetChanged();
                            } else {
                                lin_top.setVisibility(GONE);
                            }
                        }
                        else
                        {
                            lin_top.setVisibility(GONE);
                        }

                    }
                    else
                    {
                        lin_top.setVisibility(GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loadingBar.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                module.showToast(module.VolleyErrorMessage(error));
            }
        });
    }
    void getDealProducts()
    {
        loadingBar.show();
        topList.clear();
        final HashMap<String,String> params = new HashMap<>();
        params.put("user_id",session_management.getSessionItem(KEY_ID));
        params.put("city_id",session_management.getSessionItem(CITY_ID));
        module.postRequest(GET_DEAL_OF_THE_DAY,params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("deal_product",params+"\n"+response.toString());
                try {
                    JSONObject jsonObject =  new JSONObject(response);
                    if (jsonObject.getBoolean("responce"))
                    {
                        if (jsonObject.has("data")) {
                            Gson gson = new Gson();
                            Type typelist = new TypeToken<List<Product_model>>() {
                            }.getType();
                            dealsList = gson.fromJson(jsonObject.getJSONArray("data").toString(), typelist);
                            Log.e("dealList", dealsList.size() + "");
                            if (dealsList.size() > 0) {
                               card_deals.setVisibility(View.VISIBLE);
                                deals_adapter = new AllProductsAdapter( dealsList,getActivity(),"top");
                               rv_deals.setAdapter(deals_adapter);
                             deals_adapter.notifyDataSetChanged();
                            } else {
                               card_deals.setVisibility(GONE);
                            }
                        }
                        else
                        {
                            card_deals.setVisibility(GONE);
                        }

                    }
                    else
                    {
                       card_deals.setVisibility(GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loadingBar.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                module.showToast(module.VolleyErrorMessage(error));
            }
        });
    }
    void getNewProducts()
    {
        loadingBar.show();
        newList.clear();
        final HashMap<String,String> params = new HashMap<>();
        params.put("user_id",session_management.getSessionItem(KEY_ID));
        params.put("city_id",session_management.getSessionItem(CITY_ID));
        module.postRequest(GET_NEW_PRODUCTS,params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("new_product",params+"\n"+response.toString());
                try {
                    JSONObject jsonObject =  new JSONObject(response);
                    if (jsonObject.getBoolean("responce"))
                    {
                        if (jsonObject.has("new_product")) {
                            Gson gson = new Gson();
                            Type typelist = new TypeToken<List<Product_model>>() {
                            }.getType();
                            newList = gson.fromJson(jsonObject.getJSONArray("new_product").toString(), typelist);
                            Log.e("newList", newList.size() + "");
                            if (newList.size() > 0) {
                                lin_recommed.setVisibility(View.VISIBLE);
                                new_adapter = new AllProductsAdapter( newList,getActivity(),"new");
                                rv_new.setAdapter(new_adapter);
                                new_adapter.notifyDataSetChanged();
                            } else {
                                lin_recommed.setVisibility(GONE);
                            }

                        }
                        else
                        {
                            lin_recommed.setVisibility(GONE);
                        }
                    }
                    else
                    {
                        lin_recommed.setVisibility(GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loadingBar.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
                module.showToast(module.VolleyErrorMessage(error));
            }
        });
    }

}
