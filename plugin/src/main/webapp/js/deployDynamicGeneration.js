function createTableHeaderCell(text, row) {
    var cell = document.createElement('th');
    var cellText = document.createTextNode(text);
    cell.appendChild(cellText);
    row.appendChild(cell);
}

function createTableCell(text, row) {
    var cell = document.createElement('td');
    var cellText = document.createTextNode(text);
    cell.appendChild(cellText);
    row.appendChild(cell);
}

function getEnvTitleFromUrl() {
    var currentURL = document.URL;
    var equalsIndex = currentURL.lastIndexOf('=') + 1;
    if (equalsIndex > 0) {
        return currentURL.substring(equalsIndex);
    } else {
        return '';
    }
}

function createEnvHeaderDiv(env, collapsable) {
    var headerDiv = document.createElement('div');
    headerDiv.className = 'env-panel-header';
    if (collapsable) {
        headerDiv.className += ' collapse-pointer';
    }
    if (env.deployments.length == 0) {
        headerDiv.className += ' env-panel-empty';
    }
    headerDiv.setAttribute('role', 'tab');
    headerDiv.id = 'heading' + index;

    if (collapsable) {
        headerDiv.setAttribute('data-toggle', 'collapse');
        headerDiv.setAttribute('data-target', '#collapse' + index);
        headerDiv.setAttribute('aria-expanded', 'true');
        headerDiv.setAttribute('aria-controls', 'collapse' + index);
    }
    var headerSpan1 = document.createElement('span');
    headerDiv.appendChild(headerSpan1);
    headerSpan1.className = 'env-panel-title';
    headerSpan1.innerHTML = env.title;

    var headerSpan2 = document.createElement('span');
    headerDiv.appendChild(headerSpan2);
    headerSpan2.className = 'env-panel-details';
    if (collapsable) {
        headerSpan2.innerHTML = env.deployments.length + ' deployments';
    } else {
        headerSpan2.innerHTML = 'No deployments';
    }
    return headerDiv;
}

function createDeploymentTableDiv(deployments, collapsable) {
    var buildTableDiv = document.createElement('div');
    envDiv.appendChild(buildTableDiv);

    if (collapsable) {
        buildTableDiv.className = 'panel-collapse collapse in';
    } else {
        buildTableDiv.className = 'panel';
    }

    buildTableDiv.id = 'collapse' + index;
    buildTableDiv.setAttribute('role', 'tabpanel');
    buildTableDiv.setAttribute('aria-labelledby', 'heading' + index);


    var buildTable = document.createElement('table');
    buildTableDiv.appendChild(buildTable);
    buildTable.className = 'table table-striped';

    var buildTableHeader = document.createElement('thead');
    buildTable.appendChild(buildTableHeader);

    var buildTableHeaderRow = document.createElement('tr');
    buildTableHeader.appendChild(buildTableHeaderRow);

    createTableHeaderCell(getBuildVersion(), buildTableHeaderRow);
    createTableHeaderCell(getApplication(), buildTableHeaderRow);
    createTableHeaderCell(getDeploymentTime(), buildTableHeaderRow);
    createTableHeaderCell(getBuildJob(), buildTableHeaderRow);
    createTableHeaderCell(getBuildTime(), buildTableHeaderRow);


    var tableBody = document.createElement('tbody');
    buildTable.appendChild(tableBody);


    for (var deployNum = deployments.length - 1; deployNum >= 0; deployNum--) {


        var row = document.createElement('tr');
        row.className = 'collapse-pointer collapse-pointer-hover';
        tableBody.appendChild(row);

        createTableCell(deployments[deployNum].buildVersion, row);
        createTableCell(deployments[deployNum].applicationName, row);
        createTableCell(deployments[deployNum].deployedAt, row);

        cell = document.createElement('td');
        var a = document.createElement('a');
        a.href = '/' + deployments[deployNum].build.buildUrl;
        a.innerHTML = deployments[deployNum].build.jobName + " #" + deployments[deployNum].build.number;
        cell.appendChild(a);
        row.appendChild(cell);

        createTableCell(deployments[deployNum].build.builtAt, row);

    }
    return buildTableDiv;
}

//dynamic generation of environments table
var envTitle = getEnvTitleFromUrl();

var environments = getEnvironments();

var accordion = document.getElementById('accordion');
var envList = [];

for (var index = 0; environments.length > index; index++) {
    if (environments[index].title == envTitle || envTitle == '') {
        envList.push(environments[index]);
    }
}

if(envList.length > 0) accordion.innerText = '';
for (var envDepIndex = 0; envList.length > envDepIndex; envDepIndex++) {
    var envDiv = document.createElement('div');
    accordion.appendChild(envDiv);
    envDiv.className = 'panel panel-default';
    if (envDepIndex > 0) {
        envDiv.className += ' env-panel-header-second';
    }

    collapsable = envList.length > 1;

    var headerDiv = createEnvHeaderDiv(envList[envDepIndex], collapsable);
    envDiv.appendChild(headerDiv);

    if (envList[envDepIndex].deployments.length > 0) {
        var buildTableDiv = createDeploymentTableDiv(envList[envDepIndex].deployments, collapsable);
        envDiv.appendChild(buildTableDiv);
    }
}
