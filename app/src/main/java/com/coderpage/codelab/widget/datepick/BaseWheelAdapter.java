package com.coderpage.codelab.widget.datepick;

import android.database.DataSetObservable;
import android.database.DataSetObserver;

/**
 * @author lc. 2019-08-12 18:30
 * @since 0.7.0
 */
public abstract class BaseWheelAdapter implements IWheelAdapter {

    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }
}
