package com.example.anew;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment {



    // TODO: Rename and change types of parameters


    private GoogleMap mMap;
    private EditText mSearchText;
    private Button mSearchButton;

    private Marker mMarker;

    public MapFragment() {

        // Required empty public constructor
    }
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mSearchText = view.findViewById(R.id.search_text);
        mSearchButton = view.findViewById(R.id.search_button);
        mSearchText.setText(((MainActivity)getActivity()).SearchAddress);

        mSearchButton = view.findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).SearchAddress = mSearchText.getText().toString();
                String searchText = mSearchText.getText().toString();
                new GeocodeTask().execute(searchText);
            }
        });

        mSearchButton.performClick();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.getUiSettings().setZoomControlsEnabled(true);

            }
        });

        return view;
    }
    private class GeocodeTask extends AsyncTask<String, Void, List<Address>> {
        @Override
        protected List<Address> doInBackground(String... params) {
            Geocoder geocoder = new Geocoder(getActivity());
            String searchText = params[0];
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocationName(searchText, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }
        @Override
        protected void onPostExecute(List<Address> addresses) {

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                if (mMarker != null) {
                    mMarker.remove();
                }
                mMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        // Show the dialog to save the location here
                        CustomDialog customDialog = new CustomDialog(getContext());



                        customDialog.setPositiveButton( new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText name= customDialog.findViewById(R.id.edittext_name);
                                EditText address = customDialog.findViewById(R.id.edittext_address);
                                Spinner typeLocation = customDialog.findViewById(R.id.spinner_group);
                                Location location = new Location(name.getText().toString(),address.getText().toString(),typeLocation.getSelectedItem().toString());
                                ArrayList<Location> locationsList = new ArrayList<Location>();
                                // Save the location here
                                // You can get the location from the Marker object
                                LatLng latLng = marker.getPosition();

                                Toast.makeText(getActivity(), "Đã lưu địa điểm", Toast.LENGTH_SHORT).show();
                                File directory = getActivity().getFilesDir();
                                File file = new File(directory, "Location.json");

                                if (file.exists()) {
                                    try{
                                        FileReader fileReader = new FileReader(file);
                                        Type type = new TypeToken<ArrayList<Location>>(){}.getType();
                                        Gson gson = new Gson();
                                        locationsList = gson.fromJson(fileReader, type);
                                        fileReader.close();
                                        locationsList.add(location);
                                        try {
                                            FileWriter fileWriter = new FileWriter(file);
                                            gson.toJson(locationsList, fileWriter);
                                            fileWriter.close();

                                        } catch (IOException e) {
                                            System.err.println("Loi viet file");
                                        }

                                    } catch (FileNotFoundException e) {
                                        System.err.println("Loi fileReader");
                                    } catch (IOException e) {
                                        System.err.println("Loi close fileReader");
                                    }
                                }else {
                                    locationsList = new ArrayList<>();
                                    locationsList.add(location);

                                    try {
                                        FileWriter fileWriter = new FileWriter(file);
                                        Gson gson = new Gson();
                                        gson.toJson(locationsList, fileWriter);
                                        fileWriter.close();
                                    } catch (IOException e) {
                                        System.err.println("Loi viet file");
                                    }
                                }
                            }
                        });
                        customDialog.show();

                        EditText address = customDialog.findViewById(R.id.edittext_address);
                        address.setText(((MainActivity)getActivity()).SearchAddress);
                        return true;
                    }
                });

            } else {
                Toast.makeText(getActivity(), "Address not found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


