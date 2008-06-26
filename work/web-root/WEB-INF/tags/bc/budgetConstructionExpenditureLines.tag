<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<c:if test="${!accountingLineScriptsLoaded}">
	<script type='text/javascript' src="dwr/interface/ChartService.js"></script>
	<script type='text/javascript' src="dwr/interface/AccountService.js"></script>
	<script type='text/javascript' src="dwr/interface/SubAccountService.js"></script>
	<script type='text/javascript' src="dwr/interface/ObjectCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/ObjectTypeService.js"></script>
	<script type='text/javascript' src="dwr/interface/SubObjectCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/ProjectCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/OriginationCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/DocumentTypeService.js"></script>
	<script language="JavaScript" type="text/javascript" src="scripts/kfs/objectInfo.js"></script>
	<c:set var="accountingLineScriptsLoaded" value="true" scope="request" />
</c:if>

<c:set var="pbglExpenditureAttributes" value="${DataDictionary.PendingBudgetConstructionGeneralLedger.attributes}" />
<c:set var="readOnly" value="${KualiForm.editingMode['systemViewOnly'] || !KualiForm.editingMode['fullEntry']}" />
<c:set var="salsetDisabled" value="${KualiForm.salarySettingDisabled}" />
<c:set var="benecalcDisabled" value="${KualiForm.benefitsCalculationDisabled}" />
<c:set var="salarySettingOnly" value="${KualiForm.document.salarySettingOnly}" />


<kul:tab tabTitle="Expenditure" defaultOpen="false" tabErrorKey="${BCConstants.BUDGET_CONSTRUCTION_EXPENDITURE_TAB_ERRORS}">
<div class="tab-container" align=center>

        <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
            <bc:subheadingWithDetailToggleRow
              columnCount="7"
              subheading="Expenditure"
              usePercentAdj="true"
              readOnly="${readOnly}"/>
			<tr>
				<th>
				    &nbsp;	
				</th>
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglExpenditureAttributes.financialObjectCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglExpenditureAttributes.financialSubObjectCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglExpenditureAttributes.financialBeginningBalanceLineAmount}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglExpenditureAttributes.accountLineAnnualBalanceAmount}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglExpenditureAttributes.percentChange}" />
				<th>
					Action
				</th>
			</tr>
			
            <c:if test="${!readOnly}">
              <c:set var="valuesMap" value="${KualiForm.newExpenditureLine.valuesMap}"/>
                
			<tr>
              <kul:htmlAttributeHeaderCell literalLabel="Add:" scope="row" rowspan="1">
                  <%-- these hidden fields are inside a table cell to keep the HTML valid --%>
                  <html:hidden property="newExpenditureLine.documentNumber"/>
                  <html:hidden property="newExpenditureLine.universityFiscalYear"/>
                  <html:hidden property="newExpenditureLine.chartOfAccountsCode"/>
                  <html:hidden property="newExpenditureLine.accountNumber"/>
                  <html:hidden property="newExpenditureLine.subAccountNumber"/>
                  <html:hidden property="newExpenditureLine.financialBalanceTypeCode"/>
                  <html:hidden property="newExpenditureLine.financialObjectTypeCode"/>
                  <html:hidden property="newExpenditureLine.versionNumber"/>
                  <html:hidden property="newExpenditureLine.financialBeginningBalanceLineAmount"/>
              </kul:htmlAttributeHeaderCell>

              <bc:pbglLineDataCell dataCellCssClass="infoline"
                  accountingLine="newExpenditureLine"
                  field="financialObjectCode" detailFunction="loadObjectCodeInfo"
                  detailField="financialObject.financialObjectCodeName"
                  attributes="${pbglExpenditureAttributes}" lookup="true" inquiry="true"
                  boClassSimpleName="ObjectCode"
                  readOnly="false"
                  displayHidden="false"
                  lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode"
                  lookupUnkeyedFieldConversions="financialObjectTypeCode:newExpenditureLine.financialObjectTypeCode,"
                  accountingLineValuesMap="${newExpenditureLine.valuesMap}"
                  inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}"
                  anchor="expenditurenewLineLineAnchor" />

              <bc:pbglLineDataCell dataCellCssClass="infoline"
                  accountingLine="newExpenditureLine"
                  field="financialSubObjectCode" detailFunction="loadSubObjectInfo"
                  detailFunctionExtraParam="'${KualiForm.document.universityFiscalYear}', "
                  detailField="financialSubObject.financialSubObjectCodeName"
                  attributes="${pbglExpenditureAttributes}" lookup="true" inquiry="true"
                  boClassSimpleName="SubObjCd"
                  readOnly="false"
                  displayHidden="false"
                  lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode,financialObjectCode,accountNumber"
                  accountingLineValuesMap="${newExpenditureLine.valuesMap}"
                  inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}"
                  lookupAnchor="expenditurenewLineLineAnchor" />

              <td class="infoline" nowrap><div align="right"><span>
                  &nbsp;
              </span></div></td>

              <bc:pbglLineDataCell dataCellCssClass="datacell"
                  accountingLine="newExpenditureLine"
                  cellProperty="newExpenditureLine.accountLineAnnualBalanceAmount"
                  attributes="${pbglExpenditureAttributes}"
                  field="accountLineAnnualBalanceAmount"
                  fieldAlign="right"
                  readOnly="false"
                  rowSpan="1" dataFieldCssClass="amount" />

              <td class="infoline" nowrap><div align="right"><span>
                  &nbsp;
              </span></div></td>
              <td class="infoline" nowrap><div align="center">
                  <html:image property="methodToCall.insertExpenditureLine.anchorexpenditurenewLineLineAnchor" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" title="Add an Expenditure Line" alt="Add an Expenditure Line" styleClass="tinybutton"/>
              </div></td>
			</tr>
            </c:if>
			

			<c:forEach items="${KualiForm.document.pendingBudgetConstructionGeneralLedgerExpenditureLines}" var="item" varStatus="status" >

