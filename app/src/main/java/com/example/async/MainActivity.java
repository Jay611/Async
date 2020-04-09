package com.example.async;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {
    URL url = null;
    ProgressDialog p;
    //ArrayList<Model> itemList;
    TextView textViewId;
    TextView textViewTitle;
    TextView textViewBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        textViewId = findViewById(R.id.textViewId);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewBody = findViewById(R.id.textViewBody);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AsyncTaskExample asyncTask = new AsyncTaskExample(2);
                asyncTask.execute("https://jsonplaceholder.typicode.com/posts");
            }
        });
    }

    private class AsyncTaskExample extends AsyncTask<String, String, Model> {
        int userId;
        //Model model;

        public AsyncTaskExample(int userId){
            super();
            this.userId = userId;
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            p = new ProgressDialog(MainActivity.this);
            p.setMessage("Please wait... It is downloading");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected Model doInBackground(String... strings) {
            String response = "";
            try{
                url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                InputStream stream = conn.getInputStream();

                response = convertStreamToString(stream);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(response.length() != 0){
                try{
                    JSONArray items = new JSONArray(response);      // get JSON array

                    for(int i = 0; i < items.length(); i++){
                        JSONObject item = items.getJSONObject(i);
                        if(item.getInt("userId") == userId){    // create new model(userId == 2)
                            Model model = new Model();
                            model.setUserId(item.getInt("userId"));
                            model.setId(item.getInt("id"));
                            model.setTitle(item.getString("title"));
                            model.setBody(item.getString("body"));
                            return model;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Model model){
            super.onPostExecute(model);
            if(model != null){
                textViewId.setText(Integer.toString(model.getId()));
                textViewTitle.setText(model.getTitle());
                textViewBody.setText(model.getBody());
            }else{
                textViewId.setText("");
                textViewTitle.setText("");
                textViewBody.setText("");
            }
            p.hide();
        }
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }



}
