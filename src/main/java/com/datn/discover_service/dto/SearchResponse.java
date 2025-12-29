package com.datn.discover_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SearchResponse {
    private String keyword;
    private int page;
    private int size;

    private List<TripSearchDTO> trips;
    private List<UserSearchDTO> users;
}
