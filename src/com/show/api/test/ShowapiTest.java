
package com.show.api.test;

import com.show.api.NormalRequest;
import com.show.api.ShowApiRequest;

import java.io.File;

public class ShowapiTest {

    /***
     * 测试接口 ip归属地
     */
    public static void main(String[] args)  {
        File file = new File("d:\\validity.jpg");
        String res=ShowApiRequest.fileToBase64(file);
        System.out.println(res);
        res = "";
        res = NormalRequest.fileToBase64(file);
        System.out.println(res);

    }

}
