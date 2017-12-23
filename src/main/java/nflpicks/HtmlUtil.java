package nflpicks;

import java.util.List;

/**
 * 
 * This class holds static functions that kind of deal with html generically
 * so we can reuse this code.
 * 
 * @author albundy
 *
 */
public class HtmlUtil {

	/**
	 * 
	 * This function will create the html for a select input with the given options.
	 * 
	 * It'll only add each of the "attributes" if they're not blank.  Not much to it.
	 * 
	 * @param options
	 * @param selectedOptionValue
	 * @param id
	 * @param name
	 * @param onChangeFunction
	 * @param cssClass
	 * @param style
	 * @return
	 */
	public static String createSelectHtml(List<String[]> options, String selectedOptionValue, String id, String name, String onChangeFunction, String cssClass, String style){
		
		//Steps to do:
		//	1. Just build the "<select>" part and add in the attributes that
		//	   aren't blank.
		//	2. Add in the options if we have them.
		
		StringBuilder select = new StringBuilder();
		
		select.append("<select ");
		
		if (!Util.isBlank(id)){
			select.append(" id=\"" + id + "\" ");
		}
		
		if (!Util.isBlank(name)){
			select.append(" name=\"" + name + "\" ");
		}
		
		if (!Util.isBlank(onChangeFunction)){
			select.append(" onChange=\"" + onChangeFunction + "\" ");
		}
		
		if (!Util.isBlank(cssClass)){
			select.append(" class=\"" + cssClass + "\" ");
		}
		
		if (!Util.isBlank(style)){
			select.append(" style=\"" + style + "\" ");
		}
		
		select.append(" >");
		
		if (options != null){
			for (int index = 0; index < options.size(); index++){
				String[] option = options.get(index);
				String value = option[0];
				String label = option[1];
				String selected = "";
				if (selectedOptionValue != null && value.contentEquals(selectedOptionValue)){
					selected = " selected ";
				}
				select.append("<option value=\"" + value + "\" " + selected + " >" + label + "</option>");
			}
		}
		
		select.append("</select>");
		
		String selectHtml = select.toString();
		
		return selectHtml;
	}
}
