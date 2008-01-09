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
package org.kuali.module.effort.util;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.effort.EffortConstants.SystemParameters;
import org.kuali.module.effort.batch.EffortCertificationCreateStep;
import org.kuali.module.effort.batch.EffortCertificationExtractStep;

/**
 * This class is a convenient utility that can delegate the calling client to retrieve system parameters of effor certification
 * module.
 */
public class EffortCertificationParameterFinder {
    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);

    /**
     * get the run indicator setup in system paremters
     * 
     * @return the run indicator setup in system paremters
     */
    public static boolean getRunIndicator() {
        return parameterService.getIndicatorParameter(EffortCertificationExtractStep.class, SystemParameters.RUN_IND);
    }
    
    /**
     * get the federal agency type codes setup in system parameters
     * 
     * @return the federal agency type codes setup in system parameters
     */
    public static List<String> getFederalAgencyTypeCodes() {
        return parameterService.getParameterValues(EffortCertificationExtractStep.class, SystemParameters.FEDERAL_AGENCY_TYPE_CODE);
    }

    /**
     * get the fedeal only balance indicatior
     * 
     * @return the fedeal only balance indicatior
     */
    public static boolean getFederalOnlyBalanceIndicator() {
        return parameterService.getIndicatorParameter(EffortCertificationExtractStep.class, SystemParameters.FEDERAL_ONLY_BALANCE_IND);
    }

    /**
     * get the fedeal only balance indicatior
     * 
     * @return the fedeal only balance indicatior
     */
    public static List<String> getFederalOnlyBalanceIndicatorAsString() {
        List<String> indicatorList = new ArrayList<String>();
        indicatorList.add(Boolean.toString(getFederalOnlyBalanceIndicator()));
        return indicatorList;
    }

    /**
     * get the fund group denotes C&G indicator setup in system paremters
     * 
     * @return the fund group denotes C&G indicator setup in system paremters
     */
    public static boolean getFundGroupDenotesCGIndicator() {
        return parameterService.getIndicatorParameter(Account.class, SystemParameters.FUND_GROUP_DENOTES_CG_IND);
    }

    /**
     * get the fund group denotes C&G indicator setup in system paremters
     * 
     * @return the fund group denotes C&G indicator setup in system paremters
     */
    public static List<String> getFundGroupDenotesCGIndicatorAsString() {
        List<String> indicatorList = new ArrayList<String>();
        indicatorList.add(Boolean.toString(getFundGroupDenotesCGIndicator()));
        return indicatorList;
    }

    /**
     * get the C&G denoting values setup in system paremters
     * 
     * @return the C&G denoting values setup in system paremters
     */
    public static List<String> getCGDenotingValues() {
        return parameterService.getParameterValues(Account.class, SystemParameters.CG_DENOTING_VALUE);
    }

    /**
     * get the account type codes setup in system parameters
     * 
     * @return the account type codes setup in system parameters
     */
    public static List<String> getAccountTypeCodes() {
        return parameterService.getParameterValues(EffortCertificationExtractStep.class, SystemParameters.ACCOUNT_TYPE_CODE_BALANCE_SELECT);
    }

    /**
     * get the report fiscal year setup in system paremters for extract process
     * 
     * @return the report fiscal year setup in system paremters
     */
    public static Integer getExtractReportFiscalYear() {
        return Integer.valueOf(parameterService.getParameterValue(EffortCertificationExtractStep.class, SystemParameters.RUN_FISCAL_YEAR));
    }

    /**
     * get the report number setup in system paremters for extract process
     * 
     * @return the report number setup in system paremters
     */
    public static String getExtractReportNumber() {
        return parameterService.getParameterValue(EffortCertificationExtractStep.class, SystemParameters.RUN_REPORT_NUMBER);
    }

    /**
     * get the cost share sub account type code setup in system paremters
     * 
     * @return the cost share sub account type code setup in system paremters
     */
    public static List<String> getCostShareSubAccountTypeCode() {
        return parameterService.getParameterValues(EffortCertificationExtractStep.class, SystemParameters.COST_SHARE_SUB_ACCOUNT_TYPE_CODE);
    }

    /**
     * get the expense sub account type code setup in system paremters
     * 
     * @return the expense sub account type code setup in system paremters
     */
    public static List<String> getExpenseSubAccountTypeCode() {
        return parameterService.getParameterValues(EffortCertificationExtractStep.class, SystemParameters.EXPENSE_SUB_ACCOUNT_TYPE_CODE);
    }
    
    /**
     * get the report fiscal year setup in system paremters for create process
     * 
     * @return the report fiscal year setup in system paremters
     */
    public static Integer getCreateReportFiscalYear() {
        return Integer.valueOf(parameterService.getParameterValue(EffortCertificationCreateStep.class, SystemParameters.RUN_FISCAL_YEAR));
    }

    /**
     * get the report number setup in system paremters for create process
     * 
     * @return the report number setup in system paremters
     */
    public static String getCreateReportNumber() {
        return parameterService.getParameterValue(EffortCertificationCreateStep.class, SystemParameters.RUN_REPORT_NUMBER);
    }
}
