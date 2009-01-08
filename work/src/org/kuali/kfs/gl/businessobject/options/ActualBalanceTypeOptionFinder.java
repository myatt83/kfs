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
package org.kuali.kfs.gl.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.service.BalanceTypService;
import org.kuali.kfs.sys.businessobject.Options;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * A value finder that returns all balance type, but selects the actual balance type
 */
public class ActualBalanceTypeOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Returns the value to select: here the value of the actual balance type
     * 
     * @return the balance type code for actual balances
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        OptionsService os = SpringContext.getBean(OptionsService.class);
        Options o = os.getCurrentYearOptions();

        return o.getActualFinancialBalanceTypeCd();
    }

    /**
     * Returns a list of the key value pairs of all balance type codes and their names
     * 
     * @return a List of all balance types to populate a dropdown control
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List labels = new ArrayList();

        BalanceTypService bts = SpringContext.getBean(BalanceTypService.class);
        Collection c = bts.getAllBalanceTyps();

        for (Iterator iter = c.iterator(); iter.hasNext();) {
            BalanceType bt = (BalanceType) iter.next();
            labels.add(new KeyLabelPair(bt.getCode(), bt.getCode() + " - " + bt.getName()));
        }

        return labels;
    }

}
