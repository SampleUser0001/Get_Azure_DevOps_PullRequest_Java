/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ittimfn.sample.azure.pullrequest.app;

import ittimfn.sample.azure.pullrequest.controller.GetPullRequestController;
import ittimfn.sample.azure.pullrequest.enums.ApplicationPropertiesEnum;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.http.client.ClientProtocolException;

public class App {

    private static final Path propertiesPath = Paths.get(System.getProperty("user.dir"),"src", "main", "resources","application.properties");

    private void exec(String[] args) throws ClientProtocolException, IOException {
        int i = 0;
        String repositoryId = args[i++];
        int pullrequestId = Integer.parseInt(args[i++]);

        GetPullRequestController controller = new GetPullRequestController(repositoryId, pullrequestId);
        controller.get();
        controller.get2();
        controller.print_curl();
    }

    public static void main(String[] args) throws IOException {
        ApplicationPropertiesEnum.load(propertiesPath);
        new App().exec(args);
    }
}