function createTypeSelectorHtml(types){
	
	var typeSelectorHtml = //'<a id="typesLink" href="javascript:" class="selector-link" onClick="onClickTypeSelector(event);">Standings</a>' + 
						   		'<div id="typeSelectorContainer" class="selection-list-container">' + 
						   			'<div>';
	
	for (var index = 0; index < types.length; index++){
		var type = types[index];
		
		var divHtmlCssClass = 'selection-item-container';
		if (index + 1 == types.length){
			divHtmlCssClass = 'selection-item-container-last';
		}
		
		var typeHtml = '<div class="' + divHtmlCssClass + '" onClick="onClickType(event, \'' + type.value + '\');">' +
					   		'<span><input type="radio" name="type" id="type-' + index + '" value="' + type.value + '" onClick="onClickType(event, \'' + type.value + '\');"/></span>' +
					   		'<span><a href="javascript:void(0);" onClick="onClickType(event, \'' + type.value + '\');">' + type.label + '</a></span>' +
					   '</div>';
		
		typeSelectorHtml = typeSelectorHtml + typeHtml;
	}
	
	typeSelectorHtml = typeSelectorHtml +
					   		'</div>' + 
					   '</div>';
	
	return typeSelectorHtml;
}

function createPlayerSelectorHtml(players){

	var multiselect = getMultiselectPlayer();
	var multiselectDisplay = 'display: none;';
	var singleSelectDisplay = '';
	if (multiselect){
		multiselectDisplay = '';
		singleSelectDisplay = 'display: none;';
	}
	
	var playerSelectorHtml = '<div id="playerSelectorContainer" class="selection-list-container">' + 
						   		'<div class="selection-header-container">' +
						   			'<table class="selection-header-items-container">' + 
						   				'<tr>' +
						   					'<td class="selection-header-multiselect-button" onClick="onClickMultiselectPlayerContainer(event);" colspan="2">' +
							   					'<input id="multiselectPlayer" type="checkbox" onClick="onClickMultiselectPlayer(event);" />' + 
							   					'<span><a href="javascript:void(0);" onClick="onClickMultiselectPlayerContainer(event);">Select more than one</a></span>' + 
						   					'</td>' +
						   				'</tr>' +
						   				'<tr id="multiselectPlayerContainer" style="' + multiselectDisplay + '">' +
						   					'<td class="selection-header-clear-button" onClick="onClickClearPlayers(event);">' +
						   						'<a href="javascript:void(0);" onClick="onClickClearPlayers(event);">Clear</a>' +
						   					'</td>' +
						   					'<td class="selection-header-select-all-button" onClick="onClickSelectAllPlayers(event);">' + 
						   						'<a href="javascript:void(0);" onClick="onClickSelectAllPlayers(event);">Select all</a>' +
						   					'</td>' +
						   				'</tr>' +
						   			'</table>' +
						   		'</div>' + 
						   		'<div class="selection-list-items-container">';
	
	for (var index = 0; index < players.length; index++){
		var player = players[index];
		
		var normalizedValue = normalizePlayerValue(player.value);

		var divHtmlCssClass = 'selection-item-container';
		if (index + 1 == players.length){
			divHtmlCssClass = 'selection-item-container-last';
		}

		var playerHtml = '<div id="player-selector-container-' + normalizedValue + '" class="' + divHtmlCssClass + '" onClick="onClickPlayer(event, \'' + player.value + '\');">' +
							'<span><input type="checkbox" id="player-checkbox-input-' + normalizedValue + '" value="' + player.value + '" style="' + multiselectDisplay + '" onClick="onClickPlayer(event, \'' + player.value + '\');"/></span>' +
							'<span><input type="radio" name="player" id="player-radio-input-' + normalizedValue + '" value="' + player.value + '" style="' + singleSelectDisplay + '" onClick="onClickPlayer(event, \'' + player.value + '\');"/></span>' +
					   		'<span><a href="javascript:void(0);" onClick="onClickPlayer(event, \'' + player.value + '\');">' + player.label + '</a></span>' +
					     '</div>';

		playerSelectorHtml = playerSelectorHtml + playerHtml;
	}

	playerSelectorHtml = playerSelectorHtml +
							'</div>' +
					   		'<div id="player-selector-footer-container" class="selection-footer-container" style="' + multiselectDisplay + '">' +
					   			'<table class="selector-footer-button-container">' +
					   				'<tr>' + 
					   					'<td class="selector-footer-cancel-button" onClick="onClickPlayerSelectionCancel(event)">' +
					   						'<a href="javascript:" onClick="onClickPlayerSelectionCancel(event)">Cancel</a>' +
					   					'</td>' +
					   					'<td class="selector-footer-ok-button" onClick="onClickPlayerSelectionOk(event)">' +
					   						'<a href="javascript:" onClick="onClickPlayerSelectionOk(event)">OK</a>' +
					   					'</td>' +
					   				'</tr>' +
					   			'</table>' +
					   		'</div>' +
					  '</div>';
	
	return playerSelectorHtml;
}

