function addOrEdit(name, addjson, editjson) {
    if (document.getElementById('editedId').innerText == '') {
        fetch('http://localhost:8082/' + name + 'Post?crudType=ADD', {
            method: "POST",
            headers: {
            Accept: "application/json",
            "Content-Type": "application/json"
            },
            body: addjson
        }).then(response => {
            if (response.ok) {
                window.location.assign("http://localhost:8082/" + name);
           } else {
                if (response.status == 403) {
                    window.location.assign("http://localhost:8082/" + name);
                    response.json().then(data => {
                        window.alert(data.message);
                    })
                } else {
                    window.location.assign("http://localhost:8082/" + name);
                    window.alert("Podano nieprawidłowe dane!");
                }
           }
        }).catch(error => {});
    } else {
        fetch('http://localhost:8082/' + name + 'Post?crudType=EDIT', {
            method: "POST",
            headers: {
            Accept: "application/json",
            "Content-Type": "application/json"
            },
            body: editjson
        }).then(response => {
            if (response.ok) {
                window.location.assign("http://localhost:8082/" + name);
           } else {
                if (response.status == 403) {
                    window.location.assign("http://localhost:8082/" + name);
                    response.json().then(data => {
                        window.alert(data.message);
                    })
                } else {
                    window.location.assign("http://localhost:8082/" + name);
                    window.alert("Podano nieprawidłowe dane!");
                }
           }
        }).catch(error => {});
    }
}

function edit(editedId) {
    document.getElementById('actionLabel').textContent = 'Edytuj ID: ';
    document.getElementById('editedId').textContent = editedId;
    document.getElementById('addOrEditButton').innerText='EDYTUJ';
}

function stopEdit() {
    document.getElementById('actionLabel').textContent = 'Dodaj:';
    document.getElementById('editedId').textContent = '';
    document.getElementById('addOrEditButton').innerText='DODAJ';
}

function search(name) {
    window.location.assign("http://localhost:8082/" + name + "?search=" + document.getElementById('search').value);
}

function remove(name, json) {
  fetch('http://localhost:8082/' + name + 'Post?crudType=REMOVE', {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json"
      },
      body: json
  }).then(response => {
        if (response.ok) {
            window.location.assign("http://localhost:8082/" + name);
       } else {
            window.location.assign("http://localhost:8082/" + name);
            window.alert("Nie można usunąć! Sprawdź, czy usuwany obiekt nie jest gdzieś wykorzystany.");
       }
  }).catch(error => {});
}

function select(name) {
    var table = document.getElementById(name + 'Table');
    var value = document.getElementById(name).value
    for (var i = 0, trow; trow = table.rows[i]; i++) {
         if (trow.cells[0].innerText == value){
            return;
         }
    }
    var row = document.createElement("tr");
    var col = document.createElement("td");
    col.appendChild(document.createTextNode(value));
    row.appendChild(col);
    document.getElementById(name + 'Table').appendChild(row);
}

function getSelectedName(name) {
    const list = [];
    var table = document.getElementById(name + 'Table');
    for (var i = 0, trow; trow = table.rows[i]; i++) {
             list.push({
                name : trow.cells[0].innerText
             })
        }
    return list;
}

function getSelectedNumber(name) {
    const list = [];
    var table = document.getElementById(name + 'Table');
    for (var i = 0, trow; trow = table.rows[i]; i++) {
             list.push({
                number : trow.cells[0].innerText
             })
        }
    return list;
}