package scn.com.sipclient.net;


import android.util.Base64;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by star on 6/17/2016.
 */
public class APIManager {

    public static final String API_URL = "http://192.168.1.113:81/api/";

//    private static OkHttpClient okHttp = new OkHttpClient().newBuilder().sslSocketFactory(getSSLConfig(context).getSocketFactory(),);
//    okHttp.setSslSocketFactory(getSSLConfig(contex).getSocketFactory());

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static final Retrofit REST_ADAPTER = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create());


    private static final SipAPI sipAPI = REST_ADAPTER.create(SipAPI.class);

    public static SipAPI getServiceAPI() {
        return sipAPI;
    }

    public static <S> S createService(Class<S> serviceClass) {
        return REST_ADAPTER.create(serviceClass);
    }

//    httpClient.addInterceptor(new Interceptor() {
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Request original = chain.request();
//            HttpUrl originalHttpUrl = original.url();
//
//            HttpUrl url = originalHttpUrl.newBuilder()
//                    .addQueryParameter("apikey", "your-actual-api-key")
//                    .build();
//
//            // Request customization: add request headers
//            Request.Builder requestBuilder = original.newBuilder()
//                    .url(url);
//
//            Request request = requestBuilder.build();
//            return chain.proceed(request);
//        }
//    });

    //Basic Authentication
    public static <S> S createService(Class<S> serviceClass, String username, String password) {
        if (username != null && password != null) {
            String credentials = username + ":" + password;
            final String basic =
                    "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", basic)
                            .header("Accept", "application/json")
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, final String authToken) {
        if (authToken != null) {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", authToken)
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, final AccessToken token) {
        if (token != null) {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization",
                                    token.getTokenType() + " " + token.getAccessToken())
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }

//    private static SSLContext getSSLConfig(Context context) throws CertificateException, IOException,
//            KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
//
//        // Loading CAs from an InputStream
//        CertificateFactory cf = null;
//        cf = CertificateFactory.getInstance("X.509");
//
//        Certificate ca = null;
//        // I'm using Java7. If you used Java6 close it manually with finally.
////        try (InputStream cert = context.getResources().openRawResource(R.raw.server)) {
////            ca = cf.generateCertificate(cert);
////        }
//
//        try {
//            InputStream cert = context.getResources().openRawResource(R.raw.server);
//            ca = cf.generateCertificate(cert);
//        }catch (Exception e){
//
//        }
//
//
//
//        // Creating a KeyStore containing our trusted CAs
//        String keyStoreType = KeyStore.getDefaultType();
//        KeyStore keyStore   = KeyStore.getInstance(keyStoreType);
//        keyStore.load(null, null);
//        keyStore.setCertificateEntry("ca", ca);
//
//        // Creating a TrustManager that trusts the CAs in our KeyStore.
//        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//        tmf.init(keyStore);
//
//        // Creating an SSLSocketFactory that uses our TrustManager
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(null, tmf.getTrustManagers(), null);
//
//        return sslContext;
//    }


}


