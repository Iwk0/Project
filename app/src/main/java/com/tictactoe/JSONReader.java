package com.tictactoe;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.tictactoe.utils.FileUnzip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by imishev on 26.9.2014 г..
 */
public class JSONReader extends AsyncTask<Void, Void, String> {

    private static final String URL_PATH = "https://www.dropbox.com/s/061l4x8feqz646x/country.json?dl=1";

    private String country;
    private String city;
    private String language;

    public JSONReader(String country, String city, String language) {
        this.country = country;
        this.city = city;
        this.language = language;
    }

    @Override
    protected String doInBackground(Void... voids) {
        StringBuilder sBuilder = new StringBuilder();
        BufferedReader in = null;

        try {
            URL url = new URL(URL_PATH);

            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String text = null;

            while ((text = in.readLine()) != null) {
                sBuilder.append(text + "\n");
            }
        } catch (MalformedURLException e) {
            Log.e("MalformedURLException", e.getMessage());
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("IOException", e.getMessage());
                }
            }
        }

        try {
            JSONArray jsonArray = new JSONArray(sBuilder.toString());
            final int JSON_LENGTH = jsonArray.length();

            for (int i = 0; i < JSON_LENGTH; i++) {
                JSONObject jObj = jsonArray.getJSONObject(i);
                if (jObj.getString("country").equals(country) && jObj.getString("city").equals(city) &&
                        jObj.getString("language").equals(language)) {
                    return jObj.getString("filePath");
                }
            }
        } catch (JSONException e) {
            Log.e("JSONException", "Error: " + e.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(String filePath) {
        new FileUnzip(Environment.getExternalStorageDirectory() + "/program/").execute(filePath);
    }
}