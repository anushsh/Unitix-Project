var User = require('../models/user.js');
var Show = require('../models/show.js')
var Ticket = require('../models/ticket.js')

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


var redeemTicket = function(req, res) {
    var ticketID = req.body.ticketID;  
    Ticket.findByIdAndUpdate(ticketID, {redeemed: true}, (err) => {
        if (err) {
            console.log(err);
        }
        res.json({"status":(!err ? "success":"error")});
    });
}



module.exports = {
    purchase_ticket: purchaseTicket,
    request_ticket: requestTicket,
    get_ticket: getTicket,
    redeem_ticket: redeemTicket
}