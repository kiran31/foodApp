package binplus.foodiswill.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import binplus.foodiswill.Config.BaseURL;
import binplus.foodiswill.R;
import binplus.foodiswill.util.ConnectivityReceiver;

import static binplus.foodiswill.MainActivity.stop_order_image;


/**
 * Created by Rajesh Dabhi on 29/6/2017.
 */

public class Reward_fragment extends Fragment {
  ImageView iv_img;
  public Reward_fragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.activity_reward_points, container, false);
    iv_img=view.findViewById(R.id.iv_img);
      view.setFocusableInTouchMode(true);
      view.requestFocus();
      view.setOnKeyListener(new View.OnKeyListener() {
          @Override
          public boolean onKey(View v, int keyCode, KeyEvent event) {
              if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                  AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                  builder.setTitle("Confirmation");
                  builder.setMessage("Are you sure want to exit?");
                  builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {
                          dialogInterface.dismiss();
                          //((MainActivity) getActivity()).finish();
                          getActivity().finishAffinity();


                      }
                  })
                          .setNegativeButton("No", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialogInterface, int i) {
                                  dialogInterface.dismiss();
                              }
                          });
                  final AlertDialog dialog=builder.create();
                  dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                      @Override
                      public void onShow(DialogInterface arg0) {
                          dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                          dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                      }
                  });
                  dialog.show();
                  return true;
              }
              return false;
          }
      });

      Log.e("frgthy",stop_order_image);
      if (ConnectivityReceiver.isConnected()) {
      Glide.with(getActivity())
              .load( BaseURL.IMG_EXTRA_URL + stop_order_image)
              .placeholder( R.drawable.icon)
//              .crossFade()
              .diskCacheStrategy(DiskCacheStrategy.ALL)
              .dontAnimate()
              .into(iv_img);
    }

    iv_img.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Fragment  fm = new Help_Fragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                .addToBackStack(null).commit();
      }
    });
    return view;

  }


}