package com.matyrobbrt.website;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Meta;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Theme(variant = Lumo.DARK)
@PWA(name = "Matyrobbrt's website", shortName = "Matyrobbrt's website")
@Meta(name = "og:title", content = "Forge MDK Maker")
@Meta(name = "og:type", content = "website")
@Meta(name = "og:url", content = "https://matyrobbrt.com/projects/forge-mdk")
@Meta(name = "og:description", content = "A creator for Forge MDKs")
@Meta(name = "theme-color", content = "#FF0000")
public class AppShell implements AppShellConfigurator {
    @Override
    public void configurePage(AppShellSettings settings) {
        settings.addFavIcon("icon", "favicon.png", "256x256");
    }
}
