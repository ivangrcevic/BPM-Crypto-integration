/**
 * Created by ivangrcevic on 20/9/16.
 */
(function(){

    var doLoadPrivateKeyFromPEM = function(pemFile, password) {
        var deferred = jQuery.Deferred();
        //load file
        var reader = new FileReader();
        reader.onload = (function (){
            return function(evt) {
                var content = evt.target.result;
                var privateKey = forge.pki.decryptRsaPrivateKey(content, password);
                deferred.resolve(privateKey)
            };
        })();
        reader.readAsBinaryString(pemFile);
        return deferred.promise();
    };

    var doSign = function (privateKey, string) {
        //Create message digest with SHA1
        var md = forge.md.sha1.create();
        md.update(string);
        //sign with RSA algorithm (default)
        return privateKey.sign(md);
    };

    var TEXTS = {
        "es": {
            "signButton.text": "Firmar",
            "psswdPromt.text": "Ingrese contraseña... "
        },
        "en": {
            "signButton.text": "Sign",
            "psswdPromt.text": "Enter Password... "
        }
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

        var file;

        if (!this.hasClass(IU_ELEM_CLASS)) {
            this.addClass(IU_ELEM_CLASS);
        }
        var texts = TEXTS[(language || LANGUAGE)];
        var fileLoader = $(FILE_INPUT_TEMPLATE);
        var signButton = $(SIGN_BUTTON_TEMPLATE);
        fileLoader.appendTo(this);
        signButton.appendTo(this);

        fileLoader.change(function (evt){
            file = evt.target.files[0];
            signButton.addClass('visible');
        });
        signButton.html(texts["signButton.text"]);
        signButton.click(function (evt){
            promptForPassword(texts).then(function (password){
                doLoadPrivateKeyFromPEM(file, password).then(function (privateKey){
                    var signature = doSign(privateKey, "hola como estás");
                    alert(signature);
                });
            }, function (err){
                console.log(err);
            });
        });

    };

    function promptForPassword(texts) {
        var deferred = jQuery.Deferred();

        var password  = window.prompt(texts["psswdPromt.text"]);
        deferred.resolve(password);

        return deferred.promise();
    }


})();