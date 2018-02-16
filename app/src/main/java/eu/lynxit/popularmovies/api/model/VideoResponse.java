package eu.lynxit.popularmovies.api.model;

import java.util.List;

/**
 * Created by lynx on 16/02/18.
 */

public class VideoResponse {
    private Integer id;
    private List<VideoDTO> results;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<VideoDTO> getResults() {
        return results;
    }

    public void setResults(List<VideoDTO> results) {
        this.results = results;
    }
}
