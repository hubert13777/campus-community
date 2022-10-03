package com.htc.tool;

import com.alibaba.druid.sql.visitor.functions.Char;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LogManager.getLogger(SensitiveFilter.class);

    //替换符号
    private static final String REPLACEMENT = "*";

    //根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct  //容器初始化时，创建对象后自动调用该方法
    public void init() {
        //括号中生成的会自动在finally中关闭
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String word;
            while ((word = reader.readLine()) != null) {
                //添加到前缀树
                this.addWord(word);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }

    }

    private void addWord(String word) {
        if (StringUtils.isBlank(word)) {  //判断是否为空
            return;
        }
        TrieNode currNode = rootNode;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            TrieNode temp = currNode.getSubNode(c);
            if (temp == null) {
                temp = new TrieNode();
                currNode.addSubNode(c, temp);
            }
            currNode = temp;
        }
        currNode.setEnd(true);  //表示是一个敏感词的结尾
    }

    /**
     * 过滤敏感词
     *
     * @param text 代过滤文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        //前缀树中移动的指针1
        TrieNode t1 = rootNode;
        //字符串中移动的头指针2
        int index_begin = 0;
        //作为末尾的指针3
        int index_end = 0;
        StringBuilder sb = new StringBuilder();

        while (index_begin < text.length()) {
            //判断end是否越界
            if (index_end < text.length()) {
                char c = text.charAt(index_end);
                //需要跳过特殊符号
                if (isSymbol(c)) {    //如果是
                    //若t1处于根节点，就跳过，该符号记入
                    if (t1 == rootNode) {
                        sb.append(c);
                        index_begin++;
                    }
                    index_end++;    //指针3必定向下走
                    continue;
                }

                //检查下级节点
                TrieNode tempNode = t1.getSubNode(c);
                if (tempNode == null) {
                    //说明以begin开始的字符不是敏感词
                    sb.append(text.charAt(index_begin));    //只将begin放入
                    index_begin += 1;
                    index_end = index_begin;
                    t1 = rootNode;    //t1重新指向root
                } else if (tempNode.isEnd) {
                    //发现敏感词，begin-end的字符串需要被替换
                    for (int i = index_begin; i <= index_end; i++) {
                        sb.append(REPLACEMENT);
                    }
                    index_end += 1;
                    index_begin = index_end;
                    t1 = rootNode;
                } else {
                    //暂时还不是敏感词，继续检查下一个
                    index_end++;
                    t1=tempNode;
                }
            }else{
                //说明此时end已经越界，且begin~end未匹配到敏感词
                sb.append(text.charAt(index_begin));
                index_end=++index_begin;
                t1=rootNode;
            }
        }
        return sb.toString();
    }

    /**
     * 判断字符是否为符号
     *
     * @return true表示是符号
     */
    private boolean isSymbol(char c) {
        //0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    /**
     * 前缀树节点类
     */
    private class TrieNode {
        //true是单词结尾标志，默认为false
        private boolean isEnd = false;
        //记录子节点（字符-下一级节点）
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isEnd() {
            return isEnd;
        }

        public void setEnd(boolean end) {
            isEnd = end;
        }

        //添加子节点
        public void addSubNode(char c, TrieNode node) {
            subNodes.put(c, node);
        }

        //获取子节点
        public TrieNode getSubNode(char c) {
            return subNodes.get(c);
        }
    }
}
