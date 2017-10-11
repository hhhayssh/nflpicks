package nflpicks;

import java.util.List;

public class HtmlUtil {

	public static String createSelectHtml(List<String[]> options, String selectedOptionValue, String id, String name, String onChangeFunction, String cssClass, String style){
		
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
		
		select.append("</select>");
		
		String selectHtml = select.toString();
		
		return selectHtml;
	}
	
}
