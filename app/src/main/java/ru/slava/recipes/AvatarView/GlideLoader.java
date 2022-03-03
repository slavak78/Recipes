package ru.slava.recipes.AvatarView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import ru.slava.recipes.AvatarView.views.AvatarView;


public class GlideLoader extends ImageLoaderBase {

    public GlideLoader() {
        super();
    }

    public GlideLoader(String defaultPlaceholderString) {
        super(defaultPlaceholderString);
    }

    @Override
    public void loadImage(@NonNull AvatarView avatarView, @NonNull AvatarPlaceholder avatarPlaceholder, @NonNull String avatarUrl) {
        Glide.with(avatarView.getContext())
                .load(avatarUrl)
                .placeholder(avatarPlaceholder)
                .fitCenter()
                .into(avatarView);
    }

}
