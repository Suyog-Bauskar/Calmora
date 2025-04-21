package com.suyogbauskar.calmora.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.suyogbauskar.calmora.fragments.AnxityFragment;
import com.suyogbauskar.calmora.fragments.DizzinessVertigoFragment;
import com.suyogbauskar.calmora.fragments.FeelingOfSuffocationFragment;
import com.suyogbauskar.calmora.fragments.IntenseNeedToEscapeFragment;
import com.suyogbauskar.calmora.fragments.MentalDistortionFragment;
import com.suyogbauskar.calmora.fragments.NauseaFragment;
import com.suyogbauskar.calmora.fragments.ShortnessofBreathFragment;
import com.suyogbauskar.calmora.fragments.TremblingorShakingFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public ViewPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DizzinessVertigoFragment();
            case 1:
                return new ShortnessofBreathFragment();
            case 2:
                return new TremblingorShakingFragment();
            case 3:
                return new AnxityFragment();
            case 4:
                return new MentalDistortionFragment();
            case 5:
                return new IntenseNeedToEscapeFragment();
            case 6:
                return new FeelingOfSuffocationFragment();
            case 7:
                return new NauseaFragment();
            default:
                return new DizzinessVertigoFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 8;
    }
}
