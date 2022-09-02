package binplus.foodiswill;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.FirebaseApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import binplus.foodiswill.Config.Module;
import binplus.foodiswill.Fragment.*;
import binplus.foodiswill.Adapter.ExpandableListAdapter;
import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.SharedPref;
import binplus.foodiswill.Fragment.About_us_fragment;
import binplus.foodiswill.Fragment.Empty_cart_fragment;
import binplus.foodiswill.Fragment.Cart_fragment;
import binplus.foodiswill.Fragment.Edit_profile_fragment;
import binplus.foodiswill.Fragment.Shop_Now_fragment;
import binplus.foodiswill.Model.MenuModel;
import binplus.foodiswill.networkconnectivity.NetworkError;
import binplus.foodiswill.util.ConnectivityReceiver;
import binplus.foodiswill.util.CustomVolleyJsonArrayRequest;
import binplus.foodiswill.util.DatabaseCartHandler;
import binplus.foodiswill.util.Session_management;
import binplus.foodiswill.util.WishlistHandler;

import static binplus.foodiswill.Config.BaseURL.CITY_NAME;
import static binplus.foodiswill.Config.BaseURL.KEY_ID;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView totalBudgetCount, totalWishCount, totalBudgetCount3, tv_name, powerd_text;
    public static ImageView iv_profile;
    Module module;
    private WishlistHandler db_wish;
      RelativeLayout rel_home;
    private DatabaseCartHandler db_cart;
    String previouslyEncodedImage="";
    private Session_management sessionManagement;
    private Menu nav_menu;
    ImageView imageView;
    TextView mTitle;
    LinearLayout viewpa ,linear_profile;
    Toolbar toolbar;
    String language = "" ,share_link="" ,fb_link="" ,insta_link="" ,app_link ="";
    public static  String extra_charges="" , max_free_delivery_amt ="";
    RelativeLayout My_Order, My_Wishlist, My_Walllet, My_Cart;
    int padding = 0;
    LinearLayout linearLayout_login;
    ImageView open , close;
    private TextView txtRegId;
    NavigationView navigationView;
    LinearLayout Change_Store;
    String Store_Count;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Dialog loadingBar ;
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();
    public static String stop_order="",cod_status="",delivery_msg="",stop_order_image="",whtsapp_number="",phone_number="",contact_whtsapp="";


    BroadcastReceiver UpdateCart = null;

    @Override
    protected void onStart() {
        super.onStart();

    }

    Boolean UpdateCartRegistered = false;

    @Override
    protected void attachBaseContext(Context newBase) {


        newBase = LocaleHelper.onAttach(newBase);

        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        //      String token;
//        token = FirebaseInstanceId.getInstance().getToken();
        //    Log.d("MYTAG", "This is your Firebase token" + token);

        module = new Module(MainActivity.this);
        loadingBar=new Dialog(MainActivity.this,android.R.style.Theme_Translucent_NoTitleBar);
        loadingBar.setContentView( R.layout.progressbar );
        loadingBar.setCanceledOnTouchOutside(false);
        db_wish=new WishlistHandler(MainActivity.this);
        sharedPreferences = getSharedPreferences("lan", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
     rel_home=findViewById(R.id.rel_home);
        editor.putString("language", "english");
        SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
        editor.putBoolean("status",false);
        editor.apply();
        if (getIntent().getExtras() != null) {

            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);

                if (key.equals("MainActivity") && value.equals("True")) {
                    Intent intent = new Intent(this, Verfication_activity.class);
                    intent.putExtra("value", value);
                    startActivity(intent);
                    finish();
                }

            }
            subscribeToPushService();
        }





        Store_Count = SharedPref.getString(MainActivity.this, BaseURL.KEY_STORE_COUNT);
        rel_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//   new Module(MainActivity.this).rateApp();
//                Fragment fm = new HomeFragment();
                Fragment fm = new HomeFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
            }
        });

