<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@include file="common/header.jsp"%>
<div class="row">
	<div class="col-md-12 col-sm-12 col-xs-12">
		<div class="x_panel">
			<div class="x_title">
				<h2>
					App信息管理维护 <small><i class="fa fa-user"></i>${devUserSession.devName} 您可以通过搜索或者其他的筛选项对 App操作</small>
				</h2>
				<div class="clearfix"></div>
			</div>
			<div class="x_content">
				<br />
				<form id="demo-form2" data-parsley-validate
					class="form-horizontal form-label-left">
					<input type="hidden" name="pageIndex" value="1" />
				<ul>
				<li>
					<div class="form-group">
						<label class="control-label col-md-3 col-sm-3 col-xs-12"
							for="first-name">软件名称
						</label>
						<div class="col-md-6 col-sm-6 col-xs-12">
							<input type="text" id="querySoftwareName" name="querySoftwareName" value="${querySoftwareName}"
								class="form-control col-md-7 col-xs-12">
						</div>
					</div>
					</li>
					<li>
					<div class="form-group">
						<label class="control-label col-md-3 col-sm-3 col-xs-12"
							for="last-name">App状态</label>
						<div class="col-md-6 col-sm-6 col-xs-12">
						<select name="queryStatus" class="form-control">
						<c:if test="${statusList !=null}">
						<option value="">---请选择---</option>
						<c:forEach var="dataDictionary" items="${statusList }">
						<option <c:if test="${dataDictionary.valueId==queryStatus }">selected="selected"</c:if>
  						 value="dataDictionary.valueId">${dataDictionary.valueName }</option>
						</c:forEach>
						</c:if>
						</select>
						</div>
					</div>
					</li>
					<li>
						<div class="form-group">
							<label class="control-label col-md-3 col-sm-3 col-xs-12">所属平台</label>
							<div class="col-md-6 col-sm-6 col-xs-12">
								<select name="queryFlatformId" class="form-control">
									<c:if test="${flatFormList != null }">
									   <option value="">--请选择--</option>
									   <c:forEach var="dataDictionary" items="${flatFormList}">
									   		<option <c:if test="${dataDictionary.valueId == queryFlatformId }">selected="selected"</c:if>
									   		value="${dataDictionary.valueId}">${dataDictionary.valueName}</option>
									   </c:forEach>
									</c:if>
        						</select>
							</div>
						</div>
					</li>
					<li>
						<div class="form-group">
							<label class="control-label col-md-3 col-sm-3 col-xs-12">一级分类</label>
							<div class="col-md-6 col-sm-6 col-xs-12">
								<select id="queryCategoryLevel1" name="queryCategoryLevel1" class="form-control">
									<c:if test="${categoryLevel1List != null }">
									   <option value="">--请选择--</option>
									   <c:forEach var="appCategory" items="${categoryLevel1List}">
									   		<option <c:if test="${appCategory.id == queryCategoryLevel1 }">selected="selected"</c:if>
									   		value="${appCategory.id}">${appCategory.categoryName}</option>
									   </c:forEach>
									</c:if>
        						</select>
							</div>
						</div>
					</li>
					<li>
						<div class="form-group">
							<label class="control-label col-md-3 col-sm-3 col-xs-12">二级分类</label>
							<div class="col-md-6 col-sm-6 col-xs-12">
							<input type="hidden" name="categorylevel2list" id="categorylevel2list"/>
        						<select name="queryCategoryLevel2" id="queryCategoryLevel2" class="form-control">
        							<c:if test="${categoryLevel2List != null }">
									   <option value="">--请选择--</option>
									   <c:forEach var="appCategory" items="${categoryLevel2List}">
									   		<option <c:if test="${appCategory.id == queryCategoryLevel2 }">selected="selected"</c:if>
									   		value="${appCategory.id}">${appCategory.categoryName}</option>
									   </c:forEach>
									</c:if>
        						</select>
							</div>
						</div>
					</li>
					<li>
						<div class="form-group">
							<label class="control-label col-md-3 col-sm-3 col-xs-12">三级分类</label>
							<div class="col-md-6 col-sm-6 col-xs-12">
        						<select name="queryCategoryLevel3" id="queryCategoryLevel3" class="form-control">
        							<c:if test="${categoryLevel3List != null }">
									   <option value="">--请选择--</option>
									   <c:forEach var="appCategory" items="${categoryLevel3List}">
									   		<option <c:if test="${appCategory.id == queryCategoryLevel3 }">selected="selected"</c:if>
									   		value="${appCategory.id}">${appCategory.categoryName}</option>
									   </c:forEach>
									</c:if>
        						</select>
							</div>
						</div>
						</li>
					<li>
					<div class="form-group">
							<button type="submit" class="btn btn-primary">查询</button>
						</div>
						</li>
					</ul>
					</div>
				</form>
			</div>
		</div>
	</div>
	 <div class="x_panel">
	 <div class="x_content">
	 <a href="${pageContext.request.contextPath }/dev/flatform/app/appinfoadd" type="button" class="btn btn-success">新增app基础信息</a>
	<table id="datatable" class="table table-striped table-bordered">
                      <thead>
                        <tr>
                          <th>软件名称</th>
                          <th>APK名称</th>
                          <th>软件大小（单位：M）</th>
                          <th>所属平台</th>
                          <th>所属状态（一级分类，二级分类，三级分类）</th>
                          <th>状态</th>
                          <th>下载次数</th>
                          <th>最新版本号</th>
                          <th style="width:125px">操作</th>
                        </tr>
                      </thead>


                      <tbody>
						<c:forEach var="appInfo" items="${appInfoList }" varStatus="status">
									<tr role="row" class="odd">
										<td tabindex="0" class="sorting_1">${appInfo.softwareName}</td>
										<td>${appInfo.APKName }</td>
										<td>${appInfo.softwareSize }</td>
										<td>${appInfo.flatformName }</td>
										<td>${appInfo.categoryLevel1Name } -> ${appInfo.categoryLevel2Name } -> ${appInfo.categoryLevel3Name }</td>
										<td><span id="appInfoStatus${appInfo.id}">${appInfo.statusName }</span></td>
										<td>${appInfo.downloads }</td>
										<td>${appInfo.versionNo }</td>
						<td>
							<!-- Split button -->
							<div class="btn-group">
								<button type="button" class="btn btn-danger">点击操作</button>
								<button type="button" class="btn btn-danger dropdown-toggle"
									data-toggle="dropdown" aria-expanded="false">
									<span class="caret"></span> <span class="sr-only">Toggle
										Dropdown</span>
								</button>
								<ul class="dropdown-menu" role="menu">
									<li><c:choose>
											<c:when test="${appInfo.status == 2 || appInfo.status == 5}">
												<a class="saleSwichOpen" saleSwitch="open"
													appinfoid=${appInfo.id }
													appsoftwarename=${appInfo.softwareName }
													data-toggle="tooltip" data-placement="top" title=""
													data-original-title="恭喜您，您的审核已经通过，您可以点击上架发布您的APP">上架</a>
											</c:when>
											<c:when test="${appInfo.status == 4}">
												<a class="saleSwichClose" saleSwitch="close"
													appinfoid=${appInfo.id }
													appsoftwarename=${appInfo.softwareName }
													data-toggle="tooltip" data-placement="top" title=""
													data-original-title="您可以点击下架来停止发布您的APP，市场将不提供APP的下载">下架</a>
											</c:when>
										</c:choose></li>
									<li><a class="addVersion" 
											appinfoid="${appInfo.id }"
										data-toggle="tooltip" data-placement="top" title=""
										data-original-title="新增APP版本信息">新增版本</a></li>
									<li><a class="modifyVersion" appinfoid="${appInfo.id }"
										versionid="${appInfo.versionId }" status="${appInfo.status }"
										statusname="${appInfo.statusName }" data-toggle="tooltip"
										data-placement="top" title=""
										data-original-title="修改APP最新版本信息">修改版本</a></li>
									<!--要修改，要获取到这个id,进而获取到所有的app的所有信息也就是获取到这个对象，然后把这个对象传到页面，将我们要修改的信息展示出来，是一个传值的过程 -->
									<li><a class="modifyAppInfo" 
										appinfoid="${appInfo.id }"
										status="${appInfo.status }"
										statusname="${appInfo.statusName }" data-toggle="tooltip"
										data-placement="top" title="" data-original-title="修改APP基础信息">修改</a>
									</li>
									<li><a class="viewApp" appinfoid=${appInfo.id }
										data-toggle="tooltip" data-placement="top" title=""
										data-original-title="查看APP基础信息以及全部版本信息">查看</a></li>
									<li><a class="deleteApp" appinfoid=${appInfo.id }
										appsoftwarename=${appInfo.softwareName } data-toggle="tooltip"
										data-placement="top" title=""
										data-original-title="删除APP基础信息以及全部版本信息">删除</a></li>
								</ul>
							</div>
						</td>
					</tr>
                      </c:forEach>
                      </tbody>
                    </table>
                    </div>
                    <div class="row">
					<div class="col-sm-5">
						<div class="dataTables_info" id="datatable-responsive_info"
							role="status" aria-live="polite">共${pages.totalCount }条记录
							${pages.currentPageNo }/${pages.totalPageCount }页</div>
					</div>
					<div class="col-sm-7">
						<div class="dataTables_paginate paging_simple_numbers"
							id="datatable-responsive_paginate">
							<ul class="pagination">
								<c:if test="${pages.currentPageNo > 1}">
									<li class="paginate_button previous"><a
										href="javascript:page_nav(document.forms[0],1);"
										aria-controls="datatable-responsive" data-dt-idx="0"
										tabindex="0">首页</a>
									</li>
									<li class="paginate_button "><a
										href="javascript:page_nav(document.forms[0],${pages.currentPageNo-1});"
										aria-controls="datatable-responsive" data-dt-idx="1"
										tabindex="0">上一页</a>
									</li>
								</c:if>
								<c:if test="${pages.currentPageNo < pages.totalPageCount }">
									<li class="paginate_button "><a
										href="javascript:page_nav(document.forms[0],${pages.currentPageNo+1 });"
										aria-controls="datatable-responsive" data-dt-idx="1"
										tabindex="0">下一页</a>
									</li>
									<li class="paginate_button next"><a
										href="javascript:page_nav(document.forms[0],${pages.totalPageCount });"
										aria-controls="datatable-responsive" data-dt-idx="7"
										tabindex="0">最后一页</a>
									</li>
								</c:if>
							</ul>
						</div>
					</div>
				</div>
                    </div>
                    
</div>
<%@include file="common/footer.jsp"%>
<script src="${pageContext.request.contextPath }/statics/localjs/rollpage.js"></script>
<script src="${pageContext.request.contextPath }/statics/localjs/appinfolist.js"></script>