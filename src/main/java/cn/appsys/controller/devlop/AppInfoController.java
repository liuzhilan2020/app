package cn.appsys.controller.devlop;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;

import cn.appsys.pojo.AppCategory;
import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.AppVersion;
import cn.appsys.pojo.DataDictionary;
import cn.appsys.pojo.DevUser;
import cn.appsys.service.developer.AppCategoryService;
import cn.appsys.service.developer.AppInfoService;
import cn.appsys.service.developer.AppVersionService;
import cn.appsys.service.developer.DataDictionaryService;
import cn.appsys.tools.Constants;
import cn.appsys.tools.PageSupport;

@Controller
@RequestMapping(value="/dev/flatform/app")

public class AppInfoController {
	@Resource
	private AppInfoService appInfoService;
	@Resource
	private DataDictionaryService dataDictionaryService;
	@Resource
	private AppCategoryService appCategoryService;
	@Resource
	private AppVersionService appVersionService;
	
	private Logger logger=Logger.getLogger(AppInfoController.class);
	
	@RequestMapping(value="/list")
	public String getAppInfoList(Model model,HttpSession session,
			@RequestParam(value="querySoftwareName",required=false) String querySoftwareName,
			@RequestParam(value="queryStatus",required=false) String _queryStatus,
			@RequestParam(value="queryCategoryLevel1",required=false) String _queryCategoryLevel1,
			@RequestParam(value="queryCategroyLevel2",required=false) String _queryCategoryLevel2,
			@RequestParam(value="queryCategroyLevel3",required=false) String _queryCategoryLevel3,
			@RequestParam(value="queryFlatformId",required=false) String _queryFlatformId,
			@RequestParam(value="pageIndex",required=false) String pageIndex){
		logger.info("querySoftwareName============="+querySoftwareName);
		logger.info("queryStatus============="+_queryStatus);
		logger.info("queryCategroyLevel1============="+_queryCategoryLevel1);
		logger.info("_queryCategroyLevel2============="+_queryCategoryLevel2);
		logger.info("queryCategroyLevel3============="+_queryCategoryLevel1);
		logger.info("_queryFlatformId============="+_queryFlatformId);
		logger.info("pageIndex============="+pageIndex);
		//获取当前登录人的信息  获取当前人的id
		Integer devId=((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId();
		List<AppInfo> appInfoList=null;
		List<DataDictionary> statusList=null;
		List <DataDictionary> flatFormList=null;
		//列出一级列表 二三级列表
		List<AppCategory> categoryLevel1List=null;//categroyLevel1List--调用service的方法
		List<AppCategory> categoryLevel2List=null;//categroyLevel2List categroyLevel3List通过ajax方法
		List<AppCategory> categoryLevel3List=null;
		//页面容量
		int pageSize=Constants.pageSize;
		//当前页码
		Integer currentPageNo=1;
		if(pageIndex!=null){//前台传过来的pageIndex，将pageIndex的值赋值给currentPageNoInteger
			try {
				currentPageNo=Integer.valueOf(pageIndex);
			} catch (Exception e) {
			}
		}
		Integer queryStatus=null;
		if(_queryStatus!=null && !("").equals(_queryStatus)){
			queryStatus=Integer.parseInt(_queryStatus);
		}
		Integer queryCategoryLevel1=null;
		if(_queryCategoryLevel1!=null && !("").equals(_queryCategoryLevel1)){
			queryCategoryLevel1=Integer.parseInt(_queryCategoryLevel1);
		}
		Integer queryCategoryLevel2=null;
		if(_queryCategoryLevel2!=null && !("").equals(_queryCategoryLevel2)){
			queryCategoryLevel2=Integer.parseInt(_queryCategoryLevel2);
		}
		Integer queryCategoryLevel3=null;
		if(_queryCategoryLevel3!=null && !("").equals(_queryCategoryLevel3)){
			queryCategoryLevel3=Integer.parseInt(_queryCategoryLevel3);
		}
		
		Integer queryFlatformId=null;
		if(_queryFlatformId!=null && !("").equals(_queryFlatformId)){
			queryFlatformId=Integer.parseInt(_queryFlatformId);
		}
		//总数量
		int totalCount=0;
		try {
			totalCount = appInfoService.getAppInfoCount(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//总页数
				PageSupport pages = new PageSupport();
				pages.setCurrentPageNo(currentPageNo);
				pages.setPageSize(pageSize);
				pages.setTotalCount(totalCount);
				int totalPageCount = pages.getTotalPageCount();
				//控制首页和尾页
				if(currentPageNo < 1){
					currentPageNo = 1;
				}else if(currentPageNo > totalPageCount){
					currentPageNo = totalPageCount;
				}
				try {
					appInfoList = appInfoService.getAppInfoList(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId, currentPageNo, pageSize);
					statusList = this.getDataDictionaryList("APP_STATUS");
					flatFormList = this.getDataDictionaryList("APP_FLATFORM");
					categoryLevel1List = appCategoryService.getAppCategoryListByParentId(null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				model.addAttribute("appInfoList", appInfoList);
				model.addAttribute("statusList", statusList);
				model.addAttribute("flatFormList", flatFormList);
				model.addAttribute("categoryLevel1List", categoryLevel1List);
				model.addAttribute("pages", pages);
				model.addAttribute("queryStatus", queryStatus);
				model.addAttribute("querySoftwareName", querySoftwareName);
				model.addAttribute("queryCategoryLevel1", queryCategoryLevel1);
				model.addAttribute("queryCategoryLevel2", queryCategoryLevel2);
				model.addAttribute("queryCategoryLevel3", queryCategoryLevel3);
				model.addAttribute("queryFlatformId", queryFlatformId);
				
				//二级分类列表和三级分类列表---回显
				if(queryCategoryLevel2 != null && !queryCategoryLevel2.equals("")){
					categoryLevel2List = getCategoryList(queryCategoryLevel1.toString());
					model.addAttribute("categoryLevel2List", categoryLevel2List);
				}
				if(queryCategoryLevel3 != null && !queryCategoryLevel3.equals("")){
					categoryLevel3List = getCategoryList(queryCategoryLevel2.toString());
					model.addAttribute("categoryLevel3List", categoryLevel3List);
				}
				return "developer/appinfolist";
			}
	
	public List<DataDictionary> getDataDictionaryList(String typeCode){
		List<DataDictionary> dataDictionaryList=null;
		try {
			dataDictionaryList=dataDictionaryService.getDataDictionaryList(typeCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataDictionaryList;
		
	}
	
	/**
	 * 根据parentId查询出相应的级别列表
	 * @param pid
	 * @return
	 *//*
	@RequestMapping(value="/categoryLevel1List.json",method=RequestMethod.GET)
	@ResponseBody//返回json数组的时候一定要用这个注解
	public List<AppCategory> getAppCategoryList(@RequestParam String pid) {
		logger.info("getAppCategoryList pid========"+pid);
		//判断传过来的pid如果是空字符串，赋值null
		if(("").equals(pid)) pid=null;
		return getCategoryList(pid);//会多次调用这个方法，封装
	}*/
	
	
	/**
	 * 根据parentId查询出相应的分类级别列表
	 * @param pid
	 * @return
	 */
	@RequestMapping(value="/categorylevellist.json",method=RequestMethod.GET)
	@ResponseBody
	public List<AppCategory> getAppCategoryList (@RequestParam String pid){
		logger.debug("getAppCategoryList pid ============ " + pid);
		if(pid.equals("")) pid = null;
		return getCategoryList(pid);
	}
	
	public List<AppCategory> getCategoryList(String pid){
		List<AppCategory> categoryLeve1List=null;//定义List<AppCategory>变量
		try {																//pid==null?null:Integer.parseInt(pid)								
			categoryLeve1List=appCategoryService.getAppCategoryListByParentId(pid==null?null:Integer.parseInt(pid));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return categoryLeve1List;
	}
	

	/**
	 * 跳转到保存页面
	 * @param appInfo
	 * @return
	 */
	@RequestMapping(value="/appinfoadd",method=RequestMethod.GET)
	public String add(@ModelAttribute("appInfo") AppInfo appInfo){
		return "developer/appinfoadd";
	}
	
	/**
	 * ajax判断APKName是否已经存在或者为空
	 * @param APKName
	 * @return
	 */
	@RequestMapping(value="/apkexist.json",method=RequestMethod.GET)
	@ResponseBody
	public Object apkNameIsExist(@RequestParam String APKName){
		HashMap<String, String> resultMap=new HashMap<String, String>();
		if(StringUtils.isNullOrEmpty(APKName)){
			resultMap.put("APKName", "empty");
		}else{
			AppInfo appInfo=null;
			try {
				appInfo=appInfoService.getAppInfo(null, APKName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(null!=appInfo){
				resultMap.put("APKName", "exist");
			}else{
				resultMap.put("APKName", "noexist");
			}
		}
		return JSONArray.toJSONString(resultMap);
	}
	
	/**
	 * 根据typeCode查询出相应的数据字典列表
	 * @param pid
	 * @return
	 */
	@RequestMapping(value="/dataDictionaryList.json",method=RequestMethod.GET)
	@ResponseBody
	public List<DataDictionary> getDataDicList (@RequestParam String tcode){
		logger.debug("getDataDicList tcode ============ " + tcode);
		return this.getDataDictionaryList(tcode);
	}
	
	/** 
	 * 保存新增appInfo（主表）的数据
	 * @param appInfo
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/appinfoaddsave",method=RequestMethod.POST)
	public String addSave(AppInfo appInfo,HttpSession session,HttpServletRequest request,
					@RequestParam(value="a_logoPicPath",required= false) MultipartFile attach){		
		//AppInfo对象作为入参来接收前台页面传递的值，session给AppInfo里面的devId设值，devId从session获取
		//request从前台返回信息       request封装上传进来的文件
		String logoPicPath =  null;//项目存放的url路径
		String logoLocPath =  null;//服务器上文件存放的路径
		if(!attach.isEmpty()){
			String path = request.getSession().getServletContext().getRealPath("statics"+ File.separator+"uploadfiles");
			logger.info("uploadFile path: " + path);
			String oldFileName = attach.getOriginalFilename();//原文件名
			String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
			int filesize = 500000;
			if(attach.getSize() > filesize){//上传大小不得超过 50k
				request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_4);
				return "developer/appinfoadd";
            }else if(prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png") 
			   ||prefix.equalsIgnoreCase("jepg") || prefix.equalsIgnoreCase("pneg")){//上传图片格式
				 String fileName = appInfo.getAPKName() + ".jpg";//上传LOGO图片命名:apk名称.apk
				 File targetFile = new File(path,fileName);
				 if(!targetFile.exists()){
					 targetFile.mkdirs();
				 }
				 try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_2);
					return "developer/appinfoadd";
				} 
				 logoPicPath = request.getContextPath()+"/statics/uploadfiles/"+fileName;
				 logoLocPath = path+File.separator+fileName;
			}else{
				request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_3);
				return "developer/appinfoadd";
			}
		}
		//赋值
		appInfo.setCreatedBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appInfo.setCreationDate(new Date());
		appInfo.setLogoPicPath(logoPicPath);
		appInfo.setLogoLocPath(logoLocPath);
		appInfo.setDevId(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appInfo.setStatus(1);
		try {
			if(appInfoService.add(appInfo)){//重定向两次请求，一次跳转到list，还有一次是进行刷新
				return "redirect:/dev/flatform/app/list";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "developer/appinfoadd";
	}
	
	/**
	 * 修改
	 * @param id
	 * @param model
	 * @return
	 */
	 @RequestMapping(value="/appinfomodify",method=RequestMethod.GET)
	public String modifyAppinfo(@RequestParam("id") String id,Model model,
			@RequestParam(value="error",required=false) String fileUploadError){
		 //Model根据id查询到的信息传递给前台，用于数据的展示
		AppInfo appInfo=null;
		if(null != fileUploadError && fileUploadError.equals("error2")){
			fileUploadError = Constants.FILEUPLOAD_ERROR_2;
		}else if(null != fileUploadError && fileUploadError.equals("error4")){
			fileUploadError	= Constants.FILEUPLOAD_ERROR_4;
		}else if(null != fileUploadError && fileUploadError.equals("error3")){
			fileUploadError = Constants.FILEUPLOAD_ERROR_3;
		}
		try {
			//根据id获取到AppInfo，然后放到model里传递给前台
			appInfo=appInfoService.getAppInfo(Integer.parseInt(id), null);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		 model.addAttribute(appInfo);
		 model.addAttribute("fileUploadError",fileUploadError);
		 return "developer/appinfomodify";
		 
	 }
	 
	 /**
	  * 删除图片，清空路径
	  * @param id
	  * @return
	  */
	 @RequestMapping(value="/delfile.json",method=RequestMethod.GET)
	 @ResponseBody
	 public Object delFile(@RequestParam(value="id",required=false) String id){
		 HashMap<String, String> resultMap=new HashMap<String, String>();
		 String fileLocPath=null;
		 if(id==null ||("").equals(id)){//传过来的参数未空
			 resultMap.put("result", "files");
		 }else{//传过来的id有值
			 try {//根据地址从服务器上删除掉图片，然后再把appinfo上的路径清空
				fileLocPath=(appInfoService.getAppInfo(Integer.parseInt(id), null).getLogoLocPath());//获取地址
				File file=new File(fileLocPath);
			if(file.exists()){//判断是否存在
				if(file.delete()){//存在的话删除
					if(appInfoService.deleteAppLogo(Integer.parseInt(id))){
						resultMap.put("result", "success");//成功删除返回true
					}
				}	
				}
			 } catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }
		return JSONArray.toJSONString(resultMap);
		 
	 }
	 
		
		/**
		 * 保存修改后的appVersion
		 * @param 
		 * @param session
		 * @return
		 */
		
		@RequestMapping(value="/addinfomodifysave",method=RequestMethod.POST)
		public String modifySave(AppInfo appInfo,HttpSession session,HttpServletRequest request,
						@RequestParam(value="attach",required= false) MultipartFile attach){		
			//AppInfo对象作为入参来接收前台页面传递的值，session给AppInfo里面的devId设值，devId从session获取
			//request从前台返回信息       request封装上传进来的文件
			String logoPicPath =  null;//项目存放的url路径
			String logoLocPath =  null;//服务器上文件存放的路径
			String APKName=appInfo.getAPKName();
			if(!attach.isEmpty()){
				String path = request.getSession().getServletContext().getRealPath("statics"+ File.separator+"uploadfiles");
				logger.info("uploadFile path: " + path);
				String oldFileName = attach.getOriginalFilename();//原文件名
				String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
				int filesize = 500000;
				if(attach.getSize() > filesize){//上传大小不得超过 50k
					//request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_4);
					return "redirect:/dev/flatform/app/appinfomodify?id="+appInfo.getId()+"&error=error4";
	            }else if(prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png") 
				   ||prefix.equalsIgnoreCase("jepg") || prefix.equalsIgnoreCase("pneg")){//上传图片格式
					 String fileName = APKName+ ".jpg";//上传LOGO图片命名:apk名称.apk
					 File targetFile = new File(path,fileName);
					 if(!targetFile.exists()){
						 targetFile.mkdirs();
					 }
					 try {
						attach.transferTo(targetFile);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_2);
						return "redirect:/dev/flatform/app/appinfomodify?id="+appInfo.getId()+"&error=error2";
					} 
					 logoPicPath = request.getContextPath()+"/statics/uploadfiles/"+fileName;
					 logoLocPath = path+File.separator+fileName;
				}else{
					request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_3);
					return "redirect:/dev/flatform/app/appinfomodify?id="+appInfo.getId()+"&error=error3";
				}
			}
			appInfo.setModifyBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
			appInfo.setModifyDate(new Date());
			appInfo.setLogoLocPath(logoLocPath);
			appInfo.setLogoPicPath(logoPicPath);
			
			try {
				if(appInfoService.modify(appInfo)){
					return "redirect:/dev/flatform/app/list";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "developer/appinfomodify";
		}
		
		
		/**
		 * 增加appversion信息（跳转到新增app版本页面）
		 * @param appInfo
		 * @return
		 */
		@RequestMapping(value="/appversionadd",method=RequestMethod.GET)
		public String addVersion(@RequestParam(value="id")String appId,
								 @RequestParam(value="error",required= false)String fileUploadError,
								 AppVersion appVersion,Model model){
			// appId是app基础信息的id,通过id查询出各个列表的版本信息，AppVersion是空对象，提供给前台以后，会给里面的空对象设置
			logger.debug("fileUploadError============> " + fileUploadError);
			if(null != fileUploadError && fileUploadError.equals("error1")){
				fileUploadError = Constants.FILEUPLOAD_ERROR_1;
			}else if(null != fileUploadError && fileUploadError.equals("error2")){
				fileUploadError	= Constants.FILEUPLOAD_ERROR_2;
			}else if(null != fileUploadError && fileUploadError.equals("error3")){
				fileUploadError = Constants.FILEUPLOAD_ERROR_3;
			}
			appVersion.setAppId(Integer.parseInt(appId));
			List<AppVersion> appVersionList = null;
			try {
				appVersionList = appVersionService.getAppVersionList(Integer.parseInt(appId));
				appVersion.setAppName((appInfoService.getAppInfo(Integer.parseInt(appId),null)).getSoftwareName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			model.addAttribute("appVersionList", appVersionList);
			model.addAttribute(appVersion);
			model.addAttribute("fileUploadError",fileUploadError);
			return "developer/appversionadd";
		}
		
		
		/**
		 * 保存新增appversion数据（子表）-上传该版本的apk包
		 * @param appInfo
		 * @param appVersion
		 * @param session
		 * @param request
		 * @param attach
		 * @return
		 */
		@RequestMapping(value="/addversionsave",method=RequestMethod.POST)
		public String addVersionSave(AppVersion appVersion,HttpSession session,HttpServletRequest request,
							@RequestParam(value="a_downloadLink",required= false) MultipartFile attach ){		
			String downloadLink =  null;
			String apkLocPath = null;
			String apkFileName = null;
			if(!attach.isEmpty()){
				String path = request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
				logger.info("uploadFile path: " + path);
				String oldFileName = attach.getOriginalFilename();//原文件名
				String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
				if(prefix.equalsIgnoreCase("apk")){//apk文件命名：apk名称+版本号+.apk
					 String apkName = null;
					 try {
						apkName = appInfoService.getAppInfo(appVersion.getAppId(),null).getAPKName();
					 } catch (Exception e1) {
						e1.printStackTrace();
					 }
					 if(apkName == null || "".equals(apkName)){
						 return "redirect:/dev/flatform/app/appversionadd?id="+appVersion.getAppId()
								 +"&error=error1";
					 }
					 apkFileName = apkName + "-" +appVersion.getVersionNo() + ".apk";
					 File targetFile = new File(path,apkFileName);
					 if(!targetFile.exists()){
						 targetFile.mkdirs();
					 }
					 try {
						attach.transferTo(targetFile);
					} catch (Exception e) {
						e.printStackTrace();
						return "redirect:/dev/flatform/app/appversionadd?id="+appVersion.getAppId()
								 +"&error=error2";
					} 
					downloadLink = request.getContextPath()+"/statics/uploadfiles/"+apkFileName;
					apkLocPath = path+File.separator+apkFileName;
				}else{
					return "redirect:/dev/flatform/app/appversionadd?id="+appVersion.getAppId()
							 +"&error=error3";
				}
			}
			appVersion.setCreatedBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
			appVersion.setCreationDate(new Date());
			appVersion.setDownloadLink(downloadLink);
			appVersion.setApkLocPath(apkLocPath);
			appVersion.setApkFileName(apkFileName);
			try {
				if(appVersionService.appsysadd(appVersion)){
					return "redirect:/dev/flatform/app/list";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "redirect:/dev/flatform/app/appversionadd?id="+appVersion.getAppId();
		}
		
		
		/**
		 * 修改最新的appVersion信息（跳转到修改appVersion页面）
		 * @param versionId
		 * @param appId
		 * @param model
		 * @return
		 */
		@RequestMapping(value="/appversionmodify",method=RequestMethod.GET)
		public String modifyAppVersion(@RequestParam("vid") String versionId,
										@RequestParam("aid") String appId,
										@RequestParam(value="error",required= false)String fileUploadError,
										Model model){
			AppVersion appVersion = null;
			List<AppVersion> appVersionList = null;
			if(null != fileUploadError && fileUploadError.equals("error1")){
				fileUploadError = Constants.FILEUPLOAD_ERROR_1;
			}else if(null != fileUploadError && fileUploadError.equals("error2")){
				fileUploadError	= Constants.FILEUPLOAD_ERROR_2;
			}else if(null != fileUploadError && fileUploadError.equals("error3")){
				fileUploadError = Constants.FILEUPLOAD_ERROR_3;
			}
			try {
				appVersion = appVersionService.getAppVersionById(Integer.parseInt(versionId));
				appVersionList = appVersionService.getAppVersionList(Integer.parseInt(appId));
			}catch (Exception e) {
				e.printStackTrace();
			}
			model.addAttribute(appVersion);
			model.addAttribute("appVersionList",appVersionList);
			model.addAttribute("fileUploadError",fileUploadError);
			return "developer/appversionmodify";
		}
		
		
		/**
		 * 保存修改后的appVersion
		 * @param appVersion
		 * @param session
		 * @return
		 */
		@RequestMapping(value="/appversionmodifysave",method=RequestMethod.POST)
		public String modifyAppVersionSave(AppVersion appVersion,HttpSession session,HttpServletRequest request,
						@RequestParam(value="attach",required= false) MultipartFile attach){	
			
			String downloadLink =  null;
			String apkLocPath = null;
			String apkFileName = null;
			if(!attach.isEmpty()){
				String path = request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
				logger.info("uploadFile path: " + path);
				String oldFileName = attach.getOriginalFilename();//原文件名
				String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
				if(prefix.equalsIgnoreCase("apk")){//apk文件命名：apk名称+版本号+.apk
					 String apkName = null;
					 try {
						apkName = appInfoService.getAppInfo(appVersion.getAppId(),null).getAPKName();
					 } catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					 }
					 if(apkName == null || "".equals(apkName)){
						 return "redirect:/dev/flatform/app/appversionmodify?vid="+appVersion.getId()
								 +"&aid="+appVersion.getAppId()
								 +"&error=error1";
					 }
					 apkFileName = apkName + "-" +appVersion.getVersionNo() + ".apk";
					 File targetFile = new File(path,apkFileName);
					 if(!targetFile.exists()){
						 targetFile.mkdirs();
					 }
					 try {
						attach.transferTo(targetFile);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return "redirect:/dev/flatform/app/appversionmodify?vid="+appVersion.getId()
								 +"&aid="+appVersion.getAppId()
								 +"&error=error2";
					} 
					downloadLink = request.getContextPath()+"/statics/uploadfiles/"+apkFileName;
					apkLocPath = path+File.separator+apkFileName;
				}else{
					return "redirect:/dev/flatform/app/appversionmodify?vid="+appVersion.getId()
							 +"&aid="+appVersion.getAppId()
							 +"&error=error3";
				}
			}
			appVersion.setModifyBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
			appVersion.setModifyDate(new Date());
			appVersion.setDownloadLink(downloadLink);
			appVersion.setApkLocPath(apkLocPath);
			appVersion.setApkFileName(apkFileName);
			try {
				if(appVersionService.modify(appVersion)){
					return "redirect:/dev/flatform/app/list";
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "developer/appversionmodify";
		}
		
		/**
		 * 查看app信息，包括app基本信息和版本信息列表（跳转到查看页面）
		 * @param appInfo
		 * @return
		 */
		@RequestMapping(value="/appview/{id}",method=RequestMethod.GET)
		public String view(@PathVariable String id,Model model){
			AppInfo appInfo = null;
			List<AppVersion> appVersionList = null;
			try {
				appInfo = appInfoService.getAppInfo(Integer.parseInt(id),null);
				appVersionList = appVersionService.getAppVersionList(Integer.parseInt(id));
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			model.addAttribute("appVersionList", appVersionList);
			model.addAttribute(appInfo);
			return "developer/appinfoview";
		}
		
		//删除
		@RequestMapping(value="/delapp.json")
		@ResponseBody
		public Object delApp(@RequestParam String id){
			logger.debug("delApp appId===================== "+id);
			HashMap<String, String> resultMap = new HashMap<String, String>();
			if(StringUtils.isNullOrEmpty(id)){
				resultMap.put("delResult", "notexist");
			}else{
				try {
					if(appInfoService.appsysdeleteAppById(Integer.parseInt(id)))
						resultMap.put("delResult", "true");
					else
						resultMap.put("delResult", "false");
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return JSONArray.toJSONString(resultMap);
		}
		
		/**
		 * 上下架
		 * @param appid
		 * @param session
		 * @return
		 */
		@RequestMapping(value="/{appid}/sale",method=RequestMethod.PUT)
		@ResponseBody
		public Object sale(@PathVariable String appid,HttpSession session){
			HashMap<String, Object> resultMap = new HashMap<String, Object>();
			Integer appIdInteger = 0;
			try{
				appIdInteger = Integer.parseInt(appid);
			}catch(Exception e){
				appIdInteger = 0;
			}
			resultMap.put("errorCode", "0");
			resultMap.put("appId", appid);
			if(appIdInteger>0){
				try {
					DevUser devUser = (DevUser)session.getAttribute(Constants.DEV_USER_SESSION);
					AppInfo appInfo = new AppInfo();
					appInfo.setId(appIdInteger);
					appInfo.setModifyBy(devUser.getId());
					if(appInfoService.appsysUpdateSaleStatusByAppId(appInfo)){
						resultMap.put("resultMsg", "success");
					}else{
						resultMap.put("resultMsg", "success");
					}		
				} catch (Exception e) {
					resultMap.put("errorCode", "exception000001");
				}
			}else{
				//errorCode:0为正常
				resultMap.put("errorCode", "param000001");
			}
			
			/*
			 * resultMsg:success/failed
			 * errorCode:exception000001
			 * appId:appId
			 * errorCode:param000001
			 * testgit...........
			 */
			
			return resultMap;
		}
}
