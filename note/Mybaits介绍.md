# Mybatis介绍

[TOC]

## 一、简介

> MyBatis 本是apache的一个开源项目iBatis, 2010年这个项目由apache software foundation 迁移到了google code，并且改名为MyBatis。是一个基于Java的持久层框架（SQL映射框架）。

## 二、为什么要使用MyBatis

* 传统JDBC中，我们除了需要自己提供 SQL 外，还必须操作 Connection、Statment、ResultSet，不仅如此，为了访问不同的表，不同字段的数据，我们需要些很多雷同模板化的**繁琐又枯燥**。

* 使用了 **MyBatis** 之后，**只需要提供 SQL 语句就好了**，其余的诸如：建立连接、操作 Statment、ResultSet，处理 JDBC 相关异常等等都可以交给 MyBatis 去处理，我们的**关注点于是可以就此集中在 SQL 语句上**，关注在增删改查这些操作层面上。

*  MyBatis 支持使用简单的 XML 或注解来配置和映射原生信息，将接口和 Java 的 POJOs(Plain Old Java Objects,普通的 Java对象)映射成数据库中的记录。

* 相比较hibernate框架（ORM对象关系映射框架）

  * hibernate缺点：
    * 全自动化框架，很难对SQL进行细致的优化
    * 对于复杂SQL支持较为吃力（自定义SQL反而破坏了Hibernate封装以及简洁性）
    * 全映射框架，很难做部分字段的映射
    * 学习成本高，难精通

  * MyBatis优点：
    * 半自动框架，支持对SQL的更为细致的优化
    * 可以方便进行部分字段的映射，减少数据库压力
    * 上手容易，学习成本低

## 三、项目搭建

### 1、导入依赖包（以3.4.2为例）

```xml
<!--导入mybatis依赖包-->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.4.2</version>
</dependency>
```

