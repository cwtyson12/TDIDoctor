package com.example.charl.tdidoctorv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TabItem gauges, history;
    private PageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        gauges = (TabItem) findViewById(R.id.Gauges);
        history = (TabItem) findViewById(R.id.History);

        viewPager = (ViewPager2) findViewById(R.id.viewpager);
        viewPager.setUserInputEnabled(true);   //disable swiping


        pageAdapter = new PageAdapter(getSupportFragmentManager(), getLifecycle(), tabLayout.getTabCount());

        viewPager.setAdapter(pageAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(MainActivity.this, "onTabSelected called, tab=" + tab.getPosition(), Toast.LENGTH_SHORT).show();
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
