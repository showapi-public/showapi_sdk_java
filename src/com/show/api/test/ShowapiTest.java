
package com.show.api.test;

import com.show.api.ShowApiRequest;

public class ShowapiTest {
    public ShowapiTest() {

    }

    /***
     * 测试接口 ip归属地
     */
    public static void main(String[] args) {
        String s = new ShowApiRequest("http://route.showapi.com/20-1", "111", "222")
                .addTextPara("ip","8.8.8.8")
                .post();
        System.out.println(s);
    }

}
