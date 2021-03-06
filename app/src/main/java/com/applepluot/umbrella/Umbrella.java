package com.applepluot.umbrella;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Umbrella extends Activity {
    private static final String TAG = Umbrella.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umbrella);
        WebServiceTask webserviceTask = new WebServiceTask();
        webserviceTask.execute("Apple!");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_umbrella, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class WebServiceTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            String useUmbrellaStr = "Don't know, sorry about that.";
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/"
                        +"forecast/daily?q=San%20Jose&mode=json&units=metric&cnt=1");
                urlConnection = (HttpURLConnection) url.openConnection();
                useUmbrellaStr = useUmbrella(urlConnection.getInputStream());
                Log.i(TAG, useUmbrellaStr);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                useUmbrellaStr = e.getMessage();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return useUmbrellaStr;
        }
        protected String useUmbrella(InputStream in) {
            //read and parse InputStream
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                //JSON needs to be parsed here
                Log.i("Returned data", stringBuilder.toString());
                JSONObject forecastJson = new JSONObject(stringBuilder.toString());
                JSONArray weatherArray = forecastJson.getJSONArray("list");
                JSONObject todayForecast = weatherArray.getJSONObject(0);
                JSONObject todayWeather = todayForecast.getJSONArray("weather").getJSONObject(0);
                String description = todayWeather.getString("description").toLowerCase();
                if (description.contains("rain") || description.contains("snow")) {
                    return("Yes! " + description);
                } else {
                    return("No! " + description);
                }
            } catch (Exception e) {
                Log.e("MainActivity", "Error", e);
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return "Don't know, sorry about that.";
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView textview = (TextView) findViewById(R.id.hello);
            textview.setText("Should I take an umbrella today? "+s);
        }
    }
}
