package org.kuali.kfs.module.ar.document;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.ReceivableCustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.businessobject.ReceivableCustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.SalesTaxCustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableTaxService;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceGLPEService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.TaxDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.TaxService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.rule.event.BlanketApproveDocumentEvent;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentTypeService;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.kns.web.format.CurrencyFormatter;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomerCreditMemoDocument extends FinancialSystemTransactionalDocumentBase implements GeneralLedgerPendingEntrySource, AmountTotaling {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerCreditMemoDocument.class);

    private String statusCode;
    private Integer postingYear;
    private String financialDocumentReferenceInvoiceNumber;

    private KualiDecimal crmTotalItemAmount = KualiDecimal.ZERO;
    private KualiDecimal crmTotalTaxAmount = KualiDecimal.ZERO;
    private KualiDecimal crmTotalAmount = KualiDecimal.ZERO;

    private Integer invOutstandingDays;

    private CustomerInvoiceDocument invoice;
    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;

    private List<CustomerCreditMemoDetail> creditMemoDetails;

    protected List<GeneralLedgerPendingEntry> generalLedgerPendingEntries;
    
    private transient TaxService taxService;
    private transient AccountsReceivableTaxService arTaxService;

    public CustomerCreditMemoDocument() {
        super();
        this.postingYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        creditMemoDetails = new TypedArrayList(CustomerCreditMemoDetail.class);
        generalLedgerPendingEntries = new ArrayList<GeneralLedgerPendingEntry>();
        taxService = SpringContext.getBean(TaxService.class);
        arTaxService = SpringContext.getBean(AccountsReceivableTaxService.class);
    }

    /**
     * Gets the creditMemoDetails attribute.
     * @return Returns the creditMemoDetails.
     */
    public List<CustomerCreditMemoDetail> getCreditMemoDetails() {
        return creditMemoDetails;
    }

    /**
     * Sets the creditMemoDetails attribute value.
     * @param creditMemoDetails The creditMemoDetails to set.
     */
    public void setCreditMemoDetails(List<CustomerCreditMemoDetail> creditMemoDetails) {
        this.creditMemoDetails = creditMemoDetails;
    }

    /**
     * Gets the financialDocumentReferenceInvoiceNumber attribute.
     * @return Returns the financialDocumentReferenceInvoiceNumber.
     */
    public String getFinancialDocumentReferenceInvoiceNumber() {
        return financialDocumentReferenceInvoiceNumber;
    }

    /**
     * Sets the financialDocumentReferenceInvoiceNumber attribute value.
     * @param financialDocumentReferenceInvoiceNumber The financialDocumentReferenceInvoiceNumber to set.
     */
    public void setFinancialDocumentReferenceInvoiceNumber(String financialDocumentReferenceInvoiceNumber) {
        if (financialDocumentReferenceInvoiceNumber != null)
            financialDocumentReferenceInvoiceNumber = financialDocumentReferenceInvoiceNumber.toUpperCase();

        this.financialDocumentReferenceInvoiceNumber = financialDocumentReferenceInvoiceNumber;
    }

    /**
     * Gets the invoice attribute.
     * @return Returns the invoice.
     */
    public CustomerInvoiceDocument getInvoice() {
        if (ObjectUtils.isNull(invoice) && StringUtils.isNotEmpty(financialDocumentReferenceInvoiceNumber)){
            refreshReferenceObject("invoice");
        }

        return invoice;
    }

    /**
     * Sets the invoice attribute value.
     * @param invoice The invoice to set.
     */
    public void setInvoice(CustomerInvoiceDocument invoice) {
        this.invoice = invoice;
    }

    /**
     * Gets the statusCode attribute.
     * @return Returns the statusCode.
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the statusCode attribute value. 
     * @param statusCode The statusCode to set.
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Gets the postingYear attribute. 
     * @return Returns the postingYear.
     */
    public Integer getPostingYear() {
        return postingYear;
    }

    /**
     * Sets the postingYear attribute value. 
     * @param postingYear The postingYear to set.
     */
    public void setPostingYear(Integer postingYear) {
        this.postingYear = postingYear;
    }

    /**
     * Initializes the values for a new document.
     */
    public void initiateDocument() {
        LOG.debug("initiateDocument() started");
        setStatusCode(ArConstants.CustomerCreditMemoStatuses.INITIATE);
    }

    /**
     * Clear out the initially populated fields.
     */
    public void clearInitFields() {
        LOG.debug("clearDocument() started");

        // Clearing document Init fields
        setFinancialDocumentReferenceInvoiceNumber(null);
    }

    /**
     * Gets the crmTotalAmount attribute.
     * @return Returns the crmTotalAmount.
     */
    public KualiDecimal getCrmTotalAmount() {
        return crmTotalAmount;
    }

    /**
     * This method returns the crmTotalAmount as a currency formatted string.
     * @return String
     */
    public String getCurrencyFormattedCrmTotalAmount() {
        return (String) new CurrencyFormatter().format(crmTotalAmount);
    }

    /**
     * Sets the crmTotalAmount attribute value.
     * @param crmTotalAmount The crmTotalAmount to set.
     */
    public void setCrmTotalAmount(KualiDecimal crmTotalAmount) {
        this.crmTotalAmount = crmTotalAmount;
    }

    /**
     * Gets the crmTotalItemAmount attribute. 
     * @return Returns the crmTotalItemAmount.
     */
    public KualiDecimal getCrmTotalItemAmount() {
        return crmTotalItemAmount;
    }

    /**
     * This method returns the crmTotalItemAmount as a currency formatted string.
     * @return String
     */
    public String getCurrencyFormattedCrmTotalItemAmount() {
        return (String) new CurrencyFormatter().format(crmTotalItemAmount);
    }

    /**
     * Sets the crmTotalItemAmount attribute value. 
     * @param crmTotalItemAmount The crmTotalItemAmount to set.
     */
    public void setCrmTotalItemAmount(KualiDecimal crmTotalItemAmount) {
        this.crmTotalItemAmount = crmTotalItemAmount;
    }

    /**
     * Gets the crmTotalTaxAmount attribute.
     * @return Returns the crmTotalTaxAmount.
     */
    public KualiDecimal getCrmTotalTaxAmount() {
        return crmTotalTaxAmount;
    }

    /**
     * This method returns the crmTotalTaxAmount as a currency formatted string. 
     * @return String
     */
    public String getCurrencyFormattedCrmTotalTaxAmount() {
        return (String) new CurrencyFormatter().format(crmTotalTaxAmount);
    }

    /**
     * Sets the crmTotalTaxAmount attribute value.
     * @param crmTotalTaxAmount The crmTotalTaxAmount to set.
     */
    public void setCrmTotalTaxAmount(KualiDecimal crmTotalTaxAmount) {
        this.crmTotalTaxAmount = crmTotalTaxAmount;
    }

    /**
     * Gets the invOutstandingDays attribute.
     * @return Returns the invOutstandingDays.
     */
    public Integer getInvOutstandingDays() {
        Timestamp invBillingDateTimestamp = new Timestamp(invoice.getBillingDate().getTime());
        Timestamp todayDateTimestamp = new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime());
        double diffInDays = DateUtils.getDifferenceInDays(invBillingDateTimestamp, todayDateTimestamp);
        invOutstandingDays = new Integer(new KualiDecimal(diffInDays).intValue());

        return invOutstandingDays;
    }

    /**
     * Sets the invOutstandingDays attribute value.
     * @param invOutstandingDays The invOutstandingDays to set.
     */
    public void setInvOutstandingDays(Integer invOutstandingDays) {
        this.invOutstandingDays = invOutstandingDays;
    }

    /**
     * This method gets the glpes
     * @return a list of glpes
     */
    public List<GeneralLedgerPendingEntry> getGeneralLedgerPendingEntries() {
        return generalLedgerPendingEntries;
    }

    /**
     * This method sets the glpes
     * @return a list of glpes
     */
    public void setGeneralLedgerPendingEntries(List<GeneralLedgerPendingEntry> generalLedgerPendingEntries) {
        this.generalLedgerPendingEntries = generalLedgerPendingEntries;
    }

    public void recalculateTotalsBasedOnChangedItemAmount(CustomerCreditMemoDetail customerCreditMemoDetail) {
        KualiDecimal duplicateCreditMemoItemTotalAmount = customerCreditMemoDetail.getDuplicateCreditMemoItemTotalAmount();
        KualiDecimal creditMemoItemTotalAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();

        // substract the 'old' item amount, tax amount, and total amount accordingly from totals
        if (ObjectUtils.isNotNull(duplicateCreditMemoItemTotalAmount))
            prepareTotalsForUpdate(duplicateCreditMemoItemTotalAmount,getArTaxService().isCustomerInvoiceDetailTaxable(getInvoice(), customerCreditMemoDetail.getCustomerInvoiceDetail()));

        recalculateTotals(creditMemoItemTotalAmount,getArTaxService().isCustomerInvoiceDetailTaxable(getInvoice(), customerCreditMemoDetail.getCustomerInvoiceDetail()));

        // update duplicate credit memo item amount with 'new' value
        customerCreditMemoDetail.setDuplicateCreditMemoItemTotalAmount(creditMemoItemTotalAmount);
    }

    public void recalculateTotals(CustomerCreditMemoDetail customerCreditMemoDetail) {
        KualiDecimal duplicateCreditMemoItemTotalAmount = customerCreditMemoDetail.getDuplicateCreditMemoItemTotalAmount();

        // substract the 'old' item amount, tax amount, and total amount accordingly from totals
        if (ObjectUtils.isNotNull(duplicateCreditMemoItemTotalAmount)) {
            prepareTotalsForUpdate(duplicateCreditMemoItemTotalAmount,getArTaxService().isCustomerInvoiceDetailTaxable(getInvoice(), customerCreditMemoDetail.getCustomerInvoiceDetail()));
            customerCreditMemoDetail.setDuplicateCreditMemoItemTotalAmount(null);
        }
    }

    private void prepareTotalsForUpdate(KualiDecimal oldItemAmount, boolean isTaxableItemFlag) {
        KualiDecimal oldItemTaxAmount = KualiDecimal.ZERO;
        if (isTaxableItemFlag)
            oldItemTaxAmount = getTaxService().getTotalSalesTaxAmount(invoice.getBillingDate(), getPostalCode(), oldItemAmount);

        crmTotalItemAmount = crmTotalItemAmount.subtract(oldItemAmount);
        crmTotalTaxAmount = crmTotalTaxAmount.subtract(oldItemTaxAmount);
        crmTotalAmount = crmTotalAmount.subtract(oldItemAmount.add(oldItemTaxAmount));
    }

    public void resetTotals() {
        crmTotalItemAmount = KualiDecimal.ZERO;
        crmTotalTaxAmount = KualiDecimal.ZERO;
        crmTotalAmount = KualiDecimal.ZERO;
    }

    public void recalculateTotals(KualiDecimal itemAmount, boolean isTaxableItemFlag) {
        crmTotalItemAmount = crmTotalItemAmount.add(itemAmount);
        if (isTaxableItemFlag)
            crmTotalTaxAmount = crmTotalTaxAmount.add(getTaxService().getTotalSalesTaxAmount(invoice.getBillingDate(), getPostalCode(), itemAmount));
        crmTotalAmount = crmTotalItemAmount.add(crmTotalTaxAmount);
    }

    /*
     * populate customer credit memo details based on the invoice info
     */
    public void populateCustomerCreditMemoDetails() {
        CustomerCreditMemoDetail customerCreditMemoDetail;
        KualiDecimal invItemTaxAmount, openInvoiceQuantity, openInvoiceAmount;

        CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
        setStatusCode(ArConstants.CustomerCreditMemoStatuses.IN_PROCESS);
        
        //set accounts receivable document header
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class).getNewAccountsReceivableDocumentHeaderForCurrentUser();
        accountsReceivableDocumentHeader.setDocumentNumber(getDocumentNumber());
        accountsReceivableDocumentHeader.setCustomerNumber(invoice.getAccountsReceivableDocumentHeader().getCustomerNumber());
        setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

        List<CustomerInvoiceDetail> customerInvoiceDetails = invoice.getCustomerInvoiceDetailsWithoutDiscounts();
        for (CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails) {
            customerCreditMemoDetail = new CustomerCreditMemoDetail();

            if(ObjectUtils.isNull(customerInvoiceDetail.getInvoiceItemTaxAmount())){
                customerInvoiceDetail.setInvoiceItemTaxAmount(KualiDecimal.ZERO);
            }
            customerCreditMemoDetail.setInvoiceLineTotalAmount(customerInvoiceDetail.getInvoiceItemTaxAmount(), customerInvoiceDetail.getInvoiceItemPreTaxAmount());
            customerCreditMemoDetail.setReferenceInvoiceItemNumber(customerInvoiceDetail.getSequenceNumber());
            openInvoiceAmount = customerInvoiceDetailService.getOpenAmount( customerInvoiceDetail);

            customerCreditMemoDetail.setInvoiceOpenItemAmount(openInvoiceAmount);
            customerCreditMemoDetail.setInvoiceOpenItemQuantity(getInvoiceOpenItemQuantity(customerCreditMemoDetail, customerInvoiceDetail));
            customerCreditMemoDetail.setDocumentNumber(this.documentNumber);
            customerCreditMemoDetail.setFinancialDocumentReferenceInvoiceNumber(this.financialDocumentReferenceInvoiceNumber);

            creditMemoDetails.add(customerCreditMemoDetail);
        }

    }
    
    /**
     * This method populates credit memo details that aren't saved in database
     */
    public void populateCustomerCreditMemoDetailsAfterLoad() {

        KualiDecimal openInvoiceAmount, invItemTaxAmount, creditMemoItemAmount, creditMemoTaxAmount, taxRate;
        CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);

        List<CustomerInvoiceDetail> customerInvoiceDetails = getInvoice().getCustomerInvoiceDetailsWithoutDiscounts();
        for (CustomerCreditMemoDetail creditMemoDetail : creditMemoDetails) {

            creditMemoDetail.setFinancialDocumentReferenceInvoiceNumber(this.financialDocumentReferenceInvoiceNumber);
            CustomerInvoiceDetail customerInvoiceDetail = creditMemoDetail.getCustomerInvoiceDetail(); 
            openInvoiceAmount = customerInvoiceDetailService.getOpenAmount( customerInvoiceDetail);
            creditMemoDetail.setInvoiceOpenItemAmount(openInvoiceAmount);

            creditMemoDetail.setInvoiceOpenItemQuantity(getInvoiceOpenItemQuantity(creditMemoDetail, customerInvoiceDetail));

            if(ObjectUtils.isNull(customerInvoiceDetail.getInvoiceItemTaxAmount())){
                customerInvoiceDetail.setInvoiceItemTaxAmount(KualiDecimal.ZERO);
            }
            creditMemoDetail.setInvoiceLineTotalAmount(customerInvoiceDetail.getInvoiceItemTaxAmount(), customerInvoiceDetail.getInvoiceItemPreTaxAmount());

            creditMemoItemAmount = creditMemoDetail.getCreditMemoItemTotalAmount();
            creditMemoDetail.setDuplicateCreditMemoItemTotalAmount(creditMemoItemAmount);
            if (ObjectUtils.isNotNull(creditMemoItemAmount)) {
                if( getArTaxService().isCustomerInvoiceDetailTaxable(invoice, customerInvoiceDetail) )
                    creditMemoTaxAmount = getTaxService().getTotalSalesTaxAmount(invoice.getBillingDate(), getPostalCode(), creditMemoItemAmount);
                else
                    creditMemoTaxAmount = KualiDecimal.ZERO;
                creditMemoDetail.setCreditMemoItemTaxAmount(creditMemoTaxAmount);
                creditMemoDetail.setCreditMemoLineTotalAmount(creditMemoItemAmount.add(creditMemoTaxAmount));

                crmTotalItemAmount = crmTotalItemAmount.add(creditMemoItemAmount);
                crmTotalTaxAmount = crmTotalTaxAmount.add(creditMemoTaxAmount);
                crmTotalAmount = crmTotalAmount.add(creditMemoItemAmount.add(creditMemoTaxAmount));
            }
        }
    }
    
    public KualiDecimal getInvoiceOpenItemQuantity(CustomerCreditMemoDetail customerCreditMemoDetail,CustomerInvoiceDetail customerInvoiceDetail) {
        KualiDecimal invoiceOpenItemQuantity;
        BigDecimal invoiceItemUnitPrice = customerInvoiceDetail.getInvoiceItemUnitPrice();
        if (ObjectUtils.isNull(invoiceItemUnitPrice) || invoiceItemUnitPrice.equals(BigDecimal.ZERO))
            invoiceOpenItemQuantity = KualiDecimal.ZERO;
        else {
            BigDecimal invoiceOpenItemAmount = customerCreditMemoDetail.getInvoiceOpenItemAmount().bigDecimalValue();
            // subtract out the tax, otherwise the quantity calculations are off 
            invoiceOpenItemAmount = invoiceOpenItemAmount.subtract(customerInvoiceDetail.getInvoiceItemTaxAmount().bigDecimalValue());
            invoiceOpenItemQuantity = new KualiDecimal(invoiceOpenItemAmount.divide(invoiceItemUnitPrice, 4));
        }
        return invoiceOpenItemQuantity;
    }
    
    /**
     * do all the calculations before the document gets saved
     * gets called for 'Submit', 'Save', and 'Blanket Approved'
     * @see org.kuali.rice.kns.document.Document#prepareForSave(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    public void prepareForSave(KualiDocumentEvent event) {
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)event.getDocument();
        CustomerCreditMemoDocumentService customerCreditMemoDocumentService = SpringContext.getBean(CustomerCreditMemoDocumentService.class);
        if (event instanceof BlanketApproveDocumentEvent)
            customerCreditMemoDocumentService.recalculateCustomerCreditMemoDocument(customerCreditMemoDocument,true);
        else
            customerCreditMemoDocumentService.recalculateCustomerCreditMemoDocument(customerCreditMemoDocument,false);
        
        // generate GLPEs
        if (!SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(this)) {
            logErrors();
            throw new ValidationException("general ledger GLPE generation failed");
        }
        super.prepareForSave(event);  
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#generateDocumentGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;
        return success;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#isDebit(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail)
     */
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return false;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#clearAnyGeneralLedgerPendingEntries()
     */
    public void clearAnyGeneralLedgerPendingEntries() {
        generalLedgerPendingEntries = new ArrayList<GeneralLedgerPendingEntry>();
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#getGeneralLedgerPostables()
     */
    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
        List<GeneralLedgerPendingEntrySourceDetail> generalLedgerPendingEntrySourceDetails = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
        if (creditMemoDetails != null) {
            Iterator iter = creditMemoDetails.iterator();
            CustomerCreditMemoDetail customerCreditMemoDetail;
            KualiDecimal amount; 
            while (iter.hasNext()) {
                customerCreditMemoDetail = (CustomerCreditMemoDetail)iter.next();
                amount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
                
                // get only non empty credit memo details to generate GLPEs
                if (ObjectUtils.isNotNull(amount) && amount.isGreaterThan(KualiDecimal.ZERO))
                    generalLedgerPendingEntrySourceDetails.add((GeneralLedgerPendingEntrySourceDetail)customerCreditMemoDetail);
            }
        }
        return generalLedgerPendingEntrySourceDetails;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#addPendingEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    public void addPendingEntry(GeneralLedgerPendingEntry entry) {
        generalLedgerPendingEntries.add(entry);
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#getGeneralLedgerPendingEntryAmountForGeneralLedgerPostable(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail)
     */
    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail postable) {
        return postable.getAmount();
    }

    /**
     * Returns the financial document type code for the given document, using the DocumentTypeService
     * @return the financial document type code for the given document
     */
    public String getFinancialDocumentTypeCode() {
        return SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(this.getClass());
    }
    
    /**
     * When document is processed do the following:
     * 
     * 1) Apply amounts to writeoff invoice
     * 2) Mark off invoice indiciator
     *
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange(){
        super.handleRouteStatusChange();
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            
            //have to populate because not all the customer credit memo details are populated while doc is in workflow
            populateCustomerCreditMemoDetailsAfterLoad();
            
            // apply writeoff amounts by only retrieving only the invoice details that ARE NOT discounts
            SpringContext.getBean(InvoicePaidAppliedService.class).saveInvoicePaidApplieds(getCreditMemoDetails(), documentNumber);
            KualiDecimal totalAppliedByCustomerCreditMemoDocument = SpringContext.getBean(InvoicePaidAppliedService.class).getTotalAmountApplied(getCreditMemoDetails());
            SpringContext.getBean(CustomerInvoiceDocumentService.class).closeCustomerInvoiceDocumentIfFullyPaidOff(getInvoice(), totalAppliedByCustomerCreditMemoDocument);
        }
    }        

    /**
     * This method creates the following GLPE's for the customer credit memo
     *
     * 1. Credit to receivable object for total line amount (excluding sales tax)
     * 2. Debit to income object for total line amount (excluding sales tax)
     * 3. Credit to receivable object in sales tax account(if sales tax exists)
     * 4. Debit to liability object in sales tax account(if sales tax exists)
     * 5. Credit to receivable object in district tax account(if district tax exists)
     * 6. Debit to liability object code in district tax account(if district tax exists)
     *
     * @see org.kuali.kfs.service.impl.GenericGeneralLedgerPendingEntryGenerationProcessImpl#processGenerateGeneralLedgerPendingEntries(org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */

    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {

        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean hasClaimOnCashOffset = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption);
        
        addReceivableGLPEs(sequenceHelper, glpeSourceDetail, hasClaimOnCashOffset);
        sequenceHelper.increment();
        addIncomeGLPEs(sequenceHelper, glpeSourceDetail, hasClaimOnCashOffset);
        
        //if sales tax is enabled generate GLPEs
        CustomerInvoiceDetail invoiceDetail = ((CustomerCreditMemoDetail) glpeSourceDetail).getCustomerInvoiceDetail();
        if( getArTaxService().isCustomerInvoiceDetailTaxable(getInvoice(), invoiceDetail) )
            addSalesTaxGLPEs( sequenceHelper, glpeSourceDetail, hasClaimOnCashOffset );

        return true;
    }
    
    /**
     * This method creates the receivable GLPEs for credit memo detail lines.
     *
     * @param poster
     * @param sequenceHelper
     * @param postable
     * @param explicitEntry
     */
    protected void addReceivableGLPEs(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, boolean hasClaimOnCashOffset) {

        CustomerCreditMemoDetail customerCreditMemoDetail = (CustomerCreditMemoDetail)glpeSourceDetail;
        CustomerInvoiceDetail customerInvoiceDetail = customerCreditMemoDetail.getCustomerInvoiceDetail();
        ReceivableCustomerInvoiceDetail receivableCustomerInvoiceDetail = new ReceivableCustomerInvoiceDetail(customerInvoiceDetail, this.getInvoice());
        boolean isDebit = false;       
        
        CustomerInvoiceGLPEService service = SpringContext.getBean(CustomerInvoiceGLPEService.class);
        service.createAndAddGenericInvoiceRelatedGLPEs(this, receivableCustomerInvoiceDetail, sequenceHelper, isDebit, hasClaimOnCashOffset, customerCreditMemoDetail.getCreditMemoItemTotalAmount());
    }
    
    /**
     * This method adds pending entry with transaction ledger entry amount set to item price * quantity
     *
     * @param poster
     * @param sequenceHelper
     * @param postable
     * @param explicitEntry
     */
    protected void addIncomeGLPEs(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, boolean hasClaimOnCashOffset) {

        CustomerCreditMemoDetail customerCreditMemoDetail = (CustomerCreditMemoDetail)glpeSourceDetail;        
        boolean isDebit = true;
        
        CustomerInvoiceGLPEService service = SpringContext.getBean(CustomerInvoiceGLPEService.class);
        service.createAndAddGenericInvoiceRelatedGLPEs(this, customerCreditMemoDetail, sequenceHelper, isDebit, hasClaimOnCashOffset, customerCreditMemoDetail.getCreditMemoItemTotalAmount());
    }
    
    /**
     * This method add pending entries for every tax detail that exists for a particular postal code
     * 
     * @param sequenceHelper
     * @param glpeSourceDetail
     * @param hasClaimOnCashOffset
     */
    protected void addSalesTaxGLPEs(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, boolean hasClaimOnCashOffset){
        
        CustomerCreditMemoDetail customerCreditMemoDetail = (CustomerCreditMemoDetail)glpeSourceDetail;        
        boolean isDebit = false;
        
        String postalCode = getPostalCode();
        Date dateOfTransaction = getInvoice().getBillingDate();
        
        List<TaxDetail> salesTaxDetails = getTaxService().getSalesTaxDetails(dateOfTransaction, postalCode, customerCreditMemoDetail.getCreditMemoItemTotalAmount());
        
        CustomerInvoiceGLPEService service = SpringContext.getBean(CustomerInvoiceGLPEService.class);
        SalesTaxCustomerCreditMemoDetail salesTaxCustomerCreditMemoDetail;
        ReceivableCustomerCreditMemoDetail receivableCustomerCreditMemoDetail;
        for( TaxDetail salesTaxDetail : salesTaxDetails ){
            
            salesTaxCustomerCreditMemoDetail = new SalesTaxCustomerCreditMemoDetail( salesTaxDetail, customerCreditMemoDetail );
            receivableCustomerCreditMemoDetail = new ReceivableCustomerCreditMemoDetail(salesTaxCustomerCreditMemoDetail, this);
            
            sequenceHelper.increment();
            service.createAndAddGenericInvoiceRelatedGLPEs(this, receivableCustomerCreditMemoDetail, sequenceHelper, isDebit, hasClaimOnCashOffset, salesTaxDetail.getTaxAmount());
            
            sequenceHelper.increment();
            service.createAndAddGenericInvoiceRelatedGLPEs(this, salesTaxCustomerCreditMemoDetail, sequenceHelper, !isDebit, hasClaimOnCashOffset, salesTaxDetail.getTaxAmount());
        }
    }  

    public KualiDecimal getTotalDollarAmount() {
        return crmTotalAmount;
    } 
    
    
    public AccountsReceivableDocumentHeader getAccountsReceivableDocumentHeader() {
        return accountsReceivableDocumentHeader;
    }

    public void setAccountsReceivableDocumentHeader(AccountsReceivableDocumentHeader accountsReceivableDocumentHeader) {
        this.accountsReceivableDocumentHeader = accountsReceivableDocumentHeader;
    }
    
    public String getPostalCode() {
        String postalCode = getArTaxService().getPostalCodeForTaxation(getInvoice());
        return postalCode;
    }

    /**
     * Gets the taxService attribute. 
     * @return Returns the taxService.
     */
    public TaxService getTaxService() {
        //  lazy init the service if its been nullified by session-izing the document
        if (taxService == null) taxService = SpringContext.getBean(TaxService.class); 
        return taxService;
    }

    /**
     * Gets the arTaxService attribute. 
     * @return Returns the arTaxService.
     */
    public AccountsReceivableTaxService getArTaxService() {
        //  lazy init the service if its been nullified by session-izing the document
        if (arTaxService == null) arTaxService = SpringContext.getBean(AccountsReceivableTaxService.class);
        return arTaxService;
    }

}
