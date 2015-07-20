function Shape() {};

Shape.prototype.drawPathFrom = function(points) {
    var path = new Path2D();
    path.moveTo(points[0], points[1]);
    for (var i = 2; i < points.length; i+=2) {
        path.lineTo(points[i], points[i+1]);
    };
    return path;
};
