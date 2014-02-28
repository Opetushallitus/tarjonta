var express = require('express');
var server = express(); 
var request = require('request');

var luokka="https://itest-virkailija.oph.ware.fi"
var reppu="https://test-virkailija.oph.ware.fi"

var USE_ENV=luokka;



var env={
	remote:USE_ENV
}

function proxy(server, prefix) {
  var h=function(req,res){
	url = env.remote + prefix + req.url;
	console.log("proxying request to", url);
	req.pipe(request(url)).pipe(res);
  }
	server.use(prefix, h);
}

proxy(server, "/virkailija-raamit");
proxy(server, "/cas");
proxy(server, "/lokalisointi");

server.configure(function(){
  var dir = __dirname + "/../app/";
  console.log("serving directory: ",dir);
  server.use(express.static(dir));
});

console.log("http://127.0.0.1:8000/");
server.listen(8000);


