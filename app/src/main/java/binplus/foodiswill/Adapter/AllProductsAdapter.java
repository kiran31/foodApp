package binplus.foodiswill.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.HashMap;

import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Config.Module;
import binplus.foodiswill.MainActivity;
import binplus.foodiswill.Model.ProductAttributeModel;
import binplus.foodiswill.Model.ProductVariantModel;
import binplus.foodiswill.Model.Product_model;
import binplus.foodiswill.R;
import binplus.foodiswill.util.DatabaseCartHandler;
import binplus.foodiswill.util.Session_management;
import binplus.foodiswill.util.WishlistHandler;

import static binplus.foodiswill.Config.BaseURL.KEY_ID;

public class AllProductsAdapter extends RecyclerView.Adapter<AllProductsAdapter.ViewHolder> {
    ArrayList<Product_model>productList;
    Activity activity ;
    String type ;
    private DatabaseCartHandler db_cart;
    WishlistHandler db_wish;
    Session_management sessionManagement ;
    Module module ;
    float stock =0;
    int selectIndex =0;
    int stk=0;
    public AllProductsAdapter(ArrayList<Product_model> productList, Activity activity, String type) {
        this.productList = productList;
        this.activity = activity;
        this.type = type;
        db_cart = new DatabaseCartHandler(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v = null;
       if (type.equalsIgnoreCase("all"))
       {
           v = LayoutInflater.from(activity).inflate(R.layout.row_top_selling,null);

       }
       else
       {
           v = LayoutInflater.from(activity).inflate(R.layout.row_top_product_rv,null);

       }
       return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Product_model mList = productList.get(position);
        final ArrayList<ProductAttributeModel> attrList = new ArrayList<>();
        String img_array = mList.getProduct_image();
        String img_name = null;
        try {
            JSONArray array = new JSONArray(img_array);
            img_name = array.get(0).toString();
            Picasso.with(activity).load(BaseURL.IMG_PRODUCT_URL + img_name).into(holder.image, new Callback() {
                @Override
                public void onSuccess() {
                    holder.pb_bar.setVisibility(View.GONE);
                    holder.image.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {

                }
            });
            holder.tv_mrp.setPaintFlags( holder.tv_mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tv_name.setText(mList.getProduct_name());

            if(db_wish.isInWishtable(mList.getProduct_id(), sessionManagement.getSessionItem(KEY_ID)))
            {
                holder.wish_after.setVisibility( View.VISIBLE);
                holder.wish_before.setVisibility( View.GONE );

            }
            else
            {
                holder.wish_after.setVisibility( View.GONE);
                holder.wish_before.setVisibility( View.VISIBLE );
            }
            String atr= String.valueOf(mList.getProduct_attribute());
            if (atr.equals("[]"))
            {
                stock= Float.parseFloat(mList.getStock());
                holder.tv_mrp.setText(activity.getResources().getString(R.string.currency)+mList.getMrp());
                holder.tv_price.setText(activity.getResources().getString(R.string.currency)+mList.getPrice());
                holder.txtrate.setText(mList.getUnit_value() +" "+mList.getUnit());
                int dis = module.getDiscount(mList.getPrice(),mList.getMrp());
                if (dis>0) {
                    holder.tv_discount.setText(dis+" % \n OFF");
                }
                else
                {
                    holder.tv_discount.setVisibility(View.GONE);
                    holder.tv_mrp.setVisibility(View.GONE);
                    holder.iv_dis.setVisibility(View.GONE);
                }
            }
            else
            {
                JSONArray jsonArr = null;

                try {
//\"id\":\"704\",\"product_id\":\"648\",\"attribute_name\":\"1KG\",\"attribute_value\":\"18\",\"attribute_mrp\":\"30\",\"stock_value\":\"1\",\"city\":\"[\\\"23\\\"]\"
                    jsonArr = new JSONArray(atr);
                    for (int i = 0 ;i<jsonArr.length();i++) {
                       ProductAttributeModel model = new ProductAttributeModel();
                        JSONObject jsonObj = jsonArr.getJSONObject(i);

                        model.setId(jsonObj.getString("id"));
//                        model.setProductId(jsonObj.getString("product_id"));
                        model.setAttributeName(jsonObj.getString("attribute_name"));
                        model.setAttributeValue(jsonObj.getString("attribute_value"));
                        model.setAttributeMrp(jsonObj.getString("attribute_mrp"));
                        model.setDealPrice(jsonObj.getString("deal_price"));
                        model.setDeal_qty(jsonObj.getString("deal_qty"));
                        model.setIs_deal(jsonObj.getString("is_deal"));
//                        model.setStockValue(jsonObj.getString("stock_value"));
//                        model.setCity(jsonObj.getString("city"));



                        //"product_attribute":"[{\"id\":\"568\",\"attribute_name\":\"10KG\",\"attribute_value\":\"355\"," +
//		"\"attribute_mrp\":\"355\",\"deal_price\":null,\"deal_qty\":null,\"is_deal\":\"0\"}]


                        attrList.add(model);

                    }
                    Log.e("attrList",attrList.size()+"\n"+attrList.toString());
//                    stock= Float.parseFloat(attrList.get(0).getStockValue());
                    stock= Float.parseFloat(module.checkNullNumber(attrList.get(0).getDeal_qty()));
                    holder.tv_mrp.setText(activity.getResources().getString(R.string.currency)+attrList.get(0).getAttributeMrp());
                    holder.tv_price.setText(activity.getResources().getString(R.string.currency)+attrList.get(0).getAttributeValue());
                    holder.txtrate.setVisibility(View.GONE);
                    holder.rel_variant.setVisibility(View.VISIBLE);
                    holder.dialog_unit_type.setText(attrList.get(0).getAttributeName());
                    holder.txtVar.setText(attrList.get(0).getAttributeValue()+"@"+attrList.get(0).getAttributeName()+"@"+attrList.get(0).getAttributeMrp());
                    int dis = module.getDiscount(attrList.get(0).getAttributeValue(),attrList.get(0).getAttributeMrp());
                    if (dis>0) {
                        holder.tv_discount.setText(dis+" % \n OFF");
                    }
                    else
                    {
                        holder.tv_discount.setVisibility(View.GONE);
                        holder.tv_mrp.setVisibility(View.GONE);
                        holder.iv_dis.setVisibility(View.GONE);
                    }
                    holder.dialog_txtId.setText(attrList.get(0).getId().toString()+"@"+"0");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            holder.elegantNumberButton.setRange(0,(int)stock);
            if(stock<=0 || mList.getIn_stock().equals("0"))
            {
                holder.rel_out.setVisibility( View.VISIBLE);
            }
            else
            {
                holder.rel_out.setVisibility( View.GONE);
            }

            holder.rel_variant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(activity);
                    dialog.setContentView(R.layout.dialog_vairant_layout);
                    ListView l1 = dialog.findViewById(R.id.list_view_varaint);
                  ProductVareintAdapter adapter = new ProductVareintAdapter(attrList,activity);
                  l1.setAdapter(adapter);
                  l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                      @Override
                      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                          selectIndex = position;
//                          stock= Float.parseFloat(attrList.get(position).getStockValue());
                          stock= Float.parseFloat(module.checkNullNumber(attrList.get(0).getDeal_qty()));
                          holder.tv_mrp.setText(activity.getResources().getString(R.string.currency)+attrList.get(position).getAttributeMrp());
                          if (attrList.get(position).getIs_deal().equals("1")){
                              holder.tv_price.setText(activity.getResources().getString(R.string.currency)+attrList.get(position).getDealPrice());
                          }else {
                              holder.tv_price.setText(activity.getResources().getString(R.string.currency)+attrList.get(position).getAttributeValue());
                          }


//                          holder.dialog_unit_type.setText(attrList.get(position).getAttributeName());
                          holder.dialog_unit_type.setText(""+attrList.get(position).getAttributeName());
                          CookieHandler variantList;
                          holder.dialog_txtId.setText(attrList.get(0).getId() + "@" + position);

                          holder.txtVar.setText(attrList.get(0).getAttributeValue() + "@" + attrList.get(0).getAttributeName() + "@" + attrList.get(position).getAttributeMrp());

                          stk = Integer.parseInt(module.checkNullNumber(attrList.get(position).getDeal_qty()));

                          int dis = module.getDiscount(attrList.get(position).getAttributeValue(),attrList.get(position).getAttributeMrp());
                          if (dis>0) {
                              holder.tv_discount.setText(dis+" % \n OFF");
                          }
                          else
                          {
                              holder.tv_discount.setVisibility(View.GONE);
                              holder.tv_mrp.setVisibility(View.GONE);
                              holder.iv_dis.setVisibility(View.GONE);
                          }


                          String atr = String.valueOf(productList.get(position).getProduct_attribute());
                          String product_id = String.valueOf(productList.get(position).getProduct_id());
                          if (atr.equals("[]")) {
                              boolean st = db_cart.isInCart(product_id);
                              if (st == true) {
                                  holder.add_Button.setVisibility(View.GONE);
                                  holder.elegantNumberButton.setNumber(db_cart.getCartItemQty(product_id));
                                  holder.elegantNumberButton.setVisibility(View.VISIBLE);
                              } else {
                                  holder.add_Button.setVisibility(View.VISIBLE);
                                  holder.elegantNumberButton.setVisibility(View.GONE);
                              }
                          }
                          else {
                              String str_id = holder.dialog_txtId.getText().toString();
                              String[] str = str_id.split("@");
                              String at_id = String.valueOf(str[0]);
                              boolean st = db_cart.isInCart(at_id);
                              if (st == true) {
                                  holder.add_Button.setVisibility(View.GONE);
                                  holder.elegantNumberButton.setNumber(db_cart.getCartItemQty(at_id));
                                  holder.elegantNumberButton.setVisibility(View.VISIBLE);
                              } else {
                                  holder.add_Button.setVisibility(View.VISIBLE);
                                  holder.elegantNumberButton.setVisibility(View.GONE);
                              }
                          }

                          dialog.dismiss();
                      }
                  });
                  dialog.show();
                  notifyDataSetChanged();
                }
            });


//            holder.add_Button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    float qty = Float.parseFloat( holder.elegantNumberButton.getNumber() );
//
//                    Log.e("check_quantity",qty+"\n"+stk);
//                    if (qty>stk)
//                    {
//                        Toast.makeText(activity, "Out Of Stock", Toast.LENGTH_LONG).show();
//                    }else {
//                        module.addToCart(activity,mList.getProduct_id(),mList.getProduct_image(),mList.getCategory_id(),mList.getProduct_name(),module.checkNullNumber(mList.getPrice()),
//                                mList.getProduct_description(),mList.getRewards(),module.checkNullNumber(mList.getUnit_value()),mList.getUnit(),mList.getIncreament(),module.checkNullNumber(mList.getStock()),
//                                mList.getIn_stock(),mList.getTitle(),module.checkNullNumber(mList.getMrp()),mList.getProduct_attribute(),sessionManagement.getSessionItem(KEY_ID),mList.getProduct_name_hindi(),"1","p","0");
//
//
//                    }
//                }
//            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


        holder.wish_before.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.wish_after.setVisibility( View.VISIBLE );
                holder.wish_before.setVisibility( View.INVISIBLE );


//                module.addToWishlist(activity,mList.getProduct_id(),mList.getProduct_image(),mList.getCategory_id(),mList.getProduct_name(),module.checkNullNumber(mList.getPrice()),
//                        mList.getProduct_description(),mList.getRewards(),module.checkNullNumber(mList.getUnit_value()),mList.getUnit(),mList.getIncreament(),module.checkNullNumber(mList.getStock()),
//                        mList.getIn_stock(),mList.getTitle(),module.checkNullNumber(mList.getMrp()),mList.getProduct_attribute(),sessionManagement.getSessionItem(KEY_ID),mList.getProduct_name_hindi());

                module.addToWishlist(activity,mList.getProduct_id(),mList.getProduct_image(),mList.getCategory_id(),mList.getProduct_name(),module.checkNullNumber(mList.getPrice()),
                        mList.getProduct_description(),mList.getRewards(),module.checkNullNumber(mList.getUnit_value()),mList.getUnit(),mList.getIncreament(),module.checkNullNumber(mList.getStock()),
                        mList.getIn_stock(),mList.getTitle(),module.checkNullNumber(mList.getMrp()),mList.getProduct_attribute(),sessionManagement.getSessionItem(KEY_ID),mList.getProduct_name_hindi(),
                       module.checkNullString(mList.getProduct_name_arb()),module.checkNullString(mList.getProduct_description_arb()),module.checkNullString(mList.getDeal_price()),
                        module.checkNullString(mList.getStart_date()),module.checkNullString(mList.getStart_time()),module.checkNullString(mList.getEnd_date()),module.checkNullString(mList.getEnd_time()),
                        module.checkNullString(mList.getStatus()), module.checkNullString(mList.getSize()),module.checkNullString(mList.getColor()),module.checkNullString(mList.getCity()));
            }
        } );

        holder.wish_after.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.wish_after.setVisibility( View.INVISIBLE );
                holder.wish_before.setVisibility( View.VISIBLE );
                db_wish.removeItemFromWishtable(mList.getProduct_id(),sessionManagement.getSessionItem(KEY_ID));
                Toast.makeText(activity, "Removed from Wishlist", Toast.LENGTH_LONG).show();

                module.updatewishintent();
                notifyDataSetChanged();

            }
        } );

        String atr= String.valueOf(mList.getProduct_attribute());
        final String product_id= String.valueOf(mList.getProduct_id());
        if(atr.equals("[]"))
        {
            boolean st=db_cart.isInCart(product_id);
            if(st==true)
            {
                holder.add_Button.setVisibility( View.GONE);
                holder.elegantNumberButton.setNumber(db_cart.getCartItemQty(product_id));
                holder.elegantNumberButton.setVisibility( View.VISIBLE);
            }
            else
            {
                holder.add_Button.setVisibility( View.VISIBLE );
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
                holder.add_Button.setVisibility( View.GONE);
                holder.elegantNumberButton.setNumber(String.valueOf(db_cart.getCartItemQty(at_id)));
                holder.elegantNumberButton.setVisibility( View.VISIBLE);
            }
            else {
                holder.add_Button.setVisibility( View.VISIBLE );
                holder.elegantNumberButton.setVisibility( View.GONE );

            }
        }



        holder.add_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                float qty = Float.parseFloat( holder.elegantNumberButton.getNumber() );

