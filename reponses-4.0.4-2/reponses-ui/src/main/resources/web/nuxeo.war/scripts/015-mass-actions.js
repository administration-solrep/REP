function doMassDonnerAvisFavorable() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/actionMasse/favorable";
    var myRequest = getMassActionRequest(ajaxUrl, "#MASS_DONNER_AVIS_FAVORABLE_DOSSIER");

    callAjaxRequest(myRequest, checkErrorOrHardReload, displaySimpleErrorMessage);
}

function doMassDonnerAvisDefavorableRetour() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/actionMasse/defavorableRetour";
    var myRequest = getMassActionRequest(ajaxUrl, "#MASS_REJETER_RETOUR_DOSSIER");

    callAjaxRequest(myRequest, checkErrorOrHardReload, displaySimpleErrorMessage);
}

function doMassDonnerAvisDefavorablePoursuivre() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/actionMasse/defavorablePoursuivre";
    var myRequest = getMassActionRequest(ajaxUrl, "#MASS_REJETER_DOSSIER");

    callAjaxRequest(myRequest, checkErrorOrHardReload, displaySimpleErrorMessage);
}

function doMassExportZip() {
    constructAlertContainer([constructInfoAlert("Export Zip demandé")]);

    var ajaxUrl = $("#ajaxCallPath").val() + "/actionMasse/export/zip";
    var myRequest = {
        contentId: null,
        dataToSend: {
            id: getSelectedContent(),
        },
        method: "POST",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
        xhrFields: {
            responseType: "blob", // to avoid binary data being mangled on charset conversion
        },
    };
    callAjaxRequest(myRequest, downloadFile, displaySimpleErrorMessage);
}

function doEnvoyerListeDossierMail(event) {
    var form = $("#formDossierMail");
    if (isValidForm(form)) {
        var ajaxUrl = $("#ajaxCallPath").val() + "/actionMasse/sendMail";
        var options = $("#model-mail-destinataire option");
        var destinataires = [];
        $.map(options, function (option) {
            destinataires.push(option.text);
        });
        var destinataireIds = [];
        $.map(options, function (option) {
            destinataireIds.push(option.value);
        });

        var myRequest = {
            contentId: null,
            dataToSend: {
                destinataires: destinataires,
                copie: $('input[name="copie"]:checked').val(),
                destinataireIds: destinataireIds,
                autres: $("#autres").val(),
                objet: $("#objet").val(),
                message: encodeHTMLSpecialChar(tinymce.get("mce_mail").getContent()),
                idDossiers: getSelectedContent(),
            },
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: "reload-loader",
        };
        callAjaxRequest(myRequest, showMessage, displaySimpleErrorMessage);
    }
    closeModal($("#modal-dossier-send-mail").get(0));
}

function doMassReattribution() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/actionMasse/reattribution";

    var myRequest = {
        contentId: null,
        dataToSend: {
            idDossiers: getSelectedContent(),
            ministereId: $("#ministere-rattachement-reattribution-key").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doMassReattributionDirect() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/actionMasse/reattributionDirect";

    var myRequest = {
        contentId: null,
        dataToSend: {
            idDossiers: getSelectedContent(),
            observations: $("#observations").val(),
            ministereId: $("#ministere-key").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doMassReorientation() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/actionMasse/reorientation";
    var myRequest = getMassActionRequest(ajaxUrl, "#MASS_REORIENTATION");

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doMassReaffectation() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/actionMasse/reaffectation";

    var myRequest = {
        contentId: null,
        dataToSend: {
            idDossiers: getSelectedContent(),
            ministereId: $("#ministere-rattachement-reattribution-key").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doMassArbitrage() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/actionMasse/arbitrage";
    var myRequest = getMassActionRequest(ajaxUrl);

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doMassChangeMinistereRattachement() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/actionMasse/changeMinRattachement";

    var myRequest = {
        contentId: null,
        dataToSend: {
            idDossiers: getSelectedContent(),
            ministereId: $("#ministere-rattachement-reattribution-key").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doMassChangeDirectionPilote() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/actionMasse/changeDirPilote";

    var myRequest = {
        contentId: null,
        dataToSend: {
            idDossiers: getSelectedContent(),
            directionId: $("#mqm-direction-key").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function getMassActionRequest(ajaxUrl, loadingButton) {
    var myRequest = {
        contentId: null,
        dataToSend: {
            idDossiers: getSelectedContent(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };
    return myRequest;
}

function getSelectedContent() {
    var myTable = $("[id^=table_mass_action_toolbar]").closest(".tableForm");
    var dossiers = [];
    $(myTable)
        .find(":checkbox:checked.js-custom-table-line-check")
        .each(function () {
            dossiers.push($(this).closest("tr").attr("data-id"));
        });
    return dossiers;
}
