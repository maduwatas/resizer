/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.util;

import static es.xproject.resizer.Ventana.tracePendings;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author jsolis
 */
public class FileList {

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger();
    
    public static boolean anyImageFile(File destinationFile) {
        if (destinationFile != null) {

            File[] fileList = destinationFile.listFiles();
            for (File file : fileList) {
                if (file.isDirectory()) {
                    if (anyImageFile(file))
                        return true;
                } 
                else if (accept(file)) 
                    return true;
            }
        }
        return false;
    }

    public FileList build(File dir) {
        this.onlyDir = false;
        this.recursive = true;
        list(dir);
        return this;
    }

    public FileList build(File dir, boolean recursive) {
        this.onlyDir = false;
        this.recursive = recursive;
        list(dir);
        return this;
    }

    public FileList build(File dir, boolean recursive, boolean onlyDir) {
        this.onlyDir = onlyDir;
        this.recursive = recursive;
        list(dir);
        return this;
    }

    private boolean onlyDir, recursive;
    public final ArrayList<File> files;
    public final ArrayList<File> dirs;
    private static final String[] okFileExtensions = new String[]{"jpg", "jpeg", "png", "gif", "tif", "tiff", "svg", "webp", "bmp", "ico"};

    private static boolean accept(File file) {
        for (String extension : okFileExtensions) {
            if (FilenameUtils.getExtension(file.getName()).toLowerCase().equals(extension)) {
                return true;
            }
        }
        return false;
    }

    public FileList() {
        files = new ArrayList<>();
        dirs = new ArrayList<>();
    }

    public void list(File source) {

        if (source != null) {
            dirs.add(source);
            File[] fileList = source.listFiles();
            for (File file : fileList) {

                if (file.isDirectory() && recursive) {
                    log.trace("list dir " + file.getName());
                    list(file);
                } else if (!onlyDir) {

                    if (accept(file)) {
                        files.add(file);
                        tracePendings("Source file " + file.getAbsolutePath());
                    } else {
                        log.trace("discard file " + file.getName());
                    }
                }
            }

            Collections.sort(dirs);
            Collections.sort(files);
        }
    }

    public boolean hasFiles() {
        return !files.isEmpty();
    }

}