<%--
                <c:when test="${!readOnly && empty item.laborObject || (!empty item.laborObject && item.laborObject.financialObjectFringeOrSalaryCode != 'F')}">
--%>
            <c:choose>
                <c:when test="${readOnly || (!benecalcDisabled && !empty item.laborObject && item.laborObject.financialObjectFringeOrSalaryCode == BCConstants.LABOR_OBJECT_FRINGE_CODE)}">
                    <c:set var="lineIsEditable" value="false" />
                </c:when>
                <c:otherwise>
                    <c:set var="lineIsEditable" value="true" />
                </c:otherwise>
            </c:choose>

            <tr>
              <kul:htmlAttributeHeaderCell scope="row" rowspan="1">
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].documentNumber"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].universityFiscalYear"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].chartOfAccountsCode"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].accountNumber"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].subAccountNumber"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].financialBalanceTypeCode"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].financialObjectTypeCode"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].versionNumber"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].persistedAccountLineAnnualBalanceAmount"/>
                  <bc:pbglLineDataCellDetail/>
              </kul:htmlAttributeHeaderCell>

              <bc:pbglLineDataCell dataCellCssClass="datacell"
                  accountingLine="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}]"
                  field="financialObjectCode" detailFunction="loadObjectCodeInfo"
                  detailField="financialObject.financialObjectCodeShortName"
                  attributes="${pbglExpenditureAttributes}" lookup="true" inquiry="true"
                  boClassSimpleName="ObjectCode"
                  readOnly="true"
                  displayHidden="false"
                  lookupOrInquiryKeys="chartOfAccountsCode"
                  lookupUnkeyedFieldConversions="financialObjectTypeCode:document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].objectTypeCode,"
                  accountingLineValuesMap="${item.valuesMap}"
                  inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}"
                  anchor="expenditureexistingLineLineAnchor${status.index}" />

              <c:set var="doLookupOrInquiry" value="false"/>
              <c:if test="${item.financialSubObjectCode ne '---'}"><%-- FIXME: need to get current "default" value from constants --%>
                  <c:set var="doLookupOrInquiry" value="true"/>
              </c:if>

              <bc:pbglLineDataCell dataCellCssClass="datacell"
                  accountingLine="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}]"
                  field="financialSubObjectCode" detailFunction="loadSubObjectInfo"
                  detailFunctionExtraParam="'${KualiForm.document.universityFiscalYear}', "
                  detailField="financialSubObject.financialSubObjectCdshortNm"
                  attributes="${pbglExpenditureAttributes}" lookup="${doLookupOrInquiry}" inquiry="${doLookupOrInquiry}"
                  boClassSimpleName="SubObjCd"
                  readOnly="true"
                  displayHidden="false"
                  lookupOrInquiryKeys="chartOfAccountsCode,financialObjectCode,accountNumber"
                  accountingLineValuesMap="${item.valuesMap}"
                  inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}" />

              <c:set var="fieldTrailerValue" value="" />
              <c:if test="${empty item.financialBeginningBalanceLineAmount}">
                  <c:set var="fieldTrailerValue" value="&nbsp;" />
              </c:if>
              <bc:pbglLineDataCell dataCellCssClass="datacell"
                  accountingLine="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}]"
                  cellProperty="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].financialBeginningBalanceLineAmount"
                  attributes="${pbglExpenditureAttributes}"
                  field="financialBeginningBalanceLineAmount"
                  fieldAlign="right"
                  readOnly="true"
                  fieldTrailerValue="${fieldTrailerValue}"
                  rowSpan="1" dataFieldCssClass="amount" />

              <bc:pbglLineDataCell dataCellCssClass="datacell"
                  accountingLine="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}]"
                  cellProperty="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].accountLineAnnualBalanceAmount"
                  attributes="${pbglExpenditureAttributes}"
                  field="accountLineAnnualBalanceAmount"
                  fieldAlign="right"
                  readOnly="${!lineIsEditable}"
                  rowSpan="1" dataFieldCssClass="amount" />

              <c:set var="fieldTrailerValue" value="" />
              <c:if test="${empty item.percentChange}">
                  <c:set var="fieldTrailerValue" value="&nbsp;" />
              </c:if>
			  <fmt:formatNumber value="${item.percentChange}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="2" />
              <bc:pbglLineDataCell dataCellCssClass="datacell"
                  accountingLine="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].percentChange"
                  cellProperty="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].percentChange"
                  attributes="${pbglExpenditureAttributes}"
                  field="percentChange"
                  formattedNumberValue="${formattedNumber}"
                  fieldTrailerValue="${fieldTrailerValue}"
                  fieldAlign="right"
                  readOnly="true"
                  rowSpan="1" dataFieldCssClass="amount" />

             <td class="datacell" nowrap>
                 <div align="center">
				   <c:choose>
					 <c:when test="${empty item.budgetConstructionMonthly[0]}" > 
                       <c:if test="${lineIsEditable}">
						 <html:image src="${ConfigProperties.externalizable.images.url}tinybutton-createmonth.gif" styleClass="tinybutton" property="methodToCall.performMonthlyExpenditureBudget.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" title="Create Month" alt="Create Month"/>
                       </c:if>
                       <c:if test="${!lineIsEditable}">
						 <html:img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" styleClass="tinybutton" alt="" width="80" height="15"/>
                       </c:if>
					 </c:when> 
					 <c:otherwise> 
                       <c:choose>
                         <c:when test="${lineIsEditable}">
                           <html:image src="${ConfigProperties.externalizable.images.url}tinybutton-editmonth.gif" styleClass="tinybutton" property="methodToCall.performMonthlyExpenditureBudget.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" title="Edit Month" alt="Edit Month"/>
                         </c:when> 
                         <c:otherwise> 
                           <html:image src="${ConfigProperties.externalizable.images.url}tinybutton-viewmonth.gif" styleClass="tinybutton" property="methodToCall.performMonthlyExpenditureBudget.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" title="View Month" alt="View Month"/>
                         </c:otherwise> 
                       </c:choose>
						
					 </c:otherwise> 
				   </c:choose>

                   <html:image property="methodToCall.performBalanceInquiryForExpenditureLine.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" src="${ConfigProperties.externalizable.images.url}tinybutton-balinquiry.gif" title="Balance Inquiry For Expenditure Line ${status.index}" alt="Balance Inquiry For Expenditure Line ${status.index}" styleClass="tinybutton" />
