package com.emicorporg.users_ms.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emicorporg.users_ms.dto.UserDto;
import com.emicorporg.users_ms.entities.UserEntity;
import com.emicorporg.users_ms.repositories.UserRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;



@RestController
@RequestMapping("/users")
public class UsersController {

    private final UserRepository usersRepository;

    public UsersController(UserRepository usersRepository){
        this.usersRepository = usersRepository;
    }
   
    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserEntity> userEntities = usersRepository.findAll();
        List<UserDto> userDtos = userEntities.stream()
            .map(UserDto::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @PostMapping()
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        UserEntity existingUser = usersRepository.findByEmail(userDto.email());
        if (existingUser != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists");
        }
        UserEntity userEntity = userDto.toEntity();
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDto.fromEntity(usersRepository.save(userEntity)));
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        UserEntity userEntity = usersRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return ResponseEntity.ok(UserDto.fromEntity(userEntity));
    }
    
   
    
    
}
