package fr.dila.reponses.webtest.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Modèle qui représente un objet du plan de classement ( par exemple : agroalimentaire (1)
 * @author user
 *
 */
public class PlanClassementItem {

	private String tag = StringUtils.EMPTY;
	
	private Integer count = 0; 
	
	public PlanClassementItem(String content){
		super();
		initContent(content);
	}
	
	/**
	 * Parse la chaîne de caractère et remplit les attibuts de l'objet
	 */
	private void initContent(String content){
		Pattern pattern = Pattern.compile("(.*)\\(([0-9]*)\\)");
		Matcher matcher = pattern.matcher(content);
		if (matcher.find( )) {
			this.tag = matcher.group(1);
			this.tag = this.tag.trim();
			this.count = Integer.parseInt(matcher.group(2));
		}
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
}
