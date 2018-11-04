package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 5000)
@Transactional
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map = new HashMap<>();
        //空格处理
        String keywords= (String)searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));//关键字去掉空格
        //1.查询列表
        map.putAll(searchList(searchMap));
        //2.根据关键字查询商品分类
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);
        if (categoryList.size() > 0) {
            map.putAll(searchBrandAndSpecList(categoryList.get(0)));
        }
        return map;
    }



    public Map<String, Object> searchList(Map searchMap) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (searchMap.get("keywords") != null) {
            String keywords = (String) searchMap.get("keywords");
            SimpleHighlightQuery query = new SimpleHighlightQuery();
            //设置高亮
            HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
            highlightOptions.setSimplePrefix("<em style='color:red'>");
            highlightOptions.setSimplePostfix("</em>");
            query.setHighlightOptions(highlightOptions);
            //设置查询条件 关键字
            Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
            query.addCriteria(criteria);
            //设置其他查询条件 商品分类category 品牌brand
            if (!"".equals(searchMap.get("category"))) {
                Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!"".equals(searchMap.get("brand"))) {
                Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            //设置查询条件 规格 spec : {"网络":"3G",...}
            if (searchMap.get("spec")!=null){
                Map<String,String> specMap = (Map)searchMap.get("spec");
                for (String key : specMap.keySet()) {
                    System.out.println("key  :  "+"item_spec_"+key+"value  :  "+specMap.get(key));
                    Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
                    FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                    query.addFilterQuery(filterQuery);
                }
            }
            //按价格过滤
            if(!"".equals(searchMap.get("price")) ){
                String[] price = ((String)searchMap.get("price")).split("-");
                if (!"0".equals(price[0])){
                    Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                    FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                    query.addFilterQuery(filterQuery);
                }
                if (!"*".equals(price[1])){
                    Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                    FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                    query.addFilterQuery(filterQuery);
                }
            }
            // 排序
            String sortValue= (String)searchMap.get("sort");//升序ASC 降序DESC
            String sortField=  (String)searchMap.get("sortField");//排序字段
            if(!"".equals(sortValue) && !"".equals(sortField)){
                if(sortValue.equals("ASC")){
                    Sort sort=new Sort(Sort.Direction.ASC, "item_"+sortField);
                    query.addSort(sort);
                }
                if(sortValue.equals("DESC")){
                    Sort sort=new Sort(Sort.Direction.DESC, "item_"+sortField);
                    query.addSort(sort);
                }
            }

            //设置分页
            Integer pageNo= (Integer) searchMap.get("pageNo");
            Integer pageSize= (Integer) searchMap.get("pageSize");
            if ( pageNo==null ){
                pageNo=1;
            }
            if ( pageSize==null ){
                pageSize=20;
            }
            query.setOffset( (pageNo-1)*pageSize );
            query.setRows(pageSize);

            //total
            HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
            for (HighlightEntry<TbItem> h : page.getHighlighted()) {
                TbItem item = h.getEntity();
                if (h.getHighlights().size() > 0 &&
                        h.getHighlights().get(0).getSnipplets().size() > 0) {
                    item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
                }
            }
            map.put("rows", page.getContent());
            map.put("totalPages", page.getTotalPages());//总页数
            map.put("total", page.getTotalElements());//总数

            System.out.println("================================" + page.getTotalElements());
        }

        return map;
    }

    public List searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList();
        String keywords = (String) searchMap.get("keywords");
        Query query = new SimpleQuery();
        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组查询
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合 并遍历 添加到list
        for (GroupEntry<TbItem> groupEntry : groupEntries.getContent()) {
            String groupValue = groupEntry.getGroupValue();
            list.add(groupValue);
        }
        return list;
    }

    public Map searchBrandAndSpecList(String category) {
        Map map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        System.out.println("typeId : " + typeId);
        if (typeId != null) {
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("brandList", brandList);
            System.out.println(brandList);
            map.put("specList", specList);
            System.out.println(specList);
        }
        return map;
    }

    @Override
    public void importList(List<TbItem> itemList) {
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List<Long> goodsIdList) {
        SolrDataQuery query=new SimpleQuery();
        Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

}
