package binplus.foodiswill.Adapter;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.Fragment.Details_Fragment;
import binplus.foodiswill.MainActivity;
import binplus.foodiswill.Model.ProductVariantModel;
import binplus.foodiswill.Model.Product_model;
import binplus.foodiswill.R;
import binplus.foodiswill.util.DatabaseCartHandler;
import binplus.foodiswill.util.Session_management;
import binplus.foodiswill.util.WishlistHandler;


import static android.content.Context.MODE_PRIVATE;
import static binplus.foodiswill.Config.BaseURL.KEY_ID;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.viewHolder> {
    List<String> image_list;
    // Dialog dialog;
    ListView listView1;
    String atr_id="";
    String atr_product_id="";
    String attribute_name="";
    String attribute_value="";
    String attribute_mrp="";
    ArrayList<ProductVariantModel> variantList;
    ArrayList<ProductVariantModel> attributeList;
    ProductVariantAdapter productVariantAdapter;

    private List<Product_model> modelList;
    private Context context;
    int status=0;

    private DatabaseCartHandler db_cart;
    private WishlistHandler db_wish;
    String language;
    SharedPreferences preferences;
    Session_management sessionManagement  ;
    String user_id ;
Module module ;
    float stock ;

    public ProductAdapter(List<Product_model> modelList, Context context) {
        this.modelList = modelList;
        this.context = context;
        db_cart=new DatabaseCartHandler(context);
        db_wish=new WishlistHandler(context);
    }


    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate( R.layout.row_product_grid, parent, false);
        Context context = parent.getContext();
        return new ProductAdapter.viewHolder( itemView);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {
        final Product_model mList = modelList.get(position);
        final String getid = mList.getProduct_id();


        stock= Float.parseFloat(modelList.get(position).getStock());
        holder.elegantNumberButton.setRange( 1, (int) stock );

        if(stock<=0 || mList.getIn_stock().equals("0"))
        {
            holder.rel_out.setVisibility( View.VISIBLE);
//            holder.rel_add.setVisibility( View.GONE);
//            holder.wish_before.setVisibility( View.GONE );
//            holder.wish_after.setVisibility( View.GONE );
        }
        else
        {
            holder.rel_out.setVisibility( View.GONE);

        }
        if(db_wish.isInWishtable(getid ,user_id ) == false)
        {
            holder.wish_after.setVisibility( View.GONE);
            holder.wish_before.setVisibility( View.VISIBLE );
        }
        else
        {
            holder.wish_after.setVisibility( View.VISIBLE);
            holder.wish_before.setVisibility( View.GONE );
        }

        try
        {
            image_list.clear();
            JSONArray array=new JSONArray(modelList.get(position).getProduct_image().toString());
            //Toast.makeText(,""+mList.getProduct_image().toString(),Toast.LENGTH_LONG).show();
            if(mList.getProduct_image().equals(null))
            {
                Glide.with(context)
                        .load( BaseURL.IMG_PRODUCT_URL +modelList.get(position).getProduct_image().toString() )
                        .fitCenter()
                        .placeholder( R.drawable.icon)
//                        .crossFade()
                        .diskCacheStrategy( DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .into(holder.product_img);

            }
            else
            {
                for(int i=0; i<=array.length()-1;i++)
                {
                    image_list.add(array.get(i).toString());

                }


                Glide.with(context)
                        .load( BaseURL.IMG_PRODUCT_URL +image_list.get(0).toString() )
                        .fitCenter()
                        .placeholder( R.drawable.icon)
//                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .into(holder.product_img);

            }
        }
        catch (Exception ex)
        {
            //  Toast.makeText(context,""+ex.getMessage(),Toast.LENGTH_LONG).show();
        }

        preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
        language=preferences.getString("language","");
//        if (language.contains("english")) {
//            holder.product_name.setText(mList.getProduct_name());
//        }
//        else {
//            holder.product_name.setText(mList.getProduct_name());
//
//
//        }
        if(mList.getProduct_name_hindi().isEmpty())
        {
            holder.product_name.setText(mList.getProduct_name());
        }
        else
        {
            holder.product_name.setText(mList.getProduct_name() +"\n( "+mList.getProduct_name_hindi()+" )");
        }

        holder.product_mrp.setPaintFlags( holder.product_mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        String atr= String.valueOf(mList.getProduct_attribute());
        if(atr.equals("[]"))
        {


            status=1;

            String p= String.valueOf(mList.getPrice());
            String m= String.valueOf(mList.getMrp());
            int mm = Integer.parseInt( m );
            int pp = Integer.parseInt( p );
            holder.product_price.setText(context.getResources().getString( R.string.currency)+ mList.getPrice());
            if(mm>pp) {
                holder.product_mrp.setText( context.getResources().getString( R.string.currency ) + mList.getMrp() );
                int discount = getDiscount( p, m );
                //Toast.makeText(getActivity(),""+atr,Toast.LENGTH_LONG).show();
                holder.product_discount.setText( "" + discount + "% "+"\n OFF" );
                holder.iv_dis.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.product_mrp.setVisibility( View.GONE );
                holder.product_discount.setVisibility( View.GONE );
                holder.iv_dis.setVisibility( View.GONE );

            }
            //   holder.txtrate.setVisibility(View.VISIBLE);
            holder.rel_varient.setVisibility( View.GONE);
            //   holder.txtrate.setText(mList.getUnit_value()+" "+mList.getUnit());

        }

        else
        {

            status=2;
            attributeList.clear();
            holder.rel_varient.setVisibility( View.VISIBLE);
//            String atr=String.valueOf(mList.getProduct_attribute());
            JSONArray jsonArr = null;
            try {

                jsonArr = new JSONArray(atr);
                for (int i = 0; i < jsonArr.length(); i++)
                {
                    ProductVariantModel model=new ProductVariantModel();
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    String atrid=jsonObj.getString("id");
                    String atrproductid=jsonObj.getString("product_id");
                    String attributename=jsonObj.getString("attribute_name");
                    String attributevalue=jsonObj.getString("attribute_value");
                    String attributemrp=jsonObj.getString("attribute_mrp");


                    model.setId(atrid);
//                    model.setProduct_id(atrproductid);
                    model.setAttribute_value(attributevalue);
                    model.setAttribute_name(attributename);
                    model.setAttribute_mrp(attributemrp);

                    attributeList.add(model);

                    //     arrayList.add(new AttributeModel(atr_id,product_id,attribute_name,attribute_value));

                    //Toast.makeText(getActivity(),"id "+atr_id+"\n p_id "+product_id+"\n atr_name "+attribute_name+"\n atr_value "+attribute_value,Toast.LENGTH_LONG).show();
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }

            try
            {




                atr_id=attributeList.get(0).getId();
//                atr_product_id=attributeList.get(0).getProduct_id();
                attribute_name=attributeList.get(0).getAttribute_name();
                attribute_value=attributeList.get(0).getAttribute_value();
                attribute_mrp=attributeList.get(0).getAttribute_mrp();



                //     arrayList.add(new AttributeModel(atr_id,product_id,attribute_name,attribute_value));

                //Toast.makeText(getActivity(),"id "+atr_id+"\n p_id "+product_id+"\n atr_name "+attribute_name+"\n atr_value "+attribute_value,Toast.LENGTH_LONG).show();



                String atr_price= String.valueOf(attribute_value);
                String atr_mrp= String.valueOf(attribute_mrp);
                int atr_m = Integer.parseInt( atr_mrp );
                int atr_p = Integer.parseInt( atr_price );
                holder.product_price.setText("\u20B9"+attribute_value.toString());
                if (atr_m>atr_p) {
                    int atr_dis = getDiscount( atr_price, atr_mrp );
                    holder.product_discount.setText( "" + atr_dis + "%"+"\n OFF" );

                    holder.product_mrp.setText( "\u20B9" + attribute_mrp.toString() );
                    holder.iv_dis.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.iv_dis.setVisibility(View.GONE);
                    holder.product_discount.setVisibility( View.GONE );
                    holder.product_mrp.setVisibility( View.GONE );
                }
                holder.dialog_txtId.setText(atr_id.toString()+"@"+"0");
                holder.dialog_unit_type.setText("\u20B9"+attribute_value+"/"+attribute_name);
                holder.dialog_txtVar.setText(attribute_value+"@"+attribute_name+"@"+attribute_mrp);
                //  holder.txtTotal.setText("\u20B9"+String.valueOf(list_atr_value.get(0).toString()));

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }


        }
        final String product_id= String.valueOf(mList.getProduct_id());
        if(atr.equals("[]"))
        {
            boolean st=db_cart.isInCart(product_id);
            if(st==true)
            {
                holder.add.setVisibility( View.GONE);
                holder.elegantNumberButton.setNumber(db_cart.getCartItemQty(product_id));
                holder.elegantNumberButton.setVisibility( View.VISIBLE);
            }
            else
            {
                holder.add.setVisibility( View.VISIBLE );
                holder.elegantNumberButton.setVisibility( View.GONE );
            }

        }
        else
        {
            String str_id=holder.dialog_txtId.getText().toString();
            String[] str=str_id.split("@");
            String at_id= String.valueOf(str[0]);
            boolean st=db_cart.isInCart(at_id);
            //  boolean st=true;
            if(st==true)
            {
                holder.add.setVisibility( View.GONE);
                holder.elegantNumberButton.setNumber(String.valueOf(db_cart.getCartItemQty(at_id)));
                holder.elegantNumberButton.setVisibility( View.VISIBLE);
            }
            else {
                holder.add.setVisibility( View.VISIBLE );
                holder.elegantNumberButton.setVisibility( View.GONE );

            }
        }





        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                float stck = Float.parseFloat(mList.getStock());
                if (stck <= 0 || mList.getIn_stock().equals("0")) {
                    Toast.makeText(context, "Out Of Stock", Toast.LENGTH_LONG).show();
                } else {
                    holder.add.setVisibility(View.GONE);
                    holder.elegantNumberButton.setVisibility(View.VISIBLE);
                    holder.elegantNumberButton.setNumber(String.valueOf(1));
                    Product_model mList = modelList.get(position);
                    String atr = String.valueOf(modelList.get(position).getProduct_attribute());
                    if (atr.equals( "[]" )) {
                        HashMap<String, String> mapProduct = new HashMap<String, String>();
                        String unt = String.valueOf( mList.getUnit_value() + " " + mList.getUnit() );
                        mapProduct.put( "cart_id", mList.getProduct_id() );
                        mapProduct.put( "product_id", mList.getProduct_id() );
                        mapProduct.put( "product_image", mList.getProduct_image() );
                        mapProduct.put( "cat_id", mList.getCategory_id() );
                        mapProduct.put( "product_name", mList.getProduct_name() );
                        mapProduct.put( "price", mList.getPrice() );
                        mapProduct.put( "unit_price", mList.getPrice() );
                        mapProduct.put( "stock", mList.getStock() );
                        mapProduct.put( "unit", unt );
                        mapProduct.put( "mrp", mList.getMrp() );
                        mapProduct.put( "type", "p" );
                        try {

                            boolean tr = db_cart.setCart( mapProduct, (float) 1 );
                            if (tr == true) {
                                MainActivity mainActivity = new MainActivity();
                                mainActivity.setCartCounter( "" + db_cart.getCartCount() );

                                //   context.setCartCounter("" + holder.db_cart.getCartCount());
                                Toast.makeText( context, "Added to Cart", Toast.LENGTH_LONG ).show();
                                int n = db_cart.getCartCount();
                                updateintent();


                            } else if (tr == false) {
                                Toast.makeText( context, "cart updated", Toast.LENGTH_LONG ).show();
                            }

                        } catch (Exception ex) {
                            Toast.makeText( context, "" + ex.getMessage(), Toast.LENGTH_LONG ).show();
                        }

                        //Toast.makeText(context,"1\n"+status+"\n"+modelList.get(position).getProduct_attribute(),Toast.LENGTH_LONG).show();
                    } else {
                        //ProductVariantModel model=variantList.get(position);

                        String str_id = holder.dialog_txtId.getText().toString();
                        String s = holder.dialog_txtVar.getText().toString();
                        String[] st = s.split("@");
                        String st0 = String.valueOf(st[0]);
                        String st1 = String.valueOf(st[1]);
                        String st2 = String.valueOf(st[2]);
                        String[] str = str_id.split("@");
                        String at_id = String.valueOf(str[0]);
                        int j = Integer.parseInt(String.valueOf(str[1]));
                        //       Toast.makeText(context,""+str[0].toString()+"\n"+str[1].toString(),Toast.LENGTH_LONG).show();
                        HashMap<String, String> mapProduct = new HashMap<String, String>();
                        mapProduct.put("cart_id", at_id);
                        mapProduct.put("product_id", mList.getProduct_id());
                        mapProduct.put("product_image", mList.getProduct_image());
                        mapProduct.put("cat_id", mList.getCategory_id());
                        mapProduct.put("product_name", mList.getProduct_name());
                        mapProduct.put("price", st0);
                        mapProduct.put("unit_price", st0);
                        mapProduct.put("stock", mList.getStock());
                        mapProduct.put("unit", st1);
                        mapProduct.put("mrp", st2);
                        mapProduct.put("type", "a");
                        //  Toast.makeText(context,""+attributeList.get(j).getId()+"\n"+mapProduct,Toast.LENGTH_LONG).show();
                        try {

                            boolean tr = db_cart.setCart(mapProduct, (float) 1);
                            if (tr == true) {
                                MainActivity mainActivity = new MainActivity();
                                mainActivity.setCartCounter("" + db_cart.getCartCount());

                                //   context.setCartCounter("" + holder.db_cart.getCartCount());
                                Toast.makeText(context, "Added to Cart", Toast.LENGTH_LONG).show();
                                int n = db_cart.getCartCount();
                                updateintent();


                            } else if (tr == false) {
                                Toast.makeText(context, "cart updated", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception ex) {
                            Toast.makeText(context, "" + ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                        updateintent();

                }


            }
        });

        holder.elegantNumberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                final Product_model mList = modelList.get(position);
                float qty = Float.parseFloat( holder.elegantNumberButton.getNumber() );
                String atr = String.valueOf( modelList.get( position ).getProduct_attribute() );
                if(newValue<=0)
                {
                    boolean st=checkAttributeStatus(atr);
                    if(st==false)
                    {
                        db_cart.removeItemFromCart(product_id);

                    }
                    else if(st==true)
                    {

                        String str_id = holder.dialog_txtId.getText().toString();
                        String[] str = str_id.split("@");
                        String at_id = String.valueOf(str[0]);
                        db_cart.removeItemFromCart(at_id);
                    }

                    holder.elegantNumberButton.setVisibility( View.GONE);
                    holder.add.setVisibility( View.VISIBLE);
                    updateintent();
                }
//                else if (newValue==stock)
//                {
//                    int ss = Integer.parseInt( mList.getStock());
//                    Toast.makeText(context,"Only " +ss+ " in Stock",Toast.LENGTH_LONG ).show();
//                   // holder.elegantNumberButton.setNumber( String.valueOf(oldValue) );
//                }
                else {


                    if (atr.equals( "[]" )) {
                        double pr = Double.parseDouble( mList.getPrice() );
                        double amt = pr * qty;
                        HashMap<String, String> mapProduct = new HashMap<String, String>();
                        String unt = String.valueOf( mList.getUnit_value() + " " + mList.getUnit() );
                        mapProduct.put( "cart_id", mList.getProduct_id() );
                        mapProduct.put( "product_id", mList.getProduct_id() );
                        mapProduct.put( "product_image", mList.getProduct_image() );
                        mapProduct.put( "cat_id", mList.getCategory_id() );
                        mapProduct.put( "product_name", mList.getProduct_name() );
                        mapProduct.put( "price", String.valueOf( amt ) );
                        mapProduct.put( "unit_price", mList.getPrice() );
                        mapProduct.put("stock", mList.getStock());
                        mapProduct.put( "unit", unt );
                        mapProduct.put( "mrp", mList.getMrp() );
                        mapProduct.put( "type", "p" );
                        try {

                            boolean tr = db_cart.setCart( mapProduct, qty );
                            if (tr == true) {
                                MainActivity mainActivity = new MainActivity();
                                mainActivity.setCartCounter( "" + db_cart.getCartCount() );

                                //   context.setCartCounter("" + holder.db_cart.getCartCount());
                                Toast.makeText( context, "Added to Cart", Toast.LENGTH_LONG ).show();
                                int n = db_cart.getCartCount();
                                updateintent();


                            } else if (tr == false) {
                                Toast.makeText( context, "cart updated", Toast.LENGTH_LONG ).show();
                            }

                        } catch (Exception ex) {
                            Toast.makeText( context, "" + ex.getMessage(), Toast.LENGTH_LONG ).show();
                        }

                        //Toast.makeText(context,"1\n"+status+"\n"+modelList.get(position).getProduct_attribute(),Toast.LENGTH_LONG).show();
                    } else {
                        //ProductVariantModel model=variantList.get(position);

                        String str_id = holder.dialog_txtId.getText().toString();


                        String s = holder.dialog_txtVar.getText().toString();
                        String[] st = s.split( "@" );
                        String st0 = String.valueOf( st[0] );
                        String st1 = String.valueOf( st[1] );
                        String st2 = String.valueOf( st[2] );
                        String[] str = str_id.split( "@" );
                        String at_id = String.valueOf( str[0] );
                        int k = Integer.parseInt( String.valueOf( str[1] ) );
                        double pr = Double.parseDouble( st0 );
                        double amt = pr * qty;
                        //       Toast.makeText(context,""+str[0].toString()+"\n"+str[1].toString(),Toast.LENGTH_LONG).show();
                        HashMap<String, String> mapProduct = new HashMap<String, String>();
                        mapProduct.put( "cart_id", at_id );
                        mapProduct.put( "product_id", mList.getProduct_id() );
                        mapProduct.put( "product_image", mList.getProduct_image() );
                        mapProduct.put( "cat_id", mList.getCategory_id() );
                        mapProduct.put( "product_name", mList.getProduct_name() );
                        mapProduct.put( "price", String.valueOf( amt ) );
                        mapProduct.put("stock", mList.getStock());
                        mapProduct.put( "unit_price", st0 );
                        mapProduct.put( "unit", st1 );
                        mapProduct.put( "mrp", st2 );
                        mapProduct.put( "type", "a" );
                        //  Toast.makeText(context,""+attributeList.get(j).getId()+"\n"+mapProduct,Toast.LENGTH_LONG).show();
                        try {

                            boolean tr = db_cart.setCart( mapProduct, qty );
                            if (tr == true) {
                                MainActivity mainActivity = new MainActivity();
                                mainActivity.setCartCounter( "" + db_cart.getCartCount() );

                                //   context.setCartCounter("" + holder.db_cart.getCartCount());
                                Toast.makeText( context, "Added to Cart", Toast.LENGTH_LONG ).show();
                                int n = db_cart.getCartCount();
                                updateintent();


                            } else if (tr == false) {
                                Toast.makeText( context, "cart updated", Toast.LENGTH_LONG ).show();
                            }

                        } catch (Exception ex) {
                            Toast.makeText( context, "" + ex.getMessage(), Toast.LENGTH_LONG ).show();
                        }

                    }
                }

            }
        });




        //holder.tv_reward.setText(mList.getRewards());
        //holder.tv_price.setText(context.getResources().getString(R.string.currency)+ mList.getPrice()+" / "+ mList.getUnit_value()+" "+mList.getUnit());



//        holder.tv_price.setText(context.getResources().getString(R.string.tv_pro_price) + mList.getUnit_value() + " " +
//                mList.getUnit() +" \n"+ mList.getPrice()+ context.getResources().getString(R.string.currency));
//        Double items = Double.parseDouble(dbcart.getInCartItemQty(mList.getProduct_id()));
//        Double prices = Double.parseDouble(mList.getPrice());
//        Double reward = Double.parseDouble(mList.getRewards());
//        //holder.tv_total.setText("" + price * items);
        //  holder.tv_reward.setText("" + reward * items);




//       holder.tv_add.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View view) {
//
//               int sd=db_cart.getCartCount();
//
//               Toast.makeText(context,""+sd,Toast.LENGTH_LONG).show();
//
//           }
//       });



    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView product_img ,wish_before , wish_after,iv_dis;
        TextView product_name ,product_price , product_mrp , product_discount , weight ;
        Button add ;
        CardView cardView ;

        ElegantNumberButton elegantNumberButton;
        TextView dialog_unit_type,dialog_txtId,dialog_txtVar;
        RelativeLayout rel_varient,rel_out ,rel_add,relative_layout;

        WishlistHandler db_wish;
        DatabaseCartHandler db_cart ;

        public viewHolder(View itemView) {
            super( itemView );

            product_name = (TextView)itemView.findViewById( R.id.product_name );
            product_discount=(TextView)itemView.findViewById( R.id.product_discount );
            product_mrp = (TextView)itemView.findViewById( R.id.product_mrp );
            product_price=(TextView)itemView.findViewById( R.id.product_price );
            weight =(TextView)itemView.findViewById( R.id.product_weight );
            rel_varient =(RelativeLayout)itemView.findViewById( R.id.rel_variant );
            rel_out=(RelativeLayout)itemView.findViewById( R.id.rel_out);
            relative_layout=(RelativeLayout)itemView.findViewById( R.id.relative_layout);
            cardView = itemView.findViewById( R.id.card_product );
            iv_dis = itemView.findViewById( R.id.iv_dis );

            elegantNumberButton = itemView.findViewById( R.id.elegantButton );
            product_img = itemView.findViewById( R.id.product_img );
            add = itemView.findViewById( R.id.add_btn );
            dialog_txtId = itemView.findViewById( R.id.txtId );
            dialog_txtVar = itemView.findViewById( R.id.txtVar );
            dialog_unit_type= itemView.findViewById( R.id.unit_type );
            wish_after = itemView.findViewById( R.id.wish_after );
            wish_before = itemView.findViewById( R.id.wish_before );
            rel_add =itemView.findViewById( R.id.addbtn );
            variantList=new ArrayList<>();
            attributeList=new ArrayList<>();
            wish_before.setOnClickListener(this);
            wish_after.setOnClickListener(this );
            image_list=new ArrayList<>();
            rel_varient.setOnClickListener(this);
            db_wish = new WishlistHandler(context );
            db_cart = new DatabaseCartHandler( context );
            sessionManagement = new Session_management( context );

            user_id=sessionManagement.getUserDetails().get(KEY_ID);
module = new Module(context);

            relative_layout.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            final int position = getAdapterPosition();


            if(id== R.id.relative_layout)
            {



                Details_Fragment details_fragment=new Details_Fragment();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Bundle args = new Bundle();
//
//               //Intent intent=new Intent(context, Product_details.class);
                args.putString("cat_id", modelList.get(position).getCategory_id());
                args.putString("product_id",modelList.get(position).getProduct_id());
                args.putString("product_images",modelList.get(position).getProduct_image());
                args.putString("product_name",modelList.get(position).getProduct_name());
                args.putString("product_description",modelList.get(position).getProduct_description());
                args.putString("stock",modelList.get(position).getStock());
                args.putString("in_stock",modelList.get(position).getIn_stock());
                args.putString("price",modelList.get(position).getPrice());
                args.putString("mrp",modelList.get(position).getMrp());
                args.putString("unit_value",modelList.get(position).getUnit_value());
                args.putString("unit",modelList.get(position).getUnit());
                args.putString("product_attribute",modelList.get(position).getProduct_attribute());
                args.putString("rewards",modelList.get(position).getRewards());
                args.putString("increment",modelList.get(position).getIncreament());
                args.putString("title",modelList.get(position).getTitle());
                args.putString("product_name_hindi", modelList.get(position).getProduct_name_hindi());
                details_fragment.setArguments(args);


                FragmentManager fragmentManager=activity.getFragmentManager();
                fragmentManager.beginTransaction().replace( R.id.contentPanel,details_fragment)
//
                        .addToBackStack(null).commit();

            }
            else if(id== R.id.rel_variant)
            {
                //  AlertDialog dlg=null;
                float stock=Float.parseFloat(modelList.get(position).getStock());
                if(stock<=0 || modelList.get(position).getIn_stock().equals("0")) {
                }
                else {
                    final Product_model mList = modelList.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View row = layoutInflater.inflate(R.layout.dialog_vairant_layout, null);
                    variantList.clear();
                    String atr = String.valueOf(mList.getProduct_attribute());
                    JSONArray jsonArr = null;
                    try {

                        jsonArr = new JSONArray(atr);
                        for (int i = 0; i < jsonArr.length(); i++) {
                            ProductVariantModel model = new ProductVariantModel();
                            JSONObject jsonObj = jsonArr.getJSONObject(i);
                            String atr_id = jsonObj.getString("id");
                            String atr_product_id = jsonObj.getString("product_id");
                            String attribute_name = jsonObj.getString("attribute_name");
                            String attribute_value = jsonObj.getString("attribute_value");
                            String attribute_mrp = jsonObj.getString("attribute_mrp");


                            model.setId(atr_id);
//                            model.setProduct_id(atr_product_id);
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
                    ListView l1 = (ListView) row.findViewById(R.id.list_view_varaint);
                    productVariantAdapter = new ProductVariantAdapter(context, variantList);
                    //productVariantAdapter.notifyDataSetChanged();
                    l1.setAdapter(productVariantAdapter);


                    builder.setView(row);
                    final AlertDialog ddlg = builder.create();
                    ddlg.show();
                    l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                            dialog_unit_type.setText("\u20B9" + variantList.get(i).getAttribute_value() + "/" + variantList.get(i).getAttribute_name());
                            dialog_txtId.setText(variantList.get(i).getId() + "@" + i);
                            dialog_txtVar.setText(variantList.get(i).getAttribute_value() + "@" + variantList.get(i).getAttribute_name() + "@" + variantList.get(i).getAttribute_mrp());
                            //    txtPer.setText(String.valueOf(df)+"% off");
                            String pr = String.valueOf(variantList.get(i).getAttribute_value());
                            String mr = String.valueOf(variantList.get(i).getAttribute_mrp());
                            int m = Integer.parseInt(mr);
                            int p = Integer.parseInt(pr);
                            product_price.setText("\u20B9" + variantList.get(i).getAttribute_value().toString());
                            if (m > p) {
                                product_mrp.setText("\u20B9" + variantList.get(i).getAttribute_mrp().toString());

                                int atr_dis = getDiscount(pr, mr);
                                product_discount.setText("" + atr_dis + "% "+"\n OFF");
                                iv_dis.setVisibility(View.VISIBLE);
                            } else
                                product_mrp.setVisibility(View.GONE);
                            product_discount.setVisibility(View.GONE);
                            iv_dis.setVisibility(View.GONE);
                            String atr = String.valueOf(modelList.get(position).getProduct_attribute());
                            String product_id = String.valueOf(modelList.get(position).getProduct_id());
                            if (atr.equals("[]")) {
                                boolean st = db_cart.isInCart(product_id);
                                if (st == true) {
                                    add.setVisibility(View.GONE);
                                    elegantNumberButton.setNumber(db_cart.getCartItemQty(product_id));
                                    elegantNumberButton.setVisibility(View.VISIBLE);
                                } else {
                                    add.setVisibility(View.VISIBLE);
                                    elegantNumberButton.setVisibility(View.GONE);
                                }
                            } else {
                                String str_id = dialog_txtId.getText().toString();
                                String[] str = str_id.split("@");
                                String at_id = String.valueOf(str[0]);
                                boolean st = db_cart.isInCart(at_id);
                                if (st == true) {
                                    add.setVisibility(View.GONE);
                                    elegantNumberButton.setNumber(db_cart.getCartItemQty(at_id));
                                    elegantNumberButton.setVisibility(View.VISIBLE);
                                } else {
                                    add.setVisibility(View.VISIBLE);
                                    elegantNumberButton.setVisibility(View.GONE);
                                }
                            }
                            elegantNumberButton.setNumber("1");
                            elegantNumberButton.setVisibility(View.GONE);
                            add.setVisibility(View.VISIBLE);
                            ddlg.dismiss();
                        }
                    });


                }
            }

            else if(id== R.id.wish_before) {

//                    float stck=Float.parseFloat(modelList.get(position).getStock());
//                    if (stck<1 || modelList.get(position).getIn_stock().equals("0"))
//                    {
//                        Toast.makeText( context,"Out Of Stock",Toast.LENGTH_LONG ).show();
//                    }
//                    else {
                        final Product_model mList = modelList.get( position );
                        wish_after.setVisibility( View.VISIBLE );
                        wish_before.setVisibility( View.INVISIBLE );


//                    module.addToWishlist(context,mList.getProduct_id(),mList.getProduct_image(),mList.getCategory_id(),mList.getProduct_name(),mList.getPrice(),
//                            mList.getProduct_description(),mList.getRewards(),mList.getUnit_value(),mList.getUnit(),mList.getIncreament(),mList.getStock(),
//                            mList.getIn_stock(),mList.getTitle(),mList.getMrp(),mList.getProduct_attribute(),user_id,mList.getProduct_name_hindi());

                module.addToWishlist(context,mList.getProduct_id(),mList.getProduct_image(),mList.getCategory_id(),mList.getProduct_name(),module.checkNullNumber(mList.getPrice()),
                        mList.getProduct_description(),mList.getRewards(),module.checkNullNumber(mList.getUnit_value()),mList.getUnit(),mList.getIncreament(),module.checkNullNumber(mList.getStock()),
                        mList.getIn_stock(),mList.getTitle(),module.checkNullNumber(mList.getMrp()),mList.getProduct_attribute(),sessionManagement.getSessionItem(KEY_ID),mList.getProduct_name_hindi(),
                        module.checkNullString(mList.getProduct_name_arb()),module.checkNullString(mList.getProduct_description_arb()),module.checkNullString(mList.getDeal_price()),
                        module.checkNullString(mList.getStart_date()),module.checkNullString(mList.getStart_time()),module.checkNullString(mList.getEnd_date()),module.checkNullString(mList.getEnd_time()),
                        module.checkNullString(mList.getStatus()), module.checkNullString(mList.getSize()),module.checkNullString(mList.getColor()),module.checkNullString(mList.getCity()));
//                    }


                //   Toast.makeText(context,"wish",Toast.LENGTH_LONG).show();
                //  AppCompatActivity activity = (AppCompatActivity) view.getContext();

            }
            else if (id == R.id.wish_after) {
                final Product_model mList = modelList.get(position);
                wish_after.setVisibility( View.INVISIBLE );
                wish_before.setVisibility( View.VISIBLE );
                db_wish.removeItemFromWishtable(mList.getProduct_id(),user_id);

                Toast.makeText(context, "removed from Wishlist", Toast.LENGTH_LONG).show();
                // list.remove(position);
                module.updatewishintent();
                notifyDataSetChanged();

            }


        }
    }
    public int getDiscount(String price, String mrp)
    {
        double mrp_d= Double.parseDouble(mrp);
        double price_d= Double.parseDouble(price);
        double per=((mrp_d-price_d)/mrp_d)*100;
        double df= Math.round(per);
        int d=(int)df;
        return d;
    }

    private void updateintent() {
        Intent updates = new Intent("Grocery_cart");
        updates.putExtra("type", "update");
        context.sendBroadcast(updates);
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

}

