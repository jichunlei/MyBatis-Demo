# Mybatis介绍

[TOC]

## 一、简介

> MyBatis 本是apache的一个开源项目iBatis, 2010年这个项目由apache software foundation 迁移到了google code，并且改名为MyBatis。是一个基于Java的持久层框架（SQL映射框架）。

## 二、为什么要使用MyBatis

* 传统JDBC中，我们除了需要自己提供 SQL 外，还必须操作 Connection、Statment、ResultSet，不仅如此，为了访问不同的表，不同字段的数据，我们需要些很多雷同模板化的**繁琐又枯燥**。

* 使用了 **MyBatis** 之后，**只需要提供 SQL 语句就好了**，其余的诸如：建立连接、操作 Statment、ResultSet，处理 JDBC 相关异常等等都可以交给 MyBatis 去处理，我们的**关注点于是可以就此集中在 SQL 语句上**，关注在增删改查这些操作层面上。

*  MyBatis 支持使用简单的 XML 或注解来配置和映射原生信息，将接口和 Java 的 POJOs(Plain Old Java Objects,普通的 Java对象)映射成数据库中的记录。

* 相比较hibernate框架

  * hibernate（ORM对象关系映射框架）缺点：
    * 全自动化框架，很难对SQL进行细致的优化
    * 对于复杂SQL支持较为吃力（自定义SQL反而破坏了Hibernate封装以及简洁性）
    * 全映射框架，很难做部分字段的映射
    * 学习成本高，难精通

  * MyBatis（SQL映射框架）优点：
    * 半自动框架，支持对SQL的更为细致的优化
    * 可以方便进行部分字段的映射，减少数据库压力
    * 上手容易，学习成本低

## 三、项目搭建

### 1、导入依赖包（以3.4.2版本为例）

```xml
<!--导入mybatis依赖包-->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.4.2</version>
</dependency>

<!-- mysql-connector-java驱动 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.32</version>
</dependency>
```

### 2、创建表结构

```sql
//创建用户表，存放用户基本信息
CREATE TABLE `user` (
    `id` int(11) NOT NULL,
    `name` varchar(40) NOT NULL,s
    `password` varchar(40) DEFAULT NULL,
    `email` varchar(60) DEFAULT NULL,
    `birthday` date DEFAULT NULL,
    PRIMARY KEY (`id`)
) 
```

### 3、创建实体类

* 字段与表结构保持一致，字段名可以不相同

```jav
/**
 * @Auther: xianzilei
 * @Date: 2019/8/15 18:52
 * @Description: 用户基本信息实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
//这里使用了lombok简化了代码
public class User {
    private int id;
    private String name;
    private String password;
    private String email;
    private Date birthday;
}
```

### 4、创建对应的mapper文件

* 接口形式，里面有一个查询方法

```java
/**
 * @Auther: xianzilei
 * @Date: 2019/8/15 19:02
 * @Description: Users的dao层
 */
public interface UserMapper {
    /**
     * @Description: 根据主键查询用户信息
     * @param id 1
     * @return: cn.jicl.entity.Users
     * @auther: xianzilei
     * @date: 2019/8/15 19:08
     **/
    User selectByPrimaryKey(int id);
}
```

### 5、创建配置文件

##### 5.1、全局配置文件

* mybatis-config.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!--加载类路径下的配置文件-->
    <properties resource="config/db.properties"/>

    <!--配置mybatis打印sql-->
    <settings>
        <setting name="logImpl" value="STDOUT_LOGGING" />
    </settings>

    <!-- 设置一个默认的连接环境信息 -->
    <environments default="mysql_development">
        <!-- 连接环境信息，mysql环境 -->
        <environment id="mysql_development">
            <!-- mybatis使用jdbc事务管理方式 -->
            <transactionManager type="JDBC"/>
            <!-- mybatis使用连接池方式来获取连接 -->
            <dataSource type="POOLED">
                <!-- 配置与数据库交互的4个必要属性 -->
                <property name="username" value="${mysql_username}"/>
                <property name="password" value="${mysql_password}"/>
                <property name="url" value="${mysql_jdbcUrl}"/>
                <property name="driver" value="${mysql_driverClass}"/>
            </dataSource>
        </environment>
    </environments>

    <!--将配置文件和映射文件关联起来-->
    <mappers>
        <mapper resource="mapper/User.xml"/>
    </mappers>
</configuration>
```

##### 5.2、实体与映射关系文件：SQL映射文件

* User.xml，相当于mapper接口的实现

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<!-- namespace属性是名称空间，必须唯一 -->
<mapper namespace="cn.jicl.mapper.UsersMapper">

    <!-- resultMap标签:映射实体与表
         type属性：表示实体全路径名
         id属性：为实体与表的映射取一个任意的唯一的名字
    -->
    <resultMap type="cn.jicl.entity.Users" id="BaseResultMap">
        <!-- id标签:映射主键属性
             result标签：映射非主键属性
             property属性:实体的属性名
             column属性：表的字段名
        -->
        <id property="id" column="id"/>
        <result property="name" column="name"/>
        <result property="password" column="password"/>
        <result property="email" column="email"/>
        <result property="birthday" column="birthday"/>
    </resultMap>

    <!--查询sql-->
    <select id="selectByPrimaryKey" resultMap="BaseResultMap" parameterType="integer">
        select * from users where id=#{id}
    </select>
</mapper>
```

### 6、编写测试类

```java
package cn.jicl.service;

import cn.jicl.entity.User;
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
 * @Description: 用户信息测试类
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

            //5、调用sql
            Users user = usersMapper.selectByPrimaryKey(2);
            System.out.println(user);
        }
    }
}
```

## 四、全局配置文件详解

### 1、配置文件结构总览

