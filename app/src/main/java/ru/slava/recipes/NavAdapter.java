package ru.slava.recipes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class NavAdapter extends BaseAdapter {
    List<Nav> data;
    Context mCtx;

    public NavAdapter(List<Nav> data, Context mCtx) {
        this.data = data;
        this.mCtx = mCtx;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.ideas_layout, parent, false);

        final Nav current = data.get(position);
        ImageView image = convertView.findViewById(R.id.icon);
        image.setImageDrawable(current.image);

        TextView signTextView = convertView.findViewById(R.id.text1);
        signTextView.setText(current.name);


        return convertView;
    }
}