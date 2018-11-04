package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从 redis 中提取购物车数据....."+username);
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if(cartList==null){
            cartList=new ArrayList();
        }
        return cartList;
    }
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向 redis 存入购物车数据....."+username);
        redisTemplate.boundHashOps("cartList").put(username, cartList);
    }

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据商品 SKU ID 查询 SKU 商品信息
        TbItem item = tbItemMapper.selectByPrimaryKey(itemId);
        if (item==null){
            throw new RuntimeException("商品不存在");
        }
        if (!"1".equals(item.getStatus())){
            throw new RuntimeException("商品状态无效");
        }
        //2.获取商家 ID
        String sellerId = item.getSellerId();
        //根据商家 ID 判断购物车列表中是否存在该商家的购物车
        Cart cart = searchCartBySellerId(cartList, sellerId);

        if (cart!=null){//有这个商家对应的购物车
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            TbOrderItem orderItem = searchOrderItemByItemId(orderItemList, itemId);
            if (orderItem!=null){//已经有这个订单项了
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * orderItem.getNum().doubleValue()));
            }else {
                orderItem = createOrderItem(item, num);
                orderItemList.add(orderItem);
            }
            //如果这个订单项商品的数量小于等于0 删除该订单项
            if (orderItem.getNum()<=0) {
                cart.getOrderItemList().remove(orderItem);
            }
            //如果这个商家的购物车内订单项的数量小于等于0 删除对应该商家的购物车
            if (cart.getOrderItemList().size()<=0) {
                cartList.remove(cart);
            }
        }else{//没有这个商家对应的购物车
            if (num>=0){
                cart=new Cart();
                cart.setSellerId(sellerId);
                cart.setSellerName(item.getSeller());

                //封装订单项
                TbOrderItem tbOrderItem=createOrderItem(item,num);

                //封装到订单项集合
                List<TbOrderItem> orderItemList=new ArrayList<TbOrderItem>();
                orderItemList.add(tbOrderItem);

                //添加到购物车
                cart.setOrderItemList(orderItemList);

                //购物车添加到购物车集合
                cartList.add(cart);
            }

        }

        return cartList;
    }

    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId){
        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId){
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }
    private TbOrderItem createOrderItem(TbItem item,Integer num){
        TbOrderItem orderItem=new TbOrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num.doubleValue()));
        return orderItem;
    }
}
