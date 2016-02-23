package com.ckeeda.findme;

/**
 * Created by HP on 8/9/2015.
 */


        import android.util.Log;
        import java.io.BufferedInputStream;
        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.StringReader;
        import java.net.HttpURLConnection;
        import java.net.URL;

        import javax.xml.parsers.DocumentBuilder;
        import javax.xml.parsers.DocumentBuilderFactory;
        import javax.xml.parsers.ParserConfigurationException;

        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.client.ClientProtocolException;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.apache.http.util.EntityUtils;
        import org.w3c.dom.Document;
        import org.xml.sax.InputSource;
        import org.xml.sax.SAXException;

public class XmlParser_for_current_address {


    public String getXmlFromUrl(String url) {
        // TODO Auto-generated method stub
        String xml = null;
        HttpURLConnection httpUrlConnection = null;


        try {
            httpUrlConnection = (HttpURLConnection) new URL(url).openConnection();
            InputStream in = new BufferedInputStream(httpUrlConnection.getInputStream());
            xml = readStream(in);

            /*DefaultHttpClient httpDC = new DefaultHttpClient();


            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpr = httpDC.execute(httpPost);

            HttpEntity httpEnt = httpr.getEntity();

            xml = EntityUtils.toString(httpEnt);
*/

  /*      } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
  */
      //  }
    }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
            finally {
                if (null != httpUrlConnection)
                    httpUrlConnection.disconnect();
            }
            return xml;

        }



    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer data = new StringBuffer("");
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (IOException e) {
            Log.e("HTTPCONNECTION", "IOException");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data.toString();
    }


    public Document getDataFromXml(String xml) {
        // TODO Auto-generated method stub
        Document doc = null;

        DocumentBuilderFactory dmf =DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder DocB = dmf.newDocumentBuilder();


            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));

            doc= DocB.parse(is);


        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        return doc;



    }

}
