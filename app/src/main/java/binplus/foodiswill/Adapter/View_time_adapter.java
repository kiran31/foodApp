package binplus.foodiswill.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import binplus.foodiswill.R;
import binplus.foodiswill.util.Session_management;

import static android.content.Context.MODE_PRIVATE;



public class View_time_adapter extends RecyclerView.Adapter<View_time_adapter.MyViewHolder> {

  private List<String> modelList;

  private Context context;
  SharedPreferences preferences;
  Session_management sessionManagement ;
  public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    LinearLayout linear_time ;

    public MyViewHolder(View view) {
      super(view);
      title = (TextView) view.findViewById(R.id.tv_socity_name);
      linear_time = view.findViewById( R.id.lay_home );
    }
  }

  public View_time_adapter(List<String> modelList) {
    this.modelList = modelList;
  }

  @Override
  public View_time_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.row_socity_rv, parent, false);

    context = parent.getContext();

    return new View_time_adapter.MyViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(View_time_adapter.MyViewHolder holder, final int position) {
    //Socity_model mList = modelList.get(position);
    preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
    String language=preferences.getString("language","");
    if (language.contains("spanish")) {
      final String time=modelList.get(position);
//     time=time.replace("PM","م");
//     time=time.replace("AM","ص");
      holder.title.setText(time);

      holder.linear_time.setOnClickListener( new View.OnClickListener() {
        @Override
        public void onClick(View view) {



        }
      } );

    }
    else {
      holder.title.setText(modelList.get(position));

    }

  }

  @Override
  public int getItemCount() {
    return modelList.size();
  }

}
