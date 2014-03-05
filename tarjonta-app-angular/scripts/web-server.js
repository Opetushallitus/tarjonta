var http=require("https");
var express = require('express');
var server = express(); 
var request = require('request');

var luokka="https://itest-virkailija.oph.ware.fi"
var reppu="https://test-virkailija.oph.ware.fi"



var USE_ENV=luokka;


var env={
	remote:USE_ENV,
user:'xxx',
pass:'yyy'
}
var ticketUrl = env.remote + "/service-access/accessTicket?client_id=" + env.user + "&client_secret=" + env.pass + "&service_url=" + env.remote + "/authentication-service";

function proxy(server, prefix, ticket) {
  var h=function(req,res){
	url = env.remote + prefix + req.url;
	if(ticket) {
		url=url+"?ticket=" + ticket;
	}
	console.log("proxying request to", url);
	req.pipe(request(url)).pipe(res);
  }
	server.use(prefix, h);
}

proxy(server, "/virkailija-raamit");
proxy(server, "/cas");
proxy(server, "/lokalisointi");
proxy(server, "/koodisto-service");
proxy(server, "/tarjonta-service");

server.configure(function(){
  var dir = __dirname + "/../app/";
  console.log("serving directory: ",dir);
  server.use(express.static(dir));
});



http.get(ticketUrl,function(res){

  res.on("data", function(ticket) {
    proxy(server, "/authentication-service", ticket);
  });

  }).on('error', function(e) {
    console.log("Got error: " + e.message);
  });


console.log("http://127.0.0.1:8000/");
server.listen(8000);


