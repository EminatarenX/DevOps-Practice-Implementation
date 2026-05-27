package com.emicorporg.users_ms.dto;

import com.emicorporg.users_ms.entities.UserEntity;

public record UserDto(Long id, String name, String email) {
    public UserEntity toEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setName(this.name);
        userEntity.setEmail(this.email);
        return userEntity;
    }
    public static UserDto fromEntity(UserEntity userEntity) {
        return new UserDto(userEntity.getId(), userEntity.getName(), userEntity.getEmail());
    }
}
