package binplus.foodiswill.Adapter;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.R;

public class ImageAdapter extends PagerAdapter {
    Context mContext;
    ArrayList<String> img_list;

    public ImageAdapter(Context mContext, ArrayList<String> img_list) {
        this.mContext = mContext;
        this.img_list = img_list;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }



    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView.setImageResource(Integer.parseInt(img_list.get(position)));
        Glide.with(mContext)
                .load(BaseURL.IMG_PRODUCT_URL +img_list.get(position) )
                .placeholder(R.drawable.icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(imageView);
        (container).addView(imageView, 0);
        (container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((ImageView) object);
    }

    @Override
    public int getCount() {
        return img_list.size();
    }
}