//

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setPadding(padding, toolbar.getPaddingTop(), padding, toolbar.getPaddingBottom());


        setSupportActionBar(toolbar);
//        getSupportActionBar().setIcon(R.drawable.icon);
        for (int i = 0; i < toolbar.getChildCount(); i++) {

            View view = toolbar.getChildAt(i);

            if (view instanceof TextView) {
                TextView textView = (TextView) view;

//                Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "font/bold.ttf");
//                textView.setTypeface(myCustomFont);
            }


        }
//        getSupportActionBar().setTitle(getResources().getString(R.string.name));
//        getSupportActionBar().setIcon(R.mipmap.ic_launcher_foreground);



//        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
//
//        }


        db_cart=new DatabaseCartHandler(this);

        checkConnection();

        sessionManagement = new Session_management(MainActivity.this);

        // expandableListView = findViewById(R.id.expandableListView);
        // prepareMenuData();
        // populateExpandableList();
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        View headerView = navigationView.getHeaderView(0);
//        navigationView.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);

        navigationView.setNavigationItemSelectedListener(this);
        nav_menu = navigationView.getMenu();

        View header = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        Change_Store = (LinearLayout) header.findViewById(R.id.change_store_btn);
        viewpa = (LinearLayout) header.findViewById(R.id.viewpa);
        if (sessionManagement.isLoggedIn()) {
            viewpa.setVisibility(View.VISIBLE);
        }


        if (Store_Count.equals("1")) {
            Change_Store.setVisibility(View.INVISIBLE);
        } else if (Store_Count.equals("2")) {
            Change_Store.setVisibility(View.VISIBLE);
            Change_Store.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, SelectStore.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
        }

        linear_profile =header.findViewById( R.id.linear_profile );

        iv_profile = (ImageView) header.findViewById(R.id.iv_header_img);
        String getimage=sessionManagement.getUpdateProfile().get(BaseURL.KEY_IMAGE);
        if (!TextUtils.isEmpty(getimage)) {


            Glide.with( MainActivity.this )
                    .load( BaseURL.IMG_PROFILE_URL + getimage)
                    .fitCenter()
                    .placeholder( R.drawable.user )
//                    .crossFade()
                    .diskCacheStrategy( DiskCacheStrategy.ALL )
                    .dontAnimate()
                    .into( iv_profile );
        }
        tv_name = (TextView) header.findViewById(R.id.tv_header_name);
        My_Order = (RelativeLayout) header.findViewById(R.id.my_orders);
        My_Wishlist = (RelativeLayout) header.findViewById(R.id.my_wishlist);
        //   My_Walllet = (RelativeLayout) header.findViewById(R.id.my_wallet);
        My_Cart = (RelativeLayout) header.findViewById(R.id.my_cart);
        open=header.findViewById( R.id.Save );
        close=header.findViewById( R.id.close );


        linearLayout_login=(LinearLayout)header.findViewById(R.id.linearLayout_login);


        My_Order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, My_Order_activity.class);
                startActivity(intent);

            }
        });
        My_Wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Fragment fm = new Wishlist();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();
            }
        });
