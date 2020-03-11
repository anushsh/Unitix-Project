var getSplash = function (req, res) {

    // TODO: Implement for real
    res.render('splash.ejs')
}

var getHome = function (req, res) {
    res.render('home.ejs')
}

var createEvent = function (req, res) {
    // TODO: Implement for real
    res.render('create_event.ejs')
}

// TODO: need to talk to Michael about how adding tags will work
var addEventTag = function (req, res) {
    //TODO: Implement for real
    res.render('create_event.ejs')
}


module.exports = {
    get_splash: getSplash,
    create_event: createEvent,
    get_home: getHome
}
