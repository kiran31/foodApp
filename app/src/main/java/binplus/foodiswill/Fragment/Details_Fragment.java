package binplus.foodiswill.Fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.travijuu.numberpicker.library.NumberPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import binplus.foodiswill.Adapter.AllProductsAdapter;
import binplus.foodiswill.Adapter.ImageAdapter;
import binplus.foodiswill.Adapter.Produccts_images_adapter;
import binplus.foodiswill.Adapter.ProductVariantAdapter;
import binplus.foodiswill.Adapter.ProductAdapter;
import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.CustomSlider;
import binplus.foodiswill.Model.ProductVariantModel;
import binplus.foodiswill.AppController;
import binplus.foodiswill.MainActivity;
import binplus.foodiswill.Model.Product_model;
import binplus.foodiswill.R;
import binplus.foodiswill.util.ConnectivityReceiver;
import binplus.foodiswill.util.CustomVolleyJsonRequest;
import binplus.foodiswill.util.DatabaseCartHandler;
import binplus.foodiswill.util.Session_management;
import binplus.foodiswill.util.WishlistHandler;

import static binplus.foodiswill.Config.BaseURL.IMG_PRODUCT_URL;
import static binplus.foodiswill.Config.BaseURL.KEY_ID;


public class Details_Fragment extends Fragment {
    String atr_id="";
    String atr_product_id="";
    String attribute_name="";
    String attribute_value="";
    String attribute_mrp="";
    int list_position = -1;
    Session_management session_management;
    Context context;
    Button btn_add;
    Module module;
    String mVerificationId;
    ArrayList<ProductVariantModel> variantList;
    ProductVariantAdapter productVariantAdapter;

    String user_id;
    Session_management sessionManagement ;

    private static String TAG = Details_Fragment.class.getSimpleName();
    private RecyclerView rv_cat;
    int index;
    double tot_amt=0;
    Dialog loadingBar;
    double tot=0;

    RelativeLayout rel_variant,lin_img;
   ArrayList<Product_model> product_modelList = new ArrayList<>();
    private AllProductsAdapter adapter_product;
    Activity activity;
    Button btn_add_to_cart;
    DatabaseCartHandler db_cart;
    WishlistHandler db_wish ;
    //TextView txtColor,txtSize;
    TextView txtPer;
    Button btn_buy_now;
    int status=0;
    SliderLayout img_slider ;
    private TextView dialog_unit_type,dialog_txtId,dialog_txtVar;
    String color ,size ;
    Dialog dialog;

    public static ProgressBar progressBar,pgb,pbg1;
    RelativeLayout relativeLayout_spinner,relativeLayout_size,relativeLayout_color;
    Produccts_images_adapter imagesAdapter;
    String cat_id,product_id,product_images,details_product_name,details_product_name_hindi,details_product_desc,details_product_inStock,details_product_attribute;
    String details_product_price,details_product_mrp,details_product_unit_value,details_product_unit,details_product_rewards,details_product_increament,details_product_title;
    String details_stock,details_product_color;
    String details_product_cart_id,details_product_name_arb,details_product_description_arb,details_product_deal_price,details_product_start_date, details_product_start_time,details_product_end_date, details_product_end_time,  details_product_status, details_product_size,details_product_city;
    public static ImageView btn,img2;
    private TextView txtrate,txtTotal,txtBack;
    ImageView wish_before ,wish_after,iv_dis ;
    RelativeLayout rel_out ;
    ListView listView,listView1;
//    ArrayList<String> list;
//    ArrayList<String> list_id;
//    ArrayList<String> list_atr_value;
//    ArrayList<String> list_atr_name;
//    ArrayList<String> list_atr_mrp;


    // ListView listView;
   public static ArrayList<String> image_list;

    private TextView txtName,txtPrice,txtMrp;
    ReadMoreTextView txtDesc;
    //Spinner spinner_size,spinner_color;
    RecyclerView recyclerView;
    CardView cardView;
    ImageAdapter pagerAdapter ;


    private NumberPicker numberButton;


    public Details_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.detals_fragmnet, container, false);

        sessionManagement = new Session_management(getActivity());

        user_id=sessionManagement.getUserDetails().get(KEY_ID);

        sessionManagement.cleardatetime();
        loadingBar=new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        loadingBar.setContentView( R.layout.progressbar );
        loadingBar.setCanceledOnTouchOutside(false);

        rv_cat = (RecyclerView) view.findViewById(R.id.top_selling_recycler);
        // gifImageView=(ImageView) view.findViewById(R.id.gifImageView);
//        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        rv_cat.setLayoutManager(new GridLayoutManager(getActivity(),2));
        rv_cat.setNestedScrollingEnabled(false);
        module=new Module(getActivity());
        db_cart=new DatabaseCartHandler(getActivity());
        db_wish=new WishlistHandler( getActivity() );
        Bundle bundle=getArguments();
        variantList=new ArrayList<>();

        cat_id=bundle.getString("cat_id");
        product_id=bundle.getString("product_id");
        product_images=bundle.getString("product_image");
        details_product_name=bundle.getString("product_name");
        details_product_desc=bundle.getString("product_description");
        //     details_product_color=bundle.getString("product_color");
        details_product_inStock=bundle.getString("in_stock");
        details_stock=bundle.getString("stock");
        details_product_attribute=bundle.getString("product_attribute");
        details_product_name_hindi=bundle.getString("product_name_hindi");
