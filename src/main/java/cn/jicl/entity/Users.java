package cn.jicl.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: xianzilei
 * @Date: 2019/8/15 18:52
 * @Description: 用户信息实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users implements Serializable {
    private int id;
    private String name;
    private String password;
    private String email;
    private Date birthday;
}
