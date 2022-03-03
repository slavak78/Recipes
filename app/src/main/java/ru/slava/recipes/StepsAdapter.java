package ru.slava.recipes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {
    List<Steps> data;
    Context mCtx;
    View v;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView description,orders;
        View v;
        LinearLayout lin;

        public ViewHolder(View v) {
            super(v);
            this.v = v;
            description = v.findViewById(R.id.description);
            lin = v.findViewById(R.id.forphotos);
            orders = v.findViewById(R.id.orders);
        }
    }

    public StepsAdapter(List<Steps> data, Context mCtx) {
        this.data = data;
        this.mCtx = mCtx;
    }


    @NotNull
    @Override
    public StepsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_item, parent, false);
        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull final StepsAdapter.ViewHolder holder, int position) {
        final Steps current = data.get(position);
        holder.description.setText(current.description);
        holder.orders.setText(current.orders);
        if(current.photos.size()>0) {

            for (int i = 0; i < current.photos.size(); i++) {
                View v1 = LayoutInflater.from(v.getContext()).inflate(R.layout.stepphoto_item, holder.lin, true);
                ImageView img = v1.findViewById(R.id.step_photo);
                Glide.with(mCtx).load(current.photos.get(i)).placeholder(R.drawable.nophoto).fitCenter().centerCrop().error(R.drawable.nophoto).into(img);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}