function createYearSelectorHtml(years){

	var multiselect = getMultiselectYear();
	var multiselectDisplay = 'display: none;';
	var singleSelectDisplay = '';
	if (multiselect){
		multiselectDisplay = '';
		singleSelectDisplay = 'display: none;';
	}
	
	var yearSelectorHtml = '<div id="yearSelectorContainer" class="selection-list-container">' + 
								'<div class="selection-header-container">' +
									'<table class="selection-header-items-container">' + 
										'<tr>' +
											'<td class="selection-header-multiselect-button" onClick="onClickMultiselectYearContainer(event);" colspan="2">' +
												'<input id="multiselectYear" type="checkbox" onClick="onClickMultiselectYear(event);" />' + 
												'<span><a href="javascript:void(0);" onClick="onClickMultiselectYearContainer(event);">Select more than one</a></span>' + 
											'</td>' +
										'</tr>' +
										'<tr id="multiselectYearContainer" style="' + multiselectDisplay + '">' +
											'<td class="selection-header-clear-button" onClick="onClickClearYears(event);">' +
												'<a href="javascript:void(0);" onClick="onClickClearYears(event);">Clear</a>' +
											'</td>' +
											'<td class="selection-header-select-all-button" onClick="onClickSelectAllYears(event);">' + 
												'<a href="javascript:void(0);" onClick="onClickSelectAllYears(event);">Select all</a>' +
											'</td>' +
										'</tr>' +
									'</table>' +
								'</div>' + 
						   		'<div class="selection-list-items-container">';

	for (var index = 0; index < years.length; index++){
		var year = years[index];
		
		var normalizedValue = normalizeYearValue(year.value);
		
		var divHtmlCssClass = 'selection-item-container';
		if (index + 1 == years.length){
			divHtmlCssClass = 'selection-item-container-last';
		}

		var yearHtml = '<div id="year-selector-container-' + normalizedValue + '" class="' + divHtmlCssClass + '" onClick="onClickYear(event, \'' + year.value + '\');">' +
							'<span><input type="checkbox" id="year-checkbox-input-' + normalizedValue + '" value="' + year.value + '" style="' + multiselectDisplay + '" onClick="onClickYear(event, \'' + year.value + '\');"/></span>' +
							'<span><input type="radio" name="year" id="year-radio-input-' + normalizedValue + '" value="' + year.value + '" style="' + singleSelectDisplay + '" onClick="onClickYear(event, \'' + year.value + '\');"/></span>' +
					   		'<span><a href="javascript:void(0);" onClick="onClickYear(event, \'' + year.value + '\');">' + year.label + '</a></span>' +
					     '</div>';

		yearSelectorHtml = yearSelectorHtml + yearHtml;
	}

	yearSelectorHtml = yearSelectorHtml +
							'</div>' +
							'<div id="year-selector-footer-container" class="selection-footer-container" style="' + multiselectDisplay + '">' +
					   			'<table class="selector-footer-button-container">' +
					   				'<tr>' + 
					   					'<td class="selector-footer-cancel-button" onClick="onClickYearSelectionCancel(event)">' +
					   						'<a href="javascript:" onClick="onClickYearSelectionCancel(event)">Cancel</a>' +
					   					'</td>' +
					   					'<td class="selector-footer-ok-button" onClick="onClickYearSelectionOk(event)">' +
					   						'<a href="javascript:" onClick="onClickYearSelectionOk(event)">OK</a>' +
					   					'</td>' +
					   				'</tr>' +
					   			'</table>' +
					   		'</div>' +
					  '</div>';
	
	return yearSelectorHtml;
}

