function page_nav(frm,num){
		frm.pageIndex.value = num;//frm设置为传过来的num的值
		frm.submit();//让frm进行再次提交
}
