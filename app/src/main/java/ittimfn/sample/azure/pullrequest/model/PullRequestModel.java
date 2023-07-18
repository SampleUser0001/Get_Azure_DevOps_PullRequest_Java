package ittimfn.sample.azure.pullrequest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown=true)
@Data
public class PullRequestModel {
    @JsonProperty("title")
    private String title;
}
