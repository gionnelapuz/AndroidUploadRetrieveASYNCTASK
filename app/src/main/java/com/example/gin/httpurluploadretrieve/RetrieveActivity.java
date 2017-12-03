package com.example.gin.httpurluploadretrieve;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gin.httpurluploadretrieve.ADAPTERS.retrieveTextAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RetrieveActivity extends AppCompatActivity {

    private ListView listRetrieveText;
    private ArrayList<String> name;
    private ArrayList<String> age;

    private retrieveTextAdapter retrieveTextAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve);

        name = new ArrayList<>();
        age = new ArrayList<>();

        listRetrieveText = (ListView) findViewById(R.id.listView);
        retrieveTextAdapter = new retrieveTextAdapter(RetrieveActivity.this, name, age);
        listRetrieveText.setAdapter(retrieveTextAdapter);

        new AsyncRetrieve().execute();
    }

    private class AsyncRetrieve extends AsyncTask<String, String, String> {
        HttpURLConnection connection;
        URL url = null;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL("http://192.168.1.106/php/retrieveText.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }

            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                connection.setDoOutput(true);

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }


            try {
                int response_code = connection.getResponseCode();

                if (response_code == HttpURLConnection.HTTP_OK) {

                    InputStream input = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    return (result.toString());
                } else {
                    return "unsuccessful";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                connection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
        //    Toast.makeText(RetrieveActivity.this, result, Toast.LENGTH_SHORT).show();
            try {
                showLIST(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        private void showLIST(String json) throws JSONException{
            JSONObject obj = null;
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    obj = jsonArray.getJSONObject(i);
                    name.add(obj.getString("name"));
                    age.add(obj.getString("age"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            retrieveTextAdapter adapter = new retrieveTextAdapter(RetrieveActivity.this, name, age);
            listRetrieveText.setAdapter(adapter);
        }
    }
}
