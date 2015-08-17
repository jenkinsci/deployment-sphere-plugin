		function createTableHeaderCell(text, row){
			var cell = document.createElement('th');
			var cellText = document.createTextNode(text);
			cell.appendChild(cellText);
			row.appendChild(cell);
		}
		
		function createTableCell(text, row){
			var cell = document.createElement('td');
			var cellText = document.createTextNode(text);
			cell.appendChild(cellText);
			row.appendChild(cell);
		}
		
		
		
		function filterBuildsForApp(builds, appTitle, minBuildNum, maxBuildNum){
			currentBuilds = [];
			for (var index = builds.length - 1; index > 0 ; index--){
				if (builds[index].applicationName == appTitle && builds[index].number > minBuildNum && maxBuildNum >= builds[index].number){
					currentBuilds.push(builds[index]);
				}
			}
			return currentBuilds;
		}
		
		function getBuildsForApp(builds, appTitle){
			currentBuilds = [];
			for (var index = builds.length - 1; index > 0 ; index--){
				if (builds[index].applicationName == appTitle){
					currentBuilds.push(builds[index]);
				}
			}
			return currentBuilds;
		}
		
		
		function getMinMaxChecked(arr){
			var checkedNumArr = [];
			for (var cbNum = 0; arr.length>cbNum; cbNum++){
				if (arr[cbNum].checked){
					checkedNumArr.push(arr[cbNum].id);
				}
			}
			var buildNum = {min:0, max:0};
			buildNum.min = Math.min.apply(Math, checkedNumArr);
			buildNum.max = Math.max.apply(Math, checkedNumArr);
			return buildNum;
		}
		
		function appendMetaDataTableBodyRows(tableBody, arr){
			for (var i = 0; arr.length >i; i++){
				var row = document.createElement('tr');
				var metaDataEntry = arr[i];
				
				for (var key in metaDataEntry) {
					createTableCell(metaDataEntry[key], row)
				}
				tableBody.appendChild(row);
			}
		}
		
		function redrawInfoTable(){
				
				var appTitle = getAppTitleFromUrl();
				var builds = getBuilds();
				
				var arr = document.getElementsByName('chBox');
				var buildMinMaxNum = getMinMaxChecked(arr);
				currentBuilds = filterBuildsForApp(builds, appTitle, buildMinMaxNum.min, buildMinMaxNum.max);
				
				if (currentBuilds.length == 0)
					return;
				
				var table = document.getElementById('buildInfoTable');
				var head = document.createElement('thead');
				var tableBody = document.createElement('tbody');
				var headRow = document.createElement('tr');
				
				var info = currentBuilds[0].metaData;
				
				if (document.getElementById('radioCommits').checked){
					createTableHeaderCell('author', headRow);
					createTableHeaderCell('id', headRow);
					createTableHeaderCell('message', headRow);
					head.appendChild(headRow);
					for (var index = 0; currentBuilds.length > index ; index++){
						var commits = currentBuilds[index].metaData.commits;
						appendMetaDataTableBodyRows(tableBody, commits);
					}
					
				}else{
					
					createTableHeaderCell('id', headRow);
					createTableHeaderCell('title', headRow);
					head.appendChild(headRow);
					for (var index = 0; currentBuilds.length > index ; index++){
						var tickets = currentBuilds[index].metaData.tickets;
						appendMetaDataTableBodyRows(tableBody, tickets);
					}
				}
				if (tableBody.childNodes.length>0){
					table.replaceChild(head, document.getElementById('tableHead'));
				}
				table.replaceChild(tableBody, document.getElementById('tableBody'));
				head.id = 'tableHead';
				tableBody.id = 'tableBody';

		}
		
		
		// generate appTable
		var appTitle = getAppTitleFromUrl();
		var builds = getBuilds();
		var accordion = document.getElementById('accordion');
		var tableBody = document.getElementById('appTableBody');
		
		var buildList = getBuildsForApp(builds, appTitle);
		
		document.getElementById('headerAppName').innerHTML = appTitle;
		document.getElementById('headerAppNum').innerHTML = buildList.length + ' Builds';
		
		for (var buildNum = buildList.length-1; buildNum >=0; buildNum--){
			

			var row = document.createElement('tr');
			
			// create checkbox column
			var cell = document.createElement('td');
			var box = document.createElement('input');
			box.type = 'checkbox';
			box.name = 'chBox';
			box.id = buildList[buildNum].number;
			box.onclick = function(){
			
				var counter = 0;
				var arr = document.getElementsByName('chBox');
				var radios = document.getElementsByName('radioB');
				var unCheckedArr = [];
				for (var cbNum = arr.length-1; cbNum>=0; cbNum--){
					if (arr[cbNum].checked){
						counter++;
					}else{
						unCheckedArr.push(arr[cbNum]);
					}
				}
				if (counter >= 2){
					for (var cbNum = unCheckedArr.length-1; cbNum>=0; cbNum--){
						unCheckedArr[cbNum].disabled = true;
					}
					radios[0].disabled = false;
					radios[1].disabled = false;
					redrawInfoTable();
				}else{
					for (var cbNum = arr.length-1; cbNum>=0; cbNum--){
						arr[cbNum].disabled = false;
					}
					radios[0].disabled = true;
					radios[1].disabled = true;
				}

			
			};
			cell.appendChild(box);
			row.appendChild(cell);
			
			// create other columns
			createTableCell(buildList[buildNum].buildVersion, row);
			createTableCell(buildList[buildNum].applicationName, row);
			
			cell =  document.createElement('td');
			var a = document.createElement('a');
			a.href =  '/'+buildList[buildNum].buildUrl;
			a.innerHTML = buildList[buildNum].jobName +" #"+ buildList[buildNum].number;
			cell.appendChild(a);
			row.appendChild(cell);
			
			createTableCell(buildList[buildNum].builtAt, row);
			
			tableBody.appendChild(row);
		}
		
