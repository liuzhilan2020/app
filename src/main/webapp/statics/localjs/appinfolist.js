$("#queryCategoryLevel1").change(function(){//当元素的值发生改变时，会发生 change 事件。
	var level1=$(this).val();//获取到一级分类的值
	if(level1 !="" && level1 !=null){//判断一级分类的值不等于空后者不能Null
		$.ajax({
			type:"GET",//请求类型
			url:"categorylevellist.json",//请求的url
			data:{pid:level1},//请求的参数
			dataType:"json",//请求放回的数据类型
			success:function(data){//data返回json对象
				//如果能成功拿到数据，接下来把二级分类下拉框内容清空
				$("queryCategoryLevel2").html("");
				//通过options创建dom元素，然后把dom元素值赋值给二级分类下拉框
				var options="<option value=''>--请选择--</option>";
				for(var i=0;i<data.length;i++){//遍历控制台返回的data
					//"<option value'"+data[i].id+"'>"valeu的值
					//data[i].categoryName+"</option>"下拉框展示的内容
					options+="<option value'"+data[i].id+"'>"+data[i].categoryName
					+"</option>";
				}
				$("#queryCategoryLevel2").html(options);//将循环拼接的dom对象赋值给queryCategoryLevel2
			},
			error:function(data){//当访问的时候，可能出现404，505的问题
				alert("加载二级分类失败");
			}
		})
		//以上的情况是一级分类有值的情况下，以下是一级分类是“请选择”也就是没值的情况，把二级三级分类清空
	}else{//一级分类请选择的时候，获取二级分类，然后清空，让下拉列表没有内容
		$("#queryCategoryLevel2").html();
		var options="<option value=''>--请选择--</option>";
		$("#queryCategoryLevel2").html(options);
	}
	//获取三级分类，然后清空，让下拉列表没有内容
	$("#queryCategoryLevel3").html();
	var options="<option value=''>--请选择--</option>";
	$("#queryCategoryLevel3").html(options);
});

$("#queryCategoryLevel2").change(function(){
	var queryCategoryLevel2 = $("#queryCategoryLevel2").val();
	if(queryCategoryLevel2 != '' && queryCategoryLevel2 != null){
		$.ajax({
			type:"GET",//请求类型
			url:"categorylevellist.json",//请求的url
			data:{pid:queryCategoryLevel2},//请求参数
			dataType:"json",//ajax接口（请求url）返回的数据类型
			success:function(data){//data：返回数据（json对象）
				$("#queryCategoryLevel3").html("");
				var options = "<option value=\"\">--请选择--</option>";
				for(var i = 0; i < data.length; i++){
					//alert(data[i].id);
					//alert(data[i].categoryName);
					options += "<option value=\""+data[i].id+"\">"+data[i].categoryName+"</option>";
				}
				$("#queryCategoryLevel3").html(options);
			},
			error:function(data){//当访问时候，404，500 等非200的错误状态码
				alert("加载三级分类失败！");
			}
		});
	}else{
		$("#queryCategoryLevel3").html("");
		var options = "<option value=\"\">--请选择--</option>";
		$("#queryCategoryLevel3").html(options);
	}
});

