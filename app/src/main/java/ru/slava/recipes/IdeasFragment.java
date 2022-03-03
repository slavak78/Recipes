package ru.slava.recipes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.rtoshiro.secure.SecureSharedPreferences;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
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

public class IdeasFragment extends Fragment {
    List<Recipe> data = new ArrayList<>();
    RecyclerView mRecyclerView;
    RecipeAdapter mAdapter;
    GridLayoutManager mLayoutManager;
    FrameLayout nointernet, rell;
    SwipeRefreshLayout mSwipeRefreshLayout;
    JSONArray main;
    String res;
    SecureSharedPreferences mSettings;
    String APP_PREFERENCES_PHONE = "phone";
    Button update;
    String user_id;
    int nachalo, first;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private View rootView = null;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_ideas, container, false);
            rell = rootView.findViewById(R.id.rell);
            rell.setVisibility(View.VISIBLE);
            nointernet = rootView.findViewById(R.id.nointernet);
            update = rootView.findViewById(R.id.update);
            update.setOnClickListener(v -> {
                data.clear();
                nachalo = 0;
                first = 0;
                mSwipeRefreshLayout.setVisibility(View.GONE);
                rell.setVisibility(View.VISIBLE);
                nointernet.setVisibility(View.GONE);
                dozapros();
            });
            mSwipeRefreshLayout = rootView.findViewById(R.id.activity_main_swipe_refresh_layout1);
            mSwipeRefreshLayout.setVisibility(View.GONE);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.ed3851);
            mRecyclerView = rootView.findViewById(R.id.recipes_view);

            data.clear();
            nachalo = 0;
            first = 0;
            mSettings = new SecureSharedPreferences(requireActivity());
            if (mSettings.contains(APP_PREFERENCES_PHONE)) {
                user_id = mSettings.getString(APP_PREFERENCES_PHONE, "");
            } else {
                user_id = "0";
            }


            mSwipeRefreshLayout.setOnRefreshListener(() -> {
                data.clear();
                nachalo = 0;
                first = 0;
                mSwipeRefreshLayout.setRefreshing(false);
                dozapros();
            });
            dozapros();
        } else {
            if(mSettings.contains("pos")) {
                int pos = mSettings.getInt("pos", 0);
                String idd1 = data.get(pos).idd;
                String avatar1 = data.get(pos).avatar;
                String fio1 = data.get(pos).fio;
                String photo1 = data.get(pos).photo;
                String title1 = data.get(pos).title;
                String title_translit1 = data.get(pos).title_translit;
                String idd_user1 = data.get(pos).idd_user;
                int subscribe1 = data.get(pos).subscribe;
                long ago1 = data.get(pos).ago;
                String fav1 = mSettings.getString("fav", "gray");
                Recipe fishData = new Recipe();
                fishData.idd = idd1;
                fishData.avatar = avatar1;
                fishData.fio = fio1;
                fishData.photo = photo1;
                fishData.title = title1;
                fishData.title_translit = title_translit1;
                fishData.idd_user = idd_user1;
                fishData.subscribe = subscribe1;
                fishData.ago = ago1;
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





    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        menu.getItem(0).setOnMenuItemClickListener(item -> {
            GlavActivity gla = (GlavActivity) getActivity();
            assert gla != null;
            gla.navController.navigate(R.id.search);
            return true;
        });
        super.onCreateOptionsMenu(menu, inflater);
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
        url = "https://www.книгавкусныхидей.рф/recipes/recipes.php?user_id=" + user_id + "&limit=" + nachalo;
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
    }

    public void dosome(String res) {
        rell.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        nointernet.setVisibility(View.GONE);
        JSONObject dataJsonObj;

        try {

            dataJsonObj = new JSONObject(res);
            main = dataJsonObj.getJSONArray("recipes");

            for (int i = 0; i < main.length(); i++) {
                Recipe fishData = new Recipe();
                JSONObject mainstroka = main.getJSONObject(i);
                fishData.idd = mainstroka.getString("id");
                fishData.avatar = mainstroka.getString("avatar");
                fishData.fio = mainstroka.getString("fio");
                fishData.photo = mainstroka.getString("photo");
                fishData.title = mainstroka.getString("title");
                fishData.title_translit = mainstroka.getString("title_translit");
                fishData.fav = mainstroka.getInt("fav");
                fishData.idd_user = mainstroka.getString("id_user");
                fishData.subscribe = mainstroka.getInt("subscribe");
                fishData.ago = mainstroka.getLong("ago");
                fishData.view_rate = mainstroka.getInt("view_rate");
                fishData.stars = mainstroka.getString("vs");
                data.add(fishData);
                if(i==0) {
                        Recipe fishData1 = new Recipe();
                        data.add(fishData1);
                }
            }
            if(nachalo == 0) {
                mAdapter = new RecipeAdapter(data, requireActivity(), user_id, requireActivity());
                mRecyclerView.setAdapter(mAdapter);
                mLayoutManager = new GridLayoutManager(requireActivity(), 1, GridLayoutManager.VERTICAL, false);
                mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, 15, true));
                mRecyclerView.setLayoutManager(mLayoutManager);
            } else {
                mAdapter.addNewItems(data, requireActivity(), user_id, requireActivity());
            }

            loading = true;

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) //check for scroll down
                    {
                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        GridLayoutManager lay = (GridLayoutManager) mRecyclerView.getLayoutManager();
                        assert lay != null;
                        pastVisiblesItems = lay.findFirstVisibleItemPosition();

                        if (loading) {
                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                loading = false;
                                nachalo = nachalo + 30;
                                dozapros();
                            }
                        }
                    }
                }
            });

        } catch (JSONException ignored) {

        }

    }
}
