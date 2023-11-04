package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.config.SecurityUserDetailsService;
import ru.skypro.homework.dto.NewPasswordDTO;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.exeptions.ForbiddenException;
import ru.skypro.homework.exeptions.UnauthorizedException;
import ru.skypro.homework.mappers.UserMapper;
import ru.skypro.homework.service.UserService;
import ru.skypro.homework.service.entities.ImageEntity;
import ru.skypro.homework.service.entities.UserEntity;
import ru.skypro.homework.service.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityUserDetailsService securityUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final ImageServiceImpl imageService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           SecurityUserDetailsService securityUserDetailsService,
                           PasswordEncoder passwordEncoder, ImageServiceImpl imageService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.securityUserDetailsService = securityUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.imageService = imageService;
    }
    // Метод updateUser обновляет данные пользователя
    // В параметре передается объект UpdateUserDTO с новыми данными пользователя
    // Метод получает текущего пользователя, обновляет его данные с помощью маппера,
    // сохраняет изменения в БД и возвращает объект UpdateUserDTO с обновленными данными.
    @Override
    public UpdateUserDTO updateUser(UpdateUserDTO updateUserDTO) {
        UserEntity userEntity = getCurrentUser();
        UserEntity updateUserEntity = userMapper.userEntityUpdate(updateUserDTO, userEntity);
        UpdateUserDTO updateUser = userMapper.toUpdateUserDto(userEntity);
        userRepository.saveAndFlush(updateUserEntity);

        return updateUser;
    }
    // Метод getUser возвращает информацию о текущем пользователе
    // Метод получает текущего пользователя, преобразует его в объект UserDTO с помощью маппера
    // и возвращает этот объект.
    @Override
    public UserDTO getUser() {
        UserEntity user = getCurrentUser();
        return userMapper.toUserDto(user);
    }
    // Метод updatePassword обновляет пароль пользователя
    // В параметре передается объект NewPasswordDTO с новым паролем и текущим паролем пользователя.
    // Метод проверяет длину нового пароля, получает текущего пользователя, загружает его данные из БД,
    // проверяет совпадение текущего пароля с паролем из БД, кодирует новый пароль и сохраняет изменения в БД.
    @Override
    public void updatePassword(NewPasswordDTO newPasswordDTO) {
        String newPassword = newPasswordDTO.getNewPassword();

        if (newPassword.length() < 8) {
            throw new ForbiddenException("Новый пароль меньше 8 символов");
        }

        UserEntity user = getCurrentUser();
        UserDetails userDetails = securityUserDetailsService.loadUserByUsername(user.getEmail());

        if (!passwordEncoder.matches(newPasswordDTO.getCurrentPassword(), userDetails.getPassword())) {
            throw new UnauthorizedException("Пароли не совпадают");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        userRepository.saveAndFlush(user);
    }
    // Метод updateAvatar обновляет аватар пользователя
    // В параметре передается файл изображения.
    // Метод получает текущего пользователя, загружает изображение с помощью сервиса ImageService,
    // сохраняет информацию об изображении в БД и обновляет аватар пользователя в БД.
    @Override
    public void updateAvatar(MultipartFile image) {
        // Получаем текущего пользователя
        UserEntity user = getCurrentUser();
        // Загружаем изображение с помощью сервиса ImageService
        ImageEntity imageEntity = imageService.downloadImage(image);
        user.setImageEntity(imageEntity);
        // Сохраняем информацию об изображении в БД
        userRepository.saveAndFlush(user);
    }

    /**
     * Приватный метод getCurrentUser получает текущего авторизованного пользователя.
     */
    // Метод получает имя пользователя из контекста безопасности, находит пользователя в БД по email
    // и возвращает объект UserEntity.
    private UserEntity getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(userName);
    }
}

