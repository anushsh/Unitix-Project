var Group = require('../models/group.js');
var Event = require('../models/event.js')
var Show = require('../models/show.js')
var Ticket = require('../models/ticket.js')
var Change = require('../models/change.js')

var async = require('async')


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


// route will wipe Event DB
// WARNING - DEPRECATED
var clearAllEvents = function (req, res) {
    callback = function () { console.log("done") }
    Event.remove({}, callback)
    Show.remove({}, callback)
    res.json("finished")
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

var listEventsWithShows = function (_, res) {
    Event.find((err, allEvents) => {
        if (err) {
            res.json({ 'status': err })
        } else if (allEvents.length == 0) {
            res.json({ 'status': 'no events' })
        }
        events = [];
        async.forEach(allEvents, (event, done) => {
            event = event.toJSON();
            getEventWithShows(event._id, (err, event) => {
                if (!err && event) {
                    events.push(event);
                }
                done();
            })}, () => {
            res.json({
                'status': 'success',
                'events': events
            })
        })
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


var findEventWithShows = function (req, res) {
    var eventID = req.query.eventID;
    getEventWithShows(eventID, (err, event) => {
        if (err || !event) {
            res.json({ "status": "failure" });
        } else {
            res.json({
                "status": "success",
                "event": event
            });
        }
    })
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

var getEventByName = function(req, res) {
    var eventName = req.query.eventName
    Event.findOne({name:eventName}, (err, event) => {
        !err && event ? res.json({"event":event}) : res.json({"err":err})
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
                !err ? res.json({ "status": "success" }) : res.json({"status":"failure"})
            });
        }
    })
}

var addEventIdToGroup = function (req, res) {
    var eventID = req.body.eventID
    var groupEmail = req.body.groupEmail
    console.log("ADDING EVENT ID TO GROUP")
    Group.findOne({email:groupEmail}, (err, group) => {
        if (err || !group) {
            res.json({"status":"failure finding group"})
        } else {
            currentEventsUpdated = group.currentEvents
            currentEventsUpdated.push(eventID)
            group.update({currentEvents: currentEventsUpdated}, (err) => {
                !err ? res.json({ "status": "success" }) : res.json({"status":"failure"})
            })
        }
    })
}

function getShowWithTickets(req, res) {
    var showID = req.query.showID;
    console.log("SHOW ID " + showID);
    Show.findById(showID, (err, show) => {
        if (!err && show) {
            show = show.toJSON();
            var ticketIDs = show.tickets ? show.tickets : [];
            getAllTickets(ticketIDs, (tickets) => {
                show.tickets = tickets;
                res.json({"status":"success", "show":show});
            });
        } else {
            res.json({"status":"error"})
        }
    });
}

function getAllTickets(ticketIDs, callback) {
    var tickets = []; // array of ticket objects
    async.forEach(ticketIDs, (ticketID, done) => {
        Ticket.findById(ticketID, (err, ticket) => {
            if (!err && ticket) {
                ticket = ticket.toJSON();
                tickets.push(ticket);
            }
            done();
        })
    }, () => {
        // no error since will always return at least empty array
        callback(tickets);
    });
}

// TODO: Add Change Field
var updateEventOverview = function (req, res) {
    old = req.body.old
    updated = req.body.updated


    changeArr = []
    if (old.name != updated.name) {
        changeArr.push({
            field_changed: "name",
            prior_value: old.name,
            updated_value: updated.name
        })
    }
    if (old.tags != updated.tags) {
        changeArr.push({
            fieldChanged: "tags",
            priorValue: JSON.stringify(old.tags),
            updatedValue: JSON.stringify(updated.tags)
        })
    }
    console.log("Found " + changeArr.length + " changes")

    changeIDs = []
    async.forEach(changeArr, (changeObj, done) => {
        var change = new Change({
            field_changed: changeObj.fieldChanged,
            prior_value: changeObj.priorValue,
            updated_value: changeObj.updatedValue,
            time: Date.now()
        })
    
        change.save((err, changeSaved) => {
            if (err) console.log("Error creating change obj. " + err)
            changeIDs.push(changeSaved._id)
            done()
        })
    }, () => {
        // all changes processed, changeIDs has full array of obj ids
        Event.findOne({name: old.name}, (err, event) => {
            changesToPush = event.changes
            async.forEach(changeIDs, (id, done) => {
                changesToPush.push(id)
                done()
            }, () => {
                // changesToPush has the updated array of change obj ids
                Event.findOneAndUpdate({name: old.name}, {
                    name: updated.name,
                    tags: updated.tags,
                    changes: changesToPush
                }, (err, _) => {
                    err ? res.json({'err': err}) : res.redirect('/home')
                })
            })
        })
    })
}


// deletes all shows and all changes as well. also deletes this event from group array.
var deleteEvent = function (req, res) {
    name = req.query.name
    Event.findById(req.query.eventID, (err, event) => {
        if (!event) {
            console.log("ERROR GETTING EVENT. " + err)
        }
        deleteChanges(event.changes)
        deleteShows(event.shows)
        deleteEventFromGroup(req.session.user, req.query.eventID)
        Event.deleteOne({name: name}, (err, _) => {
            !err ? res.json("success") : res.json(err)
        })
    })
}



var deleteChanges = function(changes) {
    changes.forEach(change => {
        Changes.deleteOne({_id: change}, (err, _) => {
            if (err) console.log(err)
        })
    })
}

var deleteShows = function(shows) {
    shows.forEach(show => {
        Show.deleteOne({_id: show}, (err, _) => {
            if (err) console.log(err)
        })
    })
}

var deleteEventFromGroup = function(email, eventID) {
    Group.findOne({email: email}, (err, group) => {
        if (err || !group) console.log("ERROR : " + err)
        else {
            events = group.currentEvents
            modifiedEvents = []
            async.forEach(events, (event, done) => {
                console.log("Event: " + event + " and input param " + eventID + " equal with two is " + eventID == event + ", equal with three is " + eventID === event)
                if (event != eventID) {
                    modifiedEvents.push(event)
                }
                done()
            }, () => {
                Group.findOneAndUpdate({email: email}, {currentEvents: modifiedEvents}, (err, _) => {
                    if (err) console.log("Error deleting event from group: " + err)
                })
            })
        }
    })
}


var getSearchResultEvents = function (req, res) {
    var query = req.query.searchQuery;
    console.log("**************" + query);

    //TODO: COME BACK AND CHANGE "group" to "group_name" once that is updated in event creation
    Event.find({ $or: [ {name:{$regex: ".*" + query + ".*"}}, {group:{$regex: ".*" + query + ".*"}} ] }, (err, allEvents) => {
        if (err) {
            res.json({ 'status': err })
        } else if (allEvents.length == 0) {
            res.json({ 'status': 'no events' })
        } else {
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
        }

    });
    // Event.find({ $or: [ { "name":{$regex:".*" + query + ".*"} }, 
    // { "group_name":{$regex:".*" + query + ".*"} } ] }, (err, allEvents) => {
        
}


module.exports = {
    create_shows: createShows,
    create_event: createEvent,
    clear_all_events: clearAllEvents,
    list_events: listEvents,
    list_shows: listShows,
    list_events_with_shows: listEventsWithShows,
    find_event_with_shows: findEventWithShows,
    get_event: getEvent,
    get_event_by_name: getEventByName,
    add_event_id_to_show: addEventIdToShow,
    add_event_id_to_group: addEventIdToGroup,
    get_show_with_tickets: getShowWithTickets,
    update_event_overview: updateEventOverview,
    delete_event: deleteEvent,
    get_search_result_events: getSearchResultEvents
}