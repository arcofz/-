package com.xunwu.leida.mapper;

import com.xunwu.leida.entity.Location;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LocationMapper {

    List<Location> findAll();

    boolean existsById(@Param("id") Long id);
}