<%-- FIXME: move this to new location with toolset
                   <c:if test="${lineIsEditable && !empty item.financialBeginningBalanceLineAmount && item.financialBeginningBalanceLineAmount != 0}">
                     <br>
                     <html:image property="methodToCall.performPercentAdjustmentExpenditureLine.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" src="${ConfigProperties.externalizable.images.url}tinybutton-percentincdec.gif" title="Percent Adjustment Expenditure Line ${status.index}" alt="Percent Adjustment Expenditure Line ${status.index}" styleClass="tinybutton"/>
                   </c:if>
--%>
				   <c:choose>
                     <c:when test="${!empty item.positionObjectBenefit[0] && !benecalcDisabled && !salarySettingOnly}">
                       <html:image property="methodToCall.performShowBenefits.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" src="${ConfigProperties.externalizable.images.url}tinybutton-showbenefits.gif" title="Show Benefits For ${status.index}" alt="Show Benefits For Line ${status.index}" styleClass="tinybutton"/>
                     </c:when>
				     <c:otherwise> 
					   <html:img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" styleClass="tinybutton" alt="" width="80" height="15"/>
				     </c:otherwise> 
				   </c:choose>

				   <c:choose>
                     <c:when test="${!empty item.laborObject && item.laborObject.detailPositionRequiredIndicator && !salsetDisabled}">
                       <html:image property="methodToCall.performSalarySetting.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" src="${ConfigProperties.externalizable.images.url}tinybutton-salarysetting.gif" title="Perform Salary Setting For ${status.index}" alt="Perform Salary Setting For Line ${status.index}" styleClass="tinybutton"/>
                     </c:when>
				     <c:otherwise> 
					   <html:img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" styleClass="tinybutton" alt="" width="80" height="15"/>
				     </c:otherwise> 
				   </c:choose>

				   <c:choose>
                     <c:when test="${lineIsEditable && (empty item.financialBeginningBalanceLineAmount || item.financialBeginningBalanceLineAmount == 0)}">
                       <html:image property="methodToCall.deleteExpenditureLine.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" title="Delete Expenditure Line ${status.index}" alt="Delete Expenditure Line ${status.index}" styleClass="tinybutton"/>
                     </c:when>
				     <c:otherwise> 
					   <html:img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" styleClass="tinybutton" alt="" width="40" height="15"/>
				     </c:otherwise> 
				   </c:choose>
                 </div>
             </td>
            </tr>

			</c:forEach>

			<tr>
				<kul:htmlAttributeHeaderCell literalLabel="Expenditure Totals" colspan="3" horizontal="true" />
                <bc:columnTotalCell dataCellCssClass="datacell"
                    cellProperty="document.expenditureFinancialBeginningBalanceLineAmountTotal"
                    textStyle="${textStyle}"
                    fieldAlign="right"
                    colSpan="1" />
                <bc:columnTotalCell dataCellCssClass="datacell"
                    cellProperty="document.expenditureAccountLineAnnualBalanceAmountTotal"
                    textStyle="${textStyle}"
                    fieldAlign="right"
                    colSpan="1" />
                <bc:columnTotalCell dataCellCssClass="datacell"
                    cellProperty="document.expenditurePercentChangeTotal"
                    textStyle="${textStyle}"
                    fieldAlign="right"
                    colSpan="1" />
				<td>&nbsp;</td>
			</tr>

            <c:if test="${!readOnly}">
	    	<tr>
			    <td colspan="7" class="subhead">
					<span class="subhead-left">Global Expenditure Actions</span>
			    </td>
			</tr>

            <tr>
              <th colspan="3" nowrap>&nbsp;
		    	<a name="anchorexpenditureControlsAnchor"></a>
              </th>
              <td colspan="3" class="datacell" nowrap><center>
				<bc:requestAdjustment attributes="${pbglExpenditureAttributes}" 
					adjustmentAmountFieldName="expenditureAdjustmentAmount"
					methodToCall="adjustAllExpenditureLinesPercent"
					anchor="anchorexpenditureControlsAnchor"/>
				</center>
              </td>
              <td colspan="1" class="datacell" nowrap>
                <div align="center">
