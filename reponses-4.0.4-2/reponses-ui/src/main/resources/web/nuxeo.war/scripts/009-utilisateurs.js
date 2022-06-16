function createTempUserFormSubmit() {
    userForm = $("#user_form");
    if (isValidForm(userForm)) {
        const ajaxUrl =
            $("#ajaxCallPath").val().substring(0, $("#ajaxCallPath").val().length) +
            "/utilisateurs/temporaire/creation";
        var myRequest = {
            contentId: null,
            dataToSend: userForm.serialize(),
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: null,
            loadingButton: userForm.find("#submit-create-temp-user"),
        };

        callAjaxRequest(myRequest, checkErrorOrGoUEspaceUtilisateur, displaySimpleErrorMessage);
    }
}

window.checkErrorOrGoUEspaceUtilisateur = function (contentId, result, caller, extraDatas, xhr) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else {
        $("#reload-loader").css("display", "block");
        window.location.replace(
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) + "/utilisateurs"
        );
    }
};
