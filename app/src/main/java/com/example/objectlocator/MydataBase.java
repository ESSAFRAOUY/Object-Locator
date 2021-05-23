package com.example.objectlocator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.Editable;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MydataBase extends SQLiteOpenHelper {
    public static final String OBJECTS_TABLE = "OBJECTS_TABLE";
    // se sont des colonnes de la tables OBJECTS_TABLE
    public static final String OBJECT_DESIGNATION = "OBJECT_DESIGNATION";
    public static final String OBJECT_LATITUDE = "OBJECT_LATITUDE";
    public static final String OBJECT_LONGITUDE = "OBJECT_LONGITUDE";
    public static final String OBJECT_ALTITUDE = "OBJECT_ALTITUDE";
    public static final String OBJECT_TIME = "OBJECT_TIME";
    public static final String COLUMN_ID = "ID";

    public MydataBase(@Nullable Context context) {
        super(context, "objects.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // cette méthode est appelée lors du premier acces a la base de donnée
        String createTableStatement= "CREATE TABLE " + OBJECTS_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + OBJECT_DESIGNATION + " TEXT," + OBJECT_LATITUDE + " FLOAT," + OBJECT_LONGITUDE + " FLOAT," + OBJECT_ALTITUDE + " FLOAT," + OBJECT_TIME + " DATETIME )";

        db.execSQL(createTableStatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //cette méthode est appelée lorsque la base de donnée change

    }

    public boolean addOne(Location location,Context context){

        SQLiteDatabase db =this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(OBJECT_LATITUDE,location.getLatitude());
        cv.put(OBJECT_LONGITUDE,location.getLongitude());
        //cv.put(OBJECT_ALTITUDE,location.getAltitude());


        Date d = new Date(location.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdf.format(d);
        cv.put(OBJECT_TIME,String.valueOf(d));


        if (location.hasAltitude()){
            cv.put(OBJECT_ALTITUDE,location.getAltitude());
        }
        else{
            cv.put(OBJECT_ALTITUDE,"Altitude is not available");
        }

        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address>addresses= geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            cv.put(OBJECT_DESIGNATION,addresses.get(0).getFeatureName());
        }
        catch (Exception e){
            cv.put(OBJECT_DESIGNATION,"Unable to get designation");
        }

        long insert = db.insert(OBJECTS_TABLE, null, cv);
        if(insert==-1){
            return false;
        }
        else{
            return true;
        }

    }

    // la fonction qui permet de supprimer un objet de la base de données
    public boolean deleteObject(String id_Value){
        SQLiteDatabase db =this.getWritableDatabase();
        /** String queryString = "DELETE *FROM " + OBJECTS_TABLE + " WHERE " + COLUMN_ID + "="+id_Value;
         Cursor cursor = db.rawQuery(queryString,null);
         if (cursor.moveToNext()){
         return true;
         }
         else {
         return false;
         }**/
        return db.delete(OBJECTS_TABLE, COLUMN_ID + "=" + id_Value, null) > 0;
    }


    // cette fonction pour faire l'update d'un objet
    public Boolean updateObject(String id, String designation,String latitude,String longitude,String altitude,String time) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(OBJECT_DESIGNATION, designation);
        contentValues.put(OBJECT_LATITUDE, latitude);
        contentValues.put(OBJECT_LONGITUDE, longitude);
        contentValues.put(OBJECT_ALTITUDE, altitude);
        contentValues.put(OBJECT_TIME, time);
        //contentValues.put(OBJECT_LATITUDE, latitude);
        /**Cursor cursor = DB.rawQuery("Select * from OBJECTS_TABLE where COLUMN_ID= ?", new String[]{id});
         if (cursor.getCount() > 0) {
         long result = DB.update(OBJECTS_TABLE, contentValues, COLUMN_ID + "=" + id,null );
         if (result == -1) {
         return false;
         } else {
         return true;
         }
         } else {
         return false;
         }**/
        return DB.update(OBJECTS_TABLE, contentValues, COLUMN_ID + "=" + id,null )>0;
    }


    // cette fonction permet d'afficher tous les object existants dans la base de données
    public List<ObjectModel> getAllObjects(){

        List<ObjectModel> objectsList = new ArrayList<>();
        //extraire les données a partir de la base de données
        String queryString = "SELECT *FROM  " + OBJECTS_TABLE ;
        SQLiteDatabase db =this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if (cursor.moveToFirst()){
            // on va boucler avec le cursor et récupérer les objets et les ajouter dans la liste
            do {
                String objectID=cursor.getString(0);
                String objectDesignation=cursor.getString(1);
                String objectLatitude=cursor.getString(2);
                String objectLongitude=cursor.getString(3);
                String objectAltitude=cursor.getString(4);
                String objectTime=cursor.getString(5);
                ObjectModel newObject=new ObjectModel(objectID,objectDesignation,objectLongitude,objectLatitude,objectAltitude,objectTime);
                //ObjectModel newObject=new ObjectModel(12,"objectDesignation",45,75,57,"7575");
                objectsList.add(newObject);


            }while (cursor.moveToNext());
        }
        else {
            // s'il n'y a pas de nouveau objets on va rien ajouter a la liste.

        }

        // on va fermer le cursor et la base de donnée après l'utilisation

        cursor.close();
        db.close();
        return objectsList;
    }

}
