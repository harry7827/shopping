package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;


    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String cartListStr = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        if (cartListStr==null || "".equals(cartListStr)){
            cartListStr="[]";
        }
        List<Cart> cartList_cookie =JSON.parseArray(cartListStr, Cart.class);
        System.out.println(cartList_cookie.size()+"#############");
        if ("anonymousUser".equals(username)){
            return cartList_cookie;
        }else {
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            if (cartList_cookie.size()>0){
                System.out.println(cartList_cookie.size()+"合并==========");
                List<Cart> cartList = mergeCartList(cartList_cookie, cartList_redis);
                CookieUtil.deleteCookie(request,response,"cartList");
                cartService.saveCartListToRedis(username,cartList);
                System.out.println(cartList_cookie.size()+"删除cartList cookie==========");
                return cartList;
            }
            return cartList_redis;
        }
    }

    private List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2) {
        for (Cart cart : cartList1) {
            for (TbOrderItem tbOrderItem : cart.getOrderItemList()) {
                cartList2 = cartService.addGoodsToCartList(cartList2, tbOrderItem.getItemId(), tbOrderItem.getNum());
            }
        }
        return cartList2;
    }

    @RequestMapping("/addGoodsToCartList")
    //@CrossOrigin(origins = "http://localhost:9105",allowCredentials = "true")
	public Result addGoodsToCartList(Long itemId,Integer num) {

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            List<Cart> cartList = findCartList();
            List<Cart> carts = cartService.addGoodsToCartList(cartList,itemId,num);//3600*24,
            if ("anonymousUser".equals(username)){
                CookieUtil.setCookie(request ,response,"cartList", JSON.toJSONString(carts),3600*24,"UTF-8");
            }else {
                cartService.saveCartListToRedis(username,carts);
            }
            return new Result(true,"加入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,e.getMessage());
        }
    }

}
