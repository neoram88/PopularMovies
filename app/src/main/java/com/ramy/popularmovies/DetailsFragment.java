package com.ramy.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DetailsFragment extends Fragment {

    final String POSTER = "poster_path";
    final String SYNOPSIS = "overview";
    final String RATING = "vote_average";
    final String RELEASE_DATE = "release_date";
    final String ID = "id";
    final String TRAILER="videos";
    final String REVIEWS="reviews";
    final String CONTENT="content";
    final String AUTHOR="author";
    boolean isFav;

    final String LOG_TAG = DetailsFragment.class.getName();

    SharedPreferences sharedPref ;

    List<String> reviewContents=new ArrayList<String>();
    List<String> authorsList=new ArrayList<String>();

    List<String > trailerRef=new ArrayList<String>();

    String movieID;
    List<String> movieIDsList=new ArrayList<String>();

    public String allReviews="";


    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        isFav=false;
        Log.v(DetailsFragment.class.getName(), ": Inside Fragment");
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        ImageView poster = (ImageView) rootView.findViewById(R.id.detailsPoster);
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + getArguments().getString(POSTER)).into((poster));

        ((TextView) rootView.findViewById(R.id.detailsRating)).setText(getArguments().getString(RATING));

        ((TextView) rootView.findViewById(R.id.detailsSynopsis)).setText(getArguments().getString(SYNOPSIS));

        ((TextView) rootView.findViewById(R.id.detailsReleaseYear)).setText(getArguments().getString(RELEASE_DATE));

        movieID=getArguments().getString(ID);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());


        final CheckBox favCheckBox=((CheckBox) rootView.findViewById(R.id.favourite_check_box));
        Log.v(LOG_TAG,"The key value to look for in Share Preference: "+getString(R.string.fav_movies));
        String favMovieIds=sharedPref.getString(getString(R.string.fav_movies), "");
        Log.v(LOG_TAG,"Fav Movie IDS"+favMovieIds);
        if(!favMovieIds.isEmpty()) {
            String[] movieIDs = favMovieIds.split(",");
            for (String id : movieIDs) {
                movieIDsList.add(id);
                if(id.equals(movieID)){
                    isFav=true;
                    break;
                }else{
                    isFav=false;
                }
            }
        }
        if(isFav==true){
            favCheckBox.setChecked(true);
        }


        favCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(!isFav && favCheckBox.isChecked()){
                addToFavourites();
            }else{
                removeFromFavourites();
            }
            }
        });

        getTrailerandReviews(movieID, TRAILER);
        getTrailerandReviews(movieID, REVIEWS);


        return rootView;
    }

     public void getTrailerandReviews(String movieID, final String detailType){

        FetchMovieDetails fetchdetails =new FetchMovieDetails(new AsyncResponse() {
            @Override
            public void processFinish(JSONArray output) throws JSONException {
            if(detailType.equals(TRAILER))
            {
                for(int i = 0; i < output.length(); i++) {
                    JSONObject trailerDetails = output.getJSONObject(i);
                    trailerRef.add(trailerDetails.getString("key"));
                    Log.v("Trailer KEY ", trailerRef.get(i));
                }
                for(int i=0;i<trailerRef.size();i++){

                    Button trailerButton = new Button(getActivity());
                    trailerButton.setText("Trailer " + (i + 1));
                    final String refKey=trailerRef.get(i);
                    LinearLayout ll = (LinearLayout) getActivity().findViewById(R.id.trailers_layout);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    ll.addView(trailerButton, lp);
                    trailerButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Log.v(DetailsFragment.class.getName(), "Opening Youtube URL: " + "http://www.youtube.com/watch?v=" + refKey);
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + refKey)));
                        }
                    });

                }

            }else if(detailType.equals(REVIEWS)){

                reviewContents.clear();
                authorsList.clear();


                for(int i = 0; i < output.length(); i++) {
                    JSONObject movieDetails = output.getJSONObject(i);
                    reviewContents.add(movieDetails.getString(CONTENT));
                    Log.v(DetailsFragment.class.getName(), REVIEWS + movieDetails.getString(CONTENT));
                    if(allReviews.isEmpty()) {
                        allReviews = movieDetails.getString(CONTENT) + "\nReview By: " + movieDetails.getString(AUTHOR);
                    }else{
                        allReviews=allReviews +"\n \n"+movieDetails.getString(CONTENT) + "\nReview By: " + movieDetails.getString(AUTHOR);
                    }
                }
                ((TextView) getActivity().findViewById(R.id.review_text)).setText(allReviews);
            }
            }
        });
        fetchdetails.execute(movieID,detailType);

    }

    public void addToFavourites(){
        movieIDsList.add(movieID);
        StringBuilder sb = new StringBuilder();
        for (String s : movieIDsList)
        {
            sb.append(s);
            sb.append(",");
        }
        sharedPref.edit().putString(getString(R.string.fav_movies), sb.toString()).commit();
        Log.v("Adding to Fav",sb.toString());
    }

    public void removeFromFavourites(){
        movieIDsList.remove(movieIDsList.indexOf(movieID));
        StringBuilder sb = new StringBuilder();
        for (String s : movieIDsList)
        {
            sb.append(s);
            sb.append(",");
        }
        Log.v("After UnFavourite ",sb.toString());
        sharedPref.edit().putString(getString(R.string.fav_movies), sb.toString()).commit();
    }

}
