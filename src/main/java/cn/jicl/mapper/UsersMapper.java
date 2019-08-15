package cn.jicl.mapper;

import cn.jicl.entity.Users;

/**
 * @Auther: xianzilei
 * @Date: 2019/8/15 19:02
 * @Description: Users的dao层
 */
public interface UsersMapper {
    /**
     * @Description: 根据主键查询用户信息
     * @param id 1
     * @return: cn.jicl.entity.Users
     * @auther: xianzilei
     * @date: 2019/8/15 19:08
     **/
    Users selectByPrimaryKey(int id);
}
