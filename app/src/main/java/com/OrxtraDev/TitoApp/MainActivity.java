package com.OrxtraDev.TitoApp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.OrxtraDev.TitoApp.adapter.ViewPagerAdapter;
import com.OrxtraDev.TitoApp.fragment.MapsFragment;
import com.OrxtraDev.TitoApp.fragment.MyOrdersFragment;
import com.OrxtraDev.TitoApp.fragment.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    //init the views

    private ViewPager viewPager;
    private TabLayout tabLayout;


    //the icons of tablayout  icon white  don't selected
    private int[] tabIcons = {
            R.drawable.ic_home_gray,
            R.drawable.ic_car_gray,
            R.drawable.ic_profile_gray
    };
    // icon of tab layout selected blue icons
    private int[] tabIconsSelected = {
            R.drawable.ic_home,
            R.drawable.ic_car,
            R.drawable.ic_profile
    };

    private String [] page_titles;

    //the firebase inital
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference df;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        df = FirebaseDatabase.getInstance().getReference();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return;
        }


        page_titles = new String[]{getResources().getString(R.string.home), getResources().getString(R.string.my_orders), getResources().getString(R.string.me)};

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MapsFragment(), "");
        adapter.addFragment(new MyOrdersFragment(), "");
        adapter.addFragment(new ProfileFragment(), "");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

    }
    /**
     * set up the tab icons to the tab layout and inti the custom view to it
     */
    private void setupTabIcons() {
        final View view0 = getLayoutInflater().inflate(R.layout.custom_tab, null);
        ((ImageView) view0.findViewById(R.id.image_tab)).setImageResource(R.drawable.ic_home);
        ((TextView) view0.findViewById(R.id.title)).setText(page_titles[0]);
        tabLayout.getTabAt(0).setCustomView(view0);




        View view1 = getLayoutInflater().inflate(R.layout.custom_tab, null);
        ((ImageView) view1.findViewById(R.id.image_tab)).setImageResource(tabIcons[1]);
        ((TextView) view1.findViewById(R.id.title)).setText(page_titles[1]);
        tabLayout.getTabAt(1).setCustomView(view1);

        View view2 = getLayoutInflater().inflate(R.layout.custom_tab, null);
        ((ImageView) view2.findViewById(R.id.image_tab)).setImageResource(tabIcons[2]);
        ((TextView) view2.findViewById(R.id.title)).setText(page_titles[2]);
        tabLayout.getTabAt(2).setCustomView(view2);




        final View[] selectedImageResources = {view0, view1,view2};

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((ImageView) selectedImageResources[tab.getPosition()].findViewById(R.id.image_tab))
                        .setImageResource(tabIconsSelected[tab.getPosition()]);

                tab.setCustomView(selectedImageResources[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((ImageView) selectedImageResources[tab.getPosition()].findViewById(R.id.image_tab))
                        .setImageResource(tabIcons[tab.getPosition()]);

                tab.setCustomView(selectedImageResources[tab.getPosition()]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
