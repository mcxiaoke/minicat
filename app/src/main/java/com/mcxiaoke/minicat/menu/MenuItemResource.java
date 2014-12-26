/**
 *
 */
package com.mcxiaoke.minicat.menu;

/**
 * @author mcxiaoke
 */
public class MenuItemResource {
    public final int id;
    public final int type;
    public final String text;
    public final int iconId;
    public boolean selected;
    public boolean highlight;// 如果是activity跳转就不需要高亮，本界面的fragment切换需要高亮

    public MenuItemResource(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.text = builder.text;
        this.iconId = builder.iconId;
        this.selected = builder.selected;
        this.highlight = builder.highlight;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        StringBuilder builder2 = new StringBuilder();
        builder2.append("MenuItemResource [id=");
        builder2.append(id);
        builder2.append(", type=");
        builder2.append(type);
        builder2.append(", text=");
        builder2.append(text);
        builder2.append(", iconId=");
        builder2.append(iconId);
        builder2.append(", selected=");
        builder2.append(selected);
        builder2.append(", highlight=");
        builder2.append(highlight);
        builder2.append("]");
        return builder2.toString();
    }

    public static class Builder {
        private int id;
        private int type;
        private String text;
        private int iconId;
        private boolean selected;
        private boolean highlight;

        public Builder() {
        }

        public MenuItemResource build() {
            return new MenuItemResource(this);
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder iconId(int iconId) {
            this.iconId = iconId;
            return this;
        }

        public Builder selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public Builder highlight(boolean highlight) {
            this.highlight = highlight;
            return this;
        }
    }

}
