/*
 * Copyright 2013 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.document.service;

import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;

/**
 * Methods which will help the reimbursbable travel & entertainment documents - the travel reimbursement, the
 * entertainment document, and the moving and relocation document - to make and cancel payments in PDP
 */
public interface ReimbursableDocumentPaymentService {
    /**
     * Cancels the payment of a reimbursable travel & entertainment document
     * @param reimbursableDoc the reimbursable travel & entertainment document to cancel payment of
     * @param cancelDate the date when the cancelation occurred
     */
    public abstract void cancelReimbursableDocument(TEMReimbursementDocument reimbursableDoc, java.sql.Date cancelDate);

    /**
     * Creates a PDP PaymentGroup to pay out a reimbursable travel & entertainment document
     * @param reimbursableDoc the reimbursable travel & entertainment document to pay out
     * @param processDate the date when the extraction is occurring
     * @return a PaymentGroup which will pay out the given document
     */
    public abstract PaymentGroup createPaymentGroupForReimbursable(TEMReimbursementDocument reimbursableDoc, java.sql.Date processDate);
}