//       Log.e("product_name_hindiwssss",""+details_product_name_hindi);
        //   details_product_size=bundle.getString("product_size");
        details_product_price=bundle.getString("price");
        details_product_mrp=bundle.getString("mrp");
        details_product_unit_value=bundle.getString("unit_value");
        details_product_unit=bundle.getString("unit");
        details_product_rewards=bundle.getString("rewards");
        details_product_increament=bundle.getString("increment");
        details_product_title=bundle.getString("title");

         details_product_cart_id = bundle.getString("cart_id");
        details_product_name_arb = bundle.getString("product_name_arb");
        details_product_description_arb = bundle.getString("product_description_arb");
        details_product_deal_price = bundle.getString("deal_price");
        details_product_start_date = bundle.getString("start_date");
        details_product_start_time = bundle.getString("start_time");
        details_product_end_date = bundle.getString("end_date");
        details_product_end_time = bundle.getString("end_time");
        details_product_status = bundle.getString("status");
        details_product_size = bundle.getString("size");
        details_product_color= bundle.getString("color");
        details_product_city = bundle.getString("city");


        ((MainActivity) getActivity()).setTitle(details_product_name);
//         list=new ArrayList<String>();
//         list_atr_name=new ArrayList<String>();
//         list_id=new ArrayList<String>();
//         list_atr_value=new ArrayList<String>();
//         list_atr_mrp=new ArrayList<String>();
        btn_add=(Button)view.findViewById(R.id.btn_add);
        dialog_unit_type=(TextView)view.findViewById(R.id.unit_type);
        dialog_txtId=(TextView)view.findViewById(R.id.txtId);
        dialog_txtVar=(TextView)view.findViewById(R.id.txtVar);
        btn_add_to_cart=(Button)view.findViewById(R.id.btn_f_Add_to_cart);
        btn_add_to_cart.setVisibility(View.GONE);
        // cardView=(CardView)view.findViewById(R.id.card_view2);
        txtPer=(TextView)view.findViewById(R.id.details_product_per);

        rel_variant=(RelativeLayout)view.findViewById(R.id.rel_variant);
      btn=(ImageView)view.findViewById(R.id.img_product);
        iv_dis=(ImageView)view.findViewById(R.id.iv_dis);
        recyclerView=view.findViewById(R.id.recylerView);
        //   listView=findViewById(R.id.lstView);
//        txtBack=(TextView)view.findViewById(R.id.txtBack);

        wish_before = view.findViewById( R.id.wish_before );
        wish_after = view.findViewById( R.id.wish_after );
        rel_out= view.findViewById( R.id.rel_out );

        lin_img = view.findViewById(R.id.relative_layout_img);
        img_slider = view.findViewById(R.id.img_slider);
        img_slider.getPagerIndicator().setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);
        img_slider.getPagerIndicator().setDefaultIndicatorColor(getActivity().getResources().getColor(R.color.colorPrimary),getActivity().getResources().getColor( R.color.all));
        image_list=new ArrayList<String>();
//        progressBar=(ProgressBar)view.findViewById(R.id.progress_bar);

        //txtColor=(TextView)view.findViewById(R.id.txtColor);
        //txtSize=(TextView)view.findViewById(R.id.txtSize);
