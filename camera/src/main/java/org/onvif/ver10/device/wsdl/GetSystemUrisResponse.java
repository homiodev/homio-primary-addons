







package org.onvif.ver10.device.wsdl;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import org.onvif.ver10.schema.SystemLogUriList;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"systemLogUris", "supportInfoUri", "systemBackupUri", "extension"})
@XmlRootElement(name = "GetSystemUrisResponse")
public class GetSystemUrisResponse {


    @XmlElement(name = "SystemLogUris")
    protected SystemLogUriList systemLogUris;


    @XmlElement(name = "SupportInfoUri")
    @XmlSchemaType(name = "anyURI")
    protected String supportInfoUri;


    @XmlElement(name = "SystemBackupUri")
    @XmlSchemaType(name = "anyURI")
    protected String systemBackupUri;


    @XmlElement(name = "Extension")
    protected GetSystemUrisResponse.Extension extension;


    public void setSystemLogUris(SystemLogUriList value) {
        this.systemLogUris = value;
    }


    public void setSupportInfoUri(String value) {
        this.supportInfoUri = value;
    }


    public void setSystemBackupUri(String value) {
        this.systemBackupUri = value;
    }


    public void setExtension(GetSystemUrisResponse.Extension value) {
        this.extension = value;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"any"})
    public static class Extension {

        @XmlAnyElement(lax = true)
        protected List<Object> any;


        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<Object>();
            }
            return this.any;
        }
    }
}
