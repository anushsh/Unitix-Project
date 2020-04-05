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
