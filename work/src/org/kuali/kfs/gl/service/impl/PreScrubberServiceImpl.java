/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.gl.service.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.service.PreScrubberService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.TransactionalServiceUtils;

/**
 * This class assumes that an account number may only belong to one chart code (i.e. as if the account number were the only primary key column of the account table)
 * Based on that assumption, this code will attempt to fill in the chart code for an origin entry if it is blank and the account number is valid
 */
public class PreScrubberServiceImpl implements PreScrubberService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreScrubberServiceImpl.class);
    
    private int maxCacheSize = 10000;
    
    public void preprocessOriginEntry(Iterator<String> inputOriginEntries, String outputFileName) throws IOException {
        PrintStream outputStream = new PrintStream(outputFileName);
        
        Map<String, String> chartCodeCache = new LinkedHashMap<String, String>() {
            @Override
            protected boolean removeEldestEntry(Entry<String, String> eldest) {
                return size() > getMaxCacheSize();
            }
        };
        
        Set<String> nonExistentAccountCache = new SizeLimitedSet();
        Set<String> multipleAccountCache = new SizeLimitedSet();
        
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        Map<String, String> criteria = new HashMap<String, String>();
        
        int inputLines = 0;
        int outputLines = 0;
        
        try {
            while (inputOriginEntries.hasNext()) {
                inputLines++;
                
                String originEntry = inputOriginEntries.next();
                String outputLine = originEntry;
                if (originEntry.length() >= getExclusiveAccountNumberEndPosition()) {
                    String chartOfAccountsCode = originEntry.substring(getInclusiveChartOfAccountsCodeStartPosition(), getExclusiveChartOfAccountsCodeEndPosition());
                    if (GeneralLedgerConstants.getSpaceChartOfAccountsCode().equals(chartOfAccountsCode)) {
                        // blank chart code... try to find the chart code
                        String accountNumber = originEntry.substring(getInclusiveAccountNumberStartPosition(), getExclusiveAccountNumberEndPosition());
                        if (StringUtils.isNotEmpty(accountNumber)) {
                            String replacementChartOfAccountsCode = null;
                            boolean nonExistent = false;
                            boolean multipleFound = false;
                            
                            if (chartCodeCache.containsKey(accountNumber))
                                replacementChartOfAccountsCode = chartCodeCache.get(accountNumber);
                            else if (nonExistentAccountCache.contains(accountNumber))
                                nonExistent = true;
                            else if (multipleAccountCache.contains(accountNumber))
                                multipleFound = true;
                            else {
                                // overwrite the criteria so we can reuse the map and save memory
                                criteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
                                Collection<Account> results = businessObjectService.findMatching(Account.class, criteria);
                                
                                if (results.isEmpty()) {
                                    nonExistent = true;
                                    nonExistentAccountCache.add(accountNumber);
                                    LOG.warn("Could not find account record for account number " + accountNumber);
                                }
                                else {
                                    Iterator<Account> accounts = results.iterator();
                                    Account account = accounts.next();
                                    if (accounts.hasNext()) {
                                        LOG.warn("Multiple chart codes found for account number " + accountNumber + ", using chart code " + account.getChartOfAccountsCode());
                                        TransactionalServiceUtils.exhaustIterator(accounts);
                                        multipleAccountCache.add(accountNumber);
                                        multipleFound = true;
                                    }
                                    else {
                                        replacementChartOfAccountsCode = account.getChartOfAccountsCode();
                                        chartCodeCache.put(accountNumber, replacementChartOfAccountsCode);
                                    }
                                }
                            }
                            
                            
                            if (!nonExistent && !multipleFound) {
                                StringBuilder buf = new StringBuilder(originEntry.length());
                                buf.append(originEntry.substring(0, getInclusiveChartOfAccountsCodeStartPosition()));
                                buf.append(replacementChartOfAccountsCode);
                                buf.append(originEntry.subSequence(getExclusiveChartOfAccountsCodeEndPosition(), originEntry.length()));
                                outputLine = buf.toString();
                            }
                        }
                    }
                }
                outputStream.printf("%s\n", outputLine);
                outputLines++;
            }
        }
        finally {
            outputStream.close();
        }
    }
    
    /**
     * Returns the position of the chart of accounts code on an origin entry line
     * @return
     */
    protected int getInclusiveChartOfAccountsCodeStartPosition() {
        return 4;
    }
    
    /**
     * Returns the position of the end of the chart of accounts code on an origin entry line,   
     * @return
     */
    protected int getExclusiveChartOfAccountsCodeEndPosition() {
        return 6;
    }
    
    /**
     * Returns the position of the chart of accounts code on an origin entry line
     * @return
     */
    protected int getInclusiveAccountNumberStartPosition() {
        return 6;
    }
    
    /**
     * Returns the position of the end of the chart of accounts code on an origin entry line,   
     * @return
     */
    protected int getExclusiveAccountNumberEndPosition() {
        return 13;
    }
    
    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }
    
    protected class SizeLimitedSet extends LinkedHashSet<String> {
        public boolean add(String s) {
            boolean newElementAdded = super.add(s);
            if (newElementAdded && getMaxCacheSize() > 1 && size() > getMaxCacheSize()) {
                remove(iterator().next());
            }
            return newElementAdded;
        }
    }
}
