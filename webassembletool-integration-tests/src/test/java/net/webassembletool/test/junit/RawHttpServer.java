package net.webassembletool.test.junit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class reprensts a simple HTTP server that simply responds to any request with a raw in memory response.
 * 
 * @author omben
 */
public class RawHttpServer extends Thread {
    byte[] response;
    ServerSocket server;

    /**
     * Create a raw http server
     * 
     * @param response
     *            desired response that will be sent to any given request
     * @param port
     *            the port to listen to
     * @throws IOException
     *             of socket server cannot be created
     */
    public RawHttpServer(byte[] response, int port) throws IOException {
        server = new ServerSocket(port);
        this.response = response;
    }

    @Override
    public void run() {
        try {
            Socket s = server.accept();
            byte[] buffer = new byte[4000];
            InputStream in = s.getInputStream();
            do
                // Ignore all the input
                in.read(buffer);
            while (in.available() > 0);
            // send response
            OutputStream out = s.getOutputStream();
            out.write(response);
            out.flush();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Bloc catch auto-g�n�r�
                e.printStackTrace();
            }
            s.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] buildHTTPBody(int httpStatus, Map<String, String> headers, String rawBody) throws UnsupportedEncodingException {
        StringBuffer b = new StringBuffer("HTTP/1.1 ");
        b.append(httpStatus);
        b.append(" OK or not so ...\r\n");
        b.append("Server: RawHttpServer\r\n");
        for (Entry<String, String> entry : headers.entrySet()) {
            b.append(entry.getKey());
            b.append(": ");
            b.append(entry.getValue());
            b.append("\r\n");
        }
        b.append("\r\n");
        b.append(rawBody);
        return b.toString().getBytes(Charset.forName("UTF-8").toString());
    }

    public static void main(String[] args) throws Exception {
        HashMap<String, String> h = new HashMap<String, String>();
        h.put("Transfer-Encoding", "chunked");
        new RawHttpServer(RawHttpServer.buildHTTPBody(200, h, "9;\r\nBonjour !\r\n2;\r\nMo\r\n0;\r\n\r\n4;\r\nOmar"), 8888).start();
    }
}
