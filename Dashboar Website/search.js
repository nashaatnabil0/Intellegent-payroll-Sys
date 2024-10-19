var searchbt = document.getElementById("searchbt");
var updatebt = document.getElementById("updatebt");
searchbt.addEventListener('click', function (e) {
    e.preventDefault();
    var ename = document.getElementById("ename").value;
    var dbname0;
    const db = firebase.firestore();
    var search;
    if (document.getElementById('fnamerb').checked == true) {
        search = db.collection("users").where("FName", "==", ename);
    } else {
        search = db.collection("users").where("EmpID", "==", ename);
    }

    console.log(ename);
    search.get()
        .then((querySnapshot) => {
            if (!querySnapshot.empty) {
                dbname0 = querySnapshot.docs[0].id;
                console.log(dbname0);
                console.log(querySnapshot.docs[0].data());

                addEventListener('input', printData());
                console.log("done");
            } else {
                console.log("3aaa");
            }

            function printData() {
                document.getElementById("fname").value =
                    querySnapshot.docs[0].get("FName")
                document.getElementById("lname").value =
                    querySnapshot.docs[0].get("LName")
                document.getElementById("address").value =
                    querySnapshot.docs[0].get("Address")
                document.getElementById("email").value =
                    querySnapshot.docs[0].get("Email")
                document.getElementById("phone").value =
                    querySnapshot.docs[0].get("Phone")
                document.getElementById("position").value =
                    querySnapshot.docs[0].get("Position")
                document.getElementById("ssn").value =
                    querySnapshot.docs[0].get("SSN")
                document.getElementById("salarypd").value =
                    querySnapshot.docs[0].get("SalaryPD")
                document.getElementById("userid").value =
                    querySnapshot.docs[0].get("EmpID")
                var img = document.getElementById('profilepic');
                var pic = querySnapshot.docs[0].get("Profile_Picture");
                img.setAttribute('src', pic);

            }

        });

    updatebt.addEventListener('click', function (e) {
        e.preventDefault();

        var fname = document.getElementById("fname").value;
        var lname = document.getElementById("lname").value;
        var address = document.getElementById("address").value;
        var email = document.getElementById("email").value;
        var phone = document.getElementById("phone").value;
        var position = document.getElementById("position").value;
        var salary = document.getElementById("salarypd").value;
        var ssn = document.getElementById("ssn").value;

        db.collection("users").doc(dbname0).update({
            FName: fname,
            LName: lname,
            Address: address,
            Email: email,
            Phone: phone,
            Position: position,
            SalaryPD: salary,
            SSN: ssn,

        })

        console.log("done");

    });
});

import { getStorage, ref } from "https://www.gstatic.com/firebasejs/9.8.1/firebase-storage.js";
const storage = getStorage();
const mountainsRef = ref(storage, 'mountains.jpg');
