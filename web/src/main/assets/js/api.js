var api = {
  socket: null,
  http: {}
};

api.ws = { url: '/api',
  contentType : "application/json",
  logLevel : 'debug',
  transport : 'websocket' ,
  reconnectInterval: 5000,
  fallbackTransport: null,
  enableProtocol: false,
  attachHeadersAsQueryString: false,
  dropHeaders: true
};

api.ws.onMessage = function (response) {
  var json = $.parseJSON(response.responseBody);
  api.onMessage(json);
};

api.onMessage = function(json) {
  $(api).trigger("api.message", json);
};

api.getConfig = function() {
  api.http.get("config");
};

api.getStats = function() {
  api.http.get("stats");
};

api.http.get = function(kind) {
  $.ajax({ method: "GET", dataType: "json", url: "/api/" + kind,
    success: api.onMessage });
};

api.bind = function(callback) {
  $(api).on("api.message",callback);
};

api.postConfig = function(id, host, viz) {
  var json = {"jsonClass": "Config"};
  json.id = id;
  json.host = host;
  if($.isArray(viz)) {
    json.viz = viz;
  } else {
    json.viz = [ viz ];
  }
  api.post(json, "config");
};

api.postStats = function(count, batch, mse, realStddev, predStddev) {
  var json = {"jsonClass": "Stats"};
  json.count = parseInt(count, 10);
  json.batch = parseInt(batch, 10);
  json.mse = parseInt(mse, 10);
  json.realStddev = parseInt(realStddev, 10);
  json.predStddev = parseInt(predStddev, 10);
  api.post(json, "stats");
};

api.post = function(json, kind) {
  var str = $.stringifyJSON(json);

  console.log("sending " + kind + " json: " + str);

  if (api.socket) {
    api.socket.push(str);
  } else {
    $.ajax({ method: "POST", dataType: "json", url: "/api", data: str,
      success: function() {
        api.http.get(kind);
      }
  });
  }
};

api.websocketOn = function() {
  if (!api.socket) {
    api.socket = $.atmosphere.subscribe(api.ws);
  }
};

api.websocketOff = function() {
  if (api.socket) {
    $.atmosphere.unsubscribe();
    api.socket = null;
  }
};

api.guid = function () {
  function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
      .toString(16)
      .substring(1);
  }
  return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
    s4() + '-' + s4() + s4() + s4();
};
