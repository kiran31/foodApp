package binplus.foodiswill;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import binplus.foodiswill.Config.Module;

public class ScreenshotActivity extends AppCompatActivity implements View.OnClickListener{


    LinearLayout relView;
    ImageView img_ss,img_back;
    String strBitmap="",order_id="";
    RelativeLayout rel_pdf;
  Activity ctx=ScreenshotActivity.this;
  Module module;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot);
        initViews();
    }

    private void initViews() {
        module=new Module(ctx);
        relView=findViewById(R.id.relView);
        img_ss=findViewById(R.id.img_ss);
        img_back=findViewById(R.id.img_back);
        rel_pdf=findViewById(R.id.rel_pdf);
        img_back.setOnClickListener(this);
        rel_pdf.setOnClickListener(this);
        strBitmap=getIntent().getStringExtra("ss");
        order_id=getIntent().getStringExtra("order_id");
        if(!strBitmap.isEmpty())
        {
            module.showToast("Yes");
            Bitmap bitmap=module.StringToBitMap(strBitmap);
            img_ss.setImageBitmap(bitmap);
        }
        else
        {
            module.showToast("NOpe");
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId() ==R.id.img_back)
        {
        }
        else if(v.getId() == R.id.rel_pdf)
        {
            Bitmap bmp=loadBitmapFromView(relView,relView.getWidth(),relView.getHeight());
            createPdf3(bmp);
//            createPDF();
        }
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void createPDF()
    {

        File folder = new File(Environment.getExternalStorageDirectory()+File.separator+"PDF Folder");
        folder.mkdirs();

        Date date = new Date() ;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        final File myFile = new File(folder + timeStamp + ".pdf");
        try {
            OutputStream output  = new FileOutputStream(myFile);
            Document document = new Document(PageSize.A4);
            try{
                PdfWriter.getInstance(document, output);
                document.open();
//                LinearLayout view2 = (LinearLayout)findViewById(R.id.MainLayout);

                relView.setDrawingCacheEnabled(true);
                Bitmap screen2= screenShot(relView);
                ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                screen2.compress(Bitmap.CompressFormat.JPEG,100, stream2);
                byte[] byteArray2 = stream2.toByteArray();
                addImage(document,byteArray2);

                document.close();
                AlertDialog.Builder builder =  new AlertDialog.Builder(ctx);
                builder.setTitle("Success")
                        .setMessage("enter code herePDF File Generated Successfully.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(myFile), "application/pdf");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                            }

                        }).show();

                //document.add(new Paragraph(mBodyEditText.getText().toString()));
            }catch (Exception e)
            {
                //loading.dismiss();
                e.printStackTrace();
            }

        }catch (FileNotFoundException e)
        {
            // loading.dismiss();
            e.printStackTrace();
        }


    }

    private void createPdf2(Bitmap bitmap){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);



        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);

        // write the document content
        String targetPdf = "/sdcard/test.pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
//            btn_convert.setText("Check PDF");
//            boolean_save=true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
    }

    private static void addImage(Document document,byte[] byteArray)
    {
        Image image = null;
        try
        {
            image = Image.getInstance(byteArray);
        }
        catch (BadElementException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // image.scaleAbsolute(150f, 150f);
        try
        {
            document.add(image);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    private void createPdf3(Bitmap bitmap){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;
        File folder = new File(Environment.getExternalStorageDirectory()+File.separator+getString(R.string.app_name));
        if(!folder.exists())
        {
            folder.mkdirs();
        }

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);

        // write the document content
        String targetPdf = Environment.getExternalStorageDirectory()+File.separator+getString(R.string.app_name)+"/"+order_id+".pdf";
        File filePath;
        filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }
        document.close();
        Toast.makeText(this, "PDF is created!!!", Toast.LENGTH_SHORT).show();
        openGeneratedPDF();

    }

    private void openGeneratedPDF(){
        File file = new File(Environment.getExternalStorageDirectory()+File.separator+getString(R.string.app_name)+"/"+order_id+".pdf");
        if (file.exists())
        {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try
            {
                startActivity(intent);
            }
            catch(ActivityNotFoundException e)
            {
                Toast.makeText(ctx, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }
}
