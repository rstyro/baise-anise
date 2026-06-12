package com.lrs.core.app.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页结果 VO
 */
@Data
public class PageResultVo<T> {

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页大小
     */
    private long size;

    /**
     * 当前页码
     */
    private long current;

    /**
     * 总页数
     */
    private long pages;
}