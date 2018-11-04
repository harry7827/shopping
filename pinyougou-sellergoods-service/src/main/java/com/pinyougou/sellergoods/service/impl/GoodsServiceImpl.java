package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper descMapper;
	@Autowired
	private TbItemMapper tbItemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbSellerMapper sellerMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		TbGoods tbGoods = goods.getTbGoods();
		tbGoods.setAuditStatus("0");//设置未申请状态
		goodsMapper.insert(tbGoods);
		TbGoodsDesc goodsDesc = goods.getTbGoodsDesc();
		goodsDesc.setGoodsId(tbGoods.getId());
		descMapper.insert(goodsDesc);
        saveItemList(goods);//保存SKU列表
    }
	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
        goodsMapper.updateByPrimaryKey(goods.getTbGoods());
        descMapper.updateByPrimaryKey(goods.getTbGoodsDesc());
        //删除原有的SKU列表
        TbItemExample example=new TbItemExample();
        com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
        tbItemMapper.deleteByExample(example);
        saveItemList(goods);//保存SKU列表
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
        Goods goods=new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        TbGoodsDesc goodsDesc = descMapper.selectByPrimaryKey(id);
        goods.setTbGoods(tbGoods);
        goods.setTbGoodsDesc(goodsDesc);
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> itemList = tbItemMapper.selectByExample(example);
        goods.setItemList(itemList);
        return goods;
    }

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}
	
	
	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusEqualTo(goods.getAuditStatus());
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteEqualTo(goods.getIsDelete());
			}else {
                criteria.andIsDeleteIsNull();
            }
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}


    private void saveItemList(Goods goods){
        List<TbItem> itemList = goods.getItemList();
        Date date=new Date();
        if ("1".equals(goods.getTbGoods().getIsEnableSpec())){
            for (TbItem tbItem : itemList) {
                setItemValues(tbItem,goods);
                //商品标题
                String title = goods.getTbGoods().getGoodsName();
                Map<String,Object> maps = JSONObject.parseObject(tbItem.getSpec(), Map.class);
                for (String key : maps.keySet()) {
                    title +=" "+maps.get(key);
                }
                tbItem.setTitle(title);
                tbItemMapper.insert(tbItem);
            }
        }else {
            TbItem tbItem=new TbItem();
            tbItem.setTitle(goods.getTbGoods().getGoodsName());//标题
            tbItem.setPrice(goods.getTbGoods().getPrice());//价格
            tbItem.setNum(99999);//库存数量
            tbItem.setStatus("1");//状态
            tbItem.setIsDefault("1");//默认
            tbItem.setSpec("{}");//规格

            setItemValues(tbItem,goods);

            tbItemMapper.insert(tbItem);
        }
    }
    private void setItemValues(TbItem item,Goods goods){
        //商品分类
        item.setCategoryid(goods.getTbGoods().getCategory3Id());//三级分类ID
        item.setCreateTime(new Date());//创建日期
        item.setUpdateTime(new Date());//更新日期

        item.setGoodsId(goods.getTbGoods().getId());//商品ID
        item.setSellerId(goods.getTbGoods().getSellerId());//商家ID

        //分类名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        //品牌名称
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
        item.setBrand(brand.getName());
        //商家名称(店铺名称)
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
        item.setSeller(seller.getNickName());

        //图片
        List<Map> imageList = JSON.parseArray( goods.getTbGoodsDesc().getItemImages(), Map.class) ;
        if(imageList.size()>0){
            item.setImage( (String)imageList.get(0).get("url"));
        }

    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }
    /**
     * 根据SPU的ID集合查询SKU列表
     * @param goodsIds
     * @param status
     * @return
     */
    @Override
    public List<TbItem>	findItemListByGoodsIdListAndStatus(Long []goodsIds,String status){
        TbItemExample example=new TbItemExample();
        com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(status);//状态
        criteria.andGoodsIdIn( Arrays.asList(goodsIds));//指定条件：SPUID集合
        return tbItemMapper.selectByExample(example);
    }
}
