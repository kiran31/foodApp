package binplus.foodiswill.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import binplus.foodiswill.Adapter.AllProductsAdapter;
import binplus.foodiswill.Adapter.SortAdapter;
import binplus.foodiswill.Adapter.ProductAdapter;
import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.Model.Category_model;
import binplus.foodiswill.Model.Product_model;
import binplus.foodiswill.Model.Slider_subcat_model;
import binplus.foodiswill.AppController;
import binplus.foodiswill.MainActivity;
import binplus.foodiswill.R;
import binplus.foodiswill.networkconnectivity.NoInternetConnection;
import binplus.foodiswill.util.ConnectivityReceiver;
import binplus.foodiswill.util.CustomVolleyJsonRequest;
import binplus.foodiswill.util.DatabaseCartHandler;
import binplus.foodiswill.util.Session_management;
import binplus.foodiswill.util.WishlistHandler;

import static binplus.foodiswill.Config.BaseURL.KEY_ID;


public class Product_fragment extends Fragment implements View.OnClickListener {
    private static String TAG = Product_fragment.class.getSimpleName();
    private RecyclerView rv_cat;
    Dialog loadingBar;
    Module module;
    WishlistHandler db_wish;
    ImageView no_product;
    Session_management session_management;
    private TabLayout tab_cat ;
    String deli_charges;
    private LinearLayout tab_filter;
    private ImageView tab_grid;
    TextView tab_sort;
    RelativeLayout rel_sort;
    private List<Category_model> category_modelList = new ArrayList<>();
    private List<Slider_subcat_model> slider_subcat_models = new ArrayList<>();
    private List<String> cat_menu_id = new ArrayList<>();
    private ArrayList<Product_model> product_modelList = new ArrayList<>();
    private ArrayList<Product_model> product_grid_modelList = new ArrayList<>();
   private AllProductsAdapter product_adapter;
//    private ProductAdapter product_adapter;
    private SortAdapter sortAdapter ;
    //private SliderLayout  banner_slider;
    String language;
    Dialog loadingBar1;
    String getcat_id ;
    SharedPreferences preferences;
    DatabaseCartHandler db_cart;
    SwipeRefreshLayout swipe;
    boolean swipe_flag=false;
    public Product_fragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_product, container, false);

        loadingBar=new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        loadingBar.setContentView( R.layout.progressbar );
        loadingBar.setCanceledOnTouchOutside(false);
        tab_cat = (TabLayout) view.findViewById( R.id.tab_cat);
        tab_filter =(LinearLayout) view.findViewById( R.id.tab_layout );
        tab_grid = (ImageView) view.findViewById( R.id.grid );
        tab_sort =(TextView) view.findViewById( R.id.sort );
        rel_sort=view.findViewById(R.id.rel_sort);
        tab_grid.setOnClickListener( this );
        tab_sort.setOnClickListener( this );
        rel_sort.setOnClickListener( this );
        module=new Module(getActivity());
        db_wish=new WishlistHandler(getActivity());
        session_management=new Session_management(getActivity());

        //   banner_slider = (SliderLayout) view.findViewById(R.id.relative_banner);
        rv_cat =  view.findViewById( R.id.rv_subcategory);

        no_product =(ImageView)view.findViewById( R.id.img_no_product );

        rv_cat.setLayoutManager(new LinearLayoutManager(getActivity()));
        getcat_id = getArguments().getString("cat_id");
        String id = getArguments().getString("id");
        String get_deal_id = getArguments().getString("cat_deal");
        String get_top_sale_id = getArguments().getString("cat_top_selling");
        String getcat_title = getArguments().getString("title");
        db_cart=new DatabaseCartHandler(getActivity());
        ((MainActivity) getActivity()).setTitle(getcat_title);

        // check internet connection
        if (ConnectivityReceiver.isConnected()) {

            makeGetProductRequest(getcat_id);


        }
        else
        {
            Intent intent = new Intent( getActivity() , NoInternetConnection.class);
            startActivity(intent);
        }
        swipe=view.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_flag=true;
                if (ConnectivityReceiver.isConnected()) {

                    makeGetProductRequest(getcat_id);


                }
                else
                {
                    Intent intent = new Intent( getActivity() , NoInternetConnection.class);
                    startActivity(intent);
                }


            }
        });

