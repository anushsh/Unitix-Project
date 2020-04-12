/**
 * This file is for helper functions that can be used in ejs files. It should be imported into any page
 * with the header
 */

 var test = function() {
     return "test";
 }

 // creates a button that calls the specified function with optional parameter using 
 // optional bulmaClass. Can also set button id. Only buttonText and functionName are required
 var createButton = function(buttonText, functionName, functionParameter, bulmaClass, buttonID) {
    var param = functionParameter ? "(\'" + functionParameter + '\')"' : '()"';
    var clickPart = 'onClick="' + functionName + param;
    var leftSide = "<button " + (bulmaClass ? 'class ="' + bulmaClass + '"' : '');
    leftSide += (buttonID ? ' id="' + buttonID + '"' : "");
    return leftSide + clickPart+">"+buttonText+"</button>";
 }

 var prettyTime = function(time) {
    var parts = time.split(":");
    var hour = parseInt(parts[0], 10);
    var min = parts[1];
    var end = "AM";
    // fix 24 hour clock
    if (hour > 12) {
        end = "PM";
        hour = hour % 12;
    }
    // fix integer minute parsing
    if (min.length < 2) {
        min = "0" + min;
    }
    return hour + ":" + min + " " + end;



    return time;
 }

 var prettyDate = function(date) {
    const monthNames = [ "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December" ];
    var dateTrim = date.split("T")[0];
    var parts = dateTrim.split("-");
    parts[1] = monthNames[parseInt(parts[1], 10)]
    return [parts[1], parts[2], parts[0]].join(" ");
 }

 function displayEvent(event) {
    var display = '<div class="box">';
    display += "<p class=\"subtitle is-5\">" + event.name + "</p>";
    display += createButton("Notify all ticket holders", "showNotifyEvent", event._id, "button is-small",
        "notification_button"+event._id);
    display += '<div id="eventNotifyText' + event._id + '"></div>'
    display += "<br>"
    event.shows = sortShows(event.shows);
    event.shows.forEach((show) => {
        var list = "<ul id=\"" + show._id + "\"></ul>";
        var name = prettyDate(show.start_date) + " " + prettyTime(show.start_time);
        var btn1 = createButton("View Attendees", "viewShow", show._id, "button is-small","show_button"+show._id);
        var btn2 = createButton("Notify Ticket Holders (this show only)", "showNotifyShow", show._id, "button is-small");
        display +=  name + "<br>" + 
            btn1 + btn2  + list + "<br>";
    });            
    display += ' </div>'
    return display;
}


function unviewShow(showID) {
    $("#" + showID).html("");
}

// TODO: refactor to allow for notifying shows or events
function showNotifyEvent(eventID) {
    // create form
    var btn = $("#notification_button" + eventID);
    if (btn.val() == "on") {
        btn.html("Notify all ticket holders"); // make generic
        btn.prop("value","off");
        $("#eventNotifyText" + eventID).html(""); // make generic
    } else {
        btn.html("Cancel");
        btn.prop("value","on");
        
        var form = '<form action="/notifyEvent" method="post">' // make generic
        form += '<textarea class = "textarea" type="text" placeholder="(Write your notification here)"'
        form += 'name="notification" required></textarea>'
        form += '<button class="button is-link is-small" type="submit" class="btn btn-primary">Notify!</button>'
        form += '</form>';
        $("#eventNotifyText" + eventID).html(form); // make generic
    }
}

function showNotifyShow(showID) {
    alert("TODO: notify show " + showID);
}

function loadEvents() {
    $.getJSON('/get_group_with_events', (res) => {
        var group = res.group;
        
        if (group) {
            var currentEvents = sortEvents(group.currentEvents);
            currentEvents.forEach(event => {     
                $("#currentEvents").append(displayEvent(event));
            });
        }
    })
}

function requestTicket(ticketID) {
    $.post("/request_ticket",{"ticketID":ticketID},(res) => {
        if (res.err == "success") {
            $("#status" + ticketID).html("");
        }
    });
}   


function viewShow(showID) {
    // alert('here');

    var btn = $("#show_button" + showID);
    if (btn.val() == "on") {
        btn.html("View attendees");
        btn.prop("value","off");
        $("#" + showID).html("");
    } else {
        btn.prop("value","on");
        btn.html("Hide attendees");
        $.getJSON("/get_show_with_tickets", {"showID":showID}, (data) => {
            var show = data.show;
            var tickets = show.tickets;
            tickets.forEach((ticket) => {
                var attendee = ticket.customer ? ticket.customer : "NAME N/A (id=" +ticket._id+")";
                var status = "";
                if (ticket.requested == false) {
                    var btn = createButton("Check in", "requestTicket", ticket._id, "button is-small");
                    status += btn;
                } else if (ticket.redeemed == false) {
                    status += " (Pending)"
                } else {
                    status += " (Checked in)"
                }
                status += " </div>"
                attendee += status;

                $("#" + show._id).append(attendee + "<br>");                                        
            })
            $("#" + show._id).append(closeButton);
            
        });
    }
}




 var sortEvents = function(events) {
    events.forEach(event => {sortShows(event.shows)});
    return events;
 }

 var sortShows = function (shows) {
     return shows.sort((a, b) => {
        try {
            var parts = [a.start_date,b.start_date].map(x => {
                return x.split("T")[0].split("-").map(y => {
                    return parseInt(y, 10)
                })
            });
            var aParts = parts[0];
            var bParts = parts[1];
            var diffs = aParts.map((x, i) => {return x - bParts[i]});
            for (diff of diffs) {
                if (diff != 0) {
                    return diff;
                }
            }
            parts = [a.start_time,b.start_time].map(x => {
                return x.split(":")}).map((y) => {
                    return parseInt(y, 10)
                });
            console.log(parts);
            aParts = parts[0];
            bParts = parts[1];
            diffs = aParts.map((x, i) => {return -x + bParts[i]});
            for (diff of diffs) {
                if (diff != 0) {
                    return diff;
                }
            }
        } catch(err) {
            console.log(err);
        }
        return -1;

     });
 }
