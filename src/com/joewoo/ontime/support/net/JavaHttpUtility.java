package com.joewoo.ontime.support.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.joewoo.ontime.support.image.ImageDownloadHelper;
import com.joewoo.ontime.support.image.ImageUploadHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import static com.joewoo.ontime.support.info.Defines.TAG;

/**
 * Created by JoeWoo on 13-11-23.
 */
public class JavaHttpUtility {

    public static final int CONNECT_TIMEOUT = 5 * 1000;
    public static final int READ_TIMEOUT = 5 * 1000;
    
    public static final int UPLOAD_CONNECT_TIMEOUT = 10 * 1000;
    public static final int UPLOAD_READ_TIMEOUT = 5 * 1000;

    public static final int DOWNLOAD_CONNECT_TIMEOUT = 5 * 1000;
    public static final int DOWNLOAD_READ_TIMEOUT = 5 * 1000;

    public byte[] doDownloadImage(String urlStr, ImageDownloadHelper.ProgressListener downloadListener) throws Exception{

        InputStream in = null;
        ByteArrayOutputStream baos = null;
        byte[] imgBytes = null;
        HttpURLConnection conn;
        try {

            URL url = new URL(urlStr);

            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setDoOutput(false);
            conn.setConnectTimeout(DOWNLOAD_CONNECT_TIMEOUT);
            conn.setReadTimeout(DOWNLOAD_READ_TIMEOUT);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");

            conn.connect();

            int bytetotal = conn.getContentLength();
            int bytesum = 0;
            int byteread = 0;

            in = new BufferedInputStream(conn.getInputStream());
            baos = new ByteArrayOutputStream();

            final Thread thread = Thread.currentThread();
            byte[] buffer = new byte[1024];
            
            while ((byteread = in.read(buffer)) != -1) {
                
                if (thread.isInterrupted()) {
                    throw new Exception();
                }

                bytesum += byteread;
                baos.write(buffer, 0, byteread);
                if (downloadListener != null && bytetotal > 0) {
                    downloadListener.downloadProgress(bytesum, bytetotal);
                }
            }

            imgBytes = baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(in != null)
                in.close();
            if(baos != null)
                baos.close();
        }

        return imgBytes;
    }

    public String doUploadFile(String urlStr, Map<String, String> param, String path, String imageParamName, final ImageUploadHelper.ProgressListener listener) throws Exception {
        String BOUNDARYSTR = getBoundry();

        File targetFile = new File(path);

        byte[] barry = null;
        int contentLength = 0;
        String sendStr = "";
        try {
            barry = ("--" + BOUNDARYSTR + "--\r\n").getBytes("UTF-8");

            sendStr = getBoundaryMessage(BOUNDARYSTR, param, imageParamName, new File(path).getName(), "image/png");
            contentLength = sendStr.getBytes("UTF-8").length + (int) targetFile.length() + 2 * barry.length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        int totalSent = 0;
        String lenstr = Integer.toString(contentLength);

        HttpURLConnection conn = null;
        BufferedOutputStream out = null;
        FileInputStream fis = null;
        try {
            URL url = new URL(urlStr);

            conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(UPLOAD_CONNECT_TIMEOUT);
            conn.setReadTimeout(UPLOAD_READ_TIMEOUT);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-type", "multipart/form-data;boundary=" + BOUNDARYSTR);
            conn.setRequestProperty("Content-Length", lenstr);
            conn.setFixedLengthStreamingMode(contentLength);

            conn.connect();

            out = new BufferedOutputStream(conn.getOutputStream());
            out.write(sendStr.getBytes("UTF-8"));
            totalSent += sendStr.getBytes("UTF-8").length;


            fis = new FileInputStream(targetFile);

            int bytesRead;
            int bytesAvailable;
            int bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024;

            bytesAvailable = fis.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fis.read(buffer, 0, bufferSize);
            int transferred = 0;
            final Thread thread = Thread.currentThread();
            while (bytesRead > 0) {
                if (thread.isInterrupted()) {
                    targetFile.delete();
                    throw new Exception();
                }
                out.write(buffer, 0, bufferSize);
                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fis.read(buffer, 0, bufferSize);
                transferred += bytesRead;
                if (transferred % 50 == 0)
                    out.flush();
                if (listener != null)
                    listener.uploadProgress(transferred, contentLength);

            }


            out.write(barry);
            totalSent += barry.length;
            out.write(barry);
            totalSent += barry.length;
            out.flush();
            out.close();
            if (listener != null) {
                listener.waitResponse();
            }


        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(fis != null)
                fis.close();
            if(out != null)
                out.close();
        }

        return handleResponse(conn);
    }

    public String doGet(String urlStr, Map<String, String> param) throws Exception {
        HttpURLConnection conn = null;
        try {

            StringBuilder sb = new StringBuilder(urlStr);
            sb.append("?").append(encodeParamFromMap(param));

            Log.e(TAG, "URL to connect: " + sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setDoOutput(false);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");

            conn.connect();

            return handleResponse(conn);
        } catch (Exception e) {
            e.printStackTrace();
            if(conn != null)
                conn.disconnect();
            throw e;
        }
    }

    public String doPost(String urlStr, Map<String, String> param) throws Exception {
        try {

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");

            conn.connect();

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.write(encodeParamFromMap(param).getBytes());
            out.flush();
            out.close();

            return handleResponse(conn);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private String handleResponse(HttpURLConnection conn) throws Exception {
        InputStream is = null;
        BufferedReader br = null;

        try {
            is = conn.getInputStream();

            String content_encode = conn.getContentEncoding();

            if (content_encode != null && !content_encode.equals("") && content_encode.equals("gzip")) {
                is = new GZIPInputStream(is);
            }

            br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            Log.e(TAG, "Http result: " + sb.toString());
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (is != null)
                is.close();
            if (br != null)
                br.close();
        }


    }

    private String encodeParamFromMap(Map<String, String> param) {

        if(param == null)
            return "";

        StringBuilder sb = new StringBuilder();

        Set<String> keys = param.keySet();
        boolean first = true;

        for (String key : keys) {
            String value = param.get(key);
            if (first)
                first = false;
            else
                sb.append("&");

            try {
                sb.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8"));
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        return sb.toString();
    }

    private static String getBoundry() {
        StringBuilder _sb = new StringBuilder();
        for (int t = 1; t < 12; t++) {
            long time = System.currentTimeMillis() + t;
            if (time % 3 == 0) {
                _sb.append((char) time % 9);
            } else if (time % 3 == 1) {
                _sb.append((char) (65 + time % 26));
            } else {
                _sb.append((char) (97 + time % 26));
            }
        }
        return _sb.toString();
    }

    private String getBoundaryMessage(String boundary, Map params, String fileField, String fileName, String fileType) {
        StringBuilder res = new StringBuilder("--").append(boundary).append("\r\n");

        Iterator keys = params.keySet().iterator();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = (String) params.get(key);
            res.append("Content-Disposition: form-data; name=\"")
                    .append(key).append("\"\r\n").append("\r\n")
                    .append(value).append("\r\n").append("--")
                    .append(boundary).append("\r\n");
        }
        res.append("Content-Disposition: form-data; name=\"").append(fileField)
                .append("\"; filename=\"").append(fileName)
                .append("\"\r\n").append("Content-Type: ")
                .append(fileType).append("\r\n\r\n");

        return res.toString();
    }

}
