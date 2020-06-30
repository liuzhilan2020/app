package cn.appsys.controller.devlop;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.aspectj.apache.bcel.classfile.Constant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.appsys.pojo.DevUser;
import cn.appsys.service.developer.DevUserService;
import cn.appsys.tools.Constants;

@Controller
@RequestMapping(value="/dev")
public class DevUserController {
	@Resource
	private DevUserService service;
	private Logger logger=Logger.getLogger(DevUserController.class);
	
	@RequestMapping(value="/login")
	public String login(){
		logger.info("login===========");
		return "devlogin";
	}
	
	@RequestMapping(value="/dologin",method=RequestMethod.POST)
	public String dologin(@RequestParam String devCode,
			@RequestParam String devPassword,
			HttpSession session,HttpServletRequest request){
		logger.info("dologin===========");
		DevUser user=null;
		//调用sercvice方法进行用户匹配  根据userCode获取到用户实例
		user=service.login(devCode, devPassword);
		if(null!=user){//登录成功
			//放入session
			session.setAttribute(Constants.DEV_USER_SESSION, user);
			//页面跳转到main.jsp
			return "redirect:/dev/flatform/main";
		}else{//登陆失败
			//保留在devlogin.jsp  提示错误信息
			request.setAttribute("error", "用户名或密码不正确");
			return "devlogin";
		}
	}

	@RequestMapping(value="/flatform/main")
	public String main(HttpSession session){
		if(session.getAttribute(Constants.DEV_USER_SESSION)==null){//判断会话USER_SESSION如果为空，则返回到登录页面
			return  "redirect:/dev/login";
		}
		//如果不为空，则跳到到主页
		return  "developer/main";
	}
	
	//注销
	@RequestMapping(value="/loginout")
	public String logionout(HttpSession session){
		//清除session
		session.removeAttribute(Constants.DEV_USER_SESSION);
		return "devlogin";
	} 

}
