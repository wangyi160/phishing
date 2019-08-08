import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import org.apache.http.client.entity.DecompressingEntity;

import com.proxy.utils.StringUtil;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class HttpClientTest {

	@Test // (expected = SSLPeerUnverifiedException.class)
	public void whenHttpsUrlIsConsumed_thenException()
			throws ClientProtocolException, IOException, InterruptedException {

		CloseableHttpClient httpClient = HttpClients.createDefault();
		String urlOverHttps = "https://www.dy2018.com";
		// HttpGet httpGet = new HttpGet(urlOverHttps);

		// setting custom http headers on the http request
		HttpUriRequest request = RequestBuilder.get().setUri(urlOverHttps)
				.setHeader(HttpHeaders.ACCEPT, "*/*")
				.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, ")
				.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9")
				.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36")
				.build();

		System.out.println("Executing request " + request.getRequestLine());

		// Create a custom response handler
		@SuppressWarnings("deprecation")
		ResponseHandler<String> responseHandler = response -> {
			int status = response.getStatusLine().getStatusCode();
			System.out.println(status);
			
			Header[] resHeaders = response.getAllHeaders();
			for(Header h: resHeaders)
			{
				System.out.println(h.getName()+":"+h.getValue());
			}			
						
			
//			if (status >= 200 && status < 300) {
			HttpEntity entity = response.getEntity();
			System.out.println(entity.getContentEncoding());
			
			
						
			byte[] bytes=EntityUtils.toByteArray(entity);
			try {
				System.out.println(StringUtil.detectCharsetImpl(bytes));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			System.out.println(bytes.length);
			
			return new String(bytes, "GB18030") ;
				
				
				
//			} else {
//				throw new ClientProtocolException("Unexpected response status: " + status);
//			}
		};

		String responseBody = httpClient.execute(request, responseHandler);
		System.out.println("----------------------------------------");
		System.out.println(responseBody);

		// System.out.println(response.getEntity().getContentLength());

		// assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
	}

}
