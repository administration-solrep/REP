function downloadExcel(obj) {
    var link = $(obj)[0].attributes["data-controls"].value;
    var param = $("#download-excel-link").prop("data-parameters");
    var fullLink = link.concat("?fileName=" + param);
    window.open(fullLink);
}

function goTomajTimbres() {
    const urlAjax = $("#ajaxCallPath").val();
    var urlPath = urlAjax.substring(0, urlAjax.length - 5);
    ariaLoader($("#reload-loader"), true);
    window.location.href = urlPath + "/admin/timbres#main_content";
}
