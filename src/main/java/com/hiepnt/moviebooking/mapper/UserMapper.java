package com.hiepnt.moviebooking.mapper;

import com.hiepnt.moviebooking.dto.request.UserCreationDto;
import com.hiepnt.moviebooking.dto.request.UserUpdateDto;
import com.hiepnt.moviebooking.dto.response.UserResponse;
import com.hiepnt.moviebooking.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    User toUser(UserCreationDto request);

    UserResponse toUserResponse(User user);

    void updateUser(@MappingTarget User user, UserUpdateDto req);
}
