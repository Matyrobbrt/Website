package com.matyrobbrt.website.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.router.RouteConfiguration;

public class Utils {
    public static Anchor withLink(Component component, Component page) {
        final Anchor anchor = new Anchor();
        anchor.add(component);
        anchor.setHref(RouteConfiguration.forApplicationScope().getUrl(page.getClass()) + "#" + component.getId().orElseThrow());
        anchor.addClassName("simple-anchor");
        return anchor;
    }
}
