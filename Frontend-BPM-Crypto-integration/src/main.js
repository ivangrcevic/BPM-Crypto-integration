/**
 * Created by ivangrcevic on 20/9/16.
 */

var callback = function(sig) {
    $("#signatureInput").val(sig);
    $("#signatureView").show();
};

var textGetter = function() {
    return $("#textToSignInput").val();
}

$(document).ready(function (){
    $("#signatureView").hide();
    $('#sign-component').DigitalSignatureUIC("", textGetter, callback);
});