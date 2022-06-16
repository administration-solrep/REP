function doActionListeElimination(endpoint, goPrevious) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/eliminationDonnees/" + endpoint;
    var myRequest = buildRequest(ajaxUrl, $("#idParam").val());

    callAjaxRequest(myRequest, goPrevious ? checkErrorOrGoPrevious : checkErrorOrReload, displaySimpleErrorMessage);
}

function doAbandonnerListeElimination(goPrevious) {
    doActionListeElimination("abandonner", goPrevious);
}

function doEliminerListeElimination(goPrevious) {
    doActionListeElimination("eliminer", goPrevious);
}

function doViderListeElimination() {
    doActionListeElimination("vider");
}

function doEditerBordereauListeElimination() {
    var myRequest = {
        contentId: null,
        dataToSend: {
            id: $("#idParam").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/eliminationDonnees/editerBordereau",
        isChangeURL: false,
        overlay: null,
        async: false,
    };
    callAjaxRequest(myRequest, checkErrorOrReloadAndPdfNewTab, displaySimpleErrorMessage);
}

function doRetirerDeListeElimination() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/eliminationDonnees/retirer";
    var myTable = $(".tableForm").first();
    var data = [];
    $(myTable)
        .find(":checkbox:checked.js-custom-table-line-check")
        .each(function () {
            data.push($(this).closest("tr").attr("data-id"));
        });

    var myRequest = buildRequest(ajaxUrl, data);
    callAjaxRequest(myRequest, checkErrorOrReloadListe, displaySimpleErrorMessage);
}

window.checkErrorOrReloadListe = function (contentId, result, caller, extraDatas, xhr) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else {
        window.location.search = $("#id").serialize();
    }
};

window.checkErrorOrReloadAndPdfNewTab = function (contentId, result, caller, extraDatas, xhr) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else {
        var urlPdf =
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) + jsonResponse.data;
        window.open(urlPdf, "_blank");
        reloadPage();
    }
};
