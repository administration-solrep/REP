function goToCritereSuppressionEtape() {
    var callPath = $("#ajaxCallPath")
        .val()
        .substring(0, $("#ajaxCallPath").val().length - 5);
    var myTable = $("[id^=SUPPRESSION_ETAPE_MASSE]").closest(".tableForm");
    var data = [];
    $(myTable)
        .find(":checkbox:checked.js-custom-table-line-check")
        .each(function () {
            data.push("idModeles=" + $(this).closest("tr").attr("data-id"));
        });

    url = callPath + "/admin/fdr/modeles/supprimerEtape?" + data.join("&");

    window.location.href = url + "#main_content";
}

const replaceHtmlFunctionAndActiveDeleteButton = function replaceHtmlFunctionAndActiveDeleteButton(
    containerID,
    result
) {
    replaceHtmlFunction(containerID, result);
    if ($("#empty_delete_step").length == 0) {
        $("#btn-confirm-deletion-doc").prop("disabled", false);
    } else {
        $("#btn-confirm-deletion-doc").prop("disabled", true);
    }
};

function openModalSuppressionMasseModeleFdr(idModeles) {
    // L'ajout de l'attribut "data-controls" permet de ne pas avoir l'erreur "tmpDialog is null" lors de la fermeture de la modal
    $('[id="SUPPRESSION_ETAPE_MASSE_VALIDER_CRITERE"]').attr("data-controls", "modal-suppression-masse-modele-fdr");
    var data = [];
    $("[id^=modele-]").each(function () {
        data.push($(this).val());
    });

    var ajaxUrl = $("#ajaxCallPath").val() + "/fdr/modeles/showContentModalSupprimerEtape";
    var myRequest = {
        contentId: "stepToRemoveContent",
        dataToSend: {
            idTypeEtape: $("#typeEtape").val(),
            idPoste: $("#poste-key").val(),
            idModeles: data,
        },
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, replaceHtmlFunctionAndActiveDeleteButton, displaySimpleErrorMessage);
}

function doSuppressionEtape() {
    var data = [];
    $("[id^=modele-]").each(function () {
        data.push($(this).val());
    });

    var ajaxUrl = $("#ajaxCallPath").val() + "/fdr/modeles/supprimerEtapeMasse";
    var myRequest = {
        contentId: null,
        dataToSend: {
            idTypeEtape: $("#typeEtape").val(),
            idPoste: $("#poste-key").val(),
            idModeles: data,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, returnToCritereSuppressionEtape, displaySimpleErrorMessage);
}

// une fois qu'on clique sur le bouton Supprimer (de la modale)
function returnToCritereSuppressionEtape() {
    var callPath = $("#ajaxCallPath")
        .val()
        .substring(0, $("#ajaxCallPath").val().length - 5);
    var data = [];
    $("[id^=modele-]").each(function () {
        data.push("idModeles=" + $(this).val());
    });

    url = callPath + "/admin/fdr/modeles/supprimerEtape?" + data.join("&");

    window.location.href = url;
}

function doSaveFirstEtapeModele() {
    var lines = getLinesToAdd();

    var ajaxUrl = $("#ajaxCallPath").val() + "/etape/saveFirstEtape";
    var myRequest = {
        contentId: null,
        dataToSend: {
            typeAjout: $("#actionLabel").val(),
            typeCreation: $("input[name='type-creation']:checked").val(),
            idBranch: $("#idModele").val(),
            lines: lines,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}
