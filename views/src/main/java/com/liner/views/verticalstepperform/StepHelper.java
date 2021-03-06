package com.liner.views.verticalstepperform;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.liner.views.ExpandLayout;
import com.liner.views.R;

/**
 * This class holds a step instance and deals with updating its views so they reflect its state.
 * It can also handle the logic of the optional confirmation step.
 * <p>
 * All this logic could certainly be in the base class of the step, but by keeping it here we make
 * that class cleaner, making it easier to understand for anyone taking a look at it.
 */
class StepHelper implements Step.InternalFormStepListener {

    private Step step;
    private VerticalStepperFormView.FormStyle formStyle;

    private View stepNumberCircleView;
    private TextView titleView;
    private TextView subtitleView;
    private TextView stepNumberTextView;
    private ImageView doneIconView;
    private TextView errorMessageView;
    private ImageView errorIconView;
    private View headerView;
    private MaterialButton nextButtonView;
    private MaterialButton cancelButtonView;
    private View lineView1;
    private View lineView2;
    private View stepAndButtonView;
    private View errorMessageContainerView;
    private View titleAndSubtitleContainerView;


    private ExpandLayout stepContainer;

    StepHelper(Step.InternalFormStepListener formListener, @NonNull Step step) {
        this(formListener, step, false);
    }

    StepHelper(Step.InternalFormStepListener formListener, Step step, boolean isConfirmationStep) {
        this.step = !isConfirmationStep ? step : new ConfirmationStep();
        this.step.addListenerInternal(this);
        this.step.addListenerInternal(formListener);
    }

    View initialize(VerticalStepperFormView form, ViewGroup parent, @LayoutRes int stepLayoutResourceId, int position, boolean isLast) {
        if (step.getEntireStepLayout() == null) {
            formStyle = form.style;

            Context context = form.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View stepLayout = inflater.inflate(stepLayoutResourceId, parent, false);

            step.initializeStepInternal(stepLayout, form, position);
            step.setContentLayoutInternal(step.createStepContentLayout());

            setupStepViews(form, stepLayout, position, isLast);
        } else {
            throw new IllegalStateException("This step has already been initialized");
        }

        return step.getEntireStepLayout();
    }

