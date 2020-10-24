package com.example.agua_planeta_rica.util;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PointsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    TaskLoadedCallback taskCallback;
    String directionMode;

    public PointsParser(TaskLoadedCallback taskCallback, String directionMode) {
        this.taskCallback = taskCallback;
        this.directionMode = directionMode;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            //Log.d("mylog", jsonData[0].toString());
            DataParser parser = new DataParser();
            //Log.d("mylog", parser.toString());
            // Starts parsing data
            routes = parser.parse(jObject);
            //Log.d("mylog", "Executing routes");
            //Log.d("mylog", routes.toString());
        } catch (Exception e) {
            Log.d("mylog", e.toString());
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();
            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);
            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            if (directionMode.equalsIgnoreCase("walking")) {
                lineOptions.width(9);
                lineOptions.color(Color.MAGENTA);
            } else {
                lineOptions.width(9);
                lineOptions.color(Color.rgb(13, 209, 91));
            }
            Log.d("mylog", "onPostExecute lineoptions decoded");
        }

        if (lineOptions != null) {
            taskCallback.onTaskDone(lineOptions);
        } else {
            Log.d("mylog", "without Polylines drawn");
        }
    }
}
