
package com.jght.nearbyplaces.Repository.models.google_places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class NearbyPlaces {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;
    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @SerializedName("status")
    @Expose
    private String status;

    private static NearbyPlaces nearbyPlaces;

    public NearbyPlaces() {
    }

    private NearbyPlaces(List<Object> htmlAttributions, List<Result> results, String status) {
        this.htmlAttributions = htmlAttributions;
        this.results = results;
        this.status = status;
    }

    public static NearbyPlaces getInstance(List<Object> htmlAttributions, List<Result> results, String status) {
        if (nearbyPlaces == null) {
            nearbyPlaces = new NearbyPlaces(htmlAttributions, results, status);
        }

        return nearbyPlaces;
    }

    public static NearbyPlaces getInstance() {
        if (nearbyPlaces == null) {
            nearbyPlaces = null;
        }

        return nearbyPlaces;
    }

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
