package binplus.foodiswill.Fragment;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;

import binplus.foodiswill.Adapter.Wishlist_Adapter;
import binplus.foodiswill.MainActivity;
import binplus.foodiswill.R;
import binplus.foodiswill.util.DatabaseCartHandler;
import binplus.foodiswill.util.Session_management;
import binplus.foodiswill.util.WishlistHandler;

import static binplus.foodiswill.Config.BaseURL.KEY_ID;


/**
 * A simple {@link Fragment} subclass.
 */
public class Wishlist extends Fragment {
    private static String TAG = Shop_Now_fragment.class.getSimpleName();
    private Bundle savedInstanceState;
    private WishlistHandler db_wish;
    public static RecyclerView rv_wishlist;
    DatabaseCartHandler db_cart;
    Dialog loadingBar;
    String user_id ;
    Session_management sessionManagement ;

    public static RelativeLayout rel_no;

    public Wishlist() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_wishlist, container, false );
        setHasOptionsMenu( true );

        ((MainActivity) getActivity()).setTitle("My WishList" );
        rv_wishlist = view.findViewById( R.id.rv_wishlist );
        rel_no = view.findViewById( R.id.rel_no );
        rv_wishlist.setLayoutManager( new GridLayoutManager( getActivity(),2 ) );
        db_cart=new DatabaseCartHandler(getActivity());
        //db = new DatabaseHandler(getActivity());
        db_wish = new WishlistHandler( getActivity() );
        sessionManagement = new Session_management( getActivity() );
        user_id=sessionManagement.getUserDetails().get(KEY_ID);

        loadingBar=new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        loadingBar.setContentView( R.layout.progressbar );
        loadingBar.setCanceledOnTouchOutside(false);
        ArrayList<HashMap<String, String>> map = db_wish.getWishtableAll(user_id);
        if (map.isEmpty())
        {
            rel_no.setVisibility(View.VISIBLE);

        }
        else {
            rel_no.setVisibility(View.GONE);

        }

//        Log.d("cart all ",""+db_cart.getCartAll());

        Wishlist_Adapter adapter = new Wishlist_Adapter( map,getActivity() );
        rv_wishlist.setAdapter( adapter );
        adapter.notifyDataSetChanged();



        return view;
    }



    private void showClearDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( getActivity() );
        alertDialog.setMessage( getResources().getString( R.string.sure_del ) );
        alertDialog.setNegativeButton( getResources().getString( R.string.cancle ), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        } );
        alertDialog.setPositiveButton( getResources().getString( R.string.yes ), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // clear cart data
                db_wish.clearWishtable(user_id);
                ArrayList<HashMap<String, String>> map = db_wish.getWishtableAll(user_id);
                Wishlist_Adapter adapter = new Wishlist_Adapter(  map,getActivity() );
                rv_wishlist.setAdapter( adapter );
                adapter.notifyDataSetChanged();
                rel_no.setVisibility(View.VISIBLE);
                rv_wishlist.setVisibility(View.GONE);
                //updateData();

                dialogInterface.dismiss();
            }
        } );

        alertDialog.show();


    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setTitle("My WishList" );
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
        ((MainActivity) getActivity()).setWishCounter("" + db_wish.getWishtableCount(sessionManagement.getUserDetails().get(KEY_ID)));
    }




    @Override
    public void onPause() {
        super.onPause();
        // unregister reciver
        getActivity().unregisterReceiver(mCart);
        getActivity().unregisterReceiver(mWish);
    }

}

