package com.farsunset.httpserver.utils;/**
 * @Author: lds
 * @Desc:
 * @Date: Create in 18:13 2019/8/31
 */

import com.farsunset.httpserver.bean.User;
import com.farsunset.httpserver.netty.http.NettyHttpRequest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: lds
 * @Desc:
 * @Date: Create in 18:13 2019/8/31
 */
public class Convert {

    public static <T> T reqToObject(NettyHttpRequest request,Class<T> cl) throws Exception{
        String content = request.contentText();
        String[] strArr = content.split("&");
        HashMap<String,String> hashMap = new HashMap<>();
        for (String s:strArr) {
            String[] att = s.split("=");
            hashMap.put(att[0],att[1]);

        }
        T o = cl.newInstance();
        Field[] fields = cl.getDeclaredFields();
        for(Map.Entry<String,String> entry: hashMap.entrySet()) {
            for (Field f : fields) {
                if(f.getName().equals(entry.getKey())) {
                    f.setAccessible(true);
                    f.set(o,entry.getValue());
                }

            }
        }

        return o;
    }
}
