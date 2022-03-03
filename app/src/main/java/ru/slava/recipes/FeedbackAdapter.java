package ru.slava.recipes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ru.slava.recipes.AvatarView.GlideLoader;
import ru.slava.recipes.AvatarView.IImageLoader;
import ru.slava.recipes.AvatarView.views.AvatarView;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {
    List<Feedback> data;
    String user_id, idd, d1;
    Context mCtx;
    CoordinatorLayout scr;
    Activity activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        AvatarView avatar_feedback, avatar;
        TextView title, likes, fio, fio_feedback, times, text_feedback;
        LinearLayout tolikes;
        View v;


        public ViewHolder(View v) {
            super(v);
            this.v = v;
            avatar_feedback = v.findViewById(R.id.avatar_feedback);
            avatar = v.findViewById(R.id.avatar);
            fio = v.findViewById(R.id.fio);
            fio_feedback = v.findViewById(R.id.fio_feedback);
            times = v.findViewById(R.id.times);
            text_feedback = v.findViewById(R.id.text_feedback);
            photo = v.findViewById(R.id.photo);
            title = v.findViewById(R.id.title);
            likes = v.findViewById(R.id.likes);
            tolikes = v.findViewById(R.id.tolikes);
        }
    }


    public FeedbackAdapter(List<Feedback> data, Context mCtx, String user_id, CoordinatorLayout scr, Activity activity) {
        this.data = data;
        this.mCtx = mCtx;
        this.user_id = user_id;
        this.scr = scr;
        this.activity = activity;
    }

    @NotNull
    @Override
    public FeedbackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedbackAdapter.ViewHolder holder, int position) {
        final Feedback current = data.get(position);
        IImageLoader imageLoader = new GlideLoader();
        if (current.avatar.equals("")) {
            current.avatar = "123";
        }
        imageLoader.loadImage(holder.avatar, current.avatar, current.fio);

        IImageLoader imageLoader1 = new GlideLoader();
        if (current.avatar_feedback.equals("")) {
            current.avatar_feedback = "123";
        }
        imageLoader1.loadImage(holder.avatar_feedback, current.avatar_feedback, current.fio_feedback);
        holder.fio.setText(current.fio);
        holder.fio_feedback.setText(current.fio_feedback);
        holder.times.setText(current.times);
        if (current.photo.equals("")) {
            Glide.with(mCtx).load(R.drawable.nophoto).fitCenter().centerCrop().into(holder.photo);
        } else {
            Glide.with(mCtx).load(current.photo).placeholder(R.drawable.nophoto).fitCenter().centerCrop().error(R.drawable.nophoto).into(holder.photo);
        }
        holder.title.setText(current.title);
        holder.likes.setText(String.valueOf(current.likes));

        holder.v.setOnClickListener(v -> {
      /*      Fragment viewfeed = new ViewFeedbackFragment();
            Bundle bundle = new Bundle();
            bundle.putString("idd", current.idd);
            viewfeed.setArguments(bundle);
            ((GlavActivity) mCtx).loadFragment(viewfeed);*/
        });

        holder.avatar_feedback.setOnClickListener(v -> {
      /*      Fragment viewuser = new ViewUserFragment();
            Bundle bundle = new Bundle();
            bundle.putString("user_id", current.idd_user);
            viewuser.setArguments(bundle);
            ((GlavActivity) mCtx).loadFragment(viewuser);*/
        });

        holder.fio.setOnClickListener(v -> {
    /*        Fragment viewrecipe = new ViewRecipeFragment();
            Bundle bundle = new Bundle();
            bundle.putString("idd", current.idd_recipe);
            viewrecipe.setArguments(bundle);
            ((GlavActivity) mCtx).loadFragment(viewrecipe);*/
        });

        holder.avatar.setOnClickListener(v -> {
      /*      Fragment viewrecipe = new ViewRecipeFragment();
            Bundle bundle = new Bundle();
            bundle.putString("idd", current.idd_recipe);
            viewrecipe.setArguments(bundle);
            ((GlavActivity) mCtx).loadFragment(viewrecipe);*/
        });

        holder.title.setOnClickListener(v -> {
    /*        Fragment viewrecipe = new ViewRecipeFragment();
            Bundle bundle = new Bundle();
            bundle.putString("idd", current.idd_recipe);
            viewrecipe.setArguments(bundle);
            ((GlavActivity) mCtx).loadFragment(viewrecipe);*/
        });

        if(current.vid==1) {
            holder.tolikes.setBackground(ResourcesCompat.getDrawable(mCtx.getResources(), R.drawable.circlecornerspink, null));
            holder.tolikes.setTag("pink");
            holder.likes.setTextColor(ContextCompat.getColor(mCtx, R.color.ed3851));
        } else {
            holder.tolikes.setBackground(ResourcesCompat.getDrawable(mCtx.getResources(), R.drawable.circlecornerscolor, null));
            holder.tolikes.setTag("gray");
            holder.likes.setTextColor(ContextCompat.getColor(mCtx, R.color.c919191));
        }

        holder.tolikes.setOnClickListener(view -> {
            if (!user_id.equals("0")) {
                d1 = holder.tolikes.getTag().toString();
                idd = current.idd;
                dozapros1(holder.tolikes, holder.likes, current.likes);
                if (d1.equals("pink")) {
                    holder.tolikes.setBackground(ResourcesCompat.getDrawable(mCtx.getResources(), R.drawable.circlecornerscolor, null));
                    holder.tolikes.setTag("gray");
                    holder.likes.setTextColor(ContextCompat.getColor(mCtx, R.color.c919191));
                    String vse = String.valueOf(current.likes - 1);
                    current.likes = current.likes - 1;
                    holder.likes.setText(vse);
                } else {
                    holder.tolikes.setBackground(ResourcesCompat.getDrawable(mCtx.getResources(), R.drawable.circlecornerspink, null));
                    holder.tolikes.setTag("pink");
                    holder.likes.setTextColor(ContextCompat.getColor(mCtx, R.color.ed3851));
                    String vse = String.valueOf(current.likes + 1);
                    current.likes = current.likes + 1;
                    holder.likes.setText(vse);
                }
            }
        });
    }

    public OkHttpClient doclient() {
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).allEnabledTlsVersions().allEnabledCipherSuites().build();
        List<ConnectionSpec> specs = new ArrayList<>();
        specs.add(ConnectionSpec.CLEARTEXT);
        specs.add(spec);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectionSpecs(specs);
        return builder.build();
    }


    public void dozapros1(LinearLayout tolikes, TextView likes, int likes1) {
        OkHttpClient client = doclient();
        Request request = new Request.Builder()
                .url("https://www.книгавкусныхидей.рф/recipes/changelikefeed.php?feedback_id=" + idd + "&user_id=" + user_id + "&d1=" + d1)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                fail3(tolikes, likes, likes1);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    assert responseBody != null;
                }
            }
        });
    }

    public void fail3(LinearLayout tolikes, TextView likes, int likes1) {
        if (tolikes.getTag().toString().equals("gray")) {
            tolikes.setBackground(ResourcesCompat.getDrawable(mCtx.getResources(), R.drawable.circlecornerspink, null));
            tolikes.setTag("pink");
            likes.setTextColor(ContextCompat.getColor(mCtx, R.color.ed3851));
            String vse = String.valueOf(likes1+1);
            likes.setText(vse);
        } else {
            tolikes.setBackground(ResourcesCompat.getDrawable(mCtx.getResources(), R.drawable.circlecornerscolor, null));
            tolikes.setTag("gray");
            likes.setTextColor(ContextCompat.getColor(mCtx, R.color.c919191));
            String vse = String.valueOf(likes1-1);
            likes.setText(vse);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}