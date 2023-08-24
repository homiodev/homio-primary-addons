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
        name = "SearchScope",
        propOrder = {
                "includedSources",
                "includedRecordings",
                "recordingInformationFilter",
                "extension"
        })
public class SearchScope {

    @XmlElement(name = "IncludedSources")
    protected List<SourceReference> includedSources;

    @XmlElement(name = "IncludedRecordings")
    protected List<String> includedRecordings;


    @Getter @XmlElement(name = "RecordingInformationFilter")
    protected String recordingInformationFilter;


    @Getter @XmlElement(name = "Extension")
    protected SearchScopeExtension extension;


    @Getter @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();


    public List<SourceReference> getIncludedSources() {
        if (includedSources == null) {
            includedSources = new ArrayList<SourceReference>();
        }
        return this.includedSources;
    }


    public List<String> getIncludedRecordings() {
        if (includedRecordings == null) {
            includedRecordings = new ArrayList<String>();
        }
        return this.includedRecordings;
    }


    public void setRecordingInformationFilter(String value) {
        this.recordingInformationFilter = value;
    }


    public void setExtension(SearchScopeExtension value) {
        this.extension = value;
    }

}
