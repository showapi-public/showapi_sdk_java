package com.show.api;

import com.show.api.util.ShowApiUtils;
import com.show.api.util.WebUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * 基于REST的客户端。
 */
public class ShowApiRequest extends NormalRequest {

    public ShowApiRequest(String url, String appid, String appSecret) {
        super(url);
        this.addUrlPara("showapi_appid", appid);
        this.addUrlPara("showapi_sign", appSecret);
    }

    public String post() {
        String res = null;
        try {
            byte b[] = postAsByte();
            res = new String(b, "utf-8");
        } catch (Exception e) {
            if (printException) e.printStackTrace();
        }
        return res;
    }


    public byte[] postAsByte() {
        byte res[] = null;
        try {
            if(this.body!=null || this.bodyString!=null){
                this.urlMap.putAll(this.textMap);
                this.textMap.clear();
            }
            res = WebUtils.doPostAsByte(this);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                res = ("{\"showapi_res_code\":-1,\"showapi_res_error\":\"" + e.toString() + "\"}").getBytes("utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
        return res;
    }


    public String get() {
        String res = null;
        try {
            byte b[] = getAsByte();
            res = new String(b, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            res = "{\"showapi_res_code\":-1,\"showapi_res_error\":\"" + e.toString() + "\"}";
        }
        return res;
    }

    public byte[] getAsByte() {
        byte[] res = null;
        try {
            if(this.body!=null || this.bodyString!=null){
                this.urlMap.putAll(this.textMap);
                this.textMap.clear();
            }
            res = WebUtils.doGetAsByte(this);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                res = ("{\"showapi_res_code\":-1,\"showapi_res_error\":\"" + e.toString() + "\"}").getBytes("utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
        return res;
    }

    private String errorMsg(String msg) {
        String str = "{\"" + Constants.SHOWAPI_RES_CODE + "\":-1,\"" + Constants.SHOWAPI_RES_ERROR + "\":\"" + msg + "\",\"" + Constants.SHOWAPI_RES_BODY + "\":{}}";
        return str;
    }


//test case
//    public static void main(String[] args)  throws  Exception{
//        byte[] b= new ShowApiRequest("https://route.showapi.com/20-1",
//                "12312" ,"7be43eb....8f45")
//                .addTextPara("ip","116.4.201.181")
//                .getAsByte();
        //        System.out.println(new String(b,"utf-8"));

//        byte[] b=  new NormalRequest("https://route.showapi.com/20-1" )
//                .addUrlPara("showapi_appid","12312")
//                .addUrlPara("showapi_sign","7be43eb....8f45")
//                .addTextPara("ip","116.4.201.181")
//                .postAsByte();
//        System.out.println(new String(b,"utf-8"));

//        System.out.println(new ShowApiRequest("https://route.showapi.com/20-1",
//                "12312" ,"7be43eb....8f45")
//                .addTextPara("ip","116.4.201.181")
//                .get());

//        System.out.println(new ShowApiRequest("https://route.showapi.com/20-1",
//                "12312" ,"7be43eb....8f45")
//                .addTextPara("ip","116.4.201.181")
//                .post());

//    }


}

