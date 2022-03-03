package ru.slava.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.widget.Toolbar;

import com.github.rtoshiro.secure.SecureSharedPreferences;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class RegistryActivity extends AppCompatActivity {
    EditText num;
    Button endstep;
    String tel,res;
    SecureSharedPreferences mSettings;
    String APP_PREFERENCES_PHONE = "phone";
    String APP_PREFERENCES_ADV = "adv";
    private FirebaseAuth mAuth;
    private String mVerificationId;
    ProgressBar pb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);

  //      getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.f1f1f1));
    //    getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.f1f1f1));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mSettings = new SecureSharedPreferences(RegistryActivity.this);
        Intent intent = getIntent();
        tel = intent.getStringExtra("tel");
        mAuth = FirebaseAuth.getInstance();
        pb = findViewById(R.id.progress_bar);
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                verifyCode(code);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                String message = getResources().getString(R.string.pre) + " " + tel;
                final AlertDialog aboutDialog = new AlertDialog.Builder(
                        RegistryActivity.this).setTitle(getResources().getString(R.string.error)).setMessage(message)
                        .setPositiveButton("OK", (dialog, which) -> {
                        }).create();
                aboutDialog.show();
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                pb.setVisibility(View.GONE);
            }
        };

        tel = "+16505553434";
     //   String smsCode = "654321";

      //  FirebaseAuthSettings firebaseAuthSettings = mAuth.getFirebaseAuthSettings();
     //   firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(tel, smsCode);

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(tel)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        num = findViewById(R.id.num);
        endstep = findViewById(R.id.endstep);

        num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int y = s.length();
                if(y>=6) {
                    endstep.setVisibility(View.VISIBLE);
                } else {
                    endstep.setVisibility(View.GONE);
                }
            }
        });

        endstep.setOnClickListener(v -> verifyCode(num.getText().toString()));
    }

    private void verifyCode(String code) {
        try {
            pb.setVisibility(View.VISIBLE);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        } catch (Exception e) {
            final AlertDialog aboutDialog = new AlertDialog.Builder(
                    RegistryActivity.this).setTitle(getResources().getString(R.string.error)).setMessage(getResources().getString(R.string.verifycode))
                    .setPositiveButton("OK", (dialog, which) -> {
                    }).create();
            aboutDialog.show();
            pb.setVisibility(View.GONE);
        }
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        dozapros3(tel);
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            final AlertDialog aboutDialog = new AlertDialog.Builder(
                                    RegistryActivity.this).setTitle(getResources().getString(R.string.error)).setMessage(getResources().getString(R.string.errorcode))
                                    .setPositiveButton("OK", (dialog, which) -> {
                                    }).create();
                            aboutDialog.show();
                            pb.setVisibility(View.GONE);
                        }
                    }
                });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            finish();
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

    public void fail(String h) {
        String error;
        if(h.equals("java.net.SocketTimeoutException")) {
            error = getResources().getString(R.string.noserver);
        } else {
            error = getResources().getString(R.string.nointernet);
        }
        final AlertDialog aboutDialog = new AlertDialog.Builder(
                RegistryActivity.this).setTitle(getResources().getString(R.string.error)).setMessage(error)
                .setPositiveButton("OK", (dialog, which) -> {
                }).create();
        aboutDialog.show();
    }

    public void dozapros3(String tel) {
        OkHttpClient client = doclient();
        RequestBody formBody = new FormBody.Builder()
                .add("tel", tel)
                .build();
        Request request = new Request.Builder()
                .url("https://www.книгавкусныхидей.рф/recipes/addclient.php")
                .post(formBody)
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
                    res = responseBody.string();
                    runOnUiThread(() -> dosome4(tel,res));
                }
            }
        });


    }


    public void dosome4(String tel, String res) {
        SecureSharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_PHONE, tel);
        JSONObject dataJsonObj;
        try {
            dataJsonObj = new JSONObject(res);
            String adv_hash = dataJsonObj.getString("adv_hash");
            if(!adv_hash.equals("")) {
                editor.putString(APP_PREFERENCES_ADV, adv_hash);
            }
            editor.apply();
            int bo = dataJsonObj.getInt("bo");
            if(bo==0) {
                Intent intent = new Intent(RegistryActivity.this, ProfileFirstActivity.class);
                intent.putExtra("user_id", tel);
                startActivity(intent);
            } else {
                Intent intent = new Intent(RegistryActivity.this, GlavActivity.class);
                startActivity(intent);
            }
            finish();
        } catch (Exception ignored) {

        }
    }
}