//
//
        tab_cat.setSelectedTabIndicatorColor(getActivity().getResources().getColor( R.color.white));

        tab_cat.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String getcat_id = cat_menu_id.get(tab.getPosition());
                if (ConnectivityReceiver.isConnected()) {
                    //Shop By Catogary Products
                    //makeGetProductRequest(getcat_id);
                    ((MainActivity) getActivity()).setTitle(String.valueOf( tab.getText() ));
                    if(getcat_id.isEmpty())
                    {
                        no_product.setVisibility(View.VISIBLE);
                        rv_cat.setVisibility( View.GONE );
                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });








        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        //  loadingBar.show();

    }

    private void makeGetProductRequest(String cat_id) {

        loadingBar1=new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        loadingBar1.setContentView( R.layout.progressbar );
        loadingBar1.setCanceledOnTouchOutside(false);
        loadingBar1.show();
        product_modelList.clear();
        String tag_json_obj = "json_product_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("cat_id", cat_id);
        params.put("city_id", module.getCityID());

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_NEW_PRODUCTS, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("rett " +
                        "", response.toString());
                if(swipe_flag){
                    swipe.setRefreshing(false);
                }
                loadingBar1.dismiss();
                try {
                    if(response.has("data"))
                    {

                        no_product.setVisibility( View.GONE );
                        rv_cat.setVisibility( View.VISIBLE );
                    }
                    else
                    {

                        no_product.setVisibility( View.VISIBLE );
                        rv_cat.setVisibility( View.GONE );
                    }

                    Boolean status = response.getBoolean("responce");

                    if (status) {
                        ///         Toast.makeText(getActivity(),""+response.getString("data"),Toast.LENGTH_LONG).show();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();
//                        if (response.has( "data" )) {
                        product_modelList = gson.fromJson( response.getString( "new_product" ), listType );
                        product_grid_modelList = gson.fromJson( response.getString( "new_product" ), listType );
//                            loadingBar.dismiss();
//                      adapter_product = new Product_adapter(product_modelList, getActivity());
                        module.showToast(""+product_modelList.size()+" products found");
                        product_adapter = new AllProductsAdapter( product_modelList, getActivity(),"all" );
                        GridLayoutManager gridLayoutManager = new GridLayoutManager( getActivity(), 2 );
                        rv_cat.setLayoutManager( gridLayoutManager );
                        rv_cat.setAdapter(product_adapter);
                       product_adapter.notifyDataSetChanged();


                            if (product_modelList.isEmpty()) {

                                loadingBar.dismiss();

                                no_product.setVisibility( View.VISIBLE );
                                rv_cat.setVisibility( View.GONE );
                                //Toast.makeText(getActivity(), getResources().getString( R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                            }
                            else
                        {
                            rv_cat.setVisibility( View.VISIBLE );
                           no_product.setVisibility( View.GONE );
                        }

                    }
                    else
                    {
                        no_product.setVisibility( View.VISIBLE );
                        rv_cat.setVisibility( View.GONE );
                    }
                } catch (JSONException e) {
                    //   e.printStackTrace();
                    String ex=e.getMessage();




                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar1.dismiss();
                String msg=module.VolleyErrorMessage(error);
                if(!msg.equals(""))
                {
                    Toast.makeText(getActivity(),""+msg,Toast.LENGTH_LONG).show();
                }

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }








    ////Get DEal Products
    private void makeDescendingProductRequest(String cat_id) {
        String tag_json_obj = "json_product_desc_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("cat_id", cat_id);
        params.put("city_id", module.getCityID());
        loadingBar.show();
        product_modelList.clear();

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_PRODUCT_DESC, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("desccccccc", response.toString());
                loadingBar.dismiss();
                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();
//                        if (response.has( "data" )) {
                        product_modelList = gson.fromJson( response.getString( "data" ), listType );
                        no_product.setVisibility( View.GONE );
                        rv_cat.setVisibility( View.VISIBLE);
//                          adapter_product = new Product_adapter(product_modelList, getActivity());
                       product_adapter = new AllProductsAdapter( product_modelList, getActivity(),"all" );
                        module.showToast(""+product_modelList.size()+" products found");
                        GridLayoutManager gridLayoutManager = new GridLayoutManager( getActivity(), 2 );
                        rv_cat.setLayoutManager( gridLayoutManager );
                        rv_cat.setAdapter(product_adapter);
                      product_adapter.notifyDataSetChanged();
                        if (getActivity() != null) {
                            if (product_modelList.isEmpty()) {
                                Toast.makeText( getActivity(), getResources().getString( R.string.no_rcord_found ), Toast.LENGTH_SHORT ).show();
                            } else {
                                //  Toast.makeText(getActivity(),""+response, Toast.LENGTH_SHORT).show();
                            }
                        }
//                        }
                        else
                        {
                            no_product.setVisibility( View.VISIBLE );
                            rv_cat.setVisibility( View.GONE );
                        }

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
                    Toast.makeText(getActivity(),""+msg,Toast.LENGTH_LONG).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private void makeAscendingProductRequest(String cat_id) {
        String tag_json_obj = "json_product_asc_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("cat_id", cat_id);
        params.put("city_id", module.getCityID());
        product_modelList.clear();
        loadingBar.show();
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_PRODUCT_ASC, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("asssssssesss", response.toString());
                loadingBar.dismiss();
                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();
//                        if (response.has( "data" )) {
                        product_modelList = gson.fromJson( response.getString( "data" ), listType );
                        no_product.setVisibility( View.GONE );
                        rv_cat.setVisibility( View.VISIBLE );
//                            adapter_product = new Product_adapter(product_modelList, getActivity());
                        product_adapter = new AllProductsAdapter( product_modelList, getActivity(),"all" );
                        module.showToast(""+product_modelList.size()+" products found");
                        GridLayoutManager gridLayoutManager = new GridLayoutManager( getActivity(), 2 );

                        rv_cat.setLayoutManager( gridLayoutManager );
                        rv_cat.setAdapter(product_adapter);
                     product_adapter.notifyDataSetChanged();
                        if (getActivity() != null) {
                            if (product_modelList.isEmpty()) {
                                no_product.setVisibility( View.VISIBLE );
                                rv_cat.setVisibility( View.GONE );
                                Toast.makeText( getActivity(), getResources().getString( R.string.no_rcord_found ), Toast.LENGTH_SHORT ).show();
                            } else {
                                //  Toast.makeText(getActivity(),""+response, Toast.LENGTH_SHORT).show();
                            }
                        }
//                        }
                        else
                        {
                            no_product.setVisibility( View.VISIBLE );
                            rv_cat.setVisibility( View.GONE );
                        }

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
                    Toast.makeText(getActivity(),""+msg,Toast.LENGTH_LONG).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void makeNewestProductRequest(String cat_id) {
        String tag_json_obj = "json_product_newest_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("cat_id", cat_id);
        params.put("city_id", module.getCityID());
        product_modelList.clear();
        loadingBar.show();
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_PRODUCT_NEWEST, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                loadingBar.dismiss();
                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();
                        if (response.has("data" )) {
                            product_modelList = gson.fromJson( response.getString( "data" ), listType );
                            no_product.setVisibility( View.GONE);
                            rv_cat.setVisibility( View.VISIBLE);
//                              adapter_product = new Product_adapter(product_modelList, getActivity());
                           product_adapter = new AllProductsAdapter( product_modelList, getActivity(),"all" );
                            module.showToast(""+product_modelList.size()+" products found");
                            GridLayoutManager gridLayoutManager = new GridLayoutManager( getActivity(), 2 );
                            rv_cat.setLayoutManager( gridLayoutManager );
                            rv_cat.setAdapter(product_adapter);
                           product_adapter.notifyDataSetChanged();
                            if (getActivity() != null) {
                                if (product_modelList.isEmpty()) {
                                    no_product.setVisibility( View.VISIBLE );
                                    rv_cat.setVisibility( View.GONE );
                                    Toast.makeText( getActivity(), getResources().getString( R.string.no_rcord_found ), Toast.LENGTH_SHORT ).show();
                                } else {
                                    //  Toast.makeText(getActivity(),""+response, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        else
                        {
                            no_product.setVisibility( View.VISIBLE );
                            rv_cat.setVisibility( View.GONE );
                        }

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
                    Toast.makeText(getActivity(),""+msg,Toast.LENGTH_LONG).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }




    ////Get Top Sale Products
    private void maketopsaleProductRequest(String cat_id) {
        loadingBar.show();
        String tag_json_obj = "json_product_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("top_selling_product", cat_id);
        product_modelList.clear();
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_ALL_TOP_SELLING_PRODUCTS, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("salesss", response.toString());

                try {
                    loadingBar.dismiss();
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();
//                        if (response.has( "data" )) {
                        product_modelList = gson.fromJson( response.getString( "top_selling_product" ), listType );
//                         adapter_product = new Product_adapter(product_modelList, getActivity());
                     product_adapter = new AllProductsAdapter( product_modelList, getActivity(),"all" );
                        GridLayoutManager gridLayoutManager = new GridLayoutManager( getActivity(), 2 );
                        rv_cat.setLayoutManager( gridLayoutManager );
                        rv_cat.setAdapter(product_adapter);
                        product_adapter.notifyDataSetChanged();
                        if (getActivity() != null) {
                            if (product_modelList.isEmpty()) {
                                loadingBar.dismiss();
                                no_product.setVisibility( View.VISIBLE );
                                rv_cat.setVisibility( View.GONE );
                                Toast.makeText( getActivity(), getResources().getString( R.string.no_rcord_found ), Toast.LENGTH_SHORT ).show();
                            }
                        }
//                        }
//                        else
//                        {
//                            no_product.setVisibility( View.VISIBLE );
//                            rv_cat.setVisibility( View.GONE );
//                        }
                    }
                } catch (JSONException e) {
                    loadingBar.dismiss();
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



    @Override
    public void onResume() {
        super.onResume();
        // register reciver
        getActivity().registerReceiver(mCart, new IntentFilter("Grocery_cart"));
        getActivity().registerReceiver(mWish, new IntentFilter("Grocery_wish"));
    }

    // broadcast reciver for receive data
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


    @Override
    public void onPause() {
        super.onPause();
        // unregister reciver
        getActivity().unregisterReceiver(mCart);
        getActivity().unregisterReceiver(mWish);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        final String cat_id = getArguments().getString("cat_id");
        String p_id = getArguments().getString("id");
        String get_deal_id = getArguments().getString("cat_deal");
        String get_top_sale_id = getArguments().getString("cat_top_selling");
        String getcat_title = getArguments().getString("title");

        if (id == R.id.sort)
        {
            final ArrayList <String>  sort_List = new ArrayList<>(  );
            sort_List.add( "Price Low - High" );
            sort_List.add("Price High - Low");
            sort_List.add("Newest First");
            //  sort_List.add ("Trending");
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            LayoutInflater layoutInflater=(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row=layoutInflater.inflate( R.layout.dialog_sort_layout,null);
            ListView l1=(ListView)row.findViewById( R.id.list_sort);
            sortAdapter=new SortAdapter(getActivity(),sort_List);
            //productVariantAdapter.notifyDataSetChanged();
            l1.setAdapter(sortAdapter);
            builder.setView(row);
            final AlertDialog ddlg=builder.create();
            ddlg.show();
            l1.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ddlg.dismiss();
                    String item = sort_List.get( i ).toString();

                    String c_id=session_management.getCategoryId();
                    if (item.equals( "Price Low - High" ))
                    {
                        // ddlg.dismiss();
                        makeAscendingProductRequest( c_id );
                        tab_grid.setImageResource( R.drawable.icons8_activity_grid_2_48px);
                        tab_grid.setTag( "grid" );
                    }
                    else if(item.equals( "Price High - Low" ))
                    {
                        // Toast.makeText( getActivity(), "category id :" +getcat_id, Toast.LENGTH_SHORT ).show();
                        // ddlg.dismiss();
                        makeDescendingProductRequest(c_id);
                        tab_grid.setImageResource( R.drawable.icons8_activity_grid_2_48px);
                        tab_grid.setTag( "grid" );


                    }
                    else if(item.equals( "Newest First" ))
                    {
                        // ddlg.dismiss();
                        makeNewestProductRequest( c_id );
                        tab_grid.setImageResource( R.drawable.icons8_activity_grid_2_48px);
                        tab_grid.setTag( "grid" );
                    }
                    else if (item.equals( "Trending" ))
                    {

                    }

                    // Toast.makeText( getActivity(),"Showing items:" +item,Toast.LENGTH_LONG ).show();
                }
            } );
        }
        else if(id == R.id.rel_sort)
        {
            final ArrayList <String>  sort_List = new ArrayList<>(  );
            sort_List.add( "Price Low - High" );
            sort_List.add("Price High - Low");
            sort_List.add("Newest First");
            //  sort_List.add ("Trending");
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            LayoutInflater layoutInflater=(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row=layoutInflater.inflate( R.layout.dialog_sort_layout,null);
            ListView l1=(ListView)row.findViewById( R.id.list_sort);
            sortAdapter=new SortAdapter(getActivity(),sort_List);
            //productVariantAdapter.notifyDataSetChanged();
            l1.setAdapter(sortAdapter);
            builder.setView(row);
            final AlertDialog ddlg=builder.create();
            ddlg.show();
            l1.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ddlg.dismiss();
                    String item = sort_List.get( i ).toString();

                    String c_id=session_management.getCategoryId();
                    if (item.equals( "Price Low - High" ))
                    {
                        // ddlg.dismiss();
                        makeAscendingProductRequest( c_id );
                        tab_grid.setImageResource( R.drawable.icons8_activity_grid_2_48px);
                        tab_grid.setTag( "grid" );
                    }
                    else if(item.equals( "Price High - Low" ))
                    {
                        // Toast.makeText( getActivity(), "category id :" +getcat_id, Toast.LENGTH_SHORT ).show();
                        // ddlg.dismiss();
                        makeDescendingProductRequest(c_id);
                        tab_grid.setImageResource( R.drawable.icons8_activity_grid_2_48px);
                        tab_grid.setTag( "grid" );


                    }
                    else if(item.equals( "Newest First" ))
                    {
                        // ddlg.dismiss();
                        makeNewestProductRequest( c_id );
                        tab_grid.setImageResource( R.drawable.icons8_activity_grid_2_48px);
                        tab_grid.setTag( "grid" );
                    }
                    else if (item.equals( "Trending" ))
                    {

                    }

                    // Toast.makeText( getActivity(),"Showing items:" +item,Toast.LENGTH_LONG ).show();
                }
            } );
        }
        else if (id == R.id.grid)
        {
            String tag = String.valueOf( tab_grid.getTag() );
            if(tag.equals( "grid" )) {
                tab_grid.setImageResource( R.drawable.icons8_menu_48px );
                tab_grid.setTag( "linear" );
//                adapter_product = new Product_adapter(product_modelList,getActivity());
              product_adapter = new AllProductsAdapter( product_grid_modelList, getActivity(),"all" );
                rv_cat.setLayoutManager( new GridLayoutManager( getActivity(), 2 ) );
                rv_cat.setAdapter(product_adapter);
                   product_adapter.notifyDataSetChanged();
            }
            else if (tag.equals( "linear" ))
            {
                tab_grid.setImageResource( R.drawable.icons8_activity_grid_2_48px);
                tab_grid.setTag( "grid" );

//              adapter_product = new Product_adapter( product_modelList, getActivity() );
//                rv_cat.setLayoutManager( new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
//                rv_cat.setAdapter(adapter_product );
            }

        }
    }




}



