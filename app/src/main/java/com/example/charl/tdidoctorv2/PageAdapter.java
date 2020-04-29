package com.example.charl.tdidoctorv2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Used to handle switching between tabs in application without loss of data transmission or stored data.
 */
public class PageAdapter extends FragmentStateAdapter {
    private int numTabs;

    public PageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, int numberOfTabs) {
        super(fragmentManager, lifecycle);
        numTabs = numberOfTabs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return new GaugesFragment();
            case 1:
                return new HistoryFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return numTabs;
    }
}
