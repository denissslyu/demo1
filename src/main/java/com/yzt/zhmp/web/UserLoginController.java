package com.yzt.zhmp.web;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.yzt.zhmp.beans.Cbuilding;
import com.yzt.zhmp.beans.User;
import com.yzt.zhmp.service.BackstageService;
import com.yzt.zhmp.service.CollectionSystemService;
import com.yzt.zhmp.service.SystemService;
import com.yzt.zhmp.service.UserService.UserLoginService;
import com.yzt.zhmp.utils.MD5Utils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import sun.security.provider.MD5;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wang
 */
@Controller
public class UserLoginController {

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private BackstageService backstageService;

    @Autowired
    private CollectionSystemService collectionSystemService;


    /**
     * 登入后台管理功能,从session中获取登录的用户
     *
     * @param session
     * @return
     */
    @RequestMapping("/pananHouTai")
    public String userLogin(HttpSession session, Model model) {
        User existUser = (User) session.getAttribute("existUser1");
        //查找用户对应的部门id
        Integer deptId = userLoginService.selectByUserid(existUser.getUsrid());
        if (deptId == 111) {
            List systemList = systemService.selectSystem();
            model.addAttribute("systemList", systemList);
        } else {
            List policeSystem = systemService.selectPoliceSystem();
            model.addAttribute("policeSystem", policeSystem);
        }
        model.addAttribute("deptid", deptId);
        //把部门id存到session中以便在其他页面判断
        session.setAttribute("deptid", deptId);
        return "WEB-INF/navigation/newnavigation";
    }


    /**
     * 磐安县登入查询用户角色返回首页(手机页面)
     *
     * @param name
     * @param password
     * @param session
     * @return
     */
    @RequestMapping("/usersLogin")
    public String usersLogin(String name, String password, HttpSession session, Model model, HttpServletRequest request) {
        User user = new User();
        user.setPassword(MD5Utils.md5(password));
        user.setName(name);
        User existUser1 = userLoginService.login(user);
        if (existUser1 == null) {
            model.addAttribute("error", "账号或密码错误");
            return "WEB-INF/login/login";
        }
        session.setAttribute("existUser1", existUser1);
        //查看角色权限字段
        Integer usrId = existUser1.getUsrid();
        List<String> fileName = userLoginService.rolelogin(usrId);
        //查看此用户是否有从此建筑权限
        List<String> buid = userLoginService.selectBuidbyUserId(usrId);
        //声明字段  为1时用户和建筑有关联
        int bfid = 0;
        for (String s : buid) {
            if ("17".equals(s)) {
                bfid = 1;
            }
        }
        session.setAttribute("bfid", bfid);

        //暂未用到  查看用户角色id
        List<String> fildId = userLoginService.selectFileIdByUserid(usrId);
        session.setAttribute("fildId", fildId);

        //查找用户对应的部门id
        Integer deptid = userLoginService.selectByUserid(usrId);
        if (deptid == null) {
            model.addAttribute("error", "请使用部门账号登录");
            return "WEB-INF/login/login";
        }
        String deptName;
        switch (deptid) {
            case 111:
                deptName = "民政";
                break;
            case 222:
                deptName = "公安";
                break;
            case 333:
                deptName = "教育";
                break;
            default:
                deptName = "";
                break;
        }
        model.addAttribute("deptName", deptName);
        model.addAttribute("deptid", deptid);

        //显示农户信息
        //Cbuilding cbuilding = collectionSystemService.selectBuildingByid(17);
        //model.addAttribute("building", cbuilding);

        //生成passport
        session.setAttribute("passport", userLoginService.generatePassport(usrId));

        session.setAttribute("existUser1", existUser1);

        return "WEB-INF/a/newsystem";
    }

    @RequestMapping("/checkPassport")
    public Integer checkPassport(String passport){
        if(null != userLoginService.selectUseridbyPassport(passport)) {
            return userLoginService.selectUseridbyPassport(passport);
        }
        return 0;
    }

    @RequestMapping("/deletePassport")
    public void deletePassport(Integer usrId){
        userLoginService.deletePassportbyUserid(usrId);
    }


    @RequestMapping("/getUserInfobyUserid")
    public void getUserInfobyUserid(int userid, HttpServletResponse response)throws IOException {
        User user = userLoginService.selectUserbyUserid(userid);
        response.getWriter().write(user.toString());
    }

    @RequestMapping("/loginOut")
    public String loginOut(HttpServletRequest request) {
        request.getSession().removeAttribute("existUser1");
        request.getSession().removeAttribute("passport");
        return "WEB-INF/a/newsystem";
    }

    @RequestMapping("/getVerificationCode")
    public void getVerifivationCode(String telephone, HttpServletResponse response) throws ClientException, IOException {
        String vcode = RandomStringUtils.randomNumeric(4);
        SendSmsResponse sendSmsResponse = sendSms(telephone,vcode);
        if(null == sendSmsResponse){
            //catch clientexception
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg","获取验证码失败");
            response.getWriter().write(jsonObject.toString());
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("VCode",vcode);
        jsonObject.put("ResultCode",sendSmsResponse.getCode());
        jsonObject.put("Message",sendSmsResponse.getMessage());
        jsonObject.put("RequestId",sendSmsResponse.getRequestId());
        jsonObject.put("BizId",sendSmsResponse.getBizId());
        response.getWriter().write(jsonObject.toString());
    }

    //阿里短信api
    public static SendSmsResponse sendSms(String telephone,String vcode) throws ClientException {
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAIt2ATSvJaZlQC", "EDuw7M6e4dg5C02Fi8ZRHATi98UB8s");
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Dysmsapi", "dysmsapi.aliyuncs.com");
        IAcsClient acsClient = new DefaultAcsClient(profile);
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(telephone);
        request.setSignName("智慧门牌");
        request.setTemplateCode("SMS_150578100");
        request.setTemplateParam("{\"code\":\""+ vcode +"\"}");
        request.setOutId("yourOutId");
        try {
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            return sendSmsResponse;
        }catch (ClientException e){
            return null;
        }
    }

    @RequestMapping("/userRegist")
    public void userRegist(String telephone,String password,HttpServletResponse response) throws IOException {
        if(null != userLoginService.selectUseridbyName(telephone)){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg","注册失败，此账号已被使用。");
            response.getWriter().write(jsonObject.toString());
            return;
        }
        User user = new User();
        user.setName(telephone);
        user.setPassword(MD5Utils.md5(password));
        backstageService.registered(user);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg","注册成功，正在跳转到登录界面。");

    }
}
