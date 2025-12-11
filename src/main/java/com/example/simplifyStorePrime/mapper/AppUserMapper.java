package com.example.simplifyStorePrime.mapper;

import com.example.simplifyStorePrime.dto.AppUserDTO;
import com.example.simplifyStorePrime.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppUserMapper {
    AppUserDTO toDTO(AppUser user);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    AppUser toEntity(AppUserDTO dto);
}
