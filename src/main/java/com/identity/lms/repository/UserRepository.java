package com.identity.lms.repository;

import com.identity.lms.domain.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Class to hold test users.
 */
@Repository
public class UserRepository {

    private final Map<String, User> users;

    public UserRepository(BCryptPasswordEncoder bCryptPasswordEncoder) {
        users = Map.of(
                "dev", new User("dev", bCryptPasswordEncoder.encode("1234"),
                        List.of(new SimpleGrantedAuthority(Role.ROLE_USER.name()))),
                "admin", new User("admin", bCryptPasswordEncoder.encode("0102"),
                        List.of(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))),
                "test", new User("test", bCryptPasswordEncoder.encode("6543"),
                        List.of(new SimpleGrantedAuthority(Role.ROLE_TEST.name())))
        );
    }

    /**
     * Returns all users in local store.
     *
     * @return list of users
     */
    public Collection<User> findAll() {
        return users.values();
    }

    /**
     * Returns {@link User} by using name.
     *
     * @param name the name
     * @return the user
     */
    public User findById(String name) {
        return users.get(name);
    }
}
