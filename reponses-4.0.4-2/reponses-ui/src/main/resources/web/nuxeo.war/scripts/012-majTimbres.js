$(document).ready(function () {
    refreshTimbres();
    initOptinsParametrage();
    refreshRecapitulatifTimbres();
});

function refreshTimbres() {
    if ($(".historique-timbres-running").length > 0) {
        setTimeout(rafraichirHistoriqueTimbres, 3000);
    }
    if ($(".detail-historique-timbres-running").length > 0) {
        setTimeout(rafraichirDetailHistoriqueTimbres, 3000);
    }
    if ($(".detail-migration-historique-timbres-running").length > 0) {
        setTimeout(rafraichirDetailMigrationHistoriqueTimbres, 3000);
    }
}

function refreshRecapitulatifTimbres() {
    if ($("#isPollCountActivated").val() === "true") {
        setTimeout(rafraichirRecapitulatifTimbres, 3000);
    }
}

function rafraichirHistoriqueTimbres() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/timbres/historique/rafraichir";
    var myRequest = {
        contentId: "listeHistorique",
        dataToSend: null,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        caller: this,
    };
    callAjaxRequest(myRequest, replaceHtmlFunctionAndRefreshTimbres, displaySimpleErrorMessage);
}

function rafraichirDetailHistoriqueTimbres() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/timbres/detail/rafraichir";
    var myRequest = {
        contentId: "listeDetailHistorique",
        dataToSend: {
            id: $("#idHidden").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        caller: this,
    };
    callAjaxRequest(myRequest, replaceHtmlFunctionAndRefreshTimbres, displaySimpleErrorMessage);
}

function rafraichirDetailMigrationHistoriqueTimbres() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/timbres/detail/rafraichirDetail";
    var myRequest = {
        contentId: "listeDetailMigrationHistorique",
        dataToSend: {
            id: $("#idDetailHidden").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        caller: this,
    };
    callAjaxRequest(myRequest, replaceHtmlFunctionAndRefreshTimbres, displaySimpleErrorMessage);
}

function rafraichirRecapitulatifTimbres() {
    var ajaxUrl =
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) + "/admin/timbres/rafraichir";
    var myRequest = {
        contentId: "listeRecap",
        dataToSend: null,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        caller: this,
    };
    callAjaxRequest(myRequest, replaceHtmlFunctionAndRefreshRecapTimbres, displaySimpleErrorMessage);
}

var replaceHtmlFunctionAndRefreshTimbres = function replaceHtmlFunctionAndRefreshTimbres(containerID, result) {
    replaceHtmlFunction(containerID, result);
    refreshTimbres();
};

var replaceHtmlFunctionAndRefreshRecapTimbres = function replaceHtmlFunctionAndRefreshRecapTimbres(
    containerID,
    result
) {
    replaceHtmlFunction(containerID, result);
    refreshRecapitulatifTimbres();
};

var replaceHtmlFunctionAndRefreshRecapTimbresWithFocus = function replaceHtmlFunctionAndRefreshRecapTimbresWithFocus(
    containerID,
    result
) {
    replaceHtmlFunctionAndRefreshRecapTimbres(containerID, result);
    history.pushState(null, null, "#listeDetailMigrationHistorique");
    window.location.hash = "#listeDetailMigrationHistorique";
    scrollToElement("listeDetailMigrationHistorique", 500);
};

function chargerDetailMigrationHistorique(el) {
    var id = $(el).closest("tr").data("id");
    var ajaxUrl = $("#ajaxCallPath").val() + "/timbres/detail/consulter";
    var myRequest = {
        contentId: "blocDetailMigration",
        dataToSend: {
            id: id,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, replaceHtmlFunctionAndRefreshRecapTimbresWithFocus, displaySimpleErrorMessage);
}

function initOptinsParametrage() {
    onChangeParametrageSignature(true);
    onChangeParametrageMigrer(true);
}

function onChangeParametrageSignature(isInit = false) {
    if ($("input[name=briserToutesSignatures]:checked").val() === "true") {
        $("input[name^=briserSignature]").attr("disabled", "disabled");
        $("input[name^=briserSignature]").closest(".form-optin").addClass("form-optin--disabled");
        $("input[id^=briserSignatureOui]").prop("checked", true);
    } else {
        $("input[name^=briserSignature]").removeAttr("disabled");
        $("input[name^=briserSignature]").closest(".form-optin").removeClass("form-optin--disabled");
        if (!isInit) {
            $("input[id^=briserSignatureNon]").prop("checked", true);
        }
    }
}

function onChangeParametrageMigrer(isInit = false) {
    if ($("input[name=migrerTousDossiersClos]:checked").val() === "true") {
        $("input[name^=migrerDossier]").attr("disabled", "disabled");
        $("input[name^=migrerDossier]").closest(".form-optin").addClass("form-optin--disabled");
        $("input[id^=migrerDossierOui]").prop("checked", true);
    } else {
        $("input[name^=migrerDossier]").removeAttr("disabled");
        $("input[name^=migrerDossier]").closest(".form-optin").removeClass("form-optin--disabled");
        if (!isInit) {
            $("input[id^=migrerDossierNon]").prop("checked", true);
        }
    }
}

function goToRecap() {
    event.preventDefault();
    var details = [];
    $(".line-ministere-form").each(function () {
        var serializeObject = $(this).serializeArray();
        var objectToJson = "{";
        for (let i = 0; i < serializeObject.length; i++) {
            var name = serializeObject[i]["name"];
            var value = serializeObject[i]["value"];
            if (
                !name.startsWith("urlPreviousPage") &&
                !name.startsWith("briserToutesSignatures") &&
                !name.startsWith("migrerTousDossiersClos")
            ) {
                if (name.startsWith("briserSignature")) {
                    name = name.substr(0, 15);
                }

                if (name.startsWith("migrerDossiersClos")) {
                    name = name.substr(0, 18);
                }
                objectToJson += '"';
                objectToJson += name;
                objectToJson += '" : "';
                objectToJson += value;
                objectToJson += '", ';

                $("select option:selected").text();
            }
        }

        if ($("input[name=briserToutesSignatures]:checked").val() === "true") {
            objectToJson += '"briserSignature" : true, ';
        }
        if ($("input[name=migrerTousDossiersClos]:checked").val() === "true") {
            objectToJson += '"migrerDossiersClos" : true';
        }

        if (objectToJson.slice(-2) == ", ") {
            objectToJson = objectToJson.substring(0, objectToJson.length - 2);
        }
        objectToJson += "}";

        // ajout au tableau
        details.push(objectToJson);
    });
    var myRequest = {
        contentId: null,
        dataToSend: {
            briserToutesSignatures: $("input[name=briserToutesSignatures]:checked").val(),
            migrerTousDossiersClos: $("input[name=migrerTousDossiersClos]:checked").val(),
            details: details,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/majTimbres/parametre",
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, checkErrorOrRedirect, displaySimpleErrorMessage);
}

function setValueNewMinistere(el) {
    var value = $(el).find("option:selected").text();
    $(el).closest(".grid__col").children("input[name=newMinistere]").val(value);
}

function openModalMettreAJour(id) {
    $("#btn-confirm-add-doc").focus();
}

function doMajTimbres() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/majTimbres/maj";
    var myRequest = {
        contentId: null,
        dataToSend: null,
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, checkErrorOrRedirect, displaySimpleErrorMessage);
}
