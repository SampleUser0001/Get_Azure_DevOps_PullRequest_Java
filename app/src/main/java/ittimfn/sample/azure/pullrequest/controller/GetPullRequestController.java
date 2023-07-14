package ittimfn.sample.azure.pullrequest.controller;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ittimfn.sample.azure.pullrequest.enums.ApplicationPropertiesEnum;
import ittimfn.sample.azure.pullrequest.model.PullRequestModel;

public class GetPullRequestController {

    private Logger logger = LogManager.getLogger();

    /*
     * API
     * 1. organization(String)
     * 2. project(String)
     * 3. repository_id(String)
     * 4. pull_request_id(int)
     */
    private static final String API_FORMAT = "https://dev.azure.com/%s/%s/_apis/git/repositories/%s/pullrequests/%d?api-version=7.0";

    private String api;
    private String repositoryId;
    private int requestId;

    public GetPullRequestController(String repositoryId, int requestId) {
        this.repositoryId = repositoryId;
        this.requestId = requestId;
        this.api = String.format(
            API_FORMAT,
            ApplicationPropertiesEnum.ORGANIZATION.getValue(),
            ApplicationPropertiesEnum.PROJECT.getValue(),
            repositoryId,
            requestId
        );
    }

    /**
     * APIを叩いて、実行結果を取得する。
     * @return
     * @throws UnsupportedEncodingException
     */
    public PullRequestModel get() throws UnsupportedEncodingException {

        PullRequestModel model = new PullRequestModel();

        HttpHost httpHost = new HttpHost("dev.azure.com",443,"https");
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        String hostName = httpHost.getHostName();
        int port = httpHost.getPort();
        String user = ApplicationPropertiesEnum.USER.getValue();
        String token = ApplicationPropertiesEnum.TOKEN.getValue();

        // 認証にクセがある。"user:token"をBase64でエンコードした値を、Basic認証で渡す必要がある。
        String pass = user+":"+token;
        String encodedPath = Base64.getEncoder().encodeToString(pass.getBytes("utf-8"));

        logger.debug("host name : {}", hostName);
        logger.debug("port : {}", port);
        logger.debug("user : {}", user);
        logger.debug("token : {}", token);

        credentialsProvider.setCredentials(
            new AuthScope(hostName, port),
            new UsernamePasswordCredentials(user, token)
        );

        HttpClient client 
            = HttpClients.custom()
                         .setRedirectStrategy(new CustomRedirectStrategy())
                         .build();

        try {
            // GET する URL を指定
            HttpGet httpGet = new HttpGet(this.api);
            httpGet.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedPath);
            httpGet.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            // リクエストを実行してレスポンスを取得
            HttpResponse response = client.execute(httpGet);

            // レスポンスのBODYを取得
            HttpEntity httpEntity = response.getEntity();
            String json = EntityUtils.toString(httpEntity);

            model = this.convertToModel(json);

            logger.info("status code : {}", response.getStatusLine().getStatusCode());
            
        } catch (Exception e) {
            e.printStackTrace();
        }        
        return model;
    }

    private class CustomRedirectStrategy extends DefaultRedirectStrategy {
        @Override
        public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
            // デフォルトのリダイレクト判定処理を行う
            boolean isRedirect = super.isRedirected(request, response, context);
            
            // カスタムのリダイレクト判定処理を追加する
            if (!isRedirect) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 301 || statusCode == 302) {
                    return true; // リダイレクトが必要な場合はtrueを返す
                }
            }
            
            return isRedirect;
        }
    }

    private PullRequestModel convertToModel(String json) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        PullRequestModel model = mapper.readValue(json, PullRequestModel.class);
        return model;
    }

}
