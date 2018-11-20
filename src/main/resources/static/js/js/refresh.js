/**
 * 
 */
window.addEventListener("keydown", keydown, false);
window.addEventListener("keypress", keypress, false);
window.addEventListener("keyup", keyup, false);
var X;
var Y;
var A;
var B;
var C;
var D;
function keydown(e) {
	var n = window.performance.now();
	B = n - X;     //press_press    p2-p1
	C = n - Y;     //P2-R1
	X = n;
}

function keypress(e) {
	console.log(window.performance.now());
}

function keyup(e) {
	var m = window.performance.now();
	A = m - X;      //press_release r1-p1
	D = m - Y;      // R2-R1
	Y = m;
	console.log(A + "   " + B + "   " + C +  "   " + D + "   " )
}