//        My_Walllet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                binplus.Jabico.Fragment fm = new Wallet_fragment();
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                        .addToBackStack(null).commit();
//            }
//        });

        My_Cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (db_cart.getCartCount() > 0) {
                        Fragment fm = new Cart_fragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                .addToBackStack(null).commit();

                    } else {

                        Fragment fm = new Empty_cart_fragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                .addToBackStack(null).commit();
                    }

            }
        });

        linear_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Fragment fm = new Edit_profile_fragment();
                    Bundle bundle=new Bundle();
                    bundle.putString("image",previouslyEncodedImage);
                    fm.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();

            }
        });


        updateHeader();

        sideMenu();


        Log.e("asdasdasd","dfghjkdfghjk"+stop_order);
        if (savedInstanceState == null) {
//            if(stop_order.equalsIgnoreCase("0"))
//            {
//                Fragment fm = new Reward_fragment();
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.contentPanel, fm,"Reward_fragment")
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                        .commit();
//            }
//            else
//            {
//                Fragment fm = new HomeFragment();
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.contentPanel, fm, "Home_fragment")
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                        .commit();
//            }

        }
        getFragmentManager().

                addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        try {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            Fragment fr = getFragmentManager().findFragmentById(R.id.contentPanel);

                            final String fm_name = fr.getClass().getSimpleName();
                            Log.e("backstack: ", ": " + fm_name);
                            if (fm_name.contentEquals("HomeFragment")) {
                                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                                toggle.setDrawerIndicatorEnabled(true);
                                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                                toggle.syncState();

                            }
                            else if(fm_name.contentEquals("Reward_fragment")) {
                                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                                toggle.setDrawerIndicatorEnabled(true);
                                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                                toggle.syncState();
                            }
                            else if (fm_name.contentEquals("My_order_fragment") ||
                                    fm_name.contentEquals("Thanks_fragment")) {
                                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                                toggle.setDrawerIndicatorEnabled(false);
                                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                toggle.syncState();

                                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Fragment fm = new HomeFragment();
                                        FragmentManager fragmentManager = getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                                .addToBackStack(null).commit();
                                    }
                                });
                            } else {

                                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                                toggle.setDrawerIndicatorEnabled(false);
                                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                toggle.syncState();

                                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        onBackPressed();
                                    }
                                });
                            }

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });


