package com.suyogbauskar.calmora.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.suyogbauskar.calmora.ImageContainer;
import com.suyogbauskar.calmora.fragments.FearHeightImg;
import com.suyogbauskar.calmora.fragments.FearSpaceImg;


public class ViewPagerAdapter2 extends FragmentStateAdapter {
    public ViewPagerAdapter2(@NonNull ImageContainer fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new FearHeightImg();
            case 1:
                return new FearSpaceImg();
            default:
                return new FearHeightImg();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}