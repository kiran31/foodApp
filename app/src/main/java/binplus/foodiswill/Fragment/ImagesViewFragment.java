package binplus.foodiswill.Fragment;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import binplus.foodiswill.Adapter.ImageAdapter;
import binplus.foodiswill.Adapter.ImageRecyclerAdapter;
import binplus.foodiswill.R;

import static binplus.foodiswill.Fragment.Details_Fragment.image_list;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImagesViewFragment extends Fragment {
    ViewPager pager ;
    ImageAdapter adapter ;
    RecyclerView img_recycler ;
    ImageRecyclerAdapter imageRecyclerAdapter;
    int sel_pos =0 ;

    public ImagesViewFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View view= inflater.inflate(R.layout.fragment_images_view, container, false);
      pager = view.findViewById(R.id.img_pager);
      adapter = new ImageAdapter(getActivity(), (ArrayList<String>) image_list);
      pager.setAdapter(adapter);
      pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         @Override
         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

         }

         @Override
         public void onPageSelected(int position) {
             sel_pos = position;
         }

         @Override
         public void onPageScrollStateChanged(int state) {

         }
     });
//      sel_pos = adapter.getItemPosition(pager.getCurrentItem());
      img_recycler = view.findViewById(R.id.img_recycler);
     imageRecyclerAdapter = new ImageRecyclerAdapter(getActivity(),image_list,sel_pos);
     img_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
     img_recycler.setAdapter(imageRecyclerAdapter);

      return  view;
    }
}
