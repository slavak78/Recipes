package ru.slava.recipes;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.rtoshiro.secure.SecureSharedPreferences;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

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

public class RecipeAuthorAdapter extends RecyclerView.Adapter<RecipeAuthorAdapter.ViewHolder> {
    List<Recipe> data;
    String idd, d, user_id;
    Context mCtx;
    Activity activity;
    String APP_PREFERENCES_ADV = "adv";
    private InterstitialAd mInterstitialAd;
    long savedTime = 0;
    String adv_hash;


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photo, imgfav;
        LinearLayout fav;
        TextView title;
        View v;

        public ViewHolder(View v) {
            super(v);
            this.v = v;
            photo = v.findViewById(R.id.photo);
            title = v.findViewById(R.id.title);
            fav = v.findViewById(R.id.fav);
            imgfav = v.findViewById(R.id.imgfav);
        }
    }


    public RecipeAuthorAdapter(List<Recipe> data, Context mCtx, String user_id, Activity activity) {
        this.data = data;
        this.mCtx = mCtx;
        this.user_id = user_id;
        this.activity = activity;
    }

    @NotNull
    @Override
    public RecipeAuthorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                             int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipeauthor_item, parent, false);
        return new ViewHolder(v);
    }

    public void doadvertise() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(mCtx,activity.getResources().getString(R.string.allscreen_banner_ad_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecipeAuthorAdapter.ViewHolder holder, int position) {
            final Recipe current = data.get(position);
            if (current.photo.equals("")) {
                Glide.with(mCtx).load(R.drawable.nophoto).fitCenter().centerCrop().into(holder.photo);
            } else {
                Glide.with(mCtx).load(current.photo).placeholder(R.drawable.nophoto).fitCenter().centerCrop().error(R.drawable.nophoto).into(holder.photo);
            }
            holder.title.setText(current.title);

            if (current.fav == 1) {
                holder.imgfav.setImageResource(R.drawable.like);
                holder.imgfav.setTag("red");
            } else {
                holder.imgfav.setImageResource(R.drawable.like_gray);
                holder.imgfav.setTag("gray");
            }

            holder.fav.setOnClickListener(view -> {
                d = holder.imgfav.getTag().toString();
                idd = current.idd;
                dozapros(holder.imgfav);
                if (d.equals("red")) {
                    holder.imgfav.setImageResource(R.drawable.like_gray);
                    holder.imgfav.setTag("gray");
                } else {
                    holder.imgfav.setImageResource(R.drawable.like);
                    holder.imgfav.setTag("red");
                }
            });
            holder.v.setOnClickListener(v -> {
                SecureSharedPreferences mSettings = new SecureSharedPreferences(activity);
                if(!mSettings.contains(APP_PREFERENCES_ADV)) {
                    if (System.currentTimeMillis() - savedTime > 60 * 1000) {
                        savedTime = System.currentTimeMillis();
                        doadvertise();
                    }
                } else {
                    if(!mSettings.getString(APP_PREFERENCES_ADV, "").equals(adv_hash)) {
                        if (System.currentTimeMillis() - savedTime > 60 * 1000) {
                            savedTime = System.currentTimeMillis();
                            doadvertise();
                        }
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putString("idd", current.idd);
                bundle.putString("translit", current.title_translit);
                bundle.putString("title", current.title);
                bundle.putString("photo", current.photo);
                SecureSharedPreferences.Editor editor = mSettings.edit();
                editor.putInt("pos", position);
                editor.apply();
                Navigation.findNavController(v).navigate(R.id.viewrecipe, bundle);
            });
    }


    @Override
    public int getItemCount() {
        return data.size();
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

    public void dozapros(ImageView fav) {
        OkHttpClient client = doclient();
        Request request = new Request.Builder()
                .url("https://www.книгавкусныхидей.рф/recipes/change.php?recipe_id=" + idd + "&user_id=" + user_id + "&d=" + d)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                activity.runOnUiThread(() -> fail(fav));
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

    public void fail(ImageView fav) {
        if (fav.getTag().toString().equals("gray")) {
            fav.setImageResource(R.drawable.like);
            fav.setTag("red");
        } else {
            fav.setImageResource(R.drawable.like_gray);
            fav.setTag("gray");
        }
    }
}
