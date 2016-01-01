function save_options() {
    var driveDocumentId = document.getElementById('driveDocumentId').value;
    chrome.storage.sync.set({
        driveDocumentId: driveDocumentId
    }, function () {
        // Update status to let user know options were saved.
        var status = document.getElementById('status');
        status.textContent = 'Options saved.';
        setTimeout(function () {
            status.textContent = '';
        }, 2000);
    });
}

function restore_options() {
    // Use default value color = 'red' and likesColor = true.
    chrome.storage.sync.get({
        driveDocumentId: ''
    }, function (items) {
        document.getElementById('driveDocumentId').value = items.driveDocumentId;
    });
}

document.addEventListener('DOMContentLoaded', restore_options);
document.getElementById('save').addEventListener('click', save_options);