//    Glide.with(this).load(R.raw.basicloader).into(btn);
        txtName=(TextView)view.findViewById(R.id.details_product_name);
        txtDesc=(ReadMoreTextView)view.findViewById(R.id.details_product_description);
        txtPrice=(TextView)view.findViewById(R.id.details_product_price);
        txtMrp=(TextView)view.findViewById(R.id.details_product_mrp);
        txtMrp.setPaintFlags(txtMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        //  btn_add_to_cart=(Button)view.findViewById(R.id.btnAdd_to_cart);


        txtrate=(TextView)view.findViewById(R.id.product_rate);
        txtTotal=(TextView)view.findViewById(R.id.product_total);
        numberButton=view.findViewById(R.id.product_qty);
        txtDesc.setText(details_product_desc);
        float mrp=Float.parseFloat(details_product_mrp);
        final float price=Float.parseFloat(details_product_price);
        float per=((mrp-price)/mrp)*100;
        int df=Math.round(per);

        if(details_product_name_hindi.isEmpty())
        {
            txtName.setText(details_product_name);
        }
        else
        {
            txtName.setText(details_product_name+"\n( "+details_product_name_hindi+" )");
        }

        final float stock = Float.parseFloat( details_stock );
        numberButton.setMin(1);
        numberButton.setMax((int) (stock));
        if (stock<1 || details_product_inStock.equals("0"))
        {
            rel_out.setVisibility( View.VISIBLE );
        }
        else
        {
            rel_out.setVisibility( View.GONE );
        }

        if(db_wish.isInWishtable( product_id ,user_id ))
        {
            wish_after.setVisibility( View.VISIBLE );
            wish_before.setVisibility( View.GONE );
        }
        else
        {
            wish_after.setVisibility( View.GONE );
            wish_before.setVisibility( View.VISIBLE );
        }


        String atr=String.valueOf(details_product_attribute);
        if(atr.equals("[]"))
        {
            if(df<=0)
            {
                txtPer.setVisibility(View.GONE);
                iv_dis.setVisibility(View.GONE);
            }
            else
            {
                txtPer.setVisibility(View.VISIBLE);
                iv_dis.setVisibility(View.VISIBLE);
                txtPer.setText(String.valueOf(df)+"% "+"\n off");

            }

            status=1;
            txtPrice.setText("\u20B9"+details_product_price);
            txtMrp.setText("\u20B9"+details_product_mrp);

            txtrate.setVisibility(View.VISIBLE);
            //  Toast.makeText(getActivity(),""+atr,Toast.LENGTH_LONG).show();
            txtrate.setText(details_product_unit_value+details_product_unit);
            txtTotal.setText("\u20B9"+String.valueOf(db_cart.getTotalAmount()));
        }

        else
        {

            rel_variant.setVisibility(View.VISIBLE);
            status=2;
            JSONArray jsonArr = null;
            try {

                jsonArr = new JSONArray(atr);

                ProductVariantModel model=new ProductVariantModel();
                JSONObject jsonObj = jsonArr.getJSONObject(0);
                atr_id=jsonObj.getString("id");
//                product_id=jsonObj.getString("product_id");
                attribute_name=jsonObj.getString("attribute_name");
                attribute_value=jsonObj.getString("attribute_value");
                attribute_mrp=jsonObj.getString("attribute_mrp");

                model.setDeal_price(jsonObj.getString("deal_price"));
                model.setDeal_qty(jsonObj.getString("deal_qty"));
                model.setIs_deal(jsonObj.getString("is_deal"));



                //     arrayList.add(new AttributeModel(atr_id,product_id,attribute_name,attribute_value));

                //Toast.makeText(getActivity(),"id "+atr_id+"\n p_id "+product_id+"\n atr_name "+attribute_name+"\n atr_value "+attribute_value,Toast.LENGTH_LONG).show();


                String atr_price=String.valueOf(attribute_value);
                String atr_mrp=String.valueOf(attribute_mrp);
                int atr_dis=getDiscount(atr_price,atr_mrp);
                txtPrice.setText("\u20B9"+attribute_value.toString());
                txtMrp.setText("\u20B9"+attribute_mrp.toString());
                dialog_txtId.setText(String.valueOf(atr_id.toString()+"@"+"0"));
                dialog_txtVar.setText(attribute_value+"@"+attribute_name+"@"+attribute_mrp);
                dialog_unit_type.setText("\u20B9"+attribute_value+"/"+attribute_name);
                //  holder.txtTotal.setText("\u20B9"+String.valueOf(list_atr_value.get(0).toString()));
                if(atr_dis<=0)
                {
                    txtPer.setVisibility(View.GONE);
                    iv_dis.setVisibility(View.GONE);
                }
                else
                {
                    txtPer.setVisibility(View.VISIBLE);
                    iv_dis.setVisibility(View.VISIBLE);

                    txtPer.setText(""+atr_dis+"%"+"\n OFF");


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }



        }


        rel_variant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //      final Product_model mList = modelList.get(position);
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                LayoutInflater layoutInflater=(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View row=layoutInflater.inflate(R.layout.dialog_vairant_layout,null);
                variantList.clear();
                String atr=String.valueOf(details_product_attribute);
                JSONArray jsonArr = null;
                try {

                    jsonArr = new JSONArray(atr);
                    for (int i = 0; i < jsonArr.length(); i++)
                    {
                        ProductVariantModel model=new ProductVariantModel();
                        JSONObject jsonObj = jsonArr.getJSONObject(i);
                        String atr_id=jsonObj.getString("id");
//                        String atr_product_id=jsonObj.getString("product_id");
                        String attribute_name=jsonObj.getString("attribute_name");
                        String attribute_value=jsonObj.getString("attribute_value");
                        String attribute_mrp=jsonObj.getString("attribute_mrp");


                        model.setDeal_price(jsonObj.getString("deal_price"));
                        model.setDeal_qty(jsonObj.getString("deal_qty"));
                        model.setIs_deal(jsonObj.getString("is_deal"));
                        model.setId(atr_id);
//                        model.setProduct_id(atr_product_id);
                        model.setAttribute_value(attribute_value);
                        model.setAttribute_name(attribute_name);
                        model.setAttribute_mrp(attribute_mrp);

                        variantList.add(model);

                        //     arrayList.add(new AttributeModel(atr_id,product_id,attribute_name,attribute_value));

                        //Toast.makeText(getActivity(),"id "+atr_id+"\n p_id "+product_id+"\n atr_name "+attribute_name+"\n atr_value "+attribute_value,Toast.LENGTH_LONG).show();
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ListView l1=(ListView)row.findViewById(R.id.list_view_varaint);
                productVariantAdapter=new ProductVariantAdapter(getActivity(),variantList);
                //productVariantAdapter.notifyDataSetChanged();
                l1.setAdapter(productVariantAdapter);


                builder.setView(row);
                final AlertDialog ddlg=builder.create();
                ddlg.show();
                l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        list_position = i;
                        atr_id=String.valueOf(variantList.get(i).getId());
//                        atr_product_id=String.valueOf(variantList.get(i).getProduct_id());
                        attribute_name=String.valueOf(variantList.get(i).getAttribute_name());
                        attribute_value=String.valueOf(variantList.get(i).getAttribute_value());
                        attribute_mrp=String.valueOf(variantList.get(i).getAttribute_mrp());

                        dialog_unit_type.setText("\u20B9"+attribute_value+"/"+attribute_name);
                        dialog_txtId.setText(variantList.get(i).getId()+"@"+i);
                        dialog_txtVar.setText(attribute_value+"@"+attribute_name+"@"+attribute_mrp);
                        //    txtPer.setText(String.valueOf(df)+"% off");

                        txtPrice.setText("\u20B9"+attribute_value.toString());
                        txtMrp.setText("\u20B9"+attribute_mrp.toString());
                        String pr=String.valueOf(attribute_value);
                        String mr=String.valueOf(attribute_mrp);
                        int atr_dis=getDiscount(pr,mr);
                        txtPer.setText(""+atr_dis+"%"+"\n OFF");
                        String atr=String.valueOf(details_product_attribute);
                        if(atr.equals("[]"))
                        {
                            boolean st=db_cart.isInCart(product_id);
                            if(st==true)
                            {
//                                btn_add.setVisibility(View.GONE);
//                                numberButton.setNumber(db_cart.getCartItemQty(product_id));
                                btn_add.setText("Update Cart");
                                numberButton.setValue(Integer.parseInt(db_cart.getCartItemQty(product_id)));
//                                numberButton.setVisibility(View.VISIBLE);
                            }
                        }
                        else
                        {
                            String str_id=dialog_txtId.getText().toString();
                            String[] str=str_id.split("@");
                            String at_id=String.valueOf(str[0]);
                            boolean st=db_cart.isInCart(at_id);
                            if(st==true)
                            {
//                                btn_add.setVisibility(View.GONE);
//                                numberButton.setNumber(db_cart.getCartItemQty(at_id));
                                btn_add.setText("Update Cart");
                                numberButton.setValue(Integer.parseInt(db_cart.getCartItemQty(at_id)));
//                                numberButton.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                btn_add.setText("Add to Cart");
//                                btn_add.setVisibility(View.VISIBLE);
//
//                                numberButton.setVisibility(View.GONE);
                            }
                        }

//    numberButton.setNumber(db_cart.getCartItemQty( product_id ));

                        txtTotal.setText("\u20B9"+String.valueOf(db_cart.getTotalAmount()));
                        ddlg.dismiss();
                    }
                });

            }
        });
        //Toast.makeText(getActivity(),"tot_amount"+atr.toString(),Toast.LENGTH_LONG).show();


        wish_before.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        wish_after.setVisibility( View.VISIBLE );
                        wish_before.setVisibility( View.INVISIBLE );


                    module.addToWishlist(getActivity(),product_id,product_images,cat_id,details_product_name,details_product_price,details_product_desc,details_product_rewards,details_product_unit_value,details_product_unit,details_product_increament,
                            details_stock,details_product_inStock,details_product_title,details_product_mrp,details_product_attribute,user_id,details_product_name_hindi,details_product_name_arb,details_product_description_arb,
                            details_product_deal_price,details_product_start_date,details_product_start_time,details_product_end_date,details_product_end_time
                            ,details_product_status,details_product_size,details_product_color,details_product_city);

