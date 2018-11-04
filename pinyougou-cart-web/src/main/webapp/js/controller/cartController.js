//控制层
app.controller('cartController', function ($scope, $controller, cartService,addressService) {

    $controller('baseController', {$scope: $scope});//继承


    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;
                $scope.totalValue = cartService.count($scope.cartList);
            }
        )
    }

    $scope.addrList=[];
    $scope.findAddressList=function () {
        addressService.findAddressList().success(
            function (response) {
                $scope.addrList=response;
                //设置默认地址
                for(var i=0;i< $scope.addrList.length;i++){
                    if($scope.addrList[i].isDefault=='1'){
                        $scope.address=$scope.addrList[i];
                        break;
                    }
                }
            }
        )
    }
    $scope.entity={};
    $scope.editAddress=function(address) {
        //$scope.entity= address;
        addressService.findOne(address.id).success(
            function(response){
                $scope.entity= response;
            }
        );
    }
    //选择地址
    $scope.selectAddress=function(address){
        $scope.address=address;
    }
    //判断是否是当前选中的地址
    $scope.isSelectedAddress=function(address){
        if(address==$scope.address){
            return true;
        }else{
            return false;
        }
    }
    //批量单个
    $scope.deleOne=function(id){
        addressService.deleOne(id).success(
            function (response) {
                if(!response.success){
                    alert(response.message);
                }else {
                    $scope.findAddressList();
                }
            }
        )
    }
    //保存
    $scope.save=function(){
        var serviceObject;//服务层对象
        if($scope.entity.id!=null){//如果有ID
            serviceObject=addressService.update( $scope.entity ); //修改
        }else{
            serviceObject=addressService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    //重新查询
                    $scope.findAddressList();
                }else{
                    alert(response.message);
                }
            }
        );
    }
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(
            function (response) {
                $scope.findCartList();
            }
        );
    }
    //保存订单
    $scope.submitOrder = function () {
        $scope.order.receiverAreaName = $scope.address.address;//地址
        $scope.order.receiverMobile = $scope.address.mobile;//手机
        $scope.order.receiver = $scope.address.contact;//联系人
        cartService.submitOrder($scope.order).success(
            function (response) {
                alert(response);
                if (response.success) {
                    //页面跳转
                    if ($scope.order.paymentType == '1') {//如果是微信支付，跳转到支付页面
                        location.href = "pay.html";
                    } else {//如果货到付款，跳转到提示页面
                        location.href = "paysuccess.html";
                    }
                } else {
                    alert(response.message); //也可以跳转到提示页面
                }
            }
        );
    }
    $scope.order={paymentType:'1'};
    //选择支付方式
    $scope.selectPayType=function(type){
        $scope.order.paymentType= type;
    }
});	
