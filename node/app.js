// imports
var express = require('express')
var session = require('express-session')


// routes
var routes = require('./routes/routes.js')
var ticket_routes = require('./routes/ticket_routes.js')
var notification_routes = require('./routes/notification_routes.js')
var event_show_routes = require('./routes/event_show_routes.js')
var user_group_routes = require('./routes/user_group_routes.js')


var app = express()
var http = require('http').createServer(app)
var port = 3000

app.use(express.bodyParser());
app.use(express.logger("default"))
app.use('/', express.static(__dirname +  "/views/static",{maxAge:1}));


app.use(session({
	resave: false,
	saveUnitialized: false,
	secret: "don't tell!"
}))

// route definitions

// generic routes
app.get('/', routes.get_splash)
app.get('/home', routes.get_home)
app.get('/profile', routes.get_profile)
app.get('/login', routes.get_login);
app.get('/register', routes.get_register);
app.post('/checkLogin', routes.check_login);
app.get('/logout', routes.get_logout);
app.get('/create_event', routes.get_create_event)
app.get('/get_message', routes.get_message)
app.get('/edit_event/:event', routes.get_edit_event)
app.get('/view_stats/:event', routes.get_view_stats)
app.get('/followers', routes.get_followers)
app.get('/get_follower_names', routes.get_follower_names)

// user and group routes
app.get('/get_group', user_group_routes.get_group)
app.get('/get_group_by_id', user_group_routes.get_group_by_id)
app.get('/get_group_with_events', user_group_routes.get_group_with_events)
app.get('/get_user_tickets', user_group_routes.get_user_tickets)
app.get('/get_user_show_info', user_group_routes.get_user_show_info);
app.post('/updategroup', user_group_routes.update_group);
app.post('/create_user', user_group_routes.create_user);
app.get('/find_user', user_group_routes.find_user);
app.post('/update_user', user_group_routes.update_user);
app.post('/creategroup', user_group_routes.create_group);
app.get('/get_all_groups',user_group_routes.get_all_groups);
app.get('/get_followed_groups', user_group_routes.get_followed_groups)
app.post('/follow_group', user_group_routes.follow_group)

// event and show routes
app.post('/create_shows', event_show_routes.create_shows)
app.post('/create_event', event_show_routes.create_event)
app.get('/clear_all_events', event_show_routes.clear_all_events)
app.get('/list_events', event_show_routes.list_events)
app.get('/list_shows', event_show_routes.list_shows)
app.get('/list_events_with_shows', event_show_routes.list_events_with_shows)
app.get('/find_event_with_shows', event_show_routes.find_event_with_shows)
app.get('/get_event', event_show_routes.get_event)
app.get('/get_show', event_show_routes.get_show)
app.get('/get_event_by_name', event_show_routes.get_event_by_name)
app.post('/add_event_id_to_show', event_show_routes.add_event_id_to_show);
app.post('/add_event_id_to_group', event_show_routes.add_event_id_to_group);
app.get('/get_show_with_tickets', event_show_routes.get_show_with_tickets);
app.post('/update_event_overview', event_show_routes.update_event_overview)
app.get('/delete_event', event_show_routes.delete_event)
app.get('/get_search_result_events', event_show_routes.get_search_result_events);
app.get('/get_search_result_events_by_tag', event_show_routes.get_search_result_by_tag);
app.get('/get_change', event_show_routes.get_change)
app.get('/get_shows_for_event', event_show_routes.get_shows_for_event)

//notifications
app.post('/create_notification', notification_routes.create_notification);
app.post('/notify_event', notification_routes.notify_event);
app.post('/notify_show', notification_routes.notify_show);
app.get('/get_user_notifications', notification_routes.get_user_notifications)
app.get('/get_user_read_notifications', notification_routes.get_user_read_notifications)
app.post('/read_all_notifications', notification_routes.read_all_notifications)
app.post('/notify_followers', notification_routes.notify_followers);


// ticket handling
app.get('/get_ticket', ticket_routes.get_ticket);
app.post('/purchase_ticket', ticket_routes.purchase_ticket);
app.post('/request_ticket', ticket_routes.request_ticket);
app.post('/redeem_ticket', ticket_routes.redeem_ticket);
app.get('/get_ticket_stats', ticket_routes.get_ticket_stats);

//payments
app.post('/api/stripe', routes.payment);


// run server - we use the http server we made so that express doesn't make a new one and ignore socket.io
http.listen(port)
console.log(`Unitix server running on ${port}!`)
