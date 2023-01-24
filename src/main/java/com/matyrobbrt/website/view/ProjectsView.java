package com.matyrobbrt.website.view;

import com.matyrobbrt.website.util.Configuration;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import io.github.matyrobbrt.curseforgeapi.CurseForgeAPI;
import io.github.matyrobbrt.curseforgeapi.request.query.ModSearchQuery;
import io.github.matyrobbrt.curseforgeapi.schemas.file.FileIndex;
import io.github.matyrobbrt.curseforgeapi.schemas.mod.Mod;
import io.github.matyrobbrt.curseforgeapi.util.Constants;
import io.github.matyrobbrt.curseforgeapi.util.Utils;
import org.apache.maven.artifact.versioning.ComparableVersion;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PageTitle("Projects")
@CssImport("./styles/shared-styles.css")
@Route(value = "/projects", layout = WebsiteLayout.class)
public class ProjectsView extends VerticalLayout {
    private static ModsToDisplay modsToDisplay;

    public ProjectsView() {
       setupMods();
    }

    private void setupMods() {
        final H1 header = new H1("Minecraft Mods");
        header.setId("mc-mods");
        add(com.matyrobbrt.website.util.Utils.withLink(header, this));

        final Grid<Mod> mods = new Grid<>();
        mods.setItems(modsToDisplay.mods);

        mods.addColumn(new ComponentRenderer<>(mod -> {
            HorizontalLayout row = new HorizontalLayout();
            row.setAlignItems(Alignment.CENTER);

            Avatar avatar = new Avatar();
            avatar.setName(mod.name());
            avatar.setImage(mod.logo().url());

            Span name = new Span(mod.name());
            Span summary = new Span(mod.summary());
            summary.getStyle()
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-s)");
            final Anchor anchor = new Anchor();
            anchor.add(name);
            anchor.setHref(mod.links().websiteUrl());

            VerticalLayout column = new VerticalLayout(anchor, summary);
            column.setPadding(false);
            column.setSpacing(false);

            row.add(avatar, column);
            row.getStyle().set("line-height", "var(--lumo-line-height-m)");

            return row;
        })).setHeader("Mod")
                .setAutoWidth(true)
                .setFooter(modsToDisplay.mods.size() + " total mods");

        mods.addColumn(new ComponentRenderer<>(mod -> new HorizontalLayout(mod.categories().stream()
                        .limit(2).<Component>map(it -> {
                            final Avatar avatar = new Avatar();
                            avatar.setName(it.name());
                            avatar.setImage(it.iconUrl());
                            final Anchor anc = new Anchor();
                            anc.add(avatar);
                            anc.setHref(it.url());
                            return anc;
                        }).toArray(Component[]::new))))
                .setFlexGrow(1)
                .setHeader("Categories");

        mods.addColumn(mod -> formatDownloads((int) mod.downloadCount()))
                .setHeader("Downloads").setSortable(true)
                .setComparator(Comparator.comparing(Mod::downloadCount))
                .setFooter(formatDownloads(modsToDisplay.mods.stream()
                        .mapToInt(it -> (int) it.downloadCount())
                        .sum()) + " total downloads")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setAutoWidth(true);

