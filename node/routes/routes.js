var Event = require('../models/event.js')
var Show = require('../models/show.js')

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

var getLogin = (req, res) => {
    res.render('login.ejs');
}

module.exports = {
    get_splash: getSplash,
    get_create_event: getCreateEvent,
    create_event: createEvent,
    list_events: listEvents,
    clear_all_events: clearAllEvents,
    get_home: getHome
}
