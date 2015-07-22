
function GameCanvas(canvas) {
  this.canvas = canvas;
  this.ctx = canvas.getContext("2d");
}

GameCanvas.prototype.initialize = function(background) {
  this.background = new Image();
  this.background.src = background;
  this.ctx.drawImage(this.background, 0, 0);
}

GameCanvas.prototype.setCanvasSize = function(size) {
  this.canvas.width = size[0];
  this.canvas.height = size[1];
};


GameCanvas.prototype.draw = function(gameData) {
  this.setCanvasSize(gameData.size);

  this.ctx.drawImage(this.background, 0, 0, this.canvas.width, this.canvas.height);

  var ctx = this.ctx;
  gameData.items.forEach(function(item) {
    item.draw(ctx);
  });
}


function GameData(data) {
  this.items = [];
  this.size = [500, 500];
  
  if (data) {
    this.parseGameData(data);
  }
}

GameData.prototype.parseGameData = function(data) {
  this.size = data.gamefield.size;
  var that = this;
  data.gamefield.territory.forEach(function (i) { that.addItem(i); });
  data.gamefield.obstacles.forEach(function (i) { that.addItem(i); });
  data.units.forEach(function (i) { that.addItem(i, "self"); });
  data.enemyUnits.forEach(function (i) { that.addItem(i, "enemy"); });
}

GameData.prototype.addItem = function(item, status) {
  this.items.push(this._parseItem(item, status));
}

GameData.prototype._parseItem = function(item, status) {
  switch(item.type) {
    case "Deposit": return new Deposit(item);
    case "Wall": return new Wall(item);
    case "Commander": return new Commander(item, status);
    case "Harvester": return new Harvester(item, status);
    case "Squaddy": return new Squaddy(item, status);
    case "Stronghold": return new Stronghold(item, status);
  };
}

