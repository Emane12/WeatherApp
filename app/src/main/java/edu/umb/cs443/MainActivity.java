package edu.umb.cs443;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.BreakIterator;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback{

	public final static String DEBUG_TAG="edu.umb.cs443.MYMSG";


    private GoogleMap mMap;
    private Object Tag;
    private TextView countTextView;
    private Integer show;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mFragment=((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mFragment.getMapAsync(this);

        show = 0;
        Button();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //**************HANDLER****************/
    public Handler threadHandler = new Handler(){
        public void handleMessage (android.os.Message message){
            countTextView.setText(show.toString());
        }
    };
    //*************RUNNABLE **************/
    private Runnable countNumbers1 = new Runnable () {
        private static final int DELAY = 1000;
        public void run() {
            try {
                while (true) {
                    threadHandler.sendEmptyMessage(0);
                    Thread.sleep (DELAY);
                    show ++;
                }
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    };


    public void getWeatherInfo(String v){

        String a1 = "https://api.openweathermap.org/data/2.5/weather?";
//        String a2 = "&APPID=cc4ae6e545dee0a295a471824c9fdbda";
        // New API
        String a2 = "&APPID=4519d27cdd6b71caa4c0b7d808ac252d";
//                          f732f682ea4fd5a88af5ac54dd6fde56
//        String a2 = "&APPID=f732f682ea4fd5a88af5ac54dd6fde56";
        //String a2 = "&APPID=3235aeae3b1ab04a7d21751922283174";
        try {
            InputStream str = null;
            String show;
            String res = a1 + v + a2;
            URL url = new URL(res);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            str = conn.getInputStream();
            show = readStream(str, 15000);
            // JSON
            JSONObject jobj = new JSONObject(show);
            final double l = jobj.getJSONObject("coord").getDouble("lon");
            final double la = jobj.getJSONObject("coord").getDouble("lat");
            final double t = jobj.getJSONObject("main").getDouble("temp");
            String arr = jobj.getJSONArray("weather").getJSONObject(0).getString("icon");
            // temperature C
            final int c = (int) (t - 273.15);

            InputStream is = null;
            ImageView iv = (ImageView) findViewById(R.id.iv);
            TextView tv = (TextView) findViewById(R.id.tv);
            icon(arr);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(la, l));
                    CameraUpdate zoom=CameraUpdateFactory.zoomTo(12);
                    mMap.moveCamera(center);
                    mMap.animateCamera(zoom);
                }
            });

            try {
                tv.setText(c + "C");
            } catch (Exception e){}


        } catch (Exception e) {
        }
        }

    private void Button() {
        Button btn = (Button) findViewById(R.id.btn);
        EditText edit = (EditText) findViewById(R.id.edit);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String w = edit.getText().toString();
                        try {
                            int x = Integer.valueOf(w);
//                            https://api.openweathermap.org/data/2.5/weather?zip=02125,us
                            getWeatherInfo("zip=" + w);
                        }catch (Exception e) {
//                            https://api.openweathermap.org/data/2.5/weather?q=boston,us
                            getWeatherInfo("q=" + w);
                        }
                    }
                }).start();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap map) {
        this.mMap=map;
    }

    private Runnable countNumbers = new Runnable () {
        private static final int DELAY = 1000;
        public void run() {
            try {
                while (true) {
//                    threadHandler.sendEmptyMessage(0);
                    Thread.sleep (DELAY);
                    int count = 0;
                    count ++;
                }
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    };

    private String readStream(InputStream k, int m)
            throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(k));
        StringBuilder w = new StringBuilder();
        try {
            String line = null;
            while ((line = r.readLine()) != null) {
                w.append(line + "\n");
            }
        } catch (IOException e) {
            Log.e((String) Tag, "IOException", e);
        } finally {
            try {
                k.close();
            } catch (IOException e) {
                Log.e((String) Tag, "IOException", e);
            }
        }
        return w.toString();
    }

    private InputStream downloadUrl1(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }

//    private Bitmap downloadUrl(String myurl) throws IOException {
    private void icon(String myurl) {
        InputStream is = null;
        try {
            ImageView iv = (ImageView) findViewById(R.id.iv);
//            URL url = new URL(myurl);
            // https://openweathermap.org/img/wn/filename.png
            URL url = new URL("https://openweathermap.org/img/w/" + myurl + ".png");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(10000 /* milliseconds */);
//            conn.setConnectTimeout(15000 /* milliseconds */);
//            conn.setRequestMethod("GET");
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
//            Log.i(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            Drawable dr = new BitmapDrawable(getResources(),bitmap);
            iv.setBackground(dr);
//            return bitmap;

        } catch (Exception e) {
            Log.i(DEBUG_TAG, e.toString());
        } finally {
            if (is != null) {
            }
        }
    }
}
