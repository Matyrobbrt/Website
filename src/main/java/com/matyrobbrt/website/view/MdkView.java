package com.matyrobbrt.website.view;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Meta;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.text.StreamingTemplateEngine;
import groovy.util.Node;
import groovy.util.NodeList;
import groovy.xml.XmlParser;
import io.github.matyrobbrt.curseforgeapi.util.Utils;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.codehaus.groovy.runtime.StringBufferWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@PageTitle("Forge MDK Maker")
@SuppressWarnings("SameParameterValue")
@Route(value = "/projects/forge-mdk", layout = WebsiteLayout.class)
public class MdkView extends VerticalLayout {
    public static final Logger LOG = LoggerFactory.getLogger(MdkView.class);
    public static final List<String> LICENSES = List.of(
            "ARR", "MIT", "LGPLv3"
    );
    public static final ComparableVersion VERSION_1_16_5 = new ComparableVersion("1.16.5");
    public static final Path MDK_ROOT;
    static {
        final Path dir = Path.of("mdk_template");
        if (Files.exists(dir)) {
            MDK_ROOT = dir.toAbsolutePath();
        } else {
            try {
                final FileSystem fs = FileSystems.newFileSystem(Path.of("mdk_template.zip"), Map.of());
                MDK_ROOT = fs.getPath("/");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private final TextField modId = createField("Mod ID", true);
    private final TextField pkg = createField("Package", true);
    private final TextField mainClassName = createField("Main Class Name", true);

    private final Select<ComparableVersion> mcVersion = new Select<>();
    private final Select<ComparableVersion> forgeVersion = new Select<>();

    private final Select<String> mappingsChannel = new Select<>();
    private final Select<String> mappingsVersion = new Select<>();

    private final ComboBox<String> license = new ComboBox<>("License");
    private final TextField name = new TextField("Mod Name");
    private final TextArea description = new TextArea("Mod Description");
    private final TextField author = new TextField("Mod Author");

    private final Checkbox gradleKotlinDSL = new Checkbox("Use Gradle Kotlin DSL");
    private final Checkbox modsDotGroovy = new Checkbox("Use mods.groovy");
    private final Checkbox useATs = new Checkbox("Use access transformers");
    private final Checkbox sharedRunDirs = new Checkbox("Shared run directories");
    private final Checkbox usesMixin = new Checkbox("Use mixins");
    private final Checkbox mixinGradle = new Checkbox("Add MixinGradle");

    private final Checkbox apiSrcSet = new Checkbox("Add API source set");
    private final Checkbox datagenSrcSet = new Checkbox("Add datagen source set");

    public MdkView() {
        add(createForm());
        add(doneButton());
    }

    private Map<String, Object> createArgs() {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("modId", modId.getValue());
        map.put("modName", name.getValue());
        map.put("modDescription", description.getValue());
        map.put("author", author.getValue());
        map.put("license", license.getValue());
        map.put("displayTest", "MATCH_VERSION");

        map.put("packageName", pkg.getValue());
        map.put("mainClass", mainClassName.getValue());

        map.put("versions", Map.of("minecraft", mcVersion.getValue().toString(), "forge", forgeVersion.getValue().toString()));
        map.put("mappings", Map.of("channel", mappingsChannel.getValue().toLowerCase(Locale.ROOT), "version", Objects.requireNonNullElse(mappingsVersion.getValue(), mcVersion.getValue().toString())));
        map.put("props", Map.of(
                "usesAccessTransformers", useATs.getValue(),
                "sharedRunDirs", sharedRunDirs.getValue(),
                "gradleKotlinDSL", gradleKotlinDSL.getValue(),
                "modsDotGroovy", modsDotGroovy.getValue(),
                "usesMixins", usesMixin.getValue(),
                "mixinGradle", mixinGradle.getValue(),
                "apiSourceSet", apiSrcSet.getValue(),
                "datagenSourceSet", datagenSrcSet.getValue()
        ));
        return map;
    }

    private Component createForm() {
        final FormLayout form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        final var versionsFull = Utils.rethrowSupplier(this::getForgeVersions).get();
        final var versions = versionsFull.entrySet()
                .stream().collect(Collectors.toMap(it -> new ComparableVersion(it.getKey()), Map.Entry::getValue));
        final var parchmentVersions = getParchmentVersions(versionsFull.keySet());
        final var mcVersionsOrdered = versionsFull.keySet().stream()
                .map(ComparableVersion::new).sorted(Comparator.<ComparableVersion>naturalOrder().reversed())
                .map(ComparableVersion::toString).toList();

        mcVersion.setLabel("Minecraft Version");
        mcVersion.setItems(versions.keySet().stream().sorted(Comparator.<ComparableVersion>naturalOrder().reversed()).toArray(ComparableVersion[]::new));
        mcVersion.setEmptySelectionAllowed(false);
        mcVersion.setPlaceholder("Select a Minecraft version");
        mcVersion.addValueChangeListener(event -> {
            forgeVersion.setReadOnly(false);
            forgeVersion.setItems(versions.get(event.getValue()));
        });
        mcVersion.setRequiredIndicatorVisible(true);
        mcVersion.addValueChangeListener(event -> {
            if (mappingsChannel.getValue().equals("Parchment")) {
                refreshParchment(mcVersionsOrdered, parchmentVersions);
            }
        });

        forgeVersion.setLabel("Forge Version");
        forgeVersion.setEmptySelectionAllowed(false);
        forgeVersion.setRequiredIndicatorVisible(true);
        forgeVersion.setReadOnly(true);
        form.add(mcVersion, forgeVersion);

        modId.setMinLength(1); modId.setMaxLength(32); modId.setPattern("^[a-z][a-z0-9_]{1,63}$"); modId.setAllowedCharPattern("[a-z0-9_]");
        pkg.setMinLength(1); pkg.setPattern("^[a-z][a-z0-9_.]*$"); pkg.setAllowedCharPattern("[a-z0-9_.]");
        mainClassName.setMinLength(1); mainClassName.setPattern("^[A-Z][a-zA-Z0-9]*$"); mainClassName.setAllowedCharPattern("[A-Za-z0-9]");
        form.add(modId, pkg, mainClassName);
        form.setColspan(modId, 2);

        final Details mappings = new Details("Mappings");
        mappingsChannel.setLabel("Channel");
        mappingsChannel.setEmptySelectionAllowed(false);
        mappingsChannel.setRequiredIndicatorVisible(true);
        mappingsChannel.setItems("Official", "Parchment");
        mappingsChannel.addValueChangeListener(event -> {
            switch (event.getValue()) {
                case "Official" -> mappingsVersion.setReadOnly(true);
                case "Parchment" -> refreshParchment(mcVersionsOrdered, parchmentVersions);
            }
        });
        mappingsChannel.setValue("Official");

        mappingsVersion.setLabel("Version");
        mappingsVersion.setEmptySelectionAllowed(false);
        mappingsVersion.setRequiredIndicatorVisible(true);
        mappings.addContent(new HorizontalLayout(mappingsChannel, mappingsVersion));

        form.add(mappings, 2);

        final Details display = new Details("Display");
        license.setRequired(true);
        license.setItems(LICENSES);
        license.setValue("ARR");
        description.setWidthFull();

        final VerticalLayout displayLayout = new VerticalLayout();
        displayLayout.add(new HorizontalLayout(name, author, license), description);
        displayLayout.setPadding(false);
        display.addContent(displayLayout);
        form.add(display, 2);

        final Details additional = new Details("Additional");
        final VerticalLayout additionalLayout = new VerticalLayout();
        usesMixin.addValueChangeListener(event -> {
            mixinGradle.setReadOnly(event.getValue());
            mixinGradle.setValue(event.getValue());
        });
        sharedRunDirs.setValue(true);
        additionalLayout.add(gradleKotlinDSL, modsDotGroovy, useATs, sharedRunDirs,
                new HorizontalLayout(usesMixin, mixinGradle),
                new HorizontalLayout(apiSrcSet, datagenSrcSet));
        additionalLayout.setPadding(false);
        additional.addContent(additionalLayout);
        form.add(additional, 2);

        return form;
    }

    private void notify(String message, NotificationVariant variant) {
        final Notification notification = createSelfClosing(layout -> layout.add(new Div(new Text(message))));
        notification.addThemeVariants(variant);
        notification.open();
    }

    private Notification createSelfClosing(Consumer<HorizontalLayout> layoutConsumer) {
        final Notification notification = new Notification();
        final Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> notification.close());
        final HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layoutConsumer.accept(layout);
        layout.add(closeButton);
        notification.add(layout);
        return notification;
    }

    private void refreshParchment(List<String> mcVersionsOrdered, Map<String, List<String>> parchmentVersions) {
        final ComparableVersion mcVersion = this.mcVersion.getValue();
        if (mcVersion == null) return;
        List<String> parchment = parchmentVersions.get(mcVersion.toString());
        if (parchment != null && !parchment.isEmpty()) {
            parchment = parchment.stream().map(it -> it + "-" + mcVersion).toList();
            mappingsVersion.setReadOnly(false);
            mappingsVersion.setItems(parchment);
            mappingsVersion.setValue(parchment.get(0));
            return;
        }

        int nextIndex = mcVersionsOrdered.indexOf(mcVersion.toString()) + 1;
        while (nextIndex < mcVersionsOrdered.size()) {
            final String mcLayer = mcVersionsOrdered.get(nextIndex);
            parchment = parchmentVersions.get(mcLayer);
            if (parchment != null && !parchment.isEmpty()) {
                parchment = parchment.stream().map(it -> mcLayer + "-" + it + "-" + mcVersion).toList();
                mappingsVersion.setReadOnly(false);
                mappingsVersion.setItems(parchment);
                mappingsVersion.setValue(parchment.get(0));
                return;
            }

            nextIndex++;
        }
    }

    private Component doneButton() {
        final HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.END);
        layout.setAlignSelf(Alignment.END);
        layout.setWidthFull();
        final Button button = new Button("Done");
        button.setDisableOnClick(true);
        button.addClickListener(event -> {
            final ProgressBar bar = new ProgressBar();
            bar.setIndeterminate(true);
            bar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
            final Div progressBarLabel = new Div();
            progressBarLabel.setText("Generating mdk...");
            final VerticalLayout progressBarLayout = new VerticalLayout();
            progressBarLayout.add(progressBarLabel, bar);
            try {
                if (mcVersion.getValue() == null) {
                    notify("Please select a Minecraft version!", NotificationVariant.LUMO_ERROR);
                    button.setEnabled(true);
                    return;
                }
                if (forgeVersion.getValue() == null) {
                    notify("Please select a Forge version!", NotificationVariant.LUMO_ERROR);
                    button.setEnabled(true);
                    return;
                }
                if (invalid(modId, "Mod ID", button)) return;
                if (invalid(pkg, "package name", button)) return;
                if (invalid(mainClassName, "main class name", button)) return;

                layout.addComponentAtIndex(0, progressBarLayout);

                final byte[] mdkData = makeMdk();
                final Notification noti = createSelfClosing(lay -> {
                    lay.add("Successfully created MDK! Use the button below to download it.");
                    final Button downloadBtn = new Button("Download");
                    downloadBtn.setIcon(VaadinIcon.DOWNLOAD.create());
                    downloadBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                    final Anchor anc = new Anchor();
                    anc.add(downloadBtn);
                    anc.setHref(new StreamResource("mdk.zip", (stream, session) -> stream.write(mdkData)));
                    lay.add(anc);
                });
                noti.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                noti.open();

                layout.remove(progressBarLayout);
            } catch (Throwable e) {
                notify("Could not create mdk: " + e.getMessage(), NotificationVariant.LUMO_CONTRAST);
                LOG.error("Encountered exception creating MDK: ", e);
                layout.remove(progressBarLayout);
                button.setEnabled(true);
            }
        });
        layout.add(button);
        return layout;
    }

    private boolean invalid(TextField field, String type, Button toEnable) {
        if (field.isInvalid() || field.getValue() == null || field.getValue().isBlank()) {
            toEnable.setEnabled(true);
            notify("Please provide a valid " + type, NotificationVariant.LUMO_PRIMARY);
            return true;
        }
        return false;
    }

    private TextField createField(String name, boolean required) {
        final TextField field = new TextField(name);
        field.setRequired(required);
        return field;
    }

    @SuppressWarnings("all")
    private Map<String, List<ComparableVersion>> getForgeVersions() throws Exception {
        final Map<String, List<ComparableVersion>> versions = new HashMap<>();
        try (final InputStream is = URI.create("https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml").toURL().openStream()) {
            final Node node = new XmlParser().parse(is);
            ((NodeList) ((Node)((NodeList)((Node) ((NodeList) node.get("versioning")).get(0)).get("versions")).get(0)).get("version")).stream().map(o -> ((Node) o).text())
                    .forEach(ver -> {
                        final String[] split = ver.toString().split("-");
                        if (split.length != 2) return;
                        final String mcVersion = split[0];
                        if (new ComparableVersion(mcVersion).compareTo(VERSION_1_16_5) < 0) return;
                        versions.computeIfAbsent(mcVersion, k -> new ArrayList<>()).add(new ComparableVersion(split[1]));
                    });
        }
        versions.forEach((mc, list) -> list.sort(Comparator.<ComparableVersion>naturalOrder().reversed()));
        return versions;
    }

    @SuppressWarnings("all")
    private Map<String, List<String>> getParchmentVersions(Collection<String> minecraftVersions) {
        final Map<String, List<String>> versions = new HashMap<>();
        for (final String mc : minecraftVersions) {
            try (final InputStream is = URI.create("https://ldtteam.jfrog.io/artifactory/parchmentmc-public/org/parchmentmc/data/parchment-" + mc + "/maven-metadata.xml").toURL().openStream()) {
                final Node node = new XmlParser().parse(is);
                ((NodeList) ((Node)((NodeList)((Node) ((NodeList) node.get("versioning")).get(0)).get("versions")).get(0)).get("version")).stream()
                        .map(o -> ((Node) o).text())
                        .forEach(verO -> {
                            final String ver = verO.toString();
                            if (ver.contains("SNAPSHOT")) return;
                            versions.computeIfAbsent(mc, k -> new ArrayList<>()).add(ver);
                        });
            } catch (Exception ignored) {}
        }
        versions.forEach((mc, vers) -> vers.sort(Comparator.comparing(MdkView::dateFromParchment).reversed()));
        return versions;
    }

    public static Date dateFromParchment(String parchmentRelease) {
        final var split = parchmentRelease.split("\\.");
        // noinspection deprecation,MagicConstant
        return new Date(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    public static final StreamingTemplateEngine TEMPLATE_ENGINE = new StreamingTemplateEngine();
    public static final GroovyShell SHELL = new GroovyShell();
    private byte[] makeMdk() throws Throwable {
        final var args = createArgs();

        final Path root = MDK_ROOT;
        final ByteArrayOutputStream bao = new ByteArrayOutputStream();
        final ZipOutputStream zos = new ZipOutputStream(bao);
        try (final Stream<Path> files = Files.walk(root, Integer.MAX_VALUE).filter(Files::isRegularFile)) {
            final Iterator<Path> itr = files.iterator();
            while (itr.hasNext()) {
                final Path next = itr.next().toAbsolutePath();
                String name = root.relativize(next).toString();

                if (name.endsWith(".jar") || name.equals("gradlew") || name.equals("gradlew.bat")) {
                    zos.putNextEntry(new ZipEntry(name));
                    zos.write(Files.readAllBytes(next));
                } else {
                    String content = Files.readString(next);
                    if (content.startsWith("// fileName:")) {
                        final int newLineIdx = content.indexOf('\n');
                        final Binding binding = new Binding(args);
                        binding.setVariable("defaultName", name);
                        final Object nm = SHELL.parse(content.substring("// fileName:".length() + 1, newLineIdx), binding).run();
                        if (nm == null) continue;
                        name = nm.toString();
                        content = content.substring(newLineIdx + 1);
                    }

                    zos.putNextEntry(new ZipEntry(name));
                    final StringBuffer buf = new StringBuffer();
                    TEMPLATE_ENGINE.createTemplate(new StringReader(content)).make(args).writeTo(new StringBufferWriter(buf));
                    zos.write(buf.toString().getBytes(StandardCharsets.UTF_8));
                }
                zos.closeEntry();
            }
        }
        zos.close();

        final byte[] data = bao.toByteArray();
        final String id = UUID.randomUUID().toString();
        Downloader.MDKs.put(id + ".zip", bao.toByteArray());
        return data;
    }

    @Controller
    public static class Downloader {
        public static final Cache<String, byte[]> MDKs = Caffeine.newBuilder()
                .expireAfterWrite(90, TimeUnit.SECONDS)
                .build();
        public static final byte[] EMPTY_BODY = new byte[0];

        @RequestMapping(value = "/projects/forge-mdk/mdk-download/{id}", method = RequestMethod.GET, produces = "application/zip")
        public ResponseEntity<byte[]> getOrder(@PathVariable("id") String id) {
            final byte[] mdk = MDKs.getIfPresent(id);
            if (mdk == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(EMPTY_BODY);
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Disposition", "attachment; filename=\"mdk.zip\"")
                    .body(mdk);
        }
    }
}
