package com.predictor.qrmap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: predictor
 * Date: 11.02.13
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
public class HttpUploader{
    private static final Logger log =  LoggerFactory.getLogger(HttpUploader.class);

    public void upload(String url, File file) throws Exception
    {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        HttpPost httppost = new HttpPost(url);
        MultipartEntity mpEntity = new MultipartEntity();
        ContentBody cbFile = new FileBody(file, "image/jpeg");
        mpEntity.addPart("mapfile", cbFile);
        httppost.setEntity(mpEntity);
        log.info("Executing request " + httppost.getRequestLine());
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity resEntity = response.getEntity();

        log.info(response.getStatusLine().toString());
        if (resEntity != null) {
            log.info(EntityUtils.toString(resEntity));
            EntityUtils.consume(resEntity);
        }
        httpclient.getConnectionManager().shutdown();
    }



}