//                module.addToWishlist(activity,mList.getProduct_id(),mList.getProduct_image(),mList.getCategory_id(),mList.getProduct_name(),module.checkNullNumber(mList.getPrice()),
//                        mList.getProduct_description(),mList.getRewards(),module.checkNullNumber(mList.getUnit_value()),mList.getUnit(),mList.getIncreament(),module.checkNullNumber(mList.getStock()),
//                        mList.getIn_stock(),mList.getTitle(),module.checkNullNumber(mList.getMrp()),mList.getProduct_attribute(),sessionManagement.getSessionItem(KEY_ID),mList.getProduct_name_hindi(),
//                        module.checkNullString(mList.getProduct_name_arb()),module.checkNullString(mList.getProduct_description_arb()),module.checkNullString(mList.getDeal_price()),
//                        module.checkNullString(mList.getStart_date()),module.checkNullString(mList.getStart_time()),module.checkNullString(mList.getEnd_date()),module.checkNullString(mList.getEnd_time()),
//                        module.checkNullString(mList.getStatus()), module.checkNullString(mList.getSize()),module.checkNullString(mList.getColor()),module.checkNullString(mList.getCity()));
                    }


        } );
        wish_after.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wish_after.setVisibility( View.INVISIBLE );
                wish_before.setVisibility( View.VISIBLE );
                db_wish.removeItemFromWishtable(product_id,user_id);
                module.updatewishintent();
                Toast.makeText(getActivity(), "removed from Wishlist", Toast.LENGTH_LONG).show();
                // list.remove(position);
                // notifyDataSetChanged();
            }
        } );

