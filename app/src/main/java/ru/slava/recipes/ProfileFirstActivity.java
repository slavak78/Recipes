package ru.slava.recipes;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.rtoshiro.secure.SecureSharedPreferences;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.ByteString;
import ru.slava.recipes.AvatarView.GlideLoader;
import ru.slava.recipes.AvatarView.IImageLoader;
import ru.slava.recipes.AvatarView.views.AvatarView;

public class ProfileFirstActivity extends AppCompatActivity {
    Button change, updateinfo;
    ActivityResultLauncher<Intent> ViborLauncher;
    Intent intentdata;
    Uri photodir1;
    AvatarView avatar;
    TextInputEditText firstname, email, about;
    String user_id;
    ActivityResultLauncher<String[]> ReadStorageLauncher;
    final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    String APP_PREFERENCES_PHONE = "phone";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilefirst);
        ViborLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        intentdata = result.getData();
                        takephoto();
                    }
                });
        avatar = findViewById(R.id.avatar);
        change = findViewById(R.id.change);
        change.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!hasPermissions(PERMISSIONS)) {
                    ReadStorageLauncher.launch(PERMISSIONS);
                } else {
                    docheck();
                }
            }
        });
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        firstname = findViewById(R.id.firstname);
        email = findViewById(R.id.email);
        updateinfo = findViewById(R.id.updateinfo);
        about = findViewById(R.id.about);

        updateinfo.setOnClickListener(v -> {
            boolean dos = true;
            if(Objects.requireNonNull(firstname.getText()).toString().equals("")) {
                firstname.setError(getResources().getString(R.string.notspecified));
                dos = false;
            }
            if(!Objects.requireNonNull(email.getText()).toString().equals("")) {
                if(!isValidEmail(email.getText().toString())) {
                    email.setError(getResources().getString(R.string.notvalidemail));
                    dos = false;
                }
            }
            if(dos) {
                dozapros(user_id);
            }
        });

        ReadStorageLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    if (permissions.containsValue(true)) {
                       docheck();
                    }
                });
    }


    public void docheck() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ViborLauncher.launch(Intent.createChooser(intent, getResources().getString(R.string.choose)));
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void takephoto() {
        if (null != intentdata) { // checking empty selection
            if (null != intentdata.getClipData()) { // checking multiple selection or not
                for (int i = 0; i < intentdata.getClipData().getItemCount(); i++) {
                    try {
                        photodir1 = intentdata.getClipData().getItemAt(i).getUri();
                        IImageLoader imageLoader = new GlideLoader();
                        imageLoader.loadImage(avatar, String.valueOf(photodir1), "");
                    } catch (Exception ignored) {

                    }
                }

            } else {
                try {
                    photodir1 = intentdata.getData();
                    IImageLoader imageLoader = new GlideLoader();
                    imageLoader.loadImage(avatar, String.valueOf(photodir1), "");
                } catch (Exception l) {
                    l.printStackTrace();
                }
            }
        }
    }

    private boolean hasPermissions(String[] permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(ProfileFirstActivity.this, permission) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
            return false;
        }
        return true;
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

    public void dozapros(String user_id) {
        OkHttpClient client = doclient();
        String uniqueID = UUID.randomUUID().toString();
        ByteString delimiter = ByteString.encodeUtf8("-------------" + uniqueID);
        List<MultipartBody.Part> parts = new ArrayList<>();
        MultipartBody.Part part;
        part = MultipartBody.Part.createFormData("tel", user_id);
        parts.add(part);
        part = MultipartBody.Part.createFormData("fio", Objects.requireNonNull(firstname.getText()).toString());
        parts.add(part);
        part = MultipartBody.Part.createFormData("email", Objects.requireNonNull(email.getText()).toString());
        parts.add(part);
        part = MultipartBody.Part.createFormData("about", Objects.requireNonNull(about.getText()).toString());
        parts.add(part);

        if(!(photodir1 == null)) {
            MultipartBody.Part[] partfiles = new MultipartBody.Part[1];
            File file = new File(Objects.requireNonNull(UriUtils.getPathFromUri(this, photodir1)));
                RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
                partfiles[0] = MultipartBody.Part.createFormData("attachment", file.getName(), requestFile);
                parts.add(partfiles[0]);
        }
                 MultipartBody m = new MultipartBody(delimiter, MultipartBody.FORM, parts);

        Request request = new Request.Builder()
                .url("https://www.книгавкусныхидей.рф/recipes/changeclient.php")
                .post(m)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> fail(e.getClass().getName()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    assert responseBody != null;
                    runOnUiThread(() ->dosome(user_id));
                }
            }
        });

    }

    public void fail(String h) {
        String error;
        if(h.equals("java.net.SocketTimeoutException")) {
            error = getResources().getString(R.string.noserver);
        } else {
            error = getResources().getString(R.string.nointernet);
        }
        final AlertDialog aboutDialog = new AlertDialog.Builder(
                ProfileFirstActivity.this).setTitle(getResources().getString(R.string.error)).setMessage(error)
                .setPositiveButton("OK", (dialog, which) -> {
                }).create();
        aboutDialog.show();
    }

    public void dosome(String user_id) {
        SecureSharedPreferences mSettings = new SecureSharedPreferences(ProfileFirstActivity.this);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_PHONE, user_id);
        editor.apply();
        Intent intent = new Intent(ProfileFirstActivity.this, GlavActivity.class);
        startActivity(intent);
        finish();
    }
}
