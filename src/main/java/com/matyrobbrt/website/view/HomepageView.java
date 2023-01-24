package com.matyrobbrt.website.view;

import com.matyrobbrt.website.util.CustomIcons;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Homepage")
@Route(value = "/", layout = WebsiteLayout.class)
public class HomepageView extends VerticalLayout {
    public HomepageView() {
        add(new H1("Socials"));

        add(createSocialCard("GitHub", "https://github.com/matyrobbrt", CustomIcons.GITHUB));
        add(createSocialCard("Discord", "https://discord.gg/vV25kEaWJX", CustomIcons.DISCORD));
        add(createSocialCard("CurseForge", "https://www.curseforge.com/members/matyrobbrt", CustomIcons.CURSEFORGE));
    }

    private Component createSocialCard(String name, String link, CustomIcons iconType) {
        final HorizontalLayout element = new HorizontalLayout();
        final Icon icon = iconType.create();
        icon.setSize("40px");
        element.add(icon);
        final Span label = new Span(name);
        label.getStyle().set("font-size", "40px");
        element.add(label);
        element.setAlignItems(Alignment.CENTER);
        final Anchor anc = new Anchor();
        anc.add(element);
        anc.setHref(link);
        anc.addClassNames("growable", "simple-anchor");
        return anc;
    }
}
