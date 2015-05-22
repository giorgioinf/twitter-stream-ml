var test = {
  count: 0
};

test.onWebsocket = function() {
  if ($(this).is(":checked")) {
    api.websocketOn();
  } else{
    api.websocketOff();
  }
};

test.postConfig = function(event) {
  api.postConfig(
    $("form#config input#id").val(),
    $("form#config input#host").val(),
    $("form#config input#viz").val()
  );
  event.preventDefault();
};

test.postStats = function(event) {
  api.postStats(
    $("form#stats input#count").val()
  );
  event.preventDefault();
};

test.onMessage = function(event, json) {
  var str = $.stringifyJSON(json);

  console.log('receiving json: ' + str);

  $("table > tbody:last").prepend(
    "<tr>"+
    "<td>"+(++test.count)+
    "<td>"+(new Date())+
    "<td>"+str+
    "</tr>"
  );
};

test.init = function() {
  api.bind(test.onMessage);
  $("#websocket").click(test.onWebsocket);
  $("form#config").submit(test.postConfig);
  $("form#stats").submit(test.postStats);
};

/**
 * Bind document ready event
 */
$( document ).ready( test.init );
