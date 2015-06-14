$(document).ready(function() {
	var socket = new WebSocket('ws://localhost:8080/api/ws');
	var container = $('#trades');

	socket.onmessage = function(data) {
		console.log("got a message: " + data.data.toSource);
		var newItem = $('<div>' + data.data + '</div>');
		container.append(newItem);
	};
});
