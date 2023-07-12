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
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
     */
    public PullRequestModel get() {

        PullRequestModel model = new PullRequestModel();

        HttpHost httpHost = new HttpHost("dev.azure.com",443,"https");
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        String hostName = httpHost.getHostName();
        int port = httpHost.getPort();
        String user = ApplicationPropertiesEnum.USER.getValue();
        String token = ApplicationPropertiesEnum.TOKEN.getValue();

        logger.debug("host name : {}", hostName);
        logger.debug("port : {}", port);
        logger.debug("user : {}", user);
        logger.debug("token : {}", token);

        credentialsProvider.setCredentials(
            new AuthScope(hostName, port),
            new UsernamePasswordCredentials(user, token)
        );

        // CloseableHttpClient のインスタンス作成 作成時に認証情報を追加する。
        CloseableHttpClient closeableHttpClient
            = HttpClients.custom()
                         .setDefaultCredentialsProvider(credentialsProvider)
                         .setRedirectStrategy(new CustomRedirectStrategy())
                         .build();

        try {
            // GET する URL を指定
            HttpGet httpGet = new HttpGet(this.api);
            // リクエストを実行してレスポンスを取得
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);

            // レスポンスのBODYを取得
            // HttpEntity httpEntity = closeableHttpResponse.getEntity();
            // System.out.println(EntityUtils.toString(httpEntity));

            logger.info("status code : {}", closeableHttpResponse.getStatusLine().getStatusCode());
            
        } catch (Exception e) {
            e.printStackTrace();
        }        
        return model;
    }

    // private Authenticator getAuthenticator() {
    //     return new Authenticator() {
    //         @Override
    //         protected PasswordAuthentication getPasswordAuthentication() {
    //             return new PasswordAuthentication("git", ApplicationPropertiesEnum.TOKEN.getValue().toCharArray());
    //         }
    //     };
    // }

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

    public void get2() throws ClientProtocolException, IOException{
        // Azure DevOpsのAPIエンドポイント
        String apiUrl = "https://dev.azure.com/ittimfn/SampleProject/_apis/git/repositories/1ee52a0c-3836-454d-bbad-11e3d499afaa/pullRequests/1?api-version=6.1";
        // String apiUrl = this.api;

        // Azure DevOpsのPAT (Personal Access Token)
        String personalAccessToken = ApplicationPropertiesEnum.TOKEN.getValue();
        
        // HttpClientのインスタンスを作成
        HttpClient httpClient = HttpClients.createDefault();

        // GETリクエストの作成
        HttpGet httpGet = new HttpGet(apiUrl);

        // HTTPヘッダーに認証トークンを追加
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + personalAccessToken);
        httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");

        // リクエストの実行
        HttpResponse response = httpClient.execute(httpGet);

        // レスポンスのステータスコードを取得
        int statusCode = response.getStatusLine().getStatusCode();

        // レスポンスのボディを取得
        String responseBody = EntityUtils.toString(response.getEntity());

        // レスポンスの処理
        if (statusCode == 200) {
            // リクエスト成功
            System.out.println("Response Body: " + responseBody);
        } else {
            // リクエスト失敗
            logger.warn("Request failed. Status code: {}", statusCode);
            // System.out.println("Response Body: " + responseBody);
        }        
    }

    public void print_curl() {
        logger.debug(String.format("curl -s -u ittimfn:%s %s", ApplicationPropertiesEnum.TOKEN.getValue(), this.api));
    }
}
