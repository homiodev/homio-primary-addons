package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ObjectTree",
        propOrder = {"rename", "split", "merge", "delete", "extension"})
public class ObjectTree {

    @XmlElement(name = "Rename")
    protected List<Rename> rename;

    @XmlElement(name = "Split")
    protected List<Split> split;

    @XmlElement(name = "Merge")
    protected List<Merge> merge;

    @XmlElement(name = "Delete")
    protected List<ObjectId> delete;


    @Getter @XmlElement(name = "Extension")
    protected ObjectTreeExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<Rename> getRename() {
        if (rename == null) {
            rename = new ArrayList<Rename>();
        }
        return this.rename;
    }


    public List<Split> getSplit() {
        if (split == null) {
            split = new ArrayList<Split>();
        }
        return this.split;
    }


    public List<Merge> getMerge() {
        if (merge == null) {
            merge = new ArrayList<Merge>();
        }
        return this.merge;
    }


    public List<ObjectId> getDelete() {
        if (delete == null) {
            delete = new ArrayList<ObjectId>();
        }
        return this.delete;
    }


    public void setExtension(ObjectTreeExtension value) {
        this.extension = value;
    }

}
