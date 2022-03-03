package ru.slava.recipes;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> {
    List<Share> data;
    Context mCtx;
    Activity activity;
    LinearLayout scr;
    ImageView photo;
    final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    ActivityResultLauncher<String[]> WriteStorageLauncher;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon1;
        TextView title;
        View v;

        public ViewHolder(View v) {
            super(v);
            this.v = v;
            icon1 = v.findViewById(R.id.icon);
            title = v.findViewById(R.id.title);
        }
    }


    public ShareAdapter(List<Share> data, Context mCtx, Activity activity, LinearLayout scr, ImageView photo, ActivityResultLauncher<String[]> WriteStorageLauncher) {
        this.data = data;
        this.mCtx = mCtx;
        this.activity = activity;
        this.scr = scr;
        this.photo = photo;
        this.WriteStorageLauncher = WriteStorageLauncher;
    }

    @NotNull
    @Override
    public ShareAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_item, parent, false);

        return new ViewHolder(v);
    }


    private static String getGalleryPath(Context mCtx) {
        return mCtx.getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/";
     //   return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/";
    }

    private void SavePicture(ImageView iv, String idd) throws IOException {

        OutputStream fOut;

        File dest = new File(getGalleryPath(mCtx)+"Recipes");
        boolean wasSuccessful = dest.mkdirs();
        if (!wasSuccessful) {
            System.out.println("was not successful.");
        }
        dest = new File(getGalleryPath(mCtx) + "Recipes/" + idd + ".jpg");
        fOut = new FileOutputStream(dest);

        Bitmap bitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // сохранять картинку в jpeg-формате с 85% сжатия.
        fOut.flush();
        fOut.close();
        MediaStore.Images.Media.insertImage(mCtx.getContentResolver(), dest.getAbsolutePath(), dest.getName(), dest.getName());
        MediaScannerConnection.scanFile(mCtx,
                new String[]{dest.toString()}, null,
                (path, uri) -> {
                });
    }


    @Override
    public void onBindViewHolder(@NonNull final ShareAdapter.ViewHolder holder, int position) {
        final Share current = data.get(position);
        if(current.type.equals("download")) {
            holder.icon1.setImageDrawable(current.icon);
            holder.title.setText(mCtx.getResources().getString(R.string.savephoto));
            holder.icon1.setOnClickListener(v -> {
                WriteStorageLauncher.launch(PERMISSIONS);
                Toast.makeText(mCtx, mCtx.getResources().getString(R.string.saved), Toast.LENGTH_LONG).show();
            });
        }
        if(current.type.equals("copylink")) {
            holder.icon1.setImageDrawable(current.icon);
            holder.title.setText(mCtx.getResources().getString(R.string.copylink));
            holder.icon1.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) mCtx.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", current.link);
                clipboard.setPrimaryClip(clip);
            });
        }
        if(current.type.equals("instamessage")) {
            holder.icon1.setImageDrawable(current.icon);
            holder.title.setText(mCtx.getResources().getString(R.string.instamessage));
            holder.icon1.setOnClickListener(v -> {
                try {
                    SavePicture(photo, current.idd);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    ClipboardManager clipboard = (ClipboardManager) mCtx.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", current.link);
                    clipboard.setPrimaryClip(clip);
                    File file = new File(getGalleryPath(mCtx) + "Recipes/" + current.idd + ".jpg");
                    Uri uri = FileProvider.getUriForFile(
                            mCtx,
                            mCtx.getPackageName() + ".provider",
                            file);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.setComponent(new ComponentName(
                            current.name.activityInfo.packageName,
                            current.name.activityInfo.name));
                    sendIntent.setType("image/*");
                    mCtx.startActivity(sendIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        if(current.type.equals("instapost")) {
            holder.icon1.setImageDrawable(current.icon);
            holder.title.setText(mCtx.getResources().getString(R.string.instapost));
            holder.icon1.setOnClickListener(v -> {
                try {
                    SavePicture(photo, current.idd);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    ClipboardManager clipboard = (ClipboardManager) mCtx.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", current.link);
                    clipboard.setPrimaryClip(clip);
                    File file = new File(getGalleryPath(mCtx) + "Recipes/" + current.idd + ".jpg");
                    Uri uri = FileProvider.getUriForFile(
                            mCtx,
                            mCtx.getPackageName() + ".provider",
                            file);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.setComponent(new ComponentName(
                            current.name.activityInfo.packageName,
                            current.name.activityInfo.name));
                    sendIntent.setType("image/*");
                    mCtx.startActivity(sendIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        if(current.type.equals("instastory")) {
            holder.icon1.setImageDrawable(current.icon);
            holder.title.setText(mCtx.getResources().getString(R.string.instastory));
            holder.icon1.setOnClickListener(v -> {
                try {
                    SavePicture(photo, current.idd);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    File file = new File(getGalleryPath(mCtx) + "Recipes/" + current.idd + ".jpg");
                    Uri uri = FileProvider.getUriForFile(
                            mCtx,
                            mCtx.getPackageName() + ".provider",
                            file);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.setComponent(new ComponentName(
                            current.name.activityInfo.packageName,
                            current.name.activityInfo.name));
                    sendIntent.setType("image/*");
                    mCtx.startActivity(sendIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        if(current.type.equals("viber")) {
            holder.icon1.setImageDrawable(current.icon);
            holder.title.setText(mCtx.getResources().getString(R.string.viber));
            holder.icon1.setOnClickListener(v -> {
                try {
                    SavePicture(photo, current.idd);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    ClipboardManager clipboard = (ClipboardManager) mCtx.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", current.link);
                    clipboard.setPrimaryClip(clip);
                    File file = new File(getGalleryPath(mCtx) + "Recipes/" + current.idd + ".jpg");
                    Uri uri = FileProvider.getUriForFile(
                            mCtx,
                            mCtx.getPackageName() + ".provider",
                            file);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.setComponent(new ComponentName(
                            current.name.activityInfo.packageName,
                            current.name.activityInfo.name));
                    sendIntent.setType("image/*");
                    mCtx.startActivity(sendIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        if(current.type.equals("vkstory")) {
            holder.icon1.setImageDrawable(current.icon);
            holder.title.setText(mCtx.getResources().getString(R.string.vkstory));
            holder.icon1.setOnClickListener(v -> {
                try {
                    SavePicture(photo, current.idd);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    File file = new File(getGalleryPath(mCtx) + "Recipes/" + current.idd + ".jpg");
                    Uri uri = FileProvider.getUriForFile(
                            mCtx,
                            mCtx.getPackageName() + ".provider",
                            file);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.setComponent(new ComponentName(
                            current.name.activityInfo.packageName,
                            current.name.activityInfo.name));
                    sendIntent.setType("image/*");
                    mCtx.startActivity(sendIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        if(current.type.equals("vkpost")) {
            holder.icon1.setImageDrawable(current.icon);
            holder.title.setText(mCtx.getResources().getString(R.string.vk));
            holder.icon1.setOnClickListener(v -> {
                try {
                    SavePicture(photo, current.idd);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, current.link);
                    File file = new File(getGalleryPath(mCtx) + "Recipes/" + current.idd + ".jpg");
                    Uri uri = FileProvider.getUriForFile(
                            mCtx,
                            mCtx.getPackageName() + ".provider",
                            file);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.setComponent(new ComponentName(
                            current.name.activityInfo.packageName,
                            current.name.activityInfo.name));
                    sendIntent.setType("image/*");
                    mCtx.startActivity(sendIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        if(current.type.equals("whatsapp")) {
            holder.icon1.setImageDrawable(current.icon);
            holder.title.setText(mCtx.getResources().getString(R.string.whatsapp));
            holder.icon1.setOnClickListener(v -> {
                try {
                    SavePicture(photo, current.idd);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, current.link);
                    File file = new File(getGalleryPath(mCtx) + "Recipes/" + current.idd + ".jpg");
                    Uri uri = FileProvider.getUriForFile(
                            mCtx,
                            mCtx.getPackageName() + ".provider",
                            file);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.setComponent(new ComponentName(
                            current.name.activityInfo.packageName,
                            current.name.activityInfo.name));
                    sendIntent.setType("image/*");
                    mCtx.startActivity(sendIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        if(current.type.equals("ok")) {
            holder.icon1.setImageDrawable(current.icon);
            holder.title.setText(mCtx.getResources().getString(R.string.ok));
            holder.icon1.setOnClickListener(v -> {
                try {
                    SavePicture(photo, current.idd);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    ClipboardManager clipboard = (ClipboardManager) mCtx.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", current.link);
                    clipboard.setPrimaryClip(clip);
                    File file = new File(getGalleryPath(mCtx) + "Recipes/" + current.idd + ".jpg");
                    Uri uri = FileProvider.getUriForFile(
                            mCtx,
                            mCtx.getPackageName() + ".provider",
                            file);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.setComponent(new ComponentName(
                            current.name.activityInfo.packageName,
                            current.name.activityInfo.name));
                    sendIntent.setType("image/*");
                    mCtx.startActivity(sendIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        if(current.type.equals("telegram")) {
            holder.icon1.setImageDrawable(current.icon);
            holder.title.setText(mCtx.getResources().getString(R.string.telegram));
            holder.icon1.setOnClickListener(v -> {
                try {
                    SavePicture(photo, current.idd);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, current.link);
                    File file = new File(getGalleryPath(mCtx) + "Recipes/" + current.idd + ".jpg");
                    Uri uri = FileProvider.getUriForFile(
                            mCtx,
                            mCtx.getPackageName() + ".provider",
                            file);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.setComponent(new ComponentName(
                            current.name.activityInfo.packageName,
                            current.name.activityInfo.name));
                    sendIntent.setType("image/*");
                    mCtx.startActivity(sendIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        if(current.type.equals("still")) {
            holder.icon1.setImageDrawable(current.icon);
            holder.title.setText(mCtx.getResources().getString(R.string.still1));
            holder.icon1.setOnClickListener(v -> {
                try {
                    SavePicture(photo, current.idd);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    ClipboardManager clipboard = (ClipboardManager) mCtx.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", current.link);
                    clipboard.setPrimaryClip(clip);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, current.link);
                    File file = new File(getGalleryPath(mCtx) + "Recipes/" + current.idd + ".jpg");
                    Uri uri = FileProvider.getUriForFile(
                            mCtx,
                            mCtx.getPackageName() + ".provider",
                            file);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.setType("image/*");
                    mCtx.startActivity(sendIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}