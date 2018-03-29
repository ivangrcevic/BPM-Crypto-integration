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
        //Create message digest with SHA1
        // TODO : USAR HMAC!!!!
        var md = forge.md.sha1.create();
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
            "signButton.text": "Firmar",
            "psswdPromt.text": "Ingrese contraseña... "
        },
        "en": {
            "label.text": "Sign with certificate:",
            "signButton.text": "Sign",
            "psswdPromt.text": "Enter Password... "
        }
    };
    var LANGUAGE = "es";

    $scope.texts = TEXTS[LANGUAGE];

    var fileInput = $element.find("input")[0]
    fileInput.onchange = function (changeEvent) {
        $scope.$apply(function() {
            $scope.file = changeEvent.target.files[0];
        });
    };

    $scope.sign = function (){
        promptForPassword().then(function (password){
            doLoadPrivateKeyFromPEM($scope.file, password).then(function (privateKey){
                if(privateKey){
                    var jsonData = JSON.stringify($scope.properties.formOutput);
                    var textToSign = btoa(unescape(encodeURIComponent(jsonData)));
                    var signatureString = btoa(doSign(privateKey, textToSign));
                    console.log("json Data " + jsonData);
                    console.log("textToSign " + textToSign);
                    console.log("signature " + signatureString);
                    $scope.properties.value = signatureString;
                } else {
                    alert("Invalid password");
                }
            });
        }, function (err){
            console.log(err);
        });
    }


}