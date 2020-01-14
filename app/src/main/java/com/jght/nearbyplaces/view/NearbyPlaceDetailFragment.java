package com.jght.nearbyplaces.view;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jght.nearbyplaces.Repository.models.google_places.Result;
import com.jght.nearbyplaces.databinding.FragmentNearbyPlaceDetailBinding;
import com.jght.nearbyplaces.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NearbyPlaceDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NearbyPlaceDetailFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyPlaceDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PLACE = "PLACE";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Result place;
    private String mParam2;

    private FragmentNearbyPlaceDetailBinding binding;

    private OnFragmentInteractionListener mListener;

    public NearbyPlaceDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param place Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NearbyPlaceDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NearbyPlaceDetailFragment getInstance(Result place, String param2) {
        NearbyPlaceDetailFragment fragment = new NearbyPlaceDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLACE, place);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            place = (Result) getArguments().getSerializable(ARG_PLACE);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNearbyPlaceDetailBinding.inflate(inflater, container, false);

        initUI();

        return binding.cvContainer.getRootView();
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
        //binding.tvOpeningHours.setText("TODO get hours");
        binding.rbPlaceRating.setRating(place.getRating().floatValue());
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
