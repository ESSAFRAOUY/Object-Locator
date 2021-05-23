package com.example.objectlocator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UpdateFragment extends Fragment {
    EditText edit_designation,edit_id,edit_longitude,edit_latitude,edit_altitude,edit_time;
    Button btn_update;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //pour lier la classe AddFragment avec le layout fragment_add
        View view= inflater.inflate(R.layout.fragment_update,container,false);
        edit_designation=(EditText)view.findViewById(R.id.edit_designation);
        edit_id=(EditText)view.findViewById(R.id.edit_id);
        edit_longitude=(EditText)view.findViewById(R.id.edit_long);
        edit_latitude=(EditText)view.findViewById(R.id.edit_lat);
        edit_altitude=(EditText)view.findViewById(R.id.edit_alt);
        edit_time=(EditText)view.findViewById(R.id.edit_time);
        btn_update=(Button) view.findViewById(R.id.btn_update);

        MydataBase db=new MydataBase(getContext());

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id =edit_id.getText().toString();
                String designation = edit_designation.getText().toString();
                String latitude = edit_latitude.getText().toString();
                String longitude = edit_longitude.getText().toString();
                String altitude = edit_altitude.getText().toString();
                String time = edit_time.getText().toString();
                // String adresse = et_adresse.getText().toString();

                Boolean checkupdatedata = db.updateObject(id,designation,latitude,longitude,altitude,time);
                if(checkupdatedata==true) {
                    Toast.makeText(getContext(), "Object Updated successfully", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "Object was Not Updated", Toast.LENGTH_SHORT).show();;
            }

        });




        return view;
    }
}
