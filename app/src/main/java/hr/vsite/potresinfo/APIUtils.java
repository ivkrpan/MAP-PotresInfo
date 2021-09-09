package hr.vsite.potresinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static hr.vsite.potresinfo.MainActivity.LOG_TAG;

public class APIUtils {

    private static final String LOCATION_SEPARATOR = " of ";

    public static List<Quake> fetchEarthquakeData(String requestUrl, Context appContext) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Quake> earthquakes = extractEarthquakes(jsonResponse,appContext);
        return earthquakes;
    }

    public static List<Quake> extractEarthquakes(String quakeJSON, Context appContext) {

        if (TextUtils.isEmpty(quakeJSON)) {
            return null;
        }

        SharedPreferences settings  = appContext.getSharedPreferences("postavke",0);

        String lang = settings.getString("lang","en");
        MainActivity.setLocale(appContext, lang);

        List<Quake> earthquakes = new ArrayList<>();

        try {
            JSONObject JSONQuake = new JSONObject(quakeJSON);
            JSONArray JSONQuakeArray = JSONQuake.getJSONArray("features");
            for (int i = 0; i < JSONQuakeArray.length(); i ++) {
                JSONObject JSONQuakeObject = JSONQuakeArray.getJSONObject(i);
                String quake_id = JSONQuakeObject.getString("id");
                JSONObject JSONQuakeProperties = JSONQuakeObject.getJSONObject("properties");
                double mag = JSONQuakeProperties.getDouble("mag");
                String place = JSONQuakeProperties.getString("place");
                long time = JSONQuakeProperties.getLong("time");
                String url = JSONQuakeProperties.getString("url");

                String offset = "";
                String location = "";

                if (place.contains(LOCATION_SEPARATOR)) {
                    String[] parts = place.split(LOCATION_SEPARATOR);

                    String newString;
                    newString = parts[0] + " ";

                    if (parts[0].contains(" NNE")){
                        newString = newString.replace(" NNE ", " " + appContext.getString(R.string.NNE) + " ");
                    }else if (parts[0].contains(" NNW")){
                        newString = newString.replace(" NNW ", " " + appContext.getString(R.string.NNW) + " ");
                    }else if (parts[0].contains(" ENE")){
                        newString = newString.replace(" ENE ", " " + appContext.getString(R.string.ENE) + " ");
                    }else if (parts[0].contains(" ESE")){
                        newString = newString.replace(" ESE ", " " + appContext.getString(R.string.ESE) + " ");
                    }else if(parts[0].contains(" SSE")) {
                        newString = newString.replace(" SSE ", " " + appContext.getString(R.string.SSE) + " ");
                    }else if(parts[0].contains(" SSW")) {
                        newString = newString.replace(" SSW ", " " + appContext.getString(R.string.SSW) + " ");
                    }else if(parts[0].contains(" WSW")) {
                        newString = newString.replace(" WSW ", " " + appContext.getString(R.string.WSW) + " ");
                    }else if(parts[0].contains(" WNW")) {
                        newString = newString.replace(" WNW ", " " + appContext.getString(R.string.WNW) + " ");

                    }else if (parts[0].contains(" NE")){
                        newString = newString.replace(" NE ", " " + appContext.getString(R.string.NE) + " ");
                    }else if (parts[0].contains(" NW")){
                        newString = newString.replace(" NW ", " " + appContext.getString(R.string.NW) + " ");
                    }else if(parts[0].contains(" SE")) {
                        newString = newString.replace(" SE ", " " + appContext.getString(R.string.SE) + " ");
                    }else if(parts[0].contains(" SW")) {
                        newString = newString.replace(" SW ", " " + appContext.getString(R.string.SW) + " ");

                    }else if (parts[0].contains(" N")){
                        newString = newString.replace(" N ", " " + appContext.getString(R.string.N) + " ");
                    }else if (parts[0].contains(" E")){
                        newString = newString.replace(" E ", " " + appContext.getString(R.string.E) + " ");
                    }else if(parts[0].contains(" W")) {
                        newString = newString.replace(" W ", " " + appContext.getString(R.string.W) + " ");
                    }else if(parts[0].contains(" S")) {
                        newString = newString.replace(" S ", " " + appContext.getString(R.string.S) + " ");
                    }

                    offset = newString + appContext.getString(R.string.of);
                    location = parts[1];
                    if (location.contains("?"))
                        location= location.replace("?", "a");

                } else {

                    offset = appContext.getString(R.string.near);
                    location = place;
                }

                earthquakes.add(new Quake(quake_id, location, offset , mag, time, url));
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return earthquakes;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();

            if (inputStream != null)
                inputStream.close();
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,  "utf8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
