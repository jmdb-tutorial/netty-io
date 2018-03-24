start-socks-proxy:
	java -jar lib/esocks.jar --port=3456

url=https://www.google.com/
get-via-proxy:
	curl -v --socks5 localhost:3456 $(url)