var http = require('http'),
    httpProxy = require('http-proxy');
//
// Create your proxy server and set the target in the options.
//
const url = 'https://mail.tutanota.com'
const proxy = httpProxy.createProxyServer({
    target: url,
    changeOrigin: true,
    ws: true,
    changeOrigin: true,
    host: url,
    headers: {origin: null}
})

proxy.on('proxyReq', (request) => {
    console.log("req", request)
})

proxy.on('proxyRes', (response) => {
    response.headers['access-control-allow-origin'] = '*'
//    console.log('res', response.headers)
})


proxy.listen(9000)