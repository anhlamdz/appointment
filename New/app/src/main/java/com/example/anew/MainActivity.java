package com.example.anew;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.anew.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    String SearchAddress = "Đại Học Kiến Trúc Hà Nội";
    ActivityMainBinding binding;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.navBottomTab.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homeScreen:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.locationScreen:
                    replaceFragment(new LocationListFragment());
                    break;
                case R.id.mapScreen:
                    replaceFragment(new MapFragment());
                    break;
            }


            return  true;
        });

    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }
}