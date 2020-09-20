package com.utkuakgungor.filmruleti;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.utkuakgungor.filmruleti.favorites.FavoritesFragment;
import com.utkuakgungor.filmruleti.home.HomeFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private HomeFragment homeFragment;
    private FavoritesFragment favoritesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        homeFragment = new HomeFragment();
        favoritesFragment = new FavoritesFragment();
        setFragment(homeFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch(menuItem.getItemId()){
                case R.id.navigation_home:
                    setFragment(homeFragment);
                    return true;
                case R.id.navigation_favorites:
                    setFragment(favoritesFragment);
                    return true;
                default:
                    return false;
            }
        });
    }

    private void setFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentManager.popBackStack();
        fragmentTransaction.commit();
    }
}