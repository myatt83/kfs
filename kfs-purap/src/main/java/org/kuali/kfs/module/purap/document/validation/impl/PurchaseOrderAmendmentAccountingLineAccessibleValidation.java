/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants.AccountDistributionMethodCodes;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.UpdateAccountingLineEvent;
import org.kuali.kfs.sys.service.FinancialSystemWorkflowHelperService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * A validation that checks whether the given accounting line is accessible to the given user or not
 */
public class PurchaseOrderAmendmentAccountingLineAccessibleValidation extends PurchasingAccountsPayableAccountingLineAccessibleValidation {

    protected PurapService purapService;

    /**
     * Validates that the given accounting line is accessible for editing by the current user.
     * <strong>This method expects a document as the first parameter and an accounting line as the second</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {

        if( purapService.isDocumentStoppedInRouteNode((PurchasingAccountsPayableDocument)event.getDocument(), "New Unordered Items") ){
            //DO NOTHING: do not check that user owns acct lines; at this level, they can edit all accounts on PO amendment
            return true;

        } else if (SpringContext.getBean(FinancialSystemWorkflowHelperService.class).isAdhocApprovalRequestedForPrincipal(event.getDocument().getDocumentHeader().getWorkflowDocument(), GlobalVariables.getUserSession().getPrincipalId())) {
            return true;
        }
        else {
            // KFSCNTRB-1433
            // if it's UpdateAccountingLineEvent and only amount changed due to the proportional distribution, that's ok,
            // since this is the result of item quantity or price change, and the amount change is from re-distributed, not by user.
            if (event instanceof UpdateAccountingLineEvent) {
                PurchaseOrderAmendmentDocument poa = (PurchaseOrderAmendmentDocument)event.getDocument();
                boolean isProportional = StringUtils.equals(poa.getAccountDistributionMethod(), AccountDistributionMethodCodes.PROPORTIONAL_CODE);
                boolean onlyAmountChanged = onlyAmountChanged(((UpdateAccountingLineEvent)event).getAccountingLine(), ((UpdateAccountingLineEvent)event).getUpdatedAccountingLine());
                if (isProportional && onlyAmountChanged) {
                    return true;
                }
            }

            boolean result = false;
            boolean setDummyAccountIdentifier = false;

            if (needsDummyAccountIdentifier()) {
                ((PurApAccountingLine)getAccountingLineForValidation()).setAccountIdentifier(Integer.MAX_VALUE);  // avoid conflicts with any accouting identifier on any other accounting lines in the doc because, you know, you never know...
                setDummyAccountIdentifier = true;
            }

            result = super.validate(event);

            if (setDummyAccountIdentifier) {
                ((PurApAccountingLine)getAccountingLineForValidation()).setAccountIdentifier(null);
            }

            return result;
        }
    }

    /**
     * Checks to see if the amount is the only difference between the original accounting line and the updated accounting line.
     *
     * @param accountingLine
     * @param updatedAccountingLine
     * @return true if only the object code has changed on the accounting line, false otherwise
     */
    private boolean onlyAmountChanged(AccountingLine accountingLine, AccountingLine updatedAccountingLine) {
        // no changes, return false; this should never happen though, since UpdateAccountingLineEvent means something is changed.
        if (accountingLine.isLike(updatedAccountingLine)) {
            return false;
        }

        // copy the updatedAccountLine so we can set the amount on the copy of the updated accounting line
        // to be the original value for comparison purposes
        AccountingLine updatedLine = null;
        updatedLine = new PurchaseOrderAccount();

        updatedLine.copyFrom(updatedAccountingLine);
        updatedLine.setAmount(accountingLine.getAmount());

        // if they're the same, the only change was the amount
        return (accountingLine.isLike(updatedLine));
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

}

