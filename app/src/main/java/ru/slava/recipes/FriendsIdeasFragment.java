package ru.slava.recipes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.rtoshiro.secure.SecureSharedPreferences;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

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

public class FriendsIdeasFragment extends Fragment {
    FrameLayout nointernet, rell, nofriends;
    SecureSharedPreferences mSettings;
    String user_id, res;
    String APP_PREFERENCES_PHONE = "phone";
    SwipeRefreshLayout mSwipeRefreshLayout;
    JSONArray main;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friendsideas, container, false);
        rell = rootView.findViewById(R.id.rell);
        rell.setVisibility(View.VISIBLE);
        nointernet = rootView.findViewById(R.id.nointernet);
        nofriends = rootView.findViewById(R.id.nofriends);
        dozapros();
        mSettings = new SecureSharedPreferences(requireActivity());
        if (mSettings.contains(APP_PREFERENCES_PHONE)) {
            user_id = mSettings.getString(APP_PREFERENCES_PHONE, "");
        } else {
            user_id = "0";
        }

        mSwipeRefreshLayout = rootView.findViewById(R.id.activity_main_swipe_refresh_layout1);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.ed3851);


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

    public void dozapros() {
        String url;
        url = "https://www.книгавкусныхидей.рф/recipes/getfriends.php?user_id=" + user_id;

        OkHttpClient client = doclient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                requireActivity().runOnUiThread(() -> fail());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    assert responseBody != null;
                    res = responseBody.string();
                    requireActivity().runOnUiThread(() -> dosome(res));
                }
            }
        });

    }

    public void fail() {
        mSwipeRefreshLayout.setVisibility(View.GONE);
        rell.setVisibility(View.GONE);
        nointernet.setVisibility(View.VISIBLE);
        nofriends.setVisibility(View.GONE);
    }

    public void dosome(String res) {
        rell.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        nointernet.setVisibility(View.GONE);
        nofriends.setVisibility(View.GONE);
        JSONObject dataJsonObj;

        try {

            dataJsonObj = new JSONObject(res);
            main = dataJsonObj.getJSONArray("subscribes");
            if(main.length()==0) {
                nofriends.setVisibility(View.VISIBLE);
            } else {
                for (int i = 0; i < main.length(); i++) {

                }
            }
        } catch (Exception ignored) {

        }

    }
}