<%--
                  <html:image property="methodToCall.performPercentChange.anchorexpenditureControlsAnchor" src="${ConfigProperties.externalizable.images.url}buttonsmall_percentincdec.gif" title="Apply Percent Change" alt="Apply Percent Change" styleClass="tinybutton"/>
--%>
                  <html:image property="methodToCall.performExpMonthSpread.anchorexpenditureControlsAnchor" src="${ConfigProperties.externalizable.images.url}tinybutton-monthspread.gif" title="Monthly Spread" alt="Monthly Spread" styleClass="tinybutton" />
                  <html:image property="methodToCall.performExpMonthDelete.anchorexpenditureControlsAnchor" src="${ConfigProperties.externalizable.images.url}tinybutton-monthdelete.gif" title="Monthly Delete" alt="Monthly Delete" styleClass="tinybutton"/>
                  <c:if test="${!benecalcDisabled && !salarySettingOnly}">
                      <html:image property="methodToCall.performCalculateBenefits.anchorexpenditureControlsAnchor" src="${ConfigProperties.externalizable.images.url}tinybutton-calculatebenefits.gif" title="Calculate Benefits" alt="Calculate Benefits" styleClass="tinybutton"/>
                  </c:if>
                </div>
              </td>
	        </tr>
	        </c:if>
			
		</table>
</div>
</kul:tab>
