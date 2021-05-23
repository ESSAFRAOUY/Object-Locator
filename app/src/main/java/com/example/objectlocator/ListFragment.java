package com.example.objectlocator;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
public class ListFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //pour lier la classe AddFragment avec le layout fragment_add
        View v=  inflater.inflate(R.layout.fragment_list, container, false);


        MydataBase db=new MydataBase(getContext());
        ArrayList<ObjectModel> allObjects;
        allObjects= (ArrayList<ObjectModel>) db.getAllObjects();
        GridView gridView = (GridView) v.findViewById(R.id.listObjects);


        gridView.setAdapter(new ListviewAdapter(getActivity(), allObjects));
        //pour enregistrer les objets dans un fichier .TXT et .CSV dans SD card
        File file = new File("/sdcard/myObjects.txt");
        File file1 = new File("/sdcard/myObjects.csv");
        try {
            file.createNewFile();
            //a chaque fois on va supprimer l'acienne version d'u fichier et on va la mettre a jour lorsque la liste
            // des objets est modifiée
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            //writer.print("\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //pour enregistrer les objets dans un fichier .txt dans SD card

        for (int i = 0; i <allObjects.size(); ++i) {
            String data=allObjects.get(i).toString();
            try{
                FileOutputStream fout = new FileOutputStream(file , true);
                String newligne=System.getProperty("line.separator");
                String dt=data+newligne;
                fout.write(dt.getBytes());
                fout.close();
                //si l'enregistrement est bien éfectué on va afficher un message toast'DATA WRITTEN in a .CSV file'
                Toast.makeText(getContext(), "Data written in .TXT file", Toast.LENGTH_SHORT).show();

            }
            //si la création du fichier n'a pas été efféctué correctement
            catch ( Exception e ){
                Toast.makeText(getContext() , e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        }



        //pour enregistrer les objets dans un fichier CSV
        try {
            file1.createNewFile();
            //a chaque fois on va supprimer l'acienne version d'u fichier et on va la mettre a jour lorsque la liste
            // des objets est modifiée
            PrintWriter writer = new PrintWriter(file1);
            writer.print("");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //pour enregistrer les objets dans un fichier .CSV dans SD card
        for (int i = 0; i <allObjects.size(); ++i) {
            String data=allObjects.get(i).toString();
            try{
                FileOutputStream fout = new FileOutputStream(file1 , true);
                String newligne=System.getProperty("line.separator");
                String dt=data+newligne;
                fout.write(dt.getBytes());
                fout.close();
                //si l'enregistrement est bien éfectué on va afficher un message toast'DATA WRITTEN in a .CSV file'
                Toast.makeText(getContext(), "Data written in .CSV file", Toast.LENGTH_SHORT).show();

            }
            //si la création du fichier n'a pas été efféctué correctement
            catch ( Exception e ){
                Toast.makeText(getContext() , e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        }






        return v;
    }
}
