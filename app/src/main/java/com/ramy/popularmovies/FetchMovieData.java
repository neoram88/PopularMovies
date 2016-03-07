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
 * Created by RamY on 28/12/15.
 */
public class FetchMovieData extends AsyncTask<String,Void,JSONArray> {

        private final String LOG_TAG=FetchMovieData.class.getSimpleName();

        public AsyncResponse delegate = null;//Call back interface

        public FetchMovieData(AsyncResponse asyncResponse) {
            delegate = asyncResponse;//Assigning call back interface through constructor
        }

        protected JSONArray doInBackground(String... params) {
            //param 1 will be sort type and param 2 will hold ascend or descend
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            //INSERT API KEY BELOW

            String appid=BuildConfig.TMDB_API_KEY;

            try {
                // Construct the URL for the moviedb query
                final String BASE_URL="http://api.themoviedb.org/3/discover/movie?";
                final String QUERY_PRAM="sort_by";


                Uri builtUri=Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PRAM,params[0]+"."+params[1])
                        .appendQueryParameter("api_key",appid)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG,"URL: "+builtUri.toString());
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
                    moviesJsonStr=null;
                }
                moviesJsonStr=buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                moviesJsonStr=null;
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
            try {
                resultsArray=getResultsFromJson(moviesJsonStr);
            }catch (Exception e){
                e.printStackTrace();
            }
            return resultsArray;
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

    private JSONArray getResultsFromJson(String moviesJsonStr) throws JSONException{


        final String RESULTS = "results";
        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviePostersArray = moviesJson.getJSONArray(RESULTS);
        return moviePostersArray;
    }


}
