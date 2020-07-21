package fr.dila.reponses.webtest.tests.junit;

import junit.framework.Assert;

import org.junit.Test;

import fr.dila.reponses.webtest.model.PlanClassementItem;

public class TestPlanClassementItem {

	
	@Test
	public void testParse(){
		PlanClassementItem item = new PlanClassementItem("agroalimentaire(1)");
		Assert.assertEquals("agroalimentaire", item.getTag());
		Assert.assertEquals(1, (int) item.getCount());
	}
}
