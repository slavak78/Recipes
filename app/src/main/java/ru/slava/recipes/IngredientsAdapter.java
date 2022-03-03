package ru.slava.recipes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {
    List<Ingredients> data;
    Context mCtx;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ing, quanity;
        View v, vi;
        ImageView ing_img;

        public ViewHolder(View v) {
            super(v);
            this.v = v;
            ing = v.findViewById(R.id.ing);
            quanity = v.findViewById(R.id.quantity);
            ing_img = v .findViewById(R.id.ing_img);
            vi = v.findViewById(R.id.vi);
        }
    }

    public IngredientsAdapter(List<Ingredients> data, Context mCtx) {
        this.data = data;
        this.mCtx = mCtx;
    }


    @NotNull
    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                            int viewType) {
        View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_item, parent, false);
        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull final IngredientsAdapter.ViewHolder holder, int position) {
        final Ingredients current = data.get(position);
        holder.ing.setText(current.ing);
        String txt = current.quantity + " " + current.unit;
        holder.quanity.setText(txt);
        Glide.with(mCtx).load(current.img).placeholder(R.drawable.nophoto).fitCenter().centerCrop().error(R.drawable.nophoto).into(holder.ing_img);
        if(position==data.size()-1) {
            holder.vi.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}