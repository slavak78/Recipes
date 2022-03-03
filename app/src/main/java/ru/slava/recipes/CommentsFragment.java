package ru.slava.recipes;

import android.app.Activity;
import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.rtoshiro.secure.SecureSharedPreferences;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import ru.slava.recipes.AvatarView.GlideLoader;
import ru.slava.recipes.AvatarView.IImageLoader;
import ru.slava.recipes.AvatarView.views.AvatarView;

public class CommentsFragment extends Fragment {
    List<Comments> data = new ArrayList<>();
    String res;
    String idd;
    View rootView;
    JSONArray main;
    RecyclerView mRecyclerView;
    CommentsAdapter mAdapter;
    GridLayoutManager mLayoutManager;
    String user_id;
    SecureSharedPreferences mSettings;
    String APP_PREFERENCES_PHONE = "phone";
    CoordinatorLayout scr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_comments, container, false);
       Bundle bundle = getArguments();
        if (bundle != null) {
            idd = bundle.getString("idd");
            res = bundle.getString("res");
        }

        RelativeLayout ele = rootView.findViewById(R.id.ele);
        ele.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
               Rect rect = view.getBackground().copyBounds();
               rect.offset(0, 5);
               outline.setRect(rect);
            }
        });

        scr = rootView.findViewById(R.id.scr);
        mSettings = new SecureSharedPreferences(requireActivity());
            user_id = mSettings.getString(APP_PREFERENCES_PHONE, "");

        mRecyclerView = rootView.findViewById(R.id.comments_view);
        data.clear();


        JSONObject dataJsonObj;

        try {

            dataJsonObj = new JSONObject(res);

            String url = dataJsonObj.getString("img");
            if(!url.equals("")) {
                ImageView photorecipe = rootView.findViewById(R.id.photorecipe);
                Glide.with(requireActivity()).load(url).placeholder(R.drawable.nophoto).fitCenter().centerCrop().error(R.drawable.nophoto).into(photorecipe);
            }

            TextView countcomments = rootView.findViewById(R.id.countcomments);
            countcomments.setText(dataJsonObj.getString("countcomments"));
            TextView rate = rootView.findViewById(R.id.rate);
            rate.setText(dataJsonObj.getString("stars"));

            String img = dataJsonObj.getString("avatar");
            String fio = dataJsonObj.getString("fio");
            AvatarView avatar = rootView.findViewById(R.id.avatar);
            IImageLoader imageLoader = new GlideLoader();
            if (img.equals("")) {
                img = "123";
            }
            imageLoader.loadImage(avatar, img, fio);
            avatar.setOnClickListener(v -> {
                Bundle bundle1 = new Bundle();
                bundle1.putString("user_id", user_id);
                Navigation.findNavController(v).navigate(R.id.viewuser, bundle1);
            });
            main = dataJsonObj.getJSONArray("comments");

            for (int i = 0; i < main.length(); i++) {
                Comments fishData = new Comments();
                JSONObject mainstroka = main.getJSONObject(i);
                fishData.comment = mainstroka.getString("comment");
                fishData.avatar = mainstroka.getString("avatar");
                fishData.name = mainstroka.getString("name");
                fishData.idd = mainstroka.getString("id");
                fishData.stars = mainstroka.getString("vs");
                data.add(fishData);
            }
            mAdapter = new CommentsAdapter(data, user_id,  requireActivity(), getActivity(), scr);

            mRecyclerView.setAdapter(mAdapter);
            Activity activity = getActivity();
            if (isAdded() && activity != null) {
                mLayoutManager = new GridLayoutManager(requireActivity(), 1, GridLayoutManager.VERTICAL, false);
                mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, 20, true));
                mRecyclerView.setLayoutManager(mLayoutManager);
            }
        } catch (JSONException ignored) {

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

    public void send(EditText types, List<Comments> data, TextView countcomments) {
        OkHttpClient client = doclient();
        RequestBody formBody = new FormBody.Builder()
                .add("txt", types.getText().toString())
                .add("user_id", user_id)
                .add("recipe_id", idd)
                .build();
        Request request = new Request.Builder()
                .url("https://www.книгавкусныхидей.рф/recipes/addcomment.php")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                requireActivity().runOnUiThread(() -> fail1(e.getClass().getName()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    assert responseBody != null;
                    res = responseBody.string();
                    requireActivity().runOnUiThread(() -> dosome1(types,data,res,countcomments));
                }
            }
        });

    }

    public void fail1(String h) {
        String error;
        if(h.equals("java.net.SocketTimeoutException")) {
            error = getResources().getString(R.string.noserver);
        } else {
            error = getResources().getString(R.string.nointernet);
        }
        final AlertDialog aboutDialog = new AlertDialog.Builder(
                requireActivity()).setTitle(getResources().getString(R.string.error)).setMessage(error)
                .setPositiveButton("OK", (dialog, which) -> {
                }).create();
        aboutDialog.show();
    }


    public String getCommentsAddition(int num) {
        int preLastDigit= num % 100 / 10;
        if (preLastDigit == 1) {
            return num + " комментариев";
        }
        switch (num % 10) {
            case 1:
            return num + " комментарий";
            case 2:
            case 3:
            case 4:
            return num + " комментария";
            default:
                return num + " комментариев";
        }
    }


    public void dosome1(EditText types, List<Comments> data, String res, TextView countcomments) {
        JSONObject dataJsonObj;
        try {
            dataJsonObj = new JSONObject(res);
            Comments fishData = new Comments();
            fishData.comment = types.getText().toString();
            fishData.name = dataJsonObj.get("name").toString();
            fishData.avatar = dataJsonObj.get("avatar").toString();
            data.add(fishData);
            mAdapter.addNewItems(data, user_id,  requireActivity(), getActivity(), scr);
            types.setText("");
            KeyboardUtils.getInstance().hide(requireActivity());
            types.setEnabled(false);
            types.setEnabled(true);
            mRecyclerView.smoothScrollToPosition(Objects.requireNonNull(mRecyclerView.getAdapter()).getItemCount() - 1);
            String count = countcomments.getText().toString();
            String numberOnly = count.replaceAll("[^0-9]", "");
            int myNum = Integer.parseInt(numberOnly);
            myNum = myNum + 1;
            countcomments.setText(getCommentsAddition(myNum));
        } catch (Exception ignored) {

        }
    }
}