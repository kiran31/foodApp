package binplus.foodiswill.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Fragment.Cart_fragment;
import binplus.foodiswill.R;
import binplus.foodiswill.util.DatabaseCartHandler;
import binplus.foodiswill.util.Session_management;

import static binplus.foodiswill.Config.BaseURL.KEY_ID;
import static binplus.foodiswill.Fragment.Cart_fragment.btn_checkout;
import static binplus.foodiswill.Fragment.Cart_fragment.linear_cart;
import static binplus.foodiswill.Fragment.Cart_fragment.linear_empty;
import static binplus.foodiswill.Fragment.Cart_fragment.tvDiscount;
import static binplus.foodiswill.Fragment.Cart_fragment.tvMrp;
import static binplus.foodiswill.Fragment.Cart_fragment.tvSubTotal;

import static android.content.Context.MODE_PRIVATE;


public class Cart_adapter extends RecyclerView.Adapter<Cart_adapter.ProductHolder> {
    ArrayList<HashMap<String, String>> list;
    Activity activity;
    String user_id="";
    Session_management session_management;
    String Reward;
    Double price ,reward ;
    SharedPreferences preferences;
    String language;
    int qty = 0;
    // String delivery_charges;

    int lastpostion;
    // DatabaseHandler dbHandler;
    DatabaseCartHandler db_cart;

    public Cart_adapter(ArrayList<HashMap<String, String>> list, Activity activity) {
        this.list = list;
        this.activity = activity;
        db_cart = new DatabaseCartHandler( activity );
    }


    //dbHandler = new DatabaseHandler(activity);

