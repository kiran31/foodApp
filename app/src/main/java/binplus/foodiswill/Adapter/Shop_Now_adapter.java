package binplus.foodiswill.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Model.ShopNow_model;
import binplus.foodiswill.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rajesh Dabhi on 22/6/2017.
 */

public class Shop_Now_adapter extends RecyclerView.Adapter<Shop_Now_adapter.MyViewHolder> {

    private List<ShopNow_model> modelList;
    private Context context;
    int count;
    String language;
    SharedPreferences preferences;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public CircleImageView image;
        public RelativeLayout rel_deactive;
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.service_text);
            image =  view.findViewById(R.id.service_image);
            rel_deactive =  view.findViewById(R.id.rel_deactive);
        }
    }

    public Shop_Now_adapter(List<ShopNow_model> modelList) {
        this.modelList = modelList;
    }

    @Override
    public Shop_Now_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_headre_catogaries, parent, false);

        context = parent.getContext();

        return new Shop_Now_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Shop_Now_adapter.MyViewHolder holder, int position) {
        ShopNow_model mList = modelList.get(position);

        if(mList.getStatus().toString().equalsIgnoreCase("0"))
        {
          holder.rel_deactive.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.rel_deactive.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(BaseURL.IMG_CATEGORY_URL + mList.getImage())
                .placeholder(R.drawable.icon)
//                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.image);

        preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
        language=preferences.getString("language","");
//        if (language.contains("english")) {
//            holder.title.setText(mList.getTitle());
//        }
//        else {
//            holder.title.setText(mList.getTitle());
//
//        }

        if( mList.getTitle_hindi().isEmpty())
        {
            holder.title.setText(mList.getTitle());
        }
        else
        {
            holder.title.setText(mList.getTitle() +"\n( "+mList.getTitle_hindi()+" )");
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

}

