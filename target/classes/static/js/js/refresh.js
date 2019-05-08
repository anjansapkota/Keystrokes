/**
 * 
 */
window.addEventListener("keydown", keydown, false);
window.addEventListener("keypress", kp, false);
window.addEventListener("keyup", keyup, false);
var KM;
var KN= 0;
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
	B = n - X;     //press_press    p1-p2
	C = n - Y;     //R1-P2
	X = n;	
	KM=KN;
	if(e.ctrlKey && e.keyCode==86){
		KN = 999999999;
	}
}

function kp(e) {
	if(!e.ctrlKey && e.keyCode!=86){
	KN = e.keyCode;
	console.log(KM)
	console.log(KN)
	}
}

function keyup(e) {
	var m = window.performance.now();
	A = m - X;      //press_release p1-r1
	D = m - Y;      // R1-R2
	Y = m;
	if(count>0){
		myObj = { "l1":KM, "l2":KN, "p1r1":Math.round(A*100), "p1p2":Math.round(B*100), 
				"r1p2":Math.round(C*100), "r1r2":Math.round(D*100)};
		list.push(myObj)
	}
	count++;
	if(count%10==9){
		list.shift()
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
						if(result == 2){
							swal("", {
								  title: "You dont seem like the real user! You exam shall be cancelled",
								  icon: "error",
								  buttons:false,
								  timer:2000
							});
							setTimeout(function () {
								SaveAction();    
					    		}, 3000);
						} else if(result == 1){
							swal("", {
								  title: "Good Job!! ",
								  icon: "success",
								  buttons:false,
								  timer:2000
							});
							setTimeout(function () {
								SaveAction();    
					    		}, 2200);
						}
						
					},
					error : function(e) {
						console.log("ERROR: ", e);
					}
				})
}

function saveresult(a) {
	 var data = { tempuser: a };
	    return $.ajax({
	        url: 'saveresult',
	        type: 'POST',
	        data:data
	    });
}

function SaveAction(){
	swal({
		  text: 'Please provide your matricula.',
		  content: "input",
		  button: {
		    text: "Submit!",
		    closeModal: false,
		  },
		})
		.then(matricula => {
		  if (matricula) {
			  saveresult(matricula).then(function (r) {
			      if(r == 1){
			  		swal("", {
			  			title: "Thank you!",
			  			icon: "success",
			  			buttons:false,
			  			timer:2000
			  		});
			  } else {
				  swal("", {
			  title: "An error occured!",
			  icon: "warning",
			  			  buttons:false,
			  			  timer:2000
			  			});
			      }			      
			  });
		  } else {
		    swal("¡Operation Cancelled");
		  }
		});
}
