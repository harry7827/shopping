 //控制层 
app.controller('itemCatController' ,function($scope,$controller,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;
				console.log($scope.entity);
			}
		);
	}
	
	//保存 
	$scope.save=function(){
		$scope.entity.parentId=$scope.parentId;
		//$scope.entity.typeId=$scope.entity.typeId.id
		var serviceObject;//服务层对象  	
		console.log($scope.entity);
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
					$scope.findByParentId($scope.parentId);//刷新列表
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds).success(
			function(response){
				if(response.success){
					$scope.findByParentId($scope.parentId);//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//根据父ID查询
	$scope.parentId=0;//记录父级ID 初始化为顶级 
	$scope.findByParentId=function(parentId){			
		itemCatService.findByParentId(parentId).success(
			function(response){
				$scope.list=response;	
			}			
		);
	}
	
	
	//分级查询
	$scope.grade=0;//初始状态为顶级
	$scope.setGrade=function(grade){
		$scope.grade = grade;
	}
	$scope.selectList=function(p_entity){
		$scope.parentId=p_entity.id;
		if ($scope.grade==0) {
			$scope.entity_1=null;
			$scope.entity_2=null;
		}
		if ($scope.grade==1) {
			$scope.entity_1=p_entity;
			$scope.entity_2=null;
		}
		if ($scope.grade==2) {
			$scope.entity_2=p_entity;
		}
		$scope.findByParentId(p_entity.id);
	}
	//分级所有类型模板
	$scope.typeTemplates={data:[]};//初始化
    //读取列表数据绑定到表单中  
	$scope.selectTypeTemplateList=function(){
		typeTemplateService.selectTypeTemplateList().success(
			function(response){
				$scope.typeTemplates={data:response};
			}			
		);
	}
});	