//                Log.e("check_quantity",qty+"\n"+stk);
//                if (qty>stk)
//                {
//                    Toast.makeText(activity, "Out Of Stock", Toast.LENGTH_LONG).show();
//                }else {
//
//                }

                float stck = Float.parseFloat(mList.getStock());
                if (stck <= 0 || mList.getIn_stock().equals("0")) {
                    Toast.makeText(activity, "Out Of Stock", Toast.LENGTH_LONG).show();
                } else {
                    holder.add_Button.setVisibility(View.GONE);
                    holder.elegantNumberButton.setVisibility(View.VISIBLE);
                    holder.elegantNumberButton.setNumber(String.valueOf(1));
                    Product_model mList = productList.get(position);
                    String atr = String.valueOf(productList.get(position).getProduct_attribute());
                    if (atr.equals( "[]" )) {
                        HashMap<String, String> mapProduct = new HashMap<String, String>();
                        String unt = String.valueOf( mList.getUnit_value() + " " + mList.getUnit() );
                        mapProduct.put( "cart_id", mList.getProduct_id() );
                        mapProduct.put( "product_id", mList.getProduct_id() );
                        mapProduct.put( "product_name", mList.getProduct_name() );
                        mapProduct.put( "product_name_hindi", mList.getProduct_name_hindi() );
                        mapProduct.put( "product_name_arb", mList.getProduct_name_arb() );
                        mapProduct.put( "product_description_arb", mList.getProduct_description_arb() );
                        mapProduct.put( "category_id", mList.getCategory_id() );
                        mapProduct.put( "product_description", mList.getProduct_description() );
                        mapProduct.put( "deal_price", mList.getDeal_price() );
                        mapProduct.put( "start_date", mList.getStart_date() );
                        mapProduct.put( "start_time", mList.getStart_time() );
                        mapProduct.put( "end_date", mList.getEnd_date() );
                        mapProduct.put( "end_time", mList.getEnd_time() );
                        mapProduct.put( "price", mList.getPrice() );
                        mapProduct.put( "mrp", mList.getMrp() );
                        mapProduct.put( "product_image", mList.getProduct_image() );
                        mapProduct.put( "status", mList.getStatus() );
                        mapProduct.put( "in_stock", mList.getIn_stock() );
                        mapProduct.put( "unit_value", mList.getUnit_value() );
                        mapProduct.put( "unit", unt);
                        mapProduct.put( "increament", mList.getIncreament() );
                        mapProduct.put( "rewards", mList.getRewards() );
                        mapProduct.put( "stock", mList.getStock() );
                        mapProduct.put( "size", mList.getSize() );
                        mapProduct.put( "color", mList.getColor() );
                        mapProduct.put( "city", mList.getCity() );
                        mapProduct.put( "title", mList.getTitle() );
                        mapProduct.put( "cat_id", mList.getCategory_id() );
                        mapProduct.put("type", "p");

                            try {

                            boolean tr = db_cart.setCart( mapProduct, (float) 1 );
                            if (tr == true) {
                                MainActivity mainActivity = new MainActivity();
                                mainActivity.setCartCounter( "" + db_cart.getCartCount() );

                                //   context.setCartCounter("" + holder.db_cart.getCartCount());
                                Toast.makeText( activity, "Added to Cart", Toast.LENGTH_LONG ).show();
                                int n = db_cart.getCartCount();
                                updateintent();


                            } else if (tr == false) {
                                Toast.makeText( activity, "cart updated", Toast.LENGTH_LONG ).show();
                            }

                        } catch (Exception ex) {
                            Toast.makeText( activity, "" + ex.getMessage(), Toast.LENGTH_LONG ).show();
                        }

                        //Toast.makeText(context,"1\n"+status+"\n"+modelList.get(position).getProduct_attribute(),Toast.LENGTH_LONG).show();
                    } else {
                        //ProductVariantModel model=variantList.get(position);

                        String str_id = holder.dialog_txtId.getText().toString();
                        String s = holder.txtVar.getText().toString();
                        String[] st = s.split("@");
                        String st0 = String.valueOf(st[0]);
                        String st1 = String.valueOf(st[1]);
                        String st2 = String.valueOf(st[2]);
                        String[] str = str_id.split("@");
                        String at_id = String.valueOf(str[0]);
                        int j = Integer.parseInt(String.valueOf(str[1]));
                        //       Toast.makeText(context,""+str[0].toString()+"\n"+str[1].toString(),Toast.LENGTH_LONG).show();
                        HashMap<String, String> mapProduct = new HashMap<String, String>();

                        mapProduct.put( "cart_id", at_id );
                        mapProduct.put( "product_id", mList.getProduct_id() );
                        mapProduct.put( "product_name", module.checkNullString(mList.getProduct_name()) );
                        mapProduct.put( "product_name_hindi", mList.getProduct_name_hindi() );
                        mapProduct.put( "product_name_arb", module.checkNullString(mList.getProduct_name_arb()) );
                        mapProduct.put( "product_description_arb", module.checkNullString(mList.getProduct_description_arb()) );
                        mapProduct.put( "category_id", module.checkNullString(mList.getCategory_id()) );
                        mapProduct.put( "product_description", module.checkNullString(mList.getProduct_description()) );
                        mapProduct.put( "deal_price", module.checkNullString(mList.getDeal_price()) );
                        mapProduct.put( "start_date", module.checkNullString(mList.getStart_date()) );
                        mapProduct.put( "start_time", module.checkNullString(mList.getStart_time()) );
                        mapProduct.put( "end_date", module.checkNullString(mList.getEnd_date()) );
                        mapProduct.put( "end_time", module.checkNullString(mList.getEnd_time()) );
                        mapProduct.put( "price", st0 );
                        mapProduct.put( "mrp", st2 );
                        mapProduct.put( "product_image", module.checkNullString(mList.getProduct_image()) );
                        mapProduct.put( "status", module.checkNullString(mList.getStatus()) );
                        mapProduct.put( "in_stock", module.checkNullString(mList.getIn_stock()) );
                        mapProduct.put( "unit_value", st0 );
                        mapProduct.put( "unit", st1);
                        mapProduct.put( "increament", module.checkNullString(mList.getIncreament()) );
                        mapProduct.put( "rewards", module.checkNullString(mList.getRewards()) );
                        mapProduct.put( "stock", module.checkNullString(mList.getStock()) );
                        mapProduct.put( "size",module.checkNullString(mList.getSize()) );
                        mapProduct.put( "color", module.checkNullString(mList.getColor()) );
                        mapProduct.put( "city",module.checkNullString(mList.getCity()) );
                        mapProduct.put( "title", module.checkNullString(mList.getTitle()) );
                        mapProduct.put( "cat_id", module.checkNullString(mList.getCategory_id()) );
                        Log.e("all_product_adapter",mapProduct.toString());
                        mapProduct.put("type", "a");
                        //  Toast.makeText(context,""+attributeList.get(j).getId()+"\n"+mapProduct,Toast.LENGTH_LONG).show();
                        try {

                            boolean tr = db_cart.setCart(mapProduct, (float) 1);
                            if (tr == true) {
                                MainActivity mainActivity = new MainActivity();
                                mainActivity.setCartCounter("" + db_cart.getCartCount());

                                //   context.setCartCounter("" + holder.db_cart.getCartCount());
                                Toast.makeText(activity, "Added to Cart", Toast.LENGTH_LONG).show();
                                int n = db_cart.getCartCount();
                                updateintent();


                            } else if (tr == false) {
                                Toast.makeText(activity, "cart updated", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception ex) {
                            Toast.makeText(activity, "" + ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    updateintent();

                }


            }
        });

        holder.elegantNumberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                final Product_model mList = productList.get(position);
                float qty = Float.parseFloat( holder.elegantNumberButton.getNumber() );
                String atr = String.valueOf( productList.get( position ).getProduct_attribute() );
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
                    holder.add_Button.setVisibility( View.VISIBLE);
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
                        mapProduct.put( "product_name", mList.getProduct_name() );
                        mapProduct.put( "product_name_hindi", mList.getProduct_name_hindi() );
                        mapProduct.put( "product_name_arb", mList.getProduct_name_arb() );
                        mapProduct.put( "product_description_arb", mList.getProduct_description_arb() );
                        mapProduct.put( "category_id", mList.getCategory_id() );
                        mapProduct.put( "product_description", mList.getProduct_description() );
                        mapProduct.put( "deal_price", mList.getDeal_price() );
                        mapProduct.put( "start_date", mList.getStart_date() );
                        mapProduct.put( "start_time", mList.getStart_time() );
                        mapProduct.put( "end_date", mList.getEnd_date() );
                        mapProduct.put( "end_time", mList.getEnd_time() );
                        mapProduct.put( "price",  String.valueOf( amt ) );
                        mapProduct.put( "mrp", mList.getMrp() );
                        mapProduct.put( "product_image", mList.getProduct_image() );
                        mapProduct.put( "status", mList.getStatus() );
                        mapProduct.put( "in_stock", mList.getIn_stock() );
                        mapProduct.put( "unit_value", mList.getPrice() );
                        mapProduct.put( "unit", unt);
                        mapProduct.put( "increament", mList.getIncreament() );
                        mapProduct.put( "rewards", mList.getRewards() );
                        mapProduct.put( "stock", mList.getStock() );
                        mapProduct.put( "size", mList.getSize() );
                        mapProduct.put( "color", mList.getColor() );
                        mapProduct.put( "city", mList.getCity() );
                        mapProduct.put( "title", mList.getTitle() );
                        mapProduct.put( "cat_id", mList.getCategory_id() );
                        mapProduct.put( "type", "p" );
                        try {

                            boolean tr = db_cart.setCart( mapProduct, qty );
                            if (tr == true) {
                                MainActivity mainActivity = new MainActivity();
                                mainActivity.setCartCounter( "" + db_cart.getCartCount() );

                                //   context.setCartCounter("" + holder.db_cart.getCartCount());
                                Toast.makeText( activity, "Added to Cart", Toast.LENGTH_LONG ).show();
                                int n = db_cart.getCartCount();
                                updateintent();


                            } else if (tr == false) {
                                Toast.makeText( activity, "cart updated", Toast.LENGTH_LONG ).show();
                            }

                        } catch (Exception ex) {
                            Toast.makeText( activity, "" + ex.getMessage(), Toast.LENGTH_LONG ).show();
                        }

                        //Toast.makeText(context,"1\n"+status+"\n"+modelList.get(position).getProduct_attribute(),Toast.LENGTH_LONG).show();
                    } else {
                        //ProductVariantModel model=variantList.get(position);

                        String str_id = holder.dialog_txtId.getText().toString();


                        String s = holder.txtVar.getText().toString();
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
                        mapProduct.put( "product_name", module.checkNullString(mList.getProduct_name()) );
                        mapProduct.put( "product_name_hindi", module.checkNullString(mList.getProduct_name_hindi()) );
                        mapProduct.put( "product_name_arb", module.checkNullString(mList.getProduct_name_arb()) );
                        mapProduct.put( "product_description_arb", module.checkNullString(mList.getProduct_description_arb()) );
                        mapProduct.put( "category_id", module.checkNullString(mList.getCategory_id()) );
                        mapProduct.put( "product_description", module.checkNullString(mList.getProduct_description()) );
                        mapProduct.put( "deal_price", module.checkNullString(mList.getDeal_price()) );
                        mapProduct.put( "start_date", module.checkNullString(mList.getStart_date()) );
                        mapProduct.put( "start_time", module.checkNullString(mList.getStart_time()) );
                        mapProduct.put( "end_date", module.checkNullString(mList.getEnd_date()) );
                        mapProduct.put( "end_time", module.checkNullString(mList.getEnd_time()) );
                        mapProduct.put( "price",  String.valueOf( amt ) );
                        mapProduct.put( "mrp", st2 );
                        mapProduct.put( "product_image", module.checkNullString(mList.getProduct_image()) );
                        mapProduct.put( "status",module.checkNullString(mList.getStatus()) );
                        mapProduct.put( "in_stock", module.checkNullString(mList.getIn_stock()) );
                        mapProduct.put( "unit_value", st0 );
                        mapProduct.put( "unit", st1);
                        mapProduct.put( "increament", module.checkNullString(mList.getIncreament()) );
                        mapProduct.put( "rewards", module.checkNullString(mList.getRewards()) );
                        mapProduct.put( "stock", module.checkNullString(mList.getStock()) );
                        mapProduct.put( "size", module.checkNullString(mList.getSize()) );
                        mapProduct.put( "color", module.checkNullString(mList.getColor()) );
                        mapProduct.put( "city", module.checkNullString(mList.getCity()) );
                        mapProduct.put( "title", module.checkNullString(mList.getTitle()) );
                        mapProduct.put( "cat_id", module.checkNullString(mList.getCategory_id()) );
                        mapProduct.put( "type", "a" );
                        //  Toast.makeText(context,""+attributeList.get(j).getId()+"\n"+mapProduct,Toast.LENGTH_LONG).show();
                        try {

                            boolean tr = db_cart.setCart( mapProduct, qty );
                            if (tr == true) {
                                MainActivity mainActivity = new MainActivity();
                                mainActivity.setCartCounter( "" + db_cart.getCartCount() );

                                //   context.setCartCounter("" + holder.db_cart.getCartCount());
                                Toast.makeText( activity, "Added to Cart", Toast.LENGTH_LONG ).show();
                                int n = db_cart.getCartCount();
                                updateintent();


                            } else if (tr == false) {
                                Toast.makeText( activity, "cart updated", Toast.LENGTH_LONG ).show();
                            }

                        } catch (Exception ex) {
                            Toast.makeText( activity, "" + ex.getMessage(), Toast.LENGTH_LONG ).show();
                        }

                    }
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       TextView tv_name, tv_mrp ,tv_discount ,tv_price,txtVar,dialog_unit_type,dialog_txtId,txtrate;
      ImageView image , wish_before ,wish_after ,iv_dis;
        ElegantNumberButton elegantNumberButton ;
        Button add_Button;
       RelativeLayout rel_variant ,rel_add ,rel_out,relativeLayout;
        CardView card_view_top ;
        ProgressBar pb_bar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.product_name);
            tv_mrp = itemView.findViewById(R.id.product_mrp);
            tv_price = itemView.findViewById(R.id.product_price);
            tv_discount = itemView.findViewById(R.id.product_discount);
            txtVar = itemView.findViewById(R.id.txtVar);
           image = itemView.findViewById(R.id.iv_icon);
           pb_bar = itemView.findViewById(R.id.pb_bar);
          wish_after = itemView.findViewById(R.id.wish_after);
          wish_before = itemView.findViewById(R.id.wish_before);
          iv_dis = itemView.findViewById(R.id.iv_dis);
          add_Button = itemView.findViewById(R.id.btn_add);
          elegantNumberButton = itemView.findViewById(R.id.product_qty);
          rel_variant = itemView.findViewById(R.id.rel_variant);
          rel_add = itemView.findViewById(R.id.rel_add);
          rel_out = itemView.findViewById(R.id.rel_out);
          relativeLayout = itemView.findViewById(R.id.relative_top);
         card_view_top = itemView.findViewById(R.id.card_view_top);
            dialog_unit_type=(TextView)itemView.findViewById( R.id.unit_type);
            dialog_txtId=(TextView)itemView.findViewById( R.id.txtId);
            txtrate=(TextView)itemView.findViewById( R.id.single_varient);
            db_cart = new DatabaseCartHandler(activity);
            db_wish = new WishlistHandler(activity);
            module = new Module(activity);
            sessionManagement = new Session_management(activity);
        }
    }



    public class ProductVareintAdapter extends BaseAdapter{
        ArrayList<ProductAttributeModel>attributeModelArrayList;
        Activity activity ;

        public ProductVareintAdapter(ArrayList<ProductAttributeModel> attributeModelArrayList, Activity activity) {
            this.attributeModelArrayList = attributeModelArrayList;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return attributeModelArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return attributeModelArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(activity).inflate(R.layout.row_vairant_layout,null);
        TextView tv_vairent = v.findViewById(R.id.txtVarient);
        tv_vairent.setText(attributeModelArrayList.get(position).getAttributeName());
                return  v;
        }
    }
    private void updateintent() {
        Intent updates = new Intent("Grocery_cart");
        updates.putExtra("type", "update");
        activity.sendBroadcast(updates);
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
