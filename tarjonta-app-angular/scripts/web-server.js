var http=require("https");
var express = require('express');
var server = express(); 
var request = require('request');

var luokka="https://itest-virkailija.oph.ware.fi"
var reppu="https://test-virkailija.oph.ware.fi"



var USE_ENV=luokka;
var port = 8888;


var env={
	remote:USE_ENV,
user:'ophadmin',
pass:'ilonkautta!'
}
var ticketUrl = env.remote + "/service-access/accessTicket?client_id=" + env.user + "&client_secret=" + env.pass + "&service_url=" + env.remote + "/authentication-service";
var orgTicketUrl = env.remote + "/service-access/accessTicket?client_id=" + env.user + "&client_secret=" + env.pass + "&service_url=" + env.remote + "/organisaatio-service";

function proxy(server, prefix, ticket) {
  var h=function(req,res){
	url = env.remote + prefix + req.url;
	if(ticket) {
	  if(url.indexOf('?')==-1) {
		url=url+"?ticket=" + ticket;
	  } else {
                url=url+"&ticket=" + ticket;
	  }
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
	console.log("auth-ticket:", ticket);
    proxy(server, "/authentication-service", ticket);

  });

  }).on('error', function(e) {
    console.log("Got error: " + e.message);
  });

http.get(orgTicketUrl,function(res){

  res.on("data", function(ticket) {
	console.log("org-ticket:", ticket);
    proxy(server, "/organisaatio-service", ticket);

  });

  }).on('error', function(e) {
    console.log("Got error: " + e.message);
  });

console.log("http://127.0.0.1:"  + port);
server.listen(port);


