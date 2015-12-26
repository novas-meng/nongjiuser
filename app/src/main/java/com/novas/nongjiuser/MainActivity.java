package com.novas.nongjiuser;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.novas.nongjiuser.uimodel.AdapterViewPro;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    class ListViewAdapter extends BaseAdapter
    {
        public List<View> list;
        public ListViewAdapter(List<View> list)
        {
            this.list=list;
        }
        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return list.get(position);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      //  LinearLayout linearLayout=(LinearLayout)this.getLayoutInflater().inflate(R.layout.activity_main,null);
       // setContentView(linearLayout);
        setContentView(R.layout.activity_main);
/*
        AdapterViewPro adapterViewPro=(AdapterViewPro)this.findViewById(R.id.adapterview);
        List<View> list=new ArrayList<>();
        list.add(new Button(this));
        list.add(new Button(this));
        ListViewAdapter adapter=new ListViewAdapter(list);
        adapterViewPro.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
