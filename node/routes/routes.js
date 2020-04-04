var Event = require('../models/event.js')
var Show = require('../models/show.js')
var User = require('../models/user.js');
var Group = require('../models/group.js');
var Ticket = require('../models/ticket.js')
var User = require('../models/user.js')

var async = require('async')

var getSplash = function (req, res) {
    res.render('splash.ejs')
}

var getHome = function (req, res) {
    res.render('home.ejs')
}

var getProfile = (req, res) => {
    console.log("SESSION");
    console.log(req.session);
    if (req.session.user) {
        res.render('profile.ejs')
    }
    res.redirect('/');
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
            start_time: show.startTime,
            end_time: show.endTime
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

    newEvent.save((err, eventSaved) => {
        if (err) {
            res.json({ 'status': err })
        } else {
            res.json({ 'status': 'success', 'eventID': eventSaved._id })
        }
    })
}

// TODO: need to talk to Michael about how adding tags will work
var addEventTag = function (req, res) {
    //TODO: Implement for real
    res.render('create_event.ejs')
}

//Login functions

var getLogin = (req, res) => {
    if (req.session.user) {
        res.redirect("/");
    }
    res.render('login.ejs', {message: ""});
}

var getRegister = (req, res) => {
    res.render('register.ejs', {message: ""});
}

//Checking login details
var checkLogin = (req, res) => {
    Group.findOne({email: req.body.email, password: req.body.password}, (err, user) => {
        if (err) {
            console.log(err);
            res.json({'status': err})
        } else {
            if (!user) {
                res.render('login.ejs', {message: "Invalid email credentials! Try again!"});
            } else {
                req.session.user = req.body.email;
                res.redirect('/home');
            }
        }
    })

}

var createGroup = (req, res) => {
    console.log("BODY");
    console.log(req.body);
    const { body } = req.body;
    var newUser = new Group({
        displayName: req.body.groupName,
        email: req.body.email,
        password: req.body.password,
        currentEvents: [],
        pastEvents: [],
        groupType: req.body.groupType,
        bio: req.body.groupBio,
        followers: 0
    })
    Group.findOne({email: req.body.email}, (err, user) => {
        if (err) {
            console.log(err);
            res.json({'status': err})
        } else {
            if (!user){
                console.log("No user exists!");
                newUser.save((err) => {
                    if (err) {
                        res.json({'status': err})
                    } else {
                        res.redirect('/home');
                    }
                })
            } else {
                console.log('Group already exists!');
                res.render('register.ejs', {message: 'Group already exists!'});
            }
        }
    })

}

var updateGroup = (req, res) => {
    console.log("REQ");
    console.log(req.body);
    console.log(req.session.user);
    Group.findOneAndUpdate({email: req.session.user}, (err, user) => {
        if (err) {
            res.json({'status': err})
        } else {
            res.json(user);
        }
    })
}

var getLogout = function(req, res) {
    if (req.session.user){
        req.session.destroy();
    }
    res.redirect('/');
};

var purchaseTicket = function (req, res) {
    var showID = req.body.showID;
    var queryEmail = req.body.email;

    // first get user by given email
    User.findOne({ email: queryEmail }, (err, user) => {
        if (err) {
            res.json({ "status": err })
        } else if (!user) {
            res.json({ 'status': 'user not found' })
        } else {
            // if user is found, get the show
            Show.findById(showID, (err, show) => {
                if (err || !show) {
                    res.json({ "status": (err ? err : "show not found") });
                } else {
                    // if the show is found, make sure there are tickets available
                    if (!show.tickets_sold) {
                        var tickets_sold = 0;
                    }
                    if (show.tickets_sold < show.capacity) {
                        // if there are tickets available, create a new ticket
                        var newTicket = new Ticket({
                            show: show._id,
                            redeemed: false
                        });

                        newTicket.save((err, ticket) => {
                            if (err) {
                                res.json({ "status": err });
                            } else {
                                // add ticket to user's tickets
                                var tickets = user.curr_tickets;
                                tickets.push(ticket._id);
                                user.update({ curr_tickets: tickets }, (err) => {
                                    if (!err) {
                                        // update tickets sold for show
                                        show.update({ tickets_sold: tickets_sold + 1 }, (err) => {
                                            res.json({ "status": "success" });
                                        });
                                    }
                                })
                            }
                        })


                    } else {
                        // if no tickets, return sold out
                        res.json({ "status": "sold out" });
                    }
                }
            })
        }
    })
}

var findEventWithShows = function (req, res) {
    var eventID = req.query.eventID;
    Event.findById(eventID, (err, event) => {
        if (err || !event) {
            res.json({ "status": "failure" });
        }
        event = event.toJSON();
        var shows = []; // to fill with actual show objects
        async.forEach(event.shows, (showID, done) => {
            Show.findById(showID, (err, show) => {
                if (!err && show) {
                    shows.push(show.toJSON());
                }
                done();
            })
        }, () => {
            event.shows = shows;
            res.json({
                "status": "success",
                "event": event._id
            });
        });
    });
}

var getEvent = function (req, res) {
    var eventID = req.query.eventID
    Event.findById(eventID, (err, event) => {
        if (err || !event) {
            res.json({ "status": "failure" })
        } else {
            event = event.toJSON()
            res.json({
                "status": "success",
                "showIDs": event.shows
            })
        }
    })
}

var addEventIdToShow = function (req, res) {
    var eventID = req.body.eventID
    var showID = req.body.showID
    console.log("showID: " + showID)
    console.log("eventID: " + eventID)
    Show.findById(showID, (err, show) => {
        if (err || !show) {
            res.json({ "status": "failure" })
        } else {
            console.log(show)
            show.update({ event: eventID }, (err) => {
                res.json({ "status": "success" });
            });
        }
    })
}

var createUser = function (req, res) {
    console.log("creating user " + req.body.email);

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
                'user': user
            })
        }
    })
}

module.exports = {
    get_splash: getSplash,
    get_create_event: getCreateEvent,
    create_shows: createShows,
    create_event: createEvent,
    get_event: getEvent,
    add_event_id_to_show: addEventIdToShow,
    list_events: listEvents,
    list_shows: listShows,
    list_events_with_shows: listEventsWithShows,
    clear_all_events: clearAllEvents,
    get_home: getHome,
    get_login: getLogin,
    create_group: createGroup,
    get_register: getRegister,
    check_login: checkLogin,
    get_logout: getLogout,
    get_profile: getProfile,
    create_user: createUser,
    find_user: findUser,
    purchase_ticket: purchaseTicket,
    find_event_with_shows: findEventWithShows,
    update_group: updateGroup
}
