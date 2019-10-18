/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.talkweb.basecomp.common.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.talkweb.basecomp.Constants;

import tk.mybatis.spring.mapper.MapperScannerConfigurer;

import java.util.Properties;


/**
 * <p>Description:MyBatis扫描接口，使用的tk.mybatis.spring.mapper.MapperScannerConfigurer，如果你不使用通用Mapper，可以改为org.xxx...</p>
 * <p>创建日期:2017年12月14日</p>
 * @author thh
 * @version 1.0
 * <p>拓维教育营销体系-技术平台部</p>
 */
@Configuration
//TODO 注意，由于MapperScannerConfigurer执行的比较早，所以必须有下面的注解
@AutoConfigureAfter(MyBatisConfig.class)
public class MyBatisMapperScannerConfig {
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName(
            "sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage(Constants.BASE_PACKAGE_NAME + ".**.dao");

        Properties properties = new Properties();
        properties.setProperty("mappers",
        		this.getClass().getPackage().getName() + ".MyMapper");
        properties.setProperty("notEmpty", "false");
        properties.setProperty("IDENTITY", "MYSQL");
        //properties.setProperty("IDENTITY", "PostgreSQL");
        mapperScannerConfigurer.setProperties(properties);

        return mapperScannerConfigurer;
    }
}
