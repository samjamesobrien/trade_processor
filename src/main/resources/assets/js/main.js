$(document).ready(function() {
	var socket = new WebSocket('ws://trade-processor.herokuapp.com/api/ws');
	var container = $('#trades');

	socket.onmessage = function(data) {
		container.innerHTML = "";

		console.log("got a message: " + data.data.toSource);
		var newItem = $('<div>' + data.data + '</div>');
		document.getElementById($('#trades')).innerHTML="";
		container.append(newItem);
	};
});
