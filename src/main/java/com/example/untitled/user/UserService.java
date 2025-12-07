package com.example.untitled.user;

import com.example.untitled.common.dto.ErrorDetails;
import com.example.untitled.common.exception.DuplicationResourceException;
import com.example.untitled.user.dto.UserRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) { this.userRepository = userRepository; }

    public User createUser(UserRequest reqDto) {
        userRepository.findByUserNameAndIsDeleted(reqDto.getUserName(), false)
                .ifPresent(user -> {
                    throw new DuplicationResourceException(
                            "Conflict detected",
                            List.of(new ErrorDetails(
                                    "userName",
                                    "User name already exist: " + reqDto.getUserName()
                            ))
                    );
                });

        User user = new User();
        user.setUserName(reqDto.getUserName());
        user.setPassword(reqDto.getPassword());

        return userRepository.save(user);
    }
}
