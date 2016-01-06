package com.ramy.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by RamY on 31/12/15.
 */
public interface AsyncResponse {
    void processFinish(JSONArray output) throws JSONException;
}
