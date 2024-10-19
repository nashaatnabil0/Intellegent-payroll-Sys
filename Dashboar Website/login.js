
// Your web app's Firebase configuration


  // Check Active User 
firebase.auth().onAuthStateChanged(function (user) {
    if (user) {

        if (user != null) {
            console.log('you are already in');
            // window.location.href = "profile.html";

        }

    } else {
        window.location.herf = "login.html";
    }
});



function login() {
    // Sending data "Email and Password" to FireBase
    "use strict";
    var ademail = document.getElementById("email").value,
        adpassword = document.getElementById("password").value;
    console.log("haaaaa");
    if (ademail == "admin@admin.com") {
        firebase.auth().signInWithEmailAndPassword(ademail, adpassword)
            .then(function () {
                console.log("haa11");
                window.location.herf = "profile.html";
            }).catch(function (error) {
                window.alert(error.message);
            });
    } else {
        alert("you dont have authorization to login here")
    }
}
