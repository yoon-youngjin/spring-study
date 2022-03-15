package dev.yoon.refactoring_board.service;

import dev.yoon.refactoring_board.domain.Area;
import dev.yoon.refactoring_board.domain.user.User;
import dev.yoon.refactoring_board.dto.UserDto;
import dev.yoon.refactoring_board.exception.NameDuplicationException;
import dev.yoon.refactoring_board.exception.UserNotFoundException;
import dev.yoon.refactoring_board.repository.AreaRepository;
import dev.yoon.refactoring_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final AreaRepository areaRepository;

    private final EntityManager entityManager;


//    @Override
//    public User loadUserByUsername(String username) throws UsernameNotFoundException {
//
//        return userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException(username));
//    }

    public UserDto.Res createUser(UserDto.SignUpReq dto) {

        isExistedName(dto.getName());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        dto.setPassword(encoder.encode(dto.getPassword()));

        Area area = new Area(dto.getAddress(), dto.getLocation());

        User user = User.builder()
                .username(dto.getName())
                .area(area)
                .password(dto.getPassword())
                .userCategory(dto.getUserCategory())
                .build();

        this.areaRepository.save(area); // area id 생성
        this.userRepository.save(user);

        return new UserDto.Res(user);

    }

    public List<UserDto.Res> readUserAll() {

        List<User> users = userRepository.findAll();

        List<UserDto.Res> res = users.stream().parallel()
                .map(user -> new UserDto.Res(user))
                .collect(Collectors.toList());

        return res;
    }

    public UserDto.Res readUserOne(Long userId) {

        return new UserDto.Res(findById(userId));
    }

    public boolean updateUser(Long userId, UserDto.Req userDto) {

        User user = findById(userId);
        user.updateUser(userDto);

        return true;
    }

    public boolean deleteUser(Long userId) {
        User user = findById(userId);
        userRepository.delete(user);
        return true;
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        Optional<User> account = userRepository.findById(id);
        account.orElseThrow(() -> new UserNotFoundException(id));
        return account.get();
    }

    @Transactional(readOnly = true)
    public void isExistedName(String username) {

        Optional<User> byUsername = userRepository.findByUsername(username);
        if (byUsername.isPresent())
            throw new NameDuplicationException(username);

    }

}
