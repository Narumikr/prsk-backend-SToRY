package com.example.untitled.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserNameAndIsDeleted(String userName, boolean isDeleted);

    Optional<User> findByIdAndIsDeleted(Long id, boolean isDeleted);
}
