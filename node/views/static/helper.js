/**
 * This file is for helper functions that can be used in ejs files. It should be imported into any page
 * with the header
 */

 var test = function() {
     return "test";
 }

 // creates a button that calls the specified function with optional parameter using 
 // optional bulmaClass. Only buttonText and functionName are required
 var createButton = function(buttonText, functionName, functionParameter, bulmaClass) {
    var param = functionParameter ? "(\'" + functionParameter + '\')"' : '()"';
    var clickPart = 'onClick="' + functionName + param;
    var leftSide = "<button " + (bulmaClass ? 'class ="' + bulmaClass + '"' : '');
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
