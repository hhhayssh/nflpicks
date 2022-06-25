function onClickStatNameSelector(event){
	event.stopPropagation();
	var wasSelectorVisible = isVisible('statNameSelectorContainer'); 
	hideSelectorContainers();
	if (!wasSelectorVisible){
		showStatNameSelector();
	}
}

function showStatNameSelector(){
	$('#statNameSelectorContainer').show();
}

function hideStatNameSelector(){
	$('#statNameSelectorContainer').hide();
}

function onClickStatName(event, value){
	event.stopPropagation();
	setSelectedStatName(value);
	hideStatNameSelector();
	NFL_PICKS_GLOBAL.selections.statName = getSelectedStatName();
	updateStatNameLink();
	updateView();
}

function getStatName(value){
	for (var index = 0; index < NFL_PICKS_GLOBAL.statNames.length; index++){
		var statName = NFL_PICKS_GLOBAL.statNames[index];
		if (statName.value == value){
			return statName;
		}
	}
	return null;
}

function updateStatNameLink(){
	var selectedStatName = getSelectedStatName();
	var statName = getStatName(selectedStatName);
	if (statName != null){
		$('#statNamesLink').text(statName.label);
	}
}

function showStatNameLink(){
	$('#statNamesLink').show();
}

function hideStatNameLink(){
	$('#statNamesLink').hide();
}

function showStatNameContainer(){
	$('#statNameContainer').show();
}

function hideStatNameContainer(){
	$('#statNameContainer').hide();
}


/**
 * 
 * Gets the selected stat name.
 * 
 * @returns
 */
function getSelectedStatName(){
	return $('input[name=statName]:checked').val();
}

/**
 * 
 * Sets the selected stat name if it's one of the options
 * on the stat name input.
 * 
 * @param statName
 * @returns
 */
function setSelectedStatName(statName){
	$('input[name=statName]').val([statName]);
	NFL_PICKS_GLOBAL.selections.statName = statName;
}
