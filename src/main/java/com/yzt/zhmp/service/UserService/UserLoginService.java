package com.yzt.zhmp.service.UserService;


import com.yzt.zhmp.beans.User;

import java.util.List;

/**
 * @author wang
 */
public interface UserLoginService {

    /**
     * 用户登录
     *
     * @param user
     * @return
     */
    User login(User user);

    /**
     * 查询部门用户
     */
    Integer selectByUserid(int userid);

    /**
     * 登入查询  根据登入用户id查询登入角色
     *
     * @param userid
     * @return
     */
    List<String> rolelogin(int userid);

    /**
     * 查询登入用户角色
     *
     * @param userid
     * @return
     */
    List<String> selectFileIdByUserid(int userid);

    /**
     * 查询用户所拥有的建筑
     *
     * @param userid
     * @return
     */
    List<String> selectBuidbyUserId(int userid);

    /**
     * 验证通行证(2小时之内)
     *
     * @param passport 通行证
     * @return
     */
    Integer selectUseridbyPassport(String passport);

    /**
     * 生成通行证
     *
     * @param userid
     * @return
     */
    String generatePassport(int userid);

    /**
     * 注销通行证
     *
     * @param userid
     * @return
     */
    void deletePassportbyUserid(int userid);

    /**
     * 通过userid查询user实体
     *
     * @param userid
     * @return
     */
    User selectUserbyUserid(int userid);

    /**
     * 通过name查询userid
     *
     * @param name
     * @return
     */
    List<String> selectUseridbyName(String name);

    /**
     * 生成手机验证码
     *
     * @param vcode,telephone
     * @return
     */
    void generateVerificationCode(String vcode,String telephone);

    /**
     * 查询手机验证码记录
     *
     * @param vcode,telephone
     * @return
     */
    List<String> checkVerificationCode(String vcode,String telephone);

}