function createWeekSelectorHtml(weeks){

	var multiselect = getMultiselectWeek();
	var multiselectDisplay = 'display: none;';
	var singleSelectDisplay = '';
	if (multiselect){
		multiselectDisplay = '';
		singleSelectDisplay = 'display: none;';
	}
	
	var weekSelectorHtml = '<div id="weekSelectorContainer" class="selection-list-container">' + 
						   		'<div class="selection-header-container">' +
								   	'<table class="selection-header-items-container">' + 
						   				'<tr>' +
						   					'<td class="selection-header-multiselect-button" onClick="onClickMultiselectWeekContainer(event);" colspan="2">' +
							   					'<input id="multiselectWeek" type="checkbox" onClick="onClickMultiselectWeek(event);" />' + 
							   					'<span><a href="javascript:void(0);" onClick="onClickMultiselectWeekContainer(event);">Select more than one</a></span>' + 
						   					'</td>' +
						   				'</tr>' +
						   				'<tr id="multiselectWeekContainer" style="' + multiselectDisplay + '">' +
						   					'<td class="selection-header-clear-button" onClick="onClickClearWeeks(event);">' +
						   						'<a href="javascript:void(0);" onClick="onClickClearWeeks(event);">Clear</a>' +
						   					'</td>' +
						   					'<td class="selection-header-select-all-button" onClick="onClickSelectAllWeeks(event);">' + 
						   						'<a href="javascript:void(0);" onClick="onClickSelectAllWeeks(event);">Select all</a>' +
						   					'</td>' +
						   				'</tr>' +
						   			'</table>' +
						   		'</div>' + 
						   		'<div class="selection-list-items-container">';

	for (var index = 0; index < weeks.length; index++){
		var week = weeks[index];
		
		var normalizedValue = normalizeWeekValue(week.value);
		
		var divHtmlCssClass = 'selection-item-container';
		if (index + 1 == weeks.length){
			divHtmlCssClass = 'selection-item-container-last';
		}

		var weekHtml = '<div id="week-selector-container-' + normalizedValue + '" class="' + divHtmlCssClass + '" onClick="onClickWeek(event, \'' + week.value + '\');">' +
							'<span><input type="checkbox" id="week-checkbox-input-' + normalizedValue + '" value="' + week.value + '" style="' + multiselectDisplay + '" onClick="onClickWeek(event, \'' + week.value + '\');"/></span>' +
							'<span><input type="radio" name="week" id="week-radio-input-' + normalizedValue + '" value="' + week.value + '" style="' + singleSelectDisplay + '" onClick="onClickWeek(event, \'' + week.value + '\');"/></span>' +
					   		'<span><a href="javascript:void(0);" onClick="onClickWeek(event, \'' + week.value + '\');">' + week.label + '</a></span>' +
					     '</div>';

		weekSelectorHtml = weekSelectorHtml + weekHtml;
	}

	weekSelectorHtml = weekSelectorHtml +
							'</div>' +
					   		'<div id="week-selector-footer-container" class="selection-footer-container" style="' + multiselectDisplay + '">' +
						   		'<table class="selector-footer-button-container">' +
					   				'<tr>' + 
					   					'<td class="selector-footer-cancel-button" onClick="onClickWeekSelectionCancel(event)">' +
					   						'<a href="javascript:" onClick="onClickWeekSelectionCancel(event)">Cancel</a>' +
					   					'</td>' +
					   					'<td class="selector-footer-ok-button" onClick="onClickWeekSelectionOk(event)">' +
					   						'<a href="javascript:" onClick="onClickWeekSelectionOk(event)">OK</a>' +
					   					'</td>' +
					   				'</tr>' +
					   			'</table>' +
					   		'</div>';
	
	return weekSelectorHtml;
}


