var getAuthToken = function () {
    chrome.identity.getAuthToken({'interactive': true}, function (token) {
        chrome.storage.sync.set({
            authToken: token
        }, function () {
            console.log("new authToken " + token);
        });
    });
};
setInterval(getAuthToken, 600000);
getAuthToken();