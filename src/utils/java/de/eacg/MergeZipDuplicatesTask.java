package de.eacg;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Task to merge duplicates in zip/jar files.
 */
public class MergeZipDuplicatesTask extends Task {
    private static final String TEMP_POSTFIX = "-merging";

    private String file;
    private Map<String, List<byte[]>> zipContent;

    private static final String NEW_ZIP = "build/new-zip.jar";

    /**
     * Initializes the task.
     */
    public MergeZipDuplicatesTask() {
        zipContent = new HashMap<>();
    }

    /**
     * Merges all duplicates in the zip file.
     */
    @Override
    public void execute() {
        try {
            readOriginZip();
            if (!zipContent.isEmpty()) {
                writeTargetZip();
                renameResult();
            }
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    private void readOriginZip() throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(file));

        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            byte[] buffer = zis.readAllBytes();
            zis.closeEntry();

            if (zipContent.containsKey(entry.getName())) {
                zipContent.get(entry.getName()).add(buffer);
            } else {
                List<byte[]> list = new LinkedList<>();
                list.add(buffer);
                zipContent.put(entry.getName(), list);
            }
        }
        zis.close();
        log("Entries read from zip file");
    }

    private void writeTargetZip() throws IOException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file + TEMP_POSTFIX));
        for (Map.Entry<String, List<byte[]>> writeEntry : zipContent.entrySet()) {
            zos.putNextEntry(new ZipEntry(writeEntry.getKey()));
            for (byte[] buffer : writeEntry.getValue()) {
                zos.write(buffer);
            }
            zos.closeEntry();
        }
        zos.close();
        log(String.format("%d entries written to temp zip file", zipContent.size()));
    }

    private void renameResult() {
        File origin = new File(file);
        File target = new File(file + TEMP_POSTFIX);
        if (origin.exists() && target.exists()) {
            origin.delete();
            target.renameTo(origin);
        }
        log("Replaced origin zip file with new one");
    }

    public void setFile(String file) {
        this.file = file;
    }
}
