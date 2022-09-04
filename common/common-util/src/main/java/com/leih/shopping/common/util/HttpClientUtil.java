package com.leih.shopping.common.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * HttpClient
 *
 */
public class HttpClientUtil {

    public static String doGet(String url)   {

        // create Httpclient object
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // create http get request
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            // execute the http request
            response = httpclient.execute(httpGet);
            // check if response status is 200
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(entity);
                httpclient.close();
                return result;
            }
            httpclient.close();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

        return  null;
    }


    public static void download(String url,String fileName)   {

        // create Httpclient object
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // create http get request
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            // execute the request
            response = httpclient.execute(httpGet);
            // check if response status is 200
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();

               // String result = EntityUtils.toString(entity, "UTF-8");
                byte[] bytes = EntityUtils.toByteArray(entity);
                File file =new File(fileName);
               //  InputStream in = entity.getContent();
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(bytes);

                EntityUtils.consume(entity);

                httpclient.close();
                fout.flush();
                fout.close();
                return  ;
            }
            httpclient.close();
        }catch (IOException e){
            e.printStackTrace();
            return  ;
        }

        return   ;
    }
}
