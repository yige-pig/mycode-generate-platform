package com.yige.mycodegenerateplatform.mapper;

import com.mybatisflex.core.BaseMapper;
import com.yige.mycodegenerateplatform.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 映射层。
 *
 * @author <a href="https://github.com/yige-pig">Yige</a>
 */
//必须加@Mapper，防止原生Mybatis污染
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
