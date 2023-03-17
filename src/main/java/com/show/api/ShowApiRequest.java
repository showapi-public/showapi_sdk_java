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
    private String appSecret;


    public ShowApiRequest(String url, String appid, String appSecret) {
        super(url);
        this.appSecret = appSecret;
        this.addUrlPara("showapi_appid", appid);
    }


    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
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

            String signResult = addSign();  //这里是在textArea中添加sian参数
            if (signResult != null) return signResult.getBytes("utf-8");
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

    private String addSign() throws IOException {
        Map<String ,String> allParam=new HashMap<String ,String>();
        if (urlMap!=null&&urlMap.get(Constants.SHOWAPI_APPID) == null) return errorMsg(Constants.SHOWAPI_APPID + "不得为空!");
        allParam.putAll(urlMap);//加入url参数
        allParam.putAll(textMap);//加入header参数

        urlMap.put(Constants.SHOWAPI_SIGN, ShowApiUtils.signRequest(allParam, appSecret));
        return null;
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
            String signResult = addSign();
            if (signResult != null) return signResult.getBytes("utf-8");
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



    public static void main(String[] args)  throws  Exception{
        byte[] b=  new NormalRequest("http://httpbin.org/anything?xxxx=5555&yyy=6666" )
                .addUrlPara("aaa","111")
                .addUrlPara("bbb","222")
                .addTextPara("ccc","中文")
//                .setBodyString("this is body text")
                .addFilePara("myfile",new File("c:/640.png"))
                .postAsByte();
        System.out.println(new String(b,"utf-8"));
    }
}

