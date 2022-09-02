package binplus.foodiswill.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import binplus.foodiswill.Config.Module;
import binplus.foodiswill.Model.CityModel;
import binplus.foodiswill.R;

import static binplus.foodiswill.Config.BaseURL.IMG_CITY_URL;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 20,March,2021
 */
public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder>  {
  Module module;
    Context context;
    ArrayList<CityModel> list;
    String type ;

    public CityAdapter(Context context, ArrayList<CityModel> list ,String type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= null;
        if (type.equals("city_name")) {
          view=  LayoutInflater.from(context).inflate(R.layout.row_city_name, null);
        }
        else
        {
            view=  LayoutInflater.from(context).inflate(R.layout.row_city, null);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
     CityModel model=list.get(position);
     holder.tv_title.setText(model.getCity_name());
      holder.pbar.setVisibility(View.VISIBLE);
      Picasso.with(context).load(Uri.parse(model.getCity_image())).noFade().into(holder.iv_icon, new Callback() {
             @Override
             public void onSuccess() {
                 holder.pbar.setVisibility(View.GONE);
             }

             @Override
             public void onError() {
                 holder.pbar.setVisibility(View.GONE);
             }
         });
       holder.rb_selected.setChecked(model.isSelected());



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        ImageView iv_icon;
        RelativeLayout rl_selected;
        ProgressBar pbar;
        RadioButton rb_selected;
        public ViewHolder(@NonNull View v) {
            super(v);
            rl_selected=v.findViewById(R.id.rl_selected);
            rb_selected=v.findViewById(R.id.rb_selected);
            iv_icon=v.findViewById(R.id.iv_icon);
            tv_title=v.findViewById(R.id.tv_title);
            pbar=v.findViewById(R.id.pbar);
            module=new Module(context);
        }

    }
}
