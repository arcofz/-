package com.xunwu.leida.mapper;

import com.xunwu.leida.entity.Notification;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NotificationMapper {

    List<Notification> findByUser(@Param("userId") Long userId,
                                  @Param("unreadOnly") boolean unreadOnly,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    long countByUser(@Param("userId") Long userId,
                     @Param("unreadOnly") boolean unreadOnly);

    Notification findById(@Param("id") Long id);

    int markRead(@Param("id") Long id);

    /** 业务事件写通知(认领/审核/匹配/交接);同一事务内调用 */
    int insert(Notification notification);
}
