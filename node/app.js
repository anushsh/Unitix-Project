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
app.post('/updategroup', routes.update_group);
// event creation
app.get('/get_group', routes.get_group)
app.get('/get_group_with_events', routes.get_group_with_events)
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
app.post('/add_event_id_to_group', routes.add_event_id_to_group);
app.get('/get_show_with_tickets', routes.get_show_with_tickets);
app.get('/get_user_tickets', routes.get_user_tickets)


// ticket handling
app.get('/get_ticket', routes.get_ticket);
app.post('/purchase_ticket', routes.purchase_ticket);
app.post('/request_ticket', routes.request_ticket);
app.post('/redeem_ticket', routes.redeem_ticket);

//user(customer) management
app.post('/create_user', routes.create_user);
app.get('/find_user', routes.find_user);
app.post('/update_user', routes.update_user);

// run server - we use the http server we made so that express doesn't make a new one and ignore socket.io
http.listen(port)
console.log(`Unitix server running on ${port}!`)
