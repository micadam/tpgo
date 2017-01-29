@(tableName : String, bot : String)


$(function() {

  var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket;
  var gameSocket = new WS("@routes.Application.join(tableName, bot).webSocketURL(request)");
  var c = document.getElementById("myCanvas");
  var div = document.getElementById("board");
  var ctx = c.getContext("2d");
  var passButton = document.getElementById("passButton");
  var gameInfo = document.getElementById('gameInfo');
  var moveInfo = document.getElementById('moveInfo');

  gameInfo.innerHTML = "Waitng for an opponent...";
  moveInfo.innerHTML = "Your move!";
  
  c.width = div.clientWidth;
  c.height = div.clientHeight;

  var boardSize = 19;
  var fieldSize = c.height / (boardSize + 1);
  var pawnSize = fieldSize/2;

  var whiteColor = 1;
  var blackColor = -1;
  var whitePrisoners = 0;
  var blackPrisoners = 0;
  var gameBoard = new Array(boardSize);
  var territoriesBoard = new Array(boardSize);

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
    	if(data.type == "move" ){
		  	placePawn(data.x, data.y, data.color);
		  } else if ( data.type == "sync" ){
        if(data.territories === false) {
          fillBoard(data.board);
        } else {
          fillTerritories(data.board);
        }
		  } else if ( data.type == "end" ){
		  	 console.log("Game ended");
		  } else if ( data.type == "go") {
          moveInfo.innerHTML = "Your move!";
          alert("Your move!");
      } else if (data.type == "prisoners") {
          whitePrisoners = data.white;
          blackPrisoners = data.black;
      }
    }
  }


  gameSocket.onmessage = receiveEvent;
  
  //passButton handling
  passButton.addEventListener('click', function(event) {
  	makeMove(-1,-1,0);
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
          ctx.fillRect((i + 1) * fieldSize - fieldSize/6, (j + 1) * fieldSize - fieldSize/6, fieldSize/3, fieldSize/3);
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

