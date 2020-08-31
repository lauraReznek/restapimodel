package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class RestApiModel {
    public static final String AUTHORIZATION_VALUE = "Bearer ";

    private static RestApiModel restApiInstance = null;
    private static String jsonParamsForAuthentication;
    private static String jsonParamsForApplicationIds;
    private static String urlForAuthentication;
    private static String urlForApplicationIds;
    private static HttpURLConnection postConnection;
    private static OutputStream outputStream;
    private static BufferedReader input;
    public static AtomicInteger apiCallNum;


    //All the necessary information should be stored in the resources folder
    private RestApiModel() {
        Properties urlProperties = new PropertiesUtil().getProperties("restApiUrlSettings.properties");

        urlForAuthentication = urlProperties.getProperty("urlForAuthentication");
        urlForApplicationIds = urlProperties.getProperty("urlForApplicationIds");

        jsonParamsForAuthentication =
                IOUtil.getFileContentFromResourceStreamBufferedReader("/json/restApiAuthentication.json");
        jsonParamsForApplicationIds =
                IOUtil.getFileContentFromResourceStreamBufferedReader("/json/restApiApplicationIds.json");

        apiCallNum = new AtomicInteger();
    }

    public static RestApiModel getInstance() {
        if (restApiInstance == null) {
            restApiInstance = new RestApiModel();
        }
        return restApiInstance;
    }

    public static String getJsonParamsForAuthentication() {
        return jsonParamsForAuthentication;
    }

    public static String getJsonParamsForApplicationIds() {
        return jsonParamsForApplicationIds;
    }

    public static String getUrlForAuthentication() {
        return urlForAuthentication;
    }

    public static String getUrlForApplicationIds() {
        return urlForApplicationIds;
    }

    public static String getPOSTRequest(String jsonParam, String urlRequest, String authorizationValue) {
        StringBuilder response = null;

        try {
            URL url = new URL(urlRequest);
            postConnection = (HttpURLConnection) url.openConnection();
            postConnection.setRequestMethod("POST");
            postConnection.setRequestProperty("Content-Type", "application/json");
            postConnection.setDoOutput(true);
            postConnection.setRequestProperty("Authorization", authorizationValue);

            outputStream = postConnection.getOutputStream();
            outputStream.write(jsonParam.getBytes());

            int responseCode = postConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                input = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
                String inputLine;
                response = new StringBuilder();

                while ((inputLine = input.readLine()) != null) {
                    response.append(inputLine);
                }

            } else {
                throw new RuntimeException("Failed : HTTP error code : " + postConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        apiCallNum.incrementAndGet();
        return response.toString();
    }


    //There are many api calls in an iteration, this is why there is a method
    // to close all the resources at the end of the loop.
    public static void closeApiConnectionAndResources() {
        postConnection.disconnect();

        try {
            outputStream.flush();
            outputStream.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
