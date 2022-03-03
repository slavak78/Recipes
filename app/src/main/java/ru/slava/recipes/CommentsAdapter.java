package ru.slava.recipes;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;


import org.jetbrains.annotations.NotNull;

import java.util.List;

import ru.slava.recipes.AvatarView.GlideLoader;
import ru.slava.recipes.AvatarView.IImageLoader;
import ru.slava.recipes.AvatarView.views.AvatarView;


public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    List<Comments> data;
    String user_id;
    Context mCtx;
    Activity activity;
    CoordinatorLayout scr;


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView comment, fio, stars;
        View v,vi;
        AvatarView avatar;
        LinearLayout clicks;

        public ViewHolder(View v) {
            super(v);
            this.v = v;
            comment = v.findViewById(R.id.comment);
            fio = v.findViewById(R.id.fio);
            avatar = v.findViewById(R.id.avatar);
            clicks = v.findViewById(R.id.cli);
            vi = v.findViewById(R.id.vi);
            stars = v.findViewById(R.id.stars);
        }
    }

    public CommentsAdapter(List<Comments> data, String user_id, Context mCtx, Activity activity, CoordinatorLayout scr) {
        this.data = data;
        this.user_id = user_id;
        this.mCtx = mCtx;
        this.activity = activity;
        this.scr = scr;
    }

    public void addNewItems(List<Comments> data, String user_id, Context mCtx, Activity activity, CoordinatorLayout scr) {
        this.data = data;
        this.user_id = user_id;
        this.mCtx = mCtx;
        this.activity = activity;
        this.scr = scr;
    }

    @NotNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                         int viewType) {
        View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final CommentsAdapter.ViewHolder holder, int position) {
        final Comments current = data.get(position);
        holder.comment.setText(current.comment);
        holder.fio.setText(current.name);
        holder.stars.setText(current.stars);
        IImageLoader imageLoader = new GlideLoader();
        if (current.avatar.equals("")) {
            current.avatar = "123";
        }
        imageLoader.loadImage(holder.avatar, current.avatar, current.name);
        holder.clicks.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("user_id", current.idd);
            Navigation.findNavController(v).navigate(R.id.viewuser, bundle);
        });
        if(position==data.size()-1) {
            holder.vi.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}