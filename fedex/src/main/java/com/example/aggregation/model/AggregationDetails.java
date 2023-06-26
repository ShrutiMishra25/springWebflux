
package com.example.aggregation.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Map;

@Data
@JsonAutoDetect
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AggregationDetails {
    private Map<String, Double>  pricing;
    private Map<String, String> track;
    private Map<String, ArrayList> shipments;
}

