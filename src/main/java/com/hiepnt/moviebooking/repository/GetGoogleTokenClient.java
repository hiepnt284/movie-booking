package com.hiepnt.moviebooking.repository;

import com.hiepnt.moviebooking.dto.request.ExchangeTokenRequest;
import com.hiepnt.moviebooking.dto.response.ExchangeTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "getGoogleTokenClient", url = "https://oauth2.googleapis.com")
public interface GetGoogleTokenClient {
    @PostMapping(value = "/token", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ExchangeTokenResponse exchangeToken(@RequestBody ExchangeTokenRequest request);
}
