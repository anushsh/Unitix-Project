/**
 * This file is for helper functions that can be used in ejs files for working with html.
 * It should be imported into any page with the header
 */
 
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


  /**
   * Helper methods for working with show html
  */
 function addShowHTML() {
    if (currShowNum == 1) {
        $("#singleShowDetails").removeClass("is-hidden")
        $("#singleShowDetails").addClass("is-active")
        $("#nextShowButton").removeClass("is-hidden")
        $("#nextShowButton").addClass("is-active")

        $("#showName").val($("#eventName").val())
    }
    if (currShowNum == $("#showCount").val()) {
        $("#createEventButton").removeClass("is-hidden")
        $("#createEventButton").addClass("is-active")

        $("#nextShowButton").removeClass("is-active")
        $("#nextShowButton").addClass("is-hidden")
    }

    $("#startDate").val('')
    $("#startTime").val('')
    $("#endTime").val('')

    $("#showHeaderInfo").html("Details for show " + currShowNum++)
}

// currently doesn't check for non-empty values.
function storeShowInfo() {
    shows.push({
        name: $("#showName").val(),
        capacity: $("#showCapacity").val(),
        price: $("#ticketPrice").val(),
        location: $("#showLocation").val(),
        description: $("#description").val(),
        date: $("#startDate").val(),
        startTime: $("#startTime").val(),
        endTime: $("#endTime").val()
    })
    console.log(shows)
    addShowHTML()
}
// --------------

/**
 * Helper method for interacting with validation of input.
 * If valid, green outline. Else, red outline with message
 */
function validateAndSetReactionaryColors(id, valid) {
    if (!valid) {
        // make red outline
        $('#' + id).removeClass('is-success');
        $('#' + id).addClass('is-danger');

        // error message becomes visible
        $('#' + id + "Invalid").removeClass('is-hidden')
        $('#' + id + "Invalid").addClass('is-active')
    } else {
        // make green outline
        $('#' + id).removeClass('is-danger');
        $('#' + id).addClass('is-success');

        // error message goes away
        $('#' + id + "Invalid").addClass('is-hidden')
        $('#' + id + "Invalid").removeClass('is-active')
    }
    return valid;
}
// ------------

module.exports = {
    getGroup: getGroup,
    greet: greet,
    goHome: goHome,
    getMessage: getMessage,
    deleteMessage: deleteMessage,
    addShowHTML: addShowHTML,
    storeShowInfo: storeShowInfo,
    validateAndSetReactionaryColors: validateAndSetReactionaryColors
}