@(tableName : String, bot : String)


$(function() {

  var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket;
  var gameSocket = new WS("@routes.Application.join(tableName, bot).webSocketURL(request)");
  var c = document.getElementById("myCanvas");
  var div = document.getElementById("board");
  var ctx = c.getContext("2d");

  var gameInfo = document.getElementById('gameInfo');
  var moveInfo = document.getElementById('moveInfo');
  var readyInfo = document.getElementById('readyInfo');
  //buttons
  var passButton = document.getElementById("passButton");
  var surrenderButton = document.getElementById("surrenderButton");
  var readyButton = document.getElementById("readyButton");
  var resumeButton = document.getElementById("resumeButton")

  var whitePrisonersNumber = document.getElementById('whitePrisonersNumber');
  var blackPrisonersNumber = document.getElementById('blackPrisonersNumber');

  var whiteReadyString = document.getElementById('whiteReadyInfo');
  var blackReadyString = document.getElementById('blackReadyInfo');

  gameInfo.innerHTML = "Waitng for an opponent...";
  
  c.width = div.clientWidth;
  c.height = div.clientHeight;

  var boardSize = 19;
  var fieldSize = c.height / (boardSize + 1);
  var pawnSize = fieldSize/2;
  var territorySquareSize = pawnSize;

  var whiteColor = 1;
  var blackColor = -1;
  var gameBoard = new Array(boardSize);
  var territoriesBoard = new Array(boardSize);

  var myColor = "PLACEHOLDER";

  for(var i = 0; i < boardSize; i++) {
  	gameBoard[i] = new Array(boardSize);
    territoriesBoard[i] = new Array(boardSize);
  	for(var j = 0; j < boardSize; j++) {
  		gameBoard[i][j] = 0;
      territoriesBoard[i][j] = 0;
    }
  }

  c.width = (boardSize + 1) * fieldSize;
  c.height = (boardSize + 1) * fieldSize;

  drawBoard();



  //FUNCTION DEFINITIONS

  var receiveEvent = function(event) {
    var data = JSON.parse(event.data)

    // Handle errors
    if(data.error) {
        chatSocket.close()
        $("#onError span").text(data.error)
        $("#onError").show()
        return
    } 
    else {
      console.log(data);

    	if(data.type == "move" ){
		  	placePawn(data.x, data.y, data.color);
		  } else if ( data.type == "sync" ){
        if(data.territories === false) {
          fillBoard(data.board);
        } else {
          fillTerritories(data.board);
        }
      } else if ( data.type == "territories") {
          moveInfo.innerHTML = "Territories mode";
          passButton.disabled = true;
          whiteReadyString.innerHTML = "NO";
          blackReadyString.innerHTML = "NO";
          readyInfo.style.visibility = "visible";
      } else if (data.type == "readyState") {
          whiteReadyString.innerHTML = (data.white == true ? "YES" : "NO");
          blackReadyString.innerHTML = (data.black == true ? "YES" : "NO");

          if(myColor == "White") {
            readyButton.innerHTML = (data.white == true ? "Unready" : "Ready");
          } else {
            readyButton.innerHTML = (data.black == true ? "Unready" : "Ready");
          }
      } else if (data.type == "resume") {
        passButton.disabled = false;
        readyInfo.style.visibility = "hidden";
        alert("Game was resumed. The player who didn't resume goes first now.");
      } else if ( data.type == "go") {
          moveInfo.innerHTML = "Your move!";
          alert("Your move!");
      } else if (data.type == "dontGo") {
          moveInfo.innerHTML = "Wait for the opponent to move...";
      } else if (data.type == "prisoners") {
          whitePrisonersNumber.innerHTML = data.white;
          blackPrisonersNumber.innerHTML = data.black;
      } else if (data.type == "start") {
          myColor = data.color;
          gameInfo.innerHTML = "You are player " + myColor;
		  } else if ( data.type == "end" ){
		  	 console.log("Game ended");
         var winner = data.winner;
         var endMessage = "Game over. The winner is player " + winner;
         var congratulations;
         if(winner == myColor) {
          congratulations = "You won!";
         } else {
          congratulations = "You lost!";
         }

         alert(endMessage + '\n' + congratulations);
      }
    }
  }


  gameSocket.onmessage = receiveEvent;
  
  //passButton handling
  passButton.addEventListener('click', function(event) {
  	makeMove(-1,-1,0);
  });

  readyButton.addEventListener('click', function(event) {
    makeMove(-1,-1,0);
  });

  surrenderButton.addEventListener('click', function(event) {
    makeMove(-2, -2, 0);
  });

  resumeButton.addEventListener('click', function(event) {
    makeMove(-3, -3, 0);
  });
  
  
  //detects click on the game board (used for placing stones)
  //calculates the closest spot (but not further than pawnSize/2) and tries to place a stone there
  c.addEventListener('click', function(event) {
    cLeft = c.offsetLeft;
    cTop = c.offsetTop;
    var x = event.pageX - cLeft;
    var y = event.pageY - cTop;

    var xDifference = x % fieldSize;
    var yDifference = y % fieldSize;
    
    if(x % fieldSize > fieldSize / 2) {
      xDifference -= fieldSize;
    }
    if(y % fieldSize > fieldSize / 2) {
      yDifference -= fieldSize;
    }

    var closestX = x - xDifference;
    var closestY = y - yDifference;
    
    if(closestX < fieldSize || closestY < fieldSize 
      || closestX >= (boardSize + 1)* fieldSize || closestY >= (boardSize + 1) * fieldSize) {
      return;
    }

    console.log("ee " + closestX + " ff " + closestY);
    if(Math.sqrt((x - closestX) * (x - closestX) + (y - closestY) * (y - closestY)) < pawnSize) {  
        console.log("here");
        closestX = closestX / fieldSize - 1;
        closestY = closestY / fieldSize - 1;

        closestX = Math.round(closestX);
        closestY = Math.round(closestY);
        makeMove(closestX, closestY, 1);
    }
  });

  //draws the current state of the board on the HTML5 canvas
  function drawBoard() {
    ctx.fillStyle="#dbb25c";
    ctx.fillRect(0, 0, (boardSize + 1) * fieldSize, (boardSize + 1) * fieldSize);
    ctx.fillStyle="#000000";
    for(var i = 0; i < boardSize; i++) {
      ctx.moveTo(fieldSize, (i + 1) * fieldSize);
      ctx.lineTo(fieldSize * boardSize, (i + 1) * fieldSize);
      ctx.stroke();
      ctx.moveTo((i + 1) * fieldSize, fieldSize);
      ctx.lineTo((i + 1) * fieldSize, fieldSize * boardSize);
      ctx.stroke();
    }
    
    if(boardSize == 19) {     //draw the little circles around the board
      for(var i=4;i<boardSize;i+=6){    
        for(var j=4;j<boardSize;j+=6){
      
          ctx.beginPath();
          ctx.arc(i * fieldSize, j * fieldSize, fieldSize/5, 0, 2 * Math.PI);
          ctx.fill();
          ctx.closePath();
        }
      }
    }
    
    for(var i = 0; i < boardSize; i++) {
      for(var j = 0; j < boardSize; j++) {
        var draw = false;
        if(gameBoard[i][j] === whiteColor) {
          ctx.fillStyle="#ffffff";
          draw = true;
        } else if (gameBoard[i][j] === blackColor) {
          ctx.fillStyle="#000000";
          draw = true;
        }
        
        if(draw === true) {
          ctx.beginPath();
          ctx.arc((i+1) * fieldSize, (j+1) * fieldSize, pawnSize, 0, 2 * Math.PI);
          ctx.fill();
          ctx.closePath();
        }
      }
    }

    for(var i = 0; i < boardSize; i++) {
      for(var j = 0; j < boardSize; j++) {
        var draw = false;
        if(territoriesBoard[i][j] === whiteColor) {
          ctx.fillStyle="#ffffff";
          draw = true;
        } else if (territoriesBoard[i][j] === blackColor) {
          ctx.fillStyle="#000000";
          draw = true;
        }
        
        if(draw === true) {
          ctx.fillRect((i + 1) * fieldSize - territorySquareSize/2, (j + 1) * fieldSize - territorySquareSize/2, territorySquareSize, territorySquareSize);
        }
      }
    }


  }

  //the function called when a stone is about to be placed on the board
  function makeMove(x, y, color) {


    gameSocket.send(JSON.stringify({x : x, y : y}));
  }


  function placePawn(x, y, color) {
    console.log("Placing piece on coordinates: " + x + ", " + y);

    gameBoard[x][y] = color;    
    drawBoard();
  }

  function fillBoard( board) {
  
  	console.log("filling board");
  	
  	for(var i = 0; i < boardSize; i++) {
  		for(var j = 0; j < boardSize; j++) {
  			gameBoard[i][j] = board[i * boardSize + j];
  	   }
    	}
    	drawBoard();				
  }

  function fillTerritories( board) {
  
    console.log("filling board");
    
    for(var i = 0; i < boardSize; i++) {
      for(var j = 0; j < boardSize; j++) {
        territoriesBoard[i][j] = board[i * boardSize + j];
       }
      }
      drawBoard();        
  }
  
});

