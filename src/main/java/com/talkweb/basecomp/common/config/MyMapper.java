package com.talkweb.basecomp.common.config;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;


/**
 * <p>Title: 爱阅读</p>
 * <p>Description:被继承的Mapper，一般业务Mapper继承它</p>
 * <p>创建日期:2017年12月14日</p>
 * @author thh
 * @version 1.0 
 * <p>拓维教育营销体系-技术平台部</p>
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
    //TODO
    //FIXME 特别注意，该接口不能被扫描到，否则会出错
}
