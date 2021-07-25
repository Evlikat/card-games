var clientId = Math.floor(Math.random() * 1000000);
var stompClient = null;
const allScenes = ['welcome', 'waiting', 'play']
var state = 'welcome'

function startNewGame() {
    connectAsFirst()
}

function joinGame() {
    connectAsSecond()
}

function renderScene(newState) {
    state = newState
    allScenes.forEach(scene => {
        var sceneElement = document.querySelector("#" + scene + "-scene")
        if (state === scene) {
            sceneElement.hidden = false;
        } else {
            sceneElement.hidden = true;
        }
    })
}

function connectAsFirst() {
    var socket = new SockJS('/gin-rummy-ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/client/' + clientId, function (response) {
            console.log('Response: ' + response.body)
            renderScene('waiting')
            var response = JSON.parse(response.body)
            document.querySelector("#waiting-game-id").innerHtml = response.gameId
            stompClient.subscribe('/game/' + response.gameId, handleGameMessage)
        });
        sendCreateNewGame()
    });
}

function connectAsSecond() {
    var socket = new SockJS('/gin-rummy-ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        var gameId = document.querySelector("#game-id-to-join").value
        console.log('Connected: ' + frame);
        stompClient.subscribe('/game/' + gameId, handleGameMessage)
        sendJoinGame(gameId)
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function handleGameMessage(message) {
    renderScene('play')
    console.log(message)
}

function sendCreateNewGame() {
    var playerInfo = {
        'name': 'Roman',// document.querySelector("#name").value,
        'clientId': clientId
    }
    stompClient.send("/app/game/new", {}, JSON.stringify(playerInfo));
}

function sendJoinGame(gameId) {
    var playerInfo = {
        'name': 'Roman',// document.querySelector("#name").value,
        'clientId': clientId
    }
    stompClient.send("/app/game/" + gameId, {}, JSON.stringify(playerInfo));
}

function showGreeting(message) {
    document.querySelector("#greetings").innerHtml += "<tr><td>" + message + "</td></tr>";
}

window.onload = function () {
    renderScene('welcome')
    document.querySelector("#start-new-game").onclick = function (e) {
        startNewGame()
    };
    document.querySelector("#join-game").onclick = function (e) {
        joinGame()
    };
};