package com.hiepnt.moviebooking.repository;

import com.hiepnt.moviebooking.dto.response.ExchangeGoogleUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "getGoogleUserClient", url = "https://people.googleapis.com/v1")
public interface GetGoogleUserClient {
    @GetMapping("/people/me")
    ExchangeGoogleUserResponse getUserInfo(@RequestHeader("Authorization") String authorization,
                                           @RequestParam("personFields") String personFields);
}
