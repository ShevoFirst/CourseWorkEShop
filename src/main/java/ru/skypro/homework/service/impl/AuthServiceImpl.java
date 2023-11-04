package ru.skypro.homework.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.config.SecurityUserDetailsService;
import ru.skypro.homework.exeptions.BadRequestException;
import ru.skypro.homework.mappers.UserMapper;
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.service.repositories.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {

    private final SecurityUserDetailsService securityUserDetailsService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;
    // Класс AuthServiceImpl реализует интерфейс AuthService и отвечает за аутентификацию и регистрацию пользователей

    // В конструкторе класса AuthServiceImpl происходит инициализация необходимых зависимостей
    public AuthServiceImpl(SecurityUserDetailsService securityUserDetailsService, UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.securityUserDetailsService = securityUserDetailsService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // Метод login осуществляет аутентификацию пользователя по его имени пользователя и паролю
    @Override
    public boolean login(String userName, String password) {
        // Получаем данные пользователя по его имени пользователя из сервиса SecurityUserDetailsService
        UserDetails userDetails = securityUserDetailsService.loadUserByUsername(userName);
        // Сравниваем пароль, введенный пользователем, с хэшем пароля, хранящимся в UserDetails
        return passwordEncoder.matches(password, userDetails.getPassword());
    }

    // Метод register осуществляет регистрацию нового пользователя
    @Override
    public boolean register(Register register) {
        // Проверяем, существует ли пользователь с таким же именем пользователя в базе данных
        if (userRepository.findByEmail(register.getUsername()) != null) {
            throw new BadRequestException(String.format("Пользователь с ником \"%s\" cуществует",
                    register.getUsername())
            );
        }
        // Преобразуем данные нового пользователя из объекта Register в сущность UserEntity с помощью маппера
        var userEntity = userMapper.toUserEntity(register);
        // Хэшируем пароль нового пользователя с помощью объекта PasswordEncoder
        userEntity.setPassword(passwordEncoder.encode(register.getPassword()));
        // Сохраняем сущность пользователя в базе данных и возвращаем true
        userRepository.saveAndFlush(userEntity);
        return true;
    }

}
