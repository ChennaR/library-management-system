package com.identity.lms.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.identity.lms.domain.LibraryUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class to hold test users.
 */
@Repository
@Slf4j
public class UserRepository {
    private final Map<String, User> users;

    public UserRepository(ObjectMapper objectMapper,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        users = loadUsers(objectMapper)
                .entrySet()
                .stream()
                .map(entry -> new User(entry.getKey(),
                        bCryptPasswordEncoder.encode(entry.getValue().getPassword()),
                        List.of(new SimpleGrantedAuthority(entry.getValue().getRole().name()))))
                .collect(Collectors.toMap(User::getUsername, value -> value));
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

    private Map<String, LibraryUser> loadUsers(ObjectMapper objectMapper) {
        try {
            TypeReference<Map<String, LibraryUser>> typeReference = new TypeReference<>() {
            };
            return objectMapper.readValue(new ClassPathResource("/data/users.json")
                    .getContentAsString(StandardCharsets.UTF_8), typeReference);
        } catch (Exception ex) {
            log.error("Error occurred while loading users from file system.", ex);
            throw new IllegalStateException("Error occurred while loading users from file system." + ex.getMessage());
        }
    }

}
