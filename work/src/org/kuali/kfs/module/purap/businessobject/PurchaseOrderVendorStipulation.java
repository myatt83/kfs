/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.module.purap.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.purap.document.PurchaseOrderDocument;

/**
 * 
 */
public class PurchaseOrderVendorStipulation extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Integer purchaseOrderVendorStipulationIdentifier;
	private String vendorStipulationDescription;
	private String vendorStipulationAuthorEmployeeIdentifier;
	private Date vendorStipulationCreateDate;

    private PurchaseOrderDocument purchaseOrder;

    public PurchaseOrderVendorStipulation() {
    }

    /**
     * Gets the documentNumber attribute. 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
	 * Gets the purchaseOrderVendorStipulationIdentifier attribute.
	 * 
	 * @return Returns the purchaseOrderVendorStipulationIdentifier
	 * 
	 */
	public Integer getPurchaseOrderVendorStipulationIdentifier() { 
		return purchaseOrderVendorStipulationIdentifier;
	}

	/**
	 * Sets the purchaseOrderVendorStipulationIdentifier attribute.
	 * 
	 * @param purchaseOrderVendorStipulationIdentifier The purchaseOrderVendorStipulationIdentifier to set.
	 * 
	 */
	public void setPurchaseOrderVendorStipulationIdentifier(Integer purchaseOrderVendorStipulationIdentifier) {
		this.purchaseOrderVendorStipulationIdentifier = purchaseOrderVendorStipulationIdentifier;
	}

	/**
	 * Gets the vendorStipulationDescription attribute.
	 * 
	 * @return Returns the vendorStipulationDescription
	 * 
	 */
	public String getVendorStipulationDescription() { 
		return vendorStipulationDescription;
	}

	/**
	 * Sets the vendorStipulationDescription attribute.
	 * 
	 * @param vendorStipulationDescription The vendorStipulationDescription to set.
	 * 
	 */
	public void setVendorStipulationDescription(String vendorStipulationDescription) {
		this.vendorStipulationDescription = vendorStipulationDescription;
	}


	/**
	 * Gets the vendorStipulationAuthorEmployeeIdentifier attribute.
	 * 
	 * @return Returns the vendorStipulationAuthorEmployeeIdentifier
	 * 
	 */
	public String getVendorStipulationAuthorEmployeeIdentifier() { 
		return vendorStipulationAuthorEmployeeIdentifier;
	}

	/**
	 * Sets the vendorStipulationAuthorEmployeeIdentifier attribute.
	 * 
	 * @param vendorStipulationAuthorEmployeeIdentifier The vendorStipulationAuthorEmployeeIdentifier to set.
	 * 
	 */
	public void setVendorStipulationAuthorEmployeeIdentifier(String vendorStipulationAuthorEmployeeIdentifier) {
		this.vendorStipulationAuthorEmployeeIdentifier = vendorStipulationAuthorEmployeeIdentifier;
	}


	/**
	 * Gets the vendorStipulationCreateDate attribute.
	 * 
	 * @return Returns the vendorStipulationCreateDate
	 * 
	 */
	public Date getVendorStipulationCreateDate() { 
		return vendorStipulationCreateDate;
	}

	/**
	 * Sets the vendorStipulationCreateDate attribute.
	 * 
	 * @param vendorStipulationCreateDate The vendorStipulationCreateDate to set.
	 * 
	 */
	public void setVendorStipulationCreateDate(Date vendorStipulationCreateDate) {
		this.vendorStipulationCreateDate = vendorStipulationCreateDate;
	}


	/**
	 * Gets the purchaseOrder attribute.
	 * 
	 * @return Returns the purchaseOrder
	 * 
	 */
	public PurchaseOrderDocument getPurchaseOrder() { 
		return purchaseOrder;
	}

	/**
	 * Sets the purchaseOrder attribute.
	 * 
	 * @param purchaseOrder The purchaseOrder to set.
	 * @deprecated
	 */
	public void setPurchaseOrder(PurchaseOrderDocument purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
        if (this.purchaseOrderVendorStipulationIdentifier != null) {
            m.put("purchaseOrderVendorStipulationIdentifier", this.purchaseOrderVendorStipulationIdentifier.toString());
        }
	    return m;
    }
}
