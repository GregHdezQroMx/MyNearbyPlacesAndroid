package com.jght.nearbyplaces.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jght.nearbyplaces.R;
import com.jght.nearbyplaces.Repository.models.google_places.NearbyPlaces;
import com.jght.nearbyplaces.Repository.models.google_places.Result;
import com.jght.nearbyplaces.utils.Utils;

import java.util.List;

public class NearbyPlacesAdapter extends RecyclerView.Adapter<NearbyPlacesAdapter.NearbyPlacesViewHolder> {
    private NearbyPlaces nearbyPlaces;
    private int lastCheckedPosition = -1;
    private NearbyPlacesClickListener nearbyPlacesClickListener;
    private Context context;


    public NearbyPlacesAdapter (NearbyPlaces nearbyPlaces, NearbyPlacesClickListener nearbyPlacesClickListener) {
        this.nearbyPlaces = nearbyPlaces;
        this.nearbyPlacesClickListener = nearbyPlacesClickListener;
    }

    public List<Result> getNearbyPlacesList() {
        return nearbyPlaces.getResults();
    }

    @NonNull
    @Override
    public NearbyPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View vista = LayoutInflater.from(context).inflate(R.layout.nearby_places_list_item, parent, false);

        return new NearbyPlacesViewHolder(vista, this.nearbyPlacesClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyPlacesViewHolder holder, int position) {
        final Result place = getNearbyPlacesList().get(position);

        String refPrice = "";
        String open = "";
        float rating=0f;

        if (place.getPriceLevel()!=null) {
            refPrice += place.getPriceLevel().toString();
        }
        if (place.getUserRatingsTotal()!=null) {
            refPrice += "-" + place.getUserRatingsTotal().toString();
        }
        if (place.getOpeningHours()!=null && place.getOpeningHours().getOpenNow() != null) {
            open = place.getOpeningHours().getOpenNow()?"Abierto":"Cerrado";
        }
        if (place.getRating()!=null ) {
            rating =place.getRating().floatValue();
        }

        holder.txtTitle.setText(place.getName());

        //holder.txtRefPriceVincity.setText(refPrice);
        holder.txtDescription.setText(place.getVicinity());
        holder.txtOpen.setText(open);
        //holder.txtOpenHours.setText("TODO get hours");
        holder.rbPlaceRating.setRating(rating);


        //CARGAR LA IMAGEN
        if (place.getPhotos() != null) {
            if (place.getPhotos().get(0).getbPhoto() != null) {
                holder.ivPlacePic.setImageBitmap(Utils.byteArray2Bitmap(place.getPhotos().get(0).getbPhoto()));
            }

        }

        holder.cvContainer.setTag(String.valueOf(position));
        holder.cvContainer.setId(position);

    }

    @Override
    public int getItemCount() {
        return getNearbyPlacesList().size();
    }

    public void selectItem(final int position) {
        lastCheckedPosition = position;
    }


    class NearbyPlacesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView txtTitle;
        //private final TextView txtRefPriceVincity;
        private final TextView txtDescription;
        private final TextView txtOpen;
        //private final TextView txtOpenHours;
        private final ImageView ivPlacePic;
        private final RatingBar rbPlaceRating;
        private final CardView cvContainer;
        private final NearbyPlacesClickListener nearbyPlacesClickListener;

        public NearbyPlacesViewHolder(@NonNull View itemView, NearbyPlacesClickListener nearbyPlacesClickListener) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.tvPlaceTitle);
            //txtRefPriceVincity = itemView.findViewById(R.id.tvReferencePriceVincity);
            txtDescription = itemView.findViewById(R.id.tvPlaceDescription);
            txtOpen = itemView.findViewById(R.id.tvPlaceOpen);
            //txtOpenHours = itemView.findViewById(R.id.tvOpeningHours);
            ivPlacePic = itemView.findViewById(R.id.ivPlacePicture);
            rbPlaceRating = itemView.findViewById(R.id.rbPlaceRating);
            cvContainer =  itemView.findViewById(R.id.cvContainer);


            this.nearbyPlacesClickListener = nearbyPlacesClickListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            this.nearbyPlacesClickListener.onClick(view, getAdapterPosition());
        }
    }

    interface NearbyPlacesClickListener {
        void onClick(final View vista, final int position);
    }
}
