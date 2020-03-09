var getSplash = function (req, res) {

    // TODO: Implement for real
    res.render('splash.ejs')
}

var createEvent = function (req, res) {
    // TODO: Implement for real
    res.render('create_event.ejs')
}


module.exports = {
    get_splash: getSplash,
    create_event: createEvent
}
