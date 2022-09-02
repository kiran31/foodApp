package binplus.foodiswill.Fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import binplus.foodiswill.Adapter.Shop_Now_adapter;
import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.Model.ShopNow_model;
import binplus.foodiswill.AppController;
import binplus.foodiswill.MainActivity;
import binplus.foodiswill.R;
import binplus.foodiswill.util.ConnectivityReceiver;
import binplus.foodiswill.util.CustomVolleyJsonRequest;
import binplus.foodiswill.util.RecyclerTouchListener;
import binplus.foodiswill.util.Session_management;

import static binplus.foodiswill.Config.BaseURL.CITY_ID;


public class Shop_Now_fragment extends Fragment {
  Module module;
  private static String TAG = Shop_Now_fragment.class.getSimpleName();
  private RecyclerView rv_items;
  private List<ShopNow_model> category_modelList = new ArrayList<>();
  private List<ShopNow_model> catList = new ArrayList<>();
  private Shop_Now_adapter adapter;
  private boolean isSubcat = false;
  SwipeRefreshLayout swipe;
  boolean swipe_flag=false;
  String getid;
  String getcat_title;
  Dialog loadingBar;
  Session_management session_management;


  public Shop_Now_fragment() {

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
    View view = inflater.inflate(R.layout.fragment_shop_now, container, false);
    loadingBar=new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
    loadingBar.setContentView( R.layout.progressbar );
    loadingBar.setCanceledOnTouchOutside(false);
    session_management=new Session_management(getActivity());

    module=new Module(getActivity());
    setHasOptionsMenu(true);

    ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.shop_now));


    if (ConnectivityReceiver.isConnected()) {
      makeGetCategoryRequest();

    }
    swipe=view.findViewById(R.id.swipe);
    swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        swipe_flag=true;
        if (ConnectivityReceiver.isConnected()) {
          makeGetCategoryRequest();

        }

      }
    });

    rv_items = (RecyclerView) view.findViewById(R.id.rv_home);
    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
    rv_items.setLayoutManager(gridLayoutManager);
    // rv_items.addItemDecoration(new GridSpacingItemDecoration(10, dpToPx(-25), true));
//    rv_items.setItemAnimator(new DefaultItemAnimator());
    rv_items.setNestedScrollingEnabled(false);


//        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),4) {
//
//            @Override
//            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
//                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(getActivity()) {
//                    private static final float SPEED = 2000f;// Change this value (default=25f)
//
//                    @Override
//                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
//                        return SPEED / displayMetrics.densityDpi;
//                    }
//                };
//                smoothScroller.setTargetPosition(position);
//                startSmoothScroll(smoothScroller);
//            }
//        };
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        rv_headre_icons.setLayoutManager(layoutManager);
//        rv_headre_icons.setHasFixedSize(true);
//
//        rv_headre_icons.setDrawingCacheEnabled(true);
//        rv_headre_icons.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
//


    //Check Internet Connection


    rv_items.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_items, new RecyclerTouchListener.OnItemClickListener() {
      @Override
      public void onItemClick(View view, int position) {

        if(category_modelList.get(position).getStatus().equalsIgnoreCase("0"))
        {
          module.showToast(getResources().getString(R.string.undelirable));
        }
        else {
          getid = category_modelList.get(position).getId();
          getcat_title = category_modelList.get(position).getTitle();
          String parent = category_modelList.get(position).getCount();

          if (parent.equals("0")) {
            Bundle args = new Bundle();
            Fragment fm = new Product_fragment();
            args.putString("cat_id", getid);
            args.putString("title", getcat_title);
            session_management.setCategoryId(getid);
            // args.putString( "" );
            // Toast.makeText(getActivity(),""+getid,Toast.LENGTH_LONG).show();
            fm.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                    .addToBackStack(null).commit();

          } else {
            Bundle args = new Bundle();
            Fragment fm = new SubCategory_Fragment();
            args.putString("cat_id", getid);
            args.putString("title", getcat_title);
            // args.putString( "" );
            // Toast.makeText(getActivity(),""+getid,Toast.LENGTH_LONG).show();
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


    return view;
  }


  private void makeGetCategoryRequest() {
    loadingBar.show();
    String tag_json_obj = "json_category_req";

    isSubcat = false;
    catList.clear();
    Map<String, String> params = new HashMap<String, String>();
    params.put("parent", "");
    params.put("city_id", session_management.getSessionItem(CITY_ID));
    isSubcat = true;
    category_modelList.clear();
       /* if (parent_id != null && parent_id != "") {
        }*/

    CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
            BaseURL.GET_ALL_CATEGORY_URL, params, new Response.Listener<JSONObject>() {

      @Override
      public void onResponse(JSONObject response) {
        Log.d("shop_now", response.toString());
            if(swipe_flag){
              swipe.setRefreshing(false);
            }
        try {
          if (response != null && response.length() > 0) {
            Boolean status = response.getBoolean("responce");
            if (status) {
              Gson gson = new Gson();
              Type listType = new TypeToken<List<ShopNow_model>>() {
              }.getType();
              catList = gson.fromJson(response.getString("data"), listType);
              for(int i =0;i<catList.size();i++)
              {
//                if(catList.get(i).getStatus().equalsIgnoreCase("1"))
//                {
                  category_modelList.add(catList.get(i));
//                }

              }

              adapter = new Shop_Now_adapter(category_modelList);

              rv_items.setAdapter(adapter);
              adapter.notifyDataSetChanged();
              loadingBar.dismiss();
            }
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


  private int dpToPx(int dp) {
    Resources r = getResources();
    return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
  }


}
