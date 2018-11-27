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
		console.log(e.keyCode);
	}else if(e.altKey){
		KN = "alt";
		console.log(e.keyCode);
	}else if(e.shiftKey){
		KN = "shift";
		console.log(e.keyCode);
	}else if(e.keyCode==8){
		KN = "backspace";
		console.log(e.keyCode);
	}
	else if(e.keyCode==46){
		KN = "del";
		console.log(e.keyCode);
	}
	else if(e.keyCode==32){
		KN = "space";
		console.log(e.keyCode);
	}
	else if(e.keyCode==20){
		KN = "capslock";
		console.log(e.keyCode);
	}
	else if(e.keyCode==37){
		KN = "leftarrow";
		console.log(e.keyCode);
	}
	else if(e.keyCode==39){
		KN = "rightarrow";
		console.log(e.keyCode);
	}
	else if(e.keyCode==38){
		KN = "uparrow";
		console.log(e.keyCode);
	}
	else if(e.keyCode==40){
		KN = "downarrow";
		console.log(e.keyCode);
	}
	else if(e.keyCode==13){
		KN = "enter";
		console.log(e.keyCode);
	}
	
	console.log(e.keyCode);
}

function kp(e) {
	if(!e.ctrlKey && !e.altKey && !e.shiftKey && !e.backspaceKey && e.keyCode!=46 && e.keyCode!=32 && e.keyCode!=20 && e.keyCode!=37 && e.keyCode!=38 && e.keyCode!=39 && e.keyCode!=40 && e.keyCode!=13){
		KN = String.fromCharCode(e.which);
	}
}

function keyup(e) {
	var m = window.performance.now();
	A = m - X;      //press_release r1-p1
	D = m - Y;      // R2-R1
	Y = m;
	if(count>0){
		var nameOfList = new Array();
		nameOfList.push(KM);
		nameOfList.push(KN);
		nameOfList.push(A);
		nameOfList.push(B);
		nameOfList.push(C);
		nameOfList.push(D);
		list.push(nameOfList)
	}
	count++;
	if(count%10==9){
		for (var i = 0; i < list .length; i ++ ){
	        window.console.log(list[i]);
	     }
		strike(list)
	}
	
}



function strike(list) {
	  var xhttp = new XMLHttpRequest();
	  xhttp.onreadystatechange = function() {
	    if (this.readyState == 4 && this.status == 200) {
	      if (this.responseText==1){
	    	  
	      }
	    }
	  };
	  xhttp.open("GET", "/refresh" + list , true);
	  xhttp.send();
	}












$(document).ready(function () {

    function fire(list){
    	fire_ajax_submit(list);
    }
});

function fire_ajax_submit(info) {
    $.ajax({
        type: "POST",
        contentType: {info:info},
        url: "/refresh",
        cache: false,
        timeout: 600000,
        success: function (data) {
        	if(data==0){
        	}else{
        		swal("Your exam will be blocked within a minute!!!!!", {
      			  icon: "warning",
      			  buttons:false,
      			  timer:2000,
      		});
        	}
          },
        error: function (e) {
            console.log("ERROR : ", e);
        }
    });

}