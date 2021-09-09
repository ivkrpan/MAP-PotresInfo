package hr.vsite.potresinfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getName();
    private String API_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time";

    private QuakeAdapter mAdapter;

    private int magnitude;
    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ucitavanje iz postavki
        SharedPreferences settings  = getSharedPreferences("postavke",0);
        magnitude = settings.getInt("minmag",1);
        num = settings.getInt("limit",10);
        String lang = settings.getString("lang","en");

        //Vezanje liste sa adapterom
        ListView mainListView = (ListView) findViewById(R.id.list);
        mAdapter = new QuakeAdapter(this, R.layout.list_item, new ArrayList<Quake>());
        mainListView.setAdapter(mAdapter);

        setLocale(this,lang);
        //Dodavanje timera za automatsko osvjezavanje liste
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                EarthquakeAsyncTask task = new EarthquakeAsyncTask();
                task.execute(API_URL + "&minmag=" + magnitude + "&limit=" + num);
            }
        },  0,60000);

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Na klik nekog od itema, otvori detaljan prikaz
                Quake currentEarthquake = mAdapter.getItem(position);
                Uri earthquakeUri = Uri.parse(currentEarthquake.getmURL());

                Intent intent = new Intent( MainActivity.this ,DetailedActivity.class);
                intent.putExtra("Earthquake", currentEarthquake);

                startActivity(intent);
            }
        });
    }

    //nakon pohrane novih postavki
    @Override
    public void onResume() {

        super.onResume();
        SharedPreferences settings;
        settings = getSharedPreferences("postavke",0);

        //Ako su promjenjene postavke, promjeni jezik i restartaj aktivnost
        if (settings.getBoolean("reload", false) == true)
        {
            if(settings.contains("lang"))
            {
                if (settings.getString("lang", "").equals("hr"))
                    setLocale(this,"hr");
                else
                    setLocale(this,"en");
            }
            settings.edit().putBoolean("reload", false).apply();
            this.recreate();
        }
    }

    private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<Quake>> {

        @Override
        protected List<Quake> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            List<Quake> result = APIUtils.fetchEarthquakeData(urls[0], getApplicationContext());
            return result;
        }

        @Override
        protected void onPostExecute(List<Quake> data) {
            // Clear the adapter of previous earthquake data
            mAdapter.clear();

            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //Instalacija glavnog menija
        MenuInflater mainMenu=getMenuInflater();
        mainMenu.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Menu controller
        switch (item.getItemId()){
            case R.id.miSettings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Postavljanje jezika za aplikaciju
    public static void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}