* configuration（配置）
  * properties（属性）
  * settings（设置）
  * typeAliases（类型别名）
  * typeHandlers（类型处理器）
  * objectFactory（对象工厂）
  * plugins（插件）
  * environments（环境配置）
    * environment（环境变量）
      * transactionManager（事务管理器）
      * dataSource（数据源）
  * databaseIdProvider（数据库厂商标识）
  * mappers（映射器）

### 2、各层结构详解

#### 2.1、properties（属性）

* 一般用于加载外部属性文件

  ```xml
  <!--加载类路径下的配置文件
          resource：表示类路径下（常用）
          url:磁盘或者网络路径
      -->
  <properties resource="config/db.properties"/>
  ```

  

#### 2.2、settings（设置）

* MyBatis 中极为重要的调整设置，它们会改变 MyBatis 的运行时行为

  ```xml
  <!--完整的 settings 元素的示例,value中设置为默认值-->
  <settings>
      <!--全局地开启或关闭配置文件中的所有映射器已经配置的任何缓存-->
      <setting name="cacheEnabled" value="true"/>
      <!--延迟加载的全局开关。当开启时，所有关联对象都会延迟加载。 特定关联关系中可通过设置 fetchType 属性来覆盖该项的开关状态-->
      <setting name="lazyLoadingEnabled" value="false"/>
      <!--是否允许单一语句返回多结果集（需要驱动支持）-->
      <setting name="multipleResultSetsEnabled" value="true"/>
      <!--使用列标签代替列名。不同的驱动在这方面会有不同的表现，具体可参考相关驱动文档或通过测试这两种不同的模式来观察所用驱动的结果-->
      <setting name="useColumnLabel" value="true"/>
      <!--允许 JDBC 支持自动生成主键，需要驱动支持。 如果设置为 true 则这个设置强制使用自动生成主键，尽管一些驱动不能支持但仍可正常工作（比如 Derby）-->
      <setting name="useGeneratedKeys" value="false"/>
      <!--指定 MyBatis 应如何自动映射列到字段或属性。 NONE 表示取消自动映射；PARTIAL 只会自动映射没有定义嵌套结果集映射的结果集。 FULL 会自动映射任意复杂的结果集（无论是否嵌套）-->
      <setting name="autoMappingBehavior" value="PARTIAL"/>
      <!--指定发现自动映射目标未知列（或者未知属性类型）的行为。
  		* NONE: 不做任何反应
  		* WARNING: 输出提醒日志 	('org.apache.ibatis.session.AutoMappingUnknownColumnBehavior' 的日志等级必须设置为 WARN)
  		* FAILING: 映射失败 (抛出 SqlSessionException)-->
      <setting name="autoMappingUnknownColumnBehavior" value="NONE"/>
      <!--配置默认的执行器。SIMPLE 就是普通的执行器；REUSE 执行器会重用预处理语句（prepared statements）； BATCH 执行器将重用语句并执行批量更新-->
      <setting name="defaultExecutorType" value="SIMPLE"/>
      <!--设置超时时间，它决定驱动等待数据库响应的秒数，默认未设置-->
      <setting name="defaultStatementTimeout" value="25"/>
      <!--为驱动的结果集获取数量（fetchSize）设置一个提示值。此参数只可以在查询设置中被覆盖，默认未设置-->
      <setting name="defaultFetchSize" value="100"/>
      <!--允许在嵌套语句中使用分页（RowBounds）。如果允许使用则设置为 false-->
      <setting name="safeRowBoundsEnabled" value="false"/>
      <!--是否开启自动驼峰命名规则（camel case）映射，即从经典数据库列名 A_COLUMN 到经典 Java 属性名 aColumn 的类似映射-->
      <setting name="mapUnderscoreToCamelCase" value="false"/>
      <!--MyBatis 利用本地缓存机制（Local Cache）防止循环引用（circular references）和加速重复嵌套查询。 默认值为 SESSION，这种情况下会缓存一个会话中执行的所有查询。 若设置值为 STATEMENT，本地会话仅用在语句执行上，对相同 SqlSession 的不同调用将不会共享数据-->
      <setting name="localCacheScope" value="SESSION"/>
      <!--当没有为参数提供特定的 JDBC 类型时，为空值指定 JDBC 类型。 某些驱动需要指定列的 JDBC 类型，多数情况直接用一般类型即可，比如 NULL、VARCHAR 或 OTHER-->
      <setting name="jdbcTypeForNull" value="OTHER"/>
      <!--指定哪个对象的方法触发一次延迟加载-->
      <setting name="lazyLoadTriggerMethods" value="equals,clone,hashCode,toString"/>
  </settings>
  ```

#### 2.3、typeAliases（类型别名）

* 为 Java 类型设置一个短的名字。 它只和 XML 配置有关，存在的意义仅在于用来减少类完全限定名的冗余

* 写法一：

  ```xml
  <!--该配置表示任何使用到cn.jicl.User都可以使用User-->
  <typeAliases>
      <!---->
      <typeAlias alias="User" type="cn.jicl.User"/>
  </typeAliases>
  ```

* 写法二：

  ```xml
  <!--该配置表示该包下的所有Javabean，在没有注解的情况下，会使用 Bean 的首字母小写的非限定类名来作为它的别名-->
  <typeAliases>
      <package name="cn.jicl"/>
  </typeAliases>
  ```

  ```java
  //有注解的情况下去注解的值
  @Alias("user")
  public class User {
      ...
  }
  ```

#### 2.4、typeHandlers（类型处理器）

* 无论是 MyBatis 在预处理语句（PreparedStatement）中设置一个参数时，还是从结果集中取出一个值时， 都会用类型处理器将获取的值以合适的方式转换成 Java 类型。下表描述了一些默认的类型处理器