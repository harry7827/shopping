 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,uploadService,goodsService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}
    $scope.status=["未申请","申请中","审核通过","已驳回"];

	//分页
	$scope.findPage=function(page,rows){
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
        var id = $location.search()['id'];
        if (id!=undefined){
            goodsService.findOne(id).success(
                function(response){
                    $scope.entity= response;
                    editor.html($scope.entity.tbGoodsDesc.introduction);
                    $scope.entity.tbGoodsDesc.itemImages=JSON.parse(response.tbGoodsDesc.itemImages);
                    $scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse(response.tbGoodsDesc.customAttributeItems);

                    $scope.entity.tbGoodsDesc.specificationItems=JSON.parse(response.tbGoodsDesc.specificationItems)
                    var list=response.itemList;
                    for (var i = 0; i < list.length; i++) {
                        $scope.entity.itemList[i].spec=JSON.parse(list[i].spec);
                    }

                }
            );
        }

	}
	
	//保存 
	$scope.save=function(){
		$scope.entity.tbGoodsDesc.introduction=editor.html();
		var serviceObject;//服务层对象  				
		if($scope.entity.tbGoods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					alert(response.message);
                    location.href='goods.html';
				}else{
					alert(response.message);
				}
			}		
		);
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}

	$scope.searchEntity={};//定义搜索对象

	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    //上传商品图片
    $scope.uploadFile=function(){
        uploadService.uploadFile().success(
            function (response) {
                if (response.success){
                    $scope.image_entity.url=response.message;
                }else{
                    alert(response.message);
                }
            }
        )
    }
    //初始化商品数据
    $scope.entity={tbGoodsDesc:{itemImages:[],customAttributeItems:[],specificationItems:[]},tbGoods:{typeTemplateId:''},itemList:[{spec:{},price:0,num:99999,status:'0',isDefault:'0'} ]};
    //添加图片数据到entity
	$scope.add_image_entity=function(){
        $scope.entity.tbGoodsDesc.itemImages.push($scope.image_entity);
    }
    //删除图片数据
    $scope.del_image_entity=function(index){
        $scope.entity.tbGoodsDesc.itemImages.splice(index,1);
    }
    //级联显示三级分类
    $scope.selectItemCat1List=function () {
        itemCatService.findByParentId("0").success(
            function(response){
                $scope.itemCat2List={};
                $scope.itemCat3List={};
                $scope.itemCat1List=response;

            }
        )
    }
    $scope.$watch('entity.tbGoods.category1Id', function(newValue, oldValue) {
        if (newValue != ''&& newValue !=undefined){
            itemCatService.findByParentId(newValue).success(
                function(response){
                    $scope.itemCat2List=response;
                }
            )
        }

    })
    $scope.$watch('entity.tbGoods.category2Id', function(newValue, oldValue) {
        if (newValue != ''&& newValue !=undefined) {
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCat3List = response;
                }
            )
        }

    })
    $scope.$watch('entity.tbGoods.category3Id', function(newValue, oldValue) {
        if (newValue != ''&& newValue !=undefined) {
            itemCatService.findOne(newValue).success(
                function (response) {
                    $scope.entity.tbGoods.typeTemplateId = response.typeId;
                }
            );
        }
    })
    $scope.$watch("entity.tbGoods.typeTemplateId",function (newValue,oldValue) {
        /*typeTemplateService.findOne(newValue).success(
            function (response) {
                var josnData=JSON.parse(response.brandIds);
                $scope.brandIds={data:josnData};
            }
        )*/
        //不用selecct2
        if (newValue != ''&& newValue !=undefined) {
            typeTemplateService.findSpecList(newValue).success(
                function (response) {
                    $scope.specList = response;
                }
            );
            typeTemplateService.findOne(newValue).success(
                function (response) {
                    $scope.typeTemplate = response;
                    $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
                    if ($location.search()['id'] == null) {
                        $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);

                    }

                }
            );
        }
    })

    //显示规格数据
    $scope.updateSpecAttribute=function ($event,name,value) {
        var obj = $scope.searchObjectByKey($scope.entity.tbGoodsDesc.specificationItems, "attributeName", name);
        if (obj != null) {
            var index = $scope.entity.tbGoodsDesc.specificationItems.indexOf(obj);
            if ($event.target.checked) {
                $scope.entity.tbGoodsDesc.specificationItems[index].attributeValue.push(value);
            } else {
                $scope.entity.tbGoodsDesc.specificationItems[index].attributeValue.splice($scope.entity.tbGoodsDesc.specificationItems[index].attributeValue.indexOf(value), 1);
                if ($scope.entity.tbGoodsDesc.specificationItems[index].attributeValue.length == 0) {
                    $scope.entity.tbGoodsDesc.specificationItems.splice(index, 1);
                }
            }

        } else {
            $scope.entity.tbGoodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]});
        }

    }
    //itemCat数据动态展示
    $scope.createItemList=function(){
        $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0'} ];
        var items=$scope.entity.tbGoodsDesc.specificationItems;
        for(var i=0;i<items.length;i++){
            $scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
        }

    }
    //itemCat数据动态展示 之 添加行
    addColumn=function(list,columnName,columnValues){
        var newList=[];
        for(var i=0;i<list.length;i++){
            var oldRow=list[i];
            for(var j=0;j<columnValues.length;j++){
                var newRow=JSON.parse(JSON.stringify(oldRow));//深克隆
                newRow.spec[columnName]=columnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }
    //初始化所有分类id和name
    $scope.itemCatList=[];
    //查询所有分类id和name
    $scope.findAllItemCat=function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    $scope.itemCatList[i]={};
                    $scope.itemCatList[i].id=response[i].id;
                    $scope.itemCatList[i].name=response[i].name;
                }
            }
        )
    }
    $scope.checkAttributeValue=function (text,optionName) {
        var specItemList=$scope.entity.tbGoodsDesc.specificationItems;
        var flag=false;
        for (var i = 0; i < specItemList.length; i++) {

            if(specItemList[i]['attributeName']==text){
                if(specItemList[i]['attributeValue'].indexOf(optionName)>=0){
                    flag=true;
                    //$scope.entity.itemList.spec=JSON.parse(response.itemList.spec);
                }
            }

        }
        return flag;
    }
});	