    private void setupStepViews(
            final VerticalStepperFormView form,
            View stepLayout,
            final int position,
            boolean isLast) {

        if (step.getContentLayout() != null) {
            ViewGroup contentContainerLayout = step.getEntireStepLayout().findViewById(R.id.step_content);
            contentContainerLayout.addView(step.getContentLayout());
        }

        stepNumberCircleView = stepLayout.findViewById(R.id.step_number_circle);
        stepNumberTextView = stepLayout.findViewById(R.id.step_number);
        titleView = stepLayout.findViewById(R.id.step_title);
        subtitleView = stepLayout.findViewById(R.id.step_subtitle);
        doneIconView = stepLayout.findViewById(R.id.step_done_icon);
        errorMessageView = stepLayout.findViewById(R.id.step_error_message);
        errorIconView = stepLayout.findViewById(R.id.step_error_icon);
        headerView = stepLayout.findViewById(R.id.step_header);
        nextButtonView = stepLayout.findViewById(R.id.step_button);
        cancelButtonView = stepLayout.findViewById(R.id.step_cancel_button);
        lineView1 = stepLayout.findViewById(R.id.line1);
        lineView2 = stepLayout.findViewById(R.id.line2);
        stepAndButtonView = step.getEntireStepLayout().findViewById(R.id.step_content_and_button);
        errorMessageContainerView = step.getEntireStepLayout().findViewById(R.id.step_error_container);
        titleAndSubtitleContainerView = step.getEntireStepLayout().findViewById(R.id.title_subtitle_container);
        stepContainer = step.getEntireStepLayout().findViewById(R.id.stepContainer);


        ViewGroup.LayoutParams layoutParamsCircle = stepNumberCircleView.getLayoutParams();
        stepNumberCircleView.setLayoutParams(layoutParamsCircle);

        ViewGroup.LayoutParams layoutParamsLine1 = lineView1.getLayoutParams();
        lineView1.setLayoutParams(layoutParamsLine1);

        ViewGroup.LayoutParams layoutParamsLine2 = lineView2.getLayoutParams();
        lineView2.setLayoutParams(layoutParamsLine2);

        LinearLayout.LayoutParams titleAndSubtitleContainerLayoutParams =
                (LinearLayout.LayoutParams) titleAndSubtitleContainerView.getLayoutParams();
        titleAndSubtitleContainerView.setLayoutParams(titleAndSubtitleContainerLayoutParams);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (formStyle.allowStepOpeningOnHeaderClick) {
                    form.goToStep(position, true);
                }
            }
        });
        nextButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                form.goToStep(position + 1, true);
            }
        });
        cancelButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                form.cancelForm();
            }
        });

        String title = step.getTitle();
        String subtitle = step.getSubtitle();
        String stepNextButtonText = isLast ? "Создать" : "Далее";

        stepNumberTextView.setText(String.valueOf(position + 1));
        step.updateTitle(title, false);
        step.updateSubtitle(subtitle, false);
        step.updateNextButtonText(stepNextButtonText, false);

        if (formStyle.displayCancelButtonInLastStep && isLast) {
            String cancelButtonText = "Отмена";
            cancelButtonView.setText(cancelButtonText);
            cancelButtonView.setVisibility(View.VISIBLE);
        }

        if (!formStyle.displayStepButtons && !isConfirmationStep()) {
            nextButtonView.setVisibility(View.GONE);
        }

        if (isLast) {
            lineView1.setVisibility(View.GONE);
            lineView2.setVisibility(View.GONE);
        }

        if (step.isOpen() && !step.isCompleted() && !step.getErrorMessage().isEmpty()) {
            errorMessageContainerView.setVisibility(View.VISIBLE);
        } else {
            errorMessageContainerView.setVisibility(View.GONE);
        }

        subtitleView.setText(getActualSubtitleText());
        onUpdatedStepCompletionState(position, false);
        onUpdatedStepVisibility(position, false);
    }

    public Step getStepInstance() {
        return step;
    }

    @Override
    public void onUpdatedTitle(int stepPosition, boolean useAnimations) {
        if (step.getEntireStepLayout() != null) {
            updateTitleTextViewValue();
        }
    }

    @Override
    public void onUpdatedSubtitle(int stepPosition, boolean animation) {
        subtitleView.setText(getActualSubtitleText());
    }

    @Override
    public void onUpdatedButtonText(int stepPosition, boolean useAnimations) {
        if (step.getEntireStepLayout() != null) {
            updateButtonTextValue();
        }
    }

    @Override
    public void onUpdatedErrorMessage(int stepPosition, boolean useAnimations) {
        if (step.getEntireStepLayout() != null) {
            if (updateErrorMessageTextViewValue()) {
                if (step.isOpen() && !step.isCompleted() && !step.getErrorMessage().isEmpty()) {
                    errorMessageContainerView.setVisibility(View.VISIBLE);
                } else {
                    errorMessageContainerView.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onUpdatedStepVisibility(int stepPosition, boolean useAnimations) {
        if (step.getEntireStepLayout() != null) {
            if (step.isOpen()) {
                stepContainer.expand();
                // As soon as the step opens, we update its completion state
                boolean wasCompleted = step.isCompleted();
                boolean isCompleted = step.markAsCompletedOrUncompleted(useAnimations);
                if (isCompleted == wasCompleted) {
                    updateHeader(useAnimations);
                }
            } else {
                stepContainer.collapse();
                updateHeader(useAnimations);
            }
        }
    }

    @Override
    public void onUpdatedStepCompletionState(int stepPosition, boolean useAnimations) {
        if (step.getEntireStepLayout() != null) {
            if (step.isCompleted()) {
                enableNextButton();
            } else {
                disableNextButton();
            }
            updateHeader(useAnimations);
        }
    }

    private void updateHeader(boolean useAnimations) {

        // Update alpha of header elements
        boolean enableHeader = step.isOpen() || step.isCompleted();
        float alpha = enableHeader ? 1f : 0.5f;
        float subtitleAlpha = enableHeader ? 1f : 0f;
        titleView.setAlpha(alpha);
        stepNumberCircleView.setAlpha(alpha);


        // Update step position circle indicator layout
        if (step.isOpen() || !step.isCompleted()) {
            showStepNumberAndHideDoneIcon();
        } else {
            showDoneIconAndHideStepNumber();
        }
        updateSubtitleTextViewValue();
    }

    private void showDoneIconAndHideStepNumber() {
        doneIconView.setVisibility(View.VISIBLE);
        stepNumberTextView.setVisibility(View.GONE);
    }

    private void showStepNumberAndHideDoneIcon() {
        doneIconView.setVisibility(View.GONE);
        stepNumberTextView.setVisibility(View.VISIBLE);
    }

    void enableNextButton() {
        nextButtonView.setEnabled(true);
        nextButtonView.setAlpha(1f);
    }

    void disableNextButton() {
        nextButtonView.setEnabled(false);
        nextButtonView.setAlpha(0.5f);
    }

    void enableCancelButton() {
        cancelButtonView.setEnabled(true);
        cancelButtonView.setAlpha(1f);
    }

    void disableCancelButton() {
        cancelButtonView.setEnabled(false);
        cancelButtonView.setAlpha(0.5f);
    }

    void enableAllButtons() {
        if (step.isCompleted()) {
            enableNextButton();
        }
        enableCancelButton();
    }

    void disableAllButtons() {
        disableNextButton();
        disableCancelButton();
    }

    private boolean updateTitleTextViewValue() {
        CharSequence previousValue = titleView.getText();
        String previousValueAsString = previousValue == null ? "" : previousValue.toString();

        String title = step.getTitle();
        if (!title.equals(previousValueAsString)) {
            titleView.setText(title);
            return true;
        }

        return false;
    }

    private boolean updateSubtitleTextViewValue() {
        CharSequence previousValue = subtitleView.getText();
        String previousValueAsString = previousValue == null ? "" : previousValue.toString();

        String subtitle = getActualSubtitleText();
        if (!subtitle.equals(previousValueAsString)) {
            if (!subtitle.isEmpty()) {
                subtitleView.setText(subtitle);
            }
            return true;
        }
        return false;
    }

    private boolean updateButtonTextValue() {
        CharSequence previousValue = nextButtonView.getText();
        String previousValueAsString = previousValue == null ? "" : previousValue.toString();

        String buttonText = step.getNextButtonText();
        if (!buttonText.equals(previousValueAsString)) {
            nextButtonView.setText(buttonText);
            return true;
        }

        return false;
    }

    private boolean updateErrorMessageTextViewValue() {
        CharSequence previousValue = errorMessageView.getText();
        String previousValueAsString = previousValue == null ? "" : previousValue.toString();

        String errorMessage = step.getErrorMessage();
        if (!errorMessage.equals(previousValueAsString)) {
            if (!errorMessage.isEmpty()) {
                errorMessageView.setText(errorMessage);
            }
            return true;
        }
        return false;
    }

    private String getActualSubtitleText() {
        String subtitle = formStyle.displayStepDataInSubtitleOfClosedSteps && !step.isOpen()
                ? step.getStepDataAsHumanReadableString()
                : step.getSubtitle();
        subtitle = subtitle == null ? "" : subtitle;

        return subtitle;
    }

    boolean isConfirmationStep() {
        return step instanceof ConfirmationStep;
    }

    /**
     * This step will just display a button that the user will have to click to complete the form.
     */
    private class ConfirmationStep extends Step<Object> {

        ConfirmationStep() {
            super("");
        }

        @Override
        public Object getStepData() {
            return null;
        }

        @Override
        public String getStepDataAsHumanReadableString() {
            return getSubtitle();
        }

        @Override
        public void restoreStepData(Object data) {
            // No need to do anything here
        }

        @Override
        protected IsDataValid isStepDataValid(Object stepData) {
            return null;
        }

        @Override
        protected View createStepContentLayout() {
            return null;
        }

        @Override
        protected void onStepOpened(boolean animated) {
            // No need to do anything here
        }

        @Override
        protected void onStepClosed(boolean animated) {
            if (!getFormView().isFormCompleted()) {
                markAsUncompleted("", animated);
            }
        }

        @Override
        protected void onStepMarkedAsCompleted(boolean animated) {
            // No need to do anything here
        }

        @Override
        protected void onStepMarkedAsUncompleted(boolean animated) {
            // No need to do anything here
        }
    }
}
