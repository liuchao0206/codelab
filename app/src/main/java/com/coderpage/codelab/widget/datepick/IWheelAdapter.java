package com.coderpage.codelab.widget.datepick;

import android.database.DataSetObserver;

/**
 * @author lc. 2019-08-08 11:01
 * @since 0.7.0
 */
public interface IWheelAdapter {

    void registerDataSetObserver(DataSetObserver observer);

    void unregisterDataSetObserver(DataSetObserver observer);

    int getCount();

    String getText(int position);
}
