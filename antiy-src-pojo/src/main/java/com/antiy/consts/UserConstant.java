package com.antiy.consts;

/**
 * @author wangqian
 * created on 2019-07-25
 * @version 1.0.0
 * @program kbms-parent
 * @description 用户相关常量
 */
public interface UserConstant {
    String USERNAME_EXIST = "用户名已存在";
    String USER_NOT_EXIST = "用户不存在";
    String USER_FORBIDDEN = "用户已被禁用";
    String USER_OLD_PASSEORD_ERROR = "旧密码错误";

    /**
     * 用户类型-系统用户
     */
    Integer USER_TYPE_SYSTEM = 1;

    /**
     * 用户类型-外部用户
     */
    Integer USER_TYPE_OUTER = 2;

    /**
     * 用户状态-正常
     */
    Integer USER_STATUS_NORMAL = 1;

    /**
     * 用户状态-锁定
     */
    Integer USER_STATUS_LOCKED = 2;

    /**
     * 用户状态-禁用
     */
    Integer USER_STATUS_FORBIDDEN = 0;
    /**
     * 角色-超级管理员
     */
    Integer ROLE_TYPE_SUPERADMIN = 1;
    /**
     * 角色-普通管理员
     */
    Integer ROLE_TYPE_ADMIN = 2;
    /**
     * 角色-审核员
     */
    Integer ROLE_TYPE_SH = 3;
    /**
     * 角色-普通用户
     */
    Integer ROLE_TYPE_NORMAL = 4;

    /**
     * token过期时间半小时
     */
    Long EXPIRE = 60 * 30L * 1000;
}
