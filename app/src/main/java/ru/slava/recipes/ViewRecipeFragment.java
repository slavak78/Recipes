package ru.slava.recipes;

import android.app.Activity;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.github.rtoshiro.secure.SecureSharedPreferences;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ViewRecipeFragment extends Fragment {
    FrameLayout nointernet, rell;
    String idd;
    TabLayout tablayout;
    PagerAdapter pagerAdapter;
    ViewPager2 viewPager;
    ArrayList<String> st1 = new ArrayList<>();
    Button update;
    String res, user_id, translit, title, photo;
    String APP_PREFERENCES_PHONE = "phone";
    SecureSharedPreferences mSettings;
    private View rootView = null;
    Menu men;
    int fav;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        men = menu;
        inflater.inflate(R.menu.viewrecipe_menu, menu);
        if(photo.equals("")) {
            menu.getItem(1).setVisible(false);
        }

        menu.getItem(0).setOnMenuItemClickListener(item -> {
            if (fav==1) {
                menu.getItem(0).setIcon(R.drawable.like_white);
                fav=0;
                SecureSharedPreferences.Editor editor = mSettings.edit();
                editor.putString("fav", "gray");
                editor.apply();
            } else {
                menu.getItem(0).setIcon(R.drawable.like_red);
                fav=1;
                SecureSharedPreferences.Editor editor = mSettings.edit();
                editor.putString("fav", "red");
                editor.apply();
            }
            dofav();
            return true;
        });
        menu.getItem(1).setOnMenuItemClickListener(item -> {
            GlavActivity gla = (GlavActivity) getActivity();
            assert gla != null;
            Bundle bundle = new Bundle();
            bundle.putString("idd", idd);
            bundle.putString("translit", translit);
            gla.navController.navigate(R.id.share, bundle);
            return true;
        });
        super.onCreateOptionsMenu(menu, inflater);
    }




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if(rootView==null) {
            rootView = inflater.inflate(R.layout.fragment_view_recipe, container, false);
            Bundle bundle = getArguments();
            if (bundle != null) {
                idd = bundle.getString("idd");
                title = bundle.getString("title");
                photo = bundle.getString("photo");
            }
            rell = rootView.findViewById(R.id.rell);
            rell.setVisibility(View.VISIBLE);
            nointernet = rootView.findViewById(R.id.nointernet);
            tablayout = rootView.findViewById(R.id.tab_layout);
            viewPager = rootView.findViewById(R.id.viewpager);
            mSettings = new SecureSharedPreferences(requireActivity());
            user_id = mSettings.getString(APP_PREFERENCES_PHONE, "");

            update = rootView.findViewById(R.id.update);
            update.setOnClickListener(v -> {
                rell.setVisibility(View.VISIBLE);
                tablayout.setVisibility(View.GONE);
                viewPager.setVisibility(View.GONE);
                nointernet.setVisibility(View.GONE);
                dozapros();
            });

            dozapros();
        }
        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(title);
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

    public void dozapros() {
        String url;
        url = "https://www.книгавкусныхидей.рф/recipes/recipe.php?id=" + idd + "&user_id=" + user_id;

        OkHttpClient client = doclient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Activity activity = getActivity();
                if (isAdded() && activity != null) {
                    requireActivity().runOnUiThread(() -> fail());
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    assert responseBody != null;
                    res = responseBody.string();
                    Activity activity = getActivity();
                    if (isAdded() && activity != null) {
                        requireActivity().runOnUiThread(() -> dosome(res));
                    }
                }
            }
        });
    }

    public void fail() {
        rell.setVisibility(View.GONE);
        tablayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        nointernet.setVisibility(View.VISIBLE);
    }

    public void dosome(String res) {
        rell.setVisibility(View.GONE);
        tablayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        nointernet.setVisibility(View.GONE);


        st1.add(getResources().getString(R.string.description));
        st1.add(getResources().getString(R.string.ing));
        st1.add(getResources().getString(R.string.cook));
        st1.add(getResources().getString(R.string.comments));
        st1.add(getResources().getString(R.string.aboutauth));
//        viewPager.setUserInputEnabled(false);
        viewPager.setSaveEnabled(false);
        pagerAdapter = new PagerAdapter(requireActivity().getSupportFragmentManager(), getLifecycle());

        viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(tablayout, viewPager,
                (tab, position) -> tab.setText(st1.get(position))).attach();

        //     viewPager.setCurrentItem(0);




        JSONObject dataJsonObj;
        try {
            dataJsonObj = new JSONObject(res);
            translit = dataJsonObj.getString("title_translit");
            fav = dataJsonObj.getInt("fav");
            if (fav == 1) {
                men.getItem(0).setIcon(R.drawable.like_red);
            } else {
                men.getItem(0).setIcon(R.drawable.like_white);
            }
        } catch (Exception ignored) {
        }
    }


        class PagerAdapter extends FragmentStateAdapter {


        public PagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }


        @Override
        public int getItemCount() {
            return st1.size();
        }

        @NotNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment = null;
            if(position==0) {
                fragment = new DescFragment();
                Bundle bundle = new Bundle();
                bundle.putString("idd", idd);
                bundle.putString("res", res);
                fragment.setArguments(bundle);
            }
            if(position==1) {
                fragment = new IngredientsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("idd", idd);
                bundle.putString("res", res);
                fragment.setArguments(bundle);
            }
            if(position==2) {
                fragment = new StepsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("idd", idd);
                bundle.putString("res", res);
                fragment.setArguments(bundle);
            }
            if(position==3) {
                fragment = new CommentsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("idd", idd);
                bundle.putString("res", res);
                fragment.setArguments(bundle);
            }
            if(position==4) {
                fragment = new AboutAuthorFragment();
                Bundle bundle = new Bundle();
                bundle.putString("idd", idd);
                bundle.putString("res", res);
                fragment.setArguments(bundle);
            }

            return Objects.requireNonNull(fragment);
        }
    }

    public void dofav() {
        OkHttpClient client = doclient();
        Request request = new Request.Builder()
                .url("https://www.книгавкусныхидей.рф/recipes/change.php?recipe_id=" + idd + "&user_id=" + user_id + "&d=" + fav)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                requireActivity().runOnUiThread(() -> fail1());
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

    public void fail1() {
        if (fav==1) {
            men.getItem(0).setIcon(R.drawable.like_white);
            fav = 0;
            SecureSharedPreferences.Editor editor = mSettings.edit();
            editor.putString("fav", "gray");
            editor.apply();
        } else {
            men.getItem(0).setIcon(R.drawable.like_red);
            fav = 1;
            SecureSharedPreferences.Editor editor = mSettings.edit();
            editor.putString("fav", "red");
            editor.apply();
        }
    }
}
