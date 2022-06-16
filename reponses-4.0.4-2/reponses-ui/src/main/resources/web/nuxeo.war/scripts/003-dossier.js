window.saveParapheur = function (data) {
    if ($("#format").val() == "html") {
        // tinymce
        var tinymceContent = tinymce.get("mce_dossier") ? tinymce.get("mce_dossier").getContent() : "";
        $("#mce_dossier").val(tinymceContent);
    }
    if (isHTML($("#mce_dossier").val())) {
        $("#mce_dossier").val(encodeHTMLSpecialChar($("#mce_dossier").val()));
    }
    data = removeAttributeInString(data, $("#mce_dossier").attr("name"));
    data += "&" + $("#mce_dossier").serialize();

    return data;
};

function saveDossier(
    callback = checkErrorOrReload,
    selectedTab = $(".tabulation__item--active").attr("data-name"),
    loadingButton = $("#SAUVEGARDER_DOSSIER")
) {
    var tabCapitalize = selectedTab.charAt(0).toUpperCase() + selectedTab.slice(1);
    var $data =
        $("form#dossierForm" + tabCapitalize).serialize() +
        "&" +
        $("#dossierId").serialize() +
        "&" +
        $("#dossierLinkId").serialize();
    var tabFunction = window["save" + tabCapitalize];
    if (typeof tabFunction !== "undefined" && tabFunction !== null) {
        $data = tabFunction($data);
    }
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/save/" + selectedTab;
    var myRequest = {
        contentId: null,
        dataToSend: $data,
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, callback, displaySimpleErrorMessage);
}

function dossierCallbackModaleSauvegarde() {
    callbackModaleSauvegarde("dossier");
    constructAlertContainer([constructSuccessAlert("L'onglet a été sauvegardé")]);
}

function saveDossierFromModale() {
    saveDossier(dossierCallbackModaleSauvegarde, $("#modale-sauvegarde-dossier").data("onglet"), null);
}

function getVersionReponse(select) {
    var $data =
        $("#dossierId").serialize() +
        "&" +
        $("#dossierLinkId").serialize() +
        "&idVersion=" +
        select.value +
        "&" +
        $("#format").serialize();
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/version";
    var myRequest = {
        contentId: "dossierFormParapheur",
        dataToSend: $data,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, replaceHtmlFunctionAndInitTinyMce);
}

var replaceHtmlFunctionAndInitTinyMce = function replaceHtmlFunctionAndInitTinyMce(containerID, result) {
    replaceHtmlFunction(containerID, result);
    removeTinyMCE("mce_dossier"); // Remove tinyMCE if exists
    initTinyMCEDossier(); // Init tinyMCE
};

