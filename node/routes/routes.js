var Event = require('../models/event.js')
var Show = require('../models/show.js')
var User = require('../models/user.js');
var Group = require('../models/group.js');

var getSplash = function (req, res) {
    res.render('splash.ejs')
}

var getHome = function (req, res) {
    res.render('home.ejs')
}

var getProfile = (req, res) => {
    res.render('profile.ejs')
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

// route will wipe Event DB
var clearAllEvents = function (req, res) {
    callback = function () { console.log("done") }
    Event.remove({}, callback)
    res.json("finished")
}

// handle post request for event creation
var createEvent = function (req, res) {
    console.log(req.body.name + ", " + req.body.group)
    var newEvent = new Event({
        event_id: "EVENT_ID_FORMATTING_TO_DO", // TODO: format this for unique id
        name: req.body.name,
        group: req.body.group,
        shows: [],
        event_type: req.body.type
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

//Login functions

var getLogin = (req, res) => {
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
        displayName: req.body.firstName,
        email: req.body.email,
        password: req.body.password,
        currentEvents: [],
        pastEvents: [],
        groupType: "",
        bio: "",
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

var getLogout = function(req, res) {
    req.session.destroy();
    res.redirect('/');
};

module.exports = {
    get_splash: getSplash,
    get_create_event: getCreateEvent,
    create_event: createEvent,
    list_events: listEvents,
    clear_all_events: clearAllEvents,
    get_home: getHome,
    get_login: getLogin,
    create_group: createGroup,
    get_register: getRegister,
    check_login: checkLogin,
    get_logout: getLogout,
    get_profile: getProfile
}
