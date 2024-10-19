
var registerbt = document.getElementById("registerbt");

registerbt.addEventListener('click', function (e) {
    e.preventDefault();

    var fname = document.getElementById("fname").value;
    var lname = document.getElementById("lname").value;
    var address = document.getElementById("address").value;
    var email = document.getElementById("email").value;
    var phone = document.getElementById("phone").value;
    var position = document.getElementById("position").value;
    var salary = document.getElementById("salary").value;
    var ssn = document.getElementById("ssn").value;
    var img = document.getElementById("img").value;
    var password = lname + ssn.substr(ssn.length - 4);
    var userid = position + "-" + ssn.substr(ssn.length - 4);
    var status = false;
    var works_day = 0;
    firebase.auth().createUserWithEmailAndPassword(email, password)
        .then((userCredential) => {

            const user = userCredential.user;

        })

    const booksRef = firebase.firestore().collection('users').add({
        FName: fname,
        LName: lname,
        Address: address,
        Email: email,
        Phone: phone,
        Position: position,
        SalaryPD: salary,
        SSN: ssn,
        Profile_Picture: img,
        EmpID: userid,
        Status: status,
        works_day: works_day

    });

    alert("Added Database Successfully");
});

ssn.addEventListener('input', cerateuserid);

function cerateuserid(position, ssn) {
    var ssn = document.getElementById("ssn").value;
    var position = document.getElementById("position").value;
    var userid = position + "-" + ssn.substr(ssn.length - 4);
    document.getElementById("userid").value = userid;
}

