package com.xunwu.leida.service;

import com.xunwu.leida.common.BizException;
import com.xunwu.leida.dto.AuthResponse;
import com.xunwu.leida.dto.LoginRequest;
import com.xunwu.leida.dto.RegisterRequest;
import com.xunwu.leida.dto.UserVO;
import com.xunwu.leida.entity.User;
import com.xunwu.leida.mapper.UserMapper;
import com.xunwu.leida.security.JwtUtil;
import com.xunwu.leida.security.PasswordUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest req) {
        String studentNo = req.getStudentNo().trim();
        if (userMapper.findByStudentNo(studentNo) != null) {
            throw new BizException(409, "学号已注册");
        }
        User user = new User();
        user.setStudentNo(studentNo);
        user.setName(req.getName().trim());
        user.setRole("student");
        user.setContact(req.getContact().trim());
        user.setPasswordHash(PasswordUtil.hash(req.getPassword()));
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            // 并发注册同学号时由唯一约束兜底
            throw new BizException(409, "学号已注册");
        }
        User saved = userMapper.findById(user.getId());
        String token = jwtUtil.generate(saved.getId(), saved.getRole());
        return new AuthResponse(token, UserVO.from(saved));
    }

    public AuthResponse login(LoginRequest req) {
        User user = userMapper.findByStudentNo(req.getStudentNo().trim());
        // 契约：账号不存在或密码错误统一 400，不用 401
        if (user == null || !PasswordUtil.verify(req.getPassword(), user.getPasswordHash())) {
            throw new BizException(400, "学号或密码错误");
        }
        String token = jwtUtil.generate(user.getId(), user.getRole());
        return new AuthResponse(token, UserVO.from(user));
    }

    public UserVO me(long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException(401, "未登录或登录已过期");
        }
        return UserVO.from(user);
    }
}
