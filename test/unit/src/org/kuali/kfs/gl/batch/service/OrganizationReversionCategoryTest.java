/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.service.impl.orgreversion;

import java.util.Map;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.OrganizationReversionService;
import org.kuali.module.gl.service.OrganizationReversionCategoryLogic;
import org.kuali.test.KualiTestBaseWithSpringOnly;
import org.springframework.beans.factory.BeanFactory;

public class OrganizationReversionCategoryTest extends KualiTestBaseWithSpringOnly {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionCategoryTest.class);

    private BeanFactory beanFactory;
    private OrganizationReversionService organizationReversionService;
    private Map<String,OrganizationReversionCategoryLogic> categories;
    private ObjectCodeService objectCodeService;

    public void setUp() throws Exception {
        super.setUp();

        beanFactory = SpringServiceLocator.getBeanFactory();
        organizationReversionService = (OrganizationReversionService)beanFactory.getBean("organizationReversionService");
        categories = organizationReversionService.getCategories();
        objectCodeService = SpringServiceLocator.getObjectCodeService();
    }

    public void testC01Reversion() {
        String category = "C01";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic)categories.get(category);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2004), "BL","5000");
        assertNotNull("S&E Object code should exist",se);

        assertFalse("S&E is not wages",cat.containsObjectCode(category,se));

        ObjectCode wages = objectCodeService.getByPrimaryId(new Integer(2004), "BL", "3854");
        assertNotNull("Wages Object code should exist",wages);

        assertTrue("Wages object is wages",cat.containsObjectCode(category,wages));
    }

    public void testC02Reversion() {
        String category = "C02";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic)categories.get(category);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2004), "BL","5000");
        assertNotNull("S&E Object code should exist",se);

        assertFalse("S&E is not salary/fringes",cat.containsObjectCode(category,se));

        ObjectCode wages = objectCodeService.getByPrimaryId(new Integer(2004), "BL", "3854");
        assertNotNull("Wages Object code should exist",wages);

        assertFalse("Wages object is not salary/fringes",cat.containsObjectCode(category,wages));

        ObjectCode sal = objectCodeService.getByPrimaryId(new Integer(2004), "BL", "2000");
        assertNotNull("Salary Object code should exist",sal);

        assertTrue("Wages object is salary/fringes",cat.containsObjectCode(category,sal));
    }

    public void testC03Reversion() {
        String category = "C03";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic)categories.get(category);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2004), "BL","5000");
        assertNotNull("S&E Object code should exist",se);

        assertFalse("S&E is not financial aid",cat.containsObjectCode(category,se));

        ObjectCode fr = objectCodeService.getByPrimaryId(new Integer(2004), "BL", "5885");
        assertNotNull("Fee Remission Object code should exist",fr);

        assertTrue("Fee Remission object is financial aid",cat.containsObjectCode(category,fr));
    }

    public void testC04Reversion() {
        String category = "C04";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic)categories.get(category);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2004), "BL","5000");
        assertNotNull("S&E Object code should exist",se);

        assertFalse("S&E is not capital equipment",cat.containsObjectCode(category,se));

        ObjectCode ce = objectCodeService.getByPrimaryId(new Integer(2004), "BL", "7677");
        assertNotNull("Capital Equip Object code should exist",ce);

        assertTrue("Art object is capital equipment",cat.containsObjectCode(category,ce));
    }

    public void testC05Reversion() {
        String category = "C05";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic)categories.get(category);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2004), "BL","5000");
        assertNotNull("S&E Object code should exist",se);

        assertFalse("S&E is not reserve",cat.containsObjectCode(category,se));
        // True test
    }

    public void testC06Reversion() {
        String category = "C06";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic)categories.get(category);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2004), "BL","5000");
        assertNotNull("S&E Object code should exist",se);

        assertFalse("S&E is not transfer out",cat.containsObjectCode(category,se));
        // True test
    }

    public void testC07Reversion() {
        String category = "C07";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic)categories.get(category);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2004), "BL","5000");
        assertNotNull("S&E Object code should exist",se);

        assertFalse("S&E is not transfer in",cat.containsObjectCode(category,se));
        // True test
    }

    public void testC08Reversion() {
        String category = "C08";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic)categories.get(category);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2004), "BL","5000");
        assertNotNull("S&E Object code should exist",se);

        assertFalse("S&E is not travel",cat.containsObjectCode(category,se));
        // True test
    }

    public void testC09Reversion() {
        String category = "C09";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic)categories.get(category);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2004), "BL","5000");
        assertNotNull("S&E Object code should exist",se);

        assertTrue("S&E is S&E",cat.containsObjectCode(category,se));
        // TODO False test
    }

    public void testC10Reversion() {
        String category = "C10";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic)categories.get(category);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2004), "BL","5000");
        assertNotNull("S&E Object code should exist",se);

        assertFalse("S&E is not asses expend",cat.containsObjectCode(category,se));
        // TODO True test
    }

    public void testC11Reversion() {
        String category = "C11";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic)categories.get(category);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2004), "BL","5000");
        assertNotNull("S&E Object code should exist",se);

        assertFalse("S&E is not revenue",cat.containsObjectCode(category,se));
        // TODO True test
    }

    public void testC12Reversion() {
        String category = "C11";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic)categories.get(category);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2004), "BL","5000");
        assertNotNull("S&E Object code should exist",se);

        assertFalse("The C11 custom rule should match no object code",cat.containsObjectCode(category,se));
    }
}
