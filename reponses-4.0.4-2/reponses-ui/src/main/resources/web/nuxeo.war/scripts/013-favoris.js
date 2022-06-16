var replaceFormHtmlFunction = function replaceHTMLinContainer(containerID, result) {
    $("#" + containerID).html(result);
    initFormValidation();
};

window.checkErrorOrGoPreviousOrReload = function (contentId, result, caller, extraDatas, xhr) {
    var urlPev = $("#urlPreviousPage").val();
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else if (urlPev) {
        goPreviousPage();
    } else {
        reloadPage();
    }
};
function onChangeFavorisType(type) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/favoris/type";
    var myRequest = {
        contentId: "favorisTravailForm",
        dataToSend: "type=" + type + "&idDossiers=" + $("#idDossiers").val(),
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, replaceFormHtmlFunction, displaySimpleErrorMessage);
}

function doAjoutFavoris() {
    var callPath = $("#ajaxCallPath")
        .val()
        .substring(0, $("#ajaxCallPath").val().length - 5);
    var myTable = $(".tableForm").first();
    var data = [];
    $(myTable)
        .find(":checkbox:checked.js-custom-table-line-check")
        .each(function () {
            data.push("idDossiers=" + $(this).closest("tr").attr("data-id"));
        });
    url = callPath + "/favoris/ajout?" + data.join("&") + "#main_content";
    window.location.href = url;
}

function doAddToMyFavorites() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/favoris/addToFavorites";
    var myRequest = {
        contentId: null,
        dataToSend: $("#selectFavorisForm").serialize(),
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: true,
        overlay: null,
        loadingButton: $("#btn-add-favarites"),
    };

    callAjaxRequest(myRequest, checkErrorOrGoPrevious, displaySimpleErrorMessage);
}

function doSupprimerFavoris() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/favoris/supprimer";
    var myRequest = buildRequest(ajaxUrl, $("#idFavori").val());

    callAjaxRequest(myRequest, checkErrorOrGoPreviousOrReload, displaySimpleErrorMessage);
}

function doRetraitFavoriTravail() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/favoris/removeToFavorites";
    var myTable = $(".tableForm").first();
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
            idFavori: $("#idFavori").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        loadingButton: $("#RETIRER_DU_FAVORI"),
    };
    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doSupprimerTousLesFavoris() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/favoris/vider";
    var myRequest = {
        contentId: null,
        dataToSend: null,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };
    callAjaxRequest(myRequest, checkErrorOrGoPreviousOrReload, displaySimpleErrorMessage);
}
