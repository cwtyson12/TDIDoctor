package com.example.charl.tdidoctorv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

/**
 * Initializes the application, as well as the fragments and ViewPager and PageAdapter to switch between them.
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private PageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tablayout);
        TabItem gauges = findViewById(R.id.Gauges);
        TabItem history = findViewById(R.id.Extras);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setUserInputEnabled(false);   //disable swiping


        pageAdapter = new PageAdapter(getSupportFragmentManager(), getLifecycle(), tabLayout.getTabCount());

        viewPager.setAdapter(pageAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 0)
                    pageAdapter.notifyDataSetChanged();
                else{
                    if(tab.getPosition() == 1)
                        pageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
