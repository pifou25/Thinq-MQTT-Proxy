import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.http.HttpRequest
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.config.Registry
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustStrategy
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.BasicHttpClientConnectionManager
import org.apache.http.message.BasicNameValuePair
import org.apache.http.protocol.HTTP
import org.apache.http.ssl.SSLContexts
import org.apache.http.util.EntityUtils

import javax.net.ssl.SSLContext

static void httpGet(LinkedHashMap<String, Object> map, Closure<Object> closure) {
    String host = map.get("uri") + map.getOrDefault("path", "")
    HttpRequest request = new org.apache.http.client.methods.HttpGet(host)
    request.addHeader("Content-Type", map.getOrDefault("requestContentType", "application/json"))
    http(request, map, closure)
}

static void httpPost(LinkedHashMap<String, Object> map, Closure<Object> closure) {
    String host = map.get("uri") + map.getOrDefault("path", "")
    HttpRequest request = new org.apache.http.client.methods.HttpPost(host)
    if (map.getOrDefault("body", null) != null) {
        if (map.getOrDefault("requestContentType", "") == "application/json") {
            request.setEntity(new StringEntity(JsonOutput.toJson(map.get("body")), ContentType.APPLICATION_JSON))
        } else {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>()
            Map<String, String> values = map.get("body")
            values.forEach((k, v) -> nvps.add(new BasicNameValuePair(k, v)))
            request.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8))
        }
    } else {
        request.addHeader("Content-Type", map.getOrDefault("requestContentType", "application/json"))
    }
    http(request, map, closure)
}

static void http(HttpRequest request, LinkedHashMap<String, Object> map, Closure<Object> closure) {
    TrustStrategy acceptingTrustStrategy = (cert, authType) -> true
    SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build()
    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
            NoopHostnameVerifier.INSTANCE)

    Registry<ConnectionSocketFactory> socketFactoryRegistry =
            RegistryBuilder.<ConnectionSocketFactory> create()
                    .register("https", sslsf)
                    .register("http", new PlainConnectionSocketFactory())
                    .build()

    BasicHttpClientConnectionManager connectionManager =
            new BasicHttpClientConnectionManager(socketFactoryRegistry)
    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
            .setConnectionManager(connectionManager).build()

    map.get("headers")?.forEach((k, v) -> request.addHeader(k, v))

    try (def response = httpClient.execute(request)) {
        Response resp = new Response()
        def ent = EntityUtils.toString(response.getEntity())
        if (map.containsKey("textParser")) {
            resp.data = [:]
            resp.data.text = ent
        } else {
            def jsonSlurper = new JsonSlurper()
            resp.data = jsonSlurper.parseText(ent)
        }
        if (response.original.code == 200) {
            closure.call(resp)
        } else
            throw new ResponseException(resp)
    }



}