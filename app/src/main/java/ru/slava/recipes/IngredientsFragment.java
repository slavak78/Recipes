package ru.slava.recipes;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class IngredientsFragment extends Fragment {
    List<Ingredients> data = new ArrayList<>();
    String res;
    String idd;
    View rootView;
    JSONArray main;
    RecyclerView mRecyclerView;
    IngredientsAdapter mAdapter;
    GridLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);
       Bundle bundle = getArguments();
        if (bundle != null) {
            idd = bundle.getString("idd");
            res = bundle.getString("res");
        }
        mRecyclerView = rootView.findViewById(R.id.ing_view);
        data.clear();
        JSONObject dataJsonObj;
        try {

            dataJsonObj = new JSONObject(res);

            String url = dataJsonObj.get("img").toString();
            if(!url.equals("")) {
                ImageView photorecipe = rootView.findViewById(R.id.photorecipe);
                Glide.with(requireActivity()).load(url).placeholder(R.drawable.nophoto).fitCenter().centerCrop().error(R.drawable.nophoto).into(photorecipe);
            }


            TextView portion = rootView.findViewById(R.id.portion);
            portion.setText(dataJsonObj.get("portion").toString());

            main = dataJsonObj.getJSONArray("ingredients");

            for (int i = 0; i < main.length(); i++) {
                Ingredients fishData = new Ingredients();
                JSONObject mainstroka = main.getJSONObject(i);
                fishData.ing = mainstroka.getString("ing");
                fishData.quantity = mainstroka.getString("quantity");
                fishData.img = mainstroka.getString("img");
                fishData.unit = mainstroka.getString("unit");
                data.add(fishData);
            }
            mAdapter = new IngredientsAdapter(data, getActivity());

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
}