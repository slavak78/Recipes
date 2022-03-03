package ru.slava.recipes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GalleryAdapter extends PagerAdapter {
    private final Context mContext;
    private final ArrayList<String> dd_arr;

    public GalleryAdapter(Context context, ArrayList<String> dd_arr){
        this.mContext = context;
        this.dd_arr = dd_arr;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.page, container,
                false);
        ImageView mr = itemView.findViewById(R.id.imga);
        Glide.with(mContext).load(dd_arr.get(position)).placeholder(R.drawable.nophoto).fitCenter().centerCrop().error(R.drawable.nophoto).into(mr);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public int getCount() {
        return dd_arr.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