function pourValidationRetourPM() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/validationRetourPM";
    var myRequest = {
        contentId: null,
        dataToSend: {
            dossierId: $("#dossierId").val(),
            dossierLinkId: $("#dossierLinkId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, goPreviousOrReloadIfError, displaySimpleErrorMessage);
}

function doReattributionQuestion() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/reattribution";
    var myRequest = {
        contentId: null,
        dataToSend: {
            dossierId: $("#dossierId").val(),
            dossierLinkId: $("#dossierLinkId").val(),
            ministereId: $("#ministere-rattachement-reattribution-key").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, goPreviousOrReloadIfError, displaySimpleErrorMessage);
}

function doReattributionDirect() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/reattributionDirect";
    var myRequest = {
        contentId: null,
        dataToSend: {
            dossierId: $("#dossierId").val(),
            dossierLinkId: $("#dossierLinkId").val(),
            observations: $("#observationReattributionDirect").val(),
            ministereId: $("#ministere-attache-key").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, goPreviousOrReloadIfError, displaySimpleErrorMessage);
}

function doMettreEnAttente() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/attente";
    var myRequest = {
        contentId: null,
        dataToSend: {
            dossierId: $("#dossierId").val(),
            dossierLinkId: $("#dossierLinkId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, goPreviousOrReloadIfError, displaySimpleErrorMessage);
}

function doRefusDeSignature() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/refusDeSignature";
    var myRequest = {
        contentId: null,
        dataToSend: {
            dossierId: $("#dossierId").val(),
            dossierLinkId: $("#dossierLinkId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, goPreviousOrReloadIfError, displaySimpleErrorMessage);
}

function doRejetDossierRetour() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/rejetRetour";
    var myRequest = {
        contentId: null,
        dataToSend: {
            dossierId: $("#dossierId").val(),
            dossierLinkId: $("#dossierLinkId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, goPreviousOrReloadIfError, displaySimpleErrorMessage);
}

function doRefusReattribution() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/refusReattribution";
    var myRequest = {
        contentId: null,
        dataToSend: {
            dossierId: $("#dossierId").val(),
            dossierLinkId: $("#dossierLinkId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, goPreviousOrReloadIfError, displaySimpleErrorMessage);
}

function doRejetDossierPoursuivre() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/rejetPoursuivre";
    var myRequest = {
        contentId: null,
        dataToSend: {
            dossierId: $("#dossierId").val(),
            dossierLinkId: $("#dossierLinkId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, goPreviousOrReloadIfError, displaySimpleErrorMessage);
}

function doSigneEtapeSuivante() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/signature";
    var myRequest = {
        contentId: null,
        dataToSend: {
            dossierId: $("#dossierId").val(),
            dossierLinkId: $("#dossierLinkId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, goPreviousOrReloadIfError, displaySimpleErrorMessage);
}

function doReorientationQuestion() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/reorientation";
    var myRequest = {
        contentId: null,
        dataToSend: {
            dossierId: $("#dossierId").val(),
            dossierLinkId: $("#dossierLinkId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, goPreviousOrReloadIfError, displaySimpleErrorMessage);
}

function doEtapeSuivante() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/etapeSuivante";
    var myRequest = {
        contentId: null,
        dataToSend: {
            dossierId: $("#dossierId").val(),
            dossierLinkId: $("#dossierLinkId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, goPreviousOrReloadIfError, displaySimpleErrorMessage);
}

function populateEditModalWithDocID(groupID, documentID) {
    $("#documentId").val(documentID);
    $("#group_" + groupID).attr("checked", true);
}

function populateModalDeleteWithDocID(documentID) {
    $("#documentIdToDelete").val(documentID);
}

function doEditDocumentFondDeDossier() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/editDocumentFondDossier";

    var formData = new FormData();
    formData.append("documentId", $("#documentId").val());
    formData.append("groupId", $("input:radio[name=groupName]").filter(":checked").val());
    formData.append("documentFile", getFilesFromFileInput("documentFile")[0]);
    var myRequest = {
        contentId: null,
        dataToSend: formData,
        processData: false,
        contentType: false,
        enctype: "multipart/form-data",
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        loadingButton: $("#btn-confirm-edit-doc"),
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doDeleteDocumentFondDeDossier() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/deleteDocumentFondDossier";

    var myRequest = {
        contentId: null,
        dataToSend: {
            documentIdToDelete: $("#documentIdToDelete").val(),
            dossierId: $("#dossierId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        loadingButton: $("#btn-confirm-deletion-doc"),
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doArbitrageSGG() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/arbitragesgg";
    var myRequest = {
        contentId: null,
        dataToSend: {
            dossierId: $("#dossierId").val(),
            dossierLinkId: $("#dossierLinkId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, goPreviousOrReloadIfError, displaySimpleErrorMessage);
}

function openModalArbitrage() {
    $("#modal-arbitrage-dossier").addClass("interstitial-overlay__content--visible");
}

function doArbitrageDossier() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/arbitrageDossier";
    var myRequest = {
        contentId: null,
        dataToSend: {
            dossierId: $("#dossierId").val(),
            dossierLinkId: $("#dossierLinkId").val(),
            observations: $("#observations").val(),
            ministereId: $("#ministere-key").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, goPreviousOrReloadIfError, displaySimpleErrorMessage);
}

function populateModalAddWithGoupID(groupID) {
    $("#hiddenGroupId").val(groupID);
}

function onSelectVersionComparator(select) {
    var isFirst = $(select).attr("id") === "versioncomparaison__select-first";
    var comparisonsHeaders = $(".versioncomparison__versions_diff .header");
    var titleVersion = $(select).children("option:selected").text();
    var newHeader = titleVersion.startsWith("Version") ? "" : "Version : ";
    newHeader += titleVersion;
    if (isFirst) {
        comparisonsHeaders.first().text(newHeader);
    } else {
        comparisonsHeaders.last().text(newHeader);
    }
}

function compareVersions() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/compare";
    var myRequest = {
        contentId: "versioncomparison__versions",
        dataToSend: {
            dossierId: $("#dossierId").val(),
            dossierLinkId: $("#dossierLinkId").val(),
            firstVersion: $("#versioncomparaison__select-first").children("option:selected").val(),
            lastVersion: $("#versioncomparaison__select-last").children("option:selected").val(),
        },
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, replaceAndCompareHtmlFunction, displaySimpleErrorMessage);
}

var replaceAndCompareHtmlFunction = function replaceAndCompareHtml(containerID, result) {
    var labelFirst = $(".versioncomparison__versions_diff .header").first().text();
    var labelLast = $(".versioncomparison__versions_diff .header").last().text();
    result = comparerAction($(result));
    $("#" + containerID).html(result);
    $(".versioncomparison__versions_diff .header").first().text(labelFirst);
    $(".versioncomparison__versions_diff .header").last().text(labelLast);
};

function doRedemarrerDossier() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/redemarrer";
    var myRequest = {
        contentId: null,
        dataToSend: {
            dossierId: $("#dossierId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doCasserCashetServeur() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/casserCachetServeur";
    var myRequest = {
        contentId: null,
        dataToSend: {
            dossierId: $("#dossierId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        loadingButton: $("#CASSER_CACHET_SERVEUR"),
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doImprimerMiseEnForme() {
    alert("imprimer mise en forme");
}

function initDossierSauvegardeModale() {
    var modale = $("#modale-sauvegarde-dossier");
    var onglet = window.location.pathname.substr(window.location.pathname.lastIndexOf("/") + 1);
    modale.attr("data-onglet", onglet);

    // parapheur
    var inner = encodeURIComponent(
        tinymce.get("mce_dossier") ? encodeHTMLSpecialChar(tinymce.get("mce_dossier").getContent()) : ""
    );
    var saveTinyMCE = $("#modale-sauvegarde-dossier-tinymce");

    if (saveTinyMCE.length) {
        saveTinyMCE.text(inner);
    } else {
        modale.append(
            $("<span>", {
                hidden: true,
                id: "modale-sauvegarde-dossier-tinymce",
                text: inner,
            })
        );
    }

    // bordereau
    [
        "AN_rubrique-AN-comp",
        "AN_analyse-AN-comp",
        "SE_theme-Senat-comp",
        "SE_rubrique-Senat-comp",
        "SE_renvoi-Senat-comp",
        "motclef_ministere-Ministere-comp",
        "ministere-rattachement",
    ].forEach(function (name) {
        var selectNode = $("#" + name + " option");
        var saveNode = $("#modale-sauvegarde-dossier-" + name);

        if (saveNode.length) {
            saveNode.html(selectNode.clone());
        } else {
            modale.append(
                $("<span>", {
                    hidden: true,
                    id: "modale-sauvegarde-dossier-" + name,
                    html: selectNode.clone(),
                })
            );
        }
    });
}

function checkDossierSauvegardeModale() {
    var modale = $("#modale-sauvegarde-dossier");
    var onglet = modale.attr("data-onglet");

    if (onglet == "parapheur") {
        return tinymce.get("mce_dossier")
            ? encodeURIComponent(encodeHTMLSpecialChar(tinymce.get("mce_dossier").getContent())) ===
                  $("#modale-sauvegarde-dossier-tinymce").text()
            : true;
    } else if (onglet == "bordereau") {
        var bordereauOk = true;

        [
            "AN_rubrique-AN-comp",
            "AN_analyse-AN-comp",
            "SE_theme-Senat-comp",
            "SE_rubrique-Senat-comp",
            "SE_renvoi-Senat-comp",
            "motclef_ministere-Ministere-comp",
            "ministere-rattachement",
        ].forEach(function (name) {
            var saved = $("#modale-sauvegarde-dossier-" + name + " option");
            var actual = $("#" + name + " option");
            if (saved.length !== actual.length) {
                bordereauOk = false;
            }
            if (saved.length && bordereauOk) {
                for (var i = 0; i < actual.length; ++i) {
                    if (saved[i][0] !== actual[i][0]) {
                        bordereauOk = false;
                    }
                }
            }
        });
        return bordereauOk;
    } else {
        return true;
    }
}

function doEditerDossierPdf() {
    var urlPdf =
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) +
        "/dossier/editerPdf/" +
        $("#dossierId").val();
    window.open(urlPdf, "_blank");
}

function initTinyMCEDossier() {
    initTinyMCE({
        selector: "textarea#mce_dossier",
        setup: function (ed) {
            ed.on("init", function (e) {
                initModaleSauvegarde("dossier");
                initBorderTinyMCE(ed);
            });
        },
    });
}

function changeEditorTypeDossier(select) {
    removeTinyMCE("mce_dossier"); // Remove tinyMCE if exists

    if (select.value == "html") {
        // Add tinyMCE only for html format
        initTinyMCEDossier();
    }
}

$(document).ready(function () {
    initTinyMCEDossier();
});

function doImprimerDossier() {
    var urlPdf =
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) +
        "/dossier/imprimer/" +
        $("#dossierId").val();
    window.open(urlPdf, "_blank");
}
