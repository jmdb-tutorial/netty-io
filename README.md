# netty-io

Herein are some examples of how to configure and use the netty-io networking library

In particular to communicate via the SOCKS protocol such that an application can communicate with a TOR network

## SOCKS PROXY

In this codebase you will find a class `HttpClient`

To see it working, you first need to start a socks proxy , which happily we have a simple version of:

```
make start-socks-proxy
```

This will run a socks proxy on `localhost:3456`

You can verify that its running by doing this in another terminal
```
make get-via-proxy url=https://www.google.com
```



## REFERENCES

https://stackoverflow.com/questions/35119032/how-to-use-socks4-5-proxy-handlers-in-netty-client-4-1


https://www.torproject.org/docs/faq.html.en#TBBSocksPort

http://www.privoxy.org/
http://www.privoxy.org/faq/misc.html#TOR
https://www.torproject.org/docs/faq.html.en#CantSetProxy

https://github.com/dgoulet/torsocks/

http://www.dest-unreach.org/socat/doc/README

https://tor.stackexchange.com/questions/8810/using-socat-through-tor-socks-proxy-gives-no-response

https://github.com/fengyouchao/sockslib

https://github.com/fengyouchao/esocks

https://blog.emacsos.com/use-socks5-proxy-in-curl.html

https://stackoverflow.com/questions/24568788/doing-https-requests-through-a-socks5-proxy-tor-with-curl

A simple server can be run by doing this:

https://github.com/fengyouchao/esocks

download esocks.jar then

```
java -jar esocks.jar --port=3456
```

IN another shell

```
curl -x socks5h://localhost:3456 http://www.google.com/
```

or

```
curl --socks5 localhost:3456 http://www.google.com/
```

In older browsers
