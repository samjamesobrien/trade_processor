var dwws = dwws || {};

(function (dwws, $) {
   "use strict";

   var $status = $("#status");
   var socket = null;

   function connectSocket() {
      if(socket == null) {
         socket = new WebSocket("ws://trade-processor.herokuapp.com/api/ws");

         socket.onopen = function() {
            console.log("Connected!");
            $status.prepend("<p>Connected websocket!</p>");
         };

         socket.onclose = function() {
            console.log("Closed!");
            $status.prepend("<p>Closed websocket</p>");
         };

         socket.onmessage = function(msg) {
            console.log("Got message", msg, this);
            $status.prepend("<p>" + msg.data + "</p>");
         };
      }
   }

   $("#broadcastBtn").click(function() {
      var msg = $("#msg").val();
      $.ajax({
         type: "POST",
         url: "api/trends",
         data: msg,
         contentType: "text/plain"
      });
   });

   $("#sendBtn").click(function() {
      if(socket != null) {
         socket.send($("#msg").val());
      }
   });

   $("#connectBtn").click(connectSocket);

   $("#disconnectBtn").click(function() {
      if(socket != null) {
         socket.close();
         socket = null;
      }
   });
}(dwws, jQuery));