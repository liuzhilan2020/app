package cn.appsys.service.developer;

import cn.appsys.pojo.DevUser;

public interface DevUserService {
	/**
	 * 登录方法
	 * @param devCode
	 * @param password
	 * @return
	 */
	public DevUser login(String devCode, String password);
}
