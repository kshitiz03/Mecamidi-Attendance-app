package com.mecamidi.www.mecamidiattendance;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDownloadUtility {

    private static final int BUFFER_SIZE = 4096;

    public static String downloadFile(String fileURL,String dates[])
            throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);

        Uri.Builder builder = new Uri.Builder().appendQueryParameter("start",dates[0]).appendQueryParameter("end",dates[1]);
        String query = builder.build().getEncodedQuery();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(httpConn.getOutputStream()));
        writer.write(query);
        writer.flush();
        writer.close();

        int responseCode = httpConn.getResponseCode();
        Log.e("export",String.valueOf(responseCode));

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 9,
                            disposition.length());
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }

            Log.e("export","Content-Type = " + contentType);
            Log.e("export","Content-Disposition = " + disposition);
            Log.e("export","Content-Length = " + contentLength);
            Log.e("export","fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String path = Environment.getExternalStorageDirectory().getPath() + "/MHPP Attendance/" + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(path);
            Log.e("export","Here");
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
            httpConn.disconnect();

            Log.e("export","File downloaded");
            return fileName;
        } else {
            Log.e("export","No file to download. Server replied HTTP code: " + responseCode);
            httpConn.disconnect();
            return "";
        }

    }
}