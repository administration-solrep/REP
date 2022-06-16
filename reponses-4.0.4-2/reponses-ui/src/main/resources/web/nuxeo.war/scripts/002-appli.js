//Put SWORD specific scripts for appli here

function getAppliName() {
    return "reponses";
}

function afficheDossier(elem) {
    var myLine = $(elem).closest("tr");
    var myID = myLine.data("id");

    var $table = $(elem).closest(".tableForm");

    var mydatas = "";

    $table.find("[data-field]").each(function () {
        mydatas += $(this).data("field") + "=" + $(this).data("value") + "&";
    });

    $table.find("input[data-isForm='true']").each(function () {
        mydatas += this.name + "=" + this.value + "&";
    });

    mydatas += "dossier=" + myID;
    var myRequest = {
        contentId: "monDossier",
        dataToSend: mydatas,
        method: "GET",
        dataType: "html",
        url:
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) + "/recherche/resultats",
        ajaxUrl: $("#ajaxCallPath").val() + "/dossier",
        isChangeURL: true,
        overlay: $("#monDossier").find(".overlay").first().attr("id"),
        caller: elem,
    };

    callAjaxRequest(myRequest, replaceHtmlFunction);
}

function isRequestAlreadyLaunch(request) {
    if (getIndexRequest(request) < 0) {
        return false;
    } else {
        return true;
    }
}

function getIndexRequest(request) {
    var index = -1;
    $.each(requestWaiting, function (i, item) {
        if (item.id === request.id && item.data === request.data) {
            index = i;
            return;
        }
    });
    return index;
}

function onChangePlanType(type) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/corbeille/plan";
    var myRequest = {
        contentId: "planClassementTree",
        dataToSend: "type=" + type,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: $("#planClassementTree").find(".overlay").first().attr("id"),
        caller: $(event.target).parent(),
    };

    callAjaxRequest(myRequest, loadTreeData);
}

function onClickPlanItem(key) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/corbeille/plan";
    var myRequest = {
        contentId: "planClassementTree",
        dataToSend: "key=" + key,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: $("#planClassementTree").find(".overlay").first().attr("id"),
        caller: $(event.target).is(":button") ? $(event.target) : $(event.target).parent(),
    };

    callAjaxRequest(myRequest, loadTreeData);
}

function onChangeMailboxTri(tri) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/corbeille/mailbox";
    var myRequest = {
        contentId: "mailboxListTree",
        dataToSend: "tri=" + tri + "&masquerCorbeilles=" + $("#masquer_corbeilles").prop("checked"),
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: $("#mailboxListTree").find(".overlay").first().attr("id"),
        caller: this,
    };

    callAjaxRequest(myRequest, replaceHtmlFunction);
}

function onClickMailboxItem(key) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/corbeille/mailbox";
    var myRequest = {
        contentId: "mailboxListTree",
        dataToSend: "key=" + key + "&masquerCorbeilles=" + $("#masquer_corbeilles").prop("checked"),
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: $("#mailboxListTree").find(".overlay").first().attr("id"),
        caller: $(event.target).is(":button") ? $(event.target) : $(event.target).parent(),
    };

    callAjaxRequest(myRequest, loadTreeData);
}

function onValidateSelectionMailbox(selectionPoste, selectionUser) {
    $(".aria-autocomplete__input").removeAttr("aria-describedby");

    if (!selectionPoste.val() && !selectionUser.val()) {
        $("#selection_poste_user_error").css("display", "block");
        $(".aria-autocomplete__input").attr("aria-describedby", "selection_poste_user_error");
        $(".aria-autocomplete__input")[0].focus();
    } else {
        sendMailBoxSelectRequest(selectionPoste, selectionUser);
    }
}

function sendMailBoxSelectRequest(selectionPoste, selectionUser) {
    $("#selection_poste_user_error").css("display", "none");

    var ajaxUrl = $("#ajaxCallPath").val() + "/corbeille/mailbox";
    var myRequest = {
        contentId: "mailboxListTree",
        dataToSend:
            selectionPoste.serialize() +
            "&" +
            selectionUser.serialize() +
            "&isSelectionValidee=" +
            true +
            "&masquerCorbeilles=" +
            $("#masquer_corbeilles").prop("checked"),
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: $("#mailboxListTree").find(".overlay").first().attr("id"),
        caller: this,
    };

    callAjaxRequest(myRequest, replaceHtmlFunction);
}

function reinitMailboxSelect(selectionPoste, selectionUser) {
    selectionPoste.val("");
    selectionPoste.find("option").remove();
    selectionPoste.next().find("input").val("");
    selectionUser.val("");
    selectionUser.find("option").remove();
    selectionUser.next().find("input").val("");

    sendMailBoxSelectRequest(selectionPoste, selectionUser);
}

function onClickMasquerCorbeilles() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/corbeille/mailbox";
    var myRequest = {
        contentId: "mailboxListTree",
        dataToSend: "masquerCorbeilles=" + $("#masquer_corbeilles").prop("checked"),
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: $("#mailboxListTree").find(".overlay").first().attr("id"),
        caller: this,
    };

    callAjaxRequest(myRequest, replaceHtmlFunction);
}

var loadOngletData = function loadOnglet(containerID, result, caller, extraDatas) {
    replaceHtmlFunction(containerID, result);

    var dossierLocked = extraDatas.dossierLocked;
    var isDossierLock = $("#LEVER_VERROU_DOSSIER").length > 0 ? true : false;
    if (dossierLocked != isDossierLock) {
        updateStatutLockDossierAria(isDossierLock);
    }

    var triggerBtn = extraDatas.triggerBtn;
    $("#" + triggerBtn.attr("id")).focus();

    initAsyncSelect();
    if (tinymce.get("mce_dossier")) {
        initModaleSauvegarde("dossier");
    } else {
        initTinyMCEDossier();
    }
};

function addFavoris(favoris) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/classement/addFavoris";
    var key = $(favoris).data("item-link").substring(18);
    var myRequest = {
        contentId: null,
        dataToSend: key,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function removeFavoris(cle) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/classement/removeFavoris";
    var key = $(cle).data("item-key");
    var myRequest = {
        contentId: null,
        dataToSend: "key=" + key,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        extraDatas: {
            successCallback: function () {
                if ($(cle).parent().parent().children().length === 1) {
                    $(cle).parent().parent().parent().remove();
                } else {
                    $(cle).parent().remove();
                }
            },
        },
    };

    callAjaxRequest(myRequest, checkErrorOrToast, displaySimpleErrorMessage);
}
