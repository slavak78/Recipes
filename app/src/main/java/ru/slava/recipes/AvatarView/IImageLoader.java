package ru.slava.recipes.AvatarView;

import androidx.annotation.NonNull;

import ru.slava.recipes.AvatarView.views.AvatarView;

public interface IImageLoader {
    void loadImage(@NonNull AvatarView avatarView, @NonNull AvatarPlaceholder avatarPlaceholder, String avatarUrl);

    void loadImage(@NonNull AvatarView avatarView, String avatarUrl, String name);

    void loadImage(@NonNull AvatarView avatarView, String avatarUrl, String name, int textSizePercentage);
}
