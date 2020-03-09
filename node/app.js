// imports
var express = require('express');
var session = require('express-session');
var routes = require('./routes/routes.js');

var app = express();
var http = require('http').createServer(app);
var port = 3000;

app.use(express.bodyParser());
app.use(express.logger("default"));

app.use(session({
	resave: false,
	saveUnitialized: false,
	secret: "don't tell!"
}));

// route definitions
app.get('/', routes.get_splash);

// event creation
app.get('/create_event', routes.get_create_event);

// run server - we use the http server we made so that express doesn't make a new one and ignore socket.io
http.listen(port);
console.log(`Unitix server running on ${port}!`);
