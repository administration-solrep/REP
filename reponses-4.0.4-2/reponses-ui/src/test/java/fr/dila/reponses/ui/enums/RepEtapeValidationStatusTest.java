package fr.dila.reponses.ui.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RepEtapeValidationStatusTest {

    @Test(expected = IllegalArgumentException.class)
    public void testGetEnumFromKeyError() {
        RepEtapeValidationStatusEnum.getEnumFromKey(null, null);
    }

    @Test
    public void testGetEnumFromKey() {
        assertEquals(
            RepEtapeValidationStatusEnum.NONCONCERNE,
            RepEtapeValidationStatusEnum.getEnumFromKey(RepEtapeValidationStatusEnum.NONCONCERNE.getKey(), "1")
        );
    }
}
