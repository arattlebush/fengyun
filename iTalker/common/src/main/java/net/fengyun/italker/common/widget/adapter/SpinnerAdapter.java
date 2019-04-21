package net.fengyun.italker.common.widget.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.fengyun.italker.common.R;

import java.util.List;

/**
 * Spinner的适配器
 *
 * @author fengyun
 */

public class SpinnerAdapter implements android.widget.SpinnerAdapter {

    private List<String> mData;
    private Context mContext;

    public SpinnerAdapter(List<String> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(mContext);
        textView.setTextSize(16);
        textView.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
        textView.setText(mData.get(position));
        return textView;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(mContext);
        textView.setTextSize(16);
        textView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
        textView.setText(mData.get(position));
        return textView;
    }
}
