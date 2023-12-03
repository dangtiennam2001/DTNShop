package com.tiennam.application.service.impl;

import com.tiennam.application.config.Contant;
import com.tiennam.application.entity.Cart;
import com.tiennam.application.entity.User;
import com.tiennam.application.exception.InternalServerException;
import com.tiennam.application.exception.NotFoundException;
import com.tiennam.application.model.dto.UserDTO;
import com.tiennam.application.model.mapper.UserMapper;
import com.tiennam.application.model.request.ChangePasswordRequest;
import com.tiennam.application.model.request.CreateUserRequest;
import com.tiennam.application.model.request.UpdateProfileRequest;
import com.tiennam.application.repository.CartRepository;
import com.tiennam.application.service.UserService;
import com.tiennam.application.exception.BadRequestException;
import com.tiennam.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public List<UserDTO> getListUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOS = new ArrayList<>();
        for (User user : users) {
            userDTOS.add(UserMapper.toUserDTO(user));
        }
        return userDTOS;
    }

    @Override
    public Page<User> adminListUserPages(String fullName, String phone, String email, Integer page) {
        page--;
        if (page < 0) {
            page = 0;
        }
        Pageable pageable = PageRequest.of(page, Contant.LIMIT_USER, Sort.by("created_at").descending());
        return userRepository.adminListUserPages(fullName, phone, email, pageable);
    }

    @Override
    public User createUser(CreateUserRequest createUserRequest) {
        User user = userRepository.findByEmail(createUserRequest.getEmail());
        if (user != null) {
            throw new BadRequestException("Email đã tồn tại trong hệ thống. Vui lòng sử dụng email khác!");
        }
        user = UserMapper.toUser(createUserRequest);
        userRepository.save(user);

        Cart cart = new Cart();
        cart.setCartProduct(null);
        cart.setTotalProduct(1);
        cart.setTotalPrice(1);
        cart.setTotalPrices(1L);
        cart.setTotalItems(1);
        cart.setUser(user);
        cartRepository.save(cart);

        return user;
    }

    @Override
    public void changePassword(User user, ChangePasswordRequest changePasswordRequest) {
        //Kiểm tra mật khẩu
        if (!BCrypt.checkpw(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu cũ không chính xác");
        }

        String hash = BCrypt.hashpw(changePasswordRequest.getNewPassword(), BCrypt.gensalt(12));
        user.setPassword(hash);
        userRepository.save(user);
    }

    @Override
    public User updateProfile(User user, UpdateProfileRequest updateProfileRequest) {
        user.setFullName(updateProfileRequest.getFullName());
        user.setPhone(updateProfileRequest.getPhone());
        user.setAddress(updateProfileRequest.getAddress());

        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void deleteUserById(Long id, Long cartId) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException("Người dùng không tồn tại");
        }
        Cart cart = cartRepository.findCartByUserId(id);
        cartRepository.delete(cart);
        userRepository.delete(opt.get());
    }

}