function createTeamSelectorHtml(teams){

	var multiselectDisplay = 'display: none;';
	var singleSelectDisplay = '';
	
	var teamSelectorHtml = '<div id="teamSelectorContainer" class="selection-list-container">' + 
						   		'<div class="selection-header-container">' +
							   		'<table class="selection-header-items-container">' +
									   	'<tr>' +
						   					'<td colspan="3" class="selection-header-multiselect-button" onClick="onClickMultiselectTeamContainer(event);" colspan="2">' +
							   					'<input id="multiselectTeam" type="checkbox" onClick="onClickMultiselectTeam(event);" />' + 
							   					'<span><a href="javascript:void(0);" onClick="onClickMultiselectTeamContainer(event);">Select more than one</a></span>' + 
						   					'</td>' +
						   				'</tr>' +
						   				'<tr>' + 
						   					'<td colspan="3" style="padding: 0px;">' +
							   					'<table style="width: 100%; border-collapse: collapse;">' +
							   						'<tr>' +
									   					'<td id="team1SelectorContainer" style="width: 40%;" class="selection-header-clear-button" onClick="onClickTeam1Selector(event);">' +
								   							'<input type="radio" id="team1SelectorRadioButton" />' +
									   						'<a id="team1SelectorLink" href="javascript:void(0);" onClick="onClickTeam1Selector(event);">Team 1</a>' +
								   						'</td>' +
									   					'<td style="width: 20%;" class="selection-header-clear-button" onClick="onClickTeamVsOrAtContainerSelector(event);">' +
								   							'<div>' +
								   								'<a id="vsOrAtSelectorLinkCurrent" href="javascript:void(0);" onClick="onClickTeamVsOrAtContainerSelector(event);">vs</a>' +
								   							'</div>' +
								   						'</td>' + 
									   					'<td id="team2SelectorContainer" style="width: 40%;" class="selection-header-select-all-button" onClick="onClickTeam2Selector(event);">' +
									   						'<input type="radio" id="team2SelectorRadioButton" />' +
									   						'<a id="team2SelectorLink" href="javascript:void(0);" onClick="onClickTeam2Selector(event);">Team 2</a>' +
									   					'</td>' +
									   				'</tr>' +
							   					'</table>' +
						   					'</td>' +
						   				'</tr>' +
						   				'<tr id="vsOrAtContainer" style="display: none;">' +
						   					'<td colspan="3" style="padding: 0px;">' +
							   					'<table style="width: 100%; border-collapse: collapse;">' +
							   						'<tr>' +
							   							'<td style="width: 40%;" class="selection-header-clear-button">&nbsp;</td>' +
							   							'<td style="width: 20%;" class="selection-header-clear-button" onClick="onClickTeamVsOrAtSelector(event);">' +
							   								'<div>' +
							   									'<a id="vsOrAtSelectorLinkNotCurrent" href="javascript:void(0);" onClick="onClickTeamVsOrAtSelector(event);">@</a>' +
							   								'</div>' +
							   							'</td>' +
							   							'<td style="width: 40%;" class="selection-header-select-all-button">&nbsp;</td>' +
							   						'</tr>' +
							   					'</table>' +
						   					'</td>' +
						   				'</tr>' +
						   				'<tr id="multiselectTeamContainer" style="' + multiselectDisplay + '">' +
						   					'<td style="width: 50%;" class="selection-header-clear-button" onClick="onClickClearTeams(event);">' +
						   						'<a href="javascript:void(0);" onClick="onClickClearTeams(event);">Clear</a>' +
						   					'</td>' +
						   					'<td style="width: 50%;" class="selection-header-select-all-button" onClick="onClickSelectAllTeams(event);">' + 
						   						'<a href="javascript:void(0);" onClick="onClickSelectAllTeams(event);">Select all</a>' +
						   					'</td>' +
						   				'</tr>' +
						   				/*'<tr>' +
						   					'<td class="selection-header-team1-at-team2-button" onClick="onClickTeam1AtTeam2SelectorContainer(event);" colspan="2">' +
							   					'<input id="team1AtTeam2" type="checkbox" onClick="onClickTeam1AtTeam2Selector(event);" />' + 
							   					'<span><a href="javascript:void(0);" onClick="onClickTeam1AtTeam2SelectorContainer(event);">Team 1 @ Team 2</a></span>' + 
						   					'</td>' +
					   					'</tr>' +
							   			'<tr id="team12Container">' +
						   					'<td id="team1SelectorContainer" class="selection-header-clear-button" onClick="onClickTeam1Selector(event);">' +
						   						'<a id="team1SelectorLink" href="javascript:void(0);" onClick="onClickTeam1Selector(event);">Team 1</a>' +
						   					'</td>' +
						   					'<td id="team2SelectorContainer" class="selection-header-select-all-button" onClick="onClickTeam2Selector(event);">' + 
						   						'<a id="team2SelectorLink" href="javascript:void(0);" onClick="onClickTeam2Selector(event);">Team 2</a>' +
						   					'</td>' +
						   				'</tr>' +*/
						   			'</table>' +
						   		'</div>' + 
						   		'<div class="selection-list-items-container">';

	var team1ListHtml = '<div id="team-1-list-items-container"><div>TEAM1</div>';
	var team2ListHtml = '<div id="team-2-list-items-container" style="display: none;"><div>TEAM2</div>';
	
	for (var index = 0; index < teams.length; index++){
		var team = teams[index];
		
		var normalizedValue = normalizeTeamValue(team.value);
		
		var divHtmlCssClass = 'selection-item-container';
		if (index + 1 == teams.length){
			divHtmlCssClass = 'selection-item-container-last';
		}

		var team1Html = '<div id="team-1-selector-container-' + normalizedValue + '" class="' + divHtmlCssClass + '" onClick="onClickTeam1(event, \'' + team.value + '\');">' +
							'<span><input type="checkbox" id="team-1-checkbox-input-' + normalizedValue + '" value="' + team.value + '" style="' + multiselectDisplay + '" onClick="onClickTeam1(event, \'' + team.value + '\');"/></span>' +
							'<span><input type="radio" id="team-1-radio-input-' + normalizedValue + '" name="team-1" value="' + team.value + '" style="' + singleSelectDisplay + '" onClick="onClickTeam1(event, \'' + team.value + '\');"/></span>' +
					   		'<span><a href="javascript:void(0);" onClick="onClickTeam1(event, \'' + team.value + '\');">' + team.label + '</a></span>' +
					     '</div>';

		team1ListHtml = team1ListHtml + team1Html;
		
		var team2Html = '<div id="team-2-selector-container-' + normalizedValue + '" class="' + divHtmlCssClass + '" onClick="onClickTeam2(event, \'' + team.value + '\');">' +
							'<span><input type="checkbox" id="team-2-checkbox-input-' + normalizedValue + '" value="' + team.value + '" style="' + multiselectDisplay + '" onClick="onClickTeam2(event, \'' + team.value + '\');"/></span>' +
							'<span><input type="radio" id="team-2-radio-input-' + normalizedValue + '" name="team-2" value="' + team.value + '" style="' + singleSelectDisplay + '" onClick="onClickTeam2(event, \'' + team.value + '\');"/></span>' +
					   		'<span><a href="javascript:void(0);" onClick="onClickTeam2(event, \'' + team.value + '\');">' + team.label + '</a></span>' +
					     '</div>';
		
		team2ListHtml = team2ListHtml + team2Html;
		
		//teamSelectorHtml = teamSelectorHtml + teamHtml;
	}
	
	team1ListHtml = team1ListHtml + '</div>';
	
	team2ListHtml = team2ListHtml + '</div>';

	teamSelectorHtml = teamSelectorHtml + 
					   team1ListHtml + 
					   team2ListHtml +
							'</div>' +
					   		'<div id="team-selector-footer-container" class="selection-footer-container">' +
					   			'<table class="selector-footer-button-container">' +
					   				'<tr>' + 
					   					'<td class="selector-footer-cancel-button" onClick="onClickTeamSelectionCancel(event)">' +
					   						'<a href="javascript:" onClick="onClickTeamSelectionCancel(event)">Cancel</a>' +
					   					'</td>' +
					   					'<td class="selector-footer-ok-button" onClick="onClickTeamSelectionOk(event)">' +
					   						'<a href="javascript:" onClick="onClickTeamSelectionOk(event)">OK</a>' +
					   					'</td>' +
					   				'</tr>' +
					   			'</table>' +
					   		'</div>' +
					  '</div>';
	
	return teamSelectorHtml;
}



