var getSplash = function (req, res) {

    // TODO: Implement for real
    res.render('splash.ejs')
}

var getHome = function (req, res) {
    res.render('home.ejs')
}

var getCreateEvent = function (req, res) {
    // TODO: Implement for real
    res.render('create_event.ejs')
}

// handle post request for event creation

// TODO: need to talk to Michael about how adding tags will work
var addEventTag = function (req, res) {
    //TODO: Implement for real
    res.render('create_event.ejs')
}


module.exports = {
    get_splash: getSplash,
    get_create_event: getCreateEvent,
    get_home: getHome
}