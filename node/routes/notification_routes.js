var User = require('../models/user.js');
var Event = require('../models/event.js')
var Show = require('../models/show.js')
var Ticket = require('../models/ticket.js')
var Notification = require('../models/notification.js')
var Group = require('../models/group.js')


var async = require('async')

var createNotification = function(req, res) {
    var userID = req.body.userID;
    var content = req.body.content;
    // calls helper which also handles user side
    addNotification(userID, content, (err, notification) => {
        if (!err && notification) {
            res.json({
                "status":"success", 
                "notification":notification
            });
        } else {
            res.json({"status":"error"});
        }
    })
}

var addNotification = function(userID, content, callback) {
    var newNotification = new Notification({
        content: content
    })
    newNotification.save((err, notification) => {
        if (err || !notification) {
            callback("error", null);
        } else {
            User.findById(userID, (err, user) => {
                if (err || !user) {
                    callback("error", null);
                } else {
                    user = user.toJSON();
                    notification = notification.toJSON();
                    // get notifications if exist
                    var notifications = user.notifications ? user.notifications : [];
                    // add notification to list
                    notifications.push(notification._id);
                    User.findByIdAndUpdate(userID, {"notifications":notifications}, (err, user) => {
                        if (!err && user) {
                            // send back notification
                            callback(null, notification);
                        } else {
                            callback("error", null);
                        }
                    })

                }
            });
        }
    });

}

function getUserNotifications(req, res) {
    var email = req.query.email;
    getAllNotifications(email, (err, notifications) => {
        if (err) {
            res.json({"status":"error"})
        } else {
            res.json({
                "status":"success","notifications":notifications
            })
        }
    })
}

function getAllNotifications(email, callback) {
    User.findOne({email:email}, (err, user) => {
        if (err || !user) {
            callback("error", null);
        } else {
            user = user.toJSON();
            var notificationIDs = user.notifications;
            if (!notificationIDs) {
                notificationIDs = [];
            }
            var notifications = [];
            async.forEach(notificationIDs, (notificationID, done) => {
                Notification.findById(notificationID, (err, notification) => {
                    if (!err && notification) {
                        // notification = notification.toJSON();
                        notifications.push(notification);
                    }
                    done();
                })
            }, () => {

                notifications = sortNotifications(notifications);
                callback(null, notifications);
            });

        }
    })
}

function readAllNotifications(req, res) {
    var email = req.body.email;
    User.findOne({email:email}, (err, user) => {
        if (!err && user) {
            user = user.toJSON();
            var notificationIDs = user.notifications;
            if (notificationIDs) {
                async.forEach(notificationIDs, (notificationID, done) => {
                    readNotification(user._id, notificationID, () => {
                        done();
                    })
                }, () => {
                    // need to manually clear any remaining notifications
                    User.findOneAndUpdate({email:email}, {notifications:[]}, (err, _) => {
                        res.json({"status":"success"});
                    });
                    
                });
            }
        }
    });
}

function readNotification(userID, notificationID, callback) {
    Notification.findByIdAndRemove(notificationID, (err, notification) => {
        User.findById(userID, (err, user) => {
            if (!err && user) {
                user = user.toJSON();
                var notifications = user.notifications ? user.notifications : [];
                notifications = notifications.filter((n) => {return n != notificationID});
                User.findByIdAndUpdate(userID, {notifications:notifications}, (err, user) => {
                    callback();
                })
            }
        });
    })
}


var notifyShow = function(req, res) {
    var content = req.body.content;
    var showID = req.body.showID;
    notifyAllShowTicketHolders(showID, content, (err) => {
        if (err) {
            res.json({"status":"error"});
        } else {
            res.json({"status":"success"})
        }
    });
}

var notifyEvent = function(req, res) {
    console.log("NOTIFY EVENT RUN");

    var content = req.body.content;
    var eventID = req.body.eventID;
    console.log(content);
    console.log(eventID)
    Event.findById(eventID, (err, event) => {
        if (!err && event) {
            event = event.toJSON();
            async.forEach(event.shows, (showID, done) => {
                notifyAllShowTicketHolders(showID, content, (err) => {
                    if (err) {
                        res.json({"status":"error"});
                    } else {
                        done();
                    }
                });
            }, () => {
                res.json({"status":"success"})
            });
        } else {
            console.log(err);
            res.json({"status":"error"});
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

function sortNotifications(notifications) {
    // TODO: this does not work!
    notifications = notifications.sort((a, b) => {
        console.log(typeof new Date(a._id.getTimestamp()))
        return -new Date(a._id.getTimestamp()) + new Date(b._id.getTimestamp());
    })
    return notifications;
}

var notifyAllShowTicketHolders = function(showID, content, callback) {
    Show.findById(showID, (err, show) => {
        if (!err && show) {
            var ticketIDs = show.tickets ? show.tickets : [];
            var preface = show.name ? show.name + ": " : "";
            getAllTickets(ticketIDs, (tickets) => {
                async.forEach(tickets, (ticket, done) => {
                    var userID = ticket.user;
                    if (userID) {
                        addNotification(userID, preface + content, (err, _) => {
                            done();
                        })
                    } else {
                        // if ticket has no user
                        done();
                    }

                }, () => {
                    callback(null); // return no error
                });
            })
        } else {
            callback(err);
        }
    });
} 


var notifyFollowers = function(req, res) {
    
    var content = req.body.content;
    var groupID = req.body.groupID;

    console.log("ENTERED NOTIFY FOLLOWERS")
    console.log("GROUPID" + groupID)
    console.log("CONTENT" + content)
    notifyAllFollowers(groupID, content, (err) => {
        if (err) {
            res.json({"status":"error"});
        } else {
            res.json({"status":"success"})
        }
    });
}


var notifyAllFollowers = function(groupID, content, callback) {
    console.log("ENTERED NOTIFY ALL FOLLOWERS")
    console.log("GROUPID" + groupID)
    console.log("CONTENT" + content)
    Group.findById(groupID, (err, group) => {
        if (!err && group) {
            var followers = group.followers;
                async.forEach(followers, (follower, done) => {
                var userID = follower;
                if (userID) {
                    addNotification(userID, content, (err, _) => {
                        done();
                    })
                } else {
                    done();
                }

            })
        } else {
            callback(err);
        }
    });
} 

module.exports = {
    create_notification: createNotification,
    get_user_notifications: getUserNotifications,
    read_all_notifications: readAllNotifications,
    notify_event: notifyEvent,
    notify_show: notifyShow,
    notify_followers: notifyFollowers
}