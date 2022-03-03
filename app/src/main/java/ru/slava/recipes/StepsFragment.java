package ru.slava.recipes;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StepsFragment extends Fragment {
    List<Steps> data = new ArrayList<>();
    String res;
    String idd;
    View rootView;
    JSONArray main;
    RecyclerView mRecyclerView;
    StepsAdapter mAdapter;
    GridLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_steps, container, false);
       Bundle bundle = getArguments();
        if (bundle != null) {
            idd = bundle.getString("idd");
            res = bundle.getString("res");
        }
        mRecyclerView = rootView.findViewById(R.id.steps_view);
        data.clear();
        JSONObject dataJsonObj;

        try {

            dataJsonObj = new JSONObject(res);

            String url = dataJsonObj.get("img").toString();
            if(!url.equals("")) {
                ImageView photorecipe = rootView.findViewById(R.id.photorecipe);
                Glide.with(requireActivity()).load(url).placeholder(R.drawable.nophoto).fitCenter().centerCrop().error(R.drawable.nophoto).into(photorecipe);
            }


            main = dataJsonObj.getJSONArray("steps");

            for (int i = 0; i < main.length(); i++) {
                Steps fishData = new Steps();
                JSONObject mainstroka = main.getJSONObject(i);
                fishData.orders = mainstroka.getString("orders");
                fishData.description = mainstroka.getString("description");
                JSONArray main1 = mainstroka.getJSONArray("photos");
                fishData.photos = new ArrayList<>();
                if(main1.length()>0) {
                    for (int i1 = 0; i1 < main1.length(); i1++) {
                        JSONObject mainstroka1 = main1.getJSONObject(i1);
                        fishData.photos.add(mainstroka1.getString("img"));
                    }
                }
                data.add(fishData);
            }
            Activity activity = getActivity();
            if (isAdded() && activity != null) {
                mAdapter = new StepsAdapter(data, requireActivity());

                mRecyclerView.setAdapter(mAdapter);
                mLayoutManager = new GridLayoutManager(requireActivity(), 1, GridLayoutManager.VERTICAL, false);
                mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, 20, true));
                mRecyclerView.setLayoutManager(mLayoutManager);
            }
        } catch (JSONException ignored) {

        }
        return rootView;
    }
}