function createStatNameSelectorHtml(statNames){
	
	var statNameSelectorHtml = //'<a id="statNamesLink" href="javascript:" class="selector-link" onClick="onClickStatNameSelector(event);">Standings</a>' + 
						   		'<div id="statNameSelectorContainer" class="selection-list-container">' + 
						   			'<div>';
	
	for (var index = 0; index < statNames.length; index++){
		var statName = statNames[index];
		
		var divHtmlCssClass = 'selection-item-container';
		if (index + 1 == statNames.length){
			divHtmlCssClass = 'selection-item-container-last';
		}
		
		var statNameHtml = '<div class="' + divHtmlCssClass + '" onClick="onClickStatName(event, \'' + statName.value + '\');">' +
					   		'<span><input type="radio" name="statName" id="statName-' + index + '" value="' + statName.value + '" onClick="onClickStatName(event, \'' + statName.value + '\');"/></span>' +
					   		'<span><a href="javascript:void(0);" onClick="onClickStatName(event, \'' + statName.value + '\');">' + statName.label + '</a></span>' + 
					   '</div>';
		
		statNameSelectorHtml = statNameSelectorHtml + statNameHtml;
	}
	
	statNameSelectorHtml = statNameSelectorHtml +
					   		'</div>' + 
					   '</div>';
	
	return statNameSelectorHtml;
}