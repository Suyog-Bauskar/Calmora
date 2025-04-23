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

/**
 * Adapter that shows only fragments specific to the user's phobia type
 */
public class PhobiaSpecificAdapter extends FragmentStateAdapter {

    private final int[] fragmentIndices;

    /**
     * Constructor for fragment activity
     * @param activity The fragment activity
     * @param fragmentIndices Array of fragment indices to show
     */
    public PhobiaSpecificAdapter(@NonNull FragmentActivity activity, int[] fragmentIndices) {
        super(activity);
        this.fragmentIndices = fragmentIndices;
    }

    /**
     * Constructor for parent fragment
     * @param fragment The parent fragment
     * @param fragmentIndices Array of fragment indices to show
     */
    public PhobiaSpecificAdapter(@NonNull Fragment fragment, int[] fragmentIndices) {
        super(fragment);
        this.fragmentIndices = fragmentIndices;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Map the position to the actual fragment index
        int actualFragmentIndex = fragmentIndices[position];
        
        // Return the fragment based on the actual index
        return getFragmentByIndex(actualFragmentIndex);
    }

    @Override
    public int getItemCount() {
        return fragmentIndices.length;
    }

    /**
     * Get the fragment instance by its index
     * @param index The fragment index
     * @return The fragment instance
     */
    private Fragment getFragmentByIndex(int index) {
        switch (index) {
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
} 