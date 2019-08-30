/**
 * Copyright 2013-2033 Xia Jun(3979434@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***************************************************************************************
 *                                                                                     *
 *                        Website : http://www.farsunset.com                           *
 *                                                                                     *
 ***************************************************************************************
 */
package com.farsunset.httpserver;

import com.farsunset.httpserver.netty.annotation.NettyHttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(includeFilters = @ComponentScan.Filter(NettyHttpHandler.class))

public class HttpServerApplication implements CommandLineRunner {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerApplication.class);
    
    public static void main(String[] args) {
        //非web启动，其实springboot启动的时候会自己根据Classpath拥有的类进行判断
        new SpringApplicationBuilder(HttpServerApplication.class).web(WebApplicationType.NONE).run(args);
    }
    
    @Override
    public void run ( String... strings ) throws Exception {
    
        LOGGER.info ("CommandLineRunner");
        
    }
    /**
     * ApplicationStartingEvent
     ApplicationEnvironmentPreparedEvent
     ApplicationPreparedEvent
     
     ApplicationStartedEvent <= 新增的事件
     CommandLineRunner
     ApplicationReadyEvent
     ApplicationFailedEvent
     
     */
}
