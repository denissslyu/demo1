package com.yzt.zhmp.dao;


import com.yzt.zhmp.beans.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author
 */
@Mapper
@Component("UserLoginDao")
public interface UserLoginDao {

    /**
     * 查询用户
     *
     * @param user 封装参数的实体
     * @return 表中查询的数据
     */
    @Select("SELECT * FROM d_user WHERE name=#{name} and password=#{password}")
    User login(User user);

    /**
     * 根据用户id查询所属部门
     *
     * @param userid 用户id
     * @return 返回部门id
     */
    @Select("SELECT deptID FROM d_deptUser WHERE usrID=#{userid}")
    Integer selectDeptUserbyUserid(int userid);

    /**
     * 登入查询  根据登入用户id查询登入角色
     *
     * @param userid 用户id
     * @return
     */
    @Select("SELECT fieldName FROM b_fileid b1, b_fileUser b2 WHERE b2.fldID = b1.fldID AND b2.usrID = #{userid}")
    List<String> rolelogin(int userid);


    /**
     * 查询登入用户角色
     *
     * @param userid 用户id
     * @return
     */
    @Select("SELECT fldID FROM b_fileUser WHERE usrID=#{userid}")
    List<String> selectFileIdByUserid(int userid);

    /**
     * 查询用户所拥有的建筑
     *
     * @param userid 用户id
     * @return
     */
    @Select("SELECT buID FROM b_fileUser WHERE usrID=#{userid}")
    List<String> selectBuidbyUserId(int userid);

    /**
     * 验证通行证(2小时之内)
     *
     * @param passport 通行证
     * @return
     */
    @Select("SELECT usrID FROM d_passport WHERE passport=#{passport} "
            + "AND createtime>=(NOW() - interval 2 hour);")
    Integer selectUseridbyPassport(String passport);

    /**
     * 生成通行证
     *
     * @param passport 通行证
     * @return
     */
    @Insert("INSERT INTO d_passport (passport,usrID,createtime) " +
            "values(#{passport},#{userid},NOW())")
    void generatePassport(@Param("passport") String passport,@Param("userid") int userid);

    /**
     * 注销通行证
     *
     * @param userid 用户id
     * @return
     */
    @Delete("DELETE FROM d_passport WHERE usrID=#{userid}"
            + "AND createtime>=(NOW() - interval 2 hour);")
    void deletePassportbyUserid(int userid);

    /**
     * 通过userid查询user实体
     *
     * @param userid 用户id
     * @return
     */
    @Select("SELECT * FROM d_user WHERE usrID=#{userid}")
    User selectUserbyUserid(int userid);

    @Select("SELECT usrID FROM d_user WHERE name=#{name}")
    List<String> selectUseridbyName(String name);
}
