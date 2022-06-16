function chargerListeQuestionConnexeParMinistere() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/connexe";
    var myRequest = {
        contentId: "d_connexe_content",
        dataToSend: {
            dossierId: $("#dossierId").val(),
        },
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, replaceHtmlFunction, displaySimpleErrorMessage);
}

function chargerListeQuestionConnexe(el) {
    var idMinistere = $(el).closest("tr").data("id");
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/connexe/liste";
    var myRequest = {
        contentId: "d_connexe_content",
        dataToSend: {
            dossierId: $("#dossierId").val(),
            idMinistere: idMinistere,
        },
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, replaceHtmlFunction, displaySimpleErrorMessage);
}

function chargerListeQuestionConnexeMinistere() {
    var idMinistere = $("#idMinistere").val();
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/connexe/liste";
    var myRequest = {
        contentId: "d_connexe_content",
        dataToSend: {
            dossierId: $("#dossierId").val(),
            idMinistere: idMinistere,
        },
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, replaceHtmlFunction, displaySimpleErrorMessage);
}

function chargerQuestionConnexe(el) {
    var idDossier = $(el).closest("tr").data("id");
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/connexe/question";
    var myRequest = {
        contentId: "d_connexe_content",
        dataToSend: {
            dossierId: $("#dossierId").val(),
            selectedDossierId: idDossier,
            idMinistere: $("#idMinistere").val(),
        },
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, replaceHtmlFunction, displaySimpleErrorMessage);
}

function creerOuAjouterQuestionLot(alloti, btn) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/connexe/lot";
    var myTable = $("#listeQuestionsConnexes");
    var dossiers = [];
    $(myTable)
        .find(":checkbox:checked.js-custom-table-line-check")
        .each(function () {
            dossiers.push($(this).closest("tr").attr("data-id"));
        });

    var myRequest = {
        contentId: "d_connexe_content",
        dataToSend: {
            dossierId: $("#dossierId").val(),
            idDossiers: dossiers,
            alloti: alloti,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        loadingButton: btn,
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function copierReponse() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/connexe/copier";
    var myRequest = {
        contentId: "d_connexe_content",
        dataToSend: {
            dossierId: $("#dossierId").val(),
            selectedDossierId: $("#selectedDossierId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, checkErrorOrGoParapheur, displaySimpleErrorMessage);
}

window.checkErrorOrGoParapheur = function (contentId, result, caller, extraDatas, xhr) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else {
        var targetUrl = "/dossier/" + $("#dossierId").val() + "/parapheur?" + $("#dossierLinkId").serialize();
        $("#reload-loader").css("display", "block");
        window.location.replace(
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) + targetUrl
        );
    }
};
