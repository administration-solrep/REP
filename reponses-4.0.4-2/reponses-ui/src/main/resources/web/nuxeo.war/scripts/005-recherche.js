function cleanSearchForm() {
    $("#searchForm").find("input[type=text], input[type=hidden], textarea").val("");
    $("#searchForm").find("input[type=radio], input[type=checkbox]").prop("checked", false);
    $("#rechercheSur-TOUS").prop("checked", true);
    $("#searchForm").find("select").prop("selectedIndex", 0);
    $("#indexMinistere-remove-all-button,#indexAn-remove-all-button,#indexSenat-remove-all-button").each(function () {
        $(this).click();
    });
}

function launchRapidSearch(event) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/recherche/fdrrapide";
    var myRequest = {
        contentId: "listeModeles",
        dataToSend: $("#searchIntitule").serialize(),
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: true,
        overlay: "result_overlay",
        caller: this,
        loadingButton: $(event.target),
        url: $("#listeModeles").data("url"),
    };
    event.preventDefault();
    callAjaxRequest(myRequest, replaceHtmlFunction, displaySimpleErrorMessage);
}

function reinitRapidSearch(event) {
    $("#searchIntitule").val("");
    launchRapidSearch(event);
}

function doDemanderElimination() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/recherche/demanderElimination";
    var myTable = $("[id^=DEMANDE_ELIMINATION]").closest(".tableForm");
    var data = [];
    $(myTable)
        .find(":checkbox:checked.js-custom-table-line-check")
        .each(function () {
            data.push($(this).closest("tr").attr("data-id"));
        });

    var myRequest = {
        contentId: null,
        dataToSend: {
            idDossiers: data,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        loadingButton: $("[id=DEMANDE_ELIMINATION]"),
    };

    callAjaxRequest(myRequest, checkErrorAboveResultsOrToast, displaySimpleErrorMessage);
}

var checkErrorAboveResultsOrToast = function (contentId, result) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    // Supprimer les alertes affichées au dessus de la liste des résultats
    $("#listeDossiers .alert").remove();
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
        $("#listeDossiers .alert--danger .alerts--flex").attr("role", "alert");
    } else if (messagesContaineur.successMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.successMessageQueue);
    }
};

function exportExcelSearchResult() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/recherche/exporterResultats";
    var myRequest = {
        contentId: null,
        dataToSend: null,
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        loadingButton: $("#RECHERCHE_DOSSIER_EXPORT"),
    };
    callAjaxRequest(myRequest, checkErrorOrToast, displaySimpleErrorMessage);
}

function launchRechercheRapide(event) {
    doLaunchRechercheRapide(event, "question");
}
