package binplus.foodiswill.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.Model.Category_model;
import binplus.foodiswill.R;

/**
 * Created by Rajesh Dabhi on 22/6/2017.
 */

public class Home_Icon_Adapter extends RecyclerView.Adapter<Home_Icon_Adapter.MyViewHolder> {

    private List<Category_model> modelList;
    private Context context;
    String language;
    SharedPreferences preferences;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView image;
        public RelativeLayout rel_deactive;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.service_text);
            image = (ImageView) view.findViewById(R.id.service_image);
            rel_deactive = (RelativeLayout) view.findViewById(R.id.rel_deactive);


        }
    }

    public Home_Icon_Adapter(List<Category_model> modelList) {
        this.modelList = modelList;
    }

    @Override
    public Home_Icon_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_headre_catogaries, parent, false);

        context = parent.getContext();

        return new Home_Icon_Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Home_Icon_Adapter.MyViewHolder holder, int position) {
        Category_model mList = modelList.get(position);

        if(mList.getStatus().equals("0"))
        {
            if(holder.rel_deactive.getVisibility()==View.GONE)
            {
                holder.rel_deactive.setVisibility(View.VISIBLE);
            }
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
//        preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
//        language=preferences.getString("language","");
//        if (language.contains("english")) {
//            holder.title.setText(mList.getTitle());
//        }
//        else {
//            holder.title.setText(mList.getTitle());
//
//        }

        if(mList.getTitle_hindi().isEmpty())
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

