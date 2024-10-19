function logout() {
  firebase.auth().signOut().then(function () {
    update();

  }, function (error) {
    console.error('Sign Out Error', error);
  });
}
function update() {
  console.log("3a");
  window.location = "login.html";
}