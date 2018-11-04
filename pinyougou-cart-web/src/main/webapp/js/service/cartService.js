//服务层
app.service('cartService',function($http){
	    	
	//查询cookie购物车
	this.findCartList=function(){
		return $http.get('../cart/findCartList.do');
	}

    //修改购物车
    this.addGoodsToCartList=function(itemId,num){
        return $http.get('../cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
    }
    //计算购物车总价
    this.count=function(cartList){
        var totalNum=0;
        var totalMoney=0.00;
        for (var i = 0; i < cartList.length; i++) {
            var obj = cartList[i].orderItemList;
            for (var j = 0; j < obj.length; j++) {
                totalMoney +=obj[j].totalFee;
                totalNum +=1;
            }
        }
        var totalValue={"totalMoney":totalMoney,"totalNum":totalNum};
        return totalValue;
    }

    //保存订单
    this.submitOrder=function(order){
        return $http.post('order/add.do',order);
    }

});
