package com.novas.nongjiuser.uimodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.novas.nongjiuser.R;

/**
 * Created by novas on 15/12/23.
 */
public class BaseInformationView implements BaseView
{
    public View rootview;
    public Context context;
    public LayoutInflater layoutInflater;
    public Object params;
    public BaseInformationView(Context context,Object params)
    {
        this.context=context;
        this.params=params;
        this.layoutInflater=LayoutInflater.from(this.context);
        onCreate();
    }
    @Override
    public void onCreate() {
        rootview=layoutInflater.inflate(R.layout.viewmodel_baseinformation,null);
        rootview.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    @Override
    public View getRootView() {
        return rootview;
    }

    @Override
    public void destroy() {

    }
}
