package com.liner.views.verticalstepperform;

import com.liner.views.verticalstepperform.listener.StepperFormListener;

/**
 * The builder to set up and initialize the form.
 */
public class Builder {

    private VerticalStepperFormView formView;
    private StepperFormListener listener;
    private StepHelper[] steps;

    public Builder(VerticalStepperFormView formView, StepperFormListener listener, Step[] steps) {
        this.formView = formView;
        this.listener = listener;
        this.steps = new StepHelper[steps.length];
        for (int i = 0; i < steps.length; i++) {
            this.steps[i] = new StepHelper(formView.internalListener, steps[i]);
        }
    }


    public void init() {
        addConfirmationStepIfRequested();
        formView.initializeForm(listener, steps);
    }

    private void addConfirmationStepIfRequested() {
        if (formView.style.includeConfirmationStep) {
            StepHelper[] currentSteps = steps;
            steps = new StepHelper[steps.length + 1];
            System.arraycopy(currentSteps, 0, steps, 0, currentSteps.length);
            steps[currentSteps.length] = new StepHelper(formView.internalListener, null, true);
        }
    }
}
