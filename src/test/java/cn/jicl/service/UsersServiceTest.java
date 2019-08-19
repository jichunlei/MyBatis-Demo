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
        String resource = "mybatis/mybatis-config.xml";
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

    /**
     * @Description: 体会Mybatis的一级缓存
     * @return: void
     * @auther: xianzilei
     * @date: 2019/8/18 18:29
     **/
    @Test
    public void test02() throws IOException {
        //1、加载mybatis的全局配置文件
        String resource = "mybatis/mybatis-config.xml";
        Reader reader = Resources.getResourceAsReader(resource);

        //2、创建一个sqlSessionFactory：sqlSession工厂，负责sqlSession的创建
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

        //3、获取sqlSession：和数据库的一次会话
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //4、获取mapper的实现类
        UsersMapper usersMapper = sqlSession.getMapper(UsersMapper.class);
        System.out.println(usersMapper);

        //5、调用sql
        //第一次调用查询，先去缓存则查询是否存在，不存在则取数据库查询
        Users user = usersMapper.selectByPrimaryKey(2);
        System.out.println(user);
        System.out.println("=======================================");
        //第二次调用同样查询，缓存则存在，则不会查询数据库，直接取缓存中的数据
        Users user2 = usersMapper.selectByPrimaryKey(2);
        System.out.println(user2);
        System.out.println(user == user2);
        System.out.println("=======================================");
        //提交事务，会清空sqlSession的一级缓存。避免脏毒
        sqlSession.commit();
        //第三次调用查询，因为一级缓存被清空，所有回去数据库中查询
        Users user3 = usersMapper.selectByPrimaryKey(2);
        System.out.println(user3);
        System.out.println(user==user3);
        sqlSession.close();
    }

    /**
     * @Description: 体会Mybatis的二级缓存
     * @return: void
     * @auther: xianzilei
     * @date: 2019/8/18 18:29
     **/
    @Test
    public void test03() throws IOException {
        //1、加载mybatis的全局配置文件
        String resource = "mybatis/mybatis-config.xml";
        Reader reader = Resources.getResourceAsReader(resource);

        //2、创建一个sqlSessionFactory：sqlSession工厂，负责sqlSession的创建
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

        //3、获取sqlSession：和数据库的一次会话
        SqlSession sqlSession1 = sqlSessionFactory.openSession();
        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        //4、获取mapper的实现类
        UsersMapper usersMapper1 = sqlSession1.getMapper(UsersMapper.class);
        UsersMapper usersMapper2 = sqlSession1.getMapper(UsersMapper.class);

        //5、调用sql
        System.out.println("=========sqlSession1查询1号用户======");
        Users user1 = usersMapper1.selectByPrimaryKey(1);
        System.out.println(user1);
        sqlSession1.commit();
        System.out.println("=========sqlSession2查询1号用户======");
        Users user2 = usersMapper2.selectByPrimaryKey(1);
        System.out.println(user2);
        System.out.println(user1 == user2);
        sqlSession1.close();
        sqlSession2.close();
    }
}
