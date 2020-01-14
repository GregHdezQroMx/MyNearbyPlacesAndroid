package com.jght.nearbyplaces.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jght.nearbyplaces.R;
import com.jght.nearbyplaces.utils.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar();
        NearbyPlacesMapFragment fragment = NearbyPlacesMapFragment.getInstance("restaurantes");
        Utils.loadFragment(this, R.id.flContainer, fragment,true);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.ic_icono_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Nearby Places");
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_menu);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void setSupportActrionBarTitle(final String title) {
        getSupportActionBar().setTitle(title);
    }

}
