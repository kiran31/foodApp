package binplus.foodiswill.Fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.ybq.android.spinkit.SpinKitView;
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
import binplus.foodiswill.Adapter.ProductAdapter;
import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.Model.Product_model;
import binplus.foodiswill.AppController;
import binplus.foodiswill.MainActivity;
import binplus.foodiswill.R;
import binplus.foodiswill.util.ConnectivityReceiver;
import binplus.foodiswill.util.CustomVolleyJsonRequest;
import binplus.foodiswill.util.DatabaseCartHandler;
import binplus.foodiswill.util.Session_management;
import binplus.foodiswill.util.WishlistHandler;

import static binplus.foodiswill.Config.BaseURL.KEY_ID;

public class Search_fragment extends Fragment {

  DatabaseCartHandler db_cart;
  WishlistHandler db_wish;
  Module module;
  Session_management session_management;
  private static String TAG = Search_fragment.class.getSimpleName();
  private EditText acTextView;
  private Button btn_search;
  private RecyclerView rv_search;
  ImageView img_no_products;
  String search_text="";
  SpinKitView progress;
  String pagenumber="";
  private ArrayList<Product_model> product_modelList = new ArrayList<>();
  private AllProductsAdapter adapter_product;

  Dialog loadingBar ;
  boolean is_scrolling=false;
  int currentItems,totalItems,scrollOutItems;
  GridLayoutManager manager;

  public Search_fragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadingBar=new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
    loadingBar.setContentView( R.layout.progressbar );
    loadingBar.setCanceledOnTouchOutside(false);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_search, container, false);

    ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.search));
    module=new Module(getActivity());
    loadingBar=new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
    loadingBar.setContentView( R.layout.progressbar );
    loadingBar.setCanceledOnTouchOutside(false);
    session_management=new Session_management(getActivity());
    db_cart=new DatabaseCartHandler(getActivity());
    db_wish=new WishlistHandler(getActivity());
    acTextView = (EditText) view.findViewById(R.id.et_search);
    img_no_products = (ImageView) view.findViewById(R.id.img_no_products);
    progress=view.findViewById(R.id.spin_kit);

//    acTextView.setAdapter(new SuggestionAdapter(getActivity(), acTextView.getText().toString()));

    btn_search = (Button) view.findViewById(R.id.btn_search);
    rv_search = (RecyclerView) view.findViewById(R.id.rv_search);
//    rv_search.setNestedScrollingEnabled(false);
    manager=new GridLayoutManager(getActivity(),2);
    rv_search.setLayoutManager(manager);
    adapter_product = new AllProductsAdapter(product_modelList, getActivity(),"all");

    rv_search.setAdapter(adapter_product);

    btn_search.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        String get_search_txt ="%"+ acTextView.getText().toString() +"%";
        if (acTextView.length()<=0) {
          acTextView.setError( "Enter product to search" );
          acTextView.requestFocus();
          // Toast.makeText(getActivity(), getResources().getString(R.string.enter_keyword), Toast.LENGTH_SHORT).show();
        }
        else {
          pagenumber="";
          search_text=get_search_txt;
          if (ConnectivityReceiver.isConnected()) {
            makeGetProductRequest(get_search_txt);
          } else {
            ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
          }
        }
        Log.e("asdasd",""+get_search_txt);

      }
    });

    rv_search.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
        {

          is_scrolling=true;
        }
      }

      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        currentItems=manager.getChildCount();
        totalItems=manager.getItemCount();
        scrollOutItems=manager.findFirstVisibleItemPosition();
        if(is_scrolling &&(currentItems+scrollOutItems==totalItems))
        {
              is_scrolling=false;

          makeGetProductRequest(search_text);
        }
      }
    });

////    rv_search.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_search, new RecyclerTouchListener.OnItemClickListener() {
////      @Override
////      public void onItemClick(View view, int position) {
////
////
////
////        Fragment details_fragment = new Details_Fragment();
////        // bundle.putString("data",as);
////        Bundle args = new Bundle();
////
////        //Intent intent=new Intent(context, Product_details.class);
////        args.putString("cat_id", product_modelList.get(position).getCategory_id());
////        args.putString("product_id", product_modelList.get(position).getProduct_id());
////        args.putString("product_images", product_modelList.get(position).getProduct_image());
////        args.putString("product_name", product_modelList.get(position).getProduct_name());
////        args.putString("product_description", product_modelList.get(position).getProduct_description());
////        args.putString("stock", product_modelList.get(position).getStock());
////        args.putString("in_stock", product_modelList.get(position).getIn_stock());
////        args.putString("product_attribute", product_modelList.get(position).getProduct_attribute());
////        args.putString("price", product_modelList.get(position).getPrice());
////        args.putString("mrp", product_modelList.get(position).getMrp());
////        args.putString("unit_value", product_modelList.get(position).getUnit_value());
////        args.putString("unit", product_modelList.get(position).getUnit());
////        args.putString("rewards", product_modelList.get(position).getRewards());
////        args.putString("increment", product_modelList.get(position).getIncreament());
////        args.putString("title", product_modelList.get(position).getTitle());
////        args.putString("product_name_hindi", product_modelList.get(position).getProduct_name_hindi());
////
////        details_fragment.setArguments(args);
////        FragmentManager fragmentManager = getFragmentManager();
////        fragmentManager.beginTransaction().replace(R.id.contentPanel, details_fragment)
////
////                .addToBackStack(null).commit();
////
////
////      }
//
//      @Override
//      public void onLongItemClick(View view, int position) {
//
//      }
//    }));
    return view;
  }

  /**
   * Method to make json object request where json response starts wtih {
   */
  private void makeGetProductRequest(String search_text) {

    // Tag used to cancel the request
    String tag_json_obj = "json_product_req";

    Map<String, String> params = new HashMap<String, String>();
    params.put("search", search_text);
    if(pagenumber.isEmpty())
    {
      product_modelList.clear();
      params.put("page", "1");
      loadingBar.show();
    }
    else
    {
      params.put("page", pagenumber);
      progress.setVisibility(View.VISIBLE);
    }

    CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
            BaseURL.URL_MASTER_SEARCH, params, new Response.Listener<JSONObject>() {

      @Override
      public void onResponse(JSONObject response) {
        try {
          if(pagenumber.isEmpty())
          {
            loadingBar.dismiss();
          }
          else
          {
            progress.setVisibility(View.GONE);
          }
//          if(response.has("data"))
//          {
//            img_no_products.setVisibility(View.GONE);
//            rv_search.setVisibility(View.VISIBLE);
//          }
//          else
//          {
//            img_no_products.setVisibility(View.VISIBLE);
//            rv_search.setVisibility(View.GONE);
//          }
          Boolean status = response.getBoolean("responce");
          if (status) {

            pagenumber=response.getString("page").toString();
            Log.e("pagegegnumber",""+pagenumber);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Product_model>>() {
            }.getType();

            ArrayList<Product_model> tempList=new ArrayList<>();
            tempList = gson.fromJson(response.getString("data"), listType);

            if(!(pagenumber.isEmpty()) && (tempList.size()!=0))
            {
              product_modelList.addAll(tempList);
              adapter_product.notifyDataSetChanged();
            }
            if (getActivity() != null) {
              if (product_modelList.isEmpty()) {
                Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
              }
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
  public void onResume() {
    super.onResume();
    // register reciver
    getActivity().registerReceiver(mCart, new IntentFilter("Grocery_cart"));
    getActivity().registerReceiver(mWish, new IntentFilter("Grocery_wish"));
  }
}