//




        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stock < 1 || details_product_inStock.equals("0")) {
                    Toast.makeText(getActivity(), "Out Of Stock", Toast.LENGTH_LONG).show();
                } else {

                    String tot1 = txtTotal.getText().toString().trim();
                    String tot_amount = tot1.substring(1, tot1.length());
                    float qty =numberButton.getValue();

                    String atr = String.valueOf(details_product_attribute);
                    if (atr.equals("[]")) {

                            HashMap<String, String> mapProduct = new HashMap<String, String>();
                            String unt = String.valueOf( details_product_unit_value + " " + details_product_unit );

                        double pr = Double.parseDouble( details_product_price );
                            double qr=(double)qty;
                        double amt = pr * qty;

                        mapProduct.put( "cart_id", product_id );
                        mapProduct.put( "product_id", product_id );
                        mapProduct.put( "product_name", details_product_name );
                        mapProduct.put( "product_name_hindi", details_product_name_hindi );
                        mapProduct.put( "product_name_arb", details_product_name_arb );
                        mapProduct.put( "product_description_arb",details_product_description_arb );
//                        mapProduct.put( "category_id", mList.getCategory_id() );
                        mapProduct.put( "product_description", details_product_desc );
                        mapProduct.put( "deal_price", details_product_deal_price );
                        mapProduct.put( "start_date", details_product_start_date );
                        mapProduct.put( "start_time", details_product_start_time );
                        mapProduct.put( "end_date", details_product_end_date );
                        mapProduct.put( "end_time", details_product_end_time );
                        mapProduct.put( "price", String.valueOf(amt) );
                        mapProduct.put( "mrp", details_product_mrp );
                        mapProduct.put( "product_image", product_images );
                        mapProduct.put( "status", details_product_status );
                        mapProduct.put( "in_stock", details_product_inStock );
                        mapProduct.put( "unit_value", details_product_price );
                        mapProduct.put( "unit", unt);
                        mapProduct.put( "increament", details_product_increament );
                        mapProduct.put( "rewards", details_product_rewards );
                        mapProduct.put( "stock", details_stock );
                        mapProduct.put( "size", details_product_size );
                        mapProduct.put( "color", details_product_color );
                        mapProduct.put( "city",details_product_city );
                        mapProduct.put( "title", details_product_title );
                        mapProduct.put( "cat_id", cat_id );
                        mapProduct.put("type", "p");

                            try {

                                boolean tr = db_cart.setCart( mapProduct, qty );
                                if (tr == true) {
                                    MainActivity mainActivity = new MainActivity();
                                    mainActivity.setCartCounter( "" + db_cart.getCartCount() );

                                    //   context.setCartCounter("" + holder.db_cart.getCartCount());
                                    Toast.makeText( getActivity(), "Added to Cart", Toast.LENGTH_LONG ).show();
                                    int n = db_cart.getCartCount();
                                    updateintent();
                                    txtTotal.setText( "\u20B9" + String.valueOf( db_cart.getTotalAmount() ) );

                                } else if (tr == false) {
                                    Toast.makeText( getActivity(), "cart updated", Toast.LENGTH_LONG ).show();
                                    txtTotal.setText( "\u20B9" + String.valueOf( db_cart.getTotalAmount() ) );
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                // Toast.makeText(getActivity(), "" + ex.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            //Toast.makeText(context,"1\n"+status+"\n"+modelList.get(position).getProduct_attribute(),Toast.LENGTH_LONG).show();
                        } else {
                            //ProductVariantModel model=variantList.get(position);

                            String str_id = dialog_txtId.getText().toString();
                            String s = dialog_txtVar.getText().toString();
                            String[] st = s.split( "@" );
                            String st0 = String.valueOf( st[0] );
                            String st1 = String.valueOf( st[1] );
                            String st2 = String.valueOf( st[2] );
                            String[] str = str_id.split( "@" );
                            String at_id = String.valueOf( str[0] );
                        double pr = Double.parseDouble( st0 );
                        double qr=(double)qty;
                        double amt = pr * qty;
                            int j = Integer.parseInt( String.valueOf( str[1] ) );
                            //       Toast.makeText(context,""+str[0].toString()+"\n"+str[1].toString(),Toast.LENGTH_LONG).show();
                            HashMap<String, String> mapProduct = new HashMap<String, String>();

                        mapProduct.put( "cart_id", at_id );
                        mapProduct.put( "product_id", product_id );
                        mapProduct.put( "product_name", module.checkNullString(details_product_name) );
                        mapProduct.put( "product_name_hindi", module.checkNullString(details_product_name_hindi) );
                        mapProduct.put( "product_name_arb", module.checkNullString(details_product_name_arb) );
                        mapProduct.put( "product_description_arb",module.checkNullString(details_product_description_arb) );
//                        mapProduct.put( "category_id", mList.getCategory_id() );
                        mapProduct.put( "product_description", module.checkNullString(details_product_desc) );
                        mapProduct.put( "deal_price", module.checkNullString(details_product_deal_price) );
                        mapProduct.put( "start_date", module.checkNullString(details_product_start_date) );
                        mapProduct.put( "start_time", module.checkNullString(details_product_start_time) );
                        mapProduct.put( "end_date", module.checkNullString(details_product_end_date) );
                        mapProduct.put( "end_time", module.checkNullString(details_product_end_time) );
                        mapProduct.put( "price", module.checkNullString(String.valueOf(amt)) );
                        mapProduct.put( "mrp", module.checkNullString(st2) );
                        mapProduct.put( "product_image", module.checkNullString(product_images) );
                        mapProduct.put( "status", module.checkNullString(details_product_status) );
                        mapProduct.put( "in_stock", module.checkNullString(details_product_inStock) );
                        mapProduct.put( "unit_value", module.checkNullString(st0) );
                        mapProduct.put( "unit", module.checkNullString(st1));
                        mapProduct.put( "increament", module.checkNullString(details_product_increament) );
                        mapProduct.put( "rewards", module.checkNullString(details_product_rewards) );
                        mapProduct.put( "stock", module.checkNullString(details_stock) );
                        mapProduct.put( "size", module.checkNullString(details_product_size) );
                        mapProduct.put( "color", module.checkNullString(details_product_color) );
                        mapProduct.put( "city",module.checkNullString(details_product_city) );
                        mapProduct.put( "title", module.checkNullString(details_product_title) );
                        mapProduct.put( "cat_id", module.checkNullString(cat_id) );
                        mapProduct.put("type", "a");



                        //  Toast.makeText(context,""+attributeList.get(j).getId()+"\n"+mapProduct,Toast.LENGTH_LONG).show();
                            try {

                                boolean tr = db_cart.setCart( mapProduct, qty );
                                if (tr == true) {
                                    MainActivity mainActivity = new MainActivity();
                                    mainActivity.setCartCounter( "" + db_cart.getCartCount() );

                                    //   context.setCartCounter("" + holder.db_cart.getCartCount());
                                    Toast.makeText( getActivity(), "Added to Cart", Toast.LENGTH_LONG ).show();
                                    int n = db_cart.getCartCount();
                                    updateintent();

                                    txtTotal.setText( "\u20B9" + String.valueOf( db_cart.getTotalAmount() ) );
                                } else if (tr == false) {
                                    Toast.makeText( getActivity(), "cart updated", Toast.LENGTH_LONG ).show();
                                    txtTotal.setText( "\u20B9" + String.valueOf( db_cart.getTotalAmount() ) );
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Toast.makeText(activity, "" + ex.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                        updateData();
                    btn_add.setText("Update Cart");
//                    btn_add.setVisibility( View.GONE );
//                    numberButton.setNumber( "1" );
//                    numberButton.setVisibility( View.VISIBLE );

                    }
//

                txtTotal.setText("\u20B9"+String.valueOf(db_cart.getTotalAmount()));
            }
        });


//     numberButton.setValueChangedListener(new ValueChangedListener() {
//         @Override
//         public void valueChanged(int value, ActionEnum action) {
//             if (value > stock) {
//                 numberButton.setValue(Integer.parseInt(String.valueOf((int)stock)));
//                 Toast.makeText(getActivity(),"not Available",Toast.LENGTH_LONG).show();
//             } else {
//
//
//                 float qty = Float.parseFloat(String.valueOf(numberButton.getValue()));
//
//                 String atr = String.valueOf(details_product_attribute);
//                 if (value <= 0) {
//                     boolean st = checkAttributeStatus(atr);
//                     if (st == false) {
//                         db_cart.removeItemFromCart(product_id);
//
//
//                         String str_id = dialog_txtId.getText().toString();
//                         String[] str = str_id.split("@");
//                         String at_id = String.valueOf(str[0]);
//                         db_cart.removeItemFromCart(at_id);
//                     }
//
//                     numberButton.setVisibility(View.GONE);
//                     btn_add.setVisibility(View.VISIBLE);
//                     updateintent();
//                 }
////                else if (newValue> stock)
////                {
////                   // int ss = Integer.parseInt( mList.getStock());
////                    Toast.makeText(context,"Only " +stock+ " in Stock",Toast.LENGTH_LONG ).show();
////                    // holder.elegantNumberButton.setNumber( String.valueOf(oldValue) );
////                }
//                 else {
//
//
//                     if (atr.equals("[]")) {
//                         double pr = Double.parseDouble(details_product_price);
//                         double amt = pr * qty;
//                         HashMap<String, String> mapProduct = new HashMap<String, String>();
//                         String unt = String.valueOf(details_product_unit_value + " " + details_product_unit);
//                         mapProduct.put("cart_id", product_id);
//                         mapProduct.put("product_id", product_id);
//                         mapProduct.put("product_image", product_images);
//                         mapProduct.put("cat_id", cat_id);
//                         mapProduct.put("product_name", details_product_name);
//                         mapProduct.put("price", String.valueOf(amt));
//                         mapProduct.put("unit_price", details_product_price);
//                         mapProduct.put("stock", details_stock);
//                         mapProduct.put("unit", unt);
//                         mapProduct.put("mrp", details_product_mrp);
//                         mapProduct.put("type", "p");
//                         try {
//
//                             boolean tr = db_cart.setCart(mapProduct, qty);
//                             if (tr == true) {
//                                 MainActivity mainActivity = new MainActivity();
//                                 mainActivity.setCartCounter("" + db_cart.getCartCount());
//
//                                 //   context.setCartCounter("" + holder.db_cart.getCartCount());
//                                 Toast.makeText(getActivity(), "Added to Cart", Toast.LENGTH_LONG).show();
//                                 int n = db_cart.getCartCount();
//                                 updateintent();
//
//                tot=price*number;
//
//                             } else if (tr == false) {
//                                 Toast.makeText(getActivity(), "cart updated", Toast.LENGTH_LONG).show();
//                             }
//
//                         } catch (Exception ex) {
//                             ex.printStackTrace();
//                             //Toast.makeText(getActivity(), "" + ex.getMessage(), Toast.LENGTH_LONG).show();
//                         }
//
//                         //Toast.makeText(context,"1\n"+status+"\n"+modelList.get(position).getProduct_attribute(),Toast.LENGTH_LONG).show();
//                     } else {
//                         //ProductVariantModel model=variantList.get(position);
//
//
//                         String s = dialog_txtVar.getText().toString();
//                         String[] st = s.split("@");
//                         String st0 = String.valueOf(st[0]);
//                         String st1 = String.valueOf(st[1]);
//                         String st2 = String.valueOf(st[2]);
//                         String str_id = dialog_txtId.getText().toString();
//                         String[] str = str_id.split("@");
//                         String at_id = String.valueOf(str[0]);
//                         int k = Integer.parseInt(String.valueOf(str[1]));
//                         double pr = Double.parseDouble(st0);
//
//            listView1 = (ListView) dialog.findViewById( R.id.list_view_color );
//            pbg1 = (ProgressBar) dialog.findViewById( R.id.pgbr );
//            //List<String> lis=new ArrayList<>();
//            final List lis = makeGetProductColorRequest( cat_id, product_id, listView1, pbg1, dialog );
//
//                         double amt = pr * qty;
//                         //       Toast.makeText(context,""+str[0].toString()+"\n"+str[1].toString(),Toast.LENGTH_LONG).show();
//                         HashMap<String, String> mapProduct = new HashMap<String, String>();
//                         mapProduct.put("cart_id", at_id);
//                         mapProduct.put("product_id", product_id);
//                         mapProduct.put("product_image", product_images);
//                         mapProduct.put("cat_id", cat_id);
//                         mapProduct.put("product_name", details_product_name);
//                         mapProduct.put("price", String.valueOf(amt));
//                         mapProduct.put("stock", details_stock);
//                         mapProduct.put("unit_price", st0);
//                         mapProduct.put("unit", st1);
//                         mapProduct.put("mrp", st2);
//                         mapProduct.put("type", "a");
//                         //  Toast.makeText(context,""+attributeList.get(j).getId()+"\n"+mapProduct,Toast.LENGTH_LONG).show();
//                         try {
//
//                             boolean tr = db_cart.setCart(mapProduct, qty);
//                             if (tr == true) {
//                                 MainActivity mainActivity = new MainActivity();
//                                 mainActivity.setCartCounter("" + db_cart.getCartCount());
//
//                                 //   context.setCartCounter("" + holder.db_cart.getCartCount());
//                                 Toast.makeText(getActivity(), "Added to Cart", Toast.LENGTH_LONG).show();
//                                 int n = db_cart.getCartCount();
//                                 updateintent();
//
//                    //  Toast.makeText(Product_Frag_details.this,""+lis.get(i).toString(),Toast.LENGTH_LONG).show();
//
//                             } else if (tr == false) {
//                                 Toast.makeText(getActivity(), "cart updated", Toast.LENGTH_LONG).show();
//                             }
//
//                         } catch (Exception ex) {
//                             ex.printStackTrace();
//                             // Toast.makeText(getActivity(), "" + ex.getMessage(), Toast.LENGTH_LONG).show();
//                         }
//
//                     }
//                 }
//                 txtTotal.setText("\u20B9" + String.valueOf(db_cart.getTotalAmount()));
//             }
//         }
//     });

//


        btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fm=new Cart_fragment();


                if (ConnectivityReceiver.isConnected()) {
//                    Intent intent=new Intent(getActivity(), OtpActivity.class);
//                    startActivity(intent);
                    if(db_cart.getCartCount()>0) {

                        makeGetLimiteRequest();
                    }
                    else
                    {
                        fm = new Empty_cart_fragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                .addToBackStack(null).commit();
                        //Toast.makeText(getActivity(),"Cart is Empty",Toast.LENGTH_LONG ).show();

                    }
                }
                else {
                    ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
                }
//                    FragmentManager fragmentManager=getFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.contentPanel,fm)
//                            .addToBackStack(null)
//                            .commit();



                //}
            }
        });




        return view;
    }

    private void updateintent() {
        Intent updates = new Intent("Grocery_cart");
        updates.putExtra("type", "update");
        getActivity().sendBroadcast(updates);
    }
    @Override
    public void onStart() {
        super.onStart();


        txtTotal.setText("\u20B9"+String.valueOf(db_cart.getTotalAmount()));

        String atr=String.valueOf(details_product_attribute);
        if(atr.equals("[]"))
        {
            boolean st=db_cart.isInCart(product_id);
            if(st==true)
            {
//                btn_add.setVisibility(View.GONE);
                btn_add.setText("Update Cart");
                numberButton.setValue(Integer.parseInt(db_cart.getCartItemQty(product_id)));
//                numberButton.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            String str_id=dialog_txtId.getText().toString();
            String[] str=str_id.split("@");
            String at_id=String.valueOf(str[0]);
            boolean st=db_cart.isInCart(at_id);
            if(st==true)
            {
//                btn_add.setVisibility(View.GONE);
                btn_add.setText("Update Cart");
                numberButton.setValue(Integer.parseInt(db_cart.getCartItemQty(at_id)));
//                numberButton.setVisibility(View.VISIBLE);
            }
        }

        //Toast.makeText(getActivity(),""+cat_id, Toast.LENGTH_LONG).show();
        makeRelatedProductRequest(cat_id);

        try
        {
            image_list.clear();
            JSONArray array=new JSONArray(product_images );
            Log.e("img_arr",array.toString());
            //Toast.makeText(this,""+product_images,Toast.LENGTH_LONG).show();
            if(product_images.equals(null))
            {
                Toast.makeText(getActivity(),"There is no1 image for this product",Toast.LENGTH_LONG).show();
            }
            else
            {
                ArrayList<HashMap<String, String>> img_array = new ArrayList<>();
                for(int i=0; i<=array.length()-1;i++)
                {
                    HashMap<String, String> img_map= new HashMap<>();

                    image_list.add(array.get(i).toString());
                    img_map.put("image",array.get(i).toString());
                    img_array.add(img_map);



                }
                if (image_list.size()>1)
                {
                    img_slider.setVisibility(View.VISIBLE);
                    btn.setVisibility(View.GONE);
                    img_slider.removeAllSliders();
                    for (final HashMap<String, String> name : img_array) {
                        CustomSlider customSlider = new CustomSlider(getActivity());
                        Log.e("slider_images",""+name.get("image").toString());
                        customSlider.description(name.get("")).image(IMG_PRODUCT_URL+name.get("image")).setScaleType( BaseSliderView.ScaleType.Fit);;
                        img_slider.addSlider(customSlider);
//                        customSlider.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
//                            @Override
//                            public void onSliderClick(BaseSliderView slider) {
//                                Fragment fm = new ImagesViewFragment();
//                                FragmentManager fragmentManager = getFragmentManager();
//                                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                                        .addToBackStack(null).commit();
//                            }
//                        });

                    }

                }
                else {
                    img_slider.setVisibility(View.GONE);
                    btn.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(IMG_PRODUCT_URL + image_list.get(0))
                            .placeholder(R.drawable.icon)
//                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .dontAnimate()
                            .into(btn);

                }


            }




        }
        catch (Exception ex)
        {
            // Toast.makeText(Product_Frag_details.this,""+ex.getMessage(),Toast.LENGTH_LONG).show();
        }



    }

    private void makeRelatedProductRequest(String cat_id) {
        loadingBar.show();
        product_modelList.clear();
        String tag_json_obj = "json_product_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("cat_id", cat_id);
        params.put("city_id", module.getCityID());
        Log.e("sdfgbh",params.toString());

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_PRODUCT_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("rett" +
                        "", response.toString());
                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
                        ///         Toast.makeText(getActivity(),""+response.getString("data"),Toast.LENGTH_LONG).show();
                        JSONArray array = response.getJSONArray("data");
                        for (int i = 0 ; i< array.length();i++)
                        {
                            JSONObject obj = array.getJSONObject(i);
                            Product_model model = new Product_model();
                            model.setProduct_id(obj.getString("product_id"));
                            model.setProduct_name(obj.getString("product_name"));
                            model.setProduct_name_hindi(obj.getString("product_name_hindi"));
                            model.setProduct_name_arb(obj.getString("product_name_arb"));
                            model.setProduct_description_arb(obj.getString("product_description_arb"));
                            model.setProduct_description(obj.getString("product_description"));
                            model.setCategory_id(obj.getString("category_id"));
                            model.setDeal_price(obj.getString("deal_price"));
                            model.setStart_date(obj.getString("start_date"));
                            model.setStart_time(obj.getString("start_time"));
                            model.setEnd_date(obj.getString("end_date"));
                            model.setEnd_time(obj.getString("end_time"));
                            model.setProduct_attribute(obj.getString("product_attribute"));
                            model.setPrice(obj.getString("price"));
                            model.setMrp(obj.getString("mrp"));
                            model.setProduct_image(obj.getString("product_image"));
                            model.setStatus(obj.getString("status"));
                            model.setIn_stock(obj.getString("in_stock"));
                            model.setUnit_value(obj.getString("unit_value"));
                            model.setUnit(obj.getString("unit"));
                            model.setStock(obj.getString("stock"));
                            model.setIncreament(obj.getString("increament"));
                            model.setRewards(obj.getString("rewards"));
                            model.setTitle(obj.getString("title"));
                            model.setSize(obj.getString("size"));
                            model.setColor(obj.getString("color"));
                            model.setCity(obj.getString("city"));

                            if (model.getProduct_id().equals(product_id))
                            {

                            }
                            else
                            {
                                product_modelList.add(model);

                            }
                        }
//                        Gson gson = new Gson();
//                        Type listType = new TypeToken<List<Product_model>>() {
//                        }.getType();
//                        product_modelList = gson.fromJson(response.getString("data"), listType);
                        Log.e("prod_list_ret", String.valueOf(product_modelList.size()));
//                        adapter_product = new Top_Selling_Adapter( getActivity(),product_modelList,"related");
                        adapter_product = new AllProductsAdapter(product_modelList,getActivity(),"all");

                        rv_cat.setAdapter(adapter_product);
                        adapter_product.notifyDataSetChanged();
//
                        loadingBar.dismiss();


                        if (getActivity() != null) {
                            if (product_modelList.isEmpty()) {

                                loadingBar.dismiss();
                                //  Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                            }
                            else
                            {

                            }
                        }

                    }
                } catch (JSONException e) {
                    loadingBar.dismiss();
                       e.printStackTrace();
                    String ex=e.getMessage();




                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String msg=module.VolleyErrorMessage(error);
                if(!msg.equals(""))
                {
                    error.printStackTrace();
                    Toast.makeText(getActivity(),""+msg,Toast.LENGTH_LONG).show();
                }

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public int getDiscount(String price, String mrp)
    {
        double mrp_d=Double.parseDouble(mrp);
        double price_d=Double.parseDouble(price);
        double per=((mrp_d-price_d)/mrp_d)*100;
        double df=Math.round(per);
        int d=(int)df;
        return d;
    }

    public boolean checkAttributeStatus(String atr)
    {
        boolean sts=false;
        if(atr.equals("[]"))
        {
            sts=false;
        }
        else
        {
            sts=true;
        }
        return sts;
    }


    private void makeGetLimiteRequest() {

        JsonArrayRequest req = new JsonArrayRequest(BaseURL.GET_LIMITE_SETTING_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        Double total_amount = Double.parseDouble(db_cart.getTotalAmount());


                        try {
                            // Parsing json array response
                            // loop through each json object

                            boolean issmall = false;
                            boolean isbig = false;

                            // arraylist list variable for store data;
                            ArrayList<HashMap<String, String>> listarray = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject jsonObject = (JSONObject) response
                                        .get(i);
                                int value;

                                if (jsonObject.getString("id").equals("1")) {
                                    value = Integer.parseInt(jsonObject.getString("value"));

                                    if (total_amount < value) {
                                        issmall = true;
                                        Toast.makeText(getActivity(), "" + jsonObject.getString("title") + " : " + value, Toast.LENGTH_SHORT).show();
                                    }
                                } else if (jsonObject.getString("id").equals("2")) {
                                    value = Integer.parseInt(jsonObject.getString("value"));

                                    if (total_amount > value) {
                                        isbig = true;
                                        Toast.makeText(getActivity(), "" + jsonObject.getString("title") + " : " + value, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                            if (!issmall && !isbig) {

                                    Bundle args = new Bundle();
                                    Fragment fm = new Delivery_fragment();
                                    fm.setArguments(args);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                            .addToBackStack(null).commit();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                String msg=module.VolleyErrorMessage(error);
                if(!msg.equals(""))
                {
                    error.printStackTrace();
                    Toast.makeText(getActivity(),""+msg,Toast.LENGTH_LONG).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);

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


}

