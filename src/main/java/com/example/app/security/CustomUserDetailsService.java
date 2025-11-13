package com.example.app.security;

import com.example.app.mapper.UserMapper;
import com.example.app.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring SecurityのUserDetailsService実装クラス。
 * データベースからユーザー情報を取得してSpring Securityの認証に使用します。
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserMapper userMapper;
    
    /**
     * ユーザー名からユーザー情報を取得します。
     * 
     * @param username ユーザー名
     * @return UserDetailsオブジェクト
     * @throws UsernameNotFoundException ユーザーが見つからない場合
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("ユーザーが見つかりません: " + username);
        }
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities("ROLE_USER")
            .build();
    }
}

