package com.example.untitled.user;

import com.example.untitled.user.dto.UserRequest;
import com.example.untitled.user.dto.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    // POST /users : ユーザー情報の登録 - Register user information
    @PostMapping
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody UserRequest request
    ) {
        User user = userService.createUser(request);
        UserResponse response = UserResponse.from(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT /users/{id} : ユーザー情報の更新 - Update user information
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable @Min(value = 1, message = "IDは1以上である必要があります。 - ID must be 1 or greater.") Long id,
            @Valid @RequestBody UserRequest request
    ) {
        User user = userService.updateUser(id, request);
        UserResponse response = UserResponse.from(user);
        return ResponseEntity.ok(response);
    }
}
