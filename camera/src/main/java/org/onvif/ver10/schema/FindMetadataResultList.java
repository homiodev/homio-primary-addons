package org.onvif.ver10.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * Java-Klasse f�r FindMetadataResultList complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten
 * ist.
 *
 * <pre>
 * <complexType name="FindMetadataResultList">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="SearchState" type="{http://www.onvif.org/ver10/schema}SearchState"/>
 *         <element name="Result" type="{http://www.onvif.org/ver10/schema}FindMetadataResult" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "FindMetadataResultList",
        propOrder = {"searchState", "result"})
public class FindMetadataResultList {

    /**
     * -- GETTER --
     *  Ruft den Wert der searchState-Eigenschaft ab.
     *
     * @return possible object is {@link SearchState }
     */
    @Getter @XmlElement(name = "SearchState", required = true)
    protected SearchState searchState;

    @XmlElement(name = "Result")
    protected List<FindMetadataResult> result;

    /**
     * Legt den Wert der searchState-Eigenschaft fest.
     *
     * @param value allowed object is {@link SearchState }
     */
    public void setSearchState(SearchState value) {
        this.searchState = value;
    }

    /**
     * Gets the value of the result property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the result
     * property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getResult().add(newItem);
     * </pre>
     *
     * <p>Objects of the following type(s) are allowed in the list {@link FindMetadataResult }
     */
    public List<FindMetadataResult> getResult() {
        if (result == null) {
            result = new ArrayList<FindMetadataResult>();
        }
        return this.result;
    }
}
