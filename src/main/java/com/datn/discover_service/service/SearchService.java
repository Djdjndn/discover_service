package com.datn.discover_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.datn.discover_service.dto.SearchResponse;
import com.datn.discover_service.dto.TripSearchDTO;
import com.datn.discover_service.dto.UserSearchDTO;
import com.datn.discover_service.model.Trip;
import com.datn.discover_service.model.User;
import com.datn.discover_service.repository.TripRepository;
import com.datn.discover_service.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final TripRepository tripRepository;
    private final UsersRepository usersRepository;

    public SearchResponse search(String keyword, int page, int size) {

        String kw = normalize(keyword);
        if (kw.isBlank()) {
            return emptyResponse(keyword, page, size);
        }

        // 1. Lấy data gốc
        List<Trip> trips = tripRepository.searchPublicTrips(kw);
        List<User> users = usersRepository.searchUsers(kw);

        // 2. Paginate
        List<Trip> pagedTrips = paginate(trips, page, size);
        List<User> pagedUsers = paginate(users, page, size);

        // 3. Map DTO
        return SearchResponse.builder()
                .keyword(keyword)
                .page(page)
                .size(size)
                .trips(
                        pagedTrips.stream()
                                .map(this::toTripDTO)
                                .toList()
                )
                .users(
                        pagedUsers.stream()
                                .map(this::toUserDTO)
                                .toList()
                )
                .build();
    }

    private TripSearchDTO toTripDTO(Trip t) {
        return TripSearchDTO.builder()
                .id(t.getId())
                .title(t.getTitle())
                .content(t.getContent())
                .coverPhoto(t.getCoverPhoto())
                .tags(t.getTags())
                .userId(t.getUserId())
                .sharedAt(t.getSharedAt())
                .likeCount(t.getLikeCount())
                .build();
    }

    private UserSearchDTO toUserDTO(User u) {
        return UserSearchDTO.builder()
                .id(u.getId())
                .fullName((u.getFirstName() + " " + u.getLastName()).trim())
                .profilePicture(u.getProfilePicture())
                .build();
    }

    private String normalize(String keyword) {
        return keyword == null ? "" : keyword.trim().toLowerCase();
    }

    private SearchResponse emptyResponse(String keyword, int page, int size) {
        return SearchResponse.builder()
                .keyword(keyword)
                .page(page)
                .size(size)
                .trips(List.of())
                .users(List.of())
                .build();
    }

    private <T> List<T> paginate(List<T> list, int page, int size) {
        if (list == null || list.isEmpty()) return List.of();
        int from = page * size;
        if (from >= list.size()) return List.of();
        return list.subList(from, Math.min(from + size, list.size()));
    }
}
