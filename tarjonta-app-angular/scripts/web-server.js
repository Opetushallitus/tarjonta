var express = require('express');
var server = express(); 
server.configure(function(){
  var dir = __dirname + "/../app/";
  console.log("serving directory: ",dir);
  server.use(express.static(dir));
});

console.log("http://127.0.0.1:8000/");
server.listen(8000);


