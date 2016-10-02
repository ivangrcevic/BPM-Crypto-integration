/**
 * Created by ivangrcevic on 20/9/16.
 */
(function(){

    var doLoadCert = function(certFile, password) {
        var deferred = jQuery.Deferred();
        //load file
        var reader = new FileReader();
        reader.onload = (function (theFile){
            return function(evt) {
                var content = evt.target.result;
                var b64 = forge.util.binary.base64.encode(new Uint8Array(content));
                var p12Der = forge.util.decode64(b64);
                var p12Asn1 = forge.asn1.fromDer(p12Der);
                try {
                    //get the certificate
                    var p12 = forge.pkcs12.pkcs12FromAsn1(p12Asn1, password);
                    var bags = p12.getBags({bagType: forge.pki.oids.certBag});
                    var cert = bags[forge.pki.oids.certBag][0];
                    deferred.resolve(cert);
                } catch (err) {
                    deferred.reject(err.message);
                }
            };
        })(certFile);
        reader.readAsArrayBuffer(certFile);
        return deferred.promise();
    };

    var doSign = function (cert, string) {
        //Create message digest with SHA1
        var md = forge.md.sha1.create();
        md.update(string);
        //sign with RSA algorithm (default)
        var privateKey = null;
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
                doLoadCert(file, password).then(function (cert){
                    var signature = doSign(cert, "hola como estás");
                    alert(signature);
                });
            }, function (err){
                console.log(err);
            });
        });

    };

    function promptForPassword(texts) {
        var deferred = jQuery.Deferred();

        var password  = window.prompt("psswdPromt.text");
        deferred.resolve(password);

        return deferred.promise();
    }


})();