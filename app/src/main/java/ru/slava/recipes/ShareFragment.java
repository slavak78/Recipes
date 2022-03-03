package ru.slava.recipes;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ShareFragment extends Fragment {
    List<Share> data = new ArrayList<>();
    FrameLayout nointernet, rell;
    String idd,translit;
    Button update;
    LinearLayout main,scr;
    ImageView photo;
    RecyclerView mRecyclerView;
    ShareAdapter mAdapter;
    GridLayoutManager mLayoutManager;
    ActivityResultLauncher<String[]> WriteStorageLauncher;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_share, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            idd = bundle.getString("idd");
            translit = bundle.getString("translit");
        }
        rell = rootView.findViewById(R.id.rell);
        rell.setVisibility(View.VISIBLE);
        nointernet = rootView.findViewById(R.id.nointernet);
        main = rootView.findViewById(R.id.main);
        update = rootView.findViewById(R.id.update);
        photo = rootView.findViewById(R.id.photo);
        scr = rootView.findViewById(R.id.scr);
        mRecyclerView = rootView.findViewById(R.id.share_view);
        String url = "https://www.книгавкусныхидей.рф/recipes/getimage.php?id=" + idd;
        update.setOnClickListener(v -> {
            rell.setVisibility(View.VISIBLE);
            nointernet.setVisibility(View.GONE);
            main.setVisibility(View.GONE);
            Glide.with(requireActivity()).load(url).fitCenter().centerCrop().into(photo);
        });

        Glide.with(requireActivity()).load(url).fitCenter().centerCrop().into(photo);

        WriteStorageLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    if (permissions.containsValue(true)) {
                        try {
                            SavePicture(photo);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Share fishData = new Share();
        fishData.type = "download";
        fishData.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.download);
        data.add(fishData);

        Share fishData1 = new Share();
        fishData1.type = "copylink";
        fishData1.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.link);
        fishData1.link = "https://www.книгавкусныхидей.рф/recipes/" + translit;
        data.add(fishData1);

        PackageManager pm = requireActivity().getPackageManager();
        java.util.List<ResolveInfo> mApps = showAllShareApp();
        for (ResolveInfo info : mApps) {
            String pN = info.activityInfo.name.toLowerCase();
            if(pN.equals("com.instagram.direct.share.handler.directexternalmediashareactivityphoto")) {
                Share fishData2 = new Share();
                fishData2.type = "instamessage";
                fishData2.icon = info.loadIcon(pm);
                fishData2.name = info;
                fishData2.idd = idd;
                fishData2.link = requireActivity().getResources().getString(R.string.text_share) +  " https://www.книгавкусныхидей.рф/recipes/" + translit;
                data.add(fishData2);
            }
            if(pN.equals("com.instagram.share.handleractivity.sharehandleractivity")) {
                Share fishData2 = new Share();
                fishData2.type = "instapost";
                fishData2.icon = info.loadIcon(pm);
                fishData2.name = info;
                fishData2.idd = idd;
                fishData2.link = requireActivity().getResources().getString(R.string.text_share) +  " https://www.книгавкусныхидей.рф/recipes/" + translit;
                data.add(fishData2);
            }
            if(pN.equals("com.instagram.share.handleractivity.storysharehandleractivity")) {
                Share fishData2 = new Share();
                fishData2.type = "instastory";
                fishData2.icon = info.loadIcon(pm);
                fishData2.name = info;
                fishData2.idd = idd;
                fishData2.link = requireActivity().getResources().getString(R.string.text_share) +  " https://www.книгавкусныхидей.рф/recipes/" + translit;
                data.add(fishData2);
            }
            if(pN.equals("com.viber.voip.welcomeshareactivity")) {
                Share fishData2 = new Share();
                fishData2.type = "viber";
                fishData2.icon = info.loadIcon(pm);
                fishData2.name = info;
                fishData2.idd = idd;
                fishData2.link = requireActivity().getResources().getString(R.string.text_share) +  " https://www.книгавкусныхидей.рф/recipes/" + translit;
                data.add(fishData2);
            }
            if(pN.equals("com.vk.stories.storyshareactivity")) {
                Share fishData2 = new Share();
                fishData2.type = "vkstory";
                fishData2.icon = info.loadIcon(pm);
                fishData2.name = info;
                fishData2.idd = idd;
                fishData2.link = requireActivity().getResources().getString(R.string.text_share) +  " https://www.книгавкусныхидей.рф/recipes/" + translit;
                data.add(fishData2);
            }
            if(pN.equals("com.vkontakte.android.sendactivity")) {
                Share fishData2 = new Share();
                fishData2.type = "vkpost";
                fishData2.icon = info.loadIcon(pm);
                fishData2.name = info;
                fishData2.idd = idd;
                fishData2.link = requireActivity().getResources().getString(R.string.text_share) +  " https://www.книгавкусныхидей.рф/recipes/" + translit;
                data.add(fishData2);
            }
            if(pN.equals("com.whatsapp.contactpicker")) {
                Share fishData2 = new Share();
                fishData2.type = "whatsapp";
                fishData2.icon = info.loadIcon(pm);
                fishData2.name = info;
                fishData2.idd = idd;
                fishData2.link = requireActivity().getResources().getString(R.string.text_share) +  " https://www.книгавкусныхидей.рф/recipes/" + translit;
                data.add(fishData2);
            }
            if(pN.equals("ru.ok.android.ui.activity.chooseshareactivity")) {
                Share fishData2 = new Share();
                fishData2.type = "ok";
                fishData2.icon = info.loadIcon(pm);
                fishData2.name = info;
                fishData2.idd = idd;
                fishData2.link = requireActivity().getResources().getString(R.string.text_share) +  " https://www.книгавкусныхидей.рф/recipes/" + translit;
                data.add(fishData2);
            }
            if(pN.equals("org.telegram.ui.launchactivity")) {
                Share fishData2 = new Share();
                fishData2.type = "telegram";
                fishData2.icon = info.loadIcon(pm);
                fishData2.name = info;
                fishData2.idd = idd;
                fishData2.link = requireActivity().getResources().getString(R.string.text_share) +  " https://www.книгавкусныхидей.рф/recipes/" + translit;
                data.add(fishData2);
            }
        }

        Share fishData3 = new Share();
        fishData3.type = "still";
        fishData3.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.more);
        fishData3.link = "https://www.книгавкусныхидей.рф/recipes/" + translit;
        data.add(fishData3);


        mAdapter = new ShareAdapter(data, requireActivity(), requireActivity(), scr, photo, WriteStorageLauncher);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
       //mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, 15, false));

        return rootView;
    }


    private java.util.List<ResolveInfo> showAllShareApp() {
        java.util.List<ResolveInfo> mApps;
        Intent intent = new Intent(Intent.ACTION_SEND, null);
        File dest = new File(getGalleryPath(requireActivity())+"Recipes/times.jpg");
        Uri uri = Uri.fromFile(dest);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/*");
        PackageManager pManager = requireActivity().getPackageManager();
        mApps = pManager.queryIntentActivities(intent, 0);
        return mApps;
    }


    private static String getGalleryPath(Context mCtx) {
        return mCtx.getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/";
        //return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/";
    }

    private void SavePicture(ImageView iv) throws IOException {

        OutputStream fOut;

        File dest = new File(getGalleryPath(requireActivity())+"Recipes");
        boolean wasSuccessful = dest.mkdirs();
        if (!wasSuccessful) {
            System.out.println("was not successful.");
        }
        dest = new File(getGalleryPath(requireActivity()) + "Recipes/" + idd + ".jpg");
        fOut = new FileOutputStream(dest);

        Bitmap bitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // сохранять картинку в jpeg-формате с 85% сжатия.
        fOut.flush();
        fOut.close();
        MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), dest.getAbsolutePath(), dest.getName(), dest.getName());
        MediaScannerConnection.scanFile(requireActivity(),
                new String[]{dest.toString()}, null,
                (path, uri) -> {
                });
    }

}