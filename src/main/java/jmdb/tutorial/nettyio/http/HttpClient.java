package jmdb.tutorial.nettyio.http;


import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;

/**
 * From https://github.com/netty/netty/blob/4.1/example/src/main/java/io/netty/example/http/snoop/HttpSnoopClient.java
 */
public class HttpClient {

    /**
     * https://stackoverflow.com/questions/35119032/how-to-use-socks4-5-proxy-handlers-in-netty-client-4-1
     * http://netty.io/4.0/api/io/netty/channel/ChannelPipeline.html
     * https://stackoverflow.com/questions/20041238/http-request-using-netty
     *
     * @param args
     */
    public static void main(String[] args) throws URISyntaxException, SSLException, InterruptedException {
        System.out.println("Going to request https://www.google.com via a local socks proxy");


        GET("https://www.google.com", new InetSocketAddress("localhost", 3456));
    }

    private static void GET(String url, InetSocketAddress socksProxy) throws java.net.URISyntaxException, javax.net.ssl.SSLException, InterruptedException {
        java.net.URI uri = new java.net.URI(url);
        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            System.err.println("Only HTTP(S) is supported.");
            return;
        }

        // Configure SSL context if necessary.
        final boolean ssl = "https".equalsIgnoreCase(scheme);
        final io.netty.handler.ssl.SslContext sslCtx;
        if (ssl) {
            sslCtx = io.netty.handler.ssl.SslContextBuilder.forClient()
                    .trustManager(io.netty.handler.ssl.util.InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        // Configure the client.
        io.netty.channel.EventLoopGroup group = new io.netty.channel.nio.NioEventLoopGroup();
        try {
            io.netty.bootstrap.Bootstrap b = new io.netty.bootstrap.Bootstrap();
            b.group(group)
                    .channel(io.netty.channel.socket.nio.NioSocketChannel.class)
                    .handler(new HttpClientInitialiser(sslCtx, socksProxy));

            // Make the connection attempt.
            io.netty.channel.Channel ch = b.connect(host, port).sync().channel();

            // Prepare the HTTP request.
            io.netty.handler.codec.http.HttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(
                    io.netty.handler.codec.http.HttpVersion.HTTP_1_1, io.netty.handler.codec.http.HttpMethod.GET, uri.getRawPath());
            request.headers().set(io.netty.handler.codec.http.HttpHeaderNames.HOST, host);
            request.headers().set(io.netty.handler.codec.http.HttpHeaderNames.CONNECTION, io.netty.handler.codec.http.HttpHeaderValues.CLOSE);
            request.headers().set(io.netty.handler.codec.http.HttpHeaderNames.ACCEPT_ENCODING, io.netty.handler.codec.http.HttpHeaderValues.GZIP);

            // Set some example cookies.
            request.headers().set(
                    io.netty.handler.codec.http.HttpHeaderNames.COOKIE,
                    io.netty.handler.codec.http.cookie.ClientCookieEncoder.STRICT.encode(
                            new io.netty.handler.codec.http.cookie.DefaultCookie("my-cookie", "foo"),
                            new io.netty.handler.codec.http.cookie.DefaultCookie("another-cookie", "bar")));

            // Send the HTTP request.
            ch.writeAndFlush(request);

            // Wait for the server to close the connection.
            ch.closeFuture().sync();
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }
    }

}

