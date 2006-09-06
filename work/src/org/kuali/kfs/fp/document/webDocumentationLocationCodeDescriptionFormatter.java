/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kuali.Constants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.DisbursementVoucherDocumentationLocation;
import org.kuali.module.financial.bo.PaymentReasonCode;

/**
 * The values are the same as the disbursementVoucherDocumentationLocationCode in the
 * DisbursementVoucherDocumentationLocation class.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DocumentationLocationCodeDescriptionFormatter extends CodeDescriptionFormatterBase {
    @Override
    protected String getDescriptionOfBO(BusinessObject bo) {
        return ((DisbursementVoucherDocumentationLocation) bo).getDisbursementVoucherDocumentationLocationName();
    }

    @Override
    protected Map<String, BusinessObject> getValuesToBusinessObjectsMap(Set values) {
        Map<String, BusinessObject> map = new HashMap<String, BusinessObject>();
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(Constants.DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE_PROPERTY_NAME, values);
        Collection<DisbursementVoucherDocumentationLocation> coll = SpringServiceLocator.getBusinessObjectService().findMatchingOrderBy(DisbursementVoucherDocumentationLocation.class, criteria, "versionNumber", true);
        // by sorting on ver #, we can guarantee that the most recent value will remain in the map (assuming the iterator returns BOs in order)
        for ( DisbursementVoucherDocumentationLocation dvdl : coll ) {
            map.put( dvdl.getDisbursementVoucherDocumentationLocationCode(), dvdl );
        }
        return map;
    }
}
