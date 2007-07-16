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
package org.kuali.module.purap.document;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Campus;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO;
import edu.iu.uis.eden.clientapp.vo.ReportCriteriaVO;
import edu.iu.uis.eden.exception.WorkflowException;


/**
 * Accounts Payable Document Base
 * 
 */
public abstract class AccountsPayableDocumentBase extends PurchasingAccountsPayableDocumentBase implements AccountsPayableDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsPayableDocumentBase.class);
    
    // SHARED FIELDS BETWEEN PAYMENT REQUEST AND CREDIT MEMO
    private Date accountsPayableApprovalDate;
    private String accountsPayableHoldIdentifier;
    private String accountsPayableProcessorIdentifier;
    private boolean holdIndicator;
    private Date extractedDate;
    private Integer purchaseOrderIdentifier;
    private String processingCampusCode;
    private String noteLine1Text;
    private String noteLine2Text;
    private String noteLine3Text;
    
    // NOT PERSISTED IN DB
    // BELOW USED BY ROUTING
    private String chartOfAccountsCode;
    private String organizationCode;

    // REFERENCE OBJECTS
    private Campus processingCampus;
    private PurchaseOrderDocument purchaseOrderDocument;

    public AccountsPayableDocumentBase() {
        super();
    }

    //  TODO: Remove this function:
    /**
     * Retrieve all references common to AccountsPayable
     */
    /*
    @Override
    public void refreshAllReferences() {
        super.refreshAllReferences();
        this.refreshReferenceObject("processingCampus");
        if (ObjectUtils.isNotNull(getPurchaseOrderDocument())) {
            setChartOfAccountsCode(getPurchaseOrderDocument().getChartOfAccountsCode());
            setOrganizationCode(getPurchaseOrderDocument().getOrganizationCode());
        } else {
            setChartOfAccountsCode(null);
            setOrganizationCode(null);
        }
    }
*/
  
    public boolean requiresAccountsPayableReviewRouting() {
        List boNotes = this.getDocumentHeader().getBoNotes();
        if (ObjectUtils.isNotNull(boNotes)) {
            for (Object obj : boNotes) {
                Note note = (Note) obj;
                if (ObjectUtils.isNotNull(note.getAttachment())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * @see org.kuali.core.document.DocumentBase#handleRouteLevelChange(edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO)
     */
    @Override
    public void handleRouteLevelChange(DocumentRouteLevelChangeVO levelChangeEvent) {
        LOG.debug("handleRouteLevelChange() started");
        super.handleRouteLevelChange(levelChangeEvent);
        try {
            String newNodeName = levelChangeEvent.getNewNodeName();
            if (StringUtils.isNotBlank(newNodeName)) {
                List orderedNodeNameList = getNodeDetailsOrderedNodeNameList();
                preProcessNodeChange(newNodeName, levelChangeEvent.getOldNodeName());
                ReportCriteriaVO reportCriteriaVO = new ReportCriteriaVO(Long.valueOf(getDocumentNumber()));
                int indexOfNode = orderedNodeNameList.indexOf(newNodeName);
                int indexOfNextNode = indexOfNode + 1;
                if ( (indexOfNode != -1) && (indexOfNextNode < orderedNodeNameList.size()) ) {
                    // we can find a valid next node name
                    String nextNodeName = (String)orderedNodeNameList.get(indexOfNextNode);
                    reportCriteriaVO.setTargetNodeName(nextNodeName);
                    if (SpringServiceLocator.getWorkflowInfoService().documentWillHaveAtLeastOneActionRequest(
                            reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_APPROVE_REQ,EdenConstants.ACTION_REQUEST_COMPLETE_REQ})) {
                        String statusCode = getNodeDetailsStatusByNodeNameMap().get(nextNodeName);
                        if (StringUtils.isNotBlank(statusCode)) {
                            SpringServiceLocator.getPurapService().updateStatusAndStatusHistory(this, statusCode);
                            populateDocumentForRouting();
                            saveDocumentFromPostProcessing();
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Document with id " + getDocumentNumber() + " will not stop in route node '" + nextNodeName + "'");
                        }
                    }
                }
            }
        }
        catch (WorkflowException e) {
            logAndThrowRuntimeException("Error getting node names for document with id " + getDocumentNumber(), e);
        }
    }
    
    public abstract void preProcessNodeChange(String newNodeName, String oldNodeName);
    
    public abstract List<String> getNodeDetailsOrderedNodeNameList();
    
    public abstract Map<String, String> getNodeDetailsStatusByNodeNameMap();
    
    public abstract void saveDocumentFromPostProcessing();

    // GETTERS AND SETTERS    
    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }

    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }

    public String getAccountsPayableProcessorIdentifier() { 
        return accountsPayableProcessorIdentifier;
    }

    public void setAccountsPayableProcessorIdentifier(String accountsPayableProcessorIdentifier) {
        this.accountsPayableProcessorIdentifier = accountsPayableProcessorIdentifier;
    }

    public String getAccountsPayableHoldIdentifier() { 
        return accountsPayableHoldIdentifier;
    }

    public void setAccountsPayableHoldIdentifier(String accountsPayableHoldIdentifier) {
        this.accountsPayableHoldIdentifier = accountsPayableHoldIdentifier;
    }

    public String getProcessingCampusCode() { 
        return processingCampusCode;
    }

    public void setProcessingCampusCode(String processingCampusCode) {
        this.processingCampusCode = processingCampusCode;
    }

    public Date getAccountsPayableApprovalDate() { 
        return accountsPayableApprovalDate;
    }

    public void setAccountsPayableApprovalDate(Date accountsPayableApprovalDate) {
        this.accountsPayableApprovalDate = accountsPayableApprovalDate;
    }

    public Date getExtractedDate() {
        return extractedDate;
    }

    public void setExtractedDate(Date extractedDate) {
        this.extractedDate = extractedDate;
    }

    public boolean isHoldIndicator() {
        return holdIndicator;
    }

    public void setHoldIndicator(boolean holdIndicator) {
        this.holdIndicator = holdIndicator;
    }

    public String getNoteLine1Text() {
        return noteLine1Text;
    }

    public void setNoteLine1Text(String noteLine1Text) {
        this.noteLine1Text = noteLine1Text;
    }

    public String getNoteLine2Text() {
        return noteLine2Text;
    }

    public void setNoteLine2Text(String noteLine2Text) {
        this.noteLine2Text = noteLine2Text;
    }

    public String getNoteLine3Text() {
        return noteLine3Text;
    }

    public void setNoteLine3Text(String noteLine3Text) {
        this.noteLine3Text = noteLine3Text;
    }

    public Campus getProcessingCampus() { 
        return processingCampus;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public PurchaseOrderDocument getPurchaseOrderDocument() {
        if ( (ObjectUtils.isNull(purchaseOrderDocument)) && (ObjectUtils.isNotNull(getPurchaseOrderIdentifier())) ) {
            setPurchaseOrderDocument(SpringServiceLocator.getPurchaseOrderService().getCurrentPurchaseOrder(this.getPurchaseOrderIdentifier()));
        }
        return purchaseOrderDocument;
    }
    
    public void setPurchaseOrderDocument(PurchaseOrderDocument purchaseOrderDocument) {
        this.purchaseOrderIdentifier = purchaseOrderDocument.getPurapDocumentIdentifier();
        this.purchaseOrderDocument = purchaseOrderDocument;
    }

    //Helper methods
    public String getAccountsPayableHoldPersonName(){
        String personName = null;
        try {
            UniversalUser user = SpringServiceLocator.getUniversalUserService().getUniversalUser(getAccountsPayableHoldIdentifier());
            personName = user.getPersonName();
        }
        catch (UserNotFoundException unfe) {
            personName = "";
        }
        
        return personName;
    }
}
