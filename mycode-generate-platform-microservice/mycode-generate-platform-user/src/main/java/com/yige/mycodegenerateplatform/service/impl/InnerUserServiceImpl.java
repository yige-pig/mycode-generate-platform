package com.yige.mycodegenerateplatform.service.impl;

import com.yige.mycodegenerateplatform.innerservice.InnerUserService;
import com.yige.mycodegenerateplatform.model.entity.User;
import com.yige.mycodegenerateplatform.model.vo.UserVO;
import com.yige.mycodegenerateplatform.service.UserService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 内部服务实现类
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserService userService;

    @Override
    public List<User> listByIds(Collection<? extends Serializable> ids) {
        return userService.listByIds(ids);
    }

    @Override
    public User getById(Serializable id) {
        return userService.getById(id);
    }

    @Override
    public UserVO getUserVO(User user) {
        return userService.getUserVO(user);
    }
}
