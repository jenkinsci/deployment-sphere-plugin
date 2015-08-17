package com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import lombok.NonNull;
import lombok.extern.java.Log;

import com.google.common.io.ByteStreams;

@Log
public class DatabaseMigrator {

    private static final Character CLASSPATH_DELIMITER = '/';
    private static final String JAR_FILE_NAME = "classes.jar";
    private static final String MIGRATION_SOURCE_PATH = "db/migration";
    private static final String MIGRATION_DIST_PATH = File.separator + ".." + File.separator + "classes" + File.separator + MIGRATION_SOURCE_PATH;
    private static final String DB_VERSION_TABLE = "SCHEMA_VERSION";

    public void migrate(@NonNull DataSource dataSource) {
        log.info("Db migration started");
        Flyway flyway = new Flyway();
        flyway.setLocations(resolveMigrationResourcesLocation());
        flyway.setBaselineOnMigrate(true);
        flyway.setTable(DB_VERSION_TABLE);
        flyway.setDataSource(dataSource);
        flyway.setClassLoader(this.getClass().getClassLoader());
        flyway.migrate();
        log.info("Db migration successfully done");
    }

    private String resolveMigrationResourcesLocation() {
        String sourcePath = "classpath:" + MIGRATION_SOURCE_PATH;
        try {
            Class clazz = DatabaseMigrator.class;
            String path = CLASSPATH_DELIMITER + clazz.getName().replace('.', CLASSPATH_DELIMITER) + ".class";
            String url = clazz.getResource(path).toString();
            if (url.contains("!")) {
                sourcePath = resolveMigrationResourceLocationFromJarFile(sourcePath, url);
            }
        } catch (IOException e) {
            log.warning("Loading migration files error: " + e.getMessage());
        }
        return sourcePath;
    }

    private String resolveMigrationResourceLocationFromJarFile(String sourcePath, final String url) throws IOException {
        String jarUriPrefix = "jar:file:";
        JarFile jarFile = new JarFile(url.substring(jarUriPrefix.length() + 1, url.indexOf("!")));
        String jarLocation = jarFile.getName().replace(JAR_FILE_NAME, "");
        copyMigrationToClassPath(jarFile, MIGRATION_SOURCE_PATH, jarLocation + MIGRATION_DIST_PATH);
        sourcePath = "filesystem:" + jarLocation + MIGRATION_DIST_PATH;
        return sourcePath;
    }

    private void copyMigrationToClassPath(JarFile jarFile, String jarDirectory, String destinationDirectory) throws IOException {
        for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
            JarEntry entry = entries.nextElement();
            if (isMigrationEntry(entry, jarDirectory)) {
                String destinationFileName = destinationDirectory + File.separator + entry.getName().substring(jarDirectory.length());
                File destinationFile = new File(destinationFileName);
                File parentFile = destinationFile.getParentFile();
                if (parentFile != null) {
                    parentFile.mkdirs();
                }
                copyMigrationResource(jarFile, entry, destinationFile);
            }
        }
    }

    private void copyMigrationResource(final JarFile jarFile, final JarEntry entry, final File destinationFile) throws IOException {
        try (InputStream in = jarFile.getInputStream(entry);
             OutputStream out = new FileOutputStream(destinationFile)) {
            ByteStreams.copy(in, out);
        }
    }

    private boolean isMigrationEntry(JarEntry entry, String jarDirectory) {
        return entry.getName().startsWith(jarDirectory + CLASSPATH_DELIMITER) && !entry.isDirectory();
    }

}