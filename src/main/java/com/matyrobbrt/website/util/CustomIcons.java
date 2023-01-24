package com.matyrobbrt.website.util;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.IconFactory;
import java.util.Locale;

@JsModule("./icons/custom_icons.js")
public enum CustomIcons implements IconFactory {
    GITHUB,
    DISCORD,
    CURSEFORGE;

    public Icon create() {
        return new Icon(this.name().toLowerCase(Locale.ENGLISH).replace('_', '-').replaceAll("^-", ""));
    }

    public static final class Icon extends com.vaadin.flow.component.icon.Icon {
        Icon(String icon) {
            super("custom-icons", icon);
        }
    }
}