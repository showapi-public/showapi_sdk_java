
package com.show.api.test;

import com.show.api.ShowApiRequest;

public class ShowapiTest {

    /***
     * 测试接口 二维码识别
     */
    public static void main(String[] args)  {
        //更多说明请访问仓库地址：https://github.com/showapi-public/showapi_sdk_java
        //这里需要替换为你自己的showapi_appid和showapi_sign，你可以在这里找到 https://www.showapi.com/console#/myApp
        ShowApiRequest req=new ShowApiRequest("http://route.showapi.com/887-4","showapi_appid","showapi_sign");

        req.addBase64Para("imgData","filePath");

        req.addTextPara("handleImg","1");

        String res=req.post();
        System.out.println(res);

    }

}
