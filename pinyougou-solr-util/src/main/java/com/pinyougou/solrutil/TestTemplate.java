/*
package com.pinyougou.solrutil;


import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:spring/applicationContext*.xml")
public class TestTemplate {
	@Autowired
	private SolrTemplate solrTemplate;
    @Autowired
    private SolrUtil solrUtil;

    @Test
    public void testAdd() {
        TbItem item = new TbItem();
        item.setId(1L);
        item.setBrand("华为");
        item.setCategory("手机");
        item.setGoodsId(1L);
        item.setSeller("华为 2 号专卖店");
        item.setTitle("华为 Mate9");
        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }

    @Test
    public void testAddList() {
        List<TbItem> list = new ArrayList();
        for (int i = 0; i < 100; i++) {
            TbItem item = new TbItem();
            item.setId(i + 1L);
            item.setBrand("华为");
            item.setCategory("手机");
            item.setGoodsId(1L);
            item.setSeller("华为 2 号专卖店");
            item.setTitle("华为 Mate" + i);
            item.setPrice(new BigDecimal(2000 + i));
            list.add(item);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Test
    public void testFindOne() {
        TbItem tbItem = solrTemplate.getById(536563, TbItem.class);
        System.out.println(tbItem.getPrice());
    }
    @Test
    public void deleteAll() {
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
    @Test
    public void testPageQuery(){
        Query query=new SimpleQuery("*:*");
        query.setOffset(0);
        query.setRows(15);
        Criteria criteria=new Criteria("item_title");
        criteria.contains("小米");
        query.addCriteria(criteria);
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        showList(tbItems.getContent());
        System.out.println(tbItems.publicgetTotalElements());
    }

    public void showList(List<TbItem> list){
        for(TbItem item:list){
            System.out.println(item.getTitle() +item.getPrice());
        }
    }
    @Test
    public void importItemData(){
        solrUtil.importItemData();
    }
}
*/
