/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.web.lookupable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.dao.LaborDao;
import org.kuali.module.labor.service.LaborInquiryOptionsService;
import org.kuali.module.labor.web.inquirable.July1PositionFundingInquirableImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * The July1PositionFundingLookupableHelperServiceImpl class is the front-end for all July 1 funds balance inquiry processing.
 */

@Transactional
public class July1PositionFundingLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private BalanceService balanceService;   
    private LaborDao laborDao;
    private KualiConfigurationService kualiConfigurationService;    
    private LaborInquiryOptionsService laborInquiryOptionsService;    
    
    private static org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(July1PositionFundingLookupableHelperServiceImpl.class);
    
    /**
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map fieldValues) {

        boolean unbounded = false;
        Long actualCountIfTruncated = new Long(0);
        
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // get the pending entry option. This method must be prior to the get search results
        String pendingEntryOption = getLaborInquiryOptionsService().getSelectedPendingEntryOption(fieldValues);

        // get the consolidation option
        boolean isConsolidated = getLaborInquiryOptionsService().isConsolidationSelected(fieldValues);
               
        // Parse the map and call the DAO to process the inquiry
        Collection searchResults = getLaborDao().getJuly1PositionFunding(fieldValues);
        return new CollectionIncomplete(searchResults, actualCountIfTruncated);
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new July1PositionFundingInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    public BalanceService getBalanceService() {
        return balanceService;
    }

    public void setBalanceService(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public LaborDao getLaborDao() {
        return laborDao;
    }

    public void setLaborDao(LaborDao laborDao) {
        this.laborDao = laborDao;
    }

    public LaborInquiryOptionsService getLaborInquiryOptionsService() {
        return laborInquiryOptionsService;
    }

    public void setLaborInquiryOptionsService(LaborInquiryOptionsService laborInquiryOptionsService) {
        this.laborInquiryOptionsService = laborInquiryOptionsService;
    }
}
