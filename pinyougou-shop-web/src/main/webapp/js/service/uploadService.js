//服务层
app.service('uploadService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.uploadFile=function(){
        var formData=new FormData();
        formData.append("file",file.files[0]);
        return $http({
            method:'POST',
            url:"../upload.do",
            data: formData,
            headers: {'Content-Type':undefined},
            transformRequest: angular.identity
        });
	}

});
