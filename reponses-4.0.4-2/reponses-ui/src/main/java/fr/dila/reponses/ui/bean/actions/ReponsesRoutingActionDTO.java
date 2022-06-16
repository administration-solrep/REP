package fr.dila.reponses.ui.bean.actions;

import fr.dila.ss.ui.bean.actions.RoutingActionDTO;

public class ReponsesRoutingActionDTO extends RoutingActionDTO {
    private boolean isStepTransmissionAssemblees;

    private boolean isStepInMinistere;

    private boolean isNextStepReorientationOrReattributionOrArbitrage;

    private boolean isStepSignature;

    private boolean isFirstStepInBranchOrParallel;

    private boolean isStepPourArbitrage;

    private boolean isStepPourReattribution;

    private boolean isStepPourReorientation;

    private boolean isStepPourInformation;

    private boolean isRootStep;

    public ReponsesRoutingActionDTO() {
        super();
    }

    public boolean getIsStepTransmissionAssemblees() {
        return isStepTransmissionAssemblees;
    }

    public void setIsStepTransmissionAssemblees(boolean isStepTransmissionAssemblees) {
        this.isStepTransmissionAssemblees = isStepTransmissionAssemblees;
    }

    public boolean getIsStepInMinistere() {
        return isStepInMinistere;
    }

    public void setIsStepInMinistere(boolean isStepInMinistere) {
        this.isStepInMinistere = isStepInMinistere;
    }

    public boolean getIsNextStepReorientationOrReattributionOrArbitrage() {
        return isNextStepReorientationOrReattributionOrArbitrage;
    }

    public void setIsNextStepReorientationOrReattributionOrArbitrage(
        boolean isNextStepReorientationOrReattributionOrArbitrage
    ) {
        this.isNextStepReorientationOrReattributionOrArbitrage = isNextStepReorientationOrReattributionOrArbitrage;
    }

    public boolean getIsStepSignature() {
        return isStepSignature;
    }

    public void setIsStepSignature(boolean isStepSignature) {
        this.isStepSignature = isStepSignature;
    }

    public boolean getIsFirstStepInBranchOrParallel() {
        return isFirstStepInBranchOrParallel;
    }

    public void setIsFirstStepInBranchOrParallel(boolean isFirstStepInBranchOrParallel) {
        this.isFirstStepInBranchOrParallel = isFirstStepInBranchOrParallel;
    }

    public boolean getIsStepPourArbitrage() {
        return isStepPourArbitrage;
    }

    public void setIsStepPourArbitrage(boolean isStepPourArbitrage) {
        this.isStepPourArbitrage = isStepPourArbitrage;
    }

    public boolean getIsStepPourReattribution() {
        return isStepPourReattribution;
    }

    public void setIsStepPourReattribution(boolean isStepPourReattribution) {
        this.isStepPourReattribution = isStepPourReattribution;
    }

    public boolean getIsStepPourReorientation() {
        return isStepPourReorientation;
    }

    public void setIsStepPourReorientation(boolean isStepPourReorientation) {
        this.isStepPourReorientation = isStepPourReorientation;
    }

    public boolean getIsStepPourInformation() {
        return isStepPourInformation;
    }

    public void setIsStepPourInformation(boolean isStepPourInformation) {
        this.isStepPourInformation = isStepPourInformation;
    }

    public boolean getIsRootStep() {
        return isRootStep;
    }

    public void setIsRootStep(boolean isRootStep) {
        this.isRootStep = isRootStep;
    }
}
