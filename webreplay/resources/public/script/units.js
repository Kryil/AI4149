function Unit(data, status) {
    this.location = data.location;
    this.health = data.health;
    this.status = status;
};

Unit.prototype = Object.create(Shape.prototype);

Unit.prototype.draw = function(ctx) {
    ctx.fillStyle = this.status === "enemy" ? "#AA0000" : "#99FF99";
    ctx.fillRect(this.location[0], this.location[1], 50, 50);
};

function Commander(data, status) {
    Unit.call(this, data, status);
};

Commander.prototype = Object.create(Unit.prototype);

Commander.prototype.constructor = Commander;

Commander.prototype.draw = function(ctx) {
    var x = this.location[0];
    var y = this.location[1];
    ctx.fillStyle = "#757575";
    ctx.fill(this.drawPathFrom([
        30, 0, 80, 0, 80, 30, 90, 30, 90, 20, 100, 20, 100, 30, 110, 30, 110,
        70, 100, 70, 100, 80, 110, 80, 110, 100, 100, 100, 100, 90, 90, 90, 90,
        100, 80, 100, 80, 80, 90, 80, 90, 70, 70, 70, 70, 110, 100, 110, 100,
        130, 60, 130, 60, 100, 50, 100, 50, 130, 10, 130, 10, 110, 40, 110, 40,
        70, 20, 70, 20, 80, 30, 80, 30, 100, 20, 100, 20, 90, 10, 90, 10, 100,
        0, 100, 0, 80, 10, 80, 10, 70, 0, 70, 0, 30, 10, 30, 10, 20, 20, 20, 20,
        30, 30, 30
    ].map(this.shiftXY(x, y))));
    ctx.fillStyle = this.status === "enemy" ? "#AA0000" : "#99FF99";
    ctx.fillRect(x+40, y+10, 30, 10);
    ctx.fill(this.drawPathFrom([
        20, 40, 50, 40, 50, 30, 60, 30, 60, 40, 90, 40, 90, 50, 60, 50, 60, 90,
        50, 90, 50, 50, 20, 50
    ].map(this.shiftXY(x, y))));
    ctx.fillRect(x+10, y+115, 40, 10);
    ctx.fillRect(x+60, y+115, 40, 10);
};

function Harvester(data, status) {
    Unit.call(this, data, status);
};

Harvester.prototype = Object.create(Unit.prototype);

Harvester.prototype.constructor = Harvester;

function Squaddy(data, status) {
    Unit.call(this, data, status);
};

Squaddy.prototype = Object.create(Unit.prototype);

Squaddy.prototype.constructor = Squaddy;

function Stronghold(data, status) {
    Unit.call(this, data, status);
};

Stronghold.prototype = Object.create(Unit.prototype);

Stronghold.prototype.constructor = Stronghold;

Stronghold.prototype.draw = function(ctx) {
    ctx.fillStyle = this.status === "enemy" ? "#AA0000" : "#99FF99";
    ctx.fillRect(this.location[0], this.location[1], 300, 300);
    ctx.strokeStyle = "black";
    ctx.lineWidth = 20;
    ctx.strokeRect(this.location[0]+25, this.location[1]+25, 250, 250);
    ctx.font = "bold 200px Arial";
    ctx.fillStyle = "black";
    ctx.fillText("B", this.location[0]+65, this.location[1]+220);
};
