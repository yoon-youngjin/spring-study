package dev.yoon.refactoring_board.controller;

import dev.yoon.refactoring_board.dto.common.Result;
import dev.yoon.refactoring_board.dto.UserDto;
import dev.yoon.refactoring_board.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto.Res> createUser(
            @RequestBody @Valid UserDto.SignUpReq userDto) {
        return ResponseEntity.ok(this.userService.createUser(userDto));
    }

    @GetMapping()
    public ResponseEntity<Result<List<UserDto.Res>>> readUserAll(
    ) {
        List<UserDto.Res> userDtos = this.userService.readUserAll();
        Result result = new Result(userDtos.size(),userDtos);

        return ResponseEntity.ok(result);
    }
    @GetMapping("{userId}")
    public ResponseEntity<UserDto.Res> readUserOne(
            @PathVariable("userId") Long userId) {

        return ResponseEntity.ok(this.userService.readUserOne(userId));
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable("userId") Long userId,
            @RequestBody UserDto.Req userDto) {
        userService.updateUser(userId, userDto);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable("userId") Long userId) {
        this.userService.deleteUser(userId);

        return ResponseEntity.noContent().build();

    }
}
