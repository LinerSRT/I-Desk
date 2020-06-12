package com.liner.views.verticalstepperform;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;

import androidx.annotation.LayoutRes;
import androidx.appcompat.widget.AppCompatImageButton;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import java.util.Arrays;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.liner.views.R;
import com.liner.views.verticalstepperform.listener.StepperFormListener;

/**
 * Custom layout that implements a vertical stepper form.
 */
public class VerticalStepperFormView extends LinearLayout {

    FormStepListener internalListener;
    FormStyle style;

    private StepperFormListener listener;
    private KeyboardTogglingObserver keyboardTogglingObserver;
    private List<StepHelper> stepHelpers;
    private boolean initialized;

    private LinearLayout formContentView;
    private NestedScrollView stepsScrollView;
    private ProgressBar progressBar;
    private ProgressBar contentProgress;
    private AppCompatImageButton previousStepButton, nextStepButton;
    private View bottomNavigationView;

    private boolean formCompleted;
    private boolean keyboardIsOpen;

    public VerticalStepperFormView(Context context) {
        super(context);

        onConstructed(context, null, 0);
    }

    public VerticalStepperFormView(Context context, AttributeSet attrs) {
        super(context, attrs);

        onConstructed(context, attrs, 0);
    }

    public VerticalStepperFormView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        onConstructed(context, attrs, defStyleAttr);
    }

    /**
     * Gets an instance of the builder that will be used to set up and initialize the form.
     *
     * @param stepperFormListener The listener for the stepper form events.
     * @param steps An array with the steps that will be displayed in the form.
     * @return An instance of the stepper form builder. Use it to configure and initialize the form.
     */
    public Builder setup(StepperFormListener stepperFormListener, Step... steps) {
        return new Builder(this, stepperFormListener, steps);
    }

    /**
     * Gets an instance of the builder that will be used to set up and initialize the form.
     *
     * @param stepperFormListener The listener for the stepper form events.
     * @param steps A list with the steps that will be displayed in the form.
     * @return An instance of the stepper form builder. Use it to configure and initialize the form.
     */
    public Builder setup(StepperFormListener stepperFormListener, List<Step> steps) {
        Step[] stepsArray = steps.toArray(new Step[0]);
        return new Builder(this, stepperFormListener, stepsArray);
    }

    /**
     * Marks the currently open step as completed or uncompleted depending on whether the step data
     * is valid or not.
     *
     * @param useAnimations True to animate the changes in the views, false to not.
     * @return True if the step was found and marked as completed; false otherwise.
     */
    public synchronized boolean markOpenStepAsCompletedOrUncompleted(boolean useAnimations) {
        return markStepAsCompletedOrUncompleted(getOpenStepPosition(), useAnimations);
    }

    /**
     * Marks the specified step as completed or uncompleted depending on whether the step data is 
     * valid or not.
     *
     * @param stepPosition The step position.
     * @param useAnimations True to animate the changes in the views, false to not.
     * @return True if the step was found and marked as completed; false otherwise.
     */
    public boolean markStepAsCompletedOrUncompleted(int stepPosition, boolean useAnimations) {
        if (stepPosition >= 0 && stepPosition < stepHelpers.size()) {
            StepHelper stepHelper = stepHelpers.get(stepPosition);
            return stepHelper.getStepInstance().markAsCompletedOrUncompleted(useAnimations);
        }

        return false;
    }

    /**
     * Marks the currently open step as completed.
     *
     * @param useAnimations True to animate the changes in the views, false to not.
     */
    public synchronized void markOpenStepAsCompleted(boolean useAnimations) {
        markStepAsCompleted(getOpenStepPosition(), useAnimations);
    }

    /**
     * Marks the specified step as completed.
     *
     * @param stepPosition The step position.
     * @param useAnimations True to animate the changes in the views, false to not.
     */
    public void markStepAsCompleted(int stepPosition, boolean useAnimations) {
        if (stepPosition >= 0 && stepPosition < stepHelpers.size()) {
            StepHelper stepHelper = stepHelpers.get(stepPosition);
            stepHelper.getStepInstance().markAsCompleted(useAnimations);
        }
    }

    /**
     * Marks the currently open step as uncompleted.
     *
     * @param errorMessage The error message.
     * @param useAnimations True to animate the changes in the views, false to not.
     */
    public synchronized void markOpenStepAsUncompleted(boolean useAnimations, String errorMessage) {
        markStepAsUncompleted(getOpenStepPosition(), errorMessage, useAnimations);
    }

    /**
     * Marks the specified step as uncompleted.
     *
     * @param stepPosition The step position.
     * @param errorMessage The error message.
     * @param useAnimations True to animate the changes in the views, false to not.
     */
    public void markStepAsUncompleted(int stepPosition, String errorMessage, boolean useAnimations) {
        if (stepPosition >= 0 && stepPosition < stepHelpers.size()) {
            StepHelper stepHelper = stepHelpers.get(stepPosition);
            stepHelper.getStepInstance().markAsUncompleted(errorMessage, useAnimations);
        }
    }

    /**
     * Determines whether the open step is marked as completed or not.
     *
     * @return True if the open step is currently marked as completed; false otherwise.
     */
    public synchronized boolean isOpenStepCompleted() {
        return isStepCompleted(getOpenStepPosition());
    }

    /**
     * Determines whether the specified step is marked as completed or not.
     *
     * @param stepPosition The step position.
     * @return True if the step is currently marked as completed; false otherwise.
     */
    public boolean isStepCompleted(int stepPosition) {
        if (stepPosition >= 0 && stepPosition < stepHelpers.size()) {
            return stepHelpers.get(stepPosition).getStepInstance().isCompleted();
        }

        return false;
    }

    /**
     * Determines whether there is at least one step marked as completed.
     *
     * @return True if at least one step has been marked as completed; false otherwise.
     */
    public boolean isAnyStepCompleted() {
        for (int i = 0; i < stepHelpers.size(); i++) {
            if (stepHelpers.get(i).getStepInstance().isCompleted()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether all the steps previous to the specified one are currently marked as completed.
     *
     * @param stepPosition The step position.
     * @return True if all the steps previous to the specified one are marked as completed; false otherwise.
     */
    public boolean areAllPreviousStepsCompleted(int stepPosition) {
        boolean previousStepsAreCompleted = true;
        for (int i = stepPosition - 1; i >= 0; i--) {
            previousStepsAreCompleted &= stepHelpers.get(i).getStepInstance().isCompleted();
        }

        return previousStepsAreCompleted;
    }

    /**
     * Determines whether all the steps are currently marked as completed.
     *
     * @return True if all the steps are marked as completed; false otherwise.
     */
    public boolean areAllStepsCompleted() {
        return areAllPreviousStepsCompleted(stepHelpers.size());
    }

    /**
     * Determines whether the form has already been completed or cancelled.
     * Please note that this could return false even if all the steps are completed (for example,
     * if the user has filled in all the required data but hasn't submitted the form yet).
     *
     * @return True if the form has been completed or cancelled; false otherwise.
     */
    public boolean isFormCompleted() {
        return formCompleted;
    }

    /**
     * If possible, goes to the step that is positioned after the currently open one, closing the
     * current one and opening the next one.
     * Please note that, unless allowNonLinearNavigation is set to true, it will only be possible to
     * navigate to a certain step if all the previous ones are marked as completed.
     *
     * @param useAnimations Indicates whether or not the affected steps will be opened/closed using
     *                      animations.
     * @return True if the navigation to the step was performed; false otherwise.
     */
    public synchronized boolean goToNextStep(boolean useAnimations) {
        return goToStep(getOpenStepPosition() + 1, useAnimations);
    }

    /**
     * If possible, goes to the step that is positioned before the currently open one, closing the
     * current one and opening the previous one.
     * Please note that, unless allowNonLinearNavigation is set to true, it will only be possible to
     * navigate to a certain step if all the previous ones are marked as completed.
     *
     * @param useAnimations Indicates whether or not the affected steps will be opened/closed using
     *                      animations.
     * @return True if the navigation to the step was performed; false otherwise.
     */
    public synchronized boolean goToPreviousStep(boolean useAnimations) {
        return goToStep(getOpenStepPosition() - 1, useAnimations);
    }

    /**
     * If possible, goes to the specified step, closing the currently open one and opening the
     * target one.
     * Please note that, unless allowNonLinearNavigation is set to true, it will only be possible to
     * navigate to a certain step if all the previous ones are marked as completed.
     * In case the navigation is possible and the specified position to go to is the last one + 1,
     * the form will attempt to complete.
     *
     * @param stepPosition The step position to go to. If it is the next one to the actual last one,
     *                     the form will attempt to complete.
     * @param useAnimations Indicates whether or not the affected steps will be opened/closed using
     *                      animations.
     * @return True if the navigation to the step was performed; false otherwise.
     */
    public synchronized boolean goToStep(int stepPosition, boolean useAnimations) {
        if (formCompleted) {
            return false;
        }

        int openStepPosition = getOpenStepPosition();
        if (openStepPosition != stepPosition && stepPosition >= 0 && stepPosition <= stepHelpers.size()) {
            boolean previousStepsAreCompleted = areAllPreviousStepsCompleted(stepPosition);
            if ((style.allowNonLinearNavigation && stepPosition < stepHelpers.size()) || previousStepsAreCompleted) {
                openStep(stepPosition, useAnimations);

                return true;
            }
        }

        return false;
    }

    /**
     * Gets the position of the currently open step.
     *
     * @return The position of the currently open step, counting from 0. -1 if not found.
     */
    public synchronized int getOpenStepPosition() {
        for (int i = 0; i < stepHelpers.size(); i++) {
            StepHelper stepHelper = stepHelpers.get(i);
            if (stepHelper.getStepInstance().isOpen()) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Gets the currently open step.
     *
     * @return The currently open step, or null if not found.
     */
    public synchronized Step getOpenStep() {
        for (int i = 0; i < stepHelpers.size(); i++) {
            StepHelper stepHelper = stepHelpers.get(i);
            if (stepHelper.getStepInstance().isOpen()) {
                return stepHelper.getStepInstance();
            }
        }

        return null;
    }

    /**
     * Gets the content layout of the specified step (i.e., the layout which was provided at start
     * to setup the step).
     *
     * @param stepPosition The step position.
     * @return If found, the layout. If not, null.
     */
    public View getStepContentLayout(int stepPosition) {
        if (stepPosition >= 0 && stepPosition < stepHelpers.size()) {
            return stepHelpers.get(stepPosition).getStepInstance().getContentLayout();
        }

        return null;
    }

    /**
     * Shows the bottom navigation bar.
     */
    public void showBottomNavigation() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the bottom navigation bar.
     */
    public void hideBottomNavigation() {
        bottomNavigationView.setVisibility(View.GONE);
    }

    /**
     * Scrolls to the top of the specified step, but only in case its content is not visible.
     *
     * @param stepPosition The step position.
     * @param smoothScroll Determines whether the scrolling should be smooth or abrupt.
     */
    public void scrollToStepIfNecessary(final int stepPosition, final boolean smoothScroll) {
        if (stepPosition >= 0 && stepPosition < stepHelpers.size()) {
            stepsScrollView.post(new Runnable() {
                public void run() {
                    Step stepInstance = stepHelpers.get(stepPosition).getStepInstance();
                    View stepEntireLayout = stepInstance.getEntireStepLayout();
                    View stepContentLayout = stepInstance.getContentLayout();
                    Rect scrollBounds = new Rect();
                    stepsScrollView.getDrawingRect(scrollBounds);
                    if (stepContentLayout == null || scrollBounds.top > stepContentLayout.getY()) {
                        if (smoothScroll) {
                            stepsScrollView.smoothScrollTo(0, stepEntireLayout.getTop());
                        } else {
                            stepsScrollView.scrollTo(0, stepEntireLayout.getTop());
                        }
                    }
                }
            });
        }
    }

    /**
     * Scrolls to the top of the currently open step, but only in case its content is not visible.
     *
     * @param smoothScroll Determines whether the scrolling should be smooth or abrupt.
     */
    public synchronized void scrollToOpenStepIfNecessary(boolean smoothScroll) {
        scrollToStepIfNecessary(getOpenStepPosition(), smoothScroll);
    }

    /**
     * If all the steps are currently marked as completed, completes the form, disabling the step
     * navigation and the button(s) of the last step, and invoking onCompletedForm() on the listener.
     * To revert these changes (for example, because saving or sending the data has failed and you
     * want the form to go back to normal so the user can use it), call
     * cancelFormCompletionOrCancellationAttempt().
     */
    public void completeForm() {
        attemptToCompleteForm(false);
    }

    /**
     * Cancels the form, disabling the step navigation and the button(s) of the currently open step,
     * and invoking onCancelledForm() on the listener.
     * To revert these changes (for example, because the user has dismissed the cancellation and you
     * want the form to go back to normal), call cancelFormCompletionOrCancellationAttempt().
     */
    public void cancelForm() {
        attemptToCompleteForm(true);
    }

    /**
     * To be used after a failed form completion attempt or after a dismissed cancellation attempt,
     * this method re-activates the navigation to other steps and re-enables the button(s) of the
     * currently open step.
     * Useful when saving the form data fails and you want to allow the user to use the form again
     * in order to re-send the data.
     */
    public synchronized void cancelFormCompletionOrCancellationAttempt() {
        if (!formCompleted) {
            return;
        }

        int openedStepPosition = getOpenStepPosition();
        openedStepPosition = openedStepPosition == -1 ? stepHelpers.size() - 1 : openedStepPosition;
        StepHelper stepHelper = stepHelpers.get(openedStepPosition);

        if (style.closeLastStepOnCompletion) {
            Step step = stepHelper.getStepInstance();
            if (!step.isOpen()) {
                step.openInternal(true);
            }
        }

        if ((openedStepPosition + 1) < stepHelpers.size() || areAllStepsCompleted()) {
            stepHelper.enableAllButtons();
        } else {
            stepHelper.enableCancelButton();
        }

        formCompleted = false;
        updateBottomNavigationButtons();
    }

    /**
     * Refreshes the progress bar of the bottom navigation depending on the number of steps marked
     * as completed, returning the number of completed steps.
     *
     * @return The number of steps that are currently marked as completed.
     */
    public int refreshFormProgress() {
        int numberOfCompletedSteps = 0;
        for (int i = 0; i < stepHelpers.size(); i++) {
            if (stepHelpers.get(i).getStepInstance().isCompleted()) {
                ++numberOfCompletedSteps;
            }
        }
        setProgress(numberOfCompletedSteps);

        return numberOfCompletedSteps;
    }

    /**
     * Gets the total number of steps of the form.
     *
     * @return The total number of steps, including the confirmation step, if any.
     */
    public int getTotalNumberOfSteps() {
        return stepHelpers.size();
    }

    private void onConstructed(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.vertical_stepper_form_layout, this, true);

        keyboardTogglingObserver = new KeyboardTogglingObserver();

        style = new FormStyle();

        // Set the default values for all the style properties
        style.displayBottomNavigation = true;
        style.displayStepButtons = true;
        style.displayCancelButtonInLastStep = false;
        style.displayStepDataInSubtitleOfClosedSteps = true;
        style.displayDifferentBackgroundColorOnDisabledElements = false;
        style.includeConfirmationStep = true;
        style.allowNonLinearNavigation = false;
        style.allowStepOpeningOnHeaderClick = true;
        style.closeLastStepOnCompletion = false;

        // Try to get the user values for the style properties to replace the default ones
        TypedArray vars;
        if (attrs != null) {
            vars = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.VerticalStepperFormView,
                    defStyleAttr,
                    0);
            style.displayBottomNavigation = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_display_bottom_navigation,
                    style.displayBottomNavigation);
            style.displayStepButtons = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_display_step_buttons,
                    style.displayStepButtons);
            style.displayCancelButtonInLastStep = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_display_cancel_button_in_last_step,
                    style.displayCancelButtonInLastStep);
            style.displayStepDataInSubtitleOfClosedSteps = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_display_step_data_in_subtitle_of_closed_steps,
                    style.displayStepDataInSubtitleOfClosedSteps);
            style.displayDifferentBackgroundColorOnDisabledElements = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_display_different_background_color_on_disabled_elements,
                    style.displayDifferentBackgroundColorOnDisabledElements);
            style.includeConfirmationStep = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_include_confirmation_step,
                    style.includeConfirmationStep);
            style.allowNonLinearNavigation = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_allow_non_linear_navigation,
                    style.allowNonLinearNavigation);
            style.allowStepOpeningOnHeaderClick = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_allow_step_opening_on_header_click,
                    style.allowStepOpeningOnHeaderClick);
            style.closeLastStepOnCompletion = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_close_last_step_on_completion,
                    style.closeLastStepOnCompletion);

            vars.recycle();
        }

        internalListener = new FormStepListener();
    }

    void initializeForm(StepperFormListener listener, StepHelper[] stepsArray) {
        this.listener = listener;
        this.stepHelpers = Arrays.asList(stepsArray);
        progressBar.setMax(stepHelpers.size());
        if (!style.displayBottomNavigation) {
            hideBottomNavigation();
        }

        contentProgress.setVisibility(GONE);
        for (int i = 0; i < stepHelpers.size(); i++) {
            View stepLayout = initializeStepHelper(i);
            formContentView.addView(stepLayout);
        }

        goToStep(0, false);

        initialized = true;
    }

    private View initializeStepHelper(int position) {
        StepHelper stepHelper = stepHelpers.get(position);
        boolean isLast = (position + 1) == stepHelpers.size();
        int stepLayoutResourceId = getStepLayoutResourceId(position, isLast);

        return stepHelper.initialize(this, formContentView, stepLayoutResourceId, position, isLast);
    }

    @LayoutRes
    protected int getStepLayoutResourceId(int position, boolean isLast) {
        // This could be overridden to use a custom step layout
        return R.layout.step_layout;
    }

    private synchronized void openStep(int stepToOpenPosition, boolean useAnimations) {
        if (stepToOpenPosition >= 0 && stepToOpenPosition < stepHelpers.size()) {

            int stepToClosePosition = getOpenStepPosition();
            if (stepToClosePosition != -1) {
                StepHelper stepToClose = stepHelpers.get(stepToClosePosition);
                stepToClose.getStepInstance().closeInternal(useAnimations);
            }

            StepHelper stepToOpen = stepHelpers.get(stepToOpenPosition);
            stepToOpen.getStepInstance().openInternal(useAnimations);
        } else if (stepToOpenPosition == stepHelpers.size()) {
            attemptToCompleteForm(false);
        }
    }

    protected synchronized void updateBottomNavigationButtons() {
        int stepPosition = getOpenStepPosition();
        if (stepPosition >= 0 && stepPosition < stepHelpers.size()) {
            StepHelper stepHelper = stepHelpers.get(stepPosition);

            if (!formCompleted && stepPosition > 0) {
                enablePreviousButtonInBottomNavigation();
            } else {
                disablePreviousButtonInBottomNavigation();
            }

            if (!formCompleted
                    && (stepPosition + 1) < stepHelpers.size()
                    && (style.allowNonLinearNavigation || stepHelper.getStepInstance().isCompleted())) {
                enableNextButtonInBottomNavigation();
            } else {
                disableNextButtonInBottomNavigation();
            }
        }
    }

    protected void disablePreviousButtonInBottomNavigation() {
        disableBottomButtonNavigation(previousStepButton);
    }

    protected void enablePreviousButtonInBottomNavigation() {
        enableBottomButtonNavigation(previousStepButton);
    }

    protected void disableNextButtonInBottomNavigation() {
        disableBottomButtonNavigation(nextStepButton);
    }

    protected void enableNextButtonInBottomNavigation() {
        enableBottomButtonNavigation(nextStepButton);
    }

    private void enableBottomButtonNavigation(View button) {
        button.setAlpha(1f);
        button.setEnabled(true);
    }

    private void disableBottomButtonNavigation(View button) {
        button.setAlpha(0.3f);
        button.setEnabled(false);
    }

    private void setProgress(int numberOfCompletedSteps) {
        if (numberOfCompletedSteps >= 0 && numberOfCompletedSteps <= stepHelpers.size()) {
            progressBar.setProgress(numberOfCompletedSteps);
        }
    }

    private void enableOrDisableLastStepNextButton() {
        if (!areAllStepsCompleted()) {
            stepHelpers.get(stepHelpers.size() - 1).disableNextButton();
        } else {
            stepHelpers.get(stepHelpers.size() - 1).enableNextButton();
        }
    }

    private synchronized void attemptToCompleteForm(boolean isCancellation) {
        if (formCompleted) {
            return;
        }

        // If the last step is a confirmation step that happens to be marked as uncompleted,
        // here we attempt to mark it as completed so the form can be completed
        boolean markedConfirmationStepAsCompleted = false;
        String confirmationStepErrorMessage = "";
        StepHelper lastStepHelper = stepHelpers.get(stepHelpers.size() - 1);
        Step lastStep = lastStepHelper.getStepInstance();
        if (!isCancellation) {
            if (!lastStep.isCompleted() && lastStepHelper.isConfirmationStep()) {
                confirmationStepErrorMessage = lastStep.getErrorMessage();
                lastStep.markAsCompletedOrUncompleted(true);
                if (lastStep.isCompleted()) {
                    markedConfirmationStepAsCompleted = true;
                }
            }
        }

        int openStepPosition = getOpenStepPosition();
        if (openStepPosition >= 0 && openStepPosition < stepHelpers.size() && (isCancellation || areAllStepsCompleted())) {
            formCompleted = true;
            stepHelpers.get(openStepPosition).disableAllButtons();
            updateBottomNavigationButtons();

            if (listener != null) {
                if (!isCancellation) {
                    listener.onCompletedForm();
                } else {
                    listener.onCancelledForm();
                }
            }
        } else if (markedConfirmationStepAsCompleted) {
            // If the completion attempt fails, we restore the confirmation step to its previous state
            lastStep.markAsUncompleted(confirmationStepErrorMessage, true);
        }

        if (!isCancellation && style.closeLastStepOnCompletion) {
            lastStep.closeInternal(true);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        findViews();
        registerListeners();
    }

    private void findViews() {
        contentProgress = findViewById(R.id.contentLoading);
        formContentView = findViewById(R.id.content);
        stepsScrollView = findViewById(R.id.steps_scroll);
        progressBar = findViewById(R.id.progress_bar);
        previousStepButton = findViewById(R.id.down_previous);
        nextStepButton = findViewById(R.id.down_next);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void registerListeners() {
        previousStepButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPreviousStep(true);
            }
        });
        nextStepButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNextStep(true);
            }
        });

        addObserverForKeyboard();
    }

    @Override
    protected void onDetachedFromWindow() {
        removeObserverForKeyboard();

        super.onDetachedFromWindow();
    }

    private void addObserverForKeyboard() {
        keyboardIsOpen = isKeyboardOpen();
        getRootView().getViewTreeObserver().addOnGlobalLayoutListener(keyboardTogglingObserver);
    }

    private void removeObserverForKeyboard() {
        getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(keyboardTogglingObserver);
    }

    private boolean isKeyboardOpen() {
        Rect r = new Rect();
        formContentView.getWindowVisibleDisplayFrame(r);
        int screenHeight = formContentView.getRootView().getHeight();
        int keyboardHeight = screenHeight - r.bottom;

        return keyboardHeight > screenHeight * 0.2;
    }


    class FormStepListener implements Step.InternalFormStepListener {

        @Override
        public void onUpdatedTitle(int stepPosition, boolean useAnimations) {
            // No need to do anything here
        }

        @Override
        public void onUpdatedSubtitle(int stepPosition, boolean useAnimation) {
            // No need to do anything here
        }

        @Override
        public void onUpdatedButtonText(int stepPosition, boolean useAnimations) {
            // No need to do anything here
        }

        @Override
        public void onUpdatedErrorMessage(int stepPosition, boolean useAnimations) {
            // No need to do anything here
        }

        @Override
        public void onUpdatedStepCompletionState(int stepPosition, boolean useAnimations) {
            updateBottomNavigationButtons();
            refreshFormProgress();
            enableOrDisableLastStepNextButton();
        }

        @Override
        public void onUpdatedStepVisibility(int stepPosition, boolean useAnimations) {
            updateBottomNavigationButtons();
            scrollToOpenStepIfNecessary(useAnimations);
            enableOrDisableLastStepNextButton();
        }
    }

    class FormStyle {
        boolean displayBottomNavigation;
        boolean displayStepButtons;
        boolean displayCancelButtonInLastStep;
        boolean displayStepDataInSubtitleOfClosedSteps;
        boolean displayDifferentBackgroundColorOnDisabledElements;
        boolean includeConfirmationStep;
        boolean allowNonLinearNavigation;
        boolean allowStepOpeningOnHeaderClick;
        boolean closeLastStepOnCompletion;
    }

    private class KeyboardTogglingObserver implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            boolean keyboardWasOpen = keyboardIsOpen;
            keyboardIsOpen = isKeyboardOpen();
            if (initialized && keyboardIsOpen != keyboardWasOpen) {
                scrollToOpenStepIfNecessary(true);
            }
        }
    }
}