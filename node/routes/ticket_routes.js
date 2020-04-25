var User = require('../models/user.js');
var Show = require('../models/show.js')
var Ticket = require('../models/ticket.js')
var Change = require('../models/change.js')
var Event = require('../models/event.js')

var async = require('async')

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
                    var tickets_sold = show.tickets_sold;
                    // if the show is found, make sure there are tickets available
                    if (!tickets_sold) {
                        tickets_sold = 0;
                    }
                    if (tickets_sold < show.capacity) {
                        // if there are tickets available, create a new ticket
                        var newTicket = new Ticket({
                            show: show._id,
                            redeemed: false,
                            requested: false,
                            customer: user.first_name + " " + user.last_name ,
                            user: user._id
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
                                        var newTickets = show.tickets ? show.tickets : [];
                                        newTickets.push(ticket._id);
                                        show.update({ tickets_sold: tickets_sold + 1, tickets: newTickets }, (err) => {
                                            if (tickets_sold + 1 == show.capacity) {
                                                sellOut(show)
                                            }
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

var requestTicket = function(req, res) {
    var ticketID = req.body.ticketID;
    Ticket.findByIdAndUpdate(ticketID, {requested : true}, (err) => {
        if (err) {
            res.json({"status":err})
        } else {
            res.json({"status":"success"});
        }
    })

}

var getTicket = function (req, res) {
    var ticketID = req.query.ticketID
    Ticket.findById(ticketID, (err, ticket) => {
        !err && ticket ? res.json({ "ticket": ticket }) : res.json({ "err": err })
    })
}


var getTicketStats = function (req, res) {
    var shows = req.query.showID;
    var isRevenue = parseInt(req.query.isRevenue, 10);
    var finalData = [];
    if (!Array.isArray(shows)) {
        shows = [shows];
    }
    async.forEach(shows, (showID, done) => {
        getShowTicketStatsHelper(showID, isRevenue, (err, data) => {
            if (!err && data) {
                data.forEach(item => {finalData.push(item)});
            }
            done();
        })
    }, () => {
        res.json({"status":"success","data":reduceData(finalData)});
    });
    
}

function reduceData(data) {
    dates = {}
    for (var d of data) {
        dates[d[0]] = 1;
    }
    var finalData = [];
    for (var d of Object.keys(dates)) {
        finalData.push([d, sum(data.map((x) => {return (x[0] == d) ? x[1] : 0}))])
    }
    return finalData;
}

function sum(arr) {
    var c = 0;
    arr.forEach(x => {c += x});
    return c;

}

function mapFormat(map) {
    obj = []
    map.forEach((v, k) => {
        obj.push([k,v]);
    })
    return obj;
}

function formatTime(time) {
    var m = time.getMonth() + 1;
    var d = time.getDay() + 1;
    var y = time.getYear() + 1900;
    var key = m + "-" + d + "-" + y;
    return key;
}

var getShowTicketStatsHelper = function(showID, isRevenue, callback) {
    var dateMap = new Map();
    Show.findById(showID, (err, show) => {
        if (!err && show) {
            show = show.toJSON();
            var scale = isRevenue ? parseFloat(show.price.toString()) : 1;
            var ticketIDs = show.tickets;
            async.forEach(ticketIDs, (ticketID, done) => {
                Ticket.findById(ticketID, (err, ticket) => {
                    if (!err && ticket) {
                        ticket = ticket.toJSON();
                        var key = formatTime(ticket._id.getTimestamp());
                        dateMap.set(key, dateMap.has(key) ? dateMap.get(key) + scale : scale);  
                    }
                    done();
                });
            }, () => {
                callback(null, mapFormat(dateMap));
            })
        } else {
            callback(err ? err : "no show", null);
        }
    })
}


var redeemTicket = function(req, res) {
    var ticketID = req.body.ticketID;  
    Ticket.findByIdAndUpdate(ticketID, {redeemed: true}, (err) => {
        if (err) {
            console.log(err);
        }
        res.json({"status":(!err ? "success":"error")});
    });
}

var sellOut = function(show) {
    var change = new Change({
        field_changed: "tickets available for " + show.name,
        previous_value: 1,
        updated_value: 0,
        time: Date.now()
    })
    change.save((err, changeSaved) => {
        if (err) console.log("Error creating change obj. " + err)
        else {
            Event.findById(show.event, (err, event) => {
                if (err) console.log("Error finding event when selling out")
                else {
                    updatedChanges = event.changes
                    updatedChanges.push(changeSaved._id)
                    Event.findByIdAndUpdate(show.event, {changes: updatedChanges}, () => {})
                }
            })
        }
    })
}



module.exports = {
    purchase_ticket: purchaseTicket,
    request_ticket: requestTicket,
    get_ticket: getTicket,
    redeem_ticket: redeemTicket,
    get_ticket_stats: getTicketStats
}