        mods.setSelectionMode(Grid.SelectionMode.NONE);
        mods.setItemDetailsRenderer(new ComponentRenderer<>(ModDetails::new, ModDetails::setMod));
        mods.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        add(mods);
    }

    private static class ModDetails extends Accordion {
        public void setMod(Mod mod) {
            final String baseFileURL = "https://www.curseforge.com/minecraft/mc-mods/" + mod.slug() + "/files/";

            {
                final FlexLayout gameVersions = new FlexLayout();
                modsToDisplay.sortedLastFiles.get(mod.slug()).forEach(version -> {
                            final Span span = new Span(version.gameVersion());

                            span.getElement().getThemeList().add("badge pill");
                            span.getElement().getThemeList().add(switch (version.releaseType()) {
                                case RELEASE -> "success";
                                case BETA -> "beta";
                                case ALPHA -> "alpha";
                            });

                            span.getStyle().set("display", "block").set("text-align", "center");

                            Tooltip.forComponent(span)
                                    .withPosition(Tooltip.TooltipPosition.TOP_START)
                                    .setText(switch (version.releaseType()) {
                                        case RELEASE -> "Release";
                                        case BETA -> "Beta";
                                        case ALPHA -> "Alpha";
                                    });

                            final Anchor anc = new Anchor();
                            anc.add(span);
                            anc.setHref(baseFileURL + version.fileId());
                            gameVersions.add(anc);
                        });

                gameVersions.getStyle()
                        .set("grid-template-columns", "repeat(7, 1fr)")
                        .set("display", "grid")
                        .set("gap", ".5rem");
                gameVersions.setFlexWrap(FlexLayout.FlexWrap.WRAP);
                gameVersions.setAlignContent(FlexLayout.ContentAlignment.SPACE_AROUND);
                add("Game versions", gameVersions);
            }

            record LinkData(@Nullable String link, String name) {}
            final HorizontalLayout links = new HorizontalLayout();
            Stream.of(
                            new LinkData(mod.links().sourceUrl(), "Source"),
                            new LinkData(mod.links().issuesUrl(), "Issues"),
                            new LinkData(mod.links().wikiUrl(), "Wiki")
                    ).filter(it -> it.link != null)
                    .forEach(it -> {
                        final Span span = new Span(it.name);
                        final Anchor anc = new Anchor();
                        anc.add(span);
                        anc.setHref(it.link);
                        links.add(anc);
                    });
            add("Links", links);
            close();
        }
    }

    public static Stream<Mod> getModsBy(CurseForgeAPI api, String authorName) {
        return Utils.rethrowSupplier(() -> api.getHelper()
                .searchMods(ModSearchQuery.of(Constants.GameIDs.MINECRAFT).searchFilter(authorName))
                .stream().flatMap(List::stream)
                .filter(it -> it.authors().stream().anyMatch(au -> au.name().equalsIgnoreCase(authorName)))).get();
    }

    public static String formatDownloads(int number) {
        if (number >= 1000000) {
            return String.format("%.2fM", number / 1000000.0);
        }
        if (number >= 1000) {
            return String.format("%.2fK", number / 1000.0);
        }
        return String.valueOf(number);
    }

    public static void refresh(CurseForgeAPI api, Configuration configuration) {
        modsToDisplay = collectData(api, configuration);
    }

    public static void empty() {
        modsToDisplay = new ModsToDisplay(List.of(), Map.of());
    }

    public static ModsToDisplay collectData(CurseForgeAPI api, Configuration configuration) {
        final List<Mod> mods = getModsBy(api, configuration.cfUser())
                .filter(it -> !configuration.modsToExclude().contains(it.slug())).toList();
        return new ModsToDisplay(mods, mods.stream()
                .collect(Collectors.toMap(Mod::slug, ProjectsView::lastFiles)));
    }

    public static List<FileIndex> lastFiles(Mod mod) {
        record IndexWithVersion(ComparableVersion version, FileIndex index) {}
        final Map<String, IndexWithVersion> byMcVersion = new HashMap<>();
        for (final FileIndex index : mod.latestFilesIndexes()) {
            final IndexWithVersion old = byMcVersion.get(index.gameVersion());
            if (old == null || old.index.releaseType().compareTo(index.releaseType()) > 0) {
                byMcVersion.put(index.gameVersion(), new IndexWithVersion(new ComparableVersion(index.gameVersion()), index));
            }
        }
        return byMcVersion.values().stream()
                .sorted(Comparator.comparing(IndexWithVersion::version).reversed())
                .map(IndexWithVersion::index)
                .toList();
    }

    record ModsToDisplay(List<Mod> mods, Map<String, List<FileIndex>> sortedLastFiles) {}
}
