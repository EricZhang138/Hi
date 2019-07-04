package com.example.wee_8.arimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.wee_8.arimage.Helper.BitmapHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Loadimage extends AppCompatActivity {
    String[] imagUrl={
            "http://manishkpr.webheavens.com/wp-content/uploads/2012/10/bloglogo1.png",
            "http://manishkpr.webheavens.com/wp-content/uploads/2012/10/bloglogo2.png",
            "http://manishkpr.webheavens.com/wp-content/uploads/2012/10/bloglogo3.png" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadimage);

        if(Build.VERSION.SDK_INT >9){
            StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }





        Bitmap bitmap = loadBitmapFromUrl("https://images.techhive.com/images/article/2015/05/aws-logo-100584713-primary.idge.jpg");
      //  Bitmap bitmap2 = loadBitmapFromUrl("https://helpx.adobe.com/content/dam/help/en/stock/how-to/visual-reverse-image-search/jcr_content/main-pars/image/visual-reverse-image-search-v2_intro.jpg");
        BitmapHelper.getInstance().setBitmap(bitmap);

        Intent intent = new Intent(Loadimage.this, MainActivity.class);
        startActivity(intent);

        System.out.println("bitmap3=" + bitmap);

    }

    private Bitmap loadBitmapFromUrl(String sourceLink) {

        try{
            URL url = new URL(sourceLink);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
            return myBitmap;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
