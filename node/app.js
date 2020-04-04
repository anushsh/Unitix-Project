// imports
var express = require('express')
var session = require('express-session')
var routes = require('./routes/routes.js')

var app = express()
var http = require('http').createServer(app)
var port = 3000

app.use(express.bodyParser());
app.use(express.logger("default"))

app.use(session({
	resave: false,
	saveUnitialized: false,
	secret: "don't tell!"
}))

// route definitions
app.get('/', routes.get_splash)
app.get('/home', routes.get_home)
app.get('/profile', routes.get_profile)
//login routes
app.get('/login', routes.get_login);
app.get('/register', routes.get_register);
app.post('/creategroup', routes.create_group);
app.post('/checkLogin', routes.check_login);
app.get('/logout', routes.get_logout);
// event creation
app.get('/create_event', routes.get_create_event)
app.post('/create_shows', routes.create_shows)
app.post('/create_event', routes.create_event)
app.get('/clear_all_events', routes.clear_all_events)
app.get('/list_events', routes.list_events)
app.get('/list_shows', routes.list_shows)
app.get('/list_events_with_shows', routes.list_events_with_shows)
app.get('/find_event_with_shows', routes.find_event_with_shows)
app.get('/get_event', routes.get_event)
app.post('/add_event_id_to_show', routes.add_event_id_to_show);


// mobile actions
app.post('/purchase_ticket', routes.purchase_ticket);

//user(customer) management
app.post('/create_user', routes.create_user)
app.get('/find_user', routes.find_user)

// run server - we use the http server we made so that express doesn't make a new one and ignore socket.io
http.listen(port)
console.log(`Unitix server running on ${port}!`)
