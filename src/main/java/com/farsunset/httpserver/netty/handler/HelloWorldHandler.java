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
package com.farsunset.httpserver.netty.handler;


import com.farsunset.httpserver.bean.User;
import com.farsunset.httpserver.dto.Response;
import com.farsunset.httpserver.netty.annotation.NettyHttpHandler;
import com.farsunset.httpserver.netty.http.NettyHttpRequest;
import com.farsunset.httpserver.utils.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NettyHttpHandler(path = "/hello")
public class HelloWorldHandler implements IFunctionHandler<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldHandler.class);

    @Override
    public Response<String> execute(NettyHttpRequest request) {
        try {
            User user = Convert.reqToObject(request, User.class);
            return Response.ok("Hello World");
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return Response.fail(e.getMessage());
        }
    }
}
