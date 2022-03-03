package ru.slava.recipes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.window.layout.WindowMetrics;
import androidx.window.layout.WindowMetricsCalculator;

import com.github.rtoshiro.secure.SecureSharedPreferences;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.EmptyRequestListener;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;

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
import okhttp3.Response;
import okhttp3.ResponseBody;


public class GlavActivity extends AppCompatActivity {
    AdView adView;
    LinearLayout adContainerView;
    SecureSharedPreferences mSettings;
    String APP_PREFERENCES_PHONE = "phone";
    String APP_PREFERENCES_ADV = "adv";
    String APP_PREFERENCES_TOK = "tok";
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    AppBarConfiguration mAppBarConfiguration;
    public ActivityCheckout mCheckout;
    public NavController navController;
    String res;
    List<Nav> data = new ArrayList<>();
    NavAdapter adapter;
    CustomNavigationView nav_view;
    String adv_hash;

    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_glav);
        int y = pxToDp(30);
        drawerLayout = findViewById(R.id.drawer_layout1);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Nav fishData = new Nav();
        fishData.name = getResources().getString(R.string.ideas);
        fishData.image = AppCompatResources.getDrawable(this, R.drawable.ideas_selector);
        data.add(fishData);

        Nav fishData1 = new Nav();
        fishData1.name = getResources().getString(R.string.newfrom);
        fishData1.image = AppCompatResources.getDrawable(this, R.drawable.persons_selector);
        data.add(fishData1);

        Nav fishData2 = new Nav();
        fishData2.name = getResources().getString(R.string.chat);
        fishData2.image = AppCompatResources.getDrawable(this, R.drawable.chat_selector);
        data.add(fishData2);


        Nav fishData4 = new Nav();
        fishData4.name = getResources().getString(R.string.fav);
        fishData4.image = AppCompatResources.getDrawable(this, R.drawable.favourites_selector);
        data.add(fishData4);

        Nav fishData3 = new Nav();
        fishData3.name = getResources().getString(R.string.person);
        fishData3.image = AppCompatResources.getDrawable(this, R.drawable.user_selector);
        data.add(fishData3);

        Nav fishData5 = new Nav();
        fishData5.name = getResources().getString(R.string.logout);
        fishData5.image = AppCompatResources.getDrawable(this, R.drawable.logout_selector);
        data.add(fishData5);


        nav_view = findViewById(R.id.nav_view);
        int hei = getStatusBarHeight();
        if(hei==0) {
            nav_view.setPadding(0, 38, 0, 0);
        } else {
            nav_view.setPadding(0, getStatusBarHeight(), 0, 0);
        }
        int adWidth;
        WindowMetrics windowMetrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this);
        Rect currentBounds = windowMetrics.getBounds();
        adWidth = currentBounds.width();
        nav_view.getLayoutParams().width = adWidth;


        adapter = new NavAdapter(data, this);
        nav_view.setAdapter(adapter, this, drawerLayout);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.ideas, R.id.news)
                .setOpenableLayout(drawerLayout)
                .build();
        navController = Navigation.findNavController(this, R.id.fragment_container);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
     //   NavigationUI.setupWithNavController(nav_view, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            String dest = (String) destination.getLabel();
            assert dest != null;
            if(dest.equals("")) {
                String f = (String) Objects.requireNonNull(navController.getCurrentDestination()).getLabel();
                Objects.requireNonNull(getSupportActionBar()).setTitle(f);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        });



        mSettings = new SecureSharedPreferences(GlavActivity.this);
        if (mSettings.contains(APP_PREFERENCES_PHONE)) {
            dozapros(mSettings.getString(APP_PREFERENCES_PHONE, ""));
        }

        adContainerView = findViewById(R.id.ad_view_container);

        mCheckout = Checkout.forActivity(this, Recipes.get().getBilling());
        mCheckout.start();
        mCheckout.createPurchaseFlow(new PurchaseListener());

        Inventory mInventory = mCheckout.makeInventory();
        mInventory.load(Inventory.Request.create()
                .loadAllPurchases()
                .loadSkus(ProductTypes.SUBSCRIPTION, "sub_02"), new InventoryCallback());


    }


    private class PurchaseListener extends EmptyRequestListener<Purchase> {
        @Override
        public void onSuccess(@NonNull Purchase purchase) {
            dopur(mSettings.getString(APP_PREFERENCES_PHONE, ""));
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(APP_PREFERENCES_ADV, adv_hash);
            editor.apply();
            Intent intent = new Intent(GlavActivity.this, SActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onError(int response, @NonNull Exception e) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.remove(APP_PREFERENCES_ADV);
            editor.apply();
        }
    }

        private class InventoryCallback implements Inventory.Callback {
        @Override
        public void onLoaded(Inventory.Products products) {
            final Inventory.Product product = products.get(ProductTypes.SUBSCRIPTION);
            if (product.isPurchased("sub_02")) {
       //         SecureSharedPreferences mSett = new SecureSharedPreferences(GlavActivity.this);
        //        if (mSett.contains(APP_PREFERENCES_PHONE)) {
         //           dozapros(mSett.getString(APP_PREFERENCES_PHONE, ""));
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString(APP_PREFERENCES_ADV, adv_hash);
                    editor.apply();
         //       }
            } else {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.remove(APP_PREFERENCES_ADV);
                editor.apply();
                doadvertise();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCheckout.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void doadvertise() {
        MobileAds.initialize(this, initializationStatus -> {
        });
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.adaptive_banner_ad_unit_id));
        adContainerView.addView(adView);
        adContainerView.post(this::loadBanner);
    }

    private void loadBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        int adWidth;
        WindowMetrics windowMetrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this);
        Rect currentBounds = windowMetrics.getBounds();
        adWidth = currentBounds.width();
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }



    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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

    public void dozapros(String tel) {
        int show = 0;
        if(!mSettings.contains(APP_PREFERENCES_ADV)) {
            show = 1;
        } else {
            if(!mSettings.getString(APP_PREFERENCES_ADV, "").equals(adv_hash)) {
                show = 1;
            }
        }
        String token = mSettings.getString(APP_PREFERENCES_TOK, "");
        String url;
        url = "https://www.книгавкусныхидей.рф/recipes/getuser.php?user_id=" + tel + "&show=" + show + "&token=" + token;

        OkHttpClient client = doclient();
        Request request = new Request.Builder()
                .url(url)
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
                    res = responseBody.string();
                    runOnUiThread(() -> dosome(res));
                }
            }
        });
    }


    public void dosome(String res) {
        JSONObject dataJsonObj;
        try {
            dataJsonObj = new JSONObject(res);
            adv_hash = dataJsonObj.getString("adv_hash");

        } catch (Exception ignored) {

        }
    }

    public void dopur(String tel) {
        OkHttpClient client = doclient();
        RequestBody formBody = new FormBody.Builder()
                .add("tel", tel)
                .build();
        Request request = new Request.Builder()
                .url("https://www.книгавкусныхидей.рф/recipes/dopur.php")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    assert responseBody != null;
                    res = responseBody.string();
                    runOnUiThread(() -> dosome5(res));

                }
            }
        });


    }

    public void dosome5(String res) {
        JSONObject dataJsonObj;
        try {
            dataJsonObj = new JSONObject(res);
            adv_hash = dataJsonObj.getString("adv_hash");

        } catch (Exception ignored) {

        }
    }
}