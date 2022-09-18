package com.htc.toolTest;

import com.alibaba.fastjson2.JSONObject;
import com.htc.tool.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SensitiveFilterTest {
    @Autowired
    private SensitiveFilter filter;

    @Test
    public void test(){
        String text,res;
        //特殊符号测试
        text="!@#$%^&*()_+=-";
        res=filter.filter(text);
        System.out.printf("text=%s\nres =%s\n",text,res);
        //正常字符串测试
        text="莱纳，你坐啊";
        res=filter.filter(text);
        System.out.printf("text=%s\nres =%s\n",text,res);
        //含敏感词测试
        text="赌博、嫖娼，是不对的";
        res=filter.filter(text);
        System.out.printf("text=%s\nres =%s\n",text,res);
        //abcd和bcd形式测试
        // text="xxxabcd";
        // res=filter.filter(text);
        // System.out.printf("text=%s\nres =%s\n",text,res);
        //abcd和bc测试
        text="xxxabc";
        res=filter.filter(text);
        System.out.printf("text=%s\nres =%s\n",text,res);
    }
}
