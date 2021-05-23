package com.example.objectlocator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.zip.Inflater;

public class AddFragment extends Fragment {
    //on peut donner un nombre aléatoire moi jai choisi 101
    private static final int PERMISSIONS_FINE_LOCATION = 101;
    TextView tv_lat, tv_lon, tv_alt, tv_time, tv_designation, tv_adress, tv_sensor, tv_updates;
    Switch sw_locationUpdates, sw_gps;
    Button btn_newObject;

    boolean updateOn = false;
    Location currentLocation;
    List<Location> savedLocations;

    // google API pour les services de positionnement, la majorité des applications utilise cet API
    FusedLocationProviderClient fusedLocationProviderClient;

    //locationRequest est un config file pour tous les paramétres qui ont une relation avec FusedLocationProviderClient
    LocationRequest locationRequest;
    LocationCallback locationCallBack;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //pour lier la classe AddFragment avec le layout fragment_add
       View view= inflater.inflate(R.layout.fragment_add,container,false);

        // gonfler(en anglais inflate) la disposition pour ce fragment
        tv_lat = (TextView)view.findViewById(R.id.tv_lat);
        tv_lat = (TextView)view.findViewById(R.id.tv_lat);
        tv_lon =(TextView)view.findViewById(R.id.tv_lon);
        tv_alt = (TextView)view.findViewById(R.id.tv_altitude);
        tv_time =(TextView)view.findViewById(R.id.tv_time);
        tv_designation =(TextView)view.findViewById(R.id.tv_designation);
        tv_adress = (TextView)view.findViewById(R.id.tv_address);
        tv_sensor = (TextView)view.findViewById(R.id.tv_sensor);
        tv_updates = (TextView)view.findViewById(R.id.tv_updates);
        sw_gps = (Switch) view.findViewById(R.id.sw_gps);
        sw_locationUpdates = (Switch) view.findViewById(R.id.sw_locationsupdates);
        btn_newObject =(Button) view.findViewById(R.id.btn_newObject);

        //start

        //set all properties for LocationRequest
        locationRequest = new LocationRequest();
        //how often does the default location check occur?
        locationRequest.setInterval(1000 * 30);
        //how often does the location check occur when set to the most frequent update?
        locationRequest.setFastestInterval(1000 * 5);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //enregistrer la localisation
                Location location = locationResult.getLastLocation();
                updateUIvalues(location);
            }
        };

        btn_newObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on va récupérer la postion (longitude,latitude...)d'un object
                //on va ajouter la localisation de cet objet dans la liste
                MyApplication myApplication =(MyApplication)getActivity().getApplicationContext();
                savedLocations = myApplication.getMyLocations();
                savedLocations.add(currentLocation);
                //ce code est ajouter pour la base de donnée

                MydataBase mydataBase = new MydataBase(getActivity());
                boolean success = mydataBase.addOne(currentLocation,getContext());
                Toast.makeText(v.getContext(), "Success = " + success, Toast.LENGTH_SHORT).show();

                // fin dun code ajouté pour la base de donnée

            }
        });

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tester si sw_gps est selectionner c'est a dire le gps est utilisé
                if (sw_gps.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensors");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");
                }
            }
        });
        updateGPS();

        sw_locationUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_locationUpdates.isChecked()) {
                    //if it's checked turn the tracking on
                    startLocationUpdates();
                } else {
                    // turn off tracking
                    stopLocationUpdates();
                }
            }
        });


        //end


        return view;//fin de onCreate
    }


    private void stopLocationUpdates() {
        tv_updates.setText("the location is NOT being tracked");
        tv_lon.setText("No tracking");
        tv_lat.setText("No tracking");
        tv_designation.setText("No tracking");
        tv_alt.setText("No tracking");
        tv_time.setText("No tracking");
        tv_adress.setText("No tracking");
        tv_sensor.setText("No tracking");
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);

    }



    private void startLocationUpdates() {
        tv_updates.setText("the location is being tracked");
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //Toast.makeText(this,"This App require GPS permission to work properly",Toast.LENGTH_SHORT).show();
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallBack,null );
            updateGPS();
        }
        //fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallBack,null );
        //updateGPS();
        else{
            Toast.makeText(getActivity(),"This App require GPS permission to work properly",Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                }
                else{
                    Toast.makeText(getActivity(),"This App require GPS permission to work properly",Toast.LENGTH_SHORT).show();

                }

        }

    }


    private void updateGPS(){
        //demander l'autorisation de l'utilsateur pour pouvoir utiliser le GPS
        //retourner la position actuelle de l'utilisateur
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //si la permission est accordée
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //on a maintenant la postion, il faut donc remplir les valeurs : longitude ,latitude...
                    updateUIvalues(location);
                    currentLocation=location;
                }

            });
        }
        else {
            //si l'utilisateur n'as pas donné l'autorisation pour utiliser le GPS
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_FINE_LOCATION);
            }
        }

    }

    private void updateUIvalues(Location location) {
        //remplir tous les textView (longitude,latitude...)avec les données de GPS obtenus
        if(location!=null){
            tv_lat.setText(String.valueOf(location.getLatitude()));
            tv_lon.setText(String.valueOf(location.getLongitude()));


            Date d = new Date(location.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            sdf.format(d);
            tv_time.setText(String.valueOf(d));

            if (location.hasAltitude()){
                tv_alt.setText(String.valueOf(location.getAltitude()));
            }
            else{
                tv_alt.setText("Altitude is not available");
            }
            Geocoder geocoder=new Geocoder(getActivity());

            try {
                List<Address>addresses= geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                tv_adress.setText(addresses.get(0).getAddressLine(0));
                //tv_designation.setText(addresses.get(0).getFeatureName());
            }
            catch (Exception e){
                tv_adress.setText("Unable to get the adress");
            }
            Geocoder geocoder1=new Geocoder(getActivity());
            try {
                List<Address>designations= geocoder1.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                tv_designation.setText(designations.get(0).getFeatureName());
            }
            catch (Exception e){
                tv_designation.setText("Unable to get the designation");}

            //On va afficher le nombre d'objets localisés dans la liste
            MyApplication myApplication=(MyApplication)getActivity().getApplicationContext();
            savedLocations=myApplication.getMyLocations();
            //tv_objectCount.setText(Integer.toString(savedLocations.size()));

            
        }
        else {
            // lorsque le gps est atteint on va retouner une localisation par défaut
            tv_designation.setText("Yasmine's home");
            tv_adress.setText("Mesnana,Tanger,Maroc");
            tv_lat.setText("35.752247");
            tv_lon.setText("-5.8509406");
            tv_time.setText("13/05/2021");
            tv_alt.setText("106.03859922276793");


        }
    }

    //end
}
