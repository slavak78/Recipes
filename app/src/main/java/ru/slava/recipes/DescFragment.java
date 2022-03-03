package ru.slava.recipes;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rtoshiro.secure.SecureSharedPreferences;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.ProductTypes;

import ru.slava.recipes.Ads.NativeTemplateStyle;
import ru.slava.recipes.Ads.TemplateView;
import ru.slava.recipes.AvatarView.GlideLoader;
import ru.slava.recipes.AvatarView.IImageLoader;
import ru.slava.recipes.AvatarView.views.AvatarView;

public class DescFragment extends Fragment {
    String res;
    Button buy;
    String idd, user_id, adv_hash;
    View rootView;
    String APP_PREFERENCES_ADV = "adv";
    String APP_PREFERENCES_PHONE = "phone";
    SecureSharedPreferences mSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_desc, container, false);
       Bundle bundle = getArguments();
        if (bundle != null) {
            idd = bundle.getString("idd");
            res = bundle.getString("res");
        }

        mSettings = new SecureSharedPreferences(requireActivity());
        user_id = mSettings.getString(APP_PREFERENCES_PHONE, "");


        buy = rootView.findViewById(R.id.buy);
        buy.setOnClickListener(v -> {
            GlavActivity gla = (GlavActivity) getActivity();
            assert gla != null;
            gla.mCheckout.whenReady(new Checkout.EmptyListener() {
                @Override
                public void onReady(@NonNull BillingRequests requests) {
                    requests.purchase(ProductTypes.SUBSCRIPTION, "sub_02", null, gla.mCheckout.getPurchaseFlow());
                }
            });

        });


        JSONObject dataJsonObj;

        try {

            dataJsonObj = new JSONObject(res);
            adv_hash = dataJsonObj.getString("adv_hash");
            if(!mSettings.contains(APP_PREFERENCES_ADV)) {
                doadvertise();
            } else {
                if(!mSettings.getString(APP_PREFERENCES_ADV, "").equals(adv_hash)) {
                    doadvertise();
                }
            }
            TextView title = rootView.findViewById(R.id.title);
            title.setText(dataJsonObj.getString("title"));

            TextView times = rootView.findViewById(R.id.times);
            if(dataJsonObj.get("times").toString().equals("")) {
                ImageView sec = rootView.findViewById(R.id.sec);
                sec.setVisibility(View.GONE);
            } else {
                times.setText(dataJsonObj.getString("times"));
            }

            String url = dataJsonObj.getString("img");
            if(!url.equals("")) {
                ImageView photorecipe = rootView.findViewById(R.id.photorecipe);
                Glide.with(requireActivity()).load(url).placeholder(R.drawable.nophoto).fitCenter().centerCrop().error(R.drawable.nophoto).into(photorecipe);
            }

            TextView description = rootView.findViewById(R.id.description);
            description.setText(dataJsonObj.getString("description"));


            TextView fio = rootView.findViewById(R.id.fio);
            fio.setText(dataJsonObj.getString("fio"));

            AvatarView avatar = rootView.findViewById(R.id.avatar);
            String urlavatar = dataJsonObj.getString("avatar");
            IImageLoader imageLoader = new GlideLoader();
            if (urlavatar.equals("")) {
                urlavatar = "123";
            }
            imageLoader.loadImage(avatar, urlavatar, dataJsonObj.getString("fio"));

            String user_id = dataJsonObj.getString("user_id");

            LinearLayout cli = rootView.findViewById(R.id.cli);

            cli.setOnClickListener(v -> {
                Bundle bundle1 = new Bundle();
                bundle1.putString("idd",user_id);
                Navigation.findNavController(v).navigate(R.id.viewuser, bundle1);
            });

            RatingBar stars = rootView.findViewById(R.id.stars);
            stars.setRating(Float.parseFloat(dataJsonObj.get("stars").toString()));

            TextView points = rootView.findViewById(R.id.points);
            points.setText(dataJsonObj.getString("stars"));



        } catch (JSONException ignored) {

        }





        return rootView;
    }

    public void doadvertise() {
        AdLoader adLoader = new AdLoader.Builder(requireActivity(), requireActivity().getResources().getString(R.string.inrescipe_banner_ad_unit_id))
                .forNativeAd(nativeAd -> {
                    ColorDrawable cd = new ColorDrawable(0xFFFFFFFF);
                    NativeTemplateStyle styles = new
                            NativeTemplateStyle.Builder().withMainBackgroundColor(cd).build();
                    TemplateView template = rootView.findViewById(R.id.temp_view);
                    template.setVisibility(View.VISIBLE);
                    template.setStyles(styles);
                    template.setNativeAd(nativeAd);
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }



}