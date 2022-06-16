function createDelegationFormSubmit() {
    var delegationForm = $("#delegation_form");
    if (isValidForm(delegationForm)) {
        const ajaxUrl = $("#ajaxCallPath").val() + "/delegation/creation";
        var myRequest = {
            contentId: null,
            dataToSend: delegationForm.serialize(),
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: null,
            loadingButton: delegationForm.find("button[type='submit']"),
        };

        callAjaxRequest(myRequest, checkErrorOrGoDelegationList, displayDelegationErrorMessage);
    }
}

window.checkErrorOrGoDelegationList = function (contentId, result, caller, extraDatas, xhr) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else {
        $("#reload-loader").css("display", "block");
        window.location.replace(
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) + "/admin/delegation/liste#main_content"
        );
    }
};

window.displayDelegationErrorMessage = function (contentId, result, caller, extraDatas, xhr) {
    constructAlert(errorMessageType, [result.responseText], null);
};

function modifyDelegationFormSubmit() {
    var delegationForm = $("#delegation_form");
    if (isValidForm(delegationForm)) {
        const ajaxUrl = $("#ajaxCallPath").val() + "/delegation/modification";
        var myRequest = {
            contentId: null,
            dataToSend: delegationForm.serialize(),
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: null,
            loadingButton: delegationForm.find("button[type='submit']"),
        };

        callAjaxRequest(myRequest, checkErrorOrGoPrevious, displayDelegationErrorMessage);
    }
}

function doDeleteDelegation() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/delegation/suppression";
    var myRequest = buildRequest(ajaxUrl, $("#idDelegation").val());

    callAjaxRequest(myRequest, checkErrorOrGoDelegationList, displayDelegationErrorMessage);
}
