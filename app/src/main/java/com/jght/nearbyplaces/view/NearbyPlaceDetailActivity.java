package com.jght.nearbyplaces.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.jght.nearbyplaces.R;
import com.jght.nearbyplaces.Repository.models.google_places.NearbyPlaces;
import com.jght.nearbyplaces.Repository.models.google_places.Result;
import com.jght.nearbyplaces.databinding.ActivityNearbyPlaceDetailBinding;
import com.jght.nearbyplaces.utils.Utils;

public class NearbyPlaceDetailActivity extends AppCompatActivity {
    private ActivityNearbyPlaceDetailBinding binding;
    private Result place;
    private static final String ARG_PLACE = "PLACE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_place_detail);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nearby_place_detail);

        int position = getIntent().getExtras().getInt("POSITION");
        place = NearbyPlaces.getInstance().getResults().get(position);

        initUI();
    }

    private void initUI(){
        String refPrice = "";
        String open = "";

        // SET DETAILS INFO
        if (place.getPriceLevel()!=null) {
            refPrice += place.getPriceLevel().toString();
        }
        if (place.getUserRatingsTotal()!=null) {
            refPrice += "-" + place.getUserRatingsTotal().toString();
        }
        if (place.getOpeningHours()!=null && place.getOpeningHours().getOpenNow() != null) {
            open = place.getOpeningHours().getOpenNow()?"Abierto":"Cerrado";
        }

        if (place.getPhotos().get(0).getbPhoto() != null) {
            binding.ivPlacePicture.setImageBitmap(Utils.byteArray2Bitmap(place.getPhotos().get(0).getbPhoto()));
        }

        binding.tvPlaceTitle.setText(place.getName());

        binding.tvReferencePriceVincity.setText(refPrice);
        binding.tvPlaceDescription.setText(place.getVicinity());
        binding.tvPlaceOpen.setText(open);
        binding.rbPlaceRating.setRating(place.getRating().floatValue());
    }
}
