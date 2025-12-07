package com.example.untitled.user;

import com.example.untitled.common.exception.DuplicationResourceException;
import com.example.untitled.user.dto.UserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    /**
     * createUser : 正常系 - ユーザーが正常に作成される
     */
    @Test
    public void createUser_Success() {
        UserRequest request = new UserRequest();
        request.setUserName("testuser");
        request.setPassword("testpassword");

        User createdUser = new User();
        createdUser.setId(1L);
        createdUser.setUserName("testuser");
        createdUser.setPassword("testpassword");

        when(userRepository.findByUserNameAndIsDeleted("testuser", false)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(createdUser);

        User result = userService.createUser(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUserName());
        assertEquals("testpassword", result.getPassword());

        verify(userRepository, times(1)).findByUserNameAndIsDeleted("testuser", false);
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * createUser : 異常系 - ユーザー名が重複しており、例外をスローする
     */
    @Test
    public void createUser_DuplicateUserName() {
        UserRequest request = new UserRequest();
        request.setUserName("testuser");
        request.setPassword("testpassword");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUserName("testuser");
        existingUser.setPassword("existingpassword");

        when(userRepository.findByUserNameAndIsDeleted("testuser", false)).thenReturn(Optional.of(existingUser));

        DuplicationResourceException exception = assertThrows(
                DuplicationResourceException.class,
                () -> userService.createUser(request)
        );

        assertNotNull(exception.getDetails());
        assertEquals(1, exception.getDetails().size());
        assertEquals("userName", exception.getDetails().get(0).getField());
        assertTrue(exception.getDetails().get(0).getMessage().contains("testuser"));

        verify(userRepository, times(1)).findByUserNameAndIsDeleted("testuser", false);
        verify(userRepository, never()).save(any(User.class));
    }
}
