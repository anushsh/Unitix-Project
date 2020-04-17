var User = require('../models/user.js');
var Group = require('../models/group.js');
var Event = require('../models/event.js')
var Show = require('../models/show.js')
var Ticket = require('../models/ticket.js')

var async = require('async')

var getGroup = function (req, res) {
    //req.session.user has the email of the group, so we query for the full object
    Group.findOne({email:req.session.user}, (err, group) => {
        !err && group ? res.send({'status': 'success', 'group': group}) : res.send({'status':err})
    })
}

var getGroupByID = function (req, res) {
    Group.findById(req.query.groupID, (err, group) => {
        !err && group ? res.json({"group":group}) : res.json({"err":err})
    })
}


// TODO: make into helper
var getGroupWithEvents = function (req, res) {
    Group.findOne({email:req.session.user}, (err, group) => {
        if (!err && group) {
            group = group.toJSON();
            var currentEvents = [];
            async.forEach(group.currentEvents, (eventID, done) => {
                getEventWithShows(eventID, (err, event) => {
                    if (!err && event) {
                        currentEvents.push(event);
                    } else console.log("ERROR GETTING SINGLE EVENT. " + err)
                    done();
                })
            }, () => {
                group.currentEvents = currentEvents;
                res.json({'status':'success',"group":group});
            });
        } else {
            res.json({'status':err});
        }
    });
}

// helper to find event pre-populated with shows
var getEventWithShows= function(eventID, callback) {
    Event.findById(eventID, (err, event) => {
        if (!err && event) {
            event = event.toJSON();
            var shows = [];
            async.forEach(event.shows, (showID, done) => {
                Show.findById(showID, (err, show) => {
                    if (!err && show) {
                        show = show.toJSON();
                        shows.push(show);
                        done();
                    } else {
                        done();
                    }
                })
            }, () => {
                event.shows = shows;
                callback(null, event);
            });
        } else {
            callback("no such event", null);
        }
    })
}



var getUserTickets = function(req, res) {
    var email = req.query.email;
    User.findOne({"email":email}, (err, user) => {
        user = user.toJSON();
        if (!err && user) {
            var ticketIDs = user.curr_tickets ? user.curr_tickets : [];
            getAllTickets(ticketIDs, (tickets) => {
                res.json({"status":"success","tickets":tickets})
            });
        } else {
            res.json({"status":"error"})
        }
    })
}



// TODO: is this gonna change to be different from one above?
var getUserShowInfo = function(req, res) {
    var email = req.query.email;
    User.findOne({"email":email}, (err, user) => {
        user = user.toJSON();
        if (!err && user) {
            var tickets = [];
            async.forEach(user.curr_tickets, (ticketID, done) => {
                Ticket.findById(ticketID, (err, ticket) => {
                    ticket = ticket.toJSON();
                    if (!err && ticket) {
                        tickets.push(ticket);
                    }
                    done();
                })
            }, () => {
                res.json({"status":"success","tickets":tickets})
            });
        } else {
            res.json({"status":"error"})
        }
    })
}


var updateGroup = (req, res) => {
    // console.log("REQ");
    // console.log(req.body);
    // console.log(req.session.user);
    Group.findOneAndUpdate({email: req.session.user}, {password: req.body.password,
    displayName: req.body.groupName, groupType: req.body.groupType, bio: req.body.bio}, {new: true}, (err, user) => {
        if (err) {
            res.json({'status': err})
        } else {
            res.redirect('/profile');
        }
    })
}


var createGroup = (req, res) => {
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
        curr_tickets: [],
        saved_tickets: []

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

var updateUser = function (req, res) {

    User.findOneAndUpdate({email: req.body.email}, {$set: {password: req.body.password,
         first_name: req.body.first_name, last_name: req.body.last_name, phone: req.body.phone}}, (err, user) => {
        if (err) {
            res.json({'status': err})
        } else {
            res.json({ 'status': 'success' })
        }
    })
}

module.exports = {
    get_group: getGroup,
    get_group_by_id: getGroupByID,
    get_group_with_events: getGroupWithEvents,
    get_user_tickets: getUserTickets,
    get_user_show_info: getUserShowInfo,
    update_group: updateGroup,
    create_group: createGroup,
    create_user: createUser,
    find_user: findUser,
    update_user: updateUser,
}