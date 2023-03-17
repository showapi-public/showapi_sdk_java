package com.show.api.util;

import com.show.api.FileItem;
import com.show.api.NormalRequest;
import com.show.api.ShowApiRequest;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * 网络工具类。
 */
public   class WebUtils {

	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";

	public static class VerisignTrustManager implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//			Exception exp = null;
//			for (X509Certificate cert : chain) {
//				cert.checkValidity(); // 验证证书有效期
//				try {
//					cert.verify(verisign.getPublicKey());// 验证签名
//					exp = null;
//					break;
//				} catch (Exception e) {
//					exp = e;
//				}
//			}
//
//			if (exp != null) {
//				throw new CertificateException(exp);
//			}
		}
	}
	
	
	public static class TrustAllTrustManager implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
	}

	public static ResData _do_execute(NormalRequest req,String method) throws IOException {
		HttpURLConnection conn = null;
		OutputStream out = null;
		byte[] rsp = null;
		byte[] body = new byte[0];
		ResData res=new ResData();
		String charset= req.getCharset();
		String ctype = "application/x-www-form-urlencoded;charset=" + charset;
		String query="";
		String boundary="";
		if(method.equals(METHOD_GET)){
			ctype = "application/x-www-form-urlencoded;charset=" + charset;
			query = buildQuery(req.getUrlMap(),req.getTextMap() ,charset  );

		}else if(method.equals(METHOD_POST)){
			query = buildQuery(req.getUrlMap(),null ,charset  );  //先生成url参数
			boolean upload=false;
			if(req.getUploadMap()!=null && req.getUploadMap().size()>0){
				boundary = String.valueOf(System.nanoTime()); // 随机分隔线
				ctype = "multipart/form-data;charset=" + charset + ";boundary=" + boundary;
				upload=true;
			}else{
				ctype = "application/x-www-form-urlencoded;charset=" + charset;
				ctype=getCtype(req,ctype);
			}
			if(req.getBody()!=null){
				body=req.getBody();
			}else if(req.getBodyString()!=null){
				body=req.getBodyString().getBytes(charset);
			}else{//表单字段
				if(upload){
					ByteArrayOutputStream bout=new ByteArrayOutputStream();
					byte[] entryBoundaryBytes = ("\r\n--" + boundary + "\r\n").getBytes(charset);
					// 组装文本请求参数
					Set<Entry<String, String>> textEntrySet =  req.getTextMap().entrySet();
					for (Entry<String, String> textEntry : textEntrySet) {
						byte[] textBytes = getTextEntry(textEntry.getKey(), textEntry.getValue(), charset);
						bout.write(entryBoundaryBytes);
						bout.write(textBytes);
					}

					// 组装文件请求参数
					Set<Entry<String, File>> fileEntrySet = req.getUploadMap().entrySet();
					for (Entry<String, File> fileEntry : fileEntrySet) {
						File f=fileEntry.getValue();
						FileItem fileItem = new FileItem(f);
						if (fileItem.getContent() == null) {
							continue;
						}
						byte[] fileBytes = getFileEntry(fileEntry.getKey(), fileItem.getFileName(), fileItem.getMimeType(), charset);
						bout.write(entryBoundaryBytes);
						bout.write(fileBytes);
						bout.write(fileItem.getContent());
					}

					// 添加请求结束标志 ,最后加上2个--
					byte[] endBoundaryBytes = ("\r\n--" + boundary + "--\r\n").getBytes(charset);
					bout.write(endBoundaryBytes);
					body=bout.toByteArray();
//					System.out.println(new String(body));
				}else{

					String body_content = buildQuery(null,req.getTextMap() ,charset  );

					if(body_content!=null){
//						System.out.println(req.getTextMap());
						body=body_content.getBytes(charset  );
					}
				}
			}
		}

		//整体统一提交
		URL url=buildGetUrl(req.getUrl(), query);
		try {
			conn = getConnection(url, method, ctype,req);
			conn.setConnectTimeout(req.getConnectTimeout());
			conn.setReadTimeout(req.getReadTimeout());

			if(!method.equals(METHOD_GET)){

				out = conn.getOutputStream();
				out.write(body);
				out.flush();
			}
			Map res_headMap=conn.getHeaderFields();
			rsp = getResponseAsByte(conn,getLimitReadSize(req));
			req.setRes_headMap(res_headMap); //设置返回头
			req.setRes_status(conn.getResponseCode());
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		res.setResData(rsp);
		res.setRes_maybe_encoding(getResponseCharset(conn.getContentType()));
		return res;
	}
	
	public static String doPost(NormalRequest req) throws IOException {
		ResData res=_do_execute(req,METHOD_POST);
		return new String(res.getResData(),res.getRes_maybe_encoding());
	}
	
	public static byte[] doPostAsByte(NormalRequest req) throws IOException {
		ResData res=_do_execute(req,METHOD_POST);
		return res.getResData();
	}

	

	public static String doGet(NormalRequest req) throws IOException {
		ResData res=_do_execute(req,METHOD_GET);
		return new String(res.getResData(),res.getRes_maybe_encoding());
	}


	public static byte[] doGetAsByte(NormalRequest req) throws IOException {
		ResData res=_do_execute(req,METHOD_GET);
		return res.getResData();
	}
	

	public static ByteArrayOutputStream unzip(InputStream in,int limit_size) {
		ByteArrayOutputStream fout = new ByteArrayOutputStream();
		try {
			// 建立gzip解压工作流
			GZIPInputStream gzin = new GZIPInputStream(in);
			// 建立解压文件输出流
			byte[] buf = new byte[1024];
			int num;
            int allNum=0;
			while ((num = gzin.read(buf, 0, buf.length)) != -1) {
				fout.write(buf, 0, num);
                allNum+=num;
                if(allNum>=limit_size)break;
			}
			gzin.close();
			fout.close();
			in.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return fout;
	}

	private static int getLimitReadSize(NormalRequest req){
        int limit_size=Integer.MAX_VALUE;
        if(req.getLimitReadSize()>0){limit_size=req.getLimitReadSize();}
        return limit_size;
    }
	private static byte[] getTextEntry(String fieldName, String fieldValue, String charset) throws IOException {
		StringBuilder entry = new StringBuilder();
		entry.append("Content-Disposition:form-data; name=\"");
		entry.append(fieldName);
		entry.append("\"\r\nContent-Type:text/plain\r\n\r\n");
		entry.append(fieldValue);
//		System.out.println(entry);
		return entry.toString().getBytes(charset);
	}

	private static byte[] getFileEntry(String fieldName, String fileName, String mimeType, String charset) throws IOException {
		StringBuilder entry = new StringBuilder();
		entry.append("Content-Disposition:form-data; name=\"");
		entry.append(fieldName);
		entry.append("\"; filename=\"");
		entry.append(fileName);
		entry.append("\"\r\nContent-Type:");
		entry.append(mimeType);
		entry.append("\r\n\r\n");
//		System.out.println(entry);
		return entry.toString().getBytes(charset);
	}


	//获取头里的content-type，如果没有，则根据req的请求类型，返回一个content-type
	private static String getCtype(NormalRequest req,String default_type) {
		String ret=null;
		Map params=req.getHeadMap();
		if (params == null || params.isEmpty()) {
			return null;
		}
		Set<Entry<String, String>> entries = params.entrySet();
		try {
			for (Entry<String, String> entry : entries) {
				String name = entry.getKey().toLowerCase();
				String value = entry.getValue();
				// 忽略参数名或参数值为空的参数
				if(name.equals("content-type")){
					ret=value;
					break;
				}
			}
		} catch ( Exception e) {
			e.printStackTrace();
		}
		if(ret==null){//如果在头中找不到定义
			if(req.getBody()!=null||req.getBodyString()!=null){ //如果强制设置了输入体，则做一个默认content-type
				ret = "application/octet-stream;charset=" +req.getCharset();
			}
		}

		if(ret!=null)return ret;
		else return default_type;
	}


	private static HttpURLConnection getConnection(URL url, String method, String ctype,NormalRequest req)throws IOException {
		HttpURLConnection conn;
		Proxy proxy=req.getProxy();
		if(proxy!=null){
			conn = (HttpURLConnection) url.openConnection(proxy);
		}else{
			conn = (HttpURLConnection) url.openConnection( );
		}
		if (conn instanceof HttpsURLConnection) {
			HttpsURLConnection connHttps = (HttpsURLConnection) conn;
			try {
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(null, new TrustManager[] { new TrustAllTrustManager() }, new SecureRandom());
				connHttps.setSSLSocketFactory(ctx.getSocketFactory());
				connHttps.setHostnameVerifier(new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
			} catch (Exception e) {
				throw new IOException(e);
			}
			conn = connHttps;
		}
		conn.setRequestMethod(method);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Accept", "application/json, text/javascript, */*; ");
		conn.setRequestProperty("User-Agent", "showapi-sdk-java");
		conn.setRequestProperty("Content-Type", ctype);
		Map<String, String>  headerMap=req.getHeadMap();
		if (headerMap != null) {
			for (Map.Entry<String, String> entry : headerMap.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());  //以头里发来的为优先
			}
		}
		conn.setInstanceFollowRedirects(req.isAllowRedirect());
		return conn;
	}

	private static URL buildGetUrl(String strUrl, String query) throws IOException {
		URL url = new URL(strUrl);
		if (StringUtils.isEmpty(query)) {
			return url;
		}

		if (StringUtils.isEmpty(url.getQuery())) {
			if (strUrl.endsWith("?")) {
				strUrl = strUrl + query;
			} else {
				strUrl = strUrl + "?" + query;
			}
		} else {
			if (strUrl.endsWith("&")) {
				strUrl = strUrl + query;
			} else {
				strUrl = strUrl + "&" + query;
			}
		}
		return new URL(strUrl);
	}

	public static String buildQuery(Map<String, String>  urlMap,Map<String, String>  params,String charset )   {
		Map<String ,String> allParam=new HashMap<String ,String>();
		if(urlMap!=null){allParam.putAll(urlMap);}
		if(params!=null){allParam.putAll(params);}


		StringBuilder query = new StringBuilder();
		Set<Entry<String, String>> entries = allParam.entrySet();
		boolean hasParam = false;
		try {
			for (Entry<String, String> entry : entries) {
				String name = entry.getKey();
				String value = entry.getValue();
				// 忽略参数名或参数值为空的参数
				if(name!=null&&name.length()>0&&value!=null){
					if (hasParam) {
						query.append("&");
					} else {
						hasParam = true;
					}
					query.append(name).append("=").append(URLEncoder.encode(value, charset));
				}
			}
		} catch ( Exception e) {
			e.printStackTrace();
		}

		return query.toString();
	}
	protected static byte[]  getResponseAsByte(HttpURLConnection conn ,int limit_size) throws IOException {
		InputStream es = conn.getErrorStream();  //头>=400时，会有errorStream
		if (es == null) {
			if(conn.getContentEncoding()!=null&&conn.getContentEncoding().toLowerCase().equals("gzip")){
				byte bbb[]=unzip(conn.getInputStream(),  limit_size).toByteArray();
				return  bbb;
			}else{
				return _readByteFromStream(conn.getInputStream(),limit_size );
			}
		} else {
			return _readByteFromStream(es ,limit_size);
		}
	}

	protected static String getResponseAsString(HttpURLConnection conn,int limit_size ) throws IOException {
		String charset_res = getResponseCharset(conn.getContentType());
		byte ret[]=getResponseAsByte(conn,limit_size);
        return  new String(ret,charset_res);
	}
	
	private static byte[] _readByteFromStream(InputStream stream ,int limit_size) throws IOException  {
		try {
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			byte buf[]=new byte[1024];
			int read = 0;
			int allNum=0;
			while ((read = stream.read(buf)) > 0) {
				out.write(buf, 0, read);
				allNum+=read;
				if(allNum>=limit_size)break;
			}
			return out.toByteArray();
		}  finally {
			if (stream != null) {
				stream.close();
			}
		}
	}


	private static String getResponseCharset(String ctype) {
		String charset = "utf-8";
		if (!StringUtils.isEmpty(ctype)) {
			String[] params = ctype.split(";");
			for (String param : params) {
				param = param.trim();
				if (param.startsWith("charset")) {
					String[] pair = param.split("=", 2);
					if (pair.length == 2) {
						if (!StringUtils.isEmpty(pair[1])) {
							charset = pair[1].trim();
						}
					}
					break;
				}
			}
		}
		return charset;
	}

}
