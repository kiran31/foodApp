package binplus.foodiswill.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.R;

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ViewHolder> {
    Activity activity ;
   List<String> img_list;
    int sel_pos ;

    public ImageRecyclerAdapter(Activity activity, List<String> img_list, int sel_pos) {
        this.activity = activity;
        this.img_list = img_list;
        this.sel_pos = sel_pos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(activity).inflate(R.layout.row_imges,null);
      ViewHolder holder = new ViewHolder(view);
      return  holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(activity)
                .load(BaseURL.IMG_PRODUCT_URL +img_list.get(position) )
                .placeholder(R.drawable.icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.img);

     if (position == sel_pos)
     {
         holder.view.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
     }
    else
     {
         holder.view.setBackgroundColor(activity.getResources().getColor(R.color.white));
     }

    }

    @Override
    public int getItemCount() {
        return img_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img ;
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            view = itemView.findViewById(R.id.view1);
        }
    }
}
