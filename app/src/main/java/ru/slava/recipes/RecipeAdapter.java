package ru.slava.recipes;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rtoshiro.secure.SecureSharedPreferences;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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


public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    List<Recipe> data;
    String idd, d, user_id;
    Context mCtx;
    PopupMenu popup;
    Activity activity;
    private final int TYPE_ITEM1 = 0;
    boolean zap = true;
    String APP_PREFERENCES_ADV = "adv";
    private InterstitialAd mInterstitialAd;
    long savedTime = 0;
    String adv_hash;


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photo, imgfav;
        LinearLayout fav, clicks, persons, view_rate;
        AvatarView avatar;
        TextView title, fio, stars;
        View v;
        ImageButton popup1;
        RelativeTimeTextView ago;

        public ViewHolder(View v) {
            super(v);
            this.v = v;
            avatar = v.findViewById(R.id.avatar);
            fio = v.findViewById(R.id.fio);
            photo = v.findViewById(R.id.photo);
            title = v.findViewById(R.id.title);
            fav = v.findViewById(R.id.fav);
            popup1 = v.findViewById(R.id.popup);
            imgfav = v.findViewById(R.id.imgfav);
            clicks = v.findViewById(R.id.clicks);
            ago = v.findViewById(R.id.ago);
            persons = v.findViewById(R.id.persons);
            view_rate = v.findViewById(R.id.view_rate);
            stars = v.findViewById(R.id.stars);
        }
    }


    public RecipeAdapter(List<Recipe> data, Context mCtx, String user_id, Activity activity) {
        this.data = data;
        this.mCtx = mCtx;
        this.user_id = user_id;
        this.activity = activity;


    }

    public void addNewItems(List<Recipe> data, Context mCtx, String user_id, Activity activity) {
        this.data = data;
        this.mCtx = mCtx;
        this.user_id = user_id;
        this.activity = activity;
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

    @NotNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                       int viewType) {
        View v;
        if(viewType==TYPE_ITEM1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersinrecipes_item, parent, false);
        }
        return new ViewHolder(v);
    }


    private void showPopupMenu(View view, int subscribe, String id_user, String title_translit) {
        popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        if (subscribe == 1) {
            popup.getMenu().getItem(0).setVisible(false);
            popup.getMenu().getItem(1).setVisible(true);
        } else {
            popup.getMenu().getItem(0).setVisible(true);
            popup.getMenu().getItem(1).setVisible(false);
        }
        popup.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.subscribe) {
                dozaprossub(popup, 1, id_user);
                popup.getMenu().getItem(0).setVisible(false);
                popup.getMenu().getItem(1).setVisible(true);
            }
            if (menuItem.getItemId() == R.id.unsubscribe) {
                dozaprossub(popup, 0, id_user);
                popup.getMenu().getItem(0).setVisible(true);
                popup.getMenu().getItem(1).setVisible(false);
            }
            if (menuItem.getItemId() == R.id.share) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.книгавкусныхидей.рф/recipes/" + title_translit);
                sendIntent.setType("text/html");
                mCtx.startActivity(sendIntent);
            }
            return true;

        });
        popup.show();
    }

    @Override
    public void onBindViewHolder(@NonNull final RecipeAdapter.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if(type==TYPE_ITEM1) {
            TimeZone tz = TimeZone.getDefault();
            Date now = new Date();
            int offset = tz.getOffset(now.getTime());
            final Recipe current = data.get(position);
            long timetoreal = current.ago + offset;
            holder.ago.setReferenceTime(timetoreal);
            IImageLoader imageLoader = new GlideLoader();
            if (current.avatar.equals("")) {
                current.avatar = "123";
            }
            if(current.view_rate==1) {
                holder.view_rate.setVisibility(View.VISIBLE);
                holder.stars.setText(current.stars);
            }
            imageLoader.loadImage(holder.avatar, current.avatar, current.fio);
            holder.fio.setText(current.fio);
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


            holder.clicks.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("user_id", current.idd_user);
                Navigation.findNavController(v).navigate(R.id.viewuser, bundle);
            });

              holder.popup1.setOnClickListener(v -> showPopupMenu(holder.popup1, current.subscribe, current.idd_user, current.title_translit));
        } else {
            if(zap) {
                dozaprosusers(holder.persons);
                zap = false;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        int TYPE_ITEM2 = 1;
        if (position == 1) return TYPE_ITEM2;
        return TYPE_ITEM1;
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

    public void dozaprossub(PopupMenu popup, int subscribe, String id_user) {
        OkHttpClient client = doclient();
        Request request = new Request.Builder()
                .url("https://www.книгавкусныхидей.рф/recipes/changesub.php?user_id=" + user_id + "&author_id=" + id_user + "&subscribe=" + subscribe)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                activity.runOnUiThread(() -> fail1(popup, subscribe));
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

    public void fail1(PopupMenu popup, int subscribe) {
        if (subscribe == 1) {
            popup.getMenu().getItem(0).setVisible(true);
            popup.getMenu().getItem(1).setVisible(false);
        } else {
            popup.getMenu().getItem(0).setVisible(false);
            popup.getMenu().getItem(1).setVisible(true);
        }
    }


    public void dozaprosusers(LinearLayout persons) {
        OkHttpClient client = doclient();
        Request request = new Request.Builder()
                .url("https://www.книгавкусныхидей.рф/recipes/viewsixusers.php?user_id=" + user_id)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    assert responseBody != null;
                    String res = responseBody.string();
                    activity.runOnUiThread(() -> dosomeusers(res,persons));
                }
            }
        });
    }

    public void dosomeusers(String res, LinearLayout persons) {
        JSONObject dataJsonObj;
        try {
            dataJsonObj = new JSONObject(res);
            int count = dataJsonObj.getInt("count");
            adv_hash = dataJsonObj.getString("adv_hash");
            if(count>0) {

                LinearLayout.LayoutParams lp99 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout lin = new LinearLayout(mCtx);
                lin.setLayoutParams(lp99);
                lin.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
             //   lp.setMargins(0,30,0, 0);
                persons.setLayoutParams(lp);
                MaterialCardView cd = new MaterialCardView(mCtx);
                cd.setCardElevation(0);
                cd.setMaxCardElevation(0);
                cd.setStrokeWidth(3);
                cd.setStrokeColor(ContextCompat.getColor(mCtx, R.color.dcdcdc));
                cd.setRadius(10);

                StringBuilder stroke;
                TextView str = new TextView(mCtx);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp1.setMargins(30,30,30,0);
                str.setLayoutParams(lp1);
                stroke = new StringBuilder(activity.getResources().getString(R.string.joined));
                JSONArray users = dataJsonObj.getJSONArray("users");
                int y;
                if(count==1) {
                    y=1;
                } else {
                    y=2;
                }
                for (int i = 0; i < y; i++) {
                    JSONObject mainstroka = users.getJSONObject(i);
                    String stroka;
                    if(i==0) {
                        if(count==1) {
                            stroka = "<b><font color='#1b1b1b'>" + mainstroka.getString("fio") + "</font></b>";
                        } else {
                            stroka = "<b><font color='#1b1b1b'>" + mainstroka.getString("fio") + ",</font></b>";
                        }
                    } else {
                        stroka = "<b><font color='#1b1b1b'>" + mainstroka.getString("fio") + "</font></b>";
                    }
                    stroke.append(" ").append(stroka);
                }

                if(count>2) {
                    int vsd = count-2;
                    String str1 = activity.getResources().getString(R.string.still) + " " + vsd;
                    stroke.append(" ").append(str1);
                }
                str.setText(HtmlCompat.fromHtml(stroke.toString(), HtmlCompat.FROM_HTML_MODE_COMPACT));
                lin.addView(str);

                LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout fourpersons = new LinearLayout(mCtx);
                lp4.setMargins(30,20,30,30);
                fourpersons.setLayoutParams(lp4);
                fourpersons.setOrientation(LinearLayout.HORIZONTAL);
                int y1;
                if(count<5) {
                    y1=count;
                } else {
                    y1=4;
                }
                List<Avatar> data = new ArrayList<>();
                for (int i = 0; i < y1; i++) {
                    JSONObject mainstroka1 = users.getJSONObject(i);
                    Avatar f = new Avatar();
                    f.fio = mainstroka1.getString("fio");
                    f.img = mainstroka1.getString("avatar");
                    f.id_user = mainstroka1.getString("id_user");
                    data.add(f);
                }
                View v1 = LayoutInflater.from(mCtx).inflate(R.layout.avatarview_item, fourpersons, true);
                AvatarView avatar1 = v1.findViewById(R.id.avatar1);
                AvatarView avatar2 = v1.findViewById(R.id.avatar2);
                AvatarView avatar3 = v1.findViewById(R.id.avatar3);
                AvatarView avatar4 = v1.findViewById(R.id.avatar4);
                LinearLayout lin1 = v1.findViewById(R.id.lin1);
                TextView plus = v1.findViewById(R.id.plus);
                IImageLoader imageLoader = new GlideLoader();
                if(data.size()==1) {
                    imageLoader.loadImage(avatar1, data.get(0).img, data.get(0).fio);
                    avatar1.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", data.get(0).id_user);
                        Navigation.findNavController(v).navigate(R.id.viewuser, bundle);
                    });
                    avatar2.setVisibility(View.GONE);
                    avatar3.setVisibility(View.GONE);
                    avatar4.setVisibility(View.GONE);
                    lin1.setVisibility(View.GONE);
                }
                if(data.size()==2) {
                    imageLoader.loadImage(avatar1, data.get(0).img, data.get(0).fio);
                    avatar1.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", data.get(0).id_user);
                        Navigation.findNavController(v).navigate(R.id.viewuser, bundle);
                    });
                    imageLoader.loadImage(avatar2, data.get(1).img, data.get(1).fio);
                    avatar2.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", data.get(1).id_user);
                        Navigation.findNavController(v).navigate(R.id.viewuser, bundle);
                    });
                    avatar3.setVisibility(View.GONE);
                    avatar4.setVisibility(View.GONE);
                    lin1.setVisibility(View.GONE);
                }
                if(data.size()==3) {
                    imageLoader.loadImage(avatar1, data.get(0).img, data.get(0).fio);
                    avatar1.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", data.get(0).id_user);
                        Navigation.findNavController(v).navigate(R.id.viewuser, bundle);
                    });
                    imageLoader.loadImage(avatar2, data.get(1).img, data.get(1).fio);
                    avatar2.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", data.get(1).id_user);
                        Navigation.findNavController(v).navigate(R.id.viewuser, bundle);
                    });
                    imageLoader.loadImage(avatar3, data.get(2).img, data.get(2).fio);
                    avatar3.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", data.get(2).id_user);
                        Navigation.findNavController(v).navigate(R.id.viewuser, bundle);
                    });
                    avatar4.setVisibility(View.GONE);
                    lin1.setVisibility(View.GONE);
                }
                if(data.size()==4) {
                    imageLoader.loadImage(avatar1, data.get(0).img, data.get(0).fio);
                    avatar1.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", data.get(0).id_user);
                        Navigation.findNavController(v).navigate(R.id.viewuser, bundle);
                    });
                    imageLoader.loadImage(avatar2, data.get(1).img, data.get(1).fio);
                    avatar2.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", data.get(1).id_user);
                        Navigation.findNavController(v).navigate(R.id.viewuser, bundle);
                    });
                    imageLoader.loadImage(avatar3, data.get(2).img, data.get(2).fio);
                    avatar3.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", data.get(2).id_user);
                        Navigation.findNavController(v).navigate(R.id.viewuser, bundle);
                    });
                    imageLoader.loadImage(avatar4, data.get(3).img, data.get(3).fio);
                    avatar4.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", data.get(3).id_user);
                        Navigation.findNavController(v).navigate(R.id.viewuser, bundle);
                    });
                    lin1.setVisibility(View.GONE);
                }
                if(count>4) {
                    lin1.setVisibility(View.VISIBLE);
                    lin1.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.viewusers));
                    int gr = users.length() - 4;
                    String txt = gr + "+";
                    plus.setText(txt);
                }
                lin.addView(fourpersons);
                cd.addView(lin);
                persons.addView(cd);

            }
        } catch (Exception ignored) {
        }
    }
}
