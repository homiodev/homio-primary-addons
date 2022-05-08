package de.onvif.soap;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.touchhome.common.exception.ServerException;

import javax.xml.soap.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.Iterator;
import java.util.StringTokenizer;

@Log4j2
public class HttpSOAPConnection {

    private MessageFactory messageFactory;

    public HttpSOAPConnection() throws SOAPException {
        try {
            this.messageFactory = MessageFactory.newInstance("Dynamic Protocol");
        } catch (NoSuchMethodError var2) {
            this.messageFactory = MessageFactory.newInstance();
        } catch (Exception ex) {
            log.error("SAAJ0001.p2p.cannot.create.msg.factory", ex);
            throw new ServerException("Unable to create message factory", ex);
        }

    }

    public SOAPMessage call(SOAPMessage message, Object endPoint) throws SOAPException, IOException {
        Class<?> urlEndpointClass = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            if (loader != null) {
                urlEndpointClass = loader.loadClass("javax.xml.messaging.URLEndpoint");
            } else {
                urlEndpointClass = Class.forName("javax.xml.messaging.URLEndpoint");
            }
        } catch (ClassNotFoundException ignore) {
        }

        if (urlEndpointClass != null && urlEndpointClass.isInstance(endPoint)) {
            String url;

            try {
                Method m = urlEndpointClass.getMethod("getURL", (Class<?>[]) null);
                url = (String) m.invoke(endPoint, (Object[]) null);
            } catch (Exception ex) {
                log.error("SAAJ0004.p2p.internal.err", ex);
                throw new ServerException("Internal error: " + ex.getMessage());
            }

            try {
                endPoint = new URL(url);
            } catch (MalformedURLException ex) {
                log.error("SAAJ0005.p2p.", ex);
                throw new ServerException("Bad URL: " + ex.getMessage());
            }
        }

        if (endPoint instanceof String) {
            try {
                endPoint = new URL((String) endPoint);
            } catch (MalformedURLException ex) {
                log.error("SAAJ0006.p2p.bad.URL", ex);
                throw new ServerException("Bad URL: " + ex.getMessage());
            }
        }

