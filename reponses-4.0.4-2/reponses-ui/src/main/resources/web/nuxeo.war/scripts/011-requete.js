function saveRequeteAs() {
    if ($("#requeteTable").find(".custom-table__table-line").length > 0) {
        window.location.href =
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) + "/suivi/perso/creation#main_content";
    } else {
        constructAlert(errorMessageType, ["Veuillez paramétrer une requête pour pouvoir la sauvegarder"], null);
    }
}

function saveRequetePerso() {
    event.preventDefault();
    var $form = $("#requete-perso-form");
    if (isValidForm($form)) {
        var ajaxUrl = $("#ajaxCallPath").val() + "/suivi/sauvegardePerso";
        var myRequest = {
            contentId: "resultList",
            dataToSend: $form.serialize(),
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: null,
            caller: this,
            loadingButton: $("#btn-save-requetePerso"),
        };
        callAjaxRequest(myRequest, checkErrorOrGoToRequete, displaySimpleErrorMessage);
    }
}

var checkErrorOrGoToRequete = function (contentId, result) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else {
        window.location.href =
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) +
            "/suivi/requete/?idRequete=" +
            jsonResponse.data +
            "#main_content";
    }
};

function renameRequete(input) {
    var id = $(input).parents(".quick-access__item").find("input[name=id]").val();
    window.location.href =
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) +
        "/suivi/perso/renommer?id=" +
        id +
        "#main_content";
}

function deleteRequete() {
    var id = $("#idRequete").val();
    var ajaxUrl = $("#ajaxCallPath").val() + "/suivi/supprimer";
    var myRequest = {
        contentId: null,
        dataToSend: "id=" + id,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        caller: this,
    };
    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function editRequete(input) {
    var id = $(input).parents(".quick-access__item").find("input[name=id]").val();
    loadRequete(id);
}

var checkErrorOrGoToRequeteHome = function (contentId, result) {
    var jsonResponse = JSON.parse(result);
    var redirectUrl =
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) +
        "/suivi/" +
        jsonResponse.data;
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else if (window.location.pathname == redirectUrl) {
        reloadPage();
    } else {
        $("#reload-loader").css("display", "block");
        window.location.href = redirectUrl + "#main_content";
    }
};

function editRequeteFromList(input) {
    var id = $(input).data("id");
    loadRequete(id);
}

function loadRequete(id) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/suivi/modifier";
    var myRequest = {
        contentId: null,
        dataToSend: "id=" + id,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        caller: this,
    };
    callAjaxRequest(myRequest, checkErrorOrGoToRequeteHome, displaySimpleErrorMessage);
}

function saveRequete(input) {
    var id = $(input).data("id");
    var ajaxUrl = $("#ajaxCallPath").val() + "/suivi/editerPerso";
    var myRequest = {
        contentId: "resultList",
        dataToSend: { idRequete: id },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        caller: this,
        loadingButton: $("#save-requete-button"),
    };
    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}
