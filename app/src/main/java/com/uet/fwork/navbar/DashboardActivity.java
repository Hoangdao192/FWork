package com.uet.fwork.navbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.uet.fwork.R;
import com.uet.fwork.account.profile.ProfileFragment;
import com.uet.fwork.chat.ChatListFragment;
import com.uet.fwork.database.model.UserRole;
import com.uet.fwork.database.repository.Repository;
import com.uet.fwork.database.repository.UserRepository;
import com.uet.fwork.firebasehelper.FirebaseAuthHelper;
import com.uet.fwork.notification.NotificationFragment;

public class DashboardActivity extends AppCompatActivity {

    private static final String LOG_TAG = "Dashboard activity";

    private UserRepository userRepository;
    private FirebaseAuthHelper firebaseAuthHelper;

    FirebaseAuth firebaseAuth;

    BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment = new HomeFragment();
    private CandidateDashboardFragment candidateDashboardFragment = new CandidateDashboardFragment();
    private EmployerDashboardFragment employerDashboardFragment = new EmployerDashboardFragment();
    private NotificationFragment notificationFragment = new NotificationFragment();
    private Fragment dashboardFragment = new ProfileFragment();
    private SearchFragment searchFragment = new SearchFragment();
    private ChatListFragment inboxFragment = new ChatListFragment();
    private Fragment active;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthHelper = FirebaseAuthHelper.getInstance();
        userRepository = UserRepository.getInstance();

        homeFragment = new HomeFragment();
        notificationFragment = new NotificationFragment();
        String userRole = firebaseAuthHelper.getUser().getRole();
        if (userRole != null) {
            if (userRole.equals(UserRole.CANDIDATE)) {
                dashboardFragment = new CandidateDashboardFragment();
            } else if (userRole.equals(UserRole.EMPLOYER)) {
                dashboardFragment = new EmployerDashboardFragment();
            }
        }
        searchFragment = new SearchFragment();
        inboxFragment = new ChatListFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.content, searchFragment, "search")
                .hide(searchFragment).commit();
        fragmentManager.beginTransaction()
                .add(R.id.content, notificationFragment, "notification")
                .hide(notificationFragment).commit();
        fragmentManager.beginTransaction()
                .add(R.id.content, inboxFragment, "chat")
                .hide(inboxFragment).commit();
        fragmentManager.beginTransaction()
                .add(R.id.content, dashboardFragment, "dashboard")
                .hide(dashboardFragment).commit();
        active = homeFragment;
        fragmentManager.beginTransaction().add(R.id.content, homeFragment, "home").commit();

        bottomNavigationView = findViewById(R.id.navigation);
//        getSupportFragmentManager().beginTransaction().replace(R.id.content, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                clearFragmentManagerBackstack();
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        fragmentManager.beginTransaction().show(homeFragment).commit();
                        active = homeFragment;
                        hideAllFragment();
                        return true;
                    case R.id.nav_profile:
                        fragmentManager.beginTransaction().show(dashboardFragment).commit();
                        active = dashboardFragment;
                        hideAllFragment();
                        return true;
                    case R.id.nav_search:
                        fragmentManager.beginTransaction().show(searchFragment).commit();
                        active = searchFragment;
                        hideAllFragment();
                        return true;
                    case R.id.nav_notifications:
                        fragmentManager.beginTransaction().show(notificationFragment).commit();
                        active = notificationFragment;
                        hideAllFragment();
                        return true;
                    case R.id.nav_inbox:
                        fragmentManager.beginTransaction().show(inboxFragment).commit();
                        active = inboxFragment;
                        hideAllFragment();
                        return true;
                }
                return false;
            }
        });
    }

    private void clearFragmentManagerBackstack() {
        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
    }

    private void hideAllFragment() {
        fragmentManager.getFragments().forEach(fragment -> {
            if (!active.equals(fragment)) {
                fragmentManager.beginTransaction().hide(fragment).commit();
            }
        });
    }
}