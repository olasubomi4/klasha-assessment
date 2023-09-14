package com.klasha.assessment.utilities;

import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

@Component
public class SslManager implements X509TrustManager{


        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType) {}

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType) {}

        public void disableSSL() {
            try {
                TrustManager[] trustAllCerts = new TrustManager[] {new SslManager()};
                // Install the all-trusting trust manager
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HostnameVerifier allHostsValid =
                        new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        };
                HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
