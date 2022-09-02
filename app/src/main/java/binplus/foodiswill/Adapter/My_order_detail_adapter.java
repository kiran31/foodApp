package binplus.foodiswill.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Model.My_order_detail_model;
import binplus.foodiswill.R;



public class My_order_detail_adapter extends RecyclerView.Adapter<My_order_detail_adapter.MyViewHolder> {

  private List<My_order_detail_model> modelList;
  private Context context;
  ArrayList<String> image_list;


  public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_title, tv_price, tv_qty,tv_order_total;
    public ImageView iv_img;

    public MyViewHolder(View view) {
      super(view);
      tv_title = (TextView) view.findViewById(R.id.tv_order_Detail_title);
      tv_price = (TextView) view.findViewById(R.id.tv_order_Detail_price);
      tv_qty = (TextView) view.findViewById(R.id.tv_order_Detail_qty);
      tv_order_total = (TextView) view.findViewById(R.id.tv_order_total);
      iv_img = (ImageView) view.findViewById(R.id.iv_order_detail_img);
      image_list=new ArrayList<>();

    }
  }

  public My_order_detail_adapter(List<My_order_detail_model> modelList) {
    this.modelList = modelList;
  }

  @Override
  public My_order_detail_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.row_my_order_detail_rv, parent, false);

    context = parent.getContext();

    return new My_order_detail_adapter.MyViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(My_order_detail_adapter.MyViewHolder holder, int position) {
    My_order_detail_model mList = modelList.get(position);


    try {
      image_list.clear();
      JSONArray array = new JSONArray(mList.getProduct_image());
      //Toast.makeText(this,""+product_images,Toast.LENGTH_LONG).show();
      if (mList.getProduct_image().equals(null)) {
        Glide.with(context)
                .load(BaseURL.IMG_PRODUCT_URL + mList.getProduct_image())
                .centerCrop()
                .placeholder(R.drawable.icon)
//                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.iv_img);
      } else {
        for (int i = 0; i <= array.length() - 1; i++) {
          image_list.add(array.get(i).toString());

        }
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }


    Glide.with(context)
            .load(BaseURL.IMG_PRODUCT_URL + image_list.get(0))
            .centerCrop()
            .placeholder(R.drawable.icon)
//            .crossFade()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .dontAnimate()
            .into(holder.iv_img);

    holder.tv_title.setText(mList.getProduct_name());
    holder.tv_price.setText(context.getResources().getString(R.string.currency)+mList.getUnit_value()+"/"+mList.getUnit());
    holder.tv_qty.setText(mList.getQty());
    double qty=Double.parseDouble(mList.getQty());
    double price=Double.parseDouble(mList.getUnit_value());

    holder.tv_order_total.setText("Total : "+mList.getQty()+" x "+mList.getUnit_value()+" = "+context.getResources().getString(R.string.currency)+String.valueOf(qty*price));

  }

  @Override
  public int getItemCount() {
    return modelList.size();
  }

}