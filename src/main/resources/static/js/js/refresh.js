/**
 * 
 */
window.addEventListener("keydown", keydown, false);
window.addEventListener("keypress", kp, false);
window.addEventListener("keyup", keyup, false);
var KM;
var KN;
var X;
var Y;
var A;
var B;
var C;
var D;
var count = 0;
var list = new Array();
function keydown(e) {
	var n = window.performance.now();
	B = n - X;     //press_press    p2-p1
	C = n - Y;     //P2-R1
	X = n;
	
	KM=KN;
	if(e.ctrlKey){
		KN = "ctrl";
	}else if(e.altKey){
		KN = "alt";
	}else if(e.shiftKey){
		KN = "shift";
	}else if(e.keyCode==8){
		KN = "backspace";
	}
	else if(e.keyCode==46){
		KN = "del";
	}
	else if(e.keyCode==32){
		KN = "space";
	}
	else if(e.keyCode==20){
		KN = "capslock";
	}
	else if(e.keyCode==37){
		KN = "leftarrow";
	}
	else if(e.keyCode==39){
		KN = "rightarrow";
	}
	else if(e.keyCode==38){
		KN = "uparrow";
	}
	else if(e.keyCode==40){
		KN = "downarrow";
	}
	else if(e.keyCode==13){
		KN = "enter";
	}
}

function kp(e) {
	if(!e.ctrlKey && !e.altKey && !e.shiftKey && !e.backspaceKey && e.keyCode!=46 && e.keyCode!=32 && e.keyCode!=20 && e.keyCode!=37 && e.keyCode!=38 && e.keyCode!=39 && e.keyCode!=40 && e.keyCode!=13){
		KN = String.fromCharCode(e.which);
	}
}

function keyup(e) {
	var m = window.performance.now();
	A = m - X;      //press_release r1-p1
	D = m - Y;      // R1-R2
	Y = m;
	if(count>0){
		myObj = { "l1":KM, "l2":KN, "p1r1":Math.round(A*100), "p1p2":Math.round(B*100), "r1p2":Math.round(C*100), "r1r2":Math.round(D*100)};
//		
//		var nameOfList = new Array();
//		nameOfList.push(KM);
//		nameOfList.push(KN);
//		nameOfList.push(A);
//		nameOfList.push(B);
//		nameOfList.push(C);
//		nameOfList.push(D);
		list.push(myObj)
	}
	count++;
	if(count%10==9){
		ajaxFire(list);
		for (var i = 0; i < list .length; i ++ ){
	        var lastElem = new Array();
	        lastElem=list.pop();
	        list.length = 0;
	        list.push(lastElem);
	     }
	}
	
}

function ajaxFire(list) {
				// DO POST
				$.ajax({
					type : "POST",
					contentType : "application/json",
					url : "refresh",
					data : JSON.stringify(list),
					dataType : 'json',
					success : function(result) {
					},
					error : function(e) {
						console.log("ERROR: ", e);
					}
				})
}