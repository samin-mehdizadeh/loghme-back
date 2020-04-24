package ie.domain;

import java.net.*;
import java.io.*;

public class urlReader {
    public String readURL(String url) throws Exception{
        String result = "";
        URL externalServer = new URL(url);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(externalServer.openStream(),"UTF8"));

        //BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8"));

        String inputLine;
        while ((inputLine = in.readLine()) != null)
            result += inputLine;
        in.close();

        return result;
    }
}
