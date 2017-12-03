package com.example.gin.httpurluploadretrieve;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class UploadActivity extends AppCompatActivity {

    private EditText txtName, txtAge;
    private Button btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        txtName = (EditText) findViewById(R.id.txtName);
        txtAge = (EditText) findViewById(R.id.txtAge);

        btnUpload = (Button) findViewById(R.id.btnUpload);



        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = txtName.getText().toString();
                final String age = txtAge.getText().toString();

                new AsyncLogin().execute(name,age);
            }
        });
    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        HttpURLConnection connection;
        URL url = null;

        @Override
        protected void onPreExecute(){

        }
        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL("http://192.168.1.106/php/insertText.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }

            try {
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");

                connection.setDoInput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("name", params[0])
                        .appendQueryParameter("age", params[1]);
                String query = builder.build().getEncodedQuery();

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                connection.connect();
            } catch (IOException e) {
                e.printStackTrace();
                return  "exception";
            }


            try
            {
                int response_code = connection.getResponseCode();

                if (response_code == HttpURLConnection.HTTP_OK){

                    InputStream input = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null){
                        result.append(line);
                    }
                    return (result.toString());
                }else{
                    return "unsuccessful";
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result){

            Toast.makeText(UploadActivity.this, result, Toast.LENGTH_SHORT).show();

        }
    }

}
