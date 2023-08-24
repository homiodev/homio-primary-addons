







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import org.onvif.ver10.schema.BackupFile;

import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"backupFiles"})
@XmlRootElement(name = "GetSystemBackupResponse")
public class GetSystemBackupResponse {

    @XmlElement(name = "BackupFiles", required = true)
    protected List<BackupFile> backupFiles;


    public List<BackupFile> getBackupFiles() {
        if (backupFiles == null) {
            backupFiles = new ArrayList<BackupFile>();
        }
        return this.backupFiles;
    }
}
