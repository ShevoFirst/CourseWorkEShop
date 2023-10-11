package ru.skypro.homework.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skypro.homework.service.entities.UserEntity;

import java.util.Collection;

public class SecurityUserPrincipal implements UserDetails {
    private UserEntity user;

    // Конструктор класса SecurityUserPrincipal,
    // принимающий объект класса AuthUser.
    public SecurityUserPrincipal(UserEntity user) {
        this.user = user;
    }

    @Override
    // Возвращает авторитеты (роли) пользователя.
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    // Возвращает пароль пользователя.
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    // Возвращает имя пользователя.
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    // Возвращает true, если аккаунт пользователя не истек.
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    // Возвращает true, если аккаунт пользователя не заблокирован.
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    // Возвращает true, если учётные данные пользователя не истекли.
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    // Возвращает true, если пользователь включён (активен).
    public boolean isEnabled() {
        return true;
    }
    // ...
}