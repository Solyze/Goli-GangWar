package io.github.solyze.goligangwar.utility;

import com.google.common.base.Charsets;
import io.github.solyze.goligangwar.GoliGangWar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileManager {

    private File file;
    private String name;
    private String directory;
    private YamlConfiguration configuration;

    public FileManager(final JavaPlugin plugin, final String name, final String directory) {
        this.setName(name);
        this.setDirectory(directory);
        this.file = new File(directory, name + ".yml");
        if (!this.file.exists()) plugin.saveResource(name + ".yml", false);
        this.configuration = YamlConfiguration.loadConfiguration(this.getFile());
    }

    public void save() {
        try {
            this.configuration.save(this.file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void reload() {
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        final InputStream resource = GoliGangWar.getInstance().getResource(this.name + ".yml");
        if (resource == null) return;
        this.configuration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(resource, Charsets.UTF_8)));
    }

    public File getFile() {
        return this.file;
    }

    public String getName() {
        return this.name;
    }

    public String getDirectory() {
        return this.directory;
    }

    public YamlConfiguration getConfiguration() {
        return this.configuration;
    }

    public void setFile(final File file) {
        this.file = file;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setDirectory(final String directory) {
        this.directory = directory;
    }

    public void setConfiguration(final YamlConfiguration configuration) {
        this.configuration = configuration;
    }
}
