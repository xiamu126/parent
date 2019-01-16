var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    $.ajax({
        url: `http://localhost:8888/oauth/token`,
        type: "post",
        data: {
            grant_type: "password",
            client_id: "znld",
            client_secret: "defadfc2ad2fbb4fbcdc4d64f6c8d823",
            username: "test",
            password:"defadfc2ad2fbb4fbcdc4d64f6c8d823"
        },
        contentType: "application/x-www-form-urlencoded",
        success: function(data){
            console.log(data);
            var socket = new SockJS(`/WsEndpoint?access_token=${data.access_token}`);
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                setConnected(true);
                console.log('Connected: ' + frame);
                stompClient.subscribe('/topic/greetings', function (greeting) {
                    showGreeting(JSON.parse(greeting.body).content);
                });
                stompClient.subscribe('/my/message', function (response) {
                    showGreeting(JSON.parse(response.body).msg);
                });
            });
        }
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/ws/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});

