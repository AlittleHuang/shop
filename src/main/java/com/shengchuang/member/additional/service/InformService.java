package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.domain.Inform;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.repository.query.Criteria;
import com.shengchuang.common.util.Assert;
import com.shengchuang.base.AbstractService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.regex.Pattern;

@Service
public class InformService extends AbstractService<Inform, Integer> {

    Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
    Pattern evalPattern = scriptPattern = Pattern.compile("eval\\((.*?)\\)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    Pattern expressionPattern = Pattern.compile("e­xpression\\((.*?)\\)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    Pattern hrefPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
    Pattern vbscriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
    Pattern onloadPattern = Pattern.compile("onload(.*?)=",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    public static void main(String[] args) {
        new InformService().checkXSS("javascript:");
    }

    public void checkXSS(String value) {
        if (value == null)
            return;
        Assert.state(!scriptPattern.matcher(value).find(), "内容含有非法字符");
        Assert.state(!evalPattern.matcher(value).find(), "内容含有非法字符");
        Assert.state(!expressionPattern.matcher(value).find(), "内容含有非法字符");
        Assert.state(!hrefPattern.matcher(value).find(), "内容含有非法字符");
        Assert.state(!vbscriptPattern.matcher(value).find(), "内容含有非法字符");
        Assert.state(!onloadPattern.matcher(value).find(), "内容含有非法字符");
    }

    @Override
    public <S extends Inform> S save(S entity) {
        checkXSS(entity.getHtml());
        return super.save(entity);
    }

    public Page<Inform> getPage(PageRequestMap pageRequestMap) {
        Criteria<Inform> conditions = createCriteria(pageRequestMap).addOrderByDesc("time", "id");
        conditions.andEqIgnoreEmpty("type", pageRequestMap.get("type"));
        return getPage(conditions);
    }

    /**
     * 上一条
     */
    public Inform getPre(int id) {
        Criteria<Inform> conditions = createCriteria().addOrderByDesc("time", "id");
        Inform info = getOne(id);
        if (info != null && info.getTime() != null)
            conditions.and().andLt("time", info.getTime(), Date.class);
        conditions.limit(1);
        conditions.addOrderByDesc("id");
        return findOne(conditions);
    }

    /**
     * 下一条
     */
    public Inform getNext(int id) {
        Criteria<Inform> conditions = createCriteria().addOrderByAsc("time", "id");
        Inform info = getOne(id);
        if (info != null && info.getTime() != null)
            conditions.and().andGt("time", info.getTime(), Date.class);
        conditions.limit(1);
        conditions.addOrderByAsc("id");
        return findOne(conditions);
    }

    public void update(Inform inform) {
        if (inform.getTime() == null) {
            inform.setTime(new Date());
        }
        save(inform);
    }
}