//修改
$(".modifyAppInfo").on("click",function(){
	var obj=$(this);//获取当前对象信息
	var status=obj.attr("status");//获取到obj里面的状态值
	if(status=="1"||status=="3"){//获取到obj里面的状态值对其判断             待审核，审核未通过的状态才可以修改
		window.location.href="appinfomodify?id="+obj.attr("appinfoid");
	}{
		alert("该app状态为【"+obj.attr("statusName")+"】，不能修改");
	}
})
//新增版本  --跳转到addVersion页面
$(".addVersion").on("click",function(){
	var obj=$(this);
	window.location.href="appversionadd?id="+obj.attr("appinfoid");
})
//修改版本
$(".modifyVersion").on("click",function(){
	var obj = $(this);
	var status = obj.attr("status");
	var versionid = obj.attr("versionid");//修改的版本
	var appinfoid = obj.attr("appinfoid");//展示app基础信息的所有信息，历史版本信息
	if(status == "1" || status == "3"){//待审核、审核未通过状态下才可以进行修改操作
		if(versionid == null || versionid == ""){
			alert("该APP应用无版本信息，请先增加版本信息！");
		}else{
			window.location.href="appversionmodify?vid="+ versionid + "&aid="+ appinfoid;
		}
	}else{
		alert("该APP应用的状态为：【"+obj.attr("statusname")+"】,不能修改其版本信息，只可进行【新增版本】操作！");
	}
});
//查看
$(".viewApp").on("click",function(){
	var obj = $(this);
	window.location.href="appview/"+ obj.attr("appinfoid");
});
//删除
$(".deleteApp").on("click",function(){
	var obj = $(this);
	if(confirm("你确定要删除APP应用【"+obj.attr("appsoftwarename")+"】及其所有的版本吗？")){
		$.ajax({
			type:"GET",
			url:"delapp.json",
			data:{id:obj.attr("appinfoid")},
			dataType:"json",
			success:function(data){
				if(data.delResult == "true"){//删除成功：移除删除行
					alert("删除成功");
					obj.parents("tr").remove();//移除删除的这一行
				}else if(data.delResult == "false"){//删除失败
					alert("对不起，删除AAP应用【"+obj.attr("appsoftwarename")+"】失败");
				}else if(data.delResult == "notexist"){
					alert("对不起，APP应用【"+obj.attr("appsoftwarename")+"】不存在");
				}
			},
			error:function(data){
				alert("对不起，删除失败");
			}
		});
	}
});
//上下架在一个方法里
$(document).on("click",".saleSwichOpen,.saleSwichClose",function(){
	var obj = $(this);
	var appinfoid = obj.attr("appinfoid");
	var saleSwitch = obj.attr("saleSwitch");
	if("open" === saleSwitch){//上架操作，审核通过和已下架
		saleSwitchAjax(appinfoid,obj);
	}else if("close" === saleSwitch){//下架
		if(confirm("你确定要下架您的APP应用【"+obj.attr("appsoftwarename")+"】吗？")){
			saleSwitchAjax(appinfoid,obj);
		}
	}
});
var saleSwitchAjax = function(appId,obj){
	$.ajax({
		type:"PUT",
		url:appId+"/sale.json",
		dataType:"json",
		success:function(data){
			/*
			 * resultMsg:success/failed
			 * errorCode:exception000001
			 * appId:appId
			 * errorCode:param000001
			 */
			if(data.errorCode === '0'){
				if(data.resultMsg === "success"){//操作成功
					if("open" === obj.attr("saleSwitch")){
						//alert("恭喜您，【"+obj.attr("appsoftwarename")+"】的【上架】操作成功");
						$("#appInfoStatus" + obj.attr("appinfoid")).html("已上架");
						obj.className="saleSwichClose";
						obj.html("下架");
						obj.attr("saleSwitch","close");
						$("#appInfoStatus" + obj.attr("appinfoid")).css({
							'background':'green',
							'color':'#fff',
							'padding':'3px',
							'border-radius':'3px'
						});
						$("#appInfoStatus" + obj.attr("appinfoid")).hide();
						$("#appInfoStatus" + obj.attr("appinfoid")).slideDown(300);
					}else if("close" === obj.attr("saleSwitch")){
						//alert("恭喜您，【"+obj.attr("appsoftwarename")+"】的【下架】操作成功");
						$("#appInfoStatus" + obj.attr("appinfoid")).html("已下架");
						obj.className="saleSwichOpem";
						obj.html("上架");
						obj.attr("saleSwitch","open");
						$("#appInfoStatus" + obj.attr("appinfoid")).css({
							'background':'red',
							'color':'#fff',
							'padding':'3px',
							'border-radius':'3px'
						});
						$("#appInfoStatus" + obj.attr("appinfoid")).hide();
						$("#appInfoStatus" + obj.attr("appinfoid")).slideDown(300);
					}
				}else if(data.resultMsg === "failed"){//删除失败
					if("open" === obj.attr("saleSwitch")){
						alert("恭喜您，【"+obj.attr("appsoftwarename")+"】的【上架】操作失败");
					}else if("close" === obj.attr("saleSwitch")){
						alert("恭喜您，【"+obj.attr("appsoftwarename")+"】的【下架】操作失败");
					}
				}
			}else{
				if(data.errorCode === 'exception000001'){
					alert("对不起，系统出现异常，请联系IT管理员");
				}else if(data.errorCode === 'param000001'){
					alert("对不起，参数出现错误，您可能在进行非法操作");
				}
			}
		},
		error:function(data){
			if("open" === obj.attr("saleSwitch")){
				alert("恭喜您，【"+obj.attr("appsoftwarename")+"】的【上架】操作成功");
			}else if("close" === obj.attr("saleSwitch")){
				alert("恭喜您，【"+obj.attr("appsoftwarename")+"】的【下架】操作成功");
			}
		}
	});
};

