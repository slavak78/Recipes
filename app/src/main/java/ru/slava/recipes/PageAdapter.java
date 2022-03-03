package ru.slava.recipes;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PageAdapter extends FragmentStateAdapter {


    public PageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment frag;
        if(position==0) {
            frag = new IdeasFragment();
        } else {
            frag = new FriendsIdeasFragment();
        }
        return frag;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
