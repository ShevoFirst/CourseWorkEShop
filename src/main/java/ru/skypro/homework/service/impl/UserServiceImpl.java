package ru.skypro.homework.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.NewPasswordDTO;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.mappers.UserMapper;
import ru.skypro.homework.store.entities.UserEntity;

@AllArgsConstructor
@Service
public class UserServiceImpl {
    private final UserMapper userMapper;


    public UpdateUserDTO updateUser(UserEntity userEntity) {
        if (userEntity != null) {
            userMapper.userEntityToUpdateuserDto(userEntity);
            return userMapper.userEntityToUpdateuserDto(userEntity);
        } else {
            throw new RuntimeException();
        }
    }

    public UserDTO getUser(UserEntity userEntity) {
        if (userEntity != null) {
            userMapper.userEntityToUserDto(userEntity);
            return userMapper.userEntityToUserDto(userEntity);
        } else {
            throw new RuntimeException();
        }
    }

//    public NewPasswordDTO updatePassword(UserEntity userEntity) {
//        if (userEntity != null) {
//            NewPasswordDTO newPasswordDTO = new NewPasswordDTO();
//            userMapper.userEntityToUpdateuserDto(userEntity);
//            return userMapper.userEntityToNewPasswordDto(userEntity);
//        } else {
//            throw new RuntimeException();
//        }
//    }
}
