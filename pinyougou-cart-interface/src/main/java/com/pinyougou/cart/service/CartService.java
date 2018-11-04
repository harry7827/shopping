package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

/*
 *购物车服务接口
 *
 */
public interface CartService {
    List<Cart> findCartListFromRedis(String username);

    void saveCartListToRedis(String username, List<Cart> cartList);

    /**
     * 添加商品到购物车
     *
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);
}