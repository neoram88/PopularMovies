package com.ramy.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by RamY on 07/03/16.
 */
public class FetchMovieDetails extends AsyncTask<String,Void,JSONArray> {

    private final String LOG_TAG=FetchMovieDetails.class.getSimpleName();

    public AsyncResponse delegate = null;//Call back interface

    public FetchMovieDetails(AsyncResponse asyncResponse) {
        delegate = asyncResponse;//Assigning call back interface through constructor
    }

    @Override
    protected JSONArray doInBackground(String... params) {

        //param 1 will be sort type and param 2 will hold ascend or descend
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

// Will contain the raw JSON response as a string.
        String detailsJsonreply = null;


        String appid=BuildConfig.TMDB_API_KEY;
        boolean favMovieAPICall=false;
        try {
            // Construct the URL for the moviedb query
            final String BASE_URL = "http://api.themoviedb.org/3/movie/?";
            Uri builtUri;
            if (params.length==1) {
                 favMovieAPICall=true;
            }
            if(favMovieAPICall){   //only MovieID is passed. Fav movie API call
                 builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter("api_key", appid)
                        .build();
            }else {
                 builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendPath(params[1])
                        .appendQueryParameter("api_key", appid)
                        .build();
            }

            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "URL: " + builtUri.toString());
            // Create the request to moviedb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                detailsJsonreply=null;
            }
            detailsJsonreply=buffer.toString();

        }catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            detailsJsonreply=null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        JSONArray resultsArray=null;
        if(!favMovieAPICall){
            try {
                resultsArray=getResultsFromJson(detailsJsonreply);
            }catch (Exception e){
                e.printStackTrace();
            }
            return resultsArray;
        }else {
            try {
                resultsArray=new JSONArray("["+detailsJsonreply+"]");
            }catch (Exception e){
                e.printStackTrace();
            }
            return resultsArray;
        }

    }

    @Override
    protected void onPostExecute(JSONArray strings) {
        super.onPostExecute(strings);
        try {
            delegate.processFinish(strings);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private JSONArray getResultsFromJson(String detailsJsonreply) throws JSONException {


        final String RESULTS = "results";
        JSONObject moviesJson = new JSONObject(detailsJsonreply);
        JSONArray movieDetailsArray = moviesJson.getJSONArray(RESULTS);
        Log.v(LOG_TAG, "Movie Details RESULTS array"+ movieDetailsArray.toString());
        return movieDetailsArray;
    }


}
