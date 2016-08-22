package com.cisetech.swipemenudemo;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {
    private Class []classes=new Class[]{
        SimpleActivity.class,DifferentMenuActivity.class
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListAdapter(new ArrayAdapter(this,android.R.layout.simple_list_item_1,android.R.id.text1,classes){
            @Override
            public Object getItem(int position) {
                return classes[position].getSimpleName();
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        startActivity(new Intent(this,classes[position]));
    }
}
