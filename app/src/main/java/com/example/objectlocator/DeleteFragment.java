package com.example.objectlocator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class DeleteFragment extends Fragment {
    Button btn_delete;
    EditText et_id;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //pour lier la classe AddFragment avec le layout fragment_add
        View view= inflater.inflate(R.layout.fragment_delete,container,false);
        btn_delete=(Button)view.findViewById(R.id.btn_delete);
        et_id=(EditText)view.findViewById(R.id.et_id);

        MydataBase db=new MydataBase(getContext());
        ArrayList<ObjectModel> allObjects;
        allObjects= (ArrayList<ObjectModel>) db.getAllObjects();
        ListView listView = (ListView) view.findViewById(R.id.listObjects1);


        listView.setAdapter(new ListviewAdapter(getActivity(), allObjects));


        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MydataBase mydataBase = new MydataBase(ObjectsList.this);
                boolean success = db.deleteObject(et_id.getText().toString());
                Toast.makeText(getContext(), "Deleted successfully! " , Toast.LENGTH_SHORT).show();
                List<ObjectModel> allObjects=db.getAllObjects();
                listView.setAdapter(new ListviewAdapter(getActivity(), (ArrayList<ObjectModel>) allObjects));
            }
        });




        return view;

        
    }
}
