package com.hiepnt.moviebooking.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExchangeGoogleUserResponse {

    @JsonProperty("resourceName")
    String resourceName;

    @JsonProperty("names")
    List<Name> names;

    @JsonProperty("emailAddresses")
    List<EmailAddress> emailAddresses;

    @JsonProperty("phoneNumbers")
    List<PhoneNumber> phoneNumbers;

    @JsonProperty("genders")
    List<Gender> genders;

    @JsonProperty("birthdays")
    List<Birthday> birthdays;

    @JsonProperty("photos")
    List<Photo> photos;

    // Các lớp lồng nhau
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Name {
        @JsonProperty("displayName")
        String displayName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class EmailAddress {
        @JsonProperty("value")
        String value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PhoneNumber {
        @JsonProperty("value")
        String value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Gender {
        @JsonProperty("value")
        String value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Birthday {
        @JsonProperty("date")
        Date date;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Photo {
        @JsonProperty("url")
        String url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Date {
        int year;
        int month;
        int day;
    }
}
