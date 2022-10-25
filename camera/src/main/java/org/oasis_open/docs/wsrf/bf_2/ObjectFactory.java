package org.oasis_open.docs.wsrf.bf_2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

  private final static QName _BaseFault_QNAME = new QName("http://docs.oasis-open.org/wsrf/bf-2", "BaseFault");

  /**
   * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.oasis_open.docs.wsrf.bf_2
   */
  public ObjectFactory() {
  }

  /**
   * Create an instance of {@link BaseFaultType }
   */
  public BaseFaultType createBaseFaultType() {
    return new BaseFaultType();
  }

  /**
   * Create an instance of {@link BaseFaultType.ErrorCode }
   */
  public BaseFaultType.ErrorCode createBaseFaultTypeErrorCode() {
    return new BaseFaultType.ErrorCode();
  }

  /**
   * Create an instance of {@link BaseFaultType.Description }
   */
  public BaseFaultType.Description createBaseFaultTypeDescription() {
    return new BaseFaultType.Description();
  }

  /**
   * Create an instance of {@link BaseFaultType.FaultCause }
   */
  public BaseFaultType.FaultCause createBaseFaultTypeFaultCause() {
    return new BaseFaultType.FaultCause();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link BaseFaultType }{@code >}
   */
  @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/bf-2", name = "BaseFault")
  public JAXBElement<BaseFaultType> createBaseFault(BaseFaultType value) {
    return new JAXBElement<BaseFaultType>(_BaseFault_QNAME, BaseFaultType.class, null, value);
  }

}
