package com.novas.nongjiuser.uimodel;

import android.content.Context;
import android.view.View;

/**
 * Created by novas on 15/12/23.
 */
public interface BaseView
{
    public void onCreate();
    public View getRootView();
    public void destroy();
}
