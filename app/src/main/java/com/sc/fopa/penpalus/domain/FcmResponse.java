package com.sc.fopa.penpalus.domain;

import java.util.List;

import lombok.Data;

/**
 * Created by fopa on 2017-12-14.
 */
@Data
public class FcmResponse {
    String multicast_id;
    int success;
    int failure;
    int canonical_ids;
    List<Results> results;
}
