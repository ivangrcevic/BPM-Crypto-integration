/**
 * Created by ivangrcevic on 20/9/16.
 */
(function(){

    var doLoadCert = function(pathToFile) {

    };

    var doSign = function(rsaPrivateKey, string) {
        var sig = new KJUR.crypto.Signature({"alg": "SHA1withRSA"});
        sig.init(rsaPrivateKey);
        return sig.signString(string);
    };

    var TEXTS = {
        "es": {"signButton.text": "Firmar"},
        "en": {"signButton.text": "Sign"}
    };

    var LANGUAGE = "es";
    var IU_ELEM_CLASS = "digital-signature-uic";
    var FILE_INPUT_TEMPLATE = '<input type="file" class="file-loader" name="file" />';
    var SIGN_BUTTON_TEMPLATE = '<button type="button" class="sign-button"></button>';


    $.fn.DigitalSignatureUIC = function (language) {

        if (!(window.File && window.FileReader && window.FileList && window.Blob)) {
            alert('The File APIs are not fully supported in this browser.');
            return;
        }

        if (!this.hasClass(IU_ELEM_CLASS)) {
            this.addClass(IU_ELEM_CLASS);
        }
        var texts = TEXTS[(language || LANGUAGE)];
        var fileLoader = $(FILE_INPUT_TEMPLATE);
        var signButton = $(SIGN_BUTTON_TEMPLATE);
        fileLoader.appendTo(this);
        signButton.appendTo(this);

        fileLoader.change(function (evt){
            var file = evt.target.files[0];
            console.log(file.name);
            console.log(file.size);
        });
        signButton.html(texts["signButton.text"]);
        signButton.click(function (evt){
            alert("hola!");
        });

    };


})();