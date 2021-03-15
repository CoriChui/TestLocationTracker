package com.kaonstudio.testlocationtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.kaonstudio.testlocationtracker.services.TrackingService;
import com.kaonstudio.testlocationtracker.ui.MainViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration appBarConfiguration;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_App);
        setContentView(R.layout.activity_main);
        onForegroundServiceClicked(getIntent());
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            drawerLayout = findViewById(R.id.drawer);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            NavigationView navigationView = findViewById(R.id.nav_view);
            appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
                    .setOpenableLayout(drawerLayout)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onForegroundServiceClicked(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else return false;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), drawerLayout);
    }

    private boolean isValidDestination(int destination) {
        return destination != Objects.requireNonNull(Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination()).getId();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        item.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.menu_map:
                if (isValidDestination(R.id.mapFragment))
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.mapFragment);
                return true;
            case R.id.menu_history:
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.historyFragment);
                return true;
            default:
                return true;
        }
    }

    private void onForegroundServiceClicked(@Nullable Intent intent) {
        if (intent != null) {
            if (intent.getAction().equals(TrackingService.NAVIGATE_TO_TRACKING_FRAGMENT)) {
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                if (navHostFragment != null) {
                    navHostFragment.getNavController().navigate(R.id.action_global_mapFragment);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }
}
