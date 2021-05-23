package com.example.objectlocator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import android.content.Context;

public class ListviewAdapter extends BaseAdapter {

    // déclaration des variables
    private LayoutInflater mInflater;
    private static ArrayList<ObjectModel> objectModel;
    private int position;
    private View convertView;
    private ViewGroup parent;

    //La classe (AdapteurListView) qui réccuperer et remplir "Objectmodel"
    public ListviewAdapter(Context context,ArrayList<ObjectModel> objectModel) {
        mInflater=LayoutInflater.from(context);
        this.objectModel=objectModel;
    }

    //Pour retourner le nombre de lignes
    @Override
    public int getCount() {
        return objectModel.size();
    }

    //Pour retourner l'item de la ligne actuelle
    @Override
    public Object getItem(int position) {
        return objectModel.get(position);
    }
    //Pour retourner un indice par rapport Ã  la ligne actuelle
    @Override
    public long getItemId(int position) {
        return 0;
    }
    public String getselectId(int position){
        return  objectModel.get(position).getId();
    }



    //Pour retourner la ligne (view) formatée avec getsion des événements
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // déclaration d'un Attribut
        AttributeView attributeView;
        // si la ligne n'existe pas encore
        if(convertView == null){
            //la ligne est construite avec un formatage (inflater) reliÃ© Ã  hopitaladapterview
            convertView = mInflater.inflate(R.layout.object_list_adapter, null);
            //chaque propriÃ©tÃ© du Attribute est reliÃ© Ã  un propriÃ©tÃ© graphique
            attributeView = new AttributeView();
            attributeView.designation = (TextView) convertView.findViewById(R.id.tv_designation1);
            attributeView.latitude= (TextView) convertView.findViewById(R.id.tv_lat1);
            attributeView.longitude= (TextView) convertView.findViewById(R.id.tv_lon1);
            attributeView.id= (TextView) convertView.findViewById(R.id.tv_id1);
            attributeView.time= (TextView) convertView.findViewById(R.id.tv_time1);
            attributeView.altitude= (TextView) convertView.findViewById(R.id.tv_alt);
            // affecter l'attribut Ã  la vue
            convertView.setTag(attributeView);
        } else {
            //récupération du holder dans la ligne existante
            attributeView = (AttributeView) convertView.getTag();
        }
        //la valorisation du contenu du attribut (donc de la ligne)
        attributeView.designation.setText(objectModel.get(position).getNom());
        attributeView.latitude.setText(objectModel.get(position).getLatitude());
        attributeView.longitude.setText(objectModel.get(position).getLongitude());
        attributeView.id.setText(objectModel.get(position).getId());
        attributeView.time.setText(objectModel.get(position).getTime());
        attributeView.altitude.setText(objectModel.get(position).getAltitude());

        return convertView;
    }

}
// la création de la classe (AttributeView)
class AttributeView{
    TextView designation,longitude,latitude,id,time,altitude;
    //*********************************************************
}
