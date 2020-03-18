
package com.show.api.test;

import com.show.api.NormalRequest;
import com.show.api.ShowApiRequest;

import java.io.File;

public class ShowapiTest {
    public ShowapiTest() {

    }

    /***
     * 测试接口 ip归属地
     */
    public static void main(String[] args)  {
        File file = new File("C:\\Users\\showapi006\\Desktop\\QQ截图20200317161547.png");
        String res=ShowApiRequest.fileToBase64(file);
        System.out.println(res);
        res = "";
        res = NormalRequest.fileToBase64(file);
        System.out.println(res);

    }

}
