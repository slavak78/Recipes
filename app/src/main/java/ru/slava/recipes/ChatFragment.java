package ru.slava.recipes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rtoshiro.secure.SecureSharedPreferences;

public class ChatFragment extends Fragment {
    String idd,token,user_id;
    SecureSharedPreferences mSettings;
    String APP_PREFERENCES_PHONE = "phone";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            idd = bundle.getString("idd");
            token = bundle.getString("token");
        }
        mSettings = new SecureSharedPreferences(requireActivity());
        if (mSettings.contains(APP_PREFERENCES_PHONE)) {
            user_id = mSettings.getString(APP_PREFERENCES_PHONE, "");
        } else {
            user_id = "0";
        }

        return rootView;
    }
}