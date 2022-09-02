package binplus.foodiswill;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import binplus.foodiswill.Fragment.Payment_fragment;

public class OrderFail extends AppCompatActivity {
    RelativeLayout oredr_fail;
    private Payment_fragment mItemsFragment;
    Dialog loadingBar ;
    @Override
    protected void attachBaseContext(Context newBase) {

        newBase = LocaleHelper.onAttach(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_fail);
        loadingBar=new Dialog(OrderFail.this,android.R.style.Theme_Translucent_NoTitleBar);
        loadingBar.setContentView( R.layout.progressbar );
        loadingBar.setCanceledOnTouchOutside(false);
        mItemsFragment = new Payment_fragment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.order_fail));
        oredr_fail = (RelativeLayout) findViewById(R.id.retry_order);
        oredr_fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OrderFail.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }


}
