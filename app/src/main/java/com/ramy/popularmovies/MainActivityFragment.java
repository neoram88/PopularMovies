package com.ramy.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment{

    public MainActivityFragment() {
    }

    List<String> posterURLS=new ArrayList<String>();
    List<String> synopsis=new ArrayList<String>();
    List<String> title=new ArrayList<String>();
    List<String> rating=new ArrayList<String>();
    List<String> releaseDate=new ArrayList<String>();
    final String POSTER = "poster_path";
    final String SYNOPSIS = "overview";
    final String TITLE = "original_title";
    final String RATING = "vote_average";
    final String RELEASE_DATE = "release_date";
    SharedPreferences sharedPref ;
    String sortOrder;
    GridAdapter gridadapter;

    @Override
    public void onStart() {
        super.onStart();
        updateMovieData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Log.v(MainActivityFragment.class.getName(), ": Inside Fragment");
        DisplayMetrics displayMetrics=getResources().getDisplayMetrics();


        int screen_width=displayMetrics.widthPixels;    //width of the device screen
        int screen_height=displayMetrics.heightPixels;   //height of device screen

        final int view_width=screen_width/ROW_ITEMS;   //width for imageview
        final int view_height=screen_height/2;   //height for imageview


        final GridView grid = (GridView) rootView.findViewById(R.id.gridview);
        gridadapter=new GridAdapter(getContext(), view_width, view_height, posterURLS);
        grid.setAdapter(gridadapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent detailsIntent = new Intent(getActivity(), DetailsActivity.class);
                detailsIntent.putExtra(POSTER,posterURLS.get(position));
                detailsIntent.putExtra(RATING,rating.get(position));
                detailsIntent.putExtra(TITLE,title.get(position));
                detailsIntent.putExtra(RELEASE_DATE,releaseDate.get(position));
                detailsIntent.putExtra(SYNOPSIS,synopsis.get(position));
                startActivity(detailsIntent);
            }
        });
        return rootView;
    }

    public void updateMovieData(){
        FetchMovieData fetchMovie =new FetchMovieData(new AsyncResponse() {

            @Override
            public void processFinish(JSONArray output) throws JSONException{
                posterURLS.clear();
                synopsis.clear();
                title.clear();
                rating.clear();
                releaseDate.clear();

                for(int i = 0; i < output.length(); i++) {
                    JSONObject movieDetails = output.getJSONObject(i);
                    posterURLS.add(movieDetails.getString(POSTER));
                    synopsis.add(movieDetails.getString(SYNOPSIS));
                    title.add(movieDetails.getString(TITLE));
                    rating.add(movieDetails.getString(RATING));
                    releaseDate.add(movieDetails.getString(RELEASE_DATE));
                }
                if(gridadapter!=null) {
                    gridadapter.updateList(posterURLS);
                }
            }
        });
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortOrder=sharedPref.getString(getString(R.string.pref_movies_sort_order_key), getString(R.string.pref_default_sort_by));
        fetchMovie.execute(sortOrder, "desc");
    }

    private static final int ROW_ITEMS = 2;

    private static final class GridAdapter extends BaseAdapter {

        private Context mContext;
        private int mWidth;
        private int mHeight;
        private List<String> mPosters;


        public GridAdapter(Context c,int width,int height,List<String> items) {
            mContext = c;
            mWidth=width;
            mHeight=height;
            mPosters=items;

        }

        public void updateList(List<String > list) {
            mPosters = list;
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return mPosters.size();
        }

        @Override
        public Object getItem(final int position) {
            return null;
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(mWidth, mHeight));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }
            Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/"+mPosters.get(position)).into(imageView);
            return imageView;
        }


    }
}

