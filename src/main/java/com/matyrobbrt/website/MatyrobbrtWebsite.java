package com.matyrobbrt.website;

import com.google.gson.Gson;
import com.matyrobbrt.website.util.Configuration;
import com.matyrobbrt.website.view.ProjectsView;
import io.github.matyrobbrt.curseforgeapi.CurseForgeAPI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class MatyrobbrtWebsite {
    public static Configuration configuration;
    public static CurseForgeAPI cfApi;

    public static void main(String[] args) throws IOException, LoginException {
        try (final Reader reader = Files.newBufferedReader(Path.of("config.json"))) {
            configuration = new Gson().fromJson(reader, Configuration.class);
        }
        cfApi = CurseForgeAPI.builder().apiKey(configuration.cfApiKey()).build();

        final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

        final long projectsRefreshInterval = TimeUnit.HOURS.toSeconds(6);
        service.scheduleAtFixedRate(() -> ProjectsView.refresh(cfApi, configuration), 3, projectsRefreshInterval, TimeUnit.SECONDS);

        SpringApplication.run(MatyrobbrtWebsite.class, args);
    }

}
