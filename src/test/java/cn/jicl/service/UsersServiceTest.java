package cn.jicl.service;

import cn.jicl.entity.Users;
import cn.jicl.mapper.UsersMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import javax.lang.model.element.VariableElement;
import java.io.IOException;
import java.io.Reader;

/**
 * @Auther: xianzilei
 * @Date: 2019/8/15 22:24
 * @Description: TODO
 */
public class UsersServiceTest {

    @Test
    public void test01() throws IOException {
        //1、加载mybatis的全局配置文件
        String resource= "mybatis/mybatis-config.xml";
        Reader reader = Resources.getResourceAsReader(resource);

        //2、创建一个sqlSessionFactory：sqlSession工厂，负责sqlSession的创建
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

        //3、获取sqlSession：和数据库的一次会话
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            //4、获取mapper的实现类
            UsersMapper usersMapper = sqlSession.getMapper(UsersMapper.class);
            System.out.println(usersMapper);

            //5、调用sql
            Users user = usersMapper.selectByPrimaryKey(2);
            System.out.println(user);
        }

    }
}
