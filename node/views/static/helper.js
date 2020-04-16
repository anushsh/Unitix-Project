/**
 * This file is for helper functions that can be used in ejs files. It should be imported into any page
 * with the header
 */

 var test = function() {
     return "test";
 }

 /**
  * get a group and greet them. requires a greeting id in the html
  */
 function getGroup() {
    $.get('/get_group', (data) => {
        group = data.group
        greet()
    })
  }

  function greet() {
    console.log(group)
    console.log(group.displayName)
    $('#greeting').append(group.displayName)
  }

  // redirect user to the home page
  function goHome(msg) {
    window.location.href = "/home?msg=" + msg
  }

  function getMessage() {
      $.get("/get_message", (messageRes) => {
          console.log(messageRes)
          message = messageRes
          if (message) {
            $('#msgBlock').addClass('is-active')
            $('#msgBlock').removeClass('is-hidden')
            $('#msg').html(message)
        }
      })
  }

  function deleteMessage() {
      $('#msgBlock').removeClass('is-active')
      $('#msgBlock').addClass('is-hidden')
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
 }

 var prettyDate = function(date) {
    const monthNames = [ "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December" ];
    var dateTrim = date.split("T")[0];
    var parts = dateTrim.split("-");
    parts[1] = monthNames[parseInt(parts[1], 10)]
    return [parts[1], parts[2] + ",", parts[0]].join(" ");
 }

 function displayEvent(event) {
    var display = '<div class="box">';
    display += "<p class=\"subtitle is-5\">" + event.name + "</p>";
    display += createButton("Notify all ticket holders", "showNotifyEvent", event._id, "button is-small",
        "event_notification_button"+event._id);
    display += '<div id="eventNotifyText' + event._id + '"></div>'
    display += "<br>"
    var showNumber = 1;
    event.shows.forEach((show) => {
        var list = "<ul id=\"" + show._id + "\"></ul>";
        var name = "Show #" + showNumber + " on " + prettyDate(show.start_date) + " at " + prettyTime(show.start_time);
        var tickets = "<br>Tickets sold: " + show.tickets_sold;
        var btn1 = createButton("View Attendees", "viewShow", show._id, "button is-small","show_button"+show._id);
        var btn2 = createButton("Notify Ticket Holders (this show only)", "showNotifyShow", show._id, "button is-small",
        "show_notification_button" + show._id);
        var notificationBox = '<div id="showNotifyText' + show._id + '"></div>';
        display +=  name + tickets + "<br>" + 
            btn1 + btn2 + notificationBox + list + "<br>";
        showNumber += 1;
    });            
    var footer =   '<footer class="card-footer">'+
    '<a href="/edit_event/'+encodeURI(event.name)+'" class="card-footer-item">Edit Event</a></footer>';
    display += footer;
    display += ' </div>'
    return display;
}

function showNotify(id, isShow) {
    // determine if handling show or event notifications
    var type = !isShow ? "event" : "show";
    var btn = $("#" + type + "_notification_button" + id);
    // if notification form is already open...
    if (btn.val() == "on") {
        // close it and restore button text
        var msg = "Notify all ticket holders";
        msg = isShow ? 'Notify Ticket Holders (this show only)' : msg;
        btn.html(msg); 
        btn.prop("value","off");
        $("#" + type + "NotifyText" + id).html(""); 
    } else {
        // otherwise, create form to handle posting notifications
        btn.html("Cancel");
        btn.prop("value","on");
        var form = '<form >' 
        form += '<textarea class = "textarea" id="content'+id+'" type="text" placeholder="(Write your notification here)"'
        form += 'name="notification" required></textarea>'
        form += createButton("Notify!", isShow ? "notifyShow" : "notifyEvent", id, "button is-link is-small");
        form += '</form>';
        $("#" + type + "NotifyText" + id).html(form);
    }
}

function showNotifyEvent(eventID) {
    showNotify(eventID, false);
}

function showNotifyShow(showID) {
    showNotify(showID, true);
}

function notifyEvent(eventID) {
    var content = $("#content" + eventID).val();
    $.post('/notify_event',{"eventID":eventID,"content":content}, (res) => {
        console.log(res);
    })
}

function notifyShow(showID) {
    var content = $("#content" + showID).val();
    $.post('/notify_show',{"showID":showID,"content":content}, (res) => {
        console.log(res);
    })
}

// loads events into ul with id 'currentEvents'
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
            // $("#status" + ticketID).html("");
        }
    });
}   


function viewShow(showID) {
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
            // $("#" + show._id).append(closeButton);
            
        });
    }
}




 var sortEvents = function(events) {
    events.forEach(event => {sortShows(event.shows)});
    events = events.sort((eventA, eventB) => {
        try {
            // first show should be earliest
            var showA = eventA.shows[0];
            var showB = eventB.shows[0];
            return compareShows(showA, showB);
            
        } catch(err) {
            // in case no shows
            return 0;
        }
    });
    return events;
 }

 var sortShows = function (shows) {
    return shows.sort(compareShows);
}

// -------- sorting helpers -------

var compareShows = function(showA, showB) {
    try {
        var datesDiff = compareDates(showA.start_date, showB.start_date);
        if (datesDiff != 0) {
            return datesDiff;
        }
        return compareTimes(showA.start_time, showB.start_time);
    } catch(err) {
        console.log("sorting error below");
        console.log(err);
    }
    return 0;
 }

 var compareDates = function(aDate, bDate) {
    var parts = [aDate, bDate].map(x => {
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
    return 0;
 }

 var compareTimes = function(aTime, bTime) {
    var parts = [aTime, bTime].map(x => {
        return x.split(":").map((y) => {
            return parseInt(y, 10)
        })});
    aParts = parts[0];
    bParts = parts[1];
    diffs = aParts.map((x, i) => {return x - bParts[i]});
    for (diff of diffs) {
        if (diff != 0) {
            return diff;
        }
    }
    return 0;
 }

 

 
