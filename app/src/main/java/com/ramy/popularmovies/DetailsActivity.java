package com.ramy.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    final String POSTER = "poster_path";
    final String SYNOPSIS = "overview";
    final String TITLE = "original_title";
    final String RATING = "vote_average";
    final String RELEASE_DATE = "release_date";
    final String ID = "id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        if (intent != null) {

            if (intent.hasExtra(POSTER)) {
                Log.v("Setting details poster", intent.getStringExtra(POSTER));
                bundle.putString(POSTER,intent.getStringExtra(POSTER));
            }
            if (intent.hasExtra(RATING)) {
                bundle.putString(RATING,intent.getStringExtra(RATING));
            }
            if (intent.hasExtra(SYNOPSIS)) {
                bundle.putString(SYNOPSIS,intent.getStringExtra(SYNOPSIS));
            }
            if (intent.hasExtra(TITLE)) {
                ((TextView) findViewById(R.id.detailsTitle)).setText(intent.getStringExtra(TITLE));
            }
            if (intent.hasExtra(RELEASE_DATE)) {
                bundle.putString(RELEASE_DATE,intent.getStringExtra(RELEASE_DATE));
            }
            if (intent.hasExtra(ID)) {
                bundle.putString(ID,intent.getStringExtra(ID));
            }

        }

        DetailsFragment detailsFragment=new DetailsFragment();
        detailsFragment.setArguments(bundle);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.details_fragment, detailsFragment)
                    .commit();
            Log.v(DetailsActivity.class.getName(),"Inflated Details Fragment");
        }



    }
}
