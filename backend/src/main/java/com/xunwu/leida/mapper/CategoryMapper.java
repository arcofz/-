package com.xunwu.leida.mapper;

import com.xunwu.leida.entity.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper {

    List<Category> findAll();

    boolean existsById(@Param("id") Long id);
}
