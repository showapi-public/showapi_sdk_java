# showapi_sdk_java
showapi_sdk_java


| 方法名 | 说明| 参数|
|  -- | -- | -- |
| addTextPara | 向请求中添加一个字符串类型的请求参数| 第一个参数:参数名,第二个参数: 字符串|
| addFilePara | 向请求中添加一个File类型的请求参数| 第一个参数:参数名,第二个参数: File对象|
| post | 以post的方法,发送参数到url地址,返回json字符串| |
| fileToBase64 | 将文件转换为base64字符串 | 需要传入的参数是一个File对象|




## 	普通POST   demo
   ```html
   String res=new ShowApiRequest("http://route.showapi.com/64-19","my_appId","my_appSecret")
   	.addTextPara("com","zhongtong")
   	.addTextPara("nu","75312165465979")
   	.addTextPara("senderPhone","")
   	.addTextPara("receiverPhone","")
     .post();
   System.out.println(res);
   ```

## 	文件POST   demo
   ```html
   File file = new File("C:/Users/Admin/Desktop/test.png");//需要上传的文件对象
   String res=new ShowApiRequest("http://route.showapi.com/887-2","my_appId","my_appSecret")
   	 .addFilePara("img",file)  
     .post();
   System.out.println(res);
   ```

## 	base64处理   demo
   ```html
   File file = new File("C:/Users/Admin/Desktop/test.png");//需要转换的文件对象
   String res=ShowApiRequest.fileToBase64(file); //文件转换后得到的base64
   System.out.println(res);
   ```