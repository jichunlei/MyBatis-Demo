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
  //有注解的情况下取注解里面的值
  @Alias("user")
  public class User {
      ...
  }
  ```

#### 2.4、typeHandlers（类型处理器）

* 无论是 MyBatis 在预处理语句（PreparedStatement）中设置一个参数时，还是从结果集中取出一个值时， 都会用类型处理器将获取的值以合适的方式转换成 Java 类型。

* 下面为Mybatis中默认的类型处理器。

  | 类型处理器                   | Java 类型                       | JDBC 类型                                                    |
  | :--------------------------- | :------------------------------ | :----------------------------------------------------------- |
  | `BooleanTypeHandler`         | `java.lang.Boolean`, `boolean`  | 数据库兼容的 `BOOLEAN`                                       |
  | `ByteTypeHandler`            | `java.lang.Byte`, `byte`        | 数据库兼容的 `NUMERIC` 或 `BYTE`                             |
  | `ShortTypeHandler`           | `java.lang.Short`, `short`      | 数据库兼容的 `NUMERIC` 或 `SMALLINT`                         |
  | `IntegerTypeHandler`         | `java.lang.Integer`, `int`      | 数据库兼容的 `NUMERIC` 或 `INTEGER`                          |
  | `LongTypeHandler`            | `java.lang.Long`, `long`        | 数据库兼容的 `NUMERIC` 或 `BIGINT`                           |
  | `FloatTypeHandler`           | `java.lang.Float`, `float`      | 数据库兼容的 `NUMERIC` 或 `FLOAT`                            |
  | `DoubleTypeHandler`          | `java.lang.Double`, `double`    | 数据库兼容的 `NUMERIC` 或 `DOUBLE`                           |
  | `BigDecimalTypeHandler`      | `java.math.BigDecimal`          | 数据库兼容的 `NUMERIC` 或 `DECIMAL`                          |
  | `StringTypeHandler`          | `java.lang.String`              | `CHAR`, `VARCHAR`                                            |
  | `ClobReaderTypeHandler`      | `java.io.Reader`                | -                                                            |
  | `ClobTypeHandler`            | `java.lang.String`              | `CLOB`, `LONGVARCHAR`                                        |
  | `NStringTypeHandler`         | `java.lang.String`              | `NVARCHAR`, `NCHAR`                                          |
  | `NClobTypeHandler`           | `java.lang.String`              | `NCLOB`                                                      |
  | `BlobInputStreamTypeHandler` | `java.io.InputStream`           | -                                                            |
  | `ByteArrayTypeHandler`       | `byte[]`                        | 数据库兼容的字节流类型                                       |
  | `BlobTypeHandler`            | `byte[]`                        | `BLOB`, `LONGVARBINARY`                                      |
  | `DateTypeHandler`            | `java.util.Date`                | `TIMESTAMP`                                                  |
  | `DateOnlyTypeHandler`        | `java.util.Date`                | `DATE`                                                       |
  | `TimeOnlyTypeHandler`        | `java.util.Date`                | `TIME`                                                       |
  | `SqlTimestampTypeHandler`    | `java.sql.Timestamp`            | `TIMESTAMP`                                                  |
  | `SqlDateTypeHandler`         | `java.sql.Date`                 | `DATE`                                                       |
  | `SqlTimeTypeHandler`         | `java.sql.Time`                 | `TIME`                                                       |
  | `ObjectTypeHandler`          | Any                             | `OTHER` 或未指定类型                                         |
  | `EnumTypeHandler`            | Enumeration Type                | VARCHAR 或任何兼容的字符串类型，用以存储枚举的名称（而不是索引值） |
  | `EnumOrdinalTypeHandler`     | Enumeration Type                | 任何兼容的 `NUMERIC` 或 `DOUBLE` 类型，存储枚举的序数值（而不是名称）。 |
  | `SqlxmlTypeHandler`          | `java.lang.String`              | `SQLXML`                                                     |
  | `InstantTypeHandler`         | `java.time.Instant`             | `TIMESTAMP`                                                  |
  | `LocalDateTimeTypeHandler`   | `java.time.LocalDateTime`       | `TIMESTAMP`                                                  |
  | `LocalDateTypeHandler`       | `java.time.LocalDate`           | `DATE`                                                       |
  | `LocalTimeTypeHandler`       | `java.time.LocalTime`           | `TIME`                                                       |
  | `OffsetDateTimeTypeHandler`  | `java.time.OffsetDateTime`      | `TIMESTAMP`                                                  |
  | `OffsetTimeTypeHandler`      | `java.time.OffsetTime`          | `TIME`                                                       |
  | `ZonedDateTimeTypeHandler`   | `java.time.ZonedDateTime`       | `TIMESTAMP`                                                  |
  | `YearTypeHandler`            | `java.time.Year`                | `INTEGER`                                                    |
  | `MonthTypeHandler`           | `java.time.Month`               | `INTEGER`                                                    |
  | `YearMonthTypeHandler`       | `java.time.YearMonth`           | `VARCHAR` 或 `LONGVARCHAR`                                   |
  | `JapaneseDateTypeHandler`    | `java.time.chrono.JapaneseDate` | `DATE`                                                       |

* 你可以重写类型处理器或创建你自己的类型处理器来处理不支持的或非标准的类型。 具体做法为：实现 org.apache.ibatis.type.TypeHandler 接口， 或继承 org.apache.ibatis.type.BaseTypeHandler， 然后可以选择性地将它映射到一个 JDBC 类型。（一般不使用）

#### 2.5、objectFactory（对象工厂）

* MyBatis 每次创建结果对象的新实例时，它都会使用一个对象工厂（ObjectFactory）实例来完成。 默认的对象工厂需要做的仅仅是实例化目标类，要么通过默认构造方法，要么在参数映射存在的时候通过参数构造方法来实例化
*  如果想覆盖对象工厂的默认行为，则可以通过创建自己的对象工厂来实现
* 一般不配置，使用Mybati提供的对象工厂

#### 2.6、插件（plugins）

* MyBatis 允许你在已映射语句执行过程中的某一点进行拦截调用。
* 默认情况下，MyBatis 允许使用插件来拦截的方法调用包括：
  * Executor (update, query, flushStatements, commit, rollback, getTransaction, close, isClosed)
  * ParameterHandler (getParameterObject, setParameters)
  * ResultSetHandler (handleResultSets, handleOutputParameters)
  * StatementHandler (prepare, parameterize, batch, update, query)

#### 2.7、环境配置（environments）

* MyBatis 可以配置成适应多种环境，这种机制有助于将 SQL 映射应用于多种数据库之中

* 只包含一个environment标签，表示每一个环境的具体信息

* default表示使用哪一套环境

* 后续和Spring整合时，使用Spring的事务管理器，连接信息也配置在Spring配置中，仅做了解即可

* 配置总览（举例）

  ```xml
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
      
      <!-- 连接环境信息，oracle环境 -->
      <environment id="oracle_development">
          <!-- mybatis使用jdbc事务管理方式 -->
          <transactionManager type="JDBC"/>
          <!-- mybatis使用连接池方式来获取连接 -->
          <dataSource type="POOLED">
              <!-- 配置与数据库交互的4个必要属性 -->
              <property name="username" value="${oracle_username}"/>
              <property name="password" value="${oracle_password}"/>
              <property name="url" value="${oracle_jdbcUrl}"/>
              <property name="driver" value="${oracle_driverClass}"/>
          </dataSource>
      </environment>
  </environments>
  ```

  

##### 2.7.1、environment（环境变量）

* 配置环境信息
* 包含transactionManager和dataSource标签
* id为该环境的唯一标识

###### 2.7.1.1、事务管理器（transactionManager）

* 在 MyBatis 中有两种类型的事务管理器

  * JDBC
    
  * 这个配置就是直接使用了 JDBC 的提交和回滚设置，它依赖于从数据源得到的连接来管理事务作用域
    
  * MANAGED 

    * 这个配置几乎没做什么。它从来不提交或回滚一个连接，而是让容器来管理事务的整个生命周期（比如 JEE 应用服务器的上下文）。 默认情况下它会关闭连接，然而一些容器并不希望这样，因此需要将 closeConnection 属性设置为 false 来阻止它默认的关闭行为。

      ```xml
      <transactionManager type="MANAGED">
          <property name="closeConnection" value="false"/>
      </transactionManager>
      ```

* 如果你正在使用 Spring + MyBatis，则没有必要配置事务管理器， 因为 Spring 模块会使用自带的管理器来覆盖前面的配置。

###### 2.7.1.2、数据源（dataSource）

* Mybatis有三种内建的数据源类型

  * UNPOOLED
    * 这个数据源的实现只是每次被请求时打开和关闭连接。虽然有点慢，但对于在数据库连接可用性方面没有太高要求的简单应用程序来说，是一个很好的选择。 不同的数据库在性能方面的表现也是不一样的，对于某些数据库来说，使用连接池并不重要，这个配置就很适合这种情形。
    * 属性
      * driver – 这是 JDBC 驱动的 Java 类的完全限定名（并不是 JDBC 驱动中可能包含的数据源类）。
      * url – 这是数据库的 JDBC URL 地址。
      * username – 登录数据库的用户名。
      * password – 登录数据库的密码。
      * defaultTransactionIsolationLevel – 默认的连接事务隔离级别。
      * defaultNetworkTimeout – 默认网络超时值(以毫秒为单位)
      * 可选项：你也可以传递属性给数据库驱动。只需在属性名加上“driver.”前缀即可，例如：
        - driver.encoding=UTF8

  * POOLED
    * 这种数据源的实现利用“池”的概念将 JDBC 连接对象组织起来，避免了创建新的连接实例时所必需的初始化和认证时间。 这是一种使得并发 Web 应用快速响应请求的流行处理方式。
    * 除了上述提到 UNPOOLED 下的属性外，还有更多属性用来配置 POOLED 的数据源
      * poolMaximumActiveConnections – 在任意时间可以存在的活动（也就是正在使用）连接数量，默认值：10
      * poolMaximumIdleConnections – 任意时间可能存在的空闲连接数。
      * poolMaximumCheckoutTime – 在被强制返回之前，池中连接被检出（checked out）时间，默认值：20000 毫秒（即 20 秒）
      * poolTimeToWait – 这是一个底层设置，如果获取连接花费了相当长的时间，连接池会打印状态日志并重新尝试获取一个连接（避免在误配置的情况下一直安静的失败），默认值：20000 毫秒（即 20 秒）。
      * poolMaximumLocalBadConnectionTolerance – 这是一个关于坏连接容忍度的底层设置， 作用于每一个尝试从缓存池获取连接的线程。 如果这个线程获取到的是一个坏的连接，那么这个数据源允许这个线程尝试重新获取一个新的连接，但是这个重新尝试的次数不应该超过 poolMaximumIdleConnections 与 poolMaximumLocalBadConnectionTolerance 之和。 默认值：3 （新增于 3.4.5）
      * poolPingQuery – 发送到数据库的侦测查询，用来检验连接是否正常工作并准备接受请求。默认是“NO PING QUERY SET”，这会导致多数数据库驱动失败时带有一个恰当的错误消息。
      * poolPingEnabled – 是否启用侦测查询。若开启，需要设置 poolPingQuery 属性为一个可执行的 SQL 语句（最好是一个速度非常快的 SQL 语句），默认值：false。
      * poolPingConnectionsNotUsedFor – 配置 poolPingQuery 的频率。可以被设置为和数据库连接超时时间一样，来避免不必要的侦测，默认值：0（即所有连接每一时刻都被侦测 — 当然仅当 poolPingEnabled 为 true 时适用）。

  * JNDI
    * 这个数据源的实现是为了能在如 EJB 或应用服务器这类容器中使用，容器可以集中或在外部配置数据源，然后放置一个 JNDI 上下文的引用。
    * 这种数据源配置只需要两个属性:
      * initial_context – 这个属性用来在 InitialContext 中寻找上下文（即，initialContext.lookup(initial_context)）。这是个可选属性，如果忽略，那么将会直接从 InitialContext 中寻找 data_source 属性。
      * data_source – 这是引用数据源实例位置的上下文的路径。提供了 initial_context 配置时会在其返回的上下文中进行查找，没有提供时则直接在 InitialContext 中查找

#### 2.8、数据库厂商标识（databaseIdProvider）

* MyBatis 可以根据不同的数据库厂商执行不同的语句，这种多厂商的支持是基于映射语句中的 databaseId 属性。 MyBatis 会加载不带 databaseId 属性和带有匹配当前数据库 databaseId 属性的所有语句。 如果同时找到带有 databaseId 和不带 databaseId 的相同语句，则后者会被舍弃。

  ```xml
  <databaseIdProvider type="DB_VENDOR">
      <!--name：数据库厂商标识，value：起一个别名-->
      <property name="SQL Server" value="sqlserver"/>
      <property name="DB2" value="db2"/>
      <property name="Oracle" value="oracle" />
      <property name="MySQL" value="mysql" />
  </databaseIdProvider>
  ```

#### 2.9、映射器（mappers）

* 将配置文件和映射文件关联起来

* Mybatis提供了多种写法

  * 使用相对于类路径的资源引用

    ```xml
    <mappers>
        <mapper resource="cn/jicl/mapper/xxx1.xml"/>
        <mapper resource="cn/jicl/mapper/xxx2.xml"/>
    </mappers>
    ```

  * 使用完全限定资源定位符（URL）

    ```xml
    <mappers>
        <mapper url="file:///var/mappers/AuthorMapper.xml"/>
        <mapper url="file:///var/mappers/BlogMapper.xml"/>
        <mapper url="file:///var/mappers/PostMapper.xml"/>
    </mappers>
    ```

  * 使用映射器接口实现类的完全限定类名

    ```xml
    <mappers>
        <mapper class="cn.jicl.mapper.xxx1"/>
        <mapper class="cn.jicl.mapper.xxx2"/>
    </mappers>
    ```

  * 将包内的映射器接口实现全部注册为映射器（常用）

    ```xml
    <mappers>
        <!--批量扫描-->
        <!--需要保证xml和mapper接口在同路径下，且文件名相同-->
        <package name="cn.jicl.mapper"/>
    </mappers>
    ```

## 五、SQL映射文件配置详解

### 1、可配置信息总览

* cache – 对给定命名空间的缓存配置。
* cache-ref – 对其他命名空间缓存配置的引用。
* resultMap – 是最复杂也是最强大的元素，用来描述如何从数据库结果集中来加载对象。
* sql – 可被其他语句引用的可重用语句块。
* insert – 映射插入语句
* update – 映射更新语句
* delete – 映射删除语句
* select – 映射查询语句

### 2、各个配置详解

#### 2.1、insert, update 和 delete

​																		**Insert, Update, Delete 元素的属性**

| 属性               | 描述                                                         |
| :----------------- | :----------------------------------------------------------- |
| `id`               | 命名空间中的**唯一标识符**，可被用来代表这条语句。           |
| `parameterType`    | 将要传入语句的参数的完全限定类名或别名。这个属性是可选的，因为 **MyBatis 可以通过类型处理器推断出具体传入语句的参数**，默认值为未设置（unset）。 |
| ~~`parameterMap`~~ | ~~这是引用外部 parameterMap 的已经被废弃的方法。请使用内联参数映射和 parameterType 属性。~~ |
| `flushCache`       | 将其设置为 true 后，只要语句被调用，都会导致本地缓存和二级缓存被清空，默认值：true（对于 insert、update 和 delete 语句）。 |
| `timeout`          | 这个设置是在抛出异常之前，驱动程序等待数据库返回请求结果的秒数。默认值为未设置（unset）（依赖驱动）。 |
| `statementType`    | STATEMENT，PREPARED 或 CALLABLE 的一个。这会让 MyBatis 分别使用 Statement，PreparedStatement 或 CallableStatement，默认值：PREPARED。 |
| `useGeneratedKeys` | （仅对 insert 和 update 有用）这会令 MyBatis 使用 JDBC 的 getGeneratedKeys 方法来取出由数据库内部生成的主键（比如：像 MySQL 和 SQL Server 这样的关系数据库管理系统的自动递增字段），默认值：false。 |
| `keyProperty`      | （仅对 insert 和 update 有用）唯一标记一个属性，MyBatis 会通过 getGeneratedKeys 的返回值或者通过 insert 语句的 selectKey 子元素设置它的键值，默认值：未设置（`unset`）。如果希望得到多个生成的列，也可以是逗号分隔的属性名称列表。 |
| `keyColumn`        | （仅对 insert 和 update 有用）通过生成的键值设置表中的列名，这个设置仅在某些数据库（像 PostgreSQL）是必须的，当主键列不是表中的第一列的时候需要设置。如果希望使用多个生成的列，也可以设置为逗号分隔的属性名称列表。 |
| `databaseId`       | 如果配置了数据库厂商标识（databaseIdProvider），MyBatis 会加载所有的不带 databaseId 或匹配当前 databaseId 的语句；如果带或者不带的语句都有，则不带的会被忽略。 |

* useGeneratedKeys和keyProperty ：如果你的数据库支持自动生成主键的字段（比如 MySQL 和 SQL Server），那么你可以设置 useGeneratedKeys=”true”，然后再把 keyProperty 设置到目标属性上就 OK 了

  ```xml
  <insert id="insertAuthor" useGeneratedKeys="true"
          keyProperty="id">
      insert into Author (username,password,email,bio)
      values (#{username},#{password},#{email},#{bio})
  </insert>
  ```

* selectKey：对于不支持自动生成类型的数据库或可能不支持自动生成主键的 JDBC 驱动，MyBatis 有另外一种方法来生成主键

  ```xml
  <insert id="insertAuthor">
      <selectKey keyProperty="id" resultType="int" order="BEFORE">
          select MAX(id)+1 from Author
      </selectKey>
      insert into Author
      (id, username, password, email,bio, favourite_section)
      values
      (#{id}, #{username}, #{password}, #{email}, #{bio}, #{favouriteSection,jdbcType=VARCHAR})
  </insert>
  ```

  ​                                       							 **selectKey 元素的属性**

| 属性            | 描述                                                         |
| :-------------- | :----------------------------------------------------------- |
| `keyProperty`   | selectKey 语句结果应该被设置的目标属性。如果希望得到多个生成的列，也可以是逗号分隔的属性名称列表。 |
| `keyColumn`     | 匹配属性的返回结果集中的列名称。如果希望得到多个生成的列，也可以是逗号分隔的属性名称列表。 |
| `resultType`    | 结果的类型。MyBatis 通常可以推断出来，但是为了更加精确，写上也不会有什么问题。MyBatis 允许将任何简单类型用作主键的类型，包括字符串。如果希望作用于多个生成的列，则可以使用一个包含期望属性的 Object 或一个 Map。 |
| `order`         | 这可以被设置为 BEFORE 或 AFTER。如果设置为 BEFORE，那么它会首先生成主键，设置 keyProperty 然后执行插入语句。如果设置为 AFTER，那么先执行插入语句，然后是 selectKey 中的语句 - 这和 Oracle 数据库的行为相似，在插入语句内部可能有嵌入索引调用。 |
| `statementType` | 与前面相同，MyBatis 支持 STATEMENT，PREPARED 和 CALLABLE 语句的映射类型，分别代表 Statement、PreparedStatement 和 CallableStatement 类型。 |

#### 2.2、select

查询语句是 MyBatis 中最常用的元素之一

##### 2.2.1、Select 元素的属性

| 属性            | 描述                                                         |
| :-------------- | :----------------------------------------------------------- |
| `id`            | 在命名空间中唯一的标识符，可以被用来引用这条语句。           |
| `parameterType` | 将会传入这条语句的参数类的完全限定名或别名。这个属性是可选的，因为 MyBatis 可以通过类型处理器（TypeHandler） 推断出具体传入语句的参数，默认值为未设置（unset）。 |
| parameterMap    | 这是引用外部 parameterMap 的已经被废弃的方法。请使用内联参数映射和 parameterType 属性。 |
| `resultType`    | 从这条语句中返回的期望类型的类的完全限定名或别名。 注意如果返回的是集合，那应该设置为集合包含的类型，而不是集合本身。可以使用 resultType 或 resultMap，但不能同时使用。 |
| `resultMap`     | 外部 resultMap 的命名引用。结果集的映射是 MyBatis 最强大的特性，如果你对其理解透彻，许多复杂映射的情形都能迎刃而解。可以使用 resultMap 或 resultType，但不能同时使用。 |
| `flushCache`    | 将其设置为 true 后，只要语句被调用，都会导致本地缓存和二级缓存被清空，默认值：false。 |
| `useCache`      | 将其设置为 true 后，将会导致本条语句的结果被二级缓存缓存起来，默认值：对 select 元素为 true。 |
| `timeout`       | 这个设置是在抛出异常之前，驱动程序等待数据库返回请求结果的秒数。默认值为未设置（unset）（依赖驱动）。 |
| `fetchSize`     | 这是一个给驱动的提示，尝试让驱动程序每次批量返回的结果行数和这个设置值相等。 默认值为未设置（unset）（依赖驱动）。 |
| `statementType` | STATEMENT，PREPARED 或 CALLABLE 中的一个。这会让 MyBatis 分别使用 Statement，PreparedStatement 或 CallableStatement，默认值：PREPARED。 |
| `resultSetType` | FORWARD_ONLY，SCROLL_SENSITIVE, SCROLL_INSENSITIVE 或 DEFAULT（等价于 unset） 中的一个，默认值为 unset （依赖驱动）。 |
| `databaseId`    | 如果配置了数据库厂商标识（databaseIdProvider），MyBatis 会加载所有的不带 databaseId 或匹配当前 databaseId 的语句；如果带或者不带的语句都有，则不带的会被忽略。 |
| `resultOrdered` | 这个设置仅针对嵌套结果 select 语句适用：如果为 true，就是假设包含了嵌套结果集或是分组，这样的话当返回一个主结果行的时候，就不会发生有对前面结果集的引用的情况。 这就使得在获取嵌套的结果集的时候不至于导致内存不够用。默认值：`false`。 |
| `resultSets`    | 这个设置仅对多结果集的情况适用。它将列出语句执行后返回的结果集并给每个结果集一个名称，名称是逗号分隔的。 |

##### 2.2.2、关于传参问题（使用增删改查sql）

* 单个参数（基本类型）
  * 使用：#{任意写}

* 多个参数（基本类型）

  * 使用
    * 参数不加注解情况：#{0}，#{1}（或#{param1}，#{param2}）
    * 参数加注解情况（Param直接）：使用注解中的值引用，例:@Param("id")String UserId-->#{id}

  * 当传递多个参数时候，Mybatis会自动将这些参数封装到一个map中，如果不指定param注解，key默认为参数的索引，指定则取注解的值为key

* 单个map
  * 使用：#{key}
* 单个pojo（Javabean）
  * 使用：#{pojo的属性名}

* 多个参数（混合类型）
  * method(@Param("id")Integer userId,String name,UserBean userBean,@Param("paramMap")Map<String,Object> map);
    * 各个位置取参数
      * 取userId：#{id}
      * 取name：#{param2}
      * 取userBean中属性age：#{param3.age}
      * 取map中的k1：#{paramMap.k1}

* 总结：对于多个参数，不管类型是什么，Mybatis都会封装到一个map中，根据注解或参数索引取key封装。

##### 2.2.3、#{}和${}的区别

* #{}：参数预编译方式，参数的位置使用？替代，参数后来都是预编译设置进去得我，安全，不会有sql注入问题（常用）
* ${}：直接和sql进行拼串，存在sql注入问题（一般用于动态拼接表名、order by等不支持预编译的位置）

##### 2.2.4、查询结果集的简单封装

* 返回单行记录

  * 使用map封装

    ```xml
    <!--返回结果封装到一个map中，其中key为列名，value为列值-->
    <select id="selectByIdReturnMap" resultType="map">
        select 
        * 
        from some_table
        where id = #{id}
    </select>
    ```

    

  * 使用Javabean封装

    ```xml
    <!--返回结果封装到一个map中，属性名和列名保持一致才可封装进来，否则为null-->
    <select id="selectByIdReturnPojo" resultType="cn.jicl.pojo.User">
        select 
        * 
        from some_table
        where id = #{id}
    </select>
    ```

* 返回多条记录：返回结果的类型始终为单个要素的类型

  * 使用map封装

    ```xml
    <!--xml中-->
    <!--resultType不为map，始终为单个要素的类型，即集合中元素的类型-->
    <select id="selectAllReturnMap" resultType="cn.jicl.pojo.User">
        select 
        * 
        from some_table
    </select>
    ```

    ```java
    //mapper接口类中
    //MapKey指定使用哪个字段作为key
    @MapKey("id")
    Map<String,User> selectAllReturnMap();
    ```

  * 使用List封装

    ```xml
    <!--xml中-->
    <!--resultType不为list，始终为单个要素的类型，即集合中元素的类型-->
    <select id="selectAllReturnMap" resultType="cn.jicl.pojo.User">
        select 
        * 
        from some_table
    </select>
    ```

    ```java
    //mapper接口类中
    List<User> selectAllReturnMap();
    ```

##### 2.2.5、高级结果集映射（resultMap）

###### 2.2.5.1、可配置总览

* constructor - 用于在实例化类时，注入结果到构造方法中
  * idArg - ID 参数；标记出作为 ID 的结果可以帮助提高整体性能
  * arg - 将被注入到构造方法的一个普通结果
* id – 一个 ID 结果；标记出作为 ID 的结果可以帮助提高整体性能
* result – 注入到字段或 JavaBean 属性的普通结果
* association – 一个复杂类型的关联；许多结果将包装成这种类型
  * 嵌套结果映射 – 关联本身可以是一个 resultMap 元素，或者从别处引用一个
* collection – 一个复杂类型的集合
  * 嵌套结果映射 – 集合本身可以是一个 resultMap 元素，或者从别处引用一个
* discriminator – 使用结果值来决定使用哪个 resultMap
  * case – 基于某些值的结果映射
    * 嵌套结果映射 – case 本身可以是一个 resultMap 元素，因此可以具有相同的结构和元素，或者从别处引用一个

​                                                                        **ResultMap 的属性列表**

| 属性          | 描述                                                         |
| :------------ | :----------------------------------------------------------- |
| `id`          | 当前命名空间中的一个唯一标识，用于标识一个结果映射。         |
| `type`        | 类的完全限定名, 或者一个类型别名（关于内置的类型别名，可以参考上面的表格）。 |
| `autoMapping` | 如果设置这个属性，MyBatis将会为本结果映射开启或者关闭自动映射。 这个属性会覆盖全局的属性 autoMappingBehavior。默认值：未设置（unset）。 |

###### 2.2.5.2、当表中列名和封装的pojo的属性名不对应时，可以自定义结果集

```xml
<!--id:唯一标识，方便后续引用；type：需要做自定义映射的pojo-->
<resultMap id="userResultMap" type="cn.jicl.pojo.User">
    <!--id标签:指定主键的对应规则
		result标签：指定普通列的对应规则
			property：pojo中的属性名
			column：数据库表中的列名
	-->
    <id property="id" column="user_id" />
    <result property="username" column="user_name"/>
    <result property="password" column="hashed_password"/>
</resultMap>

<!--使用resultMap自定义结果集-->
<select id="selectAllReturnResultMap" resultMap="userResultMap">
    select 
    * 
    from some_table
</select>
```

###### 2.2.5.3、使用级联属性关联

* 一对一

  * Javabean

    ```java
    //锁对象
    public Class Lock{
        private int id;
        private String lockName;
        //关联的key对象（一对多）
        private Key key;
    }
    
    //钥匙对象
    public Class Key{
        private int id;
        private String keyName;
    }
    ```

  * 表结构

    ```sql
    --锁
    CREATE TABLE `t_lock`(
       `id` INT UNSIGNED AUTO_INCREMENT,
       `lock_name` VARCHAR(100) NOT NULL,
       PRIMARY KEY ( `id` )
    );
    --钥匙
    CREATE TABLE `t_key`(
       `id` INT UNSIGNED AUTO_INCREMENT,
       `key_name` VARCHAR(100) NOT NULL,
       `lock_id` INT NOT NULL,--关联锁id
       PRIMARY KEY ( `id` )
    );
    ```

    

  * sql映射文件

    ```xml
    <!--自定义级联属性关联结果集-->
    <!--写法一-->
    <resultMap id="LockResultMap1" type="cn.jicl.pojo.Lock">
        <id property="lock_id" column="id" />
        <result property="lockName" column="lock_name"/>
        <result property="key.id" column="key_id"/>
        <result property="key.keyName" column="key_name"/>
    </resultMap>
    
    <!--写法二-->
    <resultMap id="LockResultMap2" type="cn.jicl.pojo.Lock">
        <id property="lock_id" column="id" />
        <result property="lockName" column="lock_name"/>
        <!--使用Mybatis提供的association标签
    			property：pojo中的属性名
      			javaType：属性的Java类型
    	-->
        <association property="key" javaType="cn.jicl.pojo.Key">
            <!--配置同普通属性映射-->
            <id property="id" column="key_id"/>
            <result property="keyName" column="key_name"/>
        </association>
    </resultMap>
    
    <!--使用resultMap自定义级联属性关联结果集-->
    <select id="selectAllWithAssociation" resultMap="LockResultMap1">
        select 
            l.lock_id,
            l.lock_name,
            k.id key_id
            k.key_name
        from t_lock l
        left join t_key k
        on l.id=k.lock_id
    </select>
    ```

* 一对多

  * Javabean 

    ```java
    //锁对象
    public Class Lock{
        private int id;
        private String lockName;
        //关联的key对象（一对多）
        private List<key> keys;
    }
    
    //钥匙对象
    public Class Key{
        private int id;
        private String keyName;
    }
    ```

  * 建表sql语句同上

  * sql映射文件

    ```xml
    <!--一对多映射结果集-->
    <resultMap id="LockResultMap3" type="cn.jicl.pojo.Lock">
        <id property="lock_id" column="id" />
        <result property="lockName" column="lock_name"/>
        <!--使用Mybatis提供的collection标签
    			property：pojo中的属性名
      			ofType：集合内元素的Java类型
    	-->
        <collection property="keys" ofType="cn.jicl.pojo.Key">
            <!--配置同普通属性映射-->
            <id property="id" column="key_id"/>
            <result property="keyName" column="key_name"/>
        </collection>
    </resultMap>
    
    <!--使用resultMap自定义级联属性关联结果集-->
    <select id="selectAllWithCollection" resultMap="LockResultMap3">
        select 
            l.lock_id,
            l.lock_name,
            k.id key_id
            k.key_name
        from t_lock l
        left join t_key k
        on l.id=k.lock_id
    </select>
    ```

###### 2.2.5.4、select分步查询（待补充，对于视频的258-260）

* 使用上述例子

  * LockMapper.xml

    ```xml
    <mapper namespace="cn.jicl.mapper.LockMapper">
        <!--select分步查询-->
        <resultMap id="LockResultMap4" type="cn.jicl.pojo.Lock">
        <id property="lock_id" column="id" />
        <result property="lockName" column="lock_name"/>
        <!--分布查询
    			property：pojo属性名
    			select：告诉Mybatis取调用一个查询语句
    			column：指定将哪一列作为参数传递过去
    	-->
        <association property="keys" select="cn.jicl.mapper.KeyMapper.selectKeyByLockId"
                     column="id">
            <!--配置同普通属性映射-->
            <id property="id" column="key_id"/>
            <result property="keyName" column="key_name"/>
        </association>
    </resultMap>
        
        
        <select id="selectKeyById" resultMap="LockResultMap4">
            select 
            *
            from t_lock
            where id=#{id}
        </select>
        
        
        
    </mapper>
    ```

  * KeyMapper.xml

    ```xml
    <mapper namespace="cn.jicl.mapper.KeyMapper">
        <select id="selectKeyByLockId">
            select 
            *
            from t_key
            where lock_id=#{lockId}
        </select>
    </mapper>
    ```

###### 2.2.5.5、延迟加载和按需加载

* 就2.2.5.4例子来说：查出lock会顺便带出对于的keys信息，但是如果代码中没有用到keys，则会导致查询出的数据过多，造成数据库性能问题。可不可以按照需要加载，需要使用到keys时才调用查询keys方法，否则不查询。

* Mybatis提供了延迟加载和按需加载机制，只需在全局配置文件则配置两个信息

  ```xml
  <!--开启延迟加载开关-->
  <setting name="lazyLoadingEnabled" value="true"/>
  <!--启动按需加载-->
  <setting name="aggressiveLazyLoading" value="false"/>
  ```

#### 2.3、动态sql标签（待补充）

## 六、Mybatis的缓存机制

### 6.1、缓存

* 缓存技术是一种“以空间换时间”的设计理念，是利用内存空间资源来提高数据检索速度的有效手段之一。Mybatis包含一个非常强大的查询缓存特性，可以非常方便地配置和定制。

### 6.2、Mybatis缓存

* MyBatis将数据缓存设计成两级结构，分为一级缓存、二级缓存

  ![Mybatis缓存图](D:\GitRepositories\MyBatis-Demo\note\Mybatis缓存图.PNG)

#### 6.2.1、一级缓存

​	![Mybatis一级缓存图](D:\GitRepositories\MyBatis-Demo\note\Mybatis一级缓存图.PNG)

##### 6.2.1.1、一级缓存简介

* 一级缓存基于PrepetualCache的HashMap本地缓存，其存储作用域为Session，位于表示一次数据库会话的**SqlSession**对象之中

* 每当我们使用MyBatis开启一次和数据库的会话，MyBatis会创建出一个SqlSession对象表示一次数据库会话。在对数据库的一次会话中，我们有可能会反复地执行完全相同的查询语句，如果不采取一些措施的话，每一次查询都会查询一次数据库，而我们在极短的时间内做了完全相同的查询，那么它们的结果极有可能完全相同，由于查询一次数据库的代价很大，这有可能造成很大的资源浪费

* 为了解决这一问题，减少资源的浪费，MyBatis会在表示会话的**SqlSession**对象中建立一个简单的缓存，将每次查询到的结果结果缓存起来，当下次查询的时候，如果判断先前有个完全一样的查询，会直接从缓存中直接将结果取出，返回给用户，不需要再进行一次数据库查询了。

* 一级缓存是MyBatis内部实现的一个特性，用户不能配置，默认情况下自动支持的缓存，用户没有定制它的权利（不过这也不是绝对的，可以通过开发插件对它进行修改）。

  ```java
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
      System.out.println(user == user2);//返回true
      System.out.println("=======================================");
      //提交事务，会清空sqlSession的一级缓存。避免脏毒
      sqlSession.commit();
      //第三次调用查询，因为一级缓存被清空，所有回去数据库中查询
      Users user3 = usersMapper.selectByPrimaryKey(2);
      System.out.println(user3);
      System.out.println(user==user3);//返回false
      sqlSession.close();
  }
  ```
