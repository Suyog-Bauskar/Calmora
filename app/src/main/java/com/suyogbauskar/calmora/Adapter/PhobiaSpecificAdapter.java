package com.suyogbauskar.calmora.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.suyogbauskar.calmora.fragments.AnxityFragment;
import com.suyogbauskar.calmora.fragments.DizzinessVertigoFragment;
import com.suyogbauskar.calmora.fragments.FeelingOfSuffocationFragment;
import com.suyogbauskar.calmora.fragments.HeightFiveFragment;
import com.suyogbauskar.calmora.fragments.HeightFourFragment;
import com.suyogbauskar.calmora.fragments.HeightOneFragment;
import com.suyogbauskar.calmora.fragments.HeightThreeFragment;
import com.suyogbauskar.calmora.fragments.HeightTwoFragment;
import com.suyogbauskar.calmora.fragments.HeightVideoFragment;
import com.suyogbauskar.calmora.fragments.IntenseNeedToEscapeFragment;
import com.suyogbauskar.calmora.fragments.MentalDistortionFragment;
import com.suyogbauskar.calmora.fragments.NauseaFragment;
import com.suyogbauskar.calmora.fragments.ShortnessofBreathFragment;
import com.suyogbauskar.calmora.fragments.SpaceFiveFragment;
import com.suyogbauskar.calmora.fragments.SpaceFourFragment;
import com.suyogbauskar.calmora.fragments.SpaceOneFragment;
import com.suyogbauskar.calmora.fragments.SpaceThreeFragment;
import com.suyogbauskar.calmora.fragments.SpaceTwoFragment;
import com.suyogbauskar.calmora.fragments.SpaceVideoFragment;
import com.suyogbauskar.calmora.fragments.TremblingorShakingFragment;

// New phobia fragment imports
import com.suyogbauskar.calmora.fragments.ExcessiveFearDogsFragment;
import com.suyogbauskar.calmora.fragments.AvoidanceBehaviorDogsFragment;
import com.suyogbauskar.calmora.fragments.PhysicalSymptomsDogsFragment;
import com.suyogbauskar.calmora.fragments.IrrationalThoughtsDogsFragment;
import com.suyogbauskar.calmora.fragments.DogOneFragment;
import com.suyogbauskar.calmora.fragments.DogTwoFragment;
import com.suyogbauskar.calmora.fragments.DogThreeFragment;
import com.suyogbauskar.calmora.fragments.DogFourFragment;
import com.suyogbauskar.calmora.fragments.DogFiveFragment;
import com.suyogbauskar.calmora.fragments.DogVideoFragment;

import com.suyogbauskar.calmora.fragments.WaterPanicResponseFragment;
import com.suyogbauskar.calmora.fragments.WaterAvoidanceFragment;
import com.suyogbauskar.calmora.fragments.BreathingDifficultyWaterFragment;
import com.suyogbauskar.calmora.fragments.DrowningAnxietyFragment;
import com.suyogbauskar.calmora.fragments.WaterOneFragment;
import com.suyogbauskar.calmora.fragments.WaterTwoFragment;
import com.suyogbauskar.calmora.fragments.WaterThreeFragment;
import com.suyogbauskar.calmora.fragments.WaterFourFragment;
import com.suyogbauskar.calmora.fragments.WaterFiveFragment;
import com.suyogbauskar.calmora.fragments.WaterVideoFragment;

import com.suyogbauskar.calmora.fragments.SpiderPanicResponseFragment;
import com.suyogbauskar.calmora.fragments.SpiderAvoidanceFragment;
import com.suyogbauskar.calmora.fragments.SpiderPhysicalSymptomsFragment;
import com.suyogbauskar.calmora.fragments.SpiderContaminationFearFragment;
import com.suyogbauskar.calmora.fragments.SpiderOneFragment;
import com.suyogbauskar.calmora.fragments.SpiderTwoFragment;
import com.suyogbauskar.calmora.fragments.SpiderThreeFragment;
import com.suyogbauskar.calmora.fragments.SpiderFourFragment;
import com.suyogbauskar.calmora.fragments.SpiderFiveFragment;
import com.suyogbauskar.calmora.fragments.SpiderVideoFragment;

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
            case 8:
                return new HeightOneFragment();
            case 9:
                return new HeightTwoFragment();
            case 10:
                return new HeightThreeFragment();
            case 11:
                return new HeightFourFragment();
            case 12:
                return new HeightFiveFragment();
            case 13:
                return new SpaceOneFragment();
            case 14:
                return new SpaceTwoFragment();
            case 15:
                return new SpaceThreeFragment();
            case 16:
                return new SpaceFourFragment();
            case 17:
                return new SpaceFiveFragment();
            case 18:
                return new HeightVideoFragment();
            case 19:
                return new SpaceVideoFragment();
            // Cynophobia fragments (indices 20-29)
            case 20:
                return new ExcessiveFearDogsFragment();
            case 21:
                return new AvoidanceBehaviorDogsFragment();
            case 22:
                return new PhysicalSymptomsDogsFragment();
            case 23:
                return new IrrationalThoughtsDogsFragment();
            case 24:
                return new DogOneFragment();
            case 25:
                return new DogTwoFragment();
            case 26:
                return new DogThreeFragment();
            case 27:
                return new DogFourFragment();
            case 28:
                return new DogFiveFragment();
            case 29:
                return new DogVideoFragment();
            // Hydrophobia fragments (indices 30-39)
            case 30:
                return new WaterPanicResponseFragment();
            case 31:
                return new WaterAvoidanceFragment();
            case 32:
                return new BreathingDifficultyWaterFragment();
            case 33:
                return new DrowningAnxietyFragment();
            case 34:
                return new WaterOneFragment();
            case 35:
                return new WaterTwoFragment();
            case 36:
                return new WaterThreeFragment();
            case 37:
                return new WaterFourFragment();
            case 38:
                return new WaterFiveFragment();
            case 39:
                return new WaterVideoFragment();
            // Arachnophobia fragments (indices 40-49)
            case 40:
                return new SpiderPanicResponseFragment();
            case 41:
                return new SpiderAvoidanceFragment();
            case 42:
                return new SpiderPhysicalSymptomsFragment();
            case 43:
                return new SpiderContaminationFearFragment();
            case 44:
                return new SpiderOneFragment();
            case 45:
                return new SpiderTwoFragment();
            case 46:
                return new SpiderThreeFragment();
            case 47:
                return new SpiderFourFragment();
            case 48:
                return new SpiderFiveFragment();
            case 49:
                return new SpiderVideoFragment();
            default:
                return new DizzinessVertigoFragment();
        }
    }
} 