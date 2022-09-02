package binplus.foodiswill.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Model.ProductVariantModel;
import binplus.foodiswill.Model.Product_model;
import binplus.foodiswill.MainActivity;
import binplus.foodiswill.R;
import binplus.foodiswill.util.DatabaseCartHandler;
import binplus.foodiswill.util.Session_management;

import static binplus.foodiswill.Config.BaseURL.KEY_ID;


public class Search_adapter extends RecyclerView.Adapter<Search_adapter.MyViewHolder>
        implements Filterable {
    String user_id="";
    int status=0;
    Session_management session_management;
    private List<Product_model> modelList;
    private List<Product_model> mFilteredList;
    ArrayList<ProductVariantModel> variantList;
    private Context context;
    private DatabaseCartHandler dbcart;
    ArrayList<ProductVariantModel> attributeList;
    ProductVariantAdapter productVariantAdapter;
    String atr_id="";
    String atr_product_id="";
    String attribute_name="";
    String attribute_value="";
    String attribute_mrp="";
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_title, tv_price, tv_reward, tv_total,unit_type, tv_contetiy, tv_add,tv_dis,tv_mrp;
        public ImageView iv_logo, iv_plus, iv_minus, iv_remove;
        RelativeLayout rel_stock,rel_variant;

        public MyViewHolder(View view) {
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_subcat_title);
            tv_price = (TextView) view.findViewById(R.id.tv_subcat_price);
            tv_reward = (TextView) view.findViewById(R.id.tv_reward_point);
            tv_total = (TextView) view.findViewById(R.id.tv_subcat_total);
            tv_contetiy = (TextView) view.findViewById(R.id.tv_subcat_contetiy);
            tv_add = (TextView) view.findViewById(R.id.tv_subcat_add);
            tv_dis = (TextView) view.findViewById(R.id.dis);
            tv_mrp = (TextView) view.findViewById(R.id.product_mrp);
            unit_type = (TextView) view.findViewById(R.id.unit_type);
            iv_logo = (ImageView) view.findViewById(R.id.iv_subcat_img);
            iv_plus = (ImageView) view.findViewById(R.id.iv_subcat_plus);
            iv_minus = (ImageView) view.findViewById(R.id.iv_subcat_minus);
            iv_remove = (ImageView) view.findViewById(R.id.iv_subcat_remove);
            rel_stock = (RelativeLayout) view.findViewById(R.id.rel_stock);
            rel_variant = (RelativeLayout) view.findViewById(R.id.rel_variant);
            session_management=new Session_management(context);
            variantList=new ArrayList<>();

            user_id=session_management.getUserDetails().get(KEY_ID);
            iv_remove.setVisibility(View.GONE);
            attributeList=new ArrayList<>();

            iv_minus.setOnClickListener(this);
            iv_plus.setOnClickListener(this);
            tv_add.setOnClickListener(this);
            iv_logo.setOnClickListener(this);
            rel_variant.setOnClickListener(this);
            CardView cardView = (CardView) view.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            final int position = getAdapterPosition();

            if (id == R.id.iv_subcat_plus) {

                int qty = Integer.valueOf(tv_contetiy.getText().toString());
                qty = qty + 1;

                tv_contetiy.setText(String.valueOf(qty));

            } else if (id == R.id.iv_subcat_minus) {

                int qty = 0;
                if (!tv_contetiy.getText().toString().equalsIgnoreCase(""))
                    qty = Integer.valueOf(tv_contetiy.getText().toString());

                if (qty > 0) {
                    qty = qty - 1;
                    tv_contetiy.setText(String.valueOf(qty));
                }

            } else if (id == R.id.tv_subcat_add) {

                HashMap<String, String> map = new HashMap<>();

                map.put("product_id", modelList.get(position).getProduct_id());
                map.put("product_name", modelList.get(position).getProduct_name());
                map.put("category_id",modelList.get(position).getCategory_id());
                map.put("product_description", modelList.get(position).getProduct_description());
                map.put("deal_price", modelList.get(position).getDeal_price());
                map.put("start_date", modelList.get(position).getStart_date());
                map.put("start_time", modelList.get(position).getStart_time());
                map.put("end_date", modelList.get(position).getEnd_date());
                map.put("end_time", modelList.get(position).getEnd_time());
                map.put("price", modelList.get(position).getPrice());
                map.put("product_image", modelList.get(position).getProduct_image());
                map.put("status", modelList.get(position).getStatus());
                map.put("in_stock", modelList.get(position).getIn_stock());
                map.put("unit_value", modelList.get(position).getUnit_value());
                map.put("unit", modelList.get(position).getUnit());
                map.put("increament", modelList.get(position).getIncreament());
                map.put("rewards",modelList.get(position).getRewards());
                map.put("stock", modelList.get(position).getStock());
                map.put("title", modelList.get(position).getTitle());
                map.put( "product_name_hindi", modelList.get(position).getProduct_name_hindi() );
                map.put( "product_name_arb", modelList.get(position).getProduct_name_arb() );
                map.put( "product_description_arb", modelList.get(position).getProduct_description_arb() );
                map.put( "mrp", modelList.get(position).getMrp() );
                map.put( "size", modelList.get(position).getSize() );
                map.put( "color", modelList.get(position).getColor() );
                map.put( "city", modelList.get(position).getCity() );
                map.put( "cat_id", modelList.get(position).getCategory_id() );

//                map.put( "cart_id", mList.getProduct_id() );









                if (!tv_contetiy.getText().toString().equalsIgnoreCase("0")) {

                    if (dbcart.isInCart(map.get("product_id"))) {
                        dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
                    } else {
                        dbcart.setCart(map, Float.valueOf(tv_contetiy.getText().toString()));
                        tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
                    }
                } else {
                    dbcart.removeItemFromCart(map.get("product_id"));
                    tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
                }

                Double items = Double.parseDouble(dbcart.getInCartItemQty(map.get("product_id")));
                Double price = Double.parseDouble(map.get("price"));
                Double reward = Double.parseDouble(map.get("rewards"));
                tv_reward.setText("" + reward * items);

                tv_total.setText("" + price * items);
                ((MainActivity) context).setCartCounter("" + dbcart.getCartCount());

            } else if (id == R.id.iv_subcat_img) {

            } else if (id == R.id.rel_variant) {

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
//                            String atr_product_id = jsonObj.getString("product_id");
                            String attribute_name = jsonObj.getString("attribute_name");
                            String attribute_value = jsonObj.getString("attribute_value");
                            String attribute_mrp = jsonObj.getString("attribute_mrp");


                            model.setDeal_price(jsonObj.getString("deal_price"));
                            model.setDeal_qty(jsonObj.getString("deal_qty"));
                            model.setIs_deal(jsonObj.getString("is_deal"));
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


                            unit_type.setText("\u20B9" + variantList.get(i).getAttribute_value() + "/" + variantList.get(i).getAttribute_name());
//                            dialog_txtId.setText(variantList.get(i).getId() + "@" + i);
//                            dialog_txtVar.setText(variantList.get(i).getAttribute_value() + "@" + variantList.get(i).getAttribute_name() + "@" + variantList.get(i).getAttribute_mrp());
                            //    txtPer.setText(String.valueOf(df)+"% off");
                            String pr = String.valueOf(variantList.get(i).getAttribute_value());
                            String mr = String.valueOf(variantList.get(i).getAttribute_mrp());
                            int m = Integer.parseInt(mr);
                            int p = Integer.parseInt(pr);
                            tv_price.setText("\u20B9" + variantList.get(i).getAttribute_value().toString());
                            if (m > p) {
                                tv_mrp.setText("\u20B9" + variantList.get(i).getAttribute_mrp().toString());

                                int atr_dis = getDiscount(pr, mr);
                                tv_dis.setText("" + atr_dis + "%"+"\n OFF");
                            } else
                                tv_mrp.setVisibility(View.GONE);
                            tv_dis.setVisibility(View.GONE);
                            String atr = String.valueOf(modelList.get(position).getProduct_attribute());
                            String product_id = String.valueOf(modelList.get(position).getProduct_id());
                            ddlg.dismiss();
                        }
                    });


                }

            }

        }
    }

    public Search_adapter(List<Product_model> modelList, Context context) {
        this.modelList = modelList;
        this.mFilteredList = modelList;

        dbcart = new DatabaseCartHandler(context);
    }

    @Override
    public Search_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_search_rv, parent, false);

        context = parent.getContext();

        return new Search_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Search_adapter.MyViewHolder holder, int position) {
        Product_model mList = modelList.get(position);


        float stock=Float.parseFloat(mList.getStock());
        if(stock<=0)
        {
            holder.rel_stock.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.rel_stock.setVisibility(View.GONE);
        }
        String img_array= mList.getProduct_image();
        String img_name = null;
        try {
            JSONArray array=new JSONArray(img_array);
            img_name=array.get(0).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL +img_name)

                .placeholder(R.drawable.icon)
                .centerCrop()
//                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.iv_logo);

//        holder.tv_title.setText(mList.getProduct_name());
        if(mList.getProduct_name_hindi().isEmpty())
        {
            holder.tv_title.setText(mList.getProduct_name());
        }
        else
        {
            holder.tv_title.setText(mList.getProduct_name() +"\n( "+mList.getProduct_name_hindi()+" )");
        }
        holder.tv_reward.setText(mList.getRewards());
        // holder.tv_price.setText(context.getResources().getString(R.string.currency)+ mList.getPrice()+" / "+ mList.getUnit_value()+" "+mList.getUnit());

        String atr= String.valueOf(mList.getProduct_attribute());
        if(atr.equals("[]"))
        {


            status=1;

            String p= String.valueOf(mList.getPrice());
            String m= String.valueOf(mList.getMrp());
            int mm = Integer.parseInt( m );
            int pp = Integer.parseInt( p );
            holder.tv_price.setText(context.getResources().getString( R.string.currency)+ mList.getPrice());
            if(mm>pp) {
                holder.tv_mrp.setText( context.getResources().getString( R.string.currency ) + mList.getMrp() );
                int discount = getDiscount( p, m );
                //Toast.makeText(getActivity(),""+atr,Toast.LENGTH_LONG).show();
                holder.tv_dis.setText( "" + discount + "% OFF" );
            }
            else
            {
                holder.tv_mrp.setVisibility( View.GONE );
                holder.tv_dis.setVisibility( View.GONE );
            }
            //   holder.txtrate.setVisibility(View.VISIBLE);
            holder.rel_variant.setVisibility( View.GONE);
            //   holder.txtrate.setText(mList.getUnit_value()+" "+mList.getUnit());

        }

        else
        {

            status=2;
            attributeList.clear();
            holder.rel_variant.setVisibility( View.VISIBLE);
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


                    model.setDeal_price(jsonObj.getString("deal_price"));
                    model.setDeal_qty(jsonObj.getString("deal_qty"));
                    model.setIs_deal(jsonObj.getString("is_deal"));
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
                int atr_m = Integer.parseInt( atr_mrp.trim() );
                int atr_p = Integer.parseInt( atr_price.trim() );
                holder.tv_price.setText("\u20B9"+attribute_value.toString());
                if (atr_m>atr_p) {
                    int atr_dis = getDiscount( atr_price, atr_mrp );
                    holder.tv_dis.setText( "" + atr_dis + "% OFF" );

                    holder.tv_mrp.setText( "\u20B9" + attribute_mrp.toString() );
                }
                else
                {
                    holder.tv_dis.setVisibility( View.GONE );
                    holder.tv_mrp.setVisibility( View.GONE );
                }
                //  holder.txtTotal.setText("\u20B9"+String.valueOf(list_atr_value.get(0).toString()));
                holder.unit_type.setText("\u20B9"+attribute_value+"/"+attribute_name);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }


        }
        if (dbcart.isInCart(mList.getProduct_id())) {
            holder.tv_add.setText(context.getResources().getString(R.string.tv_pro_update));
            holder.tv_contetiy.setText(dbcart.getCartItemQty(mList.getProduct_id()));
        } else {
            holder.tv_add.setText(context.getResources().getString(R.string.tv_pro_add));
        }

        Double items = Double.parseDouble(dbcart.getInCartItemQty(mList.getProduct_id()));
        Double price = Double.parseDouble(mList.getPrice());
        Double reward = Double.parseDouble(mList.getRewards());
        holder.tv_reward.setText("" + reward * items);
        holder.tv_mrp.setPaintFlags( holder.tv_mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


//        holder.tv_total.setText("" + price * items);

    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = modelList;
                } else {

                    ArrayList<Product_model> filteredList = new ArrayList<>();

                    for (Product_model androidVersion : modelList) {

                        if (androidVersion.getProduct_name().toLowerCase().contains(charString)) {

                            filteredList.add(androidVersion);
                        }
                    }
                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<Product_model>) filterResults.values;
                notifyDataSetChanged();

            }
        };
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
}