        return this.post(message, (URL) endPoint);
    }

    SOAPMessage post(SOAPMessage message, URL endPoint) throws SOAPException, IOException {
        boolean isFailure = false;
        HttpURLConnection httpConnection;
        MimeHeaders headers;
        int responseCode;
        try {
            if (endPoint.getProtocol().equals("https")) {
                throw new NotImplementedException("https not implemented");
                //this.initHttps();
            }

            URI uri = new URI(endPoint.toString());
            String userInfo = uri.getRawUserInfo();
            if (!endPoint.getProtocol().equalsIgnoreCase("http") && !endPoint.getProtocol().equalsIgnoreCase("https")) {
                log.error("SAAJ0052.p2p.protocol.mustbe.http.or.https");
                throw new IllegalArgumentException("Protocol " + endPoint.getProtocol() + " not supported in URL " + endPoint);
            }

            httpConnection = (HttpURLConnection) endPoint.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            httpConnection.setUseCaches(false);
            httpConnection.setInstanceFollowRedirects(true);
            if (message.saveRequired()) {
                message.saveChanges();
            }

            headers = message.getMimeHeaders();
            Iterator<?> it = headers.getAllHeaders();
            boolean hasAuth = false;

            while (it.hasNext()) {
                MimeHeader header = (MimeHeader) it.next();
                String[] values = headers.getHeader(header.getName());
                if (values.length == 1) {
                    httpConnection.setRequestProperty(header.getName(), header.getValue());
                } else {
                    StringBuilder concat = new StringBuilder();

                    for (int i = 0; i < values.length; ++i) {
                        if (i != 0) {
                            concat.append(',');
                        }

                        concat.append(values[i]);
                    }

                    httpConnection.setRequestProperty(header.getName(), concat.toString());
                }

                if ("Authorization".equals(header.getName())) {
                    hasAuth = true;
                }
            }

            if (!hasAuth && userInfo != null) {
                this.initAuthUserInfo(httpConnection, userInfo);
            }

            try (OutputStream out = httpConnection.getOutputStream()) {
                message.writeTo(out);
                out.flush();
            }

            httpConnection.connect();

            try {
                responseCode = httpConnection.getResponseCode();
                if (responseCode == 500) {
                    isFailure = true;
                }
            } catch (IOException ex) {
                responseCode = httpConnection.getResponseCode();
                if (responseCode != 500) {
                    throw ex;
                }

                isFailure = true;
            }
        } catch (Exception ex) {
            log.error("SAAJ0009.p2p.msg.send.failed");
            throw new ServerException("Message send failed", ex);
        }

        SOAPMessage response = null;
        InputStream httpIn = null;
        if (responseCode == 200 || isFailure) {
            try {
                headers = new MimeHeaders();
                int i = 1;

                while (true) {
                    String key = httpConnection.getHeaderFieldKey(i);
                    String value = httpConnection.getHeaderField(i);
                    if (key == null && value == null) {
                        httpIn = isFailure ? httpConnection.getErrorStream() : httpConnection.getInputStream();
                        InputStream stream = IOUtils.toBufferedInputStream(httpIn);
                        int length = httpConnection.getContentLength() == -1 ? stream.available() : httpConnection.getContentLength();
                        if (length == 0) {
                            log.warn("SAAJ0014.p2p.content.zero");
                        } else {
                            response = this.messageFactory.createMessage(headers, stream);
                        }
                        break;
                    }

                    if (key != null) {
                        StringTokenizer values = new StringTokenizer(value, ",");

                        while (values.hasMoreTokens()) {
                            headers.addHeader(key, values.nextToken().trim());
                        }
                    }

                    ++i;
                }
            } catch (SOAPException var33) {
                throw var33;
            } catch (Exception var34) {
                log.error("SAAJ0010.p2p.cannot.read.resp", var34);
                throw new ServerException("Unable to read response: " + var34.getMessage());
            } finally {
                if (httpIn != null) {
                    httpIn.close();
                }

                httpConnection.disconnect();
            }
        }

        return response;
    }

    private void initAuthUserInfo(HttpURLConnection conn, String userInfo) {
        if (userInfo != null) {
            int delimiter = userInfo.indexOf(58);
            String user;
            String password;
            if (delimiter == -1) {
                user = decode(userInfo);
                password = null;
            } else {
                user = decode(userInfo.substring(0, delimiter++));
                password = decode(userInfo.substring(delimiter));
            }

            String plain = user + ":";
            byte[] nameBytes = plain.getBytes();
            assert password != null;
            byte[] passwdBytes = password.getBytes();
            byte[] concat = new byte[nameBytes.length + passwdBytes.length];
            System.arraycopy(nameBytes, 0, concat, 0, nameBytes.length);
            System.arraycopy(passwdBytes, 0, concat, nameBytes.length, passwdBytes.length);
            String auth = "Basic " + Base64.getEncoder().encodeToString(concat);
            conn.setRequestProperty("Authorization", auth);
        }
    }

    public static String decode(String s) {
        StringBuilder sb = new StringBuilder();

        char c;
        for (int i = 0; i < s.length(); sb.append(c)) {
            c = s.charAt(i);
            if (c != '%') {
                ++i;
            } else {
                try {
                    c = unescape(s, i);
                    i += 3;
                    if ((c & 128) != 0) {
                        char c2;
                        switch (c >> 4) {
                            case 12:
                            case 13:
                                c2 = unescape(s, i);
                                i += 3;
                                c = (char) ((c & 31) << 6 | c2 & 63);
                                break;
                            case 14:
                                c2 = unescape(s, i);
                                i += 3;
                                char c3 = unescape(s, i);
                                i += 3;
                                c = (char) ((c & 15) << 12 | (c2 & 63) << 6 | c3 & 63);
                                break;
                            default:
                                throw new IllegalArgumentException();
                        }
                    }
                } catch (NumberFormatException var7) {
                    throw new IllegalArgumentException();
                }
            }
        }
        return sb.toString();
    }

    private static char unescape(String s, int i) {
        return (char) Integer.parseInt(s.substring(i + 1, i + 3), 16);
    }
}
