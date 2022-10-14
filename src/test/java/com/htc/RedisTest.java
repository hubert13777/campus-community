package com.htc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        String key="test:count";

        redisTemplate.opsForValue().set(key,1);
        System.out.println(redisTemplate.opsForValue().get(key));
        System.out.println(redisTemplate.opsForValue().increment(key));
        System.out.println(redisTemplate.opsForValue().decrement(key));
        System.out.println(redisTemplate.opsForValue().get(key));

    }

    @Test
    public void testHash(){
        String key="test:user";

        redisTemplate.opsForHash().put(key,"id",1);
        redisTemplate.opsForHash().put(key,"name","张三");

        System.out.println(redisTemplate.opsForHash().get(key,"id"));
        System.out.println(redisTemplate.opsForHash().get(key,"name"));
        System.out.println(redisTemplate.opsForHash().get(key,"password")); //应该是null
    }

    @Test
    public void testList(){
        String key="test:ids";

        //放入最左边
        redisTemplate.opsForList().leftPush(key,101);
        redisTemplate.opsForList().leftPush(key,102);
        redisTemplate.opsForList().leftPush(key,103);
        //查看
        System.out.println(redisTemplate.opsForList().size(key));
        System.out.println(redisTemplate.opsForList().index(key,0));
        System.out.println(redisTemplate.opsForList().range(key,0,2));
        //弹出
        System.out.println(redisTemplate.opsForList().leftPop(key));
        System.out.println(redisTemplate.opsForList().rightPop(key));
    }

    @Test
    public void testSet(){
        String key="test:teachers";

        redisTemplate.opsForSet().add(key,"刘老师","张老师","李老师");

        System.out.println(redisTemplate.opsForSet().size(key));
        System.out.println(redisTemplate.opsForSet().pop(key)); //随机弹出一个值
        System.out.println(redisTemplate.opsForSet().members(key)); //遍历输出
    }

    @Test
    public void testSortedSet(){
        String key="test:students";

        redisTemplate.opsForZSet().add(key,"张三",3);
        redisTemplate.opsForZSet().add(key,"李四",4);
        redisTemplate.opsForZSet().add(key,"钱六",6);
        redisTemplate.opsForZSet().add(key,"王五",5);

        System.out.println(redisTemplate.opsForZSet().zCard(key));  //统计元素个数
        System.out.println(redisTemplate.opsForZSet().score(key,"李四")); //查看某个元素的值
        System.out.println(redisTemplate.opsForZSet().rank(key,"李四")); //查看某个元素的索引
        System.out.println(redisTemplate.opsForZSet().reverseRank(key,"李四")); //倒序排名
        System.out.println(redisTemplate.opsForZSet().range(key,0,2)); //升序取前三
        System.out.println(redisTemplate.opsForZSet().reverseRange(key,0,2)); //降序
    }

    @Test
    public void testKey(){
        String key="test:user";

        redisTemplate.delete(key);
        System.out.println(redisTemplate.hasKey(key));  //判断是否存在该key
        redisTemplate.expire("test:students",10, TimeUnit.SECONDS); //设置到期时间
    }

    //多次访问同一个key
    @Test
    public void testBoundOperation(){
        String key="test:count";
        BoundValueOperations operations=redisTemplate.boundValueOps(key);   //绑定key
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    @Test
    //编程式事务
    public void testTransaction(){
        Object object=redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String key="test:transaction";
                operations.multi();     //启用事务

                operations.opsForSet().add(key,"张三");
                operations.opsForSet().add(key,"李四");
                operations.opsForSet().add(key,"王五");

                //此时还未提交，所以查询不到数据
                System.out.println(operations.opsForSet().members(key));

                return operations.exec();   //提交
            }
        });
        System.out.println(object);
    }
}