//        if (sessionManagement.getUserDetails().
//                get(BaseURL.KEY_ID) != null && !sessionManagement.getUserDetails().
//                get(BaseURL.KEY_ID).equalsIgnoreCase())
//
//        {
//            MyFirebaseRegister fireReg = new MyFirebaseRegister(this);
//            fireReg.RegisterUser(sessionManagement.getUserDetails().get(BaseURL.KEY_ID));
//        }

    }


    public void updateHeader() {
        if (sessionManagement.isLoggedIn()) {
            viewpa.setVisibility(View.VISIBLE);
            String getname = sessionManagement.getUserDetails().get(BaseURL.KEY_NAME);
            String getimage = sessionManagement.getUserDetails().get(BaseURL.KEY_IMAGE);
            String getemail = sessionManagement.getUserDetails().get(BaseURL.KEY_EMAIL);
            SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
            previouslyEncodedImage = shre.getString("image_data", "");
            if (!previouslyEncodedImage.equalsIgnoreCase("")) {
                byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                iv_profile.setImageBitmap(bitmap);
            }

            if (!TextUtils.isEmpty(getimage)) {


                Glide.with( MainActivity.this )
                        .load( BaseURL.IMG_PROFILE_URL + getimage)
                        .fitCenter()
                        .placeholder( R.drawable.user )
//                        .crossFade()
                        .diskCacheStrategy( DiskCacheStrategy.ALL )
                        .dontAnimate()
                        .into( iv_profile );
            }
            tv_name.setText(getname+"\n\n City : "+sessionManagement.getSessionItem(CITY_NAME));

        }
        else
        {
            //linearLayout_login.setVisibility(View.GONE);
        }
    }


    private void applyFontToMenuItem(MenuItem mi) {
//        Typeface font = Typeface.createFromAsset(getAssets(), "font/bold.ttf" );
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        //   mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }


    public void sideMenu() {

        if (sessionManagement.isLoggedIn()) {
            //  tv_number.setVisibility(View.VISIBLE);
//            nav_menu.findItem(R.id.nav_logout).setVisible(false);
//            nav_menu.findItem(R.id.nav_powerd).setVisible(true);

//            nav_menu.findItem(R.id.nav_user).setVisible(true);
        }
    }

    public void setFinish() {
        finish();
    }

    public void setCartCounter(String totalitem) {
        try {
            totalBudgetCount.setText(totalitem);
        } catch (Exception e) {

        }
    }
    public void setWishCounter(String totalitem) {
        try {
            totalWishCount.setText(totalitem);
        } catch (Exception e) {

        }
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem cart_item = menu.findItem(R.id.action_cart);
        final MenuItem wish_item = menu.findItem(R.id.action_wishlist);
        final MenuItem search_item = menu.findItem(R.id.action_search);
        cart_item.setVisible(true);
        wish_item.setVisible(true);
        if(stop_order.equals("1"))
        {
            search_item.setVisible(true);
        }
        else if(stop_order.equals("0"))
        {
            search_item.setVisible(false);
        }

        View count = cart_item.getActionView();
        View count_wish = wish_item.getActionView();

        count_wish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.performIdentifierAction(wish_item.getItemId(), 0);
            }
        });
        count.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                menu.performIdentifierAction(cart_item.getItemId(), 0);

            }
        });


        totalBudgetCount = (TextView) count.findViewById(R.id.actionbar_notifcation_textview);
        totalBudgetCount.setText("" + db_cart.getCartCount());
        totalWishCount = (TextView) count_wish.findViewById(R.id.actionbar_wishlist_textview);
        totalWishCount.setText("" + db_wish.getWishtableCount(sessionManagement.getUserDetails().get(KEY_ID)));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
       /* if (id == R.id.action_language) {
            openLanguageDialog();
        }*/

        if (id == R.id.action_cart) {
            if(stop_order.toString().equalsIgnoreCase("1")) {

                if (db_cart.getCartCount() > 0) {
                    Fragment fm = new Cart_fragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();

                } else {
                    Fragment fm = new Empty_cart_fragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();

                }
            }
            return true;
        }
        else if(id == R.id.action_wishlist)
        {
            if(stop_order.toString().equalsIgnoreCase("1")) {

                Fragment fm = new Wishlist();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
            }
        }

        if(id== R.id.action_search)
        {
            if(stop_order.toString().equalsIgnoreCase("1")) {

                Fragment fm = new Search_fragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
            }

        }


        return super.onOptionsItemSelected(item);
    }

    private void openLanguageDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_language, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        TextView lEnglish = v.findViewById(R.id.l_english);
        TextView lSpanish = v.findViewById(R.id.l_arabic);
        final AlertDialog dialog = builder.create();

        lEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocaleHelper.setLocale(getApplication(), "en");
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                editor.putString("language", "english");
                editor.apply();


                recreate();
                dialog.dismiss();
            }
        });
        lSpanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocaleHelper.setLocale(getApplication(), "ar");
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("language", "spanish");
                editor.apply();


                recreate();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @SuppressLint("ResourceType")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fm = null;
        Bundle args = new Bundle();
        if (id == R.id.nav_shop_now) {
            fm = new Shop_Now_fragment();
        } else if (id == R.id.nav_my_profile) {
            fm = new Edit_profile_fragment();
        }
        /*else if(id == R.id.nav_my_wishlist)


        {
            fm =new Wishlist();
        }*/
        else if (id == R.id.nav_contactus) {
            fm = new Help_Fragment();


        }
        else if (id == R.id.nav_aboutus) {
            if (sessionManagement.isLoggedIn()) {
                toolbar.setTitle( "About" );
                fm = new About_us_fragment();
                args.putString( "url", BaseURL.GET_ABOUT_URL );
                args.putString( "title", getResources().getString( R.string.nav_about ) );
                fm.setArguments( args );
            }

        }
        else if (id == R.id.nav_fb) {
redirect(fb_link);
        }
        else if (id == R.id.nav_insta) {
            redirect(insta_link);
        }
        else if (id == R.id.nav_link) {
            redirect(share_link);
        }else if(id == R.id.nav_rate){
            new Module(MainActivity.this).rateApp();
        }
        else if (id == R.id.nav_share) {
            shareApp(app_link);
        } else if (id == R.id.nav_logout) {
            sessionManagement.logoutSession();
            viewpa.setVisibility(View.GONE);

            Intent intent=new Intent(MainActivity.this,SelectCity.class);
            intent.putExtra("from","splash");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } /*else if (id == R.id.nav_powerd) {
            // stripUnderlines(textView);
            String url = "http://sameciti.com";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            finish();
        }*/

        if (fm != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                    .addToBackStack(null).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        try {
            super.startActivityForResult(intent, requestCode);
        } catch (Exception ignored) {
        }

    }

    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    public void shareApp( String url) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi friends i am using ." + " http://play.google.com/store/apps/details?id=" + getPackageName() + " APP");
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    public void reviewOnApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }


    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;

        if (!isConnected) {
            Intent intent = new Intent(MainActivity.this, NetworkError.class);
            startActivity(intent);
        }
        else
        {

//            getLinks();
            Fragment fm = new HomeFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.contentPanel, fm, "HomeFragment").addToBackStack(null)
