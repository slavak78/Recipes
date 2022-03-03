package ru.slava.recipes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.rtoshiro.secure.SecureSharedPreferences;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
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


public class AboutAuthorFragment extends Fragment {
    List<Recipe> data = new ArrayList<>();
    String res;
    String idd;
    JSONArray main;
    RecipeAuthorAdapter mAdapter;
    GridLayoutManager mLayoutManager;
    String user_id;
    SecureSharedPreferences mSettings;
    String APP_PREFERENCES_PHONE = "phone";
    private View rootView = null;
    TextView subscribes,signed;
    Button subscribe;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView == null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                idd = bundle.getString("idd");
                res = bundle.getString("res");
            }
            mSettings = new SecureSharedPreferences(requireActivity());
            if (mSettings.contains(APP_PREFERENCES_PHONE)) {
                user_id = mSettings.getString(APP_PREFERENCES_PHONE, "");
            } else {
                user_id = "0";
            }


            data.clear();
            JSONObject dataJsonObj;
            try {

                dataJsonObj = new JSONObject(res);
                main = dataJsonObj.getJSONArray("author_recipes");
                if (main.length() == 0) {
                    rootView = inflater.inflate(R.layout.fragment_aboutauthor_without, container, false);
                } else {
                    rootView = inflater.inflate(R.layout.fragment_aboutauthor, container, false);
                    RecyclerView mRecyclerView = rootView.findViewById(R.id.recipes_view);
                    for (int i = 0; i < main.length(); i++) {
                        Recipe fishData = new Recipe();
                        JSONObject mainstroka = main.getJSONObject(i);
                        fishData.idd = mainstroka.getString("id");
                        fishData.photo = mainstroka.getString("photo");
                        fishData.title = mainstroka.getString("title");
                        fishData.title_translit = mainstroka.getString("title_translit");
                        fishData.fav = mainstroka.getInt("fav");
                        data.add(fishData);
                    }
                    mAdapter = new RecipeAuthorAdapter(data, requireActivity(), user_id, requireActivity());
                    mRecyclerView.setAdapter(mAdapter);
                    mLayoutManager = new GridLayoutManager(requireActivity(), 1, GridLayoutManager.VERTICAL, false);
                    mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, 20, true));
                    mRecyclerView.setLayoutManager(mLayoutManager);
                }

                TextView fio = rootView.findViewById(R.id.fio);
                fio.setText(dataJsonObj.getString("fio"));
                String author_id = dataJsonObj.getString("user_id");
                AvatarView avatar = rootView.findViewById(R.id.avatar);
                String urlavatar = dataJsonObj.getString("avatar");
                IImageLoader imageLoader = new GlideLoader();
                if (urlavatar.equals("")) {
                    urlavatar = "123";
                }
                imageLoader.loadImage(avatar, urlavatar, dataJsonObj.getString("fio"));

                TimeZone tz = TimeZone.getDefault();
                Date now = new Date();
                int offset = tz.getOffset(now.getTime());
                long timetoreal = dataJsonObj.getLong("ago") + offset;
                RelativeTimeTextView ago = rootView.findViewById(R.id.ago);
                ago.setReferenceTime(timetoreal);

                subscribes = rootView.findViewById(R.id.subscribes);
                subscribes.setText(dataJsonObj.getString("subscribes"));
                signed = rootView.findViewById(R.id.signed);
                signed.setText(dataJsonObj.getString("signed"));
                subscribe = rootView.findViewById(R.id.subscribe);
                subscribe.setTag("unsubscribe");
                if(dataJsonObj.getInt("subs")==1) {
                    subscribe.setText(requireActivity().getString(R.string.unsubscribe));
                    subscribe.setTag("subscribe");
                } else {
                    subscribe.setText(requireActivity().getString(R.string.subscribe));
                    subscribe.setTag("unsubscribe");
                }
                subscribe.setOnClickListener(v -> {
                    if(subscribe.getTag().toString().equals("unsubscribe")) {
                        dozaprossub(subscribe, subscribes, 1, author_id);
                        subscribe.setText(requireActivity().getString(R.string.unsubscribe));
                        subscribe.setTag("subscribe");
                        int y = Integer.parseInt(subscribes.getText().toString());
                        y++;
                        subscribes.setText(String.valueOf(y));
                    } else {
                        dozaprossub(subscribe, subscribes, 0, author_id);
                        subscribe.setText(requireActivity().getString(R.string.subscribe));
                        subscribe.setTag("unsubscribe");
                        int y = Integer.parseInt(subscribes.getText().toString());
                        y--;
                        subscribes.setText(String.valueOf(y));
                    }
                });
                String token = dataJsonObj.getString("token");
                Button chat = rootView.findViewById(R.id.chat);
                chat.setOnClickListener(v -> {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("idd", author_id);
                    bundle1.putString("token", token);
                    Navigation.findNavController(v).navigate(R.id.chat, bundle1);
                });

            } catch (JSONException ignored) {

            }
        } else {
            if(mSettings.contains("pos")) {
                int pos = mSettings.getInt("pos", 0);
                String idd1 = data.get(pos).idd;
                String photo1 = data.get(pos).photo;
                String title1 = data.get(pos).title;
                String title_translit1 = data.get(pos).title_translit;
                String fav1 = mSettings.getString("fav", "gray");
                Recipe fishData = new Recipe();
                fishData.idd = idd1;
                fishData.photo = photo1;
                fishData.title = title1;
                fishData.title_translit = title_translit1;
                if(fav1.equals("gray")) {
                    fishData.fav = 0;
                } else {
                    fishData.fav = 1;
                }
                data.set(pos,fishData);
                mAdapter.notifyItemChanged(pos);
            }
    }

        return rootView;
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

    public void dozaprossub(Button subscribe, TextView subscribes, int subscribe1, String id_user) {
        OkHttpClient client = doclient();
        Request request = new Request.Builder()
                .url("https://www.книгавкусныхидей.рф/recipes/changesub.php?user_id=" + user_id + "&author_id=" + id_user + "&subscribe=" + subscribe1)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                requireActivity().runOnUiThread(() -> fail1(subscribe, subscribes, subscribe1));
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

    public void fail1(Button subscribe, TextView subscribes, int subscribe1) {
        if (subscribe1 == 1) {
            subscribe.setTag("unsubscribe");
            subscribe.setText(requireActivity().getString(R.string.unsubscribe));
            int y = Integer.parseInt(subscribes.getText().toString());
            y--;
            subscribes.setText(String.valueOf(y));
        } else {
            subscribe.setTag("subscribe");
            subscribe.setText(requireActivity().getString(R.string.subscribe));
            int y = Integer.parseInt(subscribes.getText().toString());
            y++;
            subscribes.setText(String.valueOf(y));
        }
    }
}