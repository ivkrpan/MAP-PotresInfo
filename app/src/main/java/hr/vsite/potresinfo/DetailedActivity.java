package hr.vsite.potresinfo;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;

public class DetailedActivity extends AppCompatActivity {

    private String link_url = "https://www.usgs.gov/natural-hazards/earthquake-hazards/earthquakes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        //Prikaz progress bara dok se ne ucita mapa
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setIndeterminate(true);

        WebView myWebView = (WebView) findViewById(R.id.webmap);
        myWebView.getSettings().setJavaScriptEnabled(true);


        //Skrivanje nezeljenih elementa iz html mape
        myWebView.setWebViewClient(new WebViewClient(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onPageFinished(WebView view, String url)
            {
                view.evaluateJavascript("document.getElementsByClassName('close-button')[0].style.visibility='hidden';",null);
                String chk = url.substring(url.length() -3, url.length());

                //Kada se mapa ucita, prikazi ju i sakri progress bar
                if ( chk.equals("map"))
                {
                    view.setVisibility(View.VISIBLE);
                    ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
                    pb.setVisibility(View.GONE);
                }
            }
        });

        Intent i = getIntent();
        Quake ActiveQuake = (Quake) i.getSerializableExtra("Earthquake");

        String quakeID = ActiveQuake.getmID();
        String location = ActiveQuake.getmLocation();
        String offset = ActiveQuake.getmOffset();
        double magnitude = ActiveQuake.getmMagnitude();
        long date_time = ActiveQuake.getmDate();
        String url = ActiveQuake.getmURL();

        TextView tv_magnitude = (TextView) findViewById(R.id.magnitude_tv);
        TextView tv_offset = (TextView) findViewById(R.id.location_offset_tv);
        TextView tv_location = (TextView) findViewById(R.id.primary_location_tv);
        TextView tv_datetime = (TextView) findViewById(R.id.datetime_tv);

        //Ucitavanje podataka u detaljnom pregledu
        myWebView.loadUrl("https://earthquake.usgs.gov/earthquakes/eventpage/" + quakeID + "/map");
        tv_magnitude.setText(String.format("%.1f", magnitude));
        tv_offset.setText(offset);
        tv_location.setText(location);

        SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.yyyy. H:mm:ss");
        tv_datetime.setText(dateFormat.format(date_time));

        GradientDrawable magnitudeCircle = (GradientDrawable) tv_magnitude.getBackground();

        int magnitudeColor = ContextCompat.getColor(getApplicationContext(),QuakeAdapter.getMagnitudeColor(magnitude));
        magnitudeCircle.setColor(magnitudeColor);

        setTitle(getString(R.string.near) + " " + location);
        //postavljane linka za otvaranje u web browseru
        link_url = url;
    }

    public void open_browser(View v)
    {
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link_url));
        startActivity(websiteIntent);
    }
}