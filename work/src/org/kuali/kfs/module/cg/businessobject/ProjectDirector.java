/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.cg.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ProjectDirector extends BusinessObjectBase {

    private static final long serialVersionUID = -8864103362445919041L;
    private String universalIdentifier;
    private UniversalUser universalUser;

    /**
     * Default no-arg constructor.
     */
    public ProjectDirector() {
        universalUser = new UniversalUser();
    }

    /**
     * Gets the personUniversal attribute.
     * 
     * @return - Returns the personUniversal
     *  
     */
    public UniversalUser getUniversalUser() {
        return universalUser;
    }

    /**
     * Sets the personUniversal attribute.
     * 
     * @param - personUniversal The personUniversal to set.
     * @deprecated
     */
    public void setUniversalUser(UniversalUser user) {
        this.universalUser = user;
    }

    /**
     * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("universaliUser.getUniversalIdentifier", this.getUniversalIdentifier());
        return m;
    }

    /**
     * Gets the universalIdentifier attribute.
     * 
     * @return Returns the universalIdentifier.
     */
    public String getUniversalIdentifier() {
        return universalIdentifier;
    }

    /**
     * Sets the universalIdentifier attribute value.
     * 
     * @param universalIdentifier The universalIdentifier to set.
     */
    public void setUniversalIdentifier(String universalIdentifier) {
        this.universalIdentifier = universalIdentifier;
    }
}
