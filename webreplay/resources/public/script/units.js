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
