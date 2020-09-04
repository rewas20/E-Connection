package epsilon.easyapps.e_connection;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class DoProccessActivity extends AppCompatActivity {

    Spinner spinner;
    Button btnFind,submitDisconnect,endDisconnect;
    SupportMapFragment mapFragment;
    GoogleMap map;
    TextInputLayout processDis;
    TextInputEditText processDisconnect;
    TextView proCode,ATMFound,textNumberNeedDis;

    FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat=0,currentLong=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_proccess);
        spinner = findViewById(R.id.sp_type);
        btnFind = findViewById(R.id.btn_find);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);



        submitDisconnect = findViewById(R.id.submit_disconnect);
        endDisconnect = findViewById(R.id.end_disconnect);
        processDis = findViewById(R.id.disconnect_process);
        processDisconnect = findViewById(R.id.disconnect_process_editText);
        proCode = findViewById(R.id.Pcode);
        ATMFound = findViewById(R.id.ATM_found);
        textNumberNeedDis = findViewById(R.id.Text_numberNeed);
        final String[] placeTypeList = {"ATM", "Bank"};
        final String[] placeNameList = {"ATM", "Bank"};

        spinner.setAdapter(new ArrayAdapter<>(DoProccessActivity.this, android.R.layout.simple_spinner_dropdown_item, placeNameList));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(DoProccessActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }else {
            ActivityCompat.requestPermissions(DoProccessActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
        btnFind.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    int i = spinner.getSelectedItemPosition();
                    String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" + "?location=" + currentLat + "," + currentLong + "&radius=5000" +
                            "&types=" + placeTypeList[i] +
                            "&sensor=true" +
                            "&key=" + getResources().getString(R.string.google_map_key);
                    new PlaceTask().execute(url);
                }
                ATMFound.setVisibility(View.VISIBLE);
                textNumberNeedDis.setVisibility(View.VISIBLE);
                processDis.setVisibility(View.VISIBLE);
                submitDisconnect.setVisibility(View.VISIBLE);



            }
        });
        endDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submitDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String process = processDisconnect.getText().toString();
                if(!process.isEmpty()) {
                    proCode.setVisibility(View.VISIBLE);
                    submitDisconnect.setVisibility(View.GONE);
                }else {
                    Toast.makeText(DoProccessActivity.this, "Please enter number ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if(location!=null){
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();

                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            map = googleMap;

                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat,currentLong),10));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }

    private class PlaceTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... strings) {
            String data =null;
            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException {
        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new BufferedReader(new InputStreamReader(stream)));
        StringBuilder builder = new StringBuilder();
        String line = "";
        while((line = reader.readLine())!=null){
            builder.append(line);
        }
        String data = builder.toString();
        reader.close();
        return data;

    }

    private class ParserTask extends AsyncTask<String,Integer, List<HashMap<String,String>>> {

        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser();
            List<HashMap<String,String>> mapList = null;
            JSONObject object = null;
            try {
                object = new JSONObject(strings[0]);
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {



            for (int i = 0 ; i<hashMaps.size();i++){

                HashMap<String,String> hashMapList = hashMaps.get(i);
                double lat = Double.parseDouble(hashMapList.get("lat"));
                double lng = Double.parseDouble(hashMapList.get("lng"));

                final String name = hashMapList.get("name");

                LatLng latLng = new LatLng(lat,lng);
                MarkerOptions options = new MarkerOptions();

                options.position(latLng);

                options.title(name);

                map.addMarker(options);
                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        ATMFound.setText(name);
                    }
                });
            }
        }
    }
    public void startProcessActivity(){
        Intent startProcess = new Intent(DoProccessActivity.this,ProcessesActivity.class);
        startActivity(startProcess);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) DoProccessActivity.this.getSystemService(DoProccessActivity.this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected==false) {
           return false;
        }else {
            return true;
        }

    }
}