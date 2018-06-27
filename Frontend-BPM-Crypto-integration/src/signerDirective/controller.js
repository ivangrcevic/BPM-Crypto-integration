/**
 * The controller is a JavaScript function that augments the AngularJS scope and exposes functions that can be used in the custom widget template
 *
 * Custom widget properties defined on the right can be used as variables in a controller with $scope.properties
 * To use AngularJS standard services, you must declare them in the main function arguments.
 *
 * You can leave the controller empty if you do not need it.
 */
function digitalSignatureController($scope, $element, $q) {

    function doLoadPrivateKeyFromPEM (pemFile, password) {
        var deferred = $q.defer();
        //load file
        var reader = new FileReader();
        reader.onload = (function (){
            return function(evt) {
                var content = evt.target.result;
                try {
                    var privateKey = forge.pki.decryptRsaPrivateKey(content, password);
                    deferred.resolve(privateKey);
                } catch (e){
                    deferred.reject(e);
                }
            };
        })();
        reader.readAsBinaryString(pemFile);
        return deferred.promise;
    }

    function doSign (privateKey, string) {
        //Create message digest with SHA512 (SHA-2)
        var md = forge.md.sha512.create();
        md.update(string);
        //sign with RSA algorithm (default)
        return privateKey.sign(md);
    }

    function promptForPassword() {
        var deferred = $q.defer();

        var password  = window.prompt($scope.texts["psswdPromt.text"]);
        deferred.resolve(password);

        return deferred.promise;
    }


    var TEXTS = {
        "es": {
            "label.text": "Firmar con certificado:",
            "label.password": "Contraseña:",
            "error.text": "Contraseña incorrecta o clave privada inválida.",
            "success.text": "Firma generada correctamente.",
            "signButton.text": "Firmar"
        },
        "en": {
            "label.text": "Sign with certificate:",
            "label.password": "Password:",
            "error.text": "Wrong password or invalid private key file.",
            "success.text": "Signature successfully generated.",
            "signButton.text": "Sign"
        }
    };
    var LANGUAGE = "es";

    var fileInput = $element.find("input")[0]
    fileInput.onchange = function (changeEvent) {
        $scope.$apply(function() {
            $scope.file = changeEvent.target.files[0];
        });
    };

    $scope.signSuccess = false;
    $scope.errorFound = false;
    $scope.texts = TEXTS[LANGUAGE];
    $scope.password = "";

    $scope.sign = function (){
        $scope.errorFound = false;
        $scope.signSuccess = false;
        doLoadPrivateKeyFromPEM($scope.file, $scope.password).then(function (privateKey){
            if(privateKey){
                var jsonData = JSON.stringify($scope.properties.formOutput);
                var textToSign = btoa(unescape(encodeURIComponent(jsonData)));
                var signatureString = btoa(doSign(privateKey, textToSign));
                console.log("json Data " + jsonData);
                console.log("textToSign " + textToSign);
                console.log("signature " + signatureString);
                $scope.properties.value = signatureString;
                $scope.signSuccess = true;
            } else {
                $scope.errorFound = true;
            }
        }, function (error) {
            $scope.errorFound = true;
        });
    }


}