        /*common = new CommonClass(activity);
        File cacheDir = StorageUtils.getCacheDirectory(activity);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        imgconfig = new ImageLoaderConfiguration.Builder(activity)
                .build();
        ImageLoader.getInstance().init(imgconfig);*/


    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cart_rv, parent, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductHolder holder, final int position) {
        final HashMap<String, String> map = list.get(position);

        String img_array=map.get("product_image");
        String img_name = null;
        try {
            JSONArray array=new JSONArray(img_array);
            img_name=array.get(0).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Glide.with(activity)
                .load(BaseURL.IMG_PRODUCT_URL + img_name)
                .centerCrop()
                .placeholder(R.drawable.icon)
//                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.iv_logo);
        preferences = activity.getSharedPreferences("lan", MODE_PRIVATE);
        language=preferences.getString("language","");
        holder.tv_title.setText(map.get("product_name"));


        holder.tv_price.setText(activity.getResources().getString(R.string.currency)+map.get("unit_value"));
        holder.tv_contetiy.setText(map.get("qty"));
        int items = Integer.parseInt(db_cart.getInCartItemQty(map.get("cart_id")));
        price = Double.parseDouble(map.get("unit_value"));
        holder.tv_subcat_weight.setText("Weight : "+map.get("unit"));
        holder.tv_total.setText("" + price * items);
        //   holder.tv_reward.setText("" + reward * items);
        // holder.btnQty.setNumber(String.valueOf(items));
//        final int deli_charges = Integer.parseInt( delivery_charges );
        // final int deli_charges = 10*items;
//        holder.btnQty.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
//            @Override
//            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
//
//                double tot_cart_amount=Double.parseDouble(db_cart.getTotalAmount());
//                float qt=Float.valueOf(newValue);
//                float price=Float.parseFloat(map.get("price"));
//                float unit_price=Float.parseFloat(map.get("unit_price"));
//                float tot_amt=price+unit_price;
//                holder.tv_total.setText(activity.getResources().getString(R.string.currency)+" "+tot_amt);
//                HashMap<String, String> mapProduct = new HashMap<String, String>();
//                mapProduct.put("product_id", map.get("product_id"));
//                mapProduct.put("product_image", map.get("product_image"));
//                mapProduct.put("category_id", map.get("category_id"));
//                mapProduct.put("product_name",map.get("product_name"));
//                mapProduct.put("price",String.valueOf(tot_amt));
//                mapProduct.put("unit_price",map.get("unit_price"));
//                mapProduct.put("size", map.get("size"));
//                mapProduct.put("color", map.get("color"));
//                mapProduct.put("rewards", map.get("rewards"));
//                mapProduct.put("unit_value", map.get("unit_value"));
//                mapProduct.put("unit", map.get("unit"));
//                mapProduct.put("increament", map.get("increament"));
//                mapProduct.put("stock", map.get("stock"));
//                mapProduct.put("title", map.get("title"));
//
//                boolean u=db_cart.updateCart(mapProduct,qt);
//                if(u)
//                {
//                  //  Toast.makeText(activity,"updated",Toast.LENGTH_LONG).show();
//                    Cart_fragment.tv_total.setText(activity.getResources().getString(R.string.currency)+" "+db_cart.getTotalAmount());
//                }
//                else
//                {
//                    //Toast.makeText(activity," not updated",Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
        holder.iv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int id=Integer.parseInt(map.get("cart_id"));

                ArrayList<HashMap<String, String>> mapP=db_cart.getCartProduct(id);

                HashMap<String,String> m=mapP.get(0);

                // Toast.makeText(activity,"Count"+m.get("price"),Toast.LENGTH_LONG).show();

                if (!holder.tv_contetiy.getText().toString().equalsIgnoreCase(""))
                    qty = Integer.valueOf(holder.tv_contetiy.getText().toString());

                if (qty > 0) {
                    qty = qty - 1;
                    holder.tv_contetiy.setText(String.valueOf(qty));
                    double t=Double.parseDouble(m.get("price"));
                    double p=Double.parseDouble(m.get("unit_value"));
                    holder.tv_total.setText("" + p * qty);
                    String pr =String.valueOf(t-p);
                    float qt=Float.valueOf(qty);

                    HashMap<String, String> mapProduct = new HashMap<String, String>();

                    mapProduct.put( "cart_id", map.get("cart_id") );
                    mapProduct.put( "product_id", map.get("product_id") );
                    mapProduct.put( "product_name", map.get("product_name") );
                    mapProduct.put( "product_name_hindi", map.get("product_name_hindi") );
                    mapProduct.put( "product_name_arb", map.get("product_name_arb") );
                    mapProduct.put( "product_description_arb", map.get("product_description_arb") );
                    mapProduct.put( "category_id", map.get("category_id") );
                    mapProduct.put( "product_description", map.get("product_description") );
                    mapProduct.put( "deal_price", map.get("deal_price") );
                    mapProduct.put( "start_date", map.get("start_date") );
                    mapProduct.put( "start_time", map.get("start_time") );
                    mapProduct.put( "end_date", map.get("end_date") );
                    mapProduct.put( "end_time",map.get("end_time") );
                    mapProduct.put( "price",  pr );
                    mapProduct.put( "mrp", map.get("mrp") );
                    mapProduct.put( "product_image", map.get("product_image") );
                    mapProduct.put( "status", map.get("status") );
                    mapProduct.put( "in_stock", map.get("in_stock") );
                    mapProduct.put( "unit_value", map.get("unit_value") );
                    mapProduct.put( "unit", map.get("unit"));
                    mapProduct.put( "increament", map.get("increament") );
                    mapProduct.put( "rewards", map.get("rewards") );
                    mapProduct.put( "stock", map.get("stock") );
                    mapProduct.put( "size", map.get("size") );
                    mapProduct.put( "color", map.get("color") );
                    mapProduct.put( "city", map.get("city") );
                    mapProduct.put( "title", map.get("title") );
                    mapProduct.put( "cat_id",  map.get("cat_id") );


//
//                Toast.makeText(activity,"id- "+map.get("product_id")+"\n img- "+map.get("product_image")+"\n cat_id- "+map.get("category_id")+"\n" +
//                        "\n name- "+map.get("product_name")+"\n price- "+pr+"\n unit_price- "+map.get("unit_price")+
//                        "\n size- "+ map.get("size")+"\n col- "+ map.get("color")+"rew- "+ map.get("rewards")+"unit_value- "+ map.get("unit_value")+
//                        "unit- "+map.get("unit")+"\n inc- "+map.get("increament")+"stock- "+map.get("stock")+"title- "+map.get("title"),Toast.LENGTH_LONG).show();

                    boolean update_cart=db_cart.setCart(mapProduct,qt);
                    if(update_cart==true)
                    {
                        Toast.makeText(activity,"Qty Not Updated",Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        Toast.makeText(activity,"Qty Updated",Toast.LENGTH_SHORT).show();
                        //      Cart_fragment.tv_total.setText(activity.getResources().getString(R.string.currency)+" "+db_cart.getTotalAmount());
                        tvMrp.setText(activity.getResources().getString(R.string.currency)+" "+db_cart.getTotalAmount());

                        //  String mrp= getTotMRp();
                        String mrp = "20";
                        String price=String.valueOf(db_cart.getTotalAmount());
                        // tvMrp.setText(activity.getResources().getString(R.string.currency) +mrp);
                        double mp=Double.parseDouble(mrp);
                        double pp=Double.parseDouble(price);
                        double d=mp-pp;

                        tvDiscount.setText("-"+activity.getResources().getString(R.string.currency)+String.valueOf(d));
                        double db =mp-d ;
                        //     tvDelivary.setText(activity.getResources().getString(R.string.currency)+deli_charges);
                        tvSubTotal.setText(activity.getResources().getString(R.string.currency)+db);
                    }

                }

                if (holder.tv_contetiy.getText().toString().equalsIgnoreCase("0")) {

                    String type=String.valueOf(map.get("type"));
                    if(type.equals("p"))
                    {
                        db_cart.removeItemFromCart(map.get("product_id"));
                        tvMrp.setText(activity.getResources().getString(R.string.currency)+" "+db_cart.getTotalAmount());

                        String mrp= getTotMRp();
                        String price=String.valueOf(db_cart.getTotalAmount());
                        //  tvMrp.setText(activity.getResources().getString(R.string.currency) +mrp);
                        double mp=Double.parseDouble(mrp);
                        double pp=Double.parseDouble(price);
                        double d=mp-pp;

                        tvDiscount.setText("-"+activity.getResources().getString(R.string.currency)+String.valueOf(d));
                        double db = mp-d ;
                        //  tvDelivary.setText(activity.getResources().getString(R.string.currency)+deli_charges);
                        tvSubTotal.setText(activity.getResources().getString(R.string.currency)+db);

                    }
                    else if(type.equals("a"))
                    {
                        db_cart.removeItemFromCart(map.get("cart_id"));
                        tvMrp.setText(activity.getResources().getString(R.string.currency)+" "+db_cart.getTotalAmount());

                        String mrp= getTotMRp();
                        String price=String.valueOf(db_cart.getTotalAmount());
                        //  tvMrp.setText(activity.getResources().getString(R.string.currency) +mrp);
                        double mp=Double.parseDouble(mrp);
                        double pp=Double.parseDouble(price);
                        double d=mp-pp;

                        tvDiscount.setText("-"+activity.getResources().getString(R.string.currency)+String.valueOf(d));
                        double db = (mp-d) ;
                        //  tvDelivary.setText(activity.getResources().getString(R.string.currency)+deli_charges);
                        tvSubTotal.setText(activity.getResources().getString(R.string.currency)+db);
                    }
                    list.remove(position);
                    notifyDataSetChanged();
                    // db_cart.getCartAll()
                    updateintent();
                    if(list.size()<=0)
                    {
//                        Empty_cart_fragment fm = new Empty_cart_fragment();
//
////                FragmentManager fragmentManager = .beginTransaction().replace(R.id.contentPanel, fm)
////                        .addToBackStack(null).commit();
//
//                        AppCompatActivity activity=(AppCompatActivity) view.getContext();
//                        activity.getFragmentManager().beginTransaction().replace(R.id.contentPanel,fm)
//                                .addToBackStack(null)
//                                .commit();
                        linear_cart.setVisibility( View.GONE );
                        btn_checkout.setVisibility( View.GONE );
                        linear_empty.setVisibility( View.VISIBLE );
                    }
                }
            }
        });

        holder.iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int id = Integer.parseInt(map.get("cart_id"));

                int qty = Integer.parseInt(holder.tv_contetiy.getText().toString());
                ArrayList<HashMap<String, String>> mapP = db_cart.getCartProduct(id);

                HashMap<String, String> m = mapP.get(0);

                float stock = Float.parseFloat(m.get("stock"));

                if (qty == stock) {
                    Toast.makeText(activity,"Not Available",Toast.LENGTH_SHORT).show();
                } else {

                    qty = qty + 1;

                    holder.tv_contetiy.setText(String.valueOf(qty));

                    //    holder.tv_reward.setText("" + reward * qty);


                    double t = Double.parseDouble(m.get("price"));
                    double p = Double.parseDouble(m.get("unit_value"));
                    holder.tv_total.setText("" + p * qty);
                    String pr = String.valueOf(t + p);
                    float qt = Float.valueOf(qty);

                    // Toast.makeText(activity,"\npri "+map.get("unit_value")+"\n am "+pr,Toast.LENGTH_LONG ).show();
                    HashMap<String, String> mapProduct = new HashMap<String, String>();

                    mapProduct.put( "cart_id", map.get("cart_id") );
                    mapProduct.put( "product_id", map.get("product_id") );
                    mapProduct.put( "product_name", map.get("product_name") );
                    mapProduct.put( "product_name_hindi", map.get("product_name_hindi") );
                    mapProduct.put( "product_name_arb", map.get("product_name_arb") );
                    mapProduct.put( "product_description_arb", map.get("product_description_arb") );
                    mapProduct.put( "category_id", map.get("category_id") );
                    mapProduct.put( "product_description", map.get("product_description") );
                    mapProduct.put( "deal_price", map.get("deal_price") );
                    mapProduct.put( "start_date", map.get("start_date") );
                    mapProduct.put( "start_time", map.get("start_time") );
                    mapProduct.put( "end_date", map.get("end_date") );
                    mapProduct.put( "end_time",map.get("end_time") );
                    mapProduct.put( "price",  pr );
                    mapProduct.put( "mrp", map.get("mrp") );
                    mapProduct.put( "product_image", map.get("product_image") );
                    mapProduct.put( "status", map.get("status") );
                    mapProduct.put( "in_stock", map.get("in_stock") );
                    mapProduct.put( "unit_value", map.get("unit_value") );
                    mapProduct.put( "unit", map.get("unit"));
                    mapProduct.put( "increament", map.get("increament") );
                    mapProduct.put( "rewards", map.get("rewards") );
                    mapProduct.put( "stock", map.get("stock") );
                    mapProduct.put( "size", map.get("size") );
                    mapProduct.put( "color", map.get("color") );
                    mapProduct.put( "city", map.get("city") );
                    mapProduct.put( "title", map.get("title") );
                    mapProduct.put( "cat_id",  map.get("cat_id") );


//
//                Toast.makeText(activity,"id- "+map.get("product_id")+"\n img- "+map.get("product_image")+"\n cat_id- "+map.get("category_id")+"\n" +
//                        "\n name- "+map.get("product_name")+"\n price- "+pr+"\n unit_price- "+map.get("unit_price")+
//                        "\n size- "+ map.get("size")+"\n col- "+ map.get("color")+"rew- "+ map.get("rewards")+"unit_value- "+ map.get("unit_value")+
//                        "unit- "+map.get("unit")+"\n inc- "+map.get("increament")+"stock- "+map.get("stock")+"title- "+map.get("title"),Toast.LENGTH_LONG).show();

                    boolean update_cart = db_cart.setCart(mapProduct, qt);
                    if (update_cart == true) {
                        Toast.makeText(activity, "Qty Not Updated", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(activity, "Qty Updated", Toast.LENGTH_SHORT).show();

                        tvMrp.setText(activity.getResources().getString(R.string.currency) + " " + db_cart.getTotalAmount());

                        String mrp = getTotMRp();
                        String price = String.valueOf(db_cart.getTotalAmount());
                        //  tvMrp.setText(activity.getResources().getString(R.string.currency) +mrp);
                        double mp = Double.parseDouble(mrp);
                        double pp = Double.parseDouble(price);
                        double d = mp - pp;

                        tvDiscount.setText("-" + activity.getResources().getString(R.string.currency) + String.valueOf(d));
                        double db = mp - d;
                        //  tvDelivary.setText(activity.getResources().getString(R.string.currency)+deli_charges);
                        tvSubTotal.setText(activity.getResources().getString(R.string.currency) + db);
                    }
                    //  holder.tv_total.setText(""+db_cart.getTotalAmount());
                }
            }
        });
