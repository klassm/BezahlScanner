var authorize = function () {
        chrome.identity.getAuthToken({'interactive': true}, function (token) {
            chrome.storage.sync.set({
                authToken: token
            }, function () {
                // Update status to let user know options were saved.
                var status = document.getElementById('status');
                status.textContent = 'Token saved';
                setTimeout(function () {
                    status.textContent = '';
                }, 2000);

            });
        });
    },
    restore = function () {
        var status = document.getElementById('authorize');
        chrome.storage.sync.get("authToken", function (items) {
            if (items.authToken) {
                var status = document.getElementById('status');
                status.textContent = "Authorized for Google drive";
                document.getElementById("authorize").style.display = "none";
            } else {
                document.getElementById("authorize").style.display = "visible";
                status.textContent = "";
            }
        });
    };


document.addEventListener('DOMContentLoaded', restore);
document.getElementById('authorize').addEventListener('click', authorize);


