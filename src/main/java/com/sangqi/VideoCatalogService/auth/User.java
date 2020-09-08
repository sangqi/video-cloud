package com.sangqi.VideoCatalogService.auth;

import com.google.common.collect.Lists;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public class User implements UserDetails {

    private final List<GrantedAuthority> authorities_;
    private final String password_;
    private final String username_;

    private User(String username, String password) {
        this(username, password, Lists.newArrayList());
    }

    private User(String username, String password, String... authorities) {
        username_ = username;
        password_ = password;
        authorities_ = AuthorityUtils.createAuthorityList(authorities);
    }

    private User(String username, String password, List<GrantedAuthority> authorities) {
        super();
        username_ = username;
        password_ = password;
        authorities_ = authorities;
    }

    public static UserDetails create(String username, String password,
                                     String... authorities) {
        return new User(username, password, authorities);
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities_;
    }

    public String getPassword() {
        return password_;
    }

    public String getUsername() {
        return username_;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
