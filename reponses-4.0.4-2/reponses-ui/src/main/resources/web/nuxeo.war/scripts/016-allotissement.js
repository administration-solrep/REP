function addToLot() {
    const dossierId = $("#dossierId").val();
    const searchedQuestion = $("#form_input-add-lot").val();

    const ajaxUrl = $("#ajaxCallPath").val() + "/allotissement/ajouter";
    const myRequest = {
        contentId: "",
        dataToSend: {
            searchedQuestion: searchedQuestion,
            dossierId: dossierId,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function removeFromLot() {
    const dossierId = $("#dossierId").val();
    const myTable = $("#lotTable");
    const selectedFolders = [];
    $(myTable)
        .find(":checkbox:checked.js-custom-table-line-check")
        .each(function () {
            selectedFolders.push($(this).closest("tr").attr("data-id"));
        });

    if (selectedFolders.length === 0) return;

    const ajaxUrl = $("#ajaxCallPath").val() + "/allotissement/retirer";
    const myRequest = {
        contentId: "",
        dataToSend: {
            dossierId: dossierId,
            selectedFolders: selectedFolders,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function goToCreateLotPage() {
    const callPath = $("#ajaxCallPath")
        .val()
        .substring(0, $("#ajaxCallPath").val().length - 5);
    var url = callPath + "/allotissement?";
    const myTable = $(".tableForm").first();
    var data = [];
    $(myTable)
        .find(":checkbox:checked.js-custom-table-line-check")
        .each(function () {
            data.push("idDossiers=" + $(this).closest("tr").attr("data-id"));
        });
    url = url + data.join("&") + "#main_content";
    window.location.href = url;
}

function doCreateLot() {
    const form = $("#dossier-directeur-input");
    if (!isValidForm(form)) return;

    const dossierDirecteurId = $("#form_input-create-lot").val();
    const myTable = $("#table-allotissement-possible");
    let selectedFolders = [];

    $(myTable)
        .find("> tbody > tr")
        .each(function () {
            selectedFolders.push($(this).attr("data-id"));
        });

    const ajaxUrl = $("#ajaxCallPath").val() + "/allotissement/creer";
    const myRequest = {
        contentId: "",
        dataToSend: {
            dossierDirecteurId: dossierDirecteurId,
            selectedFolders: selectedFolders,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, createLotSuccess, displaySimpleErrorMessage);
}

function createLotSuccess(containerID, result) {
    checkMessages(result, goPreviousPage);
}
