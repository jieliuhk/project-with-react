package helper;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

public class WebApiHelper {
    private String host;
    private String path;
    private String apiKey;

    public WebApiHelper(String host, String path, String apiKey) {
        this.host = host;
        this.path = path;
        this.apiKey = apiKey;
    }

    /**
     * Fetch attraction data from google map api, return all attractions near specified location and city
     *
     * @param city   city of hotel
     * @param lat    hotel latitude
     * @param lon    hotel longitude
     * @param radius search radius
     */
    public String getGoogleRespond(String city, double lat, double lon, double radius) {
        String request = createRequest(city, lat, lon, radius);
        StringBuilder sb = new StringBuilder();

        PrintWriter out = null;
        BufferedReader in = null;
        SSLSocket socket = null;
        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            // HTTPS uses port 443
            socket = (SSLSocket) factory.createSocket(host, 443);

            // output stream for the secure socket
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.println(request); // send a request to the server
            out.flush();

            // input stream for the secure socket.
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // use input stream to read server's response
            String line;
            boolean started = false;

            while ((line = in.readLine()) != null) {
                if (line.equals("{") && !started) {
                    started = true;
                }
                if (started) {
                    sb.append(line).append(System.lineSeparator());
                }
            }

            return sb.toString();
        } catch (IOException e) {
            System.out.println(
                    "An IOException occured while writing to the socket stream or reading from the stream: " + e);
        } finally {
            try {
                // close the streams and the socket
                out.close();
                in.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("An exception occured while trying to close the streams or the socket: " + e);
            }
        }
        return sb.toString();
    }

    private String createRequest(String city, double lan, double lon, double radius) {
        return "GET " + createUrl(city, lan, lon, radius) + " HTTP/1.1" + System.lineSeparator() +
                "Host: " + host + System.lineSeparator() +
                "Connection: close" + System.lineSeparator();
    }

    private String createUrl(String city, double lan, double lon, double radius) {
        return path + "?query=tourist%20attractions+in+" + city.replace(" ", "%20") + "&location="
                + lan + "," + lon + "&radius=" + radius + "&key=" + apiKey;
    }
}
