var Event = require('../models/event.js')
var Show = require('../models/show.js')
var Ticket = require('../models/ticket.js')

var async = require('async')

var getSplash = function (req, res) {
    res.render('splash.ejs')
}

var getHome = function (req, res) {
    res.render('home.ejs')
}

var getCreateEvent = function (req, res) {
    // TODO: Get group name from session object
    res.render('create_event.ejs')
}

// use to test db saving
var listEvents = function (req, res) {
    Event.find((err, allEvents) => {
        if (err) {
            res.json({ 'status': err })
        } else if (allEvents.length == 0) {
            res.json({ 'status': 'no events' })
        } else {
            res.json({
                'status': 'success',
                'events': allEvents
            })
        }
    })
}

var listEventsWithShows = function (req, res) {
    Event.find((err, allEvents) => {
        if (err) {
            res.json({ 'status': err })
        } else if (allEvents.length == 0) {
            res.json({ 'status': 'no events' })
        }
        events = [];
        async.forEach(allEvents, 
            (event, done1) => {
                event = event.toJSON();
                var shows = []; // will contain actual shows
                async.forEach(event.shows, (showID, done2) => {
                    Show.findById(showID, (err, show) => {
                        if (!err && show) {
                            console.log(show.toJSON());
                            shows.push(show.toJSON());
                        } else {
                            console.log(err);
                        }
                        done2();
                    }); 
                }, () => {
                    event.shows = shows; // replace ID's with actual shows
                    events.push(event);
                    done1();
                });
            }, () => {
                res.json({
                    'status': 'success',
                    'events': events
                })
            })
        
    });
}

// use to test db saving
var listShows = function (req, res) {
    Show.find((err, allShows) => {
        if (err) {
            res.json({ 'status': err })
        } else if (allShows.length == 0) {
            res.json({ 'status': 'no events' })
        } else {
            res.json({
                'status': 'success',
                'shows': allShows
            })
        }
    })
}

// route will wipe Event DB
var clearAllEvents = function (req, res) {
    callback = function () { console.log("done") }
    Event.remove({}, callback)
    Show.remove({}, callback)
    res.json("finished")
}


var createShows = function (req, res) {
    showSchemaIDs = []

    // create and save each show asynchronously. once mongo finishes saving the show, move on to the next one. won't send back id list until all are processed.
    async.forEach(req.body.shows, function (show, done) {
        var newShow = new Show({
            name: show.name,
            capacity: show.capacity,
            tickets_sold: 0, // shows start with zero tickets sold
            price: show.price,
            location: show.location,
            description: show.description,
            start_date: show.date,
            end_date: show.date, // right now i'm assuming only one date, if add end date later we need to reconfigure how we store times (prob just array)
            start_time: show.start_time,
            end_time: show.end_time
        })

        newShow.save((err, showSaved) => {
            if (err) {
                res.json({ 'status': err })
            } else {
                console.log("show id: " + showSaved._id)
                showSchemaIDs.push(showSaved._id)
                done()
            }
        })
    }, function () {
        // send back list of ids
        res.send(showSchemaIDs, 200)
    })


}

// handle post request for event creation. not async bc only one thing occurs and we return from its callback.
var createEvent = function (req, res) {
    var newEvent = new Event({
        name: req.body.name,
        group: req.body.group,
        shows: req.body.shows,
        tags: req.body.tags
    })

    newEvent.save((err) => {
        if (err) {
            res.json({ 'status': err })
        } else {
            res.json({ 'status': 'success' })
        }
    })
}

// TODO: need to talk to Michael about how adding tags will work
var addEventTag = function (req, res) {
    //TODO: Implement for real
    res.render('create_event.ejs')
}

var getLogin = (req, res) => {
    res.render('login.ejs');
}

var createUser = function (req, res) {
    console.log(req.body.email + ", " + req.body.password)

    var newUser = new User({
        email: req.body.email,
        password: req.body.password,
        first_name: req.body.first_name,
        last_name: req.body.last_name,
        phone: req.body.phone,
        following: [],
        past_tickets: [],
        curr_tickets: []

    })
    newUser.save((err) => {
        if (err) {
            res.json({ 'status': err })
        } else {
            res.json({ 'status': 'success' })
        }
    })
}

var purchaseTicket = function(req, res) {
    var showID = req.query.showID;
    var queryEmail = req.query.email;

    // first get user by given email
    User.findOne({email: queryEmail}, (err, user) => {
        if (err) {
            res.json({"status": err})
        } else if (!user) {
            res.json({ 'status': 'user not found' })
        } else {
            // if user is found, get the show
            Show.findById(showID, (err, show) => {
                if (err || !show) {
                    res.json({"status": (err ? err : "show not found")});
                } else {
                    // if the show is found, make sure there are tickets available
                    if (!show.tickets_sold) {
                        var tickets_sold = 0;
                    }
                    if (show.tickets_sold < show.capacity) {
                        // if there are tickets available, create a new ticket
                        var newTicket = new Ticket({
                            showID: show._id,
                            redeemed: false
                        });

                        newTicket.save((err, ticket) => {
                            if (err) {
                                res.json({"status":err});
                            } else {
                                // add ticket to user's tickets
                                var tickets = user.curr_tickets;
                                tickets.push(ticket._id);
                                user.update({curr_tickets: tickets}, (err) => {
                                    if (!err) {
                                        // update tickets sold for show
                                        show.update({tickets_sold: tickets_sold + 1}, (err) => {
                                            res.json({"status":"success"});
                                        });
                                    }
                                })
                            }
                        })

                        
                    } else {
                        // if no tickets, return sold out
                        res.json({"status": "sold out"});
                    }
                }
            })
        }
    }) 
}


//find the user and then using json returned check if passwords match
var findUser = function (req, res) {
    console.log(req.query.email + ", " + req.query.password)
    var searchEmail = req.query.email;

    User.findOne({ email: searchEmail }, (err, user) => {
        if (err) {
            res.json({ 'status': err })
        } else if (!user) {
            res.json({ 'status': 'user not found' })
        } else {
            res.json({
                'status': 'user found',
                'events': user
            })
        }
    })
}

module.exports = {
    get_splash: getSplash,
    get_create_event: getCreateEvent,
    create_shows: createShows,
    create_event: createEvent,
    list_events: listEvents,
    list_shows: listShows,
    list_events_with_shows: listEventsWithShows,
    clear_all_events: clearAllEvents,
    get_home: getHome,
    get_login: getLogin,
    create_user: createUser,
    find_user: findUser,
    purchase_ticket: purchaseTicket
}
