package com.htc.entity;

/**
 * 封装分页相关信息
 */
public class Page {
    private int current = 1;      //当前页码
    private int limit = 10;       //显示上限
    private int rows;           //数据总数，用于计算总页数
    private String path;        //查询路径，用于复用分页连接
    
    @Override
    public String toString() {
        return "Page{" +
                "current=" + current +
                ", limit=" + limit +
                ", rows=" + rows +
                ", path='" + path + '\'' +
                '}';
    }
    
    public int getCurrent() {
        return current;
    }
    
    public void setCurrent(int current) {
        this.current = current;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public void setLimit(int limit) {
        //避免单页显示上限过大
        if (limit >= 1 && limit <= 50) {
            this.limit = limit;
        }
    }
    
    public int getRows() {
        return rows;
    }
    
    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    /**
     * 获取当前页的起始行，从0开始计数
     */
    public int getOffset() {
        return (current - 1) * limit;
    }
    
    /**
     * 获取总页数
     */
    public int getTotal() {
        return (rows % limit == 0) ? rows / limit : rows / limit + 1;
    }
    
    /**
     * 获取显示在页面上的起始页码
     */
    public int getStart() {
        int start = current - 2;
        return (start < 1) ? 1 : start;
    }
    
    /**
     * 获取显示在页面上的末尾页码
     */
    public int getEnd() {
        int last = getTotal();  //总页数
        int end = current + 2;
        return (end > last) ? last : end;
    }
}
