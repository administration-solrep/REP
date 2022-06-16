package fr.dila.reponses.ui.bean.actions;

import static org.junit.Assert.*;

import org.junit.Test;

public class ReponsesRoutingActionDTOTest {

    @Test
    public void testConstructor() {
        ReponsesRoutingActionDTO dto = new ReponsesRoutingActionDTO();
        assertEquals(false, dto.getHasRelatedRoute());
        assertEquals(false, dto.getIsFeuilleRouteVisible());
        assertEquals(false, dto.getIsStepTransmissionAssemblees());
        assertEquals(false, dto.getIsStepInMinistere());
        assertEquals(false, dto.getIsNextStepReorientationOrReattributionOrArbitrage());
        assertEquals(false, dto.getIsStepSignature());
        assertEquals(false, dto.getIsFirstStepInBranchOrParallel());
        assertEquals(false, dto.getIsStepPourArbitrage());
        assertEquals(false, dto.getIsStepPourReattribution());
        assertEquals(false, dto.getIsRootStep());
    }

    @Test
    public void testSetter() {
        ReponsesRoutingActionDTO dto = new ReponsesRoutingActionDTO();
        assertEquals(false, dto.getHasRelatedRoute());
        assertEquals(false, dto.getIsFeuilleRouteVisible());
        assertEquals(false, dto.getIsStepTransmissionAssemblees());
        assertEquals(false, dto.getIsStepInMinistere());
        assertEquals(false, dto.getIsNextStepReorientationOrReattributionOrArbitrage());
        assertEquals(false, dto.getIsStepSignature());
        assertEquals(false, dto.getIsFirstStepInBranchOrParallel());
        assertEquals(false, dto.getIsStepPourArbitrage());
        assertEquals(false, dto.getIsStepPourReattribution());
        assertEquals(false, dto.getIsRootStep());

        dto.setIsFeuilleRouteVisible(true);
        assertEquals(true, dto.getIsFeuilleRouteVisible());
        dto.setIsStepTransmissionAssemblees(true);
        assertEquals(true, dto.getIsStepTransmissionAssemblees());
        dto.setIsStepInMinistere(true);
        assertEquals(true, dto.getIsStepInMinistere());
        dto.setIsNextStepReorientationOrReattributionOrArbitrage(true);
        assertEquals(true, dto.getIsNextStepReorientationOrReattributionOrArbitrage());
        dto.setIsStepSignature(true);
        assertEquals(true, dto.getIsStepSignature());
        dto.setIsFirstStepInBranchOrParallel(true);
        assertEquals(true, dto.getIsFirstStepInBranchOrParallel());
        dto.setIsStepPourArbitrage(true);
        assertEquals(true, dto.getIsStepPourArbitrage());
        dto.setIsStepPourReattribution(true);
        assertEquals(true, dto.getIsStepPourReattribution());
        dto.setIsRootStep(true);
        assertEquals(true, dto.getIsRootStep());
    }
}
