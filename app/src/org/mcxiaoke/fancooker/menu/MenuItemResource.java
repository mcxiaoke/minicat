/**
 * 
 */
package org.mcxiaoke.fancooker.menu;

/**
 * @author mcxiaoke
 * 
 */
public class MenuItemResource {
	private int id;
	private int type;
	private String text;
	private int iconId;
	private boolean selected;

	public MenuItemResource(int id, String text, int iconId) {
		this.id = id;
		this.text = text;
		this.iconId = iconId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getIconId() {
		return iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MenuItemResource [id=");
		builder.append(id);
		builder.append(", text=");
		builder.append(text);
		builder.append("]");
		return builder.toString();
	}

}