//                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
    }


    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(BaseURL.PREFS_NAME, 0);
        String regId = pref.getString("regId", null);
        Log.e(TAG, "Firebase reg id: " + regId);
        if (!TextUtils.isEmpty(regId)) {
            // txtRegId.setText("Firebase Reg Id: " + regId);
        } else {
            // txtRegId.setText("Firebase Reg Id is not received yet!");
        }
    }

    private void subscribeToPushService() {
//        FirebaseMessaging.getInstance().subscribeToTopic("news");
//
//        Log.d("Tecmanic", "Subscribed");
////        Toast.makeText(MainActivity.this, "Subscribed", Toast.LENGTH_SHORT).show();
//
//        String token = FirebaseInstanceId.getInstance().getToken();
//
//        // Log and toast
        //  Log.d("Tecmanic", token);
        //      Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        // register connection status listener
//        AppController.getInstance().setConnectivityListener(this);
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(BaseURL.REGISTRATION_COMPLETE));
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(BaseURL.PUSH_NOTIFICATION));
//        NotificationUtils.clearNotifications(getApplicationContext());
//    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // register reciver

    }



    public  void redirect(String s)
    {
        Intent viewIntent =
                new Intent("android.intent.action.VIEW",
                        Uri.parse(s));
        startActivity(viewIntent);
    }


    public void getLinks()
    {
        loadingBar.show();
        HashMap<String ,String> params = new HashMap<>();
        CustomVolleyJsonArrayRequest jsonRequest = new CustomVolleyJsonArrayRequest(Request.Method.POST,BaseURL.GET_LINKS, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                loadingBar.dismiss();
                try {
                    JSONObject object = response.getJSONObject(0);
                    share_link = object.getString("share_link");
                    fb_link = object.getString("fb_link");
                    max_free_delivery_amt= object.getString("max_amount");
                    app_link = object.getString("app_link");
                    insta_link = object.getString("insta_link");
                    extra_charges = module.checkNullNumber(object.getString("sanitization"));
                    stop_order = object.getString("stop_order");
                    cod_status = object.getString("cod");
                    delivery_msg = object.getString("delivery_msg");
                    stop_order_image = object.getString("stop_order_image");
                    whtsapp_number= object.getString("home_whatsapp");
                    phone_number = object.getString("home_call");
                    contact_whtsapp = object.getString("contact_whatsapp");
                    if(stop_order.equalsIgnoreCase("0"))
                    {
                        Fragment fm = new Reward_fragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.contentPanel, fm,"Reward_fragment")
//                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                        viewpa.setVisibility(View.GONE);
                        nav_menu.findItem(R.id.nav_shop_now).setVisible(false);
//
                    }
                    else if(stop_order.equalsIgnoreCase("1"))
                    {
                        Fragment fm = new HomeFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.contentPanel, fm, "HomeFragment")
//                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingBar.dismiss();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonRequest);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//    }
}




