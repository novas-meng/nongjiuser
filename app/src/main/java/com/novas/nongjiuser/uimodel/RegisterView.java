package com.novas.nongjiuser.uimodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.novas.nongjiuser.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by novas on 15/12/23.
 */
public class RegisterView implements BaseView
{
    public ListView rootview;
    public Context context;
    public LayoutInflater layoutInflater;
    public Object params;

    public RegisterView(Context context,Object params)
    {
        this.context=context;
        this.params=params;
        this.layoutInflater=LayoutInflater.from(context);
        onCreate();
    }
    @Override
    public void onCreate() {
        rootview=(ListView)layoutInflater.inflate(R.layout.viewmodel_listview,null);
        List<View> list=new ArrayList<>();
        list.add(new BaseInformationView(context,null).getRootView());
        list.add(new BaseInformationView(context,null).getRootView());
        list.add(new BaseInformationView(context,null).getRootView());
        list.add(new BaseInformationView(context,null).getRootView());
        list.add(new BaseInformationView(context,null).getRootView());
        list.add(new BaseInformationView(context,null).getRootView());
        list.add(new BaseInformationView(context,null).getRootView());
        list.add(new Button(context));
        ListViewAdapter listViewAdapter=new ListViewAdapter(list);
        rootview.setAdapter(listViewAdapter);
    }

    @Override
    public View getRootView() {
        return rootview;
    }

    @Override
    public void destroy() {

    }
}
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
