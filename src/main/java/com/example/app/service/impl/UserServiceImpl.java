package com.example.app.service.impl;

import com.example.app.mapper.UserMapper;
import com.example.app.model.User;
import com.example.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserServiceインターフェースの実装クラス。
 * ユーザー情報の管理に関するビジネスロジックを実装します。
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public void register(User user) {
        if (userMapper.existsByUsername(user.getUsername())) {
            throw new IllegalStateException("ユーザー名は既に使用されています");
        }
        
        // パスワードをハッシュ化
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        
        userMapper.insert(user);
    }
    
    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
}

