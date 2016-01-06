package com.ramy.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    final String POSTER = "poster_path";
    final String SYNOPSIS = "overview";
    final String TITLE = "original_title";
    final String RATING = "vote_average";
    final String RELEASE_DATE = "release_date";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();

        if(intent!=null){
            if(intent.hasExtra(POSTER)){
                Log.v("Setting details poster", intent.getStringExtra(POSTER));
                ImageView poster=(ImageView) findViewById(R.id.detailsPoster);
                Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + intent.getStringExtra(POSTER)).into((poster));
            }
            if (intent.hasExtra(RATING)){
                ((TextView) findViewById(R.id.detailsRating)).setText(intent.getStringExtra(RATING));
            }
            if (intent.hasExtra(SYNOPSIS)){
                ((TextView) findViewById(R.id.detailsSynopsis)).setText(intent.getStringExtra(SYNOPSIS));
            }
            if (intent.hasExtra(TITLE)){
                TextView titleText = (TextView) findViewById(R.id.detailsTitle);
                titleText.setText(intent.getStringExtra(TITLE));
            }
            if (intent.hasExtra(RELEASE_DATE)){
                ((TextView) findViewById(R.id.detailsReleaseYear)).setText(intent.getStringExtra(RELEASE_DATE));
            }

        }

    }

}
