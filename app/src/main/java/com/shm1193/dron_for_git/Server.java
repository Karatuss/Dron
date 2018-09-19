package com.shm1193.dron_for_git;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class Server extends NanoHTTPD {

    public Server(int port)
    {
        super(port);
    }

    //It is called when connect website
    @Override
    public Response serve(IHTTPSession session) {
        Response response;
        String answer = "";

        String path = session.getUri();

        //origin header parsing
        String parsedOrigin;
        if (session.getHeaders().toString().indexOf("origin=") == -1) {
            parsedOrigin = "There is not Server";
        }
        else
            parsedOrigin = session.getHeaders().toString().split("origin=")[1].split(",")[0];

        //WebID
        String MYWEBID = "https://drone1.databox.me/profile/card#me";

        //request of Node.js
        if (session.getUri().equals("/autologin")) {

            //Redirect
            //String redirectUrl = "http://" + parsedOrigin;
            //answer += "<meta http-equiv=\"refresh\" content=\"3; url=" + redirectUrl + "\">";
            //answer += "My WebId : " + MYWEBID + "<br>";
            //answer += "Redirect to " + redirectUrl + "... After 3 sec<br>";

            answer += MYWEBID;
            response = new Response(answer);
        }
        // else if 추가 -> 3030포트 들어올 때 명령 처리


        // 다른 리퀘스트들 처리
        else {
            try {
                // open file
                File root = Environment.getExternalStorageDirectory();
                FileReader index = new FileReader(root.getAbsolutePath() + "/www" + path);


                //read file
                BufferedReader reader = new BufferedReader(index);
                String line = "";
                while ((line = reader.readLine()) != null) {
                    answer += line + '\n';
                }
                reader.close();

                //Info to Html
                answer += "<br>------------------------------------------------------<br>";
                answer += "Session Uri : " + session.getUri() + "<br>";
                answer += "Session Method : " + session.getMethod() + "<br>";
                answer += "Session Header : " + session.getHeaders() + "<br>";
                answer += "Session Parameters : " + session.getParms() + "<br>";
                try {
                    answer += "Session QueryParameterString : " + session.getQueryParameterString() + "<br>";
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                }


            } catch (IOException ioe) {
                Log.w("serve, File Not Found", ioe.toString());
                answer += "File Not Found Exception";
            }
            response = new Response(answer);
        }

        //Cors setting
        response.addHeader("Access-Control-Allow-Methods", "DELETE, GET, POST, PUT, OPTIONS");
        response.addHeader("Access-Control-Allow-Origin",  parsedOrigin); //node
        response.addHeader("Access-Control-Allow-Headers", "X-Requested-With, Access-Control-Allow-Credentials, Content-Type");
        response.addHeader("Access-Control-Allow-Credentials", "true");


        //Info to Log
        Log.i("Line", "-------------------------------------------------");
        Log.i("serve, ACAMethods", response.getHeader("Access-Control-Allow-Methods"));
        Log.i("serve, Status", response.getStatus().toString());
        Log.i("serve, MimeType", response.getMimeType());
        Log.i("serve, S.Uri", session.getUri());
        Log.i("serve, S.Method", session.getMethod().toString());
        Log.i("serve, S.Header", session.getHeaders().toString());
        Log.i("serve, S.Parms", session.getParms().toString());
        Log.i("serve, R.Data", response.getData().toString());
        Log.i("serve, ParsedOrigin", parsedOrigin);


        return response;
    }
}
/*
String uri, Method method,
                          Map<String, String> header,
                          Map<String, String> parameters,
                          Map<String, String> files
 */