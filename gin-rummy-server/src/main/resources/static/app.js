var clientId = Math.floor(Math.random() * 1000000);
var stompClient = null;
const noCard = "X"
const allScenes = ['welcome', 'waiting', 'play']
var state = 'welcome'

var playerNum = 0
var gameId = null

var hand1 = []
var hand2 = []
var discard = []
var decision = null

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
            playerNum = 1
            console.log('Response: ' + response.body)
            renderScene('waiting')
            var responseBody = JSON.parse(response.body)
            gameId = responseBody.gameId
            document.querySelector("#waiting-game-id").innerText = responseBody.gameId
            stompClient.subscribe('/game/' + responseBody.gameId + "/client/" + clientId, handleGameMessage)
        });
        sendCreateNewGame()
    });
}

function connectAsSecond() {
    var socket = new SockJS('/gin-rummy-ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        playerNum = 2
        gameId = document.querySelector("#game-id-to-join").value
        console.log('Connected: ' + frame);
        stompClient.subscribe('/game/' + gameId + "/client/" + clientId, handleGameMessage)
        sendJoinGame(gameId)
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function handleGameMessage(response) {
    renderScene('play');
    var message = JSON.parse(response.body);
    switch (message.name) {
        case 'AskYesNo':
            handleAskYesNo(message);
            break;
        case 'AskSelectZone':
            handleAskSelectZone(message);
            break;
        case 'AskSelectCard':
            handleAskSelectCard(message);
            break;
        case 'CardMoved':
            handleCardMoved(message);
            break;
        case 'GameOver':
            handleGameOver(message);
            break;
    }
    invalidatePlayScene();
}

function handleCardMoved(message) {
    var card = parseCard(message.card)
    if (message.from === "HAND_1") {
        if (playerNum === 1) hand1 = hand1.filter(c => c.name !== card.name)
        else hand1.pop()
    } else if (message.from === "HAND_2") {
        if (playerNum === 2) hand2 = hand2.filter(c => c.name !== card.name)
        else hand2.pop()
    } else if (message.from === "DISCARD" ) {
        discard.pop()
    }

    if (message.to === "HAND_1") {
        hand1.forEach(c => { c.last = false });
        card.last = true;
        hand1.push(card);
    } else if (message.to === "HAND_2") {
        hand2.forEach(c => { c.last = false });
        card.last = true;
        hand2.push(card);
    } else if (message.to === "DISCARD") {
        discard.push(card)
    }
}

function handleAskYesNo(message) {
    decision = {
        "message" : message.message,
        "type": "yesNo"
    }
}

function handleAskSelectZone(message) {
    decision = {
        "message" : message.message,
        "type": "zone"
    }
}

function handleAskSelectCard(message) {
    decision = {
        "message" : message.message,
        "type": "card"
    }
}

function handleGameOver(message) {
    var type = message.yourScore > message.opponentScore ? "win" : "lose"
    decision = {
        message: "You " + type + ". Your score: " + message.yourScore + ". Opponent score: " + message.opponentScore,
        type: type
    }
}

function invalidatePlayScene() {
    document.querySelector("#opponents-hand-cards").innerHTML = "";
    document.querySelector("#opponents-hand-cards").append(handAsHtml(playerNum === 1 ? hand2 : hand1));

    document.querySelector("#your-hand-cards").innerHTML = "";
    document.querySelector("#your-hand-cards").append(handAsHtml(playerNum === 1 ? hand1 : hand2));

    document.querySelector("#top-discard-card").innerHTML = "";
    if (discard.length > 0) {
        document.querySelector("#top-discard-card").append(cardAsHtml(discard.at(-1)))
    }

    document.querySelector("#decision-message").innerText = decision?.message || "";

    document.querySelector("#decision-yes-no").hidden = decision?.type !== "yesNo";
}

function handAsHtml(hand) {
    var cards = hand.map(card => card);
    cards.sort(compareCards);
    var divs = cards.map(card => {
        var div = cardAsHtml(card);
        addOnclickAsSelectCard(card, div);
        return div;
    });
    var wrap = document.createElement("div");
    wrap.append(...divs);
    return wrap;
}

function compareCards(c1, c2) {
    if (c1 === null && c2 !== null) return -1
    if (c1 !== null && c2 === null) return 1
    if (c1 === null && c2 === null) return 0
    if (c1.suit < c2.suit) return -1
    if (c1.suit > c2.suit) return 1
    if (c1.order < c2.order) return -1
    if (c1.order > c2.order) return 1
    return 0
}

function cardAsHtml(card, onclickAsCard) {
    var cardDiv = document.createElement("div")
    cardDiv.className = "my-card "
    if (card.flip) {
        cardDiv.className += ""
    } else {
        cardDiv.className += card.suit.toLowerCase() + " "

        var nominalSpan = document.createElement("span")
        nominalSpan.innerText = card.nominal

        if (card.last) {
            cardDiv.className += "last-card "
        }

        cardDiv.appendChild(nominalSpan)
    }
    return cardDiv
}

function addOnclickAsSelectCard(card, cardDiv) {
    cardDiv.onclick = function (e) {
        sendGameMessage({ name: "TellSelectCard", card : card.suit + "_" + card.nominal })
        decision = null;
        invalidatePlayScene();
    }
}

function parseCard(cardName) {
    if (!cardName) {
        return {
            flip: true
        }
    }
    var parts = cardName.split("_")
    return {
        suit: parts[0],
        nominal: parts[1],
        name: cardName,
        order: cardNominalToOrder(parts[1])
    }
}

function cardNominalToOrder(nominal) {
    switch (nominal) {
        case "A": return 1;
        case "2": return 2;
        case "3": return 3;
        case "4": return 4;
        case "5": return 5;
        case "6": return 6;
        case "7": return 7;
        case "8": return 8;
        case "9": return 9;
        case "10": return 10;
        case "J": return 11;
        case "Q": return 12;
        case "K": return 13;
        default: return 0;
    }
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
    document.querySelector("#greetings").innerText += "<tr><td>" + message + "</td></tr>";
}

window.onload = function () {
    renderScene('welcome')
    document.querySelector("#start-new-game").onclick = function (e) {
        startNewGame()
    };
    document.querySelector("#join-game").onclick = function (e) {
        joinGame()
    };
    document.querySelector("#decision-yes").onclick = function (e) {
        sendGameMessage({ name: "TellYesNo", yes : true })
        decision = null;
        invalidatePlayScene();
    };
    document.querySelector("#decision-no").onclick = function (e) {
        sendGameMessage({ name: "TellYesNo", yes : false })
        decision = null;
        invalidatePlayScene();
    };
    document.querySelector("#deck").onclick = function (e) {
        sendGameMessage({ name: "TellSelectZone", zone : "DECK" })
        decision = null;
        invalidatePlayScene();
    };
    document.querySelector("#top-discard-card").onclick = function (e) {
        sendGameMessage({ name: "TellSelectZone", zone : "DISCARD" })
        decision = null;
        invalidatePlayScene();
    };
};

function sendGameMessage(messageObj) {
    stompClient.send("/app/game/" + gameId + "/client/" + clientId, {}, JSON.stringify(messageObj))
}