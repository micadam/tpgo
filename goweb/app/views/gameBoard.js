

var c = document.getElementById("myCanvas");
var ctx = c.getContext("2d");
cLeft = c.offsetLeft;
cTop = c.offsetTop;
var boardSize = 19;
var fieldSize = 50;
var pawnSize = fieldSize/2;
var whiteColor = 1;
var blackColor = -1;

var gameBoard = new Array(boardSize);

for(var i = 0; i < boardSize; i++) {
	gameBoard[i] = new Array(boardSize);
	for(var j = 0; j < boardSize; j++) {
		gameBoard[i][j] = 0;
  }
}

c.width = (boardSize + 1) * fieldSize;
c.height = (boardSize + 1) * fieldSize;

drawBoard();

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
  
  if(boardSize == 19) {     
    for(var i=4;i<boardSize;i+=6){    //4 bo jeszcze to puste
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
}

function makeMove(x, y, color) {
  console.log("Placing piece on coordinates: " + x + ", " + y);
  gameBoard[x][y] = color;
  drawBoard();
}

c.addEventListener('click', function(event) {
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
  if(Math.sqrt((x - closestX) * (x - closestX) + (y - closestY) * (y - closestY)) < pawnSize / 2) {  
      closestX = closestX / fieldSize - 1;
      closestY = closestY / fieldSize - 1;
      makeMove(closestX, closestY, 1);
  }
 });