//
//


        holder.tv_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int cnt=db_cart.getCartCount();
//                Toast.makeText(activity,"id- "+map.get("stock"),Toast.LENGTH_SHORT).show();


            }
        });


//       holder.tv_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                db_cart.setCart(map, Float.valueOf(holder.tv_contetiy.getText().toString()));
//
//                Double items = Double.parseDouble(db_cart.getInCartItemQty(map.get("product_id")));
//                Double price = Double.parseDouble(map.get("price"));
//                Double reward = Double.parseDouble(map.get("rewards"));
//                holder.tv_total.setText("" + price * qty);
//                holder.tv_reward.setText("" + reward * qty);
//             // holder.tv_total.setText(activity.getResources().getString(R.string.tv_cart_total) + price * items + " " + activity.getResources().getString(R.string.currency));
//                updateintent();
//            }
//        });

        holder.iv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type=String.valueOf(map.get("type"));
                if(type.equals("p"))
                {
                    db_cart.removeItemFromCart(map.get("product_id"));

                }
                else if(type.equals("a"))
                {
                    db_cart.removeItemFromCart(map.get("cart_id"));
                }

                list.remove(position);
                notifyDataSetChanged();

                updateintent();
                tvMrp.setText(activity.getResources().getString(R.string.currency)+" "+db_cart.getTotalAmount());
                int items = db_cart.getCartCount();

                String mrp= getTotMRp();
                String price=String.valueOf(db_cart.getTotalAmount());
                //  tvMrp.setText(activity.getResources().getString(R.string.currency) +mrp);
                double mp=Double.parseDouble(mrp);
                double pp=Double.parseDouble(price);
                double d=mp-pp;

                tvDiscount.setText("-"+activity.getResources().getString(R.string.currency)+String.valueOf(d));
                double db = mp-d;
                // tvDelivary.setText(activity.getResources().getString(R.string.currency)+deli_charges);
                tvSubTotal.setText(activity.getResources().getString(R.string.currency)+db);

                if(list.size()<=0)
                {
                    Cart_fragment.linear_empty.setVisibility(View.VISIBLE);
                    Cart_fragment.rv_cart.setVisibility(View.GONE);
                    Cart_fragment.linear_cart.setVisibility(View.GONE);
                    Cart_fragment.btn_checkout.setVisibility(View.GONE);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        public TextView tv_title, tv_price,  tv_total,tv_contetiy,
                tv_unit, tv_unit_value,tv_subcat_weight;
        //tv_reward,
        // ElegantNumberButton btnQty;
        public ImageView iv_logo,iv_plus,iv_minus, iv_remove;

        public ProductHolder(View view) {
            super(view);

            tv_title = (TextView) view.findViewById(R.id.tv_subcat_title);
            tv_price = (TextView) view.findViewById(R.id.tv_subcat_price);
            tv_total = (TextView) view.findViewById(R.id.tv_subcat_total);
            // tv_reward = (TextView) view.findViewById(R.id.tv_reward_point);
            tv_contetiy = (TextView) view.findViewById(R.id.tv_subcat_contetiy);
            tv_subcat_weight = (TextView) view.findViewById(R.id.tv_subcat_weight);
            //tv_add = (TextView) view.findViewById(R.id.tv_subcat_add);
            //  btnQty=(ElegantNumberButton)view.findViewById(R.id.product_qty);
            iv_logo = (ImageView) view.findViewById(R.id.iv_subcat_img);
            iv_plus = (ImageView) view.findViewById(R.id.iv_subcat_plus);
            iv_minus = (ImageView) view.findViewById(R.id.iv_subcat_minus);
            iv_remove = (ImageView) view.findViewById(R.id.iv_subcat_remove);
            session_management=new Session_management(activity);
            user_id=session_management.getUserDetails().get(KEY_ID);

            //tv_add.setText(R.string.tv_pro_update);

        }
    }

    private void updateintent() {
        Intent updates = new Intent("Grocery_cart");
        updates.putExtra("type", "update");
        activity.sendBroadcast(updates);
    }
    public String getTotMRp()
    {
        ArrayList<HashMap<String, String>> list = db_cart.getCartAll();
        float sum=0;
        for(int i=0;i<list.size();i++)
        {
            final HashMap<String, String> map = list.get(i);

            float q=Float.parseFloat(map.get("qty"));
            float m=Float.parseFloat(map.get("mrp"));

            sum=sum+(q*m);
            //   Toast.makeText(getActivity(),""+q+"\n"+m,Toast.LENGTH_LONG).show();

        }
        if(sum!=0)
        {
            return String.valueOf(sum);
        }
        else
            return "0";
        //Toast.makeText(getActivity(),""+sum,Toast.LENGTH_LONG).show();
    }

}