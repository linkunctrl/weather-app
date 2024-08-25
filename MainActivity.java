package com.example.weather_app;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.loader.content.AsyncTaskLoader;

import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    TextView cityName;
    Button search;
    TextView show;
    String url;

    class getWeather extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls){
            StringBuilder result = new StringBuilder();
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = reader.readLine()) != null){
                    result.append(line).append("\n");
                }
                return result.toString();
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected  void onPostExecute(String result){
            super.onPostExecute(result);
            try{
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("main");
                weatherInfo =  weatherInfo.replace("temp", "Temp");
                weatherInfo =  weatherInfo.replace("feels_like", "Feels Like");
                weatherInfo =  weatherInfo.replace("Temp_max", "Temp Max");
                weatherInfo =  weatherInfo.replace("Temp_min", "Temp Min");
                weatherInfo =  weatherInfo.replace("pressure", "Pressure");
                weatherInfo =  weatherInfo.replace("humidity", "Humidity");
                weatherInfo =  weatherInfo.replace("sea_level", "Sea Level");
                weatherInfo =  weatherInfo.replace("grnd_level", "Ground Level ");
                weatherInfo =  weatherInfo.replace("{", "");
                weatherInfo =  weatherInfo.replace("}", "");
                weatherInfo =  weatherInfo.replace(",", "\n");
                weatherInfo =  weatherInfo.replace(":", "  :  ");

                show.setText(weatherInfo);

            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.search);
        show = findViewById(R.id.weather);

        final String[] temp = {""};


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
                String city = cityName.getText().toString();
                try{
                    if(city!=null){
                        url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=3a8d294a2371de72ec64728234019651";
                    }else{
                        Toast.makeText(MainActivity.this, "Enter City", Toast.LENGTH_SHORT).show();
                    }
                    getWeather task = new getWeather();
                    temp[0] = task.execute(url).get();
                }catch(ExecutionException e){
                    e.printStackTrace();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                if(temp[0] == null){
                    show.setText("Unable to find weather");

                }


            }
        });
    }
}
