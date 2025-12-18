package com.example.untitled.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findByIsDeleted(boolean isDeleted, Pageable pageable);

    Optional<User> findByUserNameAndIsDeleted(String userName, boolean isDeleted);

    Optional<User> findByIdAndIsDeleted(Long id, boolean isDeleted);
}
