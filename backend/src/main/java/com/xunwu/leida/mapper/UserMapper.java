package com.xunwu.leida.mapper;

import com.xunwu.leida.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    User findByStudentNo(@Param("studentNo") String studentNo);

    User findById(@Param("id") Long id);

    int insert(User user);

    /** 后台用户管理:keyword 模糊匹配学号或姓名 */
    List<User> findPage(@Param("keyword") String keyword,
                        @Param("offset") int offset,
                        @Param("limit") int limit);

    long count(@Param("keyword") String keyword);
}
