# showapi_sdk_java
showapi_sdk_java

### 需要导入的包
```
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
```


## 	普通POST   demo
   ```html
public static void main(String[] args)  {
       //这里需要替换为你自己的appid和secret，你可以在这里找到 https://www.showapi.com/console#/myApp
       String showapi_appid = "XXXXX"; 
       String showapi_sign = "XXXXXXXXXXXXXXX";
       //这里的参数在对应接口的页面中查看(接口文档==>二、请求参数==>应用级参数)
       String com = "zhongtong";
       String nu= "75312165465979";
       //拼接接口所需的参数
       String paramsStr = "showapi_appid="+showapi_appid+"&showapi_sign="+showapi_sign+"&com=" + com +"&nu="+nu;
        //调用接口
       String result = sendPost("https://route.showapi.com/64-19",paramsStr,"utf-8");
       //得到返回参数
       System.out.println(result); 
    }


 /**
      *普通post请求接口的方法示例
      * @param uri
      * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
      * @param charset
      * @return
      */
     public static String sendPost(String uri, String param, String charset) {
         String result = null;
         PrintWriter out = null;
         InputStream in = null;
         try {
             URL url = new URL(uri);
             HttpURLConnection urlcon = (HttpURLConnection) url.openConnection(); //得到的是URLConnection对象
             urlcon.setDoInput(true); // 设置是否从httpUrlConnection读入，默认情况下是true;
             urlcon.setDoOutput(true);// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true, 默认情况下是false;
             urlcon.setUseCaches(false);// Post 请求不能使用缓存
             urlcon.setRequestMethod("POST");
             urlcon.connect();// 获取连接
             out = new PrintWriter(urlcon.getOutputStream());//获取输出流
             out.print(param);
             out.flush();
             in = urlcon.getInputStream();
             BufferedReader buffer = new BufferedReader(new InputStreamReader(in, charset));
             StringBuffer bs = new StringBuffer();
             String line = null;
             while ((line = buffer.readLine()) != null) {
                 bs.append(line);
             }
             result = bs.toString();
         } catch (Exception e) { System.out.println("[请求异常][地址：" + uri + "][参数：" + e.getMessage() + "]");
         } finally {
             try {
                 if (null != in)
                     in.close();
                 if (null != out)
                     out.close();
             } catch (Exception e2) {
                 System.out.println("[关闭流异常][错误信息：" + e2.getMessage() + "]");
             }
         }
         return result;
     }
   ```

## 	文件POST   demo
   ```html
public static void main(String[] args)  {
//这里需要替换为你自己的appid和secret，你可以在这里找到 https://www.showapi.com/console#/myApp
       String showapi_appid = "XXXXX"; 
       String showapi_sign = "XXXXXXXXXXXXXXX";

       //这里的参数在对应接口的页面中查看(接口文档==>二、请求参数)

//非文件类型的参数放在这里
        Map<String, String> params = new HashMap<String, String>();
        params.put("showapi_appid",showapi_appid);
        params.put("showapi_sign",showapi_sign);
//文件类型的参数放在这里
        Map<String, File> files = new HashMap<String, File>();
        File file=new File("C:\\Users\\showapi006\\Desktop\\QQ截图20200329125529.png");
        files.put("img",file);
//调用接口
        String result = filePost("https://route.showapi.com/887-4", params,files);
//得到接口返回的参数
        System.out.println(result);
    }

  /**
       *
       * @param urlStr http请求路径
       * @param params 请求参数
       * @param files 上传文件
       * @return
       */
      public static String filePost(String urlStr, Map<String, String> params,Map<String, File> files) {
          InputStream is = null;
          String result = "";
          // 定义数据分隔线
          String BOUNDARY = "========7d4a6d158c9";
          try {
              URL url = new URL(urlStr);
              HttpURLConnection con = (HttpURLConnection) url.openConnection();
  
              con.setConnectTimeout(5000);
              con.setDoInput(true);
              con.setDoOutput(true);
              con.setUseCaches(false);
              con.setRequestMethod("POST");// 设置为POST请求
              // 设置请求头参数
              con.setRequestProperty("Connection", "Keep-Alive");
              con.setRequestProperty("Charset", "UTF-8");
              con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
  
  
              StringBuilder sb = handleStrParams(params); //处理非文件类型参数
              DataOutputStream dos = new DataOutputStream(con.getOutputStream());
              if (sb != null) {
                  dos.write(sb.toString().getBytes());
              }
  
              if (files != null) { //处理文件参数
                  for (String s : files.keySet()) {
                      File f = files.get(s);
                      sb = new StringBuilder();
                      sb.append("--");
                      sb.append(BOUNDARY);
                      sb.append("\r\n");
                      sb.append("Content-Disposition: form-data; name=\"");
                      sb.append(s);
                      sb.append("\"; filename=\"");
                      sb.append(f.getName());
                      sb.append("\"\r\n");
                      sb.append("Content-Type: multipart/form-data");
                      sb.append("\r\n\r\n");// 参数头设置完以后需要两个换行，然后才是参数内容
                      dos.write(sb.toString().getBytes());
  
                      FileInputStream fis = new FileInputStream(f);
                      byte[] buffer = new byte[1024];
                      int len;
                      while ((len = fis.read(buffer)) != -1) {
                          dos.write(buffer, 0, len);
                      }
                      dos.write("\r\n".getBytes());
                      fis.close();
                  }
  
                  sb = new StringBuilder();
                  sb.append("--");
                  sb.append(BOUNDARY);
                  sb.append("--\r\n");
                  dos.write(sb.toString().getBytes());
              }
              dos.flush();
  
              if (con.getResponseCode() == 200)
                  is = con.getInputStream();
              dos.close();
  
              BufferedReader buffer = new BufferedReader(new InputStreamReader(is, "utf-8"));
              StringBuffer bs = new StringBuffer();
              String line = null;
              while ((line = buffer.readLine()) != null) {
                  bs.append(line);
              }
              result = bs.toString();
          } catch (Exception e) {
              System.out.println("发送POST请求出现异常！" + e);
              e.printStackTrace();
          }
          return result;
      }
  
      /**
       * 拼接串参数 
       * @param params
       * @return
       */
      public static StringBuilder handleStrParams(Map<String, String> params){
          if (params == null){
              return null;
          }
          StringBuilder sb  = new StringBuilder();
          for (String s : params.keySet()) {
              sb.append("--");
              sb.append("========7d4a6d158c9");
              sb.append("\r\n");
              sb.append("Content-Disposition: form-data; name=\"");
              sb.append(s);
              sb.append("\"\r\n\r\n");
              sb.append(params.get(s));
              sb.append("\r\n");
          }
          return sb;
      }
   ```

## 	base64处理   demo
   ```html
public static void main(String[] args)  {
   File file = new File("C:/Users/Admin/Desktop/test.png");//需要转换的文件对象
   String res=ShowApiRequest.fileToBase64(file); //文件转换后得到的base64
   System.out.println(res);
}


/**
	 * 
	 * @param file
	 * @return
	 */
public static String fileToBase64(File file){
		byte[] buffer = null;
		try{
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int n;
			while ((n = fis.read(b)) != -1)
			{
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
		String str = javax.xml.bind.DatatypeConverter.printBase64Binary(buffer);
